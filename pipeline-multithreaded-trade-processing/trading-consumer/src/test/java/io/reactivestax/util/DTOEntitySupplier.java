package io.reactivestax.util;

import io.reactivestax.repository.hibernate.entity.TradePayload;
import io.reactivestax.type.enums.LookupStatus;
import io.reactivestax.type.enums.PostedStatus;
import io.reactivestax.type.enums.ValidityStatus;

import java.util.function.Supplier;

public interface DTOEntitySupplier {

    Supplier<TradePayload> sellTradePayloadEntity = () -> TradePayload.builder()
            .tradeNumber("TDB_000002")
            .payload("TDB_000002,2024-09-19 22:16:18,TDB_CUST_5214938,TSLA,SELL,1,638.02")
            .validityStatus(ValidityStatus.VALID)
            .lookupStatus(LookupStatus.NOT_CHECKED)
            .journalEntryStatus(PostedStatus.NOT_POSTED).build();
}
