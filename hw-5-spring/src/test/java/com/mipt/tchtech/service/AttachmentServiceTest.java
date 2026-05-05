package com.mipt.tchtech.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import com.mipt.tchtech.exception.TaskNotFoundException;
import com.mipt.tchtech.model.TaskAttachment;
import com.mipt.tchtech.model.TaskEntity;
import com.mipt.tchtech.repository.TaskAttachmentRepository;
import com.mipt.tchtech.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AttachmentServiceTest {

    @Mock
    private TaskAttachmentRepository attachmentRepository;

    @Mock
    private TaskRepository taskRepository;

    private AttachmentService newService(Path uploadDir) {
        return new AttachmentService(attachmentRepository, taskRepository, uploadDir.toString());
    }

    @Test
    void storeAttachment_withExistingTask_savesFileAndAttachment(@TempDir Path uploadDir) throws IOException {
        AttachmentService service = newService(uploadDir);
        TaskEntity task = new TaskEntity("t", "d", false);
        task.setId(1L);
        given(taskRepository.findById(1L)).willReturn(Optional.of(task));
        given(attachmentRepository.save(any(TaskAttachment.class)))
                .willAnswer(inv -> {
                    TaskAttachment a = inv.getArgument(0);
                    a.setId(100L);
                    return a;
                });

        MockMultipartFile file = new MockMultipartFile(
                "file", "report.txt", "text/plain", "hello".getBytes());

        TaskAttachment saved = service.storeAttachment(1L, file);

        assertThat(saved.getId()).isEqualTo(100L);
        assertThat(saved.getFileName()).isEqualTo("report.txt");
        assertThat(saved.getContentType()).isEqualTo("text/plain");
        assertThat(saved.getSize()).isEqualTo(5L);
        assertThat(saved.getStoredFileName()).endsWith("_report.txt");
        assertThat(uploadDir.resolve(saved.getStoredFileName())).exists();
        assertThat(Files.readString(uploadDir.resolve(saved.getStoredFileName())))
                .isEqualTo("hello");

        ArgumentCaptor<TaskAttachment> captor = ArgumentCaptor.forClass(TaskAttachment.class);
        verify(attachmentRepository).save(captor.capture());
        assertThat(captor.getValue().getTask()).isSameAs(task);
    }

    @Test
    void storeAttachment_taskNotFound_throwsTaskNotFoundException(@TempDir Path uploadDir) {
        AttachmentService service = newService(uploadDir);
        given(taskRepository.findById(99L)).willReturn(Optional.empty());

        MockMultipartFile file = new MockMultipartFile(
                "file", "f.txt", "text/plain", "x".getBytes());

        assertThatThrownBy(() -> service.storeAttachment(99L, file))
                .isInstanceOf(TaskNotFoundException.class);
    }

    @Test
    void deleteAttachment_removesFileAndDbRow(@TempDir Path uploadDir) throws IOException {
        AttachmentService service = newService(uploadDir);

        Path stored = uploadDir.resolve("xyz_doc.pdf");
        Files.writeString(stored, "binary");

        TaskAttachment att = new TaskAttachment();
        att.setId(7L);
        att.setStoredFileName("xyz_doc.pdf");
        att.setFileName("doc.pdf");
        given(attachmentRepository.findById(7L)).willReturn(Optional.of(att));

        service.deleteAttachment(7L);

        assertThat(stored).doesNotExist();
        verify(attachmentRepository, times(1)).deleteById(7L);
    }

    @Test
    void loadAsResource_existingFile_returnsResource(@TempDir Path uploadDir) throws IOException {
        AttachmentService service = newService(uploadDir);

        Path stored = uploadDir.resolve("abc_a.txt");
        Files.writeString(stored, "hi");

        TaskAttachment att = new TaskAttachment();
        att.setId(3L);
        att.setStoredFileName("abc_a.txt");
        att.setFileName("a.txt");
        given(attachmentRepository.findById(3L)).willReturn(Optional.of(att));

        Resource resource = service.loadAsResource(3L);

        assertThat(resource.exists()).isTrue();
        assertThat(resource.getFilename()).isEqualTo("abc_a.txt");
    }

    @Test
    void getAttachmentsForTask_delegatesToRepository(@TempDir Path uploadDir) {
        AttachmentService service = newService(uploadDir);
        TaskAttachment a1 = new TaskAttachment();
        a1.setId(1L);
        TaskAttachment a2 = new TaskAttachment();
        a2.setId(2L);
        given(attachmentRepository.findByTask_Id(5L)).willReturn(List.of(a1, a2));

        List<TaskAttachment> result = service.getAttachmentsForTask(5L);

        assertThat(result).hasSize(2);
        verify(attachmentRepository, times(1)).findByTask_Id(5L);
    }
}
