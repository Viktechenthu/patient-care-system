package com.healthcare.patientcare.service;

import com.healthcare.patientcare.entity.Patient;
import com.healthcare.patientcare.entity.ProgressNote;
import com.healthcare.patientcare.entity.CarePlan;
import com.healthcare.patientcare.repository.PatientRepository;
import com.healthcare.patientcare.repository.ProgressNoteRepository;
import com.healthcare.patientcare.repository.CarePlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private ProgressNoteRepository progressNoteRepository;

    @Autowired
    private CarePlanRepository carePlanRepository;

    public Optional<Patient> getPatientByName(String name) {
        return patientRepository.findByName(name);
    }

    public Optional<Patient> getPatientById(Long id) {
        return patientRepository.findById(id);
    }

    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    public List<ProgressNote> getProgressNotesByPatientId(Long patientId) {
        return progressNoteRepository.findByPatientId(patientId);
    }

    public Optional<CarePlan> getCarePlanByPatientId(Long patientId) {
        return carePlanRepository.findByPatientId(patientId);
    }

    public CarePlan updateCarePlan(Long patientId, CarePlan carePlanRequest) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        CarePlan carePlan = carePlanRepository.findByPatientId(patientId)
                .orElse(new CarePlan());

        carePlan.setGoals(carePlanRequest.getGoals());
        carePlan.setInterventions(carePlanRequest.getInterventions());
        carePlan.setMedications(carePlanRequest.getMedications());
        carePlan.setStartDate(carePlanRequest.getStartDate());
        carePlan.setReviewDate(carePlanRequest.getReviewDate());
        carePlan.setStatus(carePlanRequest.getStatus());
        carePlan.setPatient(patient);

        return carePlanRepository.save(carePlan);
    }

    public Patient createPatient(Patient patient) {
        return patientRepository.save(patient);
    }

    public ProgressNote addProgressNote(Long patientId, ProgressNote note) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        note.setPatient(patient);
        return progressNoteRepository.save(note);
    }
}