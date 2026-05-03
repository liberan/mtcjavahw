package com.mipt.angelikaliber.config;

import com.mipt.angelikaliber.repository.StubTaskRepository;
import com.mipt.angelikaliber.repository.TaskRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Java-based configuration that registers the stub repository as a
 * {@link TaskRepository} bean.
 *
 * <p>The primary repository is registered via the {@code @Repository}
 * stereotype on {@link com.mipt.angelikaliber.repository.InMemoryTaskRepository};
 * keeping the stub registration here demonstrates the {@code @Bean} factory
 * style alongside it.
 */
@Configuration
public class RepositoryConfig {

    /**
     * Builds the stub repository. The bean name {@code stubTaskRepository}
     * is referenced by {@code @Qualifier} in
     * {@link com.mipt.angelikaliber.service.TaskStatisticsService}.
     *
     * @return a {@link StubTaskRepository} instance with fixed seed data
     */
    @Bean
    public TaskRepository stubTaskRepository() {
        return new StubTaskRepository();
    }
}
