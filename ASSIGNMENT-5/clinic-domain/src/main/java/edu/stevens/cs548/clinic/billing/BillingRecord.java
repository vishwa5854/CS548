package edu.stevens.cs548.clinic.billing;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.eclipse.persistence.annotations.Convert;

import edu.stevens.cs548.clinic.domain.Treatment;
import edu.stevens.cs548.clinic.util.DateUtils;

/**
 * Entity implementation class for Entity: BillingRecord
 *
 */
@NamedQuery(
		name="SearchBillingRecords",
		query="select b from BillingRecord b")

// TODO

@Table(indexes = @Index(columnList="treatmentId"))
public class BillingRecord implements Serializable {
	
	private static final long serialVersionUID = 1L;

	public BillingRecord() {
		super();
	}
	
	@Id @GeneratedValue(strategy = IDENTITY)
	private long id;
	
	// TODO

	private UUID treatmentId;
	
	private String description;
	
	// TODO
	private Date date;
	
	private float amount;
	
	// @PrimaryKeyJoinColumn
	@OneToOne
	private Treatment treatment;

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public LocalDate getDate() {
		return DateUtils.fromDatabaseDate(date);
	}

	public void setDate(LocalDate date) {
		this.date = DateUtils.toDatabaseDate(date);
	}

	public float getAmount() {
		return amount;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}

	public Treatment getTreatment() {
		return treatment;
	}

	public void setTreatment(Treatment treatment) {
		this.treatment = treatment;
	}
	
}
