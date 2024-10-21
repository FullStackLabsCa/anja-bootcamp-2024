package io.reactivestax.repository.jdbc;

import io.reactivestax.entity.TradePayload;
import io.reactivestax.enums.LookupStatusEnum;
import io.reactivestax.enums.PostedStatusEnum;
import io.reactivestax.enums.ValidityStatusEnum;
import io.reactivestax.exceptions.OptimisticLockingException;
import io.reactivestax.repository.TradePayloadRepository;
import io.reactivestax.utility.DateTimeFormatterUtil;
import io.reactivestax.utility.database.jdbc.JDBCTransactionUtil;

import java.sql.*;

public class JDBCTradePayloadRepository implements TradePayloadRepository {
    private static final String INSERT_TRADE_PAYLOAD = "Insert into trade_payloads (trade_number, validity_status, payload, je_status, lookup_status, created_at, updated_at) values(?, ?, ?, ?, ?, ?, ?)";
    private static final String READ_RAW_PAYLOAD_QUERY = "Select id, trade_number, payload, validity_status, lookup_status, je_status, created_at, updated_at from trade_payloads where trade_number = ?";
    private static final String UPDATE_TRADE_PAYLOAD_LOOKUP_STATUS_QUERY = "Update trade_payloads set lookup_status = ?, updated_at = ? where id = ?";
    private static final String UPDATE_TRADE_PAYLOAD_POSTED_STATUS_QUERY = "Update trade_payloads set je_status = ?, " +
            "updated_at = ? where id = ?";
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
    public void insertTradeRawPayload(TradePayload tradePayload) {
        Connection connection = JDBCTransactionUtil.getInstance().getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_TRADE_PAYLOAD)) {
            preparedStatement.setString(1, tradePayload.getTradeNumber());
            preparedStatement.setString(2, tradePayload.getValidityStatus().toString());
            preparedStatement.setString(3, tradePayload.getPayload());
            preparedStatement.setString(4, tradePayload.getJournalEntryStatus().toString());
            preparedStatement.setString(5, tradePayload.getLookupStatus().toString());
            preparedStatement.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
            preparedStatement.setTimestamp(7, new Timestamp(System.currentTimeMillis()));
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new OptimisticLockingException(OPTIMISTIC_LOCKING_EXCEPTION_MESSAGE);
        }
    }

    @Override
    public TradePayload readRawPayload(String tradeNumber) {
        TradePayload tradePayload = new TradePayload();
        Connection connection = JDBCTransactionUtil.getInstance().getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(READ_RAW_PAYLOAD_QUERY)) {
            preparedStatement.setString(1, tradeNumber);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                tradePayload.setId(resultSet.getInt("id"));
                tradePayload.setTradeNumber(resultSet.getString("trade_number"));
                tradePayload.setPayload(resultSet.getString("payload"));
                tradePayload.setValidityStatus(ValidityStatusEnum.valueOf(resultSet.getString("validity_status")));
                tradePayload.setLookupStatus(LookupStatusEnum.valueOf(resultSet.getString("lookup_status")));
                tradePayload.setJournalEntryStatus(PostedStatusEnum.valueOf(resultSet.getString("je_status")));
                tradePayload.setCreatedAt(Timestamp.valueOf(resultSet.getString("created_at")));
                tradePayload.setUpdatedAt(Timestamp.valueOf(resultSet.getString("updated_at")));
            }
        } catch (SQLException e) {
            throw new OptimisticLockingException(OPTIMISTIC_LOCKING_EXCEPTION_MESSAGE);
        }

        return tradePayload;
    }

    @Override
    public void updateTradePayloadLookupStatus(boolean lookupStatus, int tradeId) {
        Connection connection = JDBCTransactionUtil.getInstance().getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_TRADE_PAYLOAD_LOOKUP_STATUS_QUERY)) {
            preparedStatement.setString(1, String.valueOf(lookupStatus ? LookupStatusEnum.PASS : LookupStatusEnum.FAIL));
            preparedStatement.setTimestamp(2, DateTimeFormatterUtil.formattedTimestamp());
            preparedStatement.setInt(3, tradeId);
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new OptimisticLockingException(OPTIMISTIC_LOCKING_EXCEPTION_MESSAGE);
        }
    }

    @Override
    public void updateTradePayloadPostedStatus(int tradeId) {
        Connection connection = JDBCTransactionUtil.getInstance().getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_TRADE_PAYLOAD_POSTED_STATUS_QUERY)) {
            preparedStatement.setString(1, String.valueOf(PostedStatusEnum.POSTED));
            preparedStatement.setTimestamp(2, DateTimeFormatterUtil.formattedTimestamp());
            preparedStatement.setInt(3, tradeId);
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new OptimisticLockingException(OPTIMISTIC_LOCKING_EXCEPTION_MESSAGE);
        }
    }
}