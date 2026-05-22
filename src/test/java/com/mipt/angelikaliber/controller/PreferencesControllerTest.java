package com.mipt.angelikaliber.controller;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@SpringBootTest(webEnvironment = MOCK)
@ActiveProfiles("test")
class PreferencesControllerTest {

    @Autowired
    private org.springframework.web.context.WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = webAppContextSetup(context).build();
    }

    @Test
    void readReturnsDefaultAndSetsCookie() throws Exception {
        MvcResult res = mockMvc.perform(get("/api/preferences/view"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mode").value("detailed"))
                .andReturn();

        Cookie cookie = res.getResponse().getCookie("viewPreference");
        assertThat(cookie).isNotNull();
        assertThat(cookie.getValue()).isEqualTo("detailed");
    }

    @Test
    void updateAcceptsValidMode() throws Exception {
        MvcResult res = mockMvc.perform(post("/api/preferences/view").param("mode", "compact"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mode").value("compact"))
                .andReturn();

        Cookie cookie = res.getResponse().getCookie("viewPreference");
        assertThat(cookie).isNotNull();
        assertThat(cookie.getValue()).isEqualTo("compact");
    }

    @Test
    void updateRejectsInvalidMode() throws Exception {
        mockMvc.perform(post("/api/preferences/view").param("mode", "huge"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void readUsesIncomingCookieValue() throws Exception {
        mockMvc.perform(get("/api/preferences/view").cookie(new Cookie("viewPreference", "compact")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mode").value("compact"));
    }
}
