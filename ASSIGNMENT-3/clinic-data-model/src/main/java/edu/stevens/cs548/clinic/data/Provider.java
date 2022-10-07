package edu.stevens.cs548.clinic.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.*;

import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.PrimaryKey;

/**
 * Entity implementation class for Entity: Provider
 *
 */
@NamedQueries({
	@NamedQuery(
		name="SearchProviderByProviderId",
		query="select p from Provider p where p.providerId = :providerId"),
	@NamedQuery(
		name="CountProviderByProviderId",
		query="select count(p) from Provider p where p.providerId = :providerId"),
	@NamedQuery(
		name = "RemoveAllProviders", 
		query = "delete from Provider p")
})

@Entity
@Table(name = "PROVIDER", indexes = @Index(columnList = "PROVIDER_ID"))
public class Provider implements Serializable {
		
	private static final long serialVersionUID = -876909316791083094L;

	@Id
	@GeneratedValue
	@Column(name = "ID")
	private long id;

	@Convert("uuidConverter")
	@Column(name = "PROVIDER_ID", unique=true, nullable = false)
	private UUID providerId;


	@Column(name = "NPI")
	private String npi;

	@Column(name = "NAME")
	private String name;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public UUID getProviderId() {
		return providerId;
	}

	public void setProviderId(UUID providerId) {
		this.providerId = providerId;
	}

	public String getNpi() {
		return npi;
	}

	public void setNpi(String npi) {
		this.npi = npi;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "provider")
	private List<Treatment> treatments;

	public List<Treatment> getTreatments() {
		return treatments;
	}

	protected void setTreatments(List<Treatment> treatments) {
		this.treatments = treatments;
	}
	
	public void addTreatment(Treatment treatment) {
		this.treatments.add(treatment);
	}
	
	/*
	 * Addition and deletion of treatments should be done here.
	 */

	public Provider() {
		super();

		this.setTreatments(new ArrayList<>());
	}
	
	/*
	 * We will add aggregate methods later.
	 */
   
}
