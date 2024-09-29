package io.reactivestax.repository;

import io.reactivestax.model.RawPayload;

import java.sql.Connection;
import java.sql.SQLException;

public interface ReadAndWriteTradePayload {
    void insertTradeRawPayload(RawPayload rawPayload, Connection connection) throws SQLException;

    String readRawPayload(String tradeId, Connection connection) throws SQLException;
}
