package io.reactivestax.service;

import io.reactivestax.util.ApplicationPropertiesUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class FileProcessorTest {

    public static final String APPLICATION_HIBERNATE_RABBIT_MQH_2_TEST_PROPERTIES = "applicationHibernateRabbitMQH2Test.properties";

    private ApplicationPropertiesUtils applicationPropertiesUtils;

    @BeforeEach
    void setUp() {
        // Mock the construction of FileWriter and BufferedWriter
        try (MockedConstruction<FileWriter> mockedFileWriter = Mockito.mockConstruction(FileWriter.class);
             MockedConstruction<BufferedWriter> mockedBufferedWriter = Mockito.mockConstruction(BufferedWriter.class)) {
        }
        applicationPropertiesUtils = ApplicationPropertiesUtils
                .getInstance(APPLICATION_HIBERNATE_RABBIT_MQH_2_TEST_PROPERTIES);
        applicationPropertiesUtils.loadApplicationProperties(APPLICATION_HIBERNATE_RABBIT_MQH_2_TEST_PROPERTIES);
    }

    @Test
    void testWriteToFile() throws IOException, InterruptedException {
        try (MockedConstruction<FileWriter> mockedFileWriter = Mockito.mockConstruction(FileWriter.class);
             MockedConstruction<BufferedWriter> mockedBufferedWriter = Mockito.mockConstruction(BufferedWriter.class);
             MockedStatic<Files> filesMockedStatic = mockStatic(Files.class)){

//            // Create an instance of the class under test
//            FileProcessor fileProcessor = new FileProcessor();
//
//            // Call the method that uses BufferedWriter and FileWriter
//            fileProcessor.writeToFile("path/to/file.txt", "Hello, World!");

//            // Verify that BufferedWriter's write method was called with the expected content
//            BufferedWriter mockBufferedWriter = mockedBufferedWriter.constructed().get(0);
//            verify(mockBufferedWriter).write("Hello, World!");
//
//            // Verify that BufferedWriter's close method was called due to try-with-resources
//            verify(mockBufferedWriter).close();
//            ///

            BufferedWriter mockBufferedWriter = mockedBufferedWriter.constructed().get(0);
            ChunkGeneratorService chunkGeneratorService = ChunkGeneratorService.getInstance();
            chunkGeneratorService.generateChunks();
            // Mock Files.createDirectories to do nothing
            filesMockedStatic.when(() -> Files.createDirectories(any())).thenReturn(null);
            // Verify that BufferedWriter's write method was called with the expected content
            verify(mockBufferedWriter).write("TDB_000001,2024-09-19 22:16:18,TDB_CUST_5214938,TSLA,BUY,1,638.02");

            verify(mockBufferedWriter).close();

        }
    }
}