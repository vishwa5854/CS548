package edu.stevens.cs548.clinic.micro.domain;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import edu.stevens.cs548.clinic.gson.GsonProvider;
import edu.stevens.cs548.clinic.service.dto.PatientDto;

// TODO annotate
@RegisterRestClient(configKey="clinic-domain.api")
@RegisterProvider(GsonProvider.class)
@Path("patient")
public interface IPatientMicroService {

	@POST
	@Consumes("application/json")
	public Response addPatient(PatientDto dto);

	@GET
	@Produces("application/json")
	public Response getPatients();

	@GET
	@Path("/{id}")
	@Produces("application/json")
	public Response getPatient(@PathParam("id") String id, @QueryParam("treatments") String includeTreatments);

	@GET
	@Path("/{id}/treatment/{tid}")
	@Produces
	public Response getTreatment(@PathParam("id") String patientId, @PathParam("tid") String treatmentId);

	@DELETE
	public Response removeAll();
	
}
