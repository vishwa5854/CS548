package edu.stevens.cs548.clinic.data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class PhysiotherapyTreatment extends Treatment {

	private static final long serialVersionUID = 5602950140629148756L;

	@ElementCollection
	protected Collection<Date> treatmentDates;

	public Collection<Date> getTreatmentDates() {
		return treatmentDates;
	}

	public void setTreatmentDates(Collection<Date> treatmentDates) {
		this.treatmentDates = treatmentDates;
	}

	public PhysiotherapyTreatment() {
		this.treatmentDates = new ArrayList<>();
	}

}
