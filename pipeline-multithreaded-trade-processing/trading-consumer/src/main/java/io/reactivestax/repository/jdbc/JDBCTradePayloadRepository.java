package io.reactivestax.repository.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;

import io.reactivestax.repository.TradePayloadRepository;
import io.reactivestax.type.dto.TradePayloadDTO;
import io.reactivestax.type.enums.LookupStatus;
import io.reactivestax.type.enums.PostedStatus;
import io.reactivestax.type.exception.OptimisticLockingException;
import io.reactivestax.type.exception.TradePayloadSaveRuntimeException;
import io.reactivestax.util.database.jdbc.JDBCTransactionUtil;

public class JDBCTradePayloadRepository implements TradePayloadRepository {
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
    public Optional<TradePayloadDTO> readRawPayload(String tradeNumber) {
        TradePayloadDTO tradePayload = new TradePayloadDTO();
        Connection connection = JDBCTransactionUtil.getInstance().getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(READ_RAW_PAYLOAD_QUERY)) {
            preparedStatement.setString(1, tradeNumber);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                tradePayload.setId(resultSet.getLong("id"));
                tradePayload.setTradeNumber(resultSet.getString("trade_number"));
                tradePayload.setPayload(resultSet.getString("payload"));
                tradePayload.setValidityStatus(resultSet.getString("validity_status"));
                tradePayload.setLookupStatus(resultSet.getString("lookup_status"));
                tradePayload.setJournalEntryStatus(resultSet.getString("je_status"));
            }
        } catch (SQLException e) {
            throw new OptimisticLockingException(OPTIMISTIC_LOCKING_EXCEPTION_MESSAGE);
        }
        return Optional.ofNullable(tradePayload);
    }

    @Override
    public void updateTradePayloadLookupStatus(boolean lookupStatus, Long tradeId) {
        Connection connection = JDBCTransactionUtil.getInstance().getConnection();
        try (PreparedStatement preparedStatement = connection
                .prepareStatement(UPDATE_TRADE_PAYLOAD_LOOKUP_STATUS_QUERY)) {
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
        try (PreparedStatement preparedStatement = connection
                .prepareStatement(UPDATE_TRADE_PAYLOAD_POSTED_STATUS_QUERY)) {
            preparedStatement.setString(1, String.valueOf(PostedStatus.POSTED));
            preparedStatement.setLong(2, tradeId);
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new OptimisticLockingException(OPTIMISTIC_LOCKING_EXCEPTION_MESSAGE);
        }
    }

    @Override
    public void saveTradePayload(TradePayloadDTO tradePayloadDTO) {
        String sql = """
                INSERT INTO trade_payload (trade_number,
                                    payload, validity_status, lookup_status,
                                    je_status, created_timestamp, updated_timestamp) VALUES (?, ?, ?, ?,?, ?, ?)
                    """;

        try (Connection connection = JDBCTransactionUtil.getInstance().getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, tradePayloadDTO.getTradeNumber());
            preparedStatement.setString(2, tradePayloadDTO.getPayload());
            preparedStatement.setString(3, tradePayloadDTO.getValidityStatus());
            preparedStatement.setString(4, tradePayloadDTO.getLookupStatus());
            preparedStatement.setString(5, tradePayloadDTO.getJournalEntryStatus());
            Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
            preparedStatement.setTimestamp(6, currentTimestamp);
            preparedStatement.setTimestamp(7, currentTimestamp);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new TradePayloadSaveRuntimeException("Error saving trade payload", e);
        }
    }
}