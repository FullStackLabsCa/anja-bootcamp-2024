package io.reactivestax;

import io.reactivestax.service.ChunkGeneratorService;

public class TradeProcessorRunner {
    public static void main(String[] args) {
        ChunkGeneratorService chunkGeneratorService = new ChunkGeneratorService();
        chunkGeneratorService.setupDataSourceAndStartGeneratorAndProcessor();
    }
}
