package com.mipt.angelikaliber.patterns;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

// TODO: Декоратор для кеширования
//  Должен:
//      1. При findDataByKey - кешировать результаты
//      2. При saveData - обновлять данные в кэше
//      3. При deleteData - инвалидировать кэш
class CachingDecorator implements DataService {
    private final DataService original;
    Map<String, String> cache = new HashMap<>();

    public CachingDecorator(DataService original) {
        this.original = original;
    }

    @Override
    public Optional<String> findDataByKey(String key) {
        if (cache.containsKey(key)) {
            return Optional.ofNullable(cache.get(key));
        }
        return Optional.empty();
    }

    @Override
    public void saveData(String key, String data) {
        cache.put(key, data);
        original.saveData(key, data);
    }

    @Override
    public boolean deleteData(String key) {
        cache.remove(key);
        return original.deleteData(key);
    }

    // todo: ваш код тут
}