package com.mipt.tchtech.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mipt.tchtech.exception.TaskNotFoundException;
import com.mipt.tchtech.model.TaskAttachment;
import com.mipt.tchtech.repository.TaskAttachmentRepository;
import com.mipt.tchtech.repository.TaskRepository;

@Service
public class AttachmentService {

    private final TaskAttachmentRepository attachmentRepository;
    private final TaskRepository taskRepository;
    private final Path uploadDir;

    public AttachmentService(TaskAttachmentRepository attachmentRepository,
                             TaskRepository taskRepository,
                             @Value("${app.upload-dir:uploads}") String uploadDir) {
        this.attachmentRepository = attachmentRepository;
        this.taskRepository = taskRepository;
        this.uploadDir = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadDir);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось создать директорию для загрузок", e);
        }
    }

    public TaskAttachment storeAttachment(String taskId, MultipartFile file) throws IOException {
        taskRepository.findById(taskId).orElseThrow(() -> new TaskNotFoundException(taskId));

        String storedFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path targetPath = uploadDir.resolve(storedFileName);

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
        }

        TaskAttachment attachment = new TaskAttachment();
        attachment.setTaskId(taskId);
        attachment.setFileName(file.getOriginalFilename());
        attachment.setStoredFileName(storedFileName);
        attachment.setContentType(file.getContentType());
        attachment.setSize(file.getSize());
        attachment.setUploadedAt(LocalDateTime.now());

        return attachmentRepository.save(attachment);
    }

    public TaskAttachment getAttachment(Long attachmentId) {
        return attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new RuntimeException("Вложение не найдено: " + attachmentId));
    }

    public Resource loadAsResource(Long attachmentId) {
        TaskAttachment attachment = getAttachment(attachmentId);
        try {
            Path filePath = uploadDir.resolve(attachment.getStoredFileName()).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            }
            throw new RuntimeException("Файл не найден: " + attachment.getStoredFileName());
        } catch (MalformedURLException e) {
            throw new RuntimeException("Ошибка при загрузке файла", e);
        }
    }

    public void deleteAttachment(Long attachmentId) throws IOException {
        TaskAttachment attachment = getAttachment(attachmentId);
        Path filePath = uploadDir.resolve(attachment.getStoredFileName()).normalize();
        Files.deleteIfExists(filePath);
        attachmentRepository.deleteById(attachmentId);
    }

    public List<TaskAttachment> getAttachmentsForTask(String taskId) {
        return attachmentRepository.findByTaskId(taskId);
    }
}
