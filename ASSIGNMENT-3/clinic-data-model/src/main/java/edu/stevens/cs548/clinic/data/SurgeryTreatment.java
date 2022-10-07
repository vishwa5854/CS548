package edu.stevens.cs548.clinic.data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class SurgeryTreatment extends Treatment {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4173146640306267418L;

	@Column(name = "SURGERY_DATE")
	@Temporal(TemporalType.DATE)
	private Date surgeryDate;

	@Column(name = "DISCHARGE_INSTRUCTIONS")
	private String dischargeInstructions;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(
			name = "SURGERYTREATMENT_TREATMENT",
			joinColumns = @JoinColumn(name = "SURGERYTREATMENT_ID"),
			inverseJoinColumns = @JoinColumn(name = "TREATMENT_ID")
	)
	private Collection<Treatment> followupTreatments;

	public Date getSurgeryDate() {
		return surgeryDate;
	}

	public void setSurgeryDate(Date surgeryDate) {
		this.surgeryDate = surgeryDate;
	}

	public String getDischargeInstructions() {
		return dischargeInstructions;
	}

	public void setDischargeInstructions(String dischargeInstructions) {
		this.dischargeInstructions = dischargeInstructions;
	}

	public Collection<Treatment> getFollowupTreatments() {
		return followupTreatments;
	}

	public void setFollowupTreatments(Collection<Treatment> followupTreatments) {
		this.followupTreatments = followupTreatments;
	}
	
	public SurgeryTreatment() {
		this.setFollowupTreatments(new ArrayList<>());
	}

}
