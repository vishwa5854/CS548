package edu.stevens.cs548.clinic.research;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import javax.persistence.Index;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Convert;

/**
 * Entity implementation class for Entity: Subject
 */
// TODO

@Table(indexes = @Index(columnList="patientId"))
public class Subject implements Serializable {

	
	private static final long serialVersionUID = 1L;

	// TODO
	private long id;
	
	/*
	 * This will be same as patient id in Clinic database
	 */
	// TODO

	@Convert("uuidConverter")
	private UUID patientId;
		
	/*
	 * Anonymize patient (randomly generated when assigned)
	 */
	private long subjectId;
	
	// TODO
	private Collection<DrugTreatmentRecord> treatments;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public UUID getPatientId() {
		return patientId;
	}

	public void setPatientId(UUID patientId) {
		this.patientId = patientId;
	}

	public long getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(long subjectId) {
		this.subjectId = subjectId;
	}

	public Collection<DrugTreatmentRecord> getTreatments() {
		return treatments;
	}
	
	public void addTreatment(DrugTreatmentRecord treatment) {
		this.treatments.add(treatment);
	}
	
	public Subject() {
		treatments = new ArrayList<>();
	}
	
   
}
