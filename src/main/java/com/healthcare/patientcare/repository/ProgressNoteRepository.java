package com.healthcare.patientcare.repository;

import com.healthcare.patientcare.entity.ProgressNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProgressNoteRepository extends JpaRepository<ProgressNote, Long> {
    List<ProgressNote> findByPatientId(Long patientId);
}