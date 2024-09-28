package io.reactivestax;

import io.reactivestax.service.ChunkProcessorService;

public class TradeProcessorRunner {
    public static void main(String[] args) {
        ChunkProcessorService chunkProcessorService = new ChunkProcessorService();
        chunkProcessorService.processTrade("/Users/Anant.Jain/source/student/anja-bootcamp-2024/pipeline-multithreaded-trade-processing/trade-processor-app/src/main/java/io/reactivestax/assets/trades.csv");
    }
}
