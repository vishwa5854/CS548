package edu.stevens.cs548.clinic.service.dto;

import java.time.LocalDate;
import java.util.UUID;

public class PatientDto {
	
	private UUID id;
		
	private String name;
	
	private LocalDate dob;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LocalDate getDob() {
		return dob;
	}

	public void setDob(LocalDate dob) {
		this.dob = dob;
	}

	
}
