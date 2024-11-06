package io.reactivestax.service;

import io.reactivestax.type.enums.Direction;
import lombok.Getter;

public class TradeTestService {
    private static TradeTestService instance;

    @Getter
    io.reactivestax.type.dto.JournalEntry journalEntryDto1 = new io.reactivestax.type.dto.JournalEntry();
    @Getter
    io.reactivestax.type.dto.JournalEntry journalEntryDto2 = new io.reactivestax.type.dto.JournalEntry();
    @Getter
    io.reactivestax.type.dto.Position positionDto1 = new io.reactivestax.type.dto.Position();
    @Getter
    io.reactivestax.type.dto.Position positionDto2 = new io.reactivestax.type.dto.Position();

    private TradeTestService() {
        journalEntryDto1.setTradeId("TDB_000001");
        journalEntryDto1.setAccountNumber("TDB_CUST_5214938");
        journalEntryDto1.setSecurityCusip("TSLA");
        journalEntryDto1.setQuantity(1);
        journalEntryDto1.setDirection(Direction.BUY.name());
        journalEntryDto1.setTransactionTimestamp("2024-09-19 22:16:18");
        journalEntryDto2.setTradeId("TDB_000002");
        journalEntryDto2.setAccountNumber("TDB_CUST_5214938");
        journalEntryDto2.setSecurityCusip("TSLA");
        journalEntryDto2.setQuantity(1);
        journalEntryDto2.setDirection(Direction.SELL.name());
        journalEntryDto2.setTransactionTimestamp("2024-09-20 22:16:18");
        positionDto1.setAccountNumber(journalEntryDto1.getAccountNumber());
        positionDto1.setSecurityCusip(journalEntryDto1.getSecurityCusip());
        positionDto1.setHolding((long) journalEntryDto1.getQuantity());
        positionDto2.setAccountNumber(journalEntryDto2.getAccountNumber());
        positionDto2.setSecurityCusip(journalEntryDto2.getSecurityCusip());
        positionDto2.setHolding((long) journalEntryDto2.getQuantity());
    }

    public static synchronized TradeTestService getInstance() {
        if (instance == null) {
            instance = new TradeTestService();
        }

        return instance;
    }
}
