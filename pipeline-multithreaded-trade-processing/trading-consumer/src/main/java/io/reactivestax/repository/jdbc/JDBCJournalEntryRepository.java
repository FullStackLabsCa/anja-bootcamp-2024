package io.reactivestax.repository.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;

import io.reactivestax.repository.JournalEntryRepository;
import io.reactivestax.type.dto.JournalEntryDTO;
import io.reactivestax.type.enums.PostedStatus;
import io.reactivestax.type.exception.JournalEntryCreationException;
import io.reactivestax.type.exception.OptimisticLockingException;
import io.reactivestax.util.database.jdbc.JDBCTransactionUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JDBCJournalEntryRepository implements JournalEntryRepository {
    private static final String INSERT_INTO_JOURNAL_ENTRY_QUERY = "Insert into journal_entry (trade_id, account_number, "
            +
            "security_cusip, direction, quantity, posted_status, transaction_timestamp, created_timestamp, updated_timestamp) values(?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";
    private static final String UPDATE_JOURNAL_ENTRY_STATUS_QUERY = "Update journal_entry set posted_status = ?, " +
            "updated_timestamp = NOW() where id = ?";

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
    public Optional<Long> saveJournalEntry(JournalEntryDTO journalEntry) {
        Long id = null;
        Connection connection = JDBCTransactionUtil.getInstance().getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_INTO_JOURNAL_ENTRY_QUERY,
                PreparedStatement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, journalEntry.getTradeId());
            preparedStatement.setString(2, journalEntry.getAccountNumber());
            preparedStatement.setString(3, journalEntry.getSecurityCusip());
            preparedStatement.setString(4, journalEntry.getDirection());
            preparedStatement.setInt(5, journalEntry.getQuantity());
            preparedStatement.setString(6, PostedStatus.NOT_POSTED.toString());
            preparedStatement.setTimestamp(7, Timestamp.valueOf(journalEntry.getTransactionTimestamp()));
            preparedStatement.execute();
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    id = generatedKeys.getLong(1);
                    // return Optional.of(id);
                } else {
                    log.error("Inserting record failed, no ID obtained.");
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new JournalEntryCreationException("Journal Entry Creation failed", e);
        }
        return Optional.ofNullable(id);
    }

    @Override
    public void updateJournalEntryStatus(Long journalEntryId) {
        Connection connection = JDBCTransactionUtil.getInstance().getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_JOURNAL_ENTRY_STATUS_QUERY)) {
            preparedStatement.setString(1, PostedStatus.POSTED.toString());
            preparedStatement.setLong(2, journalEntryId);
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new OptimisticLockingException("Optimistic locking", e);
        }
    }
}