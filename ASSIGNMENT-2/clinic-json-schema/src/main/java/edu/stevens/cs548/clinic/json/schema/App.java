package edu.stevens.cs548.clinic.json.schema;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;

public class App {

	public static final String BASE_URI = "https://cs548.stevens.edu/clinic";

	public static final String PATIENT_URI = BASE_URI + "/patient";

	public static final String PROVIDER_URI = BASE_URI + "/provider";

	public static final String TREATMENT_URI = BASE_URI + "/treatment";

	public static final String PATIENT_CMD = "patient";

	public static final String PROVIDER_CMD = "provider";

	public static final String TREATMENT_CMD = "treatment";

	public static final String PATIENT_SCHEMA = "/schema/patient-schema.json";

	public static final String PROVIDER_SCHEMA = "/schema/provider-schema.json";

	public static final String TREATMENT_SCHEMA = "/schema/treatment-schema.json";

	private static final Logger logger = Logger.getLogger(App.class.getCanonicalName());

	public void severe(String s) {
		logger.severe(s);
	}

	public void severe(Exception e) {
		logger.log(Level.SEVERE, "Error during processing!", e);
	}

	public void warning(String s) {
		logger.info(s);
	}

	public void info(String s) {
		logger.info(s);
	}

	static void msg(String m) {
		System.out.print(m);
	}

	static void msgln(String m) {
		System.out.println(m);
	}

	static void err(String s) {
		System.err.println("** " + s);
	}

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		new App(args);
	}

	public App(String[] args) {
		
		if (args.length != 2) {
			severe("Usage: java -jar jsonschema.jar <command> <filename>");
			severe("where <command> is: patient, provider or treatment");
			return;
		}
		
		File jsonFile = new File(args[1]);
		if (!jsonFile.exists()) {
			severe("Input file does not exist: "+jsonFile);
			return;
		}
		
		JsonSchema jsonSchema;
		
		if (PATIENT_CMD.equals(args[0]) ) {
			jsonSchema = getSchema(PATIENT_SCHEMA);
		} else if (PROVIDER_CMD.equals(args[0])) {
			jsonSchema = getSchema(PROVIDER_SCHEMA);
		} else if (TREATMENT_CMD.equals(args[0])) {
			jsonSchema = getSchema(TREATMENT_SCHEMA);
		} else {
			severe("Usage: java -jar jsonschema.jar <command> <filename>");
			severe("where <command> is: patient, provider or treatment");
			return;
		}
		
		JsonNode json = getJson(jsonFile);
		
		Set<ValidationMessage> validationResult = jsonSchema.validate(json);
		
		if (validationResult.isEmpty()) {
			msgln("Validation succeeded!");
		} else {
			msgln("Validation failed!");
			for (ValidationMessage message : validationResult) {
				msgln(message.getMessage());
			}
		}
		
	}

	private JsonSchema getSchema(String filename) {
		JsonSchemaFactory schemaFactory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012);
		try (InputStream in = getClass().getResourceAsStream(filename)) {
			return schemaFactory.getSchema(in);
		} catch (IOException e) {
			throw new IllegalStateException("Error reading schema file: " + filename, e);
		}
	}

	private JsonNode getJson(File file) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.readTree(file);
		} catch (IOException e) {
			throw new IllegalStateException("Error reading JSON file: " + file.getAbsolutePath(), e);

		}
	}
}
