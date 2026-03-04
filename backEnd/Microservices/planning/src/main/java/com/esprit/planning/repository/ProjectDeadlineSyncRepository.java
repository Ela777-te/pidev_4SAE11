package com.esprit.planning.repository;

import com.esprit.planning.entity.ProjectDeadlineSync;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/** JPA repository for ProjectDeadlineSync entity. Tracks which project deadlines have been synced to the calendar. */
public interface ProjectDeadlineSyncRepository extends JpaRepository<ProjectDeadlineSync, Long> {

    Optional<ProjectDeadlineSync> findByProjectId(Long projectId);
}
