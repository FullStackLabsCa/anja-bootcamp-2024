package io.reactivestax.repository.jdbc;

import io.reactivestax.repository.TradePayloadRepository;
import io.reactivestax.type.dto.TradePayload;
import io.reactivestax.type.enums.ValidityStatus;
import io.reactivestax.util.database.jdbc.JDBCTransactionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Logger;

public class JDBCTradePayloadRepository implements TradePayloadRepository {
    Logger logger = Logger.getLogger(JDBCTradePayloadRepository.class.getName());
    private static final String INSERT_TRADE_PAYLOAD = "Insert into trade_payloads (trade_number, validity_status, " +
            "payload, je_status, lookup_status, created_timestamp, updated_timestamp) values(?, ?, ?, ?, ?, NOW(), " +
            "NOW())";
    private static JDBCTradePayloadRepository instance;

    private JDBCTradePayloadRepository() {
        // private constructor to prevent instantiation
    }

    public static synchronized JDBCTradePayloadRepository getInstance() {
        if (instance == null) {
            instance = new JDBCTradePayloadRepository();
        }
        return instance;
    }

    @Override
    public void insertTradeRawPayload(TradePayload tradePayload) {
        io.reactivestax.repository.jdbc.entity.TradePayload tradePayloadEntity = prepareTradePayloadEntity(tradePayload);
        Connection connection = JDBCTransactionUtil.getInstance().getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_TRADE_PAYLOAD)) {
            preparedStatement.setString(1, tradePayloadEntity.getTradeNumber());
            preparedStatement.setString(2, tradePayloadEntity.getValidityStatus().toString());
            preparedStatement.setString(3, tradePayloadEntity.getPayload());
            preparedStatement.setString(4, tradePayloadEntity.getJournalEntryStatus().toString());
            preparedStatement.setString(5, tradePayloadEntity.getLookupStatus().toString());
            preparedStatement.execute();
        } catch (SQLException e) {
            logger.warning("Error while inserting raw trade payload.");
        }
    }

    private io.reactivestax.repository.jdbc.entity.TradePayload prepareTradePayloadEntity(TradePayload tradePayload) {
        io.reactivestax.repository.jdbc.entity.TradePayload tradePayloadEntity =
                new io.reactivestax.repository.jdbc.entity.TradePayload();
        tradePayloadEntity.setTradeNumber(tradePayload.getTradeNumber());
        tradePayloadEntity.setPayload(tradePayload.getPayload());
        tradePayloadEntity.setValidityStatus(ValidityStatus.valueOf(tradePayload.getValidityStatus()));
        return tradePayloadEntity;
    }
}