package edu.stevens.cs548.clinic.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.logging.Logger;

import edu.stevens.cs548.clinic.domain.IPatientDao;
import edu.stevens.cs548.clinic.domain.IPatientDao.PatientExn;
import edu.stevens.cs548.clinic.domain.IProviderDao;
import edu.stevens.cs548.clinic.domain.IProviderDao.ProviderExn;
import edu.stevens.cs548.clinic.domain.IProviderFactory;
import edu.stevens.cs548.clinic.domain.ITreatmentDao.TreatmentExn;
import edu.stevens.cs548.clinic.domain.Patient;
import edu.stevens.cs548.clinic.domain.Provider;
import edu.stevens.cs548.clinic.domain.ProviderFactory;
import edu.stevens.cs548.clinic.domain.Treatment;
import edu.stevens.cs548.clinic.service.IPatientService.PatientNotFoundExn;
import edu.stevens.cs548.clinic.service.IPatientService.PatientServiceExn;
import edu.stevens.cs548.clinic.service.IProviderService;
import edu.stevens.cs548.clinic.service.dto.*;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 * CDI Bean implementation class ProviderService
 */
// TODO
@RequestScoped
@Transactional
public class ProviderService implements IProviderService {

	@SuppressWarnings("unused")
	private final Logger logger = Logger.getLogger(ProviderService.class.getCanonicalName());

	private final IProviderFactory providerFactory;

	private final ProviderDtoFactory providerDtoFactory;
	

	public ProviderService() {
		// Initialize factories
		providerFactory = new ProviderFactory();
		providerDtoFactory = new ProviderDtoFactory();
	}
	
	// TODO
	@Inject
	private IProviderDao providerDao;

	// TODO
	@Inject
	private IPatientDao patientDao;


	/**
	 * @see IProviderService#addProvider(ProviderDto dto)
	 */
	@Override
	public UUID addProvider(ProviderDto dto) throws ProviderServiceExn {
		// Use factory to create Provider entity, and persist with DAO
		try {
			Provider provider = providerFactory.createProvider();
			if (dto.getId() == null) {
				provider.setProviderId(UUID.randomUUID());
			} else {
				provider.setProviderId(dto.getId());
			}
			provider.setName(dto.getName());
			provider.setNpi(dto.getNpi());
			providerDao.addProvider(provider);
			return provider.getProviderId();
		} catch (ProviderExn e) {
			throw new ProviderServiceExn("Failed to add provider", e);
		}
	}
	
	public List<ProviderDto> getProviders() throws ProviderServiceExn {
		List<ProviderDto> dtos = new ArrayList<ProviderDto>();
		Collection<Provider> providers = providerDao.getProviders();
		for (Provider provider : providers) {
			try {
				dtos.add(providerToDto(provider, false));
			} catch (TreatmentExn e) {
				throw new ProviderServiceExn("Failed to export treatment", e);
			}
		}
		return dtos;
	}

	/**
	 * @see IProviderService#getProvider(UUID)
	 */
	@Override
	/*
	 * The boolean flag indicates if related treatments should be loaded eagerly.
	 */
	public ProviderDto getProvider(UUID id, boolean includeTreatments) throws ProviderServiceExn {
		// TODO use DAO to get Provider by external key
		try {
			return providerToDto(providerDao.getProvider(id), true);
		} catch (ProviderExn | TreatmentExn pe) {
			throw new ProviderServiceExn("Failed to get provider", pe);
		}
	}
	
	@Override
	/*
	 * By default, we eagerly load related treatments with a provider record.
	 */
	public ProviderDto getProvider(UUID id) throws ProviderServiceExn {
		return getProvider(id, true);
	}

	private ProviderDto providerToDto(Provider provider, boolean includeTreatments) throws TreatmentExn {
		ProviderDto dto = providerDtoFactory.createProviderDto();
		dto.setId(provider.getProviderId());
		dto.setName(provider.getName());
		dto.setNpi(provider.getNpi());
		if (includeTreatments) {
			dto.setTreatments(provider.exportTreatments(TreatmentExporter.exportWithoutFollowups()));
		}
		return dto;
	}
	
	private void addTreatmentImpl(TreatmentDto dto, Consumer<Treatment> parentFollowUps)
			throws PatientServiceExn, ProviderServiceExn {
		try {
			/*
			 * Set the external key here.
			 */
			if (dto.getId() == null) {
				dto.setId(UUID.randomUUID());
			}
			
			Provider provider = providerDao.getProvider(dto.getProviderId());
			Patient patient = patientDao.getPatient(dto.getPatientId());

			Consumer<Treatment> followUpsConsumer;

			if (dto instanceof DrugTreatmentDto drugTreatmentDto) {

				followUpsConsumer = provider.importtDrugTreatment(dto.getId(), patient, provider, dto.getDiagnosis(),
						drugTreatmentDto.getDrug(), drugTreatmentDto.getDosage(), drugTreatmentDto.getStartDate(),
						drugTreatmentDto.getEndDate(), drugTreatmentDto.getFrequency(), parentFollowUps);

			} else if (dto instanceof SurgeryTreatmentDto surgeryTreatmentDto) {
				followUpsConsumer = provider.importSurgery(dto.getId(), patient, provider, dto.getDiagnosis(),
						surgeryTreatmentDto.getSurgeryDate(), surgeryTreatmentDto.getDischargeInstructions(),
						parentFollowUps);
			} else if (dto instanceof RadiologyTreatmentDto radiologyTreatmentDto) {
				followUpsConsumer = provider.importRadiology(dto.getId(), patient, provider, dto.getDiagnosis(),
						radiologyTreatmentDto.getTreatmentDates(), parentFollowUps);
			} else if (dto instanceof PhysiotherapyTreatmentDto physiotherapyTreatmentDto) {
				followUpsConsumer = provider.importPhysiotherapy(dto.getId(), patient, provider, dto.getDiagnosis(),
						physiotherapyTreatmentDto.getTreatmentDates(), parentFollowUps);
			} else {
				/*
				 * TODO Handle the other cases
				 */


					throw new IllegalArgumentException("No treatment-specific info provided.");
				
			}

			for (TreatmentDto followUp : dto.getFollowupTreatments()) {
				addTreatmentImpl(followUp, followUpsConsumer);
			}

		} catch (PatientExn e) {
			throw new PatientNotFoundExn("Could not find patient for " + dto.getPatientId(), e);

		} catch (ProviderExn e) {
			throw new ProviderNotFoundExn("Could not find provider for " + dto.getProviderId(), e);
		}
	}
	
	@Override
	public void addTreatment(TreatmentDto dto) throws PatientServiceExn, ProviderServiceExn {
		addTreatmentImpl(dto, null);
	}

	@Override
	public TreatmentDto getTreatment(UUID providerId, UUID tid)
			throws ProviderServiceExn {
		// Export treatment DTO from Provider aggregate
		try {
			Provider provider = providerDao.getProvider(providerId);
			TreatmentDto treatment = provider.exportTreatment(tid, TreatmentExporter.exportWithoutFollowups());
			return treatment;

		} catch (TreatmentExn e) {
			throw new TreatmentNotFoundExn("Could not find treatment for "+tid, e);
		
		} catch (ProviderExn e) {
			throw new ProviderNotFoundExn("Could not find provider for "+providerId, e);
		}
	}


	@Override
	public void removeAll() throws ProviderServiceExn {
		providerDao.deleteProviders();
	}
	
}
