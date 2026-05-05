package com.mipt.tchtech.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.mipt.tchtech.dto.TaskResponseDto;
import com.mipt.tchtech.model.Priority;
import com.mipt.tchtech.service.FavoritesService;
import com.mipt.tchtech.service.TaskService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FavoritesController.class)
class FavoritesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FavoritesService favoritesService;

    @MockitoBean
    private TaskService taskService;

    @Test
    void addToFavorites_returns200_andDelegatesToService() throws Exception {
        mockMvc.perform(post("/api/favorites/{taskId}", 10L))
                .andExpect(status().isOk());

        verify(favoritesService).addToFavorites(eq(10L), any(HttpSession.class));
    }

    @Test
    void removeFromFavorites_returns204() throws Exception {
        mockMvc.perform(delete("/api/favorites/{taskId}", 10L))
                .andExpect(status().isNoContent());

        verify(favoritesService).removeFromFavorites(eq(10L), any(HttpSession.class));
    }

    @Test
    void getFavorites_returnsTasksResolvedByIds() throws Exception {
        given(favoritesService.getFavoriteIds(any(HttpSession.class))).willReturn(Arrays.asList(1L, 2L));

        TaskResponseDto t1 = new TaskResponseDto();
        t1.setId(1L);
        t1.setTitle("A");
        t1.setPriority(Priority.HIGH);

        TaskResponseDto t2 = new TaskResponseDto();
        t2.setId(2L);
        t2.setTitle("B");
        t2.setPriority(Priority.MEDIUM);

        given(taskService.getTaskById(1L)).willReturn(Optional.of(t1));
        given(taskService.getTaskById(2L)).willReturn(Optional.of(t2));

        mockMvc.perform(get("/api/favorites")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].title").value("B"));

        verify(favoritesService).getFavoriteIds(any(HttpSession.class));
    }

    @Test
    void getFavorites_skipsMissingTasks() throws Exception {
        given(favoritesService.getFavoriteIds(any(HttpSession.class))).willReturn(List.of(1L, 99L));
        TaskResponseDto t1 = new TaskResponseDto();
        t1.setId(1L);
        t1.setTitle("Есть");

        given(taskService.getTaskById(1L)).willReturn(Optional.of(t1));
        given(taskService.getTaskById(99L)).willReturn(Optional.empty());

        mockMvc.perform(get("/api/favorites"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1));
    }
}
