package com.mipt.angelikaliber.patterns;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ConcurrentModificationException;
import java.util.Optional;

// TODO: Декоратор для валидации
//  Должен:
//      1. При findDataByKey - влидируются входные данные
//      2. При saveData - влидируются входные данные
//      3. При deleteData - влидируются входные данные
class ValidationDecorator implements DataService {
    private final DataService original;

    public ValidationDecorator(DataService original) {
        this.original = original;
    }

    @Override
    public Optional<String> findDataByKey(String key) {
        if (key == null) {
            System.out.println("Error! key не может быть null");
            return Optional.empty();
        }
        return original.findDataByKey(key);
    }

    @Override
    public void saveData(String key, String data) {
        if (key == null) {
            System.out.println("Error! key не может быть null");
            return;
        }
        original.saveData(key, data);
    }

    @Override
    public boolean deleteData(String key) {
        if (key == null) {
            System.out.println("Error! key не может быть null");
            return false;
        }
        return original.deleteData(key);
    }
}