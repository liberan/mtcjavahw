package com.mipt.angelikaliber.patterns;

import java.util.Optional;

// TODO: Декоратор для логирования
//  Должен:
//      1. При findDataByKey - логировать действие через System.out.println
//      2. При saveData - логировать действие через System.out.println
//      3. При deleteData - логировать действие через System.out.println
class LoggingDecorator implements DataService {
    private final DataService original;

    public LoggingDecorator(DataService original) {
        this.original = original;
    }

    @Override
    public boolean deleteData(String key) {
        System.out.println("data deleted (key: " + key.toString() + ")");
        return original.deleteData(key);
    }

    @Override
    public void saveData(String key, String data) {
        System.out.println("data saved (key: " + key.toString() + "; data: " + data.toString() + ")");
        original.saveData(key, data);
    }

    @Override
    public Optional<String> findDataByKey(String key) {
        System.out.println("data found (key: " + key.toString() + ")");
        return original.findDataByKey(key);
    }
}