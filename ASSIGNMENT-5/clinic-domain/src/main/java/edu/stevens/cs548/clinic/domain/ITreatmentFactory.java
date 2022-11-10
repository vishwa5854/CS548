package edu.stevens.cs548.clinic.domain;


public interface ITreatmentFactory {
	
	public DrugTreatment createDrugTreatment ();

	public SurgeryTreatment createSurgeryTreatment();

	public PhysiotherapyTreatment createPhysiotherapyTreatment();

	public RadiologyTreatment createRadiologyTreatment();
	
	/*
	 * TODO add methods for Radiology, Surgery, Physiotherapy
	 */
}
