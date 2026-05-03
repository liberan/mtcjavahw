package com.mipt.angelikaliber.service;

import com.mipt.angelikaliber.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class TaskStatisticsService {

    private final TaskRepository primaryRepository;
    private final TaskRepository stubRepository;

    public TaskStatisticsService(TaskRepository primaryRepository,
                                 @Qualifier("stubTaskRepository") TaskRepository stubRepository) {
        this.primaryRepository = primaryRepository;
        this.stubRepository = stubRepository;
    }

    public Map<String, Integer> countsBySource() {
        return Map.of(
                describePrimarySource(), primaryRepository.read().size(),
                describeStubSource(), stubRepository.read().size()
        );
    }

    public String describePrimarySource() {
        return primaryRepository.getClass().getSimpleName();
    }

    public String describeStubSource() {
        return stubRepository.getClass().getSimpleName();
    }
}
