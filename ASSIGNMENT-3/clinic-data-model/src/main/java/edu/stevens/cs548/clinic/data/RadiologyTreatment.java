package edu.stevens.cs548.clinic.data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class RadiologyTreatment extends Treatment {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3656673416179492428L;
	@ElementCollection
	protected Collection<Date> treatmentDates;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(
			name = "RADIOLOGYTREATMENT_TREATMENT",
			joinColumns = @JoinColumn(name = "RADIOLOGYTREATMENT_ID"),
			inverseJoinColumns = @JoinColumn(name = "TREATMENT_ID")
	)
	protected Collection<Treatment> followupTreatments;

	public Collection<Date> getTreatmentDates() {
		return treatmentDates;
	}

	public void setTreatmentDates(Collection<Date> treatmentDates) {
		this.treatmentDates = treatmentDates;
	}

	public Collection<Treatment> getFollowupTreatments() {
		return followupTreatments;
	}

	public void setFollowupTreatments(Collection<Treatment> followupTreatments) {
		this.followupTreatments = followupTreatments;
	}
	
	public RadiologyTreatment() {
		this.setFollowupTreatments(new ArrayList<>());
		this.setTreatmentDates(new ArrayList<>());
	}
	
}
