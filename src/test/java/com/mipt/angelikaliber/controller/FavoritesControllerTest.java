package com.mipt.angelikaliber.controller;

import com.mipt.angelikaliber.model.Task;
import com.mipt.angelikaliber.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.mock.web.MockHttpSession;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@SpringBootTest(webEnvironment = MOCK)
@ActiveProfiles("dev")
class FavoritesControllerTest {

    @Autowired
    private org.springframework.web.context.WebApplicationContext context;

    @MockitoBean(name = "inMemoryTaskRepository")
    private TaskRepository taskRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        Mockito.reset(taskRepository);
        when(taskRepository.read()).thenReturn(List.of());
        when(taskRepository.create(any(Task.class))).thenAnswer(inv -> {
            Task t = inv.getArgument(0);
            t.setId(1L);
            return t;
        });
        when(taskRepository.read(1L)).thenReturn(Optional.of(new Task(1L, "A", "a", false)));
        when(taskRepository.read(2L)).thenReturn(Optional.of(new Task(2L, "B", "b", true)));
        when(taskRepository.read(404L)).thenReturn(Optional.empty());
        mockMvc = webAppContextSetup(context).build();
    }

    @Test
    void addRemoveAndListFavorites() throws Exception {
        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(post("/api/favorites/1").session(session))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/api/favorites/2").session(session))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/favorites").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));

        mockMvc.perform(delete("/api/favorites/1").session(session))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/favorites").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(2));
    }

    @Test
    void addUnknownTaskReturnsNotFound() throws Exception {
        mockMvc.perform(post("/api/favorites/404"))
                .andExpect(status().isNotFound());
    }

    @Test
    void favoritesAreSessionScoped() throws Exception {
        MockHttpSession s1 = new MockHttpSession();
        MockHttpSession s2 = new MockHttpSession();

        mockMvc.perform(post("/api/favorites/1").session(s1))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/favorites").session(s2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
