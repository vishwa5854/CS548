package edu.stevens.cs548.clinic.data;

import java.io.Serializable;
import java.util.*;

import javax.persistence.*;

import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.Converter;

/**
 * Entity implementation class for Entity: Patient
 *
 */
@NamedQueries({
	@NamedQuery(
		name="SearchPatientByPatientId",
		query="select p from Patient p where p.patientId = :patientId"),
	@NamedQuery(
			name="CountPatientByPatientId",
			query="select count(p) from Patient p where p.patientId = :patientId"),
	@NamedQuery(
		name = "RemoveAllPatients", 
		query = "delete from Patient p")
})

@Entity
@Table(name = "PATIENT", indexes = @Index(columnList = "PATIENT_ID"))
@Converter(name="uuidConverter", converterClass=UUIDConverter.class)
public class Patient implements Serializable {
		
	private static final long serialVersionUID = -4512912599605407549L;

	@Id
	@GeneratedValue
	@Column(name = "ID")
	private long id;


	@Convert("uuidConverter")
	@Column(name = "PATIENT_ID", unique=true, nullable = false)
	private UUID patientId;

	@Column(name = "NAME")
	private String name;

	@Column(name = "DOB")
	@Temporal(TemporalType.DATE)
	private Date dob;

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getDob() {
		return dob;
	}

	public void setDob(Date dob) {
		this.dob = dob;
	}

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "patient")
	private Collection<Treatment> treatments;

	public Collection<Treatment> getTreatments() {
		return treatments;
	}

	protected void setTreatments(Collection<Treatment> treatments) {
		this.treatments = treatments;
	}
	
	public void addTreatment(Treatment treatment) {
		this.treatments.add(treatment);
	}
	
	/*
	 * Addition and deletion of treatments should be done in the provider aggregate.
	 */

	public Patient() {
		super();

		this.setTreatments(new ArrayList<>());
	}
   
}
