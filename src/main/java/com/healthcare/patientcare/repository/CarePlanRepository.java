package com.healthcare.patientcare.repository;

import com.healthcare.patientcare.entity.CarePlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CarePlanRepository extends JpaRepository<CarePlan, Long> {
    Optional<CarePlan> findByPatientId(Long patientId);
}