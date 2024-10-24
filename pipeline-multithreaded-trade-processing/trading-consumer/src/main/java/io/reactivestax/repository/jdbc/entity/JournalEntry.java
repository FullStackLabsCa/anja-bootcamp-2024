package io.reactivestax.repository.jdbc.entity;

import io.reactivestax.type.enums.Direction;
import io.reactivestax.type.enums.PostedStatus;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class JournalEntry {
    private Long id;
    private String tradeId;
    private String accountNumber;
    private String securityCusip;
    private Direction direction;
    private int quantity;
    private PostedStatus postedStatus = PostedStatus.NOT_POSTED;
    private Timestamp transactionTimestamp;
    private Timestamp createdTimestamp;
    private Timestamp updatedTimestamp;
}
