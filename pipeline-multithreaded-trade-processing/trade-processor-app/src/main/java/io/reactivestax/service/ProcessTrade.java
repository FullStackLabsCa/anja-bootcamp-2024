package io.reactivestax.service;

import java.sql.SQLException;

public interface ProcessTrade {
    void processTrade(String tradeId) throws SQLException, InterruptedException;
}
