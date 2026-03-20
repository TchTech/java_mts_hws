package com.mipt.tchtech.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mipt.tchtech.dto.TaskResponseDto;
import com.mipt.tchtech.service.FavoritesService;
import com.mipt.tchtech.service.TaskService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/favorites")
@Tag(name = "Favorites", description = "Управление избранными задачами")
public class FavoritesController {

    private final FavoritesService favoritesService;
    private final TaskService taskService;

    public FavoritesController(FavoritesService favoritesService, TaskService taskService) {
        this.favoritesService = favoritesService;
        this.taskService = taskService;
    }

    @PostMapping("/{taskId}")
    @Operation(summary = "Добавить задачу в избранное")
    public ResponseEntity<Void> addToFavorites(@PathVariable String taskId, HttpSession session) {
        favoritesService.addToFavorites(taskId, session);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{taskId}")
    @Operation(summary = "Удалить задачу из избранного")
    public ResponseEntity<Void> removeFromFavorites(@PathVariable String taskId, HttpSession session) {
        favoritesService.removeFromFavorites(taskId, session);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "Получить список избранных задач")
    public ResponseEntity<List<TaskResponseDto>> getFavorites(HttpSession session) {
        List<TaskResponseDto> tasks = favoritesService.getFavoriteIds(session).stream()
                .flatMap(id -> taskService.getTaskById(id).stream())
                .collect(Collectors.toList());
        return ResponseEntity.ok(tasks);
    }
}
