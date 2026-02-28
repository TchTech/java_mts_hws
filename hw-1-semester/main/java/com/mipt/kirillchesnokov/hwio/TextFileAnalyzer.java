package com.mipt.kirillchesnokov.hwio;
import java.io.*;
import java.nio.file.*;

public class TextFileAnalyzer {

    public static class AnalysisResult {
        private final long lineCount;
        private final long wordCount;
        private final long charCount;

        public AnalysisResult(long lineCount, long wordCount, long charCount) {
            this.lineCount = lineCount;
            this.wordCount = wordCount;
            this.charCount = charCount;
        }

        public long getLineCount() {
            return lineCount;
        }

        public long getWordCount() {
            return wordCount;
        }

        public long getCharCount() {
            return charCount;
        }

        @Override
        public String toString() {
            return "Lines: " + lineCount + ", Words: " + wordCount + ", Characters: " + charCount;
        }
    }
    
    public AnalysisResult analyzeFile(String filePath) throws IOException {
        long lineCount = 0;
        long wordCount = 0;
        long charCount = 0;

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lineCount++;
                wordCount += line.split("\s+").length;
                charCount += line.length();
            }
        }
        
        return new AnalysisResult(lineCount, wordCount, charCount);
    }
    
    public void saveAnalysisResult(AnalysisResult result, String outputPath) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputPath))) {
            writer.write(result.toString());
        }
    }
}
