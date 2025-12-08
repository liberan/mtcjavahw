package com.mipt.angelikaliber.io;

import java.io.*;

public class TextFileAnalyzer {

    public static class AnalysisResult {
        private final long lineCount;  // количество строк в файле
        private final long wordCount; // количество слов в файле
        private final long charCount; // количество символов в файле

        @Override
        public String toString() {
            return "AnalysisResult{" +
                    "lineCount=" + lineCount +
                    ", wordCount=" + wordCount +
                    ", charCount=" + charCount +
                    '}';
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



        public AnalysisResult(long lineCount, long wordCount, long charCount) {
            this.lineCount = lineCount;
            this.wordCount = wordCount;
            this.charCount = charCount;
        }

        public AnalysisResult() {
            this.lineCount = 0;
            this.wordCount = 0;
            this.charCount = 0;
        }

        // TODO: не забудь прописать конструктор, геттеры, toString()
    }

    public AnalysisResult analyzeFile(String filePath) throws IOException {
        // TODO: Реализовать анализ файла по переданному пути `filePath`, используя BufferedReader
        // Подсчитать в файле: lineCount, wordCount, charCount
        // Использовать try-with-resources для автоматического закрытия потоков
        long lineCount = 0;
        long wordCount = 0;
        long charCount = 0;
        String str = "";
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            while((str = br.readLine()) != null) {
                System.out.println(str);
                lineCount++;
                charCount += str.length();
                String[] words = str.split(" ");
                wordCount += words.length;
            }
        }

        return new AnalysisResult(lineCount, wordCount, charCount);
    }

    public void saveAnalysisResult(AnalysisResult result, String outputPath) throws IOException {
        // TODO: Сохранить результаты в файл по указанному пути `outputPath` используя BufferedWriter в
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputPath))) {
            bw.write(result.toString());
        }
    }
}