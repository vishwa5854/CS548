package edu.stevens.cs548.clinic.service.dto;

import java.util.UUID;

import com.google.gson.annotations.SerializedName;

public abstract class TreatmentDto {
	
	public static final String SURGERY_TAG = "SURGERY";
	
	public static final String DRUGTREATMENT_TAG = "DRUGTREATMENT";
	
	public static final String RADIOLOGY_TAG = "RADIOLOGY";
	
	public static final String PHYSIOTHERAPY_TAG = "PHYSIOTHERAPY";
	
	public enum TreatmentType {
		@SerializedName(SURGERY_TAG) SURGERY,
		@SerializedName(DRUGTREATMENT_TAG) DRUGTREATMENT,
		@SerializedName(RADIOLOGY_TAG) RADIOLOGY,
		@SerializedName(PHYSIOTHERAPY_TAG) PHYSIOTHERAPY;

		public static TreatmentType parse(String value) {
			if (SURGERY_TAG.equals(value)) {
				return TreatmentType.SURGERY;
			} else if (DRUGTREATMENT_TAG.equals(value)) {
				return TreatmentType.DRUGTREATMENT;
			} else if (RADIOLOGY_TAG.equals(value)) {
				return TreatmentType.RADIOLOGY;
			} else if (PHYSIOTHERAPY_TAG.equals(value)) {
				return TreatmentType.PHYSIOTHERAPY;
			} else {
				throw new IllegalStateException("Invalid treatment type tag: "+value);
			}
		}
		
		public static boolean isValid(String value) {
			return SURGERY_TAG.equals(value) || DRUGTREATMENT_TAG.equals(value) || RADIOLOGY_TAG.equals(value) || PHYSIOTHERAPY_TAG.equals(value);
		}
	}
	
	public static final String TYPE_TAG = "type-tag";
	
	@SerializedName(TYPE_TAG)
	private TreatmentType type;
	
	private UUID id;
	
	@SerializedName("patient-id")
	private UUID patientId;
	
	@SerializedName("provider-id")
	private UUID providerId;
	
	private String diagnosis;
	
	public TreatmentDto(TreatmentType type) {
		this.type = type;
	}

	public TreatmentType getType() {
		return type;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public UUID getPatientId() {
		return patientId;
	}

	public void setPatientId(UUID patientId) {
		this.patientId = patientId;
	}

	public UUID getProviderId() {
		return providerId;
	}

	public void setProviderId(UUID providerId) {
		this.providerId = providerId;
	}

	public String getDiagnosis() {
		return diagnosis;
	}

	public void setDiagnosis(String diagnosis) {
		this.diagnosis = diagnosis;
	}
	
	

}
