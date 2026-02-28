package com.mipt.tchtech.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.mipt.tchtech.repository.StubTaskRepository;
import com.mipt.tchtech.repository.TaskRepository;

/**
 * Конфигурационный класс приложения.
 * Определяет дополнительные Spring бины, такие как репозиторий-заглушка.
 *
 * @author mts.tchtech
 * @version 1.0
 */
@Configuration
public class AppConfig {

    @Bean
    public TaskRepository stubTaskRepository() {
        return new StubTaskRepository();
    }
}
