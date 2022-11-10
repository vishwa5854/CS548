package edu.stevens.cs548.clinic.domain;

import java.util.List;
import java.util.UUID;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

// TODO
@RequestScoped
public class TreatmentDao implements ITreatmentDao {

	// TODO
	@Inject
	@ClinicDomainProducer.ClinicDomain
	private EntityManager em;

	@Override
	public Treatment getTreatment(UUID id) throws TreatmentExn {
		/*
		 * Retrieve treatment using external key
		 */
		TypedQuery<Treatment> query = em.createNamedQuery("SearchTreatmentByTreatmentId", Treatment.class).setParameter("treatmentId",id);
		List<Treatment> treatments = query.getResultList();
		
		if (treatments.size() > 1) {
			throw new TreatmentExn("Duplicate treatment records: treatment id = " + id);
		} else if (treatments.size() < 1) {
			throw new TreatmentExn("Treatment not found: treatment id = " + id);
		} else {
			Treatment t = treatments.get(0);
			return t;
		}
	}

	@Override
	public void addTreatment(Treatment t) {
		em.persist(t);
	}
	
}
