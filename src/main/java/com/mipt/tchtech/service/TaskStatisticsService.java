package com.mipt.tchtech.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mipt.tchtech.repository.TaskRepository;

@Service
public class TaskStatisticsService {

    private final TaskRepository primaryRepository;
    private final TaskRepository stubRepository;

    public TaskStatisticsService(
            TaskRepository primaryRepository,
            @Qualifier("stubTaskRepository") TaskRepository stubRepository) {
        this.primaryRepository = primaryRepository;
        this.stubRepository = stubRepository;
    }

    public String compareRepositories() {
        int primaryCount = primaryRepository.findAll().size();
        int stubCount = stubRepository.findAll().size();
        return String.format("Основной репозиторий содержит %d задач. Репозиторий-заглушка содержит %d задач.", primaryCount, stubCount);
    }
}
