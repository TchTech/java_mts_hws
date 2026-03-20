package com.mipt.tchtech.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;

@Service
public class FavoritesService {

    private static final String FAVORITES_KEY = "favoriteTaskIds";

    public void addToFavorites(String taskId, HttpSession session) {
        List<String> favorites = getFavoritesList(session);
        if (!favorites.contains(taskId)) {
            favorites.add(taskId);
        }
    }

    public void removeFromFavorites(String taskId, HttpSession session) {
        getFavoritesList(session).remove(taskId);
    }

    public List<String> getFavoriteIds(HttpSession session) {
        return new ArrayList<>(getFavoritesList(session));
    }

    @SuppressWarnings("unchecked")
    private List<String> getFavoritesList(HttpSession session) {
        List<String> favorites = (List<String>) session.getAttribute(FAVORITES_KEY);
        if (favorites == null) {
            favorites = new ArrayList<>();
            session.setAttribute(FAVORITES_KEY, favorites);
        }
        return favorites;
    }
}
