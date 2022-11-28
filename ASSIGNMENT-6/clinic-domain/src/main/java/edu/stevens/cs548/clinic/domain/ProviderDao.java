package edu.stevens.cs548.clinic.domain;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import edu.stevens.cs548.clinic.domain.ClinicDomainProducer.ClinicDomain;

// TODO
@RequestScoped
public class ProviderDao implements IProviderDao {

	// TODO
	@Inject
	@ClinicDomain
	private EntityManager em;
	
	// TODO
	@Inject
	private ITreatmentDao treatmentDao;

	private Logger logger = Logger.getLogger(ProviderDao.class.getCanonicalName());

	@Override
	public void addProvider(Provider provider) throws ProviderExn {
		// Add to database, and initialize the provider aggregate with a treatment DAO.
		UUID pid = provider.getProviderId();
		Query query = em.createNamedQuery("CountProviderByProviderId").setParameter("providerId", pid);
		Long numExisting = (Long) query.getSingleResult();

		if (numExisting < 1) {
			provider.setTreatmentDao(this.treatmentDao);
			em.persist(provider);
		} else {
			throw new IProviderDao.ProviderExn("Insertion: Provider with provider id (" + pid + ") already exists.");
		}
	}

	@Override
	/*
	 * The boolean flag indicates if related treatments should be loaded eagerly.
	 */
	public Provider getProvider(UUID id, boolean includeTreatments) throws ProviderExn {
		/*
		 * TODO retrieve Provider using external key
		 */
		TypedQuery<Provider> query = em.createNamedQuery("SearchProviderByProviderId", Provider.class).setParameter("providerId",id);
		List<Provider> providers = query.getResultList();

		if (providers.size() > 1) {
			throw new IProviderDao.ProviderExn("Duplicate provider records: patient id = " + id);
		} else if (providers.size() < 1) {
			throw new IProviderDao.ProviderExn("Provider not found: patient id = " + id);
		} else {
			Provider p = providers.get(0);
			p.setTreatmentDao(this.treatmentDao);
			return p;
		}
	}
	
	@Override
	/*
	 * By default, we eagerly load related treatments with a provider record.
	 */
	public Provider getProvider(UUID id) throws ProviderExn {
		return getProvider(id, true);
	}
	
	@Override
	public List<Provider> getProviders() {
		/*
		 * TODO Return a list of all providers (remember to set treatmentDAO)
		 */
		TypedQuery<Provider> query = em.createNamedQuery("SearchAllProviders", Provider.class);
		List<Provider> providers = query.getResultList();

		for (Provider p : providers) {
			p.setTreatmentDao(treatmentDao);
		}

		return providers;
	}
	
	@Override
	public void deleteProviders() {
		Query update = em.createNamedQuery("RemoveAllTreatments");
		update.executeUpdate();
		update = em.createNamedQuery("RemoveAllProviders");
		update.executeUpdate();
	}

}
