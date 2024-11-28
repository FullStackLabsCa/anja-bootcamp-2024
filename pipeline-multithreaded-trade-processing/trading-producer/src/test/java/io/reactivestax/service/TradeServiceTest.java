package io.reactivestax.service;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TradeServiceTest {
    private final TradeService tradeService = TradeService.getInstance();

    @Test
    void testFileLineCounter() throws IOException {
        long counter = tradeService.fileLineCounter("src/test/resources/trades_10000_sameAccountAndPosition.csv");
        assertEquals(9999, counter);
    }

    @Test
    void testBuildFilePath() {
        String chunkFilePath = "src/test/resources/chunks/trade_records_chunk";
        String filePath = tradeService.buildFilePath(5, chunkFilePath);
        assertEquals(chunkFilePath + 5 + ".csv", filePath);
    }
}
