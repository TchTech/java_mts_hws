package com.mipt.tchtech.controller;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PreferencesController.class)
class PreferencesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getView_withoutCookie_returnsDefaultMode() throws Exception {
        mockMvc.perform(get("/api/preferences/view"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mode").value("detailed"));
    }

    @Test
    void getView_withCookie_returnsCookieValue() throws Exception {
        mockMvc.perform(get("/api/preferences/view")
                        .cookie(new Cookie("viewPreference", "compact")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mode").value("compact"));
    }

    @Test
    void setView_validMode_setsCookieAndReturnsValue() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/preferences/view").param("mode", "compact"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mode").value("compact"))
                .andExpect(cookie().value("viewPreference", "compact"))
                .andExpect(cookie().path("viewPreference", "/"))
                .andReturn();

        Cookie cookie = result.getResponse().getCookie("viewPreference");
        assertThat(cookie).isNotNull();
        assertThat(cookie.getMaxAge()).isEqualTo(30 * 24 * 60 * 60);
    }

    @Test
    void setView_invalidMode_returns400() throws Exception {
        mockMvc.perform(post("/api/preferences/view").param("mode", "weird"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }
}
