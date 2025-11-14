package com.healthcare.patientcare.config;

import com.healthcare.patientcare.entity.Patient;
import com.healthcare.patientcare.entity.ProgressNote;
import com.healthcare.patientcare.entity.CarePlan;
import com.healthcare.patientcare.entity.Appointment;
import com.healthcare.patientcare.repository.PatientRepository;
import com.healthcare.patientcare.repository.ProgressNoteRepository;
import com.healthcare.patientcare.repository.CarePlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private ProgressNoteRepository progressNoteRepository;

    @Autowired
    private CarePlanRepository carePlanRepository;

    @Autowired
    private com.healthcare.patientcare.repository.AppointmentRepository appointmentRepository;

    @Override
    public void run(String... args) throws Exception {
        // Create Patient 1
        Patient patient1 = new Patient();
        patient1.setName("John Smith");
        patient1.setPatientId("PAT001");
        patient1.setDateOfBirth(LocalDate.of(1980, 5, 15));
        patient1.setGender("Male");
        patient1.setContactNumber("555-0101");
        patient1.setEmail("john.smith@email.com");
        patient1.setAddress("123 Main St, Springfield");
        patient1 = patientRepository.save(patient1);

        // Progress Notes for Patient 1
        ProgressNote note1 = new ProgressNote();
        note1.setNote("Patient presented with mild hypertension. Blood pressure: 140/90.");
        note1.setDateTime(LocalDateTime.now().minusDays(7));
        note1.setProvider("Dr. Sarah Johnson");
        note1.setNoteType("Assessment");
        note1.setPatient(patient1);
        progressNoteRepository.save(note1);

        ProgressNote note2 = new ProgressNote();
        note2.setNote("Follow-up visit. Patient reports improved symptoms. BP: 130/85.");
        note2.setDateTime(LocalDateTime.now().minusDays(3));
        note2.setProvider("Dr. Sarah Johnson");
        note2.setNoteType("Follow-up");
        note2.setPatient(patient1);
        progressNoteRepository.save(note2);

        // Care Plan for Patient 1
        CarePlan carePlan1 = new CarePlan();
        carePlan1.setGoals("Reduce blood pressure to below 130/80 within 3 months");
        carePlan1.setInterventions("Diet modification: reduce sodium intake, increase exercise to 30 min/day");
        carePlan1.setMedications("Lisinopril 10mg once daily");
        carePlan1.setStartDate(LocalDate.now().minusDays(7));
        carePlan1.setReviewDate(LocalDate.now().plusMonths(3));
        carePlan1.setStatus("Active");
        carePlan1.setPatient(patient1);
        carePlanRepository.save(carePlan1);

        // Create Patient 2
        Patient patient2 = new Patient();
        patient2.setName("Mary Johnson");
        patient2.setPatientId("PAT002");
        patient2.setDateOfBirth(LocalDate.of(1975, 8, 22));
        patient2.setGender("Female");
        patient2.setContactNumber("555-0102");
        patient2.setEmail("mary.johnson@email.com");
        patient2.setAddress("456 Oak Ave, Springfield");
        patient2 = patientRepository.save(patient2);

        // Progress Notes for Patient 2
        ProgressNote note3 = new ProgressNote();
        note3.setNote("Initial consultation for Type 2 Diabetes management. HbA1c: 7.8%");
        note3.setDateTime(LocalDateTime.now().minusDays(14));
        note3.setProvider("Dr. Michael Chen");
        note3.setNoteType("Assessment");
        note3.setPatient(patient2);
        progressNoteRepository.save(note3);

        // Care Plan for Patient 2
        CarePlan carePlan2 = new CarePlan();
        carePlan2.setGoals("Achieve HbA1c below 7.0% within 6 months");
        carePlan2.setInterventions("Dietary counseling, regular glucose monitoring, exercise program");
        carePlan2.setMedications("Metformin 500mg twice daily");
        carePlan2.setStartDate(LocalDate.now().minusDays(14));
        carePlan2.setReviewDate(LocalDate.now().plusMonths(6));
        carePlan2.setStatus("Active");
        carePlan2.setPatient(patient2);
        carePlanRepository.save(carePlan2);

        // Sample Appointments for Patient 1
        Appointment appt1 = new Appointment();
        appt1.setAppointmentDate(LocalDateTime.now().plusDays(3).withHour(10).withMinute(0));
        appt1.setReason("Blood pressure follow-up");
        appt1.setProvider("Dr. Sarah Johnson");
        appt1.setStatus("Scheduled");
        appt1.setPatient(patient1);
        appointmentRepository.save(appt1);

        // Sample Appointment for Patient 2
        Appointment appt2 = new Appointment();
        appt2.setAppointmentDate(LocalDateTime.now().plusDays(7).withHour(9).withMinute(30));
        appt2.setReason("Diabetes management review");
        appt2.setProvider("Dr. Michael Chen");
        appt2.setStatus("Scheduled");
        appt2.setPatient(patient2);
        appointmentRepository.save(appt2);

        System.out.println("Sample data initialized successfully!");
    }
}