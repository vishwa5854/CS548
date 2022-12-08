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
@RegisterRestClient(configKey="clinic-domain.api")
@RegisterProvider(GsonProvider.class)
@Path("provider")
public interface IProviderMicroService {

	@POST
	@Consumes("application/json")
	public Response addProvider(ProviderDto dto);

	@GET
	@Produces("application/json")
	public Response getProviders();

	@GET
	@Path("/{id}")
	@Produces("application/json")
	public Response getProvider(@PathParam("id") String id, @QueryParam("treatments") String includeTreatments);


	@POST
	@Path("/{id}")
	@Produces("application/json")
	public Response addTreatment(@PathParam("id") String id, TreatmentDto dto);

	@GET
	@Path("/{id}/treatment/{tid}")
	@Produces
	public Response getTreatment(@PathParam("id") String providerId, @PathParam("tid") String treatmentId);

	@DELETE
	public Response removeAll();
		
}
