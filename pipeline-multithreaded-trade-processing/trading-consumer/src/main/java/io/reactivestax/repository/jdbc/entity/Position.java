package io.reactivestax.repository.jdbc.entity;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class Position {
    private PositionCompositeKey positionCompositeKey;
    private Long holding;
    private int version;
    private Timestamp createdTimestamp;
    private Timestamp updatedTimestamp;
}
