package com.mipt.angelikaliber.patterns;

import org.junit.jupiter.api.Test;

import javax.print.attribute.standard.DateTimeAtCompleted;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

public class DecoratorTests {
    @Test
    void loggingDecoratorTest () {
        DataService loggingDS = new LoggingDecorator(new SimpleDataService());

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        try {
            loggingDS.saveData("123","abc");
            assertEquals("data saved (key: 123; data: abc)" + System.lineSeparator(), outContent.toString());
            outContent.reset();

            loggingDS.findDataByKey("123");
            assertEquals("data found (key: 123)" + System.lineSeparator(), outContent.toString());
            outContent.reset();

            loggingDS.deleteData("123");
            assertEquals("data deleted (key: 123)" + System.lineSeparator(), outContent.toString());
            outContent.reset();
        }
        finally {
            System.setOut(originalOut);
        }
    }

    @Test
    void MetricableDecoratorTest() {
        DataService metricableDS = new MetricableDecorator(new SimpleDataService());

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        try {
            metricableDS.saveData("123","abc");
            Pattern pattern = Pattern.compile("Метод выполнялся: .+", Pattern.CASE_INSENSITIVE);
            assertTrue(pattern.matcher(outContent.toString()).find());
            outContent.reset();

            metricableDS.findDataByKey("123");
            assertTrue(pattern.matcher(outContent.toString()).find());
            outContent.reset();

            metricableDS.deleteData("123");
            assertTrue(pattern.matcher(outContent.toString()).find());
            outContent.reset();
        }
        finally {
            System.setOut(originalOut);
        }
    }

    @Test
    void ValidationDecoratorTest() {
        DataService validationDS = new ValidationDecorator(new SimpleDataService());

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        try {
            validationDS.saveData("123","abc");
            assertEquals("", outContent.toString());
            outContent.reset();

            validationDS.saveData(null,"abc");
            assertEquals("Error! key не может быть null" + System.lineSeparator(), outContent.toString());
            outContent.reset();

            validationDS.findDataByKey("123");
            assertEquals("", outContent.toString());
            outContent.reset();

            validationDS.findDataByKey(null);
            assertEquals("Error! key не может быть null" + System.lineSeparator(), outContent.toString());
            outContent.reset();

            validationDS.deleteData("123");
            assertEquals("", outContent.toString());
            outContent.reset();

            validationDS.deleteData(null);
            assertEquals("Error! key не может быть null" + System.lineSeparator(), outContent.toString());
            outContent.reset();
        }
        finally {
            System.setOut(originalOut);
        }
    }
}
