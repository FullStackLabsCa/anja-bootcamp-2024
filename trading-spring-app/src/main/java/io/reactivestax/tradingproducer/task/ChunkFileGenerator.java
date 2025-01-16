package io.reactivestax.tradingproducer.task;

import io.reactivestax.tradingproducer.service.ChunkGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Scope("prototype")
public class ChunkFileGenerator implements Runnable {

    private final ChunkGeneratorService chunkGeneratorService;

    @Autowired
    public ChunkFileGenerator(ChunkGeneratorService chunkGeneratorService){
        this.chunkGeneratorService = chunkGeneratorService;
    }

    @Override
    public void run() {
        try {
            chunkGeneratorService.generateChunks();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (IOException e) {
//            logger.warning("IO Exception.");
        }
    }
}
