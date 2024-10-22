package io.reactivestax.repository.jdbc;

import io.reactivestax.entity.JournalEntry;
import io.reactivestax.enums.PostedStatus;
import io.reactivestax.exception.OptimisticLockingException;
import io.reactivestax.repository.JournalEntryRepository;
import io.reactivestax.util.DateTimeFormatterUtil;
import io.reactivestax.util.database.jdbc.JDBCTransactionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class JDBCJournalEntryRepository implements JournalEntryRepository {
    private static final String INSERT_INTO_JOURNAL_ENTRY_QUERY = "Insert into journal_entry (trade_id, account_number, " +
            "security_cusip, direction, quantity, posted_status, transaction_timestamp, created_timestamp, updated_timestamp) values(?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_JOURNAL_ENTRY_STATUS_QUERY = "Update journal_entry set posted_status = ?, " +
            "updated_timestamp = ? where id = ?";

    private static JDBCJournalEntryRepository instance;

    private JDBCJournalEntryRepository() {
    }

    public static synchronized JDBCJournalEntryRepository getInstance() {
        if (instance == null) {
            instance = new JDBCJournalEntryRepository();
        }
        return instance;
    }

    @Override
    public void insertIntoJournalEntry(JournalEntry journalEntry) {
        Connection connection = JDBCTransactionUtil.getInstance().getConnection();
        journalEntry.setCreatedTimestamp(DateTimeFormatterUtil.formattedTimestamp());
        journalEntry.setUpdatedTimestamp(DateTimeFormatterUtil.formattedTimestamp());
        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_INTO_JOURNAL_ENTRY_QUERY)) {
            preparedStatement.setString(1, journalEntry.getTradeId());
            preparedStatement.setString(2, journalEntry.getAccountNumber());
            preparedStatement.setString(3, journalEntry.getSecurityCusip());
            preparedStatement.setString(4, journalEntry.getDirection().toString());
            preparedStatement.setInt(5, journalEntry.getQuantity());
            preparedStatement.setString(6, journalEntry.getPostedStatus().toString());
            preparedStatement.setTimestamp(7, journalEntry.getTransactionTimestamp());
            preparedStatement.setTimestamp(8, journalEntry.getCreatedTimestamp());
            preparedStatement.setTimestamp(9, journalEntry.getUpdatedTimestamp());
            preparedStatement.execute();
        } catch (Exception e) {
            throw new OptimisticLockingException("Optimistic locking", e);
        }
    }

    @Override
    public void updateJournalEntryStatus(int journalEntryId) {
        Connection connection = JDBCTransactionUtil.getInstance().getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_JOURNAL_ENTRY_STATUS_QUERY)) {
            preparedStatement.setString(1, PostedStatus.POSTED.toString());
            preparedStatement.setTimestamp(2, DateTimeFormatterUtil.formattedTimestamp());
            preparedStatement.setInt(3, journalEntryId);
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new OptimisticLockingException("Optimistic locking", e);
        }
    }
}