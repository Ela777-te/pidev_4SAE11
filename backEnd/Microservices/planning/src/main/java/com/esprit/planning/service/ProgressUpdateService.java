package com.esprit.planning.service;

import com.esprit.planning.entity.ProgressUpdate;
import com.esprit.planning.repository.ProgressUpdateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProgressUpdateService {

    private final ProgressUpdateRepository progressUpdateRepository;

    @Transactional(readOnly = true)
    public List<ProgressUpdate> findAll() {
        return progressUpdateRepository.findAll();
    }

    @Transactional(readOnly = true)
    public ProgressUpdate findById(Long id) {
        return progressUpdateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ProgressUpdate not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<ProgressUpdate> findByProjectId(Long projectId) {
        return progressUpdateRepository.findByProjectId(projectId);
    }

    @Transactional(readOnly = true)
    public List<ProgressUpdate> findByContractId(Long contractId) {
        return progressUpdateRepository.findByContractId(contractId);
    }

    @Transactional(readOnly = true)
    public List<ProgressUpdate> findByFreelancerId(Long freelancerId) {
        return progressUpdateRepository.findByFreelancerId(freelancerId);
    }

    @Transactional
    public ProgressUpdate create(ProgressUpdate progressUpdate) {
        return progressUpdateRepository.save(progressUpdate);
    }

    @Transactional
    public ProgressUpdate update(Long id, ProgressUpdate updated) {
        ProgressUpdate existing = findById(id);
        existing.setProjectId(updated.getProjectId());
        existing.setContractId(updated.getContractId());
        existing.setFreelancerId(updated.getFreelancerId());
        existing.setTitle(updated.getTitle());
        existing.setDescription(updated.getDescription());
        existing.setProgressPercentage(updated.getProgressPercentage());
        return progressUpdateRepository.save(existing);
    }

    @Transactional
    public void deleteById(Long id) {
        if (!progressUpdateRepository.existsById(id)) {
            throw new RuntimeException("ProgressUpdate not found with id: " + id);
        }
        progressUpdateRepository.deleteById(id);
    }
}
