package edu.stevens.cs548.clinic.domain;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.inject.Qualifier;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@RequestScoped
@Transactional
public class ClinicDomainProducer {

    /**
     * Default constructor. 
     */
    public ClinicDomainProducer() {
    }
    
    @Qualifier  
    @Retention(RetentionPolicy.RUNTIME)  
    @Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})  
    public @interface ClinicDomain {}
    
    @PersistenceContext(unitName="clinic-domain")
    EntityManager em;
    
    @Produces @ClinicDomain
    public EntityManager clinicDomainProducer() {
    	return em;
    }
    
    public void clinicDomainDispose(@Disposes @ClinicDomain EntityManager em) {
    	// Do not dispose of entity manager in container-managed bean
    	// em.close();
    }

}
