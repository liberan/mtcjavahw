package com.mipt.angelikaliber.controller;

import com.mipt.angelikaliber.repository.TaskRepository;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@SpringBootTest(webEnvironment = MOCK)
@ActiveProfiles("dev")
class PreferencesControllerTest {

    @Autowired
    private org.springframework.web.context.WebApplicationContext context;

    @MockitoBean(name = "inMemoryTaskRepository")
    private TaskRepository taskRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        Mockito.reset(taskRepository);
        when(taskRepository.read()).thenReturn(List.of());
        when(taskRepository.create(any())).thenAnswer(inv -> {
            com.mipt.angelikaliber.model.Task t = inv.getArgument(0);
            t.setId(1L);
            return t;
        });
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
