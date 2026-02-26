package com.mipt.kirillchesnokov.hwio;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.*;
import java.nio.channels.*;
import java.util.*;

public class FileProcessor {

    public List<Path> splitFile(String sourcePath, String outputDir, int partSize) throws IOException {
        List<Path> partPaths = new ArrayList<>();
        try (FileChannel fileChannel = FileChannel.open(Paths.get(sourcePath), StandardOpenOption.READ)) {
            long fileSize = fileChannel.size();
            int partCount = (int) Math.ceil((double) fileSize / partSize);
            ByteBuffer buffer = ByteBuffer.allocate(partSize);
            
            for (int i = 0; i < partCount; i++) {
                Path partPath = Paths.get(outputDir, "part" + (i + 1));
                try (FileChannel outputChannel = FileChannel.open(partPath, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
                    int bytesRead = fileChannel.read(buffer);
                    buffer.flip();
                    outputChannel.write(buffer);
                    buffer.clear();
                }
            }
        }
        return partPaths;
    }
    
    public void mergeFiles(List<Path> partPaths, String outputPath) throws IOException {
        try (FileChannel outputChannel = FileChannel.open(Paths.get(outputPath), StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
            for (Path partPath : partPaths) {
                try (FileChannel partChannel = FileChannel.open(partPath, StandardOpenOption.READ)) {
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    while (partChannel.read(buffer) > 0) {
                        buffer.flip();
                        outputChannel.write(buffer);
                        buffer.clear();
                    }
                }
            }
        }
    }
}
