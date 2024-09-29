package io.reactivestax;

import io.reactivestax.service.ChunkGeneratorService;

public class TradeProcessorRunner {
    public static void main(String[] args) {
        ChunkGeneratorService chunkGeneratorService = new ChunkGeneratorService();
        chunkGeneratorService.processTrade("/Users/Anant.Jain/source/student/anja-bootcamp-2024/pipeline" +
                "-multithreaded-trade-processing/trade-processor-app/src/main/java/io/reactivestax/assets/trades.csv");
    }
}
