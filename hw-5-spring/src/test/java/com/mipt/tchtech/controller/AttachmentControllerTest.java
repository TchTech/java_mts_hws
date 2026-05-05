package com.mipt.tchtech.controller;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;

import com.mipt.tchtech.exception.GlobalExceptionHandler;
import com.mipt.tchtech.exception.TaskNotFoundException;
import com.mipt.tchtech.model.TaskAttachment;
import com.mipt.tchtech.service.AttachmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AttachmentController.class)
@Import(GlobalExceptionHandler.class)
class AttachmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AttachmentService attachmentService;

    @Test
    void uploadAttachment_returns201AndDto() throws Exception {
        TaskAttachment att = new TaskAttachment();
        att.setId(1L);
        att.setFileName("doc.txt");
        att.setStoredFileName("uuid_doc.txt");
        att.setContentType("text/plain");
        att.setSize(11L);
        att.setUploadedAt(LocalDateTime.now());
        given(attachmentService.storeAttachment(eq(7L), any())).willReturn(att);

        MockMultipartFile file = new MockMultipartFile(
                "file", "doc.txt", "text/plain", "hello world".getBytes());

        mockMvc.perform(multipart("/api/tasks/{taskId}/attachments", 7L).file(file))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.fileName").value("doc.txt"))
                .andExpect(jsonPath("$.size").value(11));
    }

    @Test
    void uploadAttachment_taskNotFound_returns404() throws Exception {
        given(attachmentService.storeAttachment(eq(99L), any()))
                .willThrow(new TaskNotFoundException(99L));

        MockMultipartFile file = new MockMultipartFile(
                "file", "x.txt", "text/plain", "x".getBytes());

        mockMvc.perform(multipart("/api/tasks/{taskId}/attachments", 99L).file(file))
                .andExpect(status().isNotFound());
    }

    @Test
    void downloadAttachment_returnsFileWithCorrectHeaders() throws Exception {
        TaskAttachment att = new TaskAttachment();
        att.setId(5L);
        att.setFileName("data.csv");
        att.setStoredFileName("uuid_data.csv");
        att.setContentType("text/csv");
        att.setSize(3L);

        InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream("a,b".getBytes()));
        given(attachmentService.loadAsResource(5L)).willReturn(resource);
        given(attachmentService.getAttachment(5L)).willReturn(att);

        mockMvc.perform(get("/api/attachments/{id}", 5L))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.parseMediaType("text/csv")))
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"data.csv\""));
    }

    @Test
    void deleteAttachment_returns204() throws Exception {
        mockMvc.perform(delete("/api/attachments/{id}", 1L))
                .andExpect(status().isNoContent());
    }
}
