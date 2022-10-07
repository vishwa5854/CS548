package edu.stevens.cs548.clinic.service.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.time.LocalDate;
import java.util.UUID;

public class RadiologyTreatmentDto extends TreatmentDto {
    public RadiologyTreatmentDto() {
        super(TreatmentType.RADIOLOGY);
    }

    @SerializedName("radiology-treatments-dates")
    private List<LocalDate> radiologyTreatmentDates;

    @SerializedName("follow-up-treatments")
    private List<UUID> followUpTreatments;

    public List<LocalDate> getRadiologyTreatmentDates() {
        return radiologyTreatmentDates;
    }

    public void setRadiologyTreatmentDates(List<LocalDate> radiologyTreatmentDates) {
        this.radiologyTreatmentDates = radiologyTreatmentDates;
    }

    public List<UUID> getFollowUpTreatments() {
        return followUpTreatments;
    }

    public void setFollowUpTreatments(List<UUID> followUpTreatments) {
        this.followUpTreatments = followUpTreatments;
    }
}
