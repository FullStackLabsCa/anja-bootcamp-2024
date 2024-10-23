package io.reactivestax.producer.repository.jdbc;

import io.reactivestax.producer.repository.TradePayloadRepository;
import io.reactivestax.producer.type.entity.TradePayload;
import io.reactivestax.producer.util.database.jdbc.JDBCTransactionUtil;

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
        Connection connection = JDBCTransactionUtil.getInstance().getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_TRADE_PAYLOAD)) {
            preparedStatement.setString(1, tradePayload.getTradeNumber());
            preparedStatement.setString(2, tradePayload.getValidityStatus().toString());
            preparedStatement.setString(3, tradePayload.getPayload());
            preparedStatement.setString(4, tradePayload.getJournalEntryStatus().toString());
            preparedStatement.setString(5, tradePayload.getLookupStatus().toString());
            preparedStatement.execute();
        } catch (SQLException e) {
            logger.warning("Error while inserting raw trade payload.");
        }
    }
}