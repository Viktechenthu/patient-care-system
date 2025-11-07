package com.healthcare.patientcare.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "care_plans")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarePlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 3000)
    private String goals;

    @Column(length = 3000)
    private String interventions;

    @Column(length = 3000)
    private String medications;

    private LocalDate startDate;
    private LocalDate reviewDate;

    private String status; // e.g., "Active", "Under Review", "Completed"

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    @JsonIgnore
    private Patient patient;
}