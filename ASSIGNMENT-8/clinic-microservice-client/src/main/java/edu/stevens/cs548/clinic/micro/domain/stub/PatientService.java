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

import edu.stevens.cs548.clinic.micro.domain.IPatientMicroService;
import edu.stevens.cs548.clinic.service.IPatientService;
import edu.stevens.cs548.clinic.service.dto.PatientDto;
import edu.stevens.cs548.clinic.service.dto.TreatmentDto;

// TODO
@RequestScoped
public class PatientService implements IPatientService {
	
	private Logger logger = Logger.getLogger(PatientService.class.getCanonicalName());
	
	private static final String LOCATION = "Location";
	
	// TODO
	@Inject @RestClient
	IPatientMicroService patientMicroService;

	@Override
	public UUID addPatient(PatientDto dto) throws PatientServiceExn {
		logger.info(String.format("addPatient: Adding patient %s in microservice client!", dto.getName()));
		Response response = patientMicroService.addPatient(dto);
		if (response.getStatus() >= 300) {
			Exception e = new WebApplicationException(response);
			throw new PatientServiceExn("Failed to add patient "+dto.getId(), e);
		}
		String location = response.getHeaderString(LOCATION);
		if (location == null) {
			throw new IllegalStateException("Missing location response header!");
		}
		String[] uriSegments = URI.create(location).getPath().split("/");
		return UUID.fromString(uriSegments[uriSegments.length-1]);
	}

	@Override
	public List<PatientDto> getPatients() throws PatientServiceExn {
		logger.info(String.format("getPatients: Getting all patients in microservice client!"));
		Response response = patientMicroService.getPatients();
		if (response.getStatus() >= 300) {
			Exception e = new WebApplicationException(response);
			throw new PatientServiceExn("Failed to get patients!", e);
		}
		return response.readEntity(new GenericType<List<PatientDto>>() {});
	}

	@Override
	public PatientDto getPatient(UUID id, boolean includeTreatments) throws PatientServiceExn {
		logger.info(String.format("getPatient: Getting patient %s in microservice client!", id.toString()));
		Response response = patientMicroService.getPatient(id.toString(), Boolean.toString(includeTreatments));
		if (response.getStatus() >= 300) {
			Exception e = new WebApplicationException(response);
			throw new PatientServiceExn("Failed to get patient "+id, e);
		}
		return response.readEntity(PatientDto.class);
	}

	@Override
	public PatientDto getPatient(UUID id) throws PatientServiceExn {
		return getPatient(id, true);
	}

	@Override
	public TreatmentDto getTreatment(UUID patientId, UUID treatmentId)
			throws PatientNotFoundExn, TreatmentNotFoundExn, PatientServiceExn {
		logger.info(String.format("getTreatment: Getting treatment %s in microservice client!", treatmentId.toString()));
		Response response = patientMicroService.getTreatment(patientId.toString(), treatmentId.toString());
		if (response.getStatus() >= 300) {
			Exception e = new WebApplicationException(response);
			throw new TreatmentNotFoundExn("Failed to get treatment "+treatmentId, e);
		}
		return response.readEntity(TreatmentDto.class);
	}

	@Override
	public void removeAll() throws PatientServiceExn {
		logger.info(String.format("deletePatients: Deleting all patients in microservice client!"));
		Response response = patientMicroService.removeAll();
		if (response.getStatus() >= 300) {
			Exception e = new WebApplicationException(response);
			throw new PatientServiceExn("Failed to remove patient records!", e);
		}
	}

}
