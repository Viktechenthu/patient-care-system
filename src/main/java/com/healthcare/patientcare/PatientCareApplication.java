package com.healthcare.patientcare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PatientCareApplication {

	public static void main(String[] args) {
		SpringApplication.run(PatientCareApplication.class, args);
		System.out.println("Patient Care System is running on http://localhost:8080");
		System.out.println("H2 Console available at http://localhost:8080/h2-console");
	}
}