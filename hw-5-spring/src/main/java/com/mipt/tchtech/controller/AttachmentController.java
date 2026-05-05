package com.mipt.tchtech.controller;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mipt.tchtech.dto.AttachmentResponseDto;
import com.mipt.tchtech.model.TaskAttachment;
import com.mipt.tchtech.service.AttachmentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Attachments", description = "Управление вложениями задач")
public class AttachmentController {

    private final AttachmentService attachmentService;

    public AttachmentController(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    @PostMapping("/api/tasks/{taskId}/attachments")
    @Operation(summary = "Загрузить файл для задачи")
    public ResponseEntity<AttachmentResponseDto> uploadAttachment(
            @PathVariable Long taskId,
            @RequestParam("file") MultipartFile file) throws IOException {
        TaskAttachment attachment = attachmentService.storeAttachment(taskId, file);
        return ResponseEntity.status(201).body(toDto(attachment));
    }

    @GetMapping("/api/attachments/{attachmentId}")
    @Operation(summary = "Скачать файл вложения")
    public ResponseEntity<Resource> downloadAttachment(@PathVariable Long attachmentId) {
        Resource resource = attachmentService.loadAsResource(attachmentId);
        TaskAttachment attachment = attachmentService.getAttachment(attachmentId);
        String contentType = attachment.getContentType() != null
                ? attachment.getContentType()
                : MediaType.APPLICATION_OCTET_STREAM_VALUE;
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + attachment.getFileName() + "\"")
                .body(resource);
    }

    @DeleteMapping("/api/attachments/{attachmentId}")
    @Operation(summary = "Удалить вложение")
    public ResponseEntity<Void> deleteAttachment(@PathVariable Long attachmentId) throws IOException {
        attachmentService.deleteAttachment(attachmentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/tasks/{taskId}/attachments")
    @Operation(summary = "Получить список вложений задачи")
    public ResponseEntity<List<AttachmentResponseDto>> getAttachments(@PathVariable Long taskId) {
        List<AttachmentResponseDto> dtos = attachmentService.getAttachmentsForTask(taskId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    private AttachmentResponseDto toDto(TaskAttachment attachment) {
        AttachmentResponseDto dto = new AttachmentResponseDto();
        dto.setId(attachment.getId());
        dto.setFileName(attachment.getFileName());
        dto.setSize(attachment.getSize());
        dto.setUploadedAt(attachment.getUploadedAt());
        return dto;
    }
}
