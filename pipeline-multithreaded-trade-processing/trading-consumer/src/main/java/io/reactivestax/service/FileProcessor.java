package io.reactivestax.service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class FileProcessor {
    public void writeToFile(String filePath, String content) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(content);
        }
    }
}
