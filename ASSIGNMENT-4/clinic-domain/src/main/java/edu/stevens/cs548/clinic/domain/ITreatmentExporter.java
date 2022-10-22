package edu.stevens.cs548.clinic.domain;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface ITreatmentExporter<T> {
	
	public T exportDrugTreatment (UUID tid,
								  UUID patientId,
								  UUID providerId,
								  String diagnosis,
							   	  String drug,
							   	  float dosage,
							   	  LocalDate start,
							   	  LocalDate end,
							   	  int frequency,
								  Collection<T> followups);
	
	public T exportRadiology (UUID tid,
			  				  UUID patientId,
			  				  UUID providerId,
							  String diagnosis,
							  List<LocalDate> dates,
							  Collection<T> followups);
	
	public T exportSurgery (UUID tid,
			  				UUID patientId,
			  				UUID providerId,
						 	String diagnosis,
			                LocalDate date,
							Collection<T> followups);

	
	public T exportPhysiotherapy (UUID tid,
			  					  UUID patientId,
			  					  UUID providerId,
			  					  String diagnosis,
			  					  List<LocalDate> dates,
								  Collection<T> followups);
}
