package edu.stevens.cs548.clinic.micro.domain.rest;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.sql.DataSourceDefinition;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.glassfish.jersey.CommonProperties;

import edu.stevens.cs548.clinic.gson.GsonProvider;


// TODO
@DataSourceDefinition(
		name="java:global/jdbc/cs548",
		className="org.postgresql.ds.PGSimpleDataSource",
		user="${db.username}",
		password="${ENV=DATABASE_PASSWORD}",
		databaseName="${db.database}",
		serverName="${db.host}",
		portNumber=5432)
@ApplicationPath("/")
@ApplicationScoped
public class AppConfig extends Application {
	
	@Override
	/*
	 * Register provider and resources classes IN THAT ORDER with JAX-RS implementation.
	 */
	public Set<Class<?>> getClasses() {
		Set<Class<?>> classes = new HashSet<>();;;
		classes.add(GsonProvider.class);
		classes.add(PatientMicroService.class);
		classes.add(ProviderMicroService.class);
		return classes;
	}
	
    @Override
    /*
     * Turn off the default JSON provider in Payara/Jersey.
     */
    public Map<String, Object> getProperties() {
        Map<String, Object> props = new HashMap<>();
        props.put(CommonProperties.MOXY_JSON_FEATURE_DISABLE, true);
        props.put(CommonProperties.JSON_PROCESSING_FEATURE_DISABLE, true);
        return props;
    }

}
