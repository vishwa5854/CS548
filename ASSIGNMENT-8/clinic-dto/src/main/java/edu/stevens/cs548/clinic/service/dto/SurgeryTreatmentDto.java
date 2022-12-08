package edu.stevens.cs548.clinic.service.dto;

import java.time.LocalDate;

import com.google.gson.annotations.SerializedName;

public class SurgeryTreatmentDto extends TreatmentDto {
	
	@SerializedName("surgery-date")
	private LocalDate surgeryDate;
	
	@SerializedName("discharge-instructions")
	private String dischargeInstructions;

	public LocalDate getSurgeryDate() {
		return surgeryDate;
	}

	public void setSurgeryDate(LocalDate surgeryDate) {
		this.surgeryDate = surgeryDate;
	}

	public String getDischargeInstructions() {
		return dischargeInstructions;
	}

	public void setDischargeInstructions(String dischargeInstructions) {
		this.dischargeInstructions = dischargeInstructions;
	}

}
