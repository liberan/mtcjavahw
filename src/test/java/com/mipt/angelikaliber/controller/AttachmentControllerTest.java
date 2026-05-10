package com.mipt.angelikaliber.controller;

import com.mipt.angelikaliber.model.Task;
import com.mipt.angelikaliber.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@SpringBootTest(webEnvironment = MOCK)
@ActiveProfiles("dev")
class AttachmentControllerTest {

    @Autowired
    private org.springframework.web.context.WebApplicationContext context;

    @MockitoBean(name = "inMemoryTaskRepository")
    private TaskRepository taskRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        Mockito.reset(taskRepository);
        when(taskRepository.read()).thenReturn(java.util.List.of());
        when(taskRepository.create(any(Task.class))).thenAnswer(inv -> {
            Task t = inv.getArgument(0);
            t.setId(1L);
            return t;
        });
        Task t = new Task(1L, "T", "D", false);
        when(taskRepository.read(1L)).thenReturn(Optional.of(t));
        mockMvc = webAppContextSetup(context).build();
    }

    @Test
    void uploadDownloadAndDeleteAttachment() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "hello.txt", MediaType.TEXT_PLAIN_VALUE, "hello world".getBytes());

        MvcResult uploaded = mockMvc.perform(multipart("/api/tasks/1/attachments").file(file))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fileName").value("hello.txt"))
                .andExpect(jsonPath("$.size").value(11))
                .andReturn();

        Long id = Long.valueOf(
                com.jayway.jsonpath.JsonPath.read(uploaded.getResponse().getContentAsString(), "$.id").toString());

        mockMvc.perform(get("/api/tasks/1/attachments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(id));

        MvcResult downloaded = mockMvc.perform(get("/api/attachments/" + id))
                .andExpect(status().isOk())
                .andReturn();
        assertThat(downloaded.getResponse().getContentAsString()).isEqualTo("hello world");
        assertThat(downloaded.getResponse().getHeader("Content-Disposition")).contains("hello.txt");

        mockMvc.perform(delete("/api/attachments/" + id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/attachments/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void uploadForUnknownTaskReturnsNotFound() throws Exception {
        when(taskRepository.read(999L)).thenReturn(Optional.empty());
        MockMultipartFile file = new MockMultipartFile(
                "file", "x.txt", MediaType.TEXT_PLAIN_VALUE, "x".getBytes());

        mockMvc.perform(multipart("/api/tasks/999/attachments").file(file))
                .andExpect(status().isNotFound());
    }
}
