package io.reactivestax.service;

import io.reactivestax.database.DBUtils;
import io.reactivestax.model.JournalEntry;
import io.reactivestax.repository.SecuritiesReferenceRepository;
import io.reactivestax.repository.TradePayloadRepository;
import io.reactivestax.repository.TradeStoredProcedureRepository;
import io.reactivestax.utility.ApplicationPropertiesUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class TradeProcessor implements Runnable, ProcessTrade {
    Logger logger = Logger.getLogger(TradeProcessor.class.getName());
    LinkedBlockingDeque<String> tradeDeque;
    int count = 0;
    private Connection connection;
    ApplicationPropertiesUtils applicationPropertiesUtils;

    public TradeProcessor(LinkedBlockingDeque<String> tradeDeque, ApplicationPropertiesUtils applicationPropertiesUtils) {
        this.tradeDeque = tradeDeque;
        this.applicationPropertiesUtils = applicationPropertiesUtils;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public LinkedBlockingDeque<String> getTradeDeque() {
        return this.tradeDeque;
    }

    @Override
    public void run() {
        count++;
        try {
            this.connection = DBUtils.getInstance(this.applicationPropertiesUtils).getConnection();
            while (true) {
                String tradeId = this.tradeDeque.poll(500, TimeUnit.MILLISECONDS);
                if (tradeId == null) break;
                else processTrade(tradeId);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warning("Thread was interrupted.");
        } catch (SQLException e) {
            logger.warning("Exception in database query.");
        } catch (Exception e){
            e.printStackTrace();
    }finally {
            try {
                this.connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
                logger.warning("Exception in closing the connection with DB connection pool");
            }
        }
    }

    @Override
    public void processTrade(String tradeId) throws SQLException, InterruptedException {
        try {
            TradePayloadRepository tradePayloadRepository = new TradePayloadRepository();
            String payload = tradePayloadRepository.readRawPayload(tradeId, this.connection);
            String[] payloadArr = payload.split(",");
            String cusip = payloadArr[3];
            SecuritiesReferenceRepository securitiesReferenceRepository = new SecuritiesReferenceRepository();
            boolean validSecurity = securitiesReferenceRepository.lookupSecurities(cusip, this.connection);
            tradePayloadRepository.updateTradePayloadLookupStatus(validSecurity, tradeId, this.connection);
            if (validSecurity) {
                JournalEntry journalEntry = new JournalEntry(
                        payloadArr[0],
                        payloadArr[2],
                        cusip,
                        payloadArr[4],
                        Integer.parseInt(payloadArr[5]),
                        "not_posted",
                        LocalDateTime.parse(payloadArr[1], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                );
                TradeStoredProcedureRepository tradeStoredProcedureRepository = new TradeStoredProcedureRepository();
                tradeStoredProcedureRepository.callTradeStoredProcedure(journalEntry, connection);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            logger.info("Exception in SQL.");
            this.connection.rollback();
        } catch (Exception e){
            e.printStackTrace();
    }finally {
            this.connection.setAutoCommit(true);
        }
    }
}
