package edu.stevens.cs548.clinic.rest;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/")
public class AppConfig extends Application {
	
	@Override
	/*
	 * Register resource classes with JAX-RS implementation.
	 */
	public Set<Class<?>> getClasses() {
		Set<Class<?>> classes = new HashSet<>();
		classes.add(PatientResource.class);
		classes.add(ProviderResource.class);
		return classes;
	}
	
    @Override
    /*
     * Turn off the default JSON provider in Payara/Jersey.
     */
    public Map<String, Object> getProperties() {
        Map<String, Object> props = new HashMap<>();
        props.put("jersey.config.server.disableMoxyJson", true);
        return props;
    }

}
