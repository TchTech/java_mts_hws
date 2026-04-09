package com.mipt.tchtech.api;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
public class InternalApiController {


    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getProfile(Authentication authentication) {
        List<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        Map<String, Object> profile = new LinkedHashMap<>();
        profile.put("username", authentication.getName());
        profile.put("authorities", authorities);
        return ResponseEntity.ok(profile);
    }


    @GetMapping("/docs")
    public ResponseEntity<Map<String, Object>> getDocs() {
        Map<String, Object> docs = new LinkedHashMap<>();
        docs.put("version", "1.0.0");
        docs.put("description", "Resilient Secure HTTP Gateway — API Documentation");
        docs.put("endpoints", List.of(
                "POST /api/v1/auth/login",
                "GET  /api/v1/profile",
                "GET  /api/v1/docs",
                "POST /api/v1/tasks",
                "GET  /api/v1/tasks/{id}",
                "GET  /api/v1/tasks?completed=&limit=",
                "DELETE /api/v1/tasks/{id}"
        ));
        return ResponseEntity.ok(docs);
    }
}