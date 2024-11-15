package io.reactivestax.service;

import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import static org.mockito.Mockito.verify;

public class FileProcessorTest {

    @Test
    public void testWriteToFile() throws IOException {
        // Mock the construction of FileWriter and BufferedWriter
        try (MockedConstruction<FileWriter> mockedFileWriter = Mockito.mockConstruction(FileWriter.class);
             MockedConstruction<BufferedWriter> mockedBufferedWriter = Mockito.mockConstruction(BufferedWriter.class)) {

            // Create an instance of the class under test
            FileProcessor fileProcessor = new FileProcessor();

            // Call the method that uses BufferedWriter and FileWriter
            fileProcessor.writeToFile("path/to/file.txt", "Hello, World!");

            // Verify that BufferedWriter's write method was called with the expected content
            BufferedWriter mockBufferedWriter = mockedBufferedWriter.constructed().get(0);
            verify(mockBufferedWriter).write("Hello, World!");

            // Verify that BufferedWriter's close method was called due to try-with-resources
            verify(mockBufferedWriter).close();
        }
    }
}