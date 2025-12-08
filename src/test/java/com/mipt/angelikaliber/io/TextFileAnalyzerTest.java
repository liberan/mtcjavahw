package com.mipt.angelikaliber.io;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TextFileAnalyzerTest {

    @Test
    void testAnalyzeFile() throws IOException {
        TextFileAnalyzer analyzer = new TextFileAnalyzer();

        // Создаем временный тестовый файл
        Path testFile = Files.createTempFile("test", ".txt");
        Files.write(testFile, Arrays.asList("Hello world!", "This is test."));

        TextFileAnalyzer.AnalysisResult result = analyzer.analyzeFile(testFile.toString());
        System.out.println(result);

        // TODO: пропиши тут все проверки на `result`

        assertEquals(result.getLineCount(), 2);
        assertEquals(result.getWordCount(), 5);
        assertEquals(result.getCharCount(), 25);
    }

    @Test
    void testSaveAnalysisResult() throws IOException {
        TextFileAnalyzer analyzer = new TextFileAnalyzer();

        // Создаем тестовый результат
        TextFileAnalyzer.AnalysisResult result = new TextFileAnalyzer.AnalysisResult(2, 5, 20);

        // Сохранить в файл
        Path outputFile = Files.createTempFile("analysis", ".txt");
        analyzer.saveAnalysisResult(result, outputFile.toString());

        assertTrue(Files.size(outputFile) > 0);
        assertEquals(result.toString(), (new BufferedReader(new FileReader(outputFile.toString())).readLine()));

        // Проверить что файл создан и содержит данные
        // TODO добавь проверки на файл по пути: outputFile
        //  1. его размер больше 0
        //  2. прочитай этот файл и посмотри что в нем записаны аналогинчные данные как в объекте `result`
    }
}