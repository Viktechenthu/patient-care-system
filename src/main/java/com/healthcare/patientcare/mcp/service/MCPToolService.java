package com.healthcare.patientcare.mcp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthcare.patientcare.entity.CarePlan;
import com.healthcare.patientcare.entity.Appointment;
import com.healthcare.patientcare.entity.Patient;
import com.healthcare.patientcare.entity.ProgressNote;
import com.healthcare.patientcare.mcp.annotation.Tool;
import com.healthcare.patientcare.mcp.annotation.ToolParam;
import com.healthcare.patientcare.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MCPToolService {

    @Autowired
    private PatientService patientService;

    @Autowired
    private com.healthcare.patientcare.service.AppointmentService appointmentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Tool(
            name = "get_patient_by_name",
            description = "Retrieve patient details by patient name"
    )
    public String getPatientByName(
            @ToolParam(name = "name", description = "Full name of the patient") String name
    ) {
        try {
            Optional<Patient> patient = patientService.getPatientByName(name);
            if (patient.isPresent()) {
                return objectMapper.writeValueAsString(patient.get());
            } else {
                return "{\"error\": \"Patient not found with name: " + name + "\"}";
            }
        } catch (Exception e) {
            return "{\"error\": \"" + e.getMessage() + "\"}";
        }
    }

    @Tool(
            name = "get_patient_by_id",
            description = "Retrieve patient details by patient ID"
    )
    public String getPatientById(
            @ToolParam(name = "patient_id", description = "Numeric ID of the patient") Long patientId
    ) {
        try {
            Optional<Patient> patient = patientService.getPatientById(patientId);
            if (patient.isPresent()) {
                return objectMapper.writeValueAsString(patient.get());
            } else {
                return "{\"error\": \"Patient not found with ID: " + patientId + "\"}";
            }
        } catch (Exception e) {
            return "{\"error\": \"" + e.getMessage() + "\"}";
        }
    }

    @Tool(
            name = "get_all_patients",
            description = "List all patients in the system"
    )
    public String getAllPatients() {
        try {
            List<Patient> patients = patientService.getAllPatients();
            return objectMapper.writeValueAsString(patients);
        } catch (Exception e) {
            return "{\"error\": \"" + e.getMessage() + "\"}";
        }
    }

    @Tool(
            name = "get_progress_notes",
            description = "Get all progress notes for a specific patient"
    )
    public String getProgressNotes(
            @ToolParam(name = "patient_id", description = "Numeric ID of the patient") Long patientId
    ) {
        try {
            List<ProgressNote> notes = patientService.getProgressNotesByPatientId(patientId);
            return objectMapper.writeValueAsString(notes);
        } catch (Exception e) {
            return "{\"error\": \"" + e.getMessage() + "\"}";
        }
    }

    @Tool(
            name = "get_care_plan",
            description = "Get the care plan for a specific patient"
    )
    public String getCarePlan(
            @ToolParam(name = "patient_id", description = "Numeric ID of the patient") Long patientId
    ) {
        try {
            Optional<CarePlan> carePlan = patientService.getCarePlanByPatientId(patientId);
            if (carePlan.isPresent()) {
                return objectMapper.writeValueAsString(carePlan.get());
            } else {
                return "{\"error\": \"Care plan not found for patient ID: " + patientId + "\"}";
            }
        } catch (Exception e) {
            return "{\"error\": \"" + e.getMessage() + "\"}";
        }
    }

    @Tool(
            name = "update_care_plan",
            description = "Update or create a care plan for a patient"
    )
    public String updateCarePlan(
            @ToolParam(name = "patient_id", description = "Numeric ID of the patient") Long patientId,
            @ToolParam(name = "goals", description = "Health goals for the patient") String goals,
            @ToolParam(name = "interventions", description = "Planned interventions") String interventions,
            @ToolParam(name = "medications", description = "Prescribed medications") String medications,
            @ToolParam(name = "status", description = "Status of the care plan (e.g., Active, Under Review)") String status
    ) {
        try {
            CarePlan carePlanRequest = new CarePlan();
            carePlanRequest.setGoals(goals);
            carePlanRequest.setInterventions(interventions);
            carePlanRequest.setMedications(medications);
            carePlanRequest.setStatus(status);
            carePlanRequest.setStartDate(LocalDate.now());
            carePlanRequest.setReviewDate(LocalDate.now().plusMonths(3));

            CarePlan updated = patientService.updateCarePlan(patientId, carePlanRequest);
            return objectMapper.writeValueAsString(updated);
        } catch (Exception e) {
            return "{\"error\": \"" + e.getMessage() + "\"}";
        }
    }

    @Tool(
            name = "add_progress_note",
            description = "Add a new progress note for a patient"
    )
    public String addProgressNote(
            @ToolParam(name = "patient_id", description = "Numeric ID of the patient") Long patientId,
            @ToolParam(name = "note", description = "Content of the progress note") String note,
            @ToolParam(name = "provider", description = "Name of the healthcare provider") String provider,
            @ToolParam(name = "note_type", description = "Type of note (e.g., Assessment, Treatment)") String noteType
    ) {
        try {
            ProgressNote progressNote = new ProgressNote();
            progressNote.setNote(note);
            progressNote.setProvider(provider);
            progressNote.setNoteType(noteType);
            progressNote.setDateTime(LocalDateTime.now());

            ProgressNote created = patientService.addProgressNote(patientId, progressNote);
            return objectMapper.writeValueAsString(created);
        } catch (Exception e) {
            return "{\"error\": \"" + e.getMessage() + "\"}";
        }
    }

    @Tool(
            name = "create_patient",
            description = "Create a new patient record"
    )
    public String createPatient(
            @ToolParam(name = "name", description = "Full name of the patient") String name,
            @ToolParam(name = "patient_id", description = "Unique patient identifier") String patientId,
            @ToolParam(name = "date_of_birth", description = "Date of birth (YYYY-MM-DD)") String dateOfBirth,
            @ToolParam(name = "gender", description = "Gender of the patient") String gender,
            @ToolParam(name = "contact_number", description = "Contact phone number") String contactNumber,
            @ToolParam(name = "email", description = "Email address") String email,
            @ToolParam(name = "address", description = "Residential address") String address
    ) {
        try {
            Patient patient = new Patient();
            patient.setName(name);
            patient.setPatientId(patientId);
            patient.setDateOfBirth(LocalDate.parse(dateOfBirth));
            patient.setGender(gender);
            patient.setContactNumber(contactNumber);
            patient.setEmail(email);
            patient.setAddress(address);

            Patient created = patientService.createPatient(patient);
            return objectMapper.writeValueAsString(created);
        } catch (Exception e) {
            return "{\"error\": \"" + e.getMessage() + "\"}";
        }
    }

    @Tool(
            name = "schedule_appointment",
            description = "Schedule an appointment for a patient"
    )
    public String scheduleAppointment(
            @ToolParam(name = "patient_id", description = "Numeric ID of the patient") Long patientId,
            @ToolParam(name = "appointment_date", description = "Appointment date and time (ISO-8601)") String appointmentDate,
            @ToolParam(name = "reason", description = "Reason for visit") String reason,
            @ToolParam(name = "provider", description = "Provider name") String provider
    ) {
        try {
            Appointment appt = new Appointment();
            appt.setAppointmentDate(LocalDateTime.parse(appointmentDate));
            appt.setReason(reason);
            appt.setProvider(provider);
            appt.setStatus("Scheduled");

            Appointment created = appointmentService.createAppointment(patientId, appt);
            return objectMapper.writeValueAsString(created);
        } catch (Exception e) {
            return "{\"error\": \"" + e.getMessage() + "\"}";
        }
    }

    @Tool(
            name = "get_appointments",
            description = "Get appointments for a patient"
    )
    public String getAppointments(
            @ToolParam(name = "patient_id", description = "Numeric ID of the patient") Long patientId
    ) {
        try {
            List<Appointment> appts = appointmentService.getAppointmentsByPatientId(patientId);
            return objectMapper.writeValueAsString(appts);
        } catch (Exception e) {
            return "{\"error\": \"" + e.getMessage() + "\"}";
        }
    }

    @Tool(
            name = "cancel_appointment",
            description = "Cancel an appointment by appointment ID"
    )
    public String cancelAppointment(
            @ToolParam(name = "appointment_id", description = "Numeric ID of the appointment") Long appointmentId
    ) {
        try {
            Appointment cancelled = appointmentService.cancelAppointment(appointmentId);
            return objectMapper.writeValueAsString(cancelled);
        } catch (Exception e) {
            return "{\"error\": \"" + e.getMessage() + "\"}";
        }
    }
}