package edu.stevens.cs548.clinic.data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Entity implementation class for Entity: DrugTreatment
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class DrugTreatment extends Treatment implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "DRUG")
	private String drug;

	@Column(name = "DOSAGE")
	private float dosage;
	
	@Column(name = "START_DATE")
	@Temporal(TemporalType.DATE)
	private Date startDate;

	@Column(name = "END_DATE")
	@Temporal(TemporalType.DATE)
	private Date endDate;

	@Column(name = "FREQUENCY")
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

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public int getFrequency() {
		return frequency;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public DrugTreatment() {
		super();
	}

}
