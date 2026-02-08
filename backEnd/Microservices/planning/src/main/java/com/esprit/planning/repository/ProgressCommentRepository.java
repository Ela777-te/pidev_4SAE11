package com.esprit.planning.repository;

import com.esprit.planning.entity.ProgressComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProgressCommentRepository extends JpaRepository<ProgressComment, Long> {

    List<ProgressComment> findByProgressUpdate_Id(Long progressUpdateId);
}
