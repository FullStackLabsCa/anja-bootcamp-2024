package io.reactivestax.suppliers.dto;

import java.util.function.Supplier;

import io.reactivestax.type.dto.TradePayloadDTO;
import io.reactivestax.type.enums.LookupStatus;
import io.reactivestax.type.enums.PostedStatus;
import io.reactivestax.type.enums.ValidityStatus;

public interface DTOSuppliers {
    final String GOOD_TRADE_PAYLOAD_TRADE_NUMBER = "123";

    Supplier<TradePayloadDTO> goodTradePayloadDTOSupplier = () -> TradePayloadDTO.builder()
            .tradeNumber("123")
            .payload("TDB_00000000,2024-09-19 22:16:18,TDB_CUST_5214938,V,SELL,683,638.02")
            .lookupStatus(String.valueOf(LookupStatus.PASS))
            .validityStatus(String.valueOf(ValidityStatus.VALID))
            .journalEntryStatus(String.valueOf(PostedStatus.POSTED))
            .build();

    Supplier<TradePayloadDTO> badTradePayloadDTOSupplier = () -> TradePayloadDTO.builder()
            .tradeNumber("123")
            .payload("TDB_00000000,2024-09-19 22:16:18,TDB_CUST_5214938,V,SELL,683,638.02")
            .lookupStatus(String.valueOf(LookupStatus.PASS))
            .validityStatus(String.valueOf(ValidityStatus.VALID))
            .journalEntryStatus(String.valueOf(PostedStatus.POSTED))
            .build();
}
