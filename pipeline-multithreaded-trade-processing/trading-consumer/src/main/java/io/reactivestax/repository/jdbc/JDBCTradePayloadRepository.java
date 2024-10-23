package io.reactivestax.consumer.repository.jdbc;

import io.reactivestax.consumer.type.entity.TradePayload;
import io.reactivestax.consumer.type.enums.LookupStatus;
import io.reactivestax.consumer.type.enums.PostedStatus;
import io.reactivestax.consumer.type.enums.ValidityStatus;
import io.reactivestax.consumer.type.exception.OptimisticLockingException;
import io.reactivestax.consumer.repository.TradePayloadRepository;
import io.reactivestax.consumer.util.database.jdbc.JDBCTransactionUtil;

import java.sql.*;

public class JDBCTradePayloadRepository implements TradePayloadRepository {
    private static final String INSERT_TRADE_PAYLOAD = "Insert into trade_payloads (trade_number, validity_status, " +
            "payload, je_status, lookup_status, created_timestamp, updated_timestamp) values(?, ?, ?, ?, ?, NOW(), " +
            "NOW())";
    private static final String READ_RAW_PAYLOAD_QUERY = "Select id, trade_number, payload, validity_status, lookup_status, je_status, created_timestamp, updated_timestamp from trade_payloads where trade_number = ?";
    private static final String UPDATE_TRADE_PAYLOAD_LOOKUP_STATUS_QUERY = "Update trade_payloads set lookup_status =" +
            " ?, updated_timestamp = NOW() where id = ?";
    private static final String UPDATE_TRADE_PAYLOAD_POSTED_STATUS_QUERY = "Update trade_payloads set je_status = ?, " +
            "updated_timestamp = NOW() where id = ?";
    private static final String OPTIMISTIC_LOCKING_EXCEPTION_MESSAGE = "Optimistic locking exception.";
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
    public TradePayload readRawPayload(String tradeNumber) {
        TradePayload tradePayload = new TradePayload();
        Connection connection = JDBCTransactionUtil.getInstance().getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(READ_RAW_PAYLOAD_QUERY)) {
            preparedStatement.setString(1, tradeNumber);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                tradePayload.setId(resultSet.getLong("id"));
                tradePayload.setTradeNumber(resultSet.getString("trade_number"));
                tradePayload.setPayload(resultSet.getString("payload"));
                tradePayload.setValidityStatus(ValidityStatus.valueOf(resultSet.getString("validity_status")));
                tradePayload.setLookupStatus(LookupStatus.valueOf(resultSet.getString("lookup_status")));
                tradePayload.setJournalEntryStatus(PostedStatus.valueOf(resultSet.getString("je_status")));
                tradePayload.setCreatedTimestamp(Timestamp.valueOf(resultSet.getString("created_timestamp")));
                tradePayload.setUpdatedTimestamp(Timestamp.valueOf(resultSet.getString("updated_timestamp")));
            }
        } catch (SQLException e) {
            throw new OptimisticLockingException(OPTIMISTIC_LOCKING_EXCEPTION_MESSAGE);
        }

        return tradePayload;
    }

    @Override
    public void updateTradePayloadLookupStatus(boolean lookupStatus, Long tradeId) {
        Connection connection = JDBCTransactionUtil.getInstance().getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_TRADE_PAYLOAD_LOOKUP_STATUS_QUERY)) {
            preparedStatement.setString(1, String.valueOf(lookupStatus ? LookupStatus.PASS : LookupStatus.FAIL));
            preparedStatement.setLong(2, tradeId);
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new OptimisticLockingException(OPTIMISTIC_LOCKING_EXCEPTION_MESSAGE);
        }
    }

    @Override
    public void updateTradePayloadPostedStatus(Long tradeId) {
        Connection connection = JDBCTransactionUtil.getInstance().getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_TRADE_PAYLOAD_POSTED_STATUS_QUERY)) {
            preparedStatement.setString(1, String.valueOf(PostedStatus.POSTED));
            preparedStatement.setLong(2, tradeId);
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new OptimisticLockingException(OPTIMISTIC_LOCKING_EXCEPTION_MESSAGE);
        }
    }
}