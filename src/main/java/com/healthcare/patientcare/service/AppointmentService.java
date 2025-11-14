package com.healthcare.patientcare.service;

import com.healthcare.patientcare.entity.Appointment;
import com.healthcare.patientcare.entity.Patient;
import com.healthcare.patientcare.repository.AppointmentRepository;
import com.healthcare.patientcare.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PatientRepository patientRepository;

    public Optional<Appointment> getAppointmentById(Long id) {
        return appointmentRepository.findById(id);
    }

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public List<Appointment> getAppointmentsByPatientId(Long patientId) {
        return appointmentRepository.findByPatientId(patientId);
    }

    public List<Appointment> getAppointmentsBetween(LocalDateTime start, LocalDateTime end) {
        return appointmentRepository.findByAppointmentDateBetween(start, end);
    }

    public Appointment createAppointment(Long patientId, Appointment appointment) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        appointment.setPatient(patient);
        if (appointment.getStatus() == null) {
            appointment.setStatus("Scheduled");
        }
        return appointmentRepository.save(appointment);
    }

    public Appointment updateAppointment(Long id, Appointment appointmentRequest) {
        Appointment existing = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        existing.setAppointmentDate(appointmentRequest.getAppointmentDate());
        existing.setReason(appointmentRequest.getReason());
        existing.setStatus(appointmentRequest.getStatus());
        existing.setProvider(appointmentRequest.getProvider());
        return appointmentRepository.save(existing);
    }

    public Appointment cancelAppointment(Long id) {
        Appointment existing = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        existing.setStatus("Cancelled");
        return appointmentRepository.save(existing);
    }

    public void deleteAppointment(Long id) {
        appointmentRepository.deleteById(id);
    }
}
