package com.mipt.angelikaliber.patterns;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Optional;

// TODO: Декоратор для метрик
//  Должен:
//      1. При findDataByKey - замерять скорость работы метода и отправлять через MetricService (реализация уже есть внутри класса)
//      2. При saveData - замерять скорость работы метода и отправлять через MetricService (реализация уже есть внутри класса)
//      3. При deleteData - замерять скорость работы метода и отправлять через MetricService (реализация уже есть внутри класса)
class MetricableDecorator implements DataService {
    private final DataService original;

    public MetricableDecorator(DataService original) {
        this.original = original;
    }

    public static class MetricService {
        public static void sendMetric(Duration duration) {
            System.out.println("Метод выполнялся: " + duration.toString());
        }
    }

    @Override
    public Optional<String> findDataByKey(String key) {
        LocalTime startTime = LocalTime.now();
        Optional<String> result = original.findDataByKey(key);
        MetricService.sendMetric(Duration.between(startTime, LocalTime.now()));
        return result;
    }

    @Override
    public void saveData(String key, String data) {
        LocalTime startTime = LocalTime.now();
        original.saveData(key,data);
        MetricService.sendMetric(Duration.between(startTime, LocalTime.now()));
    }

    @Override
    public boolean deleteData(String key) {
        LocalTime startTime = LocalTime.now();
        boolean result = original.deleteData(key);
        MetricService.sendMetric(Duration.between(startTime, LocalTime.now()));
        return result;
    }
}