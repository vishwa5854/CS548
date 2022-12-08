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
public interface IPatientMicroService {
	
	public Response addPatient(PatientDto dto);

	public Response getPatients();
	
	public Response getPatient(@PathParam("id") String id, @QueryParam("treatments") String includeTreatments);
	
	public Response getTreatment(@PathParam("id") String patientId, @PathParam("tid") String treatmentId);

	public Response removeAll();
	
}
