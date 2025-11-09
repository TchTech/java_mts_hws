package com.mipt.kirillchesnokov.hwio;

import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class FileProcessorTest {

    @Test
    void testSplitAndMergeFile() throws IOException {
        FileProcessor processor = new FileProcessor();

        Path testFile = Files.createTempFile("test", ".dat");
        byte[] testData = new byte[1500];
        new java.util.Random().nextBytes(testData);
        Files.write(testFile, testData);

        String outputDir = Files.createTempDirectory("parts").toString();
        List<Path> parts = processor.splitFile(testFile.toString(), outputDir, 500);

        assertEquals(3, parts.size());

        Path mergedFile = Files.createTempFile("merged", ".dat");
        processor.mergeFiles(parts, mergedFile.toString());

        assertArrayEquals(Files.readAllBytes(testFile), Files.readAllBytes(mergedFile));
    }
}
