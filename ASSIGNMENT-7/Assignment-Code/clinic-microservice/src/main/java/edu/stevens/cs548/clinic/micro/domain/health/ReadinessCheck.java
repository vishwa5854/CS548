package edu.stevens.cs548.clinic.micro.domain.health;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Logger;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;

// TODO
public class ReadinessCheck implements HealthCheck {
	
	private static final Logger logger = Logger.getLogger(ReadinessCheck.class.getCanonicalName());
	
	private static final String DATABASE_HOST_PROPERTY = "db.host";
	
	private static final String DATABASE_PORT_PROPERTY = "db.port";
	
	private static final String READINESS_CHECK_NAME = "Database Readiness Check";
	
	private static final String ERROR_KEY = "error";

	// TODO

	private String host;

	// TODO
	private int port;

	@Override
	public HealthCheckResponse call() {
		HealthCheckResponseBuilder responseBuilder = HealthCheckResponse.named(READINESS_CHECK_NAME);
		try {
			
			pingServer();
			
			logger.info("Readiness check for database succeeded!");
			return responseBuilder.up().build();
			
		} catch (IOException e) {
			
			return responseBuilder.down().withData(ERROR_KEY, e.getMessage()).build();
			
		}
	}
	
	private void pingServer() throws UnknownHostException, IOException {
		Socket socket = new Socket(host,port);
		socket.close();
	}

}
