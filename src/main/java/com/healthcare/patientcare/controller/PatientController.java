package com.healthcare.patientcare.controller;

import com.healthcare.patientcare.entity.Patient;
import com.healthcare.patientcare.entity.ProgressNote;
import com.healthcare.patientcare.entity.CarePlan;
import com.healthcare.patientcare.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @GetMapping
    public ResponseEntity<List<Patient>> getAllPatients() {
        return ResponseEntity.ok(patientService.getAllPatients());
    }

    @GetMapping("/by-name/{name}")
    public ResponseEntity<Patient> getPatientByName(@PathVariable String name) {
        return patientService.getPatientByName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Patient> getPatientById(@PathVariable Long id) {
        return patientService.getPatientById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/progress-notes")
    public ResponseEntity<List<ProgressNote>> getProgressNotes(@PathVariable Long id) {
        List<ProgressNote> notes = patientService.getProgressNotesByPatientId(id);
        return ResponseEntity.ok(notes);
    }

    @GetMapping("/{id}/care-plan")
    public ResponseEntity<CarePlan> getCarePlan(@PathVariable Long id) {
        return patientService.getCarePlanByPatientId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/care-plan")
    public ResponseEntity<CarePlan> updateCarePlan(
            @PathVariable Long id,
            @RequestBody CarePlan carePlan) {
        try {
            CarePlan updated = patientService.updateCarePlan(id, carePlan);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Patient> createPatient(@RequestBody Patient patient) {
        Patient created = patientService.createPatient(patient);
        return ResponseEntity.ok(created);
    }

    @PostMapping("/{id}/progress-notes")
    public ResponseEntity<ProgressNote> addProgressNote(
            @PathVariable Long id,
            @RequestBody ProgressNote note) {
        try {
            ProgressNote created = patientService.addProgressNote(id, note);
            return ResponseEntity.ok(created);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}