package edu.stevens.cs548.clinic.micro.domain.rest;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

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
import edu.stevens.cs548.clinic.service.dto.DrugTreatmentDto;
import edu.stevens.cs548.clinic.service.dto.PhysiotherapyTreatmentDto;
import edu.stevens.cs548.clinic.service.dto.ProviderDto;
import edu.stevens.cs548.clinic.service.dto.ProviderDtoFactory;
import edu.stevens.cs548.clinic.service.dto.RadiologyTreatmentDto;
import edu.stevens.cs548.clinic.service.dto.SurgeryTreatmentDto;
import edu.stevens.cs548.clinic.service.dto.TreatmentDto;

/**
 * Microservice for provider aggregate
 */
// TODO
@RequestScoped
@Transactional
@Path("/provider")
public class ProviderMicroService {

	private Logger logger = Logger.getLogger(ProviderMicroService.class.getCanonicalName());

	private IProviderFactory providerFactory;

	private ProviderDtoFactory providerDtoFactory;
	
	public ProviderMicroService() {
		// Initialize factories
		providerFactory = new ProviderFactory();
		providerDtoFactory = new ProviderDtoFactory();
	}
	

	// TODO
	@Context
	UriInfo uriInfo;
	
	// TODO
	@Inject
	private IProviderDao providerDao;

	// TODO
	@Inject
	private IPatientDao patientDao;

	// TODO
	@POST
	@Consumes("application/json")
	public Response addProvider(ProviderDto dto) {
		// Use factory to create Provider entity, and persist with DAO
		try {
			logger.info(String.format("addProvider: Adding provider %s in microservice!", dto.getName()));
			Provider provider = providerFactory.createProvider();
			if (dto.getId() == null) {
				provider.setProviderId(UUID.randomUUID());
			} else {
				provider.setProviderId(dto.getId());
			}
			provider.setName(dto.getName());
			provider.setNpi(dto.getNpi());
			providerDao.addProvider(provider);
			URI location = uriInfo.getBaseUriBuilder().path(provider.getProviderId().toString()).build();
			return Response.created(location).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Failed to add provider!", e);
			return Response.serverError().build();
		}
	}
	
	// TODO
	@GET
	@Produces("application/json")
	public Response getProviders() {
		try {
			logger.info(String.format("getProviders: Getting all providers in microservice!"));
			List<ProviderDto> dtos = new ArrayList<ProviderDto>();
			Collection<Provider> providers = providerDao.getProviders();
			for (Provider provider : providers) {
				dtos.add(providerToDto(provider, false));
			}
			GenericEntity<List<ProviderDto>> ps = new GenericEntity<List<ProviderDto>>(dtos){};
			return Response.ok(ps, MediaType.APPLICATION_JSON).build();
		} catch (TreatmentExn e) {
			logger.log(Level.SEVERE, "Failed to get providers!", e);
			return Response.serverError().build();
		}
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
	
	// TODO
	@GET
	@Path("/{id}")
	@Produces("application/json")
	public Response getProvider(@PathParam("id") String id, @QueryParam("treatments") @DefaultValue("true")String treatments) {
		try {
			logger.info(String.format("getProvider: Getting provider %s in microservice!", id));
			UUID providerId = UUID.fromString(id);
			boolean includeTreatments = Boolean.parseBoolean(treatments);
			
			// TODO use DAO to get Provider by external key

			Provider provider = providerDao.getProvider(providerId, includeTreatments);
			ProviderDto providerDto = providerToDto(provider, includeTreatments);
			
			return Response.ok(providerDto, MediaType.APPLICATION_JSON).build();
		} catch (ProviderExn e) {
			logger.info("Failed to find provider with id "+id);
			return Response.status(Status.NOT_FOUND).build();
		} catch (TreatmentExn e) {
			logger.log(Level.SEVERE, String.format("Failed to get provider %s!", id), e);
			return Response.serverError().build();
		}
	}
	
	// TODO
	@POST
	@Path("/{id}")
	@Produces("application/json")
	public Response addTreatment(@PathParam("id") String id, TreatmentDto dto) {
		try {
			logger.info(String.format("addTreatment: Adding treatment for %s in microservice!", dto.getPatientName()));
			UUID providerId = UUID.fromString(id);
			if (!providerId.equals(dto.getProviderId())) {
				throw new IllegalArgumentException(String.format("Provider id %s in URI is not the same as the treatment provider id %s", id, dto.getProviderId().toString()));
			}
			addTreatmentImpl(dto, null);
			// Service doesn't return treatment id, so can't construct URI for Location header!
			return Response.status(Status.CREATED).build();
		} catch (PatientExn | ProviderExn e) {
			return Response.status(Status.NOT_FOUND).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, String.format("Failed to add treatment %s!", id), e);
			return Response.serverError().build();
		}
	}

	private void addTreatmentImpl(TreatmentDto dto, Consumer<Treatment> parentFollowUps)
			throws PatientExn, ProviderExn {
		/*
		 * Set the external key here.
		 */
		if (dto.getId() == null) {
			dto.setId(UUID.randomUUID());
		}

		Provider provider = providerDao.getProvider(dto.getProviderId());
		Patient patient = patientDao.getPatient(dto.getPatientId());

		Consumer<Treatment> followUpsConsumer;

		if (dto instanceof DrugTreatmentDto) {

			DrugTreatmentDto drugTreatmentDto = (DrugTreatmentDto) dto;
			followUpsConsumer = provider.importtDrugTreatment(dto.getId(), patient, provider, dto.getDiagnosis(),
					drugTreatmentDto.getDrug(), drugTreatmentDto.getDosage(), drugTreatmentDto.getStartDate(),
					drugTreatmentDto.getEndDate(), drugTreatmentDto.getFrequency(), parentFollowUps);

		} else if (dto instanceof SurgeryTreatmentDto) {
			SurgeryTreatmentDto surgeryTreatmentDto = (SurgeryTreatmentDto) dto;
			followUpsConsumer = provider.importSurgery(
					dto.getId(),
					patient,
					provider,
					dto.getDiagnosis(),
					surgeryTreatmentDto.getSurgeryDate(),
					surgeryTreatmentDto.getDischargeInstructions(),
					parentFollowUps
			);
		} else if (dto instanceof RadiologyTreatmentDto) {
			RadiologyTreatmentDto radiologyTreatmentDto = (RadiologyTreatmentDto) dto;
			followUpsConsumer = provider.importRadiology(
					dto.getId(),
					patient,
					provider,
					dto.getDiagnosis(),
					radiologyTreatmentDto.getTreatmentDates(),
					parentFollowUps
			);
		} else {
			/*
			 * TODO Handle the other cases
			 */
			PhysiotherapyTreatmentDto physiotherapyTreatmentDto = (PhysiotherapyTreatmentDto) dto;
			followUpsConsumer = provider.importPhysiotherapy(
					dto.getId(),
					patient,
					provider,
					dto.getDiagnosis(),
					physiotherapyTreatmentDto.getTreatmentDates(),
					parentFollowUps
			);
		}

		for (TreatmentDto followUp : dto.getFollowupTreatments()) {
			addTreatmentImpl(followUp, followUpsConsumer);
		}

	}
	
	// TODO
	@GET
	@Path("/{id}/treatment/{tid}")
	@Produces
	public Response getTreatment(@PathParam("id") String id, @PathParam("tid") String tid) {
		try {
			logger.info(String.format("getTreatment: Getting treatment %s in microservice!", tid));
			// Export treatment DTO from Provider aggregate
			UUID providerId = UUID.fromString(id);
			UUID treatmentId = UUID.fromString(tid);
			Provider provider = providerDao.getProvider(providerId);
			TreatmentDto treatment = provider.exportTreatment(treatmentId, TreatmentExporter.exportWithoutFollowups());
			return Response.ok(treatment, MediaType.APPLICATION_JSON).build();
		} catch (ProviderExn e) {
			logger.info("Failed to find provider with id "+id);
			return Response.status(Status.NOT_FOUND).build();
		} catch (TreatmentExn e) {
			logger.info(String.format("Failed to find treatment %s for provider %s", tid, id));
			return Response.status(Status.NOT_FOUND).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, String.format("Failed to get treatment %s!", tid), e);
			return Response.serverError().build();
		}
	}
	

	// TODO
	@DELETE
	public void removeAll() {
		logger.info(String.format("deleteProviders: Deleting all providers in microservice!"));
		providerDao.deleteProviders();
	}


}
