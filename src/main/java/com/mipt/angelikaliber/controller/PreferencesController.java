package com.mipt.angelikaliber.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/preferences")
@Tag(name = "Preferences", description = "User preferences stored in cookies")
public class PreferencesController {

    public static final String COOKIE_NAME = "viewPreference";
    public static final String DEFAULT_VIEW = "detailed";
    private static final Set<String> ALLOWED = Set.of("compact", "detailed");

    @Operation(summary = "Read view preference")
    @GetMapping("/view")
    public ResponseEntity<Map<String, String>> read(
            @CookieValue(value = COOKIE_NAME, defaultValue = DEFAULT_VIEW) String mode,
            HttpServletResponse response) {
        if (!ALLOWED.contains(mode)) {
            mode = DEFAULT_VIEW;
        }
        Cookie cookie = buildCookie(mode);
        response.addCookie(cookie);
        return ResponseEntity.ok(Map.of("mode", mode));
    }

    @Operation(summary = "Update view preference")
    @PostMapping("/view")
    public ResponseEntity<Map<String, String>> update(@RequestParam String mode,
                                                      HttpServletResponse response) {
        if (!ALLOWED.contains(mode)) {
            return ResponseEntity.badRequest().body(Map.of("error", "mode must be compact or detailed"));
        }
        response.addCookie(buildCookie(mode));
        return ResponseEntity.ok(Map.of("mode", mode));
    }

    private Cookie buildCookie(String mode) {
        Cookie cookie = new Cookie(COOKIE_NAME, mode);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24 * 30);
        cookie.setHttpOnly(false);
        return cookie;
    }
}
