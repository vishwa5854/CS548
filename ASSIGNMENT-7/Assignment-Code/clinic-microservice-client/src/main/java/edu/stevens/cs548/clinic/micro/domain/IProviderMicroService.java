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
import edu.stevens.cs548.clinic.service.dto.ProviderDto;
import edu.stevens.cs548.clinic.service.dto.TreatmentDto;

// TODO annotate
public interface IProviderMicroService {
	
	public Response addProvider(ProviderDto dto);
	
	public Response getProviders();

	public Response getProvider(@PathParam("id") String id, @QueryParam("treatments") String includeTreatments);
		
	public Response addTreatment(@PathParam("id") String id, TreatmentDto dto);
	
	public Response getTreatment(@PathParam("id") String providerId, @PathParam("tid") String treatmentId);

	public Response removeAll();
		
}
