package edu.stevens.cs548.clinic.domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

import edu.stevens.cs548.clinic.util.DateUtils;

import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Entity implementation class for Entity: DrugTreatment
 * 
 */
// TODO JPA annotations
@Entity
public class DrugTreatment extends Treatment implements Serializable {

	private static final long serialVersionUID = 1L;

	private String drug;
	
	private float dosage;

    // TODO
	@Temporal(TemporalType.DATE)
	private Date startDate;

    // TODO
	@Temporal(TemporalType.DATE)
	private Date endDate;
	
	private int frequency;

	public String getDrug() {
		return drug;
	}

	public void setDrug(String drug) {
		this.drug = drug;
	}

	public float getDosage() {
		return dosage;
	}

	public void setDosage(float dosage) {
		this.dosage = dosage;
	}

	public LocalDate getStartDate() {
		return DateUtils.fromDatabaseDate(startDate);
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = DateUtils.toDatabaseDate(startDate);
	}

	public LocalDate getEndDate() {
		return DateUtils.fromDatabaseDate(endDate);
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = DateUtils.toDatabaseDate(endDate);
	}

	public int getFrequency() {
		return frequency;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}
	

	public <T> T export(ITreatmentExporter<T> visitor) {
		return visitor.exportDrugTreatment(treatmentId,
										   patient.getPatientId(),
										   patient.getName(),
										   provider.getProviderId(),
										   provider.getName(),
								   		   diagnosis,
								   		   drug, 
								   		   dosage,
								   		   DateUtils.fromDatabaseDate(startDate),
								   		   DateUtils.fromDatabaseDate(endDate),
								   		   frequency,
								   		   () -> exportFollowupTreatments(visitor));
	}

	public DrugTreatment() {
		super();
	}

}
