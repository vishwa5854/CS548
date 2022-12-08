package edu.stevens.cs548.clinic.micro.domain.stub;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import edu.stevens.cs548.clinic.micro.domain.IProviderMicroService;
import edu.stevens.cs548.clinic.service.IPatientService.PatientServiceExn;
import edu.stevens.cs548.clinic.service.IProviderService;
import edu.stevens.cs548.clinic.service.dto.ProviderDto;
import edu.stevens.cs548.clinic.service.dto.TreatmentDto;

// TODO
public class ProviderService implements IProviderService {
	
	private Logger logger = Logger.getLogger(ProviderService.class.getCanonicalName());

	private static final String LOCATION = "location";
	
	// TODO
	IProviderMicroService providerMicroService;
	
	@Override
	public UUID addProvider(ProviderDto dto) throws ProviderServiceExn {
		logger.info(String.format("addProvider: Adding provider %s in microservice client!", dto.getName()));
		Response response = providerMicroService.addProvider(dto);
		if (response.getStatus() >= 300) {
			Exception e = new WebApplicationException(response);
			throw new ProviderServiceExn("Failed to add provider "+dto.getId(), e);
		}
		String location = response.getHeaderString(LOCATION);
		if (location == null) {
			throw new IllegalStateException("Missing location response header!");
		}
		String[] uriSegments = URI.create(location).getPath().split("/");
		return UUID.fromString(uriSegments[uriSegments.length-1]);
	}

	@Override
	public List<ProviderDto> getProviders() throws ProviderServiceExn {
		logger.info(String.format("getProviders: Getting all providers in microservice client!"));
		Response response = providerMicroService.getProviders();
		if (response.getStatus() >= 300) {
			Exception e = new WebApplicationException(response);
			throw new ProviderServiceExn("Failed to get providers!", e);
		}
		return response.readEntity(new GenericType<List<ProviderDto>>() {});
	}

	@Override
	public ProviderDto getProvider(UUID id, boolean includeTreatments) throws ProviderServiceExn {
		logger.info(String.format("getProvider: Getting provider %s in microservice client!", id.toString()));
		Response response = providerMicroService.getProvider(id.toString(), Boolean.toString(includeTreatments));
		if (response.getStatus() >= 300) {
			Exception e = new WebApplicationException(response);
			throw new ProviderServiceExn("Failed to get provider "+id, e);
		}
		return response.readEntity(ProviderDto.class);
	}

	@Override
	public ProviderDto getProvider(UUID id) throws ProviderServiceExn {
		return getProvider(id, true);
	}

	@Override
	public void addTreatment(TreatmentDto dto) throws PatientServiceExn, ProviderServiceExn {
		logger.info(String.format("addTreatment: Adding treatment for %s in microservice client!", dto.getPatientName()));
		Response response = providerMicroService.addTreatment(dto.getProviderId().toString(), dto);
		if (response.getStatus() >= 300) {
			Exception e = new WebApplicationException(response);
			throw new ProviderServiceExn("Failed to add treatment "+dto.getId(), e);
		}		
	}

	@Override
	public TreatmentDto getTreatment(UUID providerId, UUID treatmentId)
			throws ProviderNotFoundExn, TreatmentNotFoundExn, ProviderServiceExn {
		logger.info(String.format("getTreatment: Getting treatment %s in microservice client!", treatmentId.toString()));
		Response response = providerMicroService.getTreatment(providerId.toString(), treatmentId.toString());
		if (response.getStatus() >= 300) {
			Exception e = new WebApplicationException(response);
			throw new TreatmentNotFoundExn("Failed to get treatment "+treatmentId, e);
		}
		return response.readEntity(TreatmentDto.class);
	}

	@Override
	public void removeAll() throws ProviderServiceExn {
		logger.info(String.format("deleteProviders: Deleting all providers in microservice client!"));
		Response response = providerMicroService.removeAll();
		if (response.getStatus() >= 300) {
			Exception e = new WebApplicationException(response);
			throw new ProviderServiceExn("Failed to remove provider records!", e);
		}
	}

}
