package com.mipt.angelikaliber.controller;

import com.mipt.angelikaliber.model.Priority;
import com.mipt.angelikaliber.model.Task;
import com.mipt.angelikaliber.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@SpringBootTest(webEnvironment = MOCK)
@ActiveProfiles("test")
class AttachmentControllerTest {

    @Autowired
    private org.springframework.web.context.WebApplicationContext context;

    @Autowired
    private TaskRepository taskRepository;

    private MockMvc mockMvc;
    private Long taskId;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
        Task task = new Task(null, "T", "D", false);
        task.setPriority(Priority.LOW);
        taskId = taskRepository.save(task).getId();
        mockMvc = webAppContextSetup(context).build();
    }

    @Test
    void uploadDownloadAndDeleteAttachment() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "hello.txt", MediaType.TEXT_PLAIN_VALUE, "hello world".getBytes());

        MvcResult uploaded = mockMvc.perform(multipart("/api/tasks/" + taskId + "/attachments").file(file))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fileName").value("hello.txt"))
                .andExpect(jsonPath("$.size").value(11))
                .andReturn();

        Long id = Long.valueOf(
                com.jayway.jsonpath.JsonPath.read(uploaded.getResponse().getContentAsString(), "$.id").toString());

        mockMvc.perform(get("/api/tasks/" + taskId + "/attachments"))
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
        MockMultipartFile file = new MockMultipartFile(
                "file", "x.txt", MediaType.TEXT_PLAIN_VALUE, "x".getBytes());

        mockMvc.perform(multipart("/api/tasks/999999/attachments").file(file))
                .andExpect(status().isNotFound());
    }
}
