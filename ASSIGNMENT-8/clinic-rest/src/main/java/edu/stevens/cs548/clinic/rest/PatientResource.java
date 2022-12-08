package edu.stevens.cs548.clinic.rest;

import java.util.UUID;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import edu.stevens.cs548.clinic.service.IPatientService;
import edu.stevens.cs548.clinic.service.IPatientService.PatientServiceExn;
import edu.stevens.cs548.clinic.service.dto.PatientDto;
import edu.stevens.cs548.clinic.service.dto.TreatmentDto;

// TODO
@RequestScoped
@Path("/patient")
public class PatientResource extends ResourceBase {
	
	private static final Logger logger = Logger.getLogger(PatientResource.class.getCanonicalName());
	
	@Context
	private UriInfo uriInfo;
	
	// TODO
	@Inject
	private IPatientService patientService;
	
	// TODO
	/*
	 * Return a provider DTO including the list of treatments they are administering.
	 */
	@GET
	@Path("/{id}")
	@Produces
	public Response getPatient(@PathParam("id") String id) {
		try {
			UUID patientId = UUID.fromString(id);
			PatientDto patient = patientService.getPatient(patientId, true);
			ResponseBuilder responseBuilder = Response.ok(patient);
			/* 
			 * Add links for treatments in response headers.
			 */
			for (TreatmentDto treatment : patient.getTreatments()) {
				responseBuilder.link(getTreatmentUri(uriInfo, treatment.getProviderId(), treatment.getId()), TREATMENT);
			}
			return responseBuilder.build();
		} catch (PatientServiceExn e) {
			logger.info("Failed to find patient with id "+id);
			return Response.status(Status.NOT_FOUND).build();
		} catch (IllegalArgumentException e) {
			logger.info("Badly formed patient id: "+id);
			return Response.status(Status.BAD_REQUEST).build();
		}
	}
	
}
