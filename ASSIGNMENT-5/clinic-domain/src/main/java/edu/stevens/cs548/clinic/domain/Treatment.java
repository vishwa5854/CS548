package edu.stevens.cs548.clinic.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import javax.persistence.*;

import org.eclipse.persistence.annotations.Convert;


/**
 * Entity implementation class for Entity: Treatment
 *
 */
@NamedQueries({
	@NamedQuery(
		name="SearchTreatmentByTreatmentId",
		query="select t from Treatment t where t.treatmentId = :treatmentId"),
	@NamedQuery(
			name="SearchTreatmentWithFollowupsByTreatmentId",
			query="select t from Treatment t left join fetch t.followupTreatments where t.treatmentId = :treatmentId"),
	@NamedQuery(
			name="CountTreatmentByTreatmentId",
			query="select count(t) from Treatment t where t.treatmentId = :treatmentId"),
	@NamedQuery(
		name = "RemoveAllTreatments", 
		query = "delete from Treatment t")
})

// TODO
@Entity
@Inheritance(strategy= InheritanceType.JOINED)
@Table(indexes = @Index(columnList="treatmentId"))
public abstract class Treatment implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	// TODO PK
	@Id
	@GeneratedValue
	protected long id;
	
	// TODO
	@Convert("uuidConverter")
	@Column(unique=true, nullable = false)
	protected UUID treatmentId;
	
	protected String diagnosis;
	
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public UUID getTreatmentId() {
		return treatmentId;
	}

	public void setTreatmentId(UUID treatmentId) {
		this.treatmentId = treatmentId;
	}

	public String getDiagnosis() {
		return diagnosis;
	}

	public void setDiagnosis(String diagnosis) {
		this.diagnosis = diagnosis;
	}

	/*
	 * TODO
	 */
	@ManyToOne(cascade = CascadeType.ALL)
	protected Patient patient;

	public Patient getPatient() {
		return patient;
	}

	
	void setPatient(Patient patient) {
		this.patient = patient;
		// More logic in the domain model.
	}

	/*
	 * TODO
	 */
	@ManyToOne(cascade = CascadeType.ALL)
	protected Provider provider;

	public Provider getProvider() {
		return provider;
	}	
	
	public void setProvider(Patient patient, Provider provider) {
		this.provider = provider;
		/*
		 * Make sure the provider also links back to this treatment.
		 */
		if (!provider.administers(this))
			provider.addTreatment(patient, this);
	}	
	
	/*
	 * TODO
	 */
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	protected Collection<Treatment> followupTreatments;
	
	public void addFollowupTreatment(Treatment t) {
		followupTreatments.add(t);
	}


	/*
	 * We use the visitor pattern to access a treatment.
	 */
	public abstract <T> T export(ITreatmentExporter<T> visitor);
	
	protected final <T> List<T> exportFollowupTreatments(ITreatmentExporter<T> visitor) {
		List<T> exports = new ArrayList<T>();
		for (Treatment t : followupTreatments) {
			exports.add(t.export(visitor));
		}
		return exports;
	}

	
	public Treatment() {
		super();
		/*
		 * TODO initialize lists
		 */
		this.followupTreatments = new ArrayList<>();
	}   
}
