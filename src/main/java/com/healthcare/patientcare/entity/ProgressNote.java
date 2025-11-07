package com.healthcare.patientcare.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "progress_notes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgressNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 2000)
    private String note;

    @Column(nullable = false)
    private LocalDateTime dateTime;

    private String provider;
    private String noteType; // e.g., "Assessment", "Treatment", "Observation"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    @JsonIgnore
    private Patient patient;
}