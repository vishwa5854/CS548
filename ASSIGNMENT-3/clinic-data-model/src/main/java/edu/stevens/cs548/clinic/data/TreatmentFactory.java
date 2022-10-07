package edu.stevens.cs548.clinic.data;

public class TreatmentFactory {
	
	public DrugTreatment createDrugTreatment() {
		return new DrugTreatment();
	}

	public SurgeryTreatment createSurgeryTreatment() {
		return new SurgeryTreatment();
	}

	public RadiologyTreatment createRadiologyTreatment() { return new RadiologyTreatment(); }

	public PhysiotherapyTreatment createPhysiotherapyTreatment() { return new PhysiotherapyTreatment(); }

}
