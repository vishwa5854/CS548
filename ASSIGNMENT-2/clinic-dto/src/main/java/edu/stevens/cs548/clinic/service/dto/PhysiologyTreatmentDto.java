package edu.stevens.cs548.clinic.service.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.time.LocalDate;

public class PhysiologyTreatmentDto extends TreatmentDto {
    public PhysiologyTreatmentDto() {
        super(TreatmentType.PHYSIOTHERAPY);
    }

    @SerializedName("physiotherapy-treatment-dates")
    private List<LocalDate> physiotherapyTreatmentDates;

    public List<LocalDate> getPhysiotherapyTreatmentDates() {
        return physiotherapyTreatmentDates;
    }

    public void setPhysiotherapyTreatmentDates(List<LocalDate> physiotherapyTreatmentDates) {
        this.physiotherapyTreatmentDates = physiotherapyTreatmentDates;
    }
}
