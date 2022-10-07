package edu.stevens.cs548.clinic.service.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.google.gson.annotations.SerializedName;

public class SurgeryTreatmentDto extends TreatmentDto {
    @SerializedName("date-of-surgery")
    private LocalDate dateOfSurgery;

    @SerializedName("discharge-instructions")
    private String dischargeInstructions;

    @SerializedName("follow-up-treatments")
    private List<UUID> followUpTreatments;

    public SurgeryTreatmentDto() {
        super(TreatmentType.SURGERY);
    }

    public SurgeryTreatmentDto(LocalDate dateOfSurgery, String dischargeInstructions, List<UUID> followUpTreatments) {
        super(TreatmentType.SURGERY);
        this.setDateOfSurgery(dateOfSurgery);
        this.setDischargeInstructions(dischargeInstructions);
        this.setFollowUpTreatments(followUpTreatments);
    }

    public String getDischargeInstructions() {
        return dischargeInstructions;
    }

    public void setDischargeInstructions(String dischargeInstructions) {
        this.dischargeInstructions = dischargeInstructions;
    }

    public List<UUID> getFollowUpTreatments() {
        return followUpTreatments;
    }

    public void setFollowUpTreatments(List<UUID> followUpTreatments) {
        this.followUpTreatments = followUpTreatments;
    }

    public LocalDate getDateOfSurgery() {
        return dateOfSurgery;
    }

    public void setDateOfSurgery(LocalDate dateOfSurgery) {
        this.dateOfSurgery = dateOfSurgery;
    }
}
