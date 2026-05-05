package com.mipt.tchtech.service;

import java.util.List;

import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;

import static org.assertj.core.api.Assertions.assertThat;

class FavoritesServiceTest {

    private FavoritesService service;
    private HttpSession session;

    @BeforeEach
    void setUp() {
        service = new FavoritesService();
        session = new MockHttpSession();
    }

    @Test
    void addToFavorites_addsId_andIsIdempotent() {
        service.addToFavorites(1L, session);
        service.addToFavorites(2L, session);
        service.addToFavorites(1L, session);

        List<Long> ids = service.getFavoriteIds(session);
        assertThat(ids).containsExactly(1L, 2L);
    }

    @Test
    void removeFromFavorites_removesOnlyTargetId() {
        service.addToFavorites(1L, session);
        service.addToFavorites(2L, session);

        service.removeFromFavorites(1L, session);

        assertThat(service.getFavoriteIds(session)).containsExactly(2L);
    }

    @Test
    void getFavoriteIds_emptyByDefault() {
        assertThat(service.getFavoriteIds(session)).isEmpty();
    }

    @Test
    void getFavoriteIds_returnsCopy_notLiveList() {
        service.addToFavorites(1L, session);
        List<Long> snapshot = service.getFavoriteIds(session);
        snapshot.clear();

        assertThat(service.getFavoriteIds(session)).containsExactly(1L);
    }
}
