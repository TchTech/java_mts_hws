package com.mipt.tchtech.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/preferences")
@Tag(name = "Preferences", description = "Пользовательские настройки отображения")
public class PreferencesController {

    private static final String VIEW_COOKIE = "viewPreference";
    private static final String DEFAULT_MODE = "detailed";

    @GetMapping("/view")
    @Operation(summary = "Получить текущий режим отображения")
    public ResponseEntity<Map<String, String>> getViewPreference(
            @CookieValue(name = VIEW_COOKIE, defaultValue = DEFAULT_MODE) String viewPreference) {
        return ResponseEntity.ok(Map.of("mode", viewPreference));
    }

    @PostMapping("/view")
    @Operation(summary = "Установить режим отображения")
    public ResponseEntity<Map<String, String>> setViewPreference(
            @RequestParam String mode,
            HttpServletResponse response) {
        if (!mode.equals("compact") && !mode.equals("detailed")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Допустимые значения: compact, detailed"));
        }
        Cookie cookie = new Cookie(VIEW_COOKIE, mode);
        cookie.setPath("/");
        cookie.setMaxAge(30 * 24 * 60 * 60);
        response.addCookie(cookie);
        return ResponseEntity.ok(Map.of("mode", mode));
    }
}
