package com.esprit.planning.repository;

import com.esprit.planning.entity.ProgressUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProgressUpdateRepository extends JpaRepository<ProgressUpdate, Long> {

    List<ProgressUpdate> findByProjectId(Long projectId);

    List<ProgressUpdate> findByContractId(Long contractId);

    List<ProgressUpdate> findByFreelancerId(Long freelancerId);
}
