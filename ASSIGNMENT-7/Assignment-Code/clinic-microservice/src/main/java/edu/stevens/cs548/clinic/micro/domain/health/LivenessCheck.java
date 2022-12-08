package edu.stevens.cs548.clinic.micro.domain.health;

import java.util.logging.Logger;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;

// TODO
public class LivenessCheck implements HealthCheck {

	private static final Logger logger = Logger.getLogger(ReadinessCheck.class.getCanonicalName());
	
	private static final String MEMORY_THRESHOLD_PROPERTY = "memory.threshold";
		
	private static final String LIVENESS_CHECK_NAME = "Available Memory Check";
	
	private static final String ERROR_KEY = "error";

	// TODO
    private long threshold;
	
    @Override
    public HealthCheckResponse call() {
    	
        HealthCheckResponseBuilder responseBuilder = HealthCheckResponse.named(LIVENESS_CHECK_NAME);
        
        long freeMemory = Runtime.getRuntime().freeMemory();
        
        if (freeMemory >= threshold) {
        	
			logger.info("Liveness check for database microservice succeeded!");
            return responseBuilder.up().build();
            
        } else {
        	
			logger.info("Liveness check for database microservice failed!");
        	String errorMessage = String.format("Insufficient free memory, %d currently available.  Please restart the application",  freeMemory);
            return responseBuilder.down().withData(ERROR_KEY,errorMessage).build();
            
        }
    }
}
