package io.reactivestax.util;

import io.reactivestax.repository.hibernate.entity.TradePayload;
import io.reactivestax.type.enums.LookupStatus;
import io.reactivestax.type.enums.PostedStatus;
import io.reactivestax.type.enums.ValidityStatus;

import java.util.function.Supplier;

public class EntitySupplier {
    public static final Supplier<TradePayload> buyTradePayloadEntity = () -> TradePayload.builder()
            .tradeNumber("TDB_000001")
            .payload("TDB_000001,2024-09-19 22:16:18,TDB_CUST_5214938,TSLA,BUY,1,638.02")
            .validityStatus(ValidityStatus.VALID)
            .lookupStatus(LookupStatus.NOT_CHECKED)
            .journalEntryStatus(PostedStatus.NOT_POSTED)
            .build();

    public static final Supplier<io.reactivestax.type.dto.JournalEntry> journalEntrySupplier =
            () -> io.reactivestax.type.dto.JournalEntry.builder()
                    .accountNumber("TDB_CUST_5214938")
                    .securityCusip("TSLA")
                    .direction("BUY")
                    .tradeId("TDB_000001")
                    .quantity(10)
                    .transactionTimestamp("2024-09-19 22:16:18")
                    .build();

    public static final Supplier<io.reactivestax.type.dto.Position> positionSupplier =
            () -> io.reactivestax.type.dto.Position.builder()
                    .accountNumber("TDB_CUST_5214938")
                    .securityCusip("TSLA")
                    .holding(10L)
                    .build();

}
