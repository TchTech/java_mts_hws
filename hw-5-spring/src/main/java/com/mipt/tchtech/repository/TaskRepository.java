package com.mipt.tchtech.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mipt.tchtech.model.Priority;
import com.mipt.tchtech.model.TaskEntity;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Long> {

    List<TaskEntity> findByCompletedAndPriority(boolean completed, Priority priority);

    List<TaskEntity> findByCompleted(boolean completed);

    @Query("SELECT t FROM TaskEntity t WHERE t.dueDate IS NOT NULL "
            + "AND t.dueDate BETWEEN :from AND :to ORDER BY t.dueDate ASC")
    List<TaskEntity> findUpcoming(@Param("from") LocalDate from, @Param("to") LocalDate to);

    @EntityGraph(attributePaths = {"attachments"})
    @Query("SELECT DISTINCT t FROM TaskEntity t")
    List<TaskEntity> findAllWithAttachments();

    @EntityGraph(attributePaths = {"attachments"})
    Optional<TaskEntity> findWithAttachmentsById(Long id);
}