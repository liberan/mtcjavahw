package com.mipt.angelikaliber.api;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class ProfileController {

    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> profile(Authentication authentication) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("username", authentication.getName());
        body.put("authorities", authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).toList());
        return ResponseEntity.ok(body);
    }

    @GetMapping("/docs")
    public ResponseEntity<Map<String, Object>> docs(Authentication authentication) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("title", "Internal documentation");
        body.put("viewer", authentication.getName());
        body.put("content", "Confidential reference for users with READ_PRIVILEGE.");
        return ResponseEntity.ok(body);
    }
}
