package io.reactivestax.service;

import io.reactivestax.task.ChunkGenerator;
import io.reactivestax.type.exception.FileNotFoundRuntimeException;
import io.reactivestax.util.ApplicationPropertiesUtils;
import io.reactivestax.util.QueueProvider;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static io.reactivestax.util.GeneralUtils.logTheExceptionTrace;
import static java.nio.file.Files.lines;

//@Log4j2
public class ChunkGeneratorService implements ChunkGenerator {

    private static ChunkGeneratorService instance;

    private ChunkGeneratorService() {
    }

    public static synchronized ChunkGeneratorService getInstance() {
        if (instance == null) {
            instance = new ChunkGeneratorService();
        }

        return instance;
    }

    @Override
    public void generateChunks() throws IOException, InterruptedException {
        ApplicationPropertiesUtils applicationPropertiesUtils = ApplicationPropertiesUtils.getInstance();

        TradeFileChunkDetailsRecord tradeFileChunkDetailsRecord = calculateTotalNumberOfLinesPerChunk(applicationPropertiesUtils);
        TradeService tradeService = TradeService.getInstance();

        int tempChunkCount = 1;
        String tradeChunkFilePath = tradeService.buildNextChunkFilePath(tempChunkCount, applicationPropertiesUtils.getChunkFilePathWithName());
        Files.createDirectories(Paths.get(applicationPropertiesUtils.getChunkDirectoryPath()));
        BufferedWriter writer = new BufferedWriter(new FileWriter(tradeChunkFilePath));
        long tempLineCount = 0;
        try (InputStream inputStream = ApplicationPropertiesUtils.class.getClassLoader().getResourceAsStream(tradeFileChunkDetailsRecord.tradeFilePath());
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            //skipping header
            String line = reader.readLine();
            //
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.newLine();
                tempLineCount++;
                if (tempLineCount == tradeFileChunkDetailsRecord.linesCountPerChunkFile() && tempChunkCount != tradeFileChunkDetailsRecord.tradeRecordChunksCount()) {
                    tempChunkCount++;
                    tempLineCount = 0;
                    writer.close();
                    QueueProvider.getInstance().getChunkQueue().put(tradeChunkFilePath);
                    tradeChunkFilePath = tradeService.buildNextChunkFilePath(tempChunkCount, applicationPropertiesUtils.getChunkFilePathWithName());
                    writer = new BufferedWriter(new FileWriter(tradeChunkFilePath));
                }
            }
            QueueProvider.getInstance().getChunkQueue().put(tradeChunkFilePath);
        } finally {
            writer.close();
        }
    }

    private TradeFileChunkDetailsRecord calculateTotalNumberOfLinesPerChunk(ApplicationPropertiesUtils applicationPropertiesUtils) {
        String tradeFilePath = applicationPropertiesUtils.getFilePath();
        long totalNoOfLinesInTradeFile = obtainTotalNoOfLinesInTradeFile(tradeFilePath);
        int tradeRecordChunksCount = applicationPropertiesUtils.getNumberOfChunks();
        long linesCountPerChunkFile = totalNoOfLinesInTradeFile / tradeRecordChunksCount;
        return new TradeFileChunkDetailsRecord(tradeFilePath, tradeRecordChunksCount, linesCountPerChunkFile);
    }

    private record TradeFileChunkDetailsRecord(String tradeFilePath, int tradeRecordChunksCount,
                                               long linesCountPerChunkFile) {
    }

    private long obtainTotalNoOfLinesInTradeFile(String tradeFilePath) {
        try (InputStream inputStream = ApplicationPropertiesUtils.class.getClassLoader().getResourceAsStream(tradeFilePath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
             Stream<String> lines = reader.lines()) {
            return lines.count();
        } catch (IOException e) {
            logTheExceptionTrace(e);
            throw new FileNotFoundRuntimeException(String.format("File with name %s not found", tradeFilePath));
        }
    }

}
