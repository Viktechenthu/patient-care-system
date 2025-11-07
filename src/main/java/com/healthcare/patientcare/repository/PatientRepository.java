package com.healthcare.patientcare.repository;

import com.healthcare.patientcare.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByName(String name);
    Optional<Patient> findByPatientId(String patientId);
}