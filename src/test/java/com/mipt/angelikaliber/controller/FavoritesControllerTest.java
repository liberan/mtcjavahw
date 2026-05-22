package com.mipt.angelikaliber.controller;

import com.mipt.angelikaliber.model.Priority;
import com.mipt.angelikaliber.model.Task;
import com.mipt.angelikaliber.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@SpringBootTest(webEnvironment = MOCK)
@ActiveProfiles("test")
class FavoritesControllerTest {

    @Autowired
    private org.springframework.web.context.WebApplicationContext context;

    @Autowired
    private TaskRepository taskRepository;

    private MockMvc mockMvc;
    private Long idOne;
    private Long idTwo;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
        Task one = new Task(null, "A", "a", false);
        one.setPriority(Priority.LOW);
        Task two = new Task(null, "B", "b", true);
        two.setPriority(Priority.HIGH);
        idOne = taskRepository.save(one).getId();
        idTwo = taskRepository.save(two).getId();
        mockMvc = webAppContextSetup(context).build();
    }

    @Test
    void addRemoveAndListFavorites() throws Exception {
        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(post("/api/favorites/" + idOne).session(session))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/api/favorites/" + idTwo).session(session))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/favorites").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        mockMvc.perform(delete("/api/favorites/" + idOne).session(session))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/favorites").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(idTwo));
    }

    @Test
    void addUnknownTaskReturnsNotFound() throws Exception {
        mockMvc.perform(post("/api/favorites/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void favoritesAreSessionScoped() throws Exception {
        MockHttpSession s1 = new MockHttpSession();
        MockHttpSession s2 = new MockHttpSession();

        mockMvc.perform(post("/api/favorites/" + idOne).session(s1))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/favorites").session(s2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
