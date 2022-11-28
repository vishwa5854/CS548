package edu.stevens.cs548.clinic.rest.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import edu.stevens.cs548.clinic.rest.client.stub.IStreamingOutput;
import edu.stevens.cs548.clinic.rest.client.stub.WebClient;
import edu.stevens.cs548.clinic.service.dto.*;
import edu.stevens.cs548.clinic.service.dto.util.GsonFactory;

public class App {
	
	public static final String APP_PROPERTIES = "/app.properties";
	
	public static final String SERVER_URI_PROPERTY = "server.uri";
	
	public static final String DATABASE_FILE_PROPERTY = "database.file";

	public static final String PATIENTS = "patients";

	public static final String PROVIDERS = "providers";

	public static final String TREATMENTS = "treatments";

	private static final Logger logger = Logger.getLogger(App.class.getCanonicalName());

	private final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

	private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

	private final PatientDtoFactory patientFactory = new PatientDtoFactory();

	private final ProviderDtoFactory providerFactory = new ProviderDtoFactory();

	private final TreatmentDtoFactory treatmentFactory = new TreatmentDtoFactory();

	private final Gson gson = GsonFactory.createGson();
	
	private final WebClient client;

	private List<PatientDto> patients = new ArrayList<PatientDto>();

	private List<ProviderDto> providers = new ArrayList<ProviderDto>();

	private List<TreatmentDto> treatments = new ArrayList<TreatmentDto>();
		
	private URI serverUri;
	
	private String database;

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

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		new App(args);
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
	
	protected void loadProperties() {
		/*
		 * Load default properties.
		 */
		try {
			Properties props = new Properties();
			InputStream propsIn = getClass().getResourceAsStream(APP_PROPERTIES);
			props.load(propsIn);
			propsIn.close();
			serverUri = URI.create(props.getProperty(SERVER_URI_PROPERTY));
			database = props.getProperty(DATABASE_FILE_PROPERTY);
		} catch (IOException e) {
			throw new IllegalStateException("Failed to load properties from "+APP_PROPERTIES, e);
		}
	}
	
	protected List<String> processArgs(String[] args) {
		/*
		 * Default properties may be overridden on the command line.
		 */
		List<String> commandLineArgs = new ArrayList<String>();
		int ix = 0;
		Hashtable<String, String> opts = new Hashtable<String, String>();

		while (ix < args.length) {
			if (args[ix].startsWith("--")) {
				String option = args[ix++].substring(2);
				if (ix == args.length || args[ix].startsWith("--"))
					severe("Missing argument for --" + option + " option.");
				else if (opts.containsKey(option))
					severe("Option \"" + option + "\" already set.");
				else
					opts.put(option, args[ix++]);
			} else {
				commandLineArgs.add(args[ix++]);
			}
		}
		/*
		 * Overrides of values from configuration file.
		 */
		Enumeration<String> keys = opts.keys();
		while (keys.hasMoreElements()) {
			String k = keys.nextElement();
			if ("server".equals(k))
				serverUri = URI.create(opts.get("server"));
			else if ("db".equals(k)) 
				database = opts.get("db");
			else
				severe("Unrecognized option: --" + k);
		}

		return commandLineArgs;
	}


	public App(String[] args) {
		
		loadProperties();
		
		processArgs(args);
		
		client = new WebClient(serverUri);
		
		// Main command-line interface loop

		while (true) {
			try {
				msg("cs548> ");
				String line = in.readLine();
				if (line == null) {
					return;
				}
				String[] inputs = line.split("\\s+");
				if (inputs.length > 0) {
					String cmd = inputs[0];
					if (cmd.length() == 0)
						;
					else if ("load".equals(cmd))
						load(inputs);
					else if ("save".equals(cmd))
						save(inputs);
					else if ("addpatient".equals(cmd))
						addPatient();
					else if ("addprovider".equals(cmd))
						addProvider();
					else if ("addtreatment".equals(cmd))
						addOneTreatment();
					else if ("list".equals(cmd))
						list(inputs);
					else if ("upload".equals(cmd))
						upload(inputs);
					else if ("help".equals(cmd))
						help(inputs);
					else if ("quit".equals(cmd))
						return;
					else
						msgln("Bad input.  Type \"help\" for more information.");
				}
			} catch (Exception e) {
				severe(e);
			}
		}
	}

	public void load(String[] inputs) throws IOException, ParseException {
		String filename;
		if (inputs.length == 1) {
			filename = database;
		} else if (inputs.length == 2) {
			filename = inputs[1];
		} else {
			err("Usage: load [filename]");
			return;
		}
		File input = new File(filename);
		if (!input.exists()) {
			err("File " + input + " does not exist!");
			return;
		}
		JsonReader rd = gson.newJsonReader(new BufferedReader(new FileReader(input)));
		rd.beginObject();

		/*
		 * Parse the list of patients.
		 */
		if (!PATIENTS.equals(rd.nextName())) {
			throw new ParseException("Expected field: " + PATIENTS, 0);
		}
		rd.beginArray();
		while (rd.hasNext()) {
			PatientDto patient = gson.fromJson(rd, PatientDto.class);
			patients.add(patient);
		}
		rd.endArray();

		/*
		 * Parse the list of providers.
		 */
		if (!PROVIDERS.equals(rd.nextName())) {
			throw new ParseException("Expected field: " + PROVIDERS, 0);
		}
		rd.beginArray();
		while (rd.hasNext()) {
			ProviderDto provider = gson.fromJson(rd, ProviderDto.class);
			providers.add(provider);
		}
		rd.endArray();

		/*
		 * TODO Load the treatment information
		 */
		if (!TREATMENTS.equals(rd.nextName())) {
			throw new ParseException("Expected field: " + TREATMENTS, 0);
		}
		rd.beginArray();
		while (rd.hasNext()) {
			TreatmentDto treatment = gson.fromJson(rd, TreatmentDto.class);
			treatments.add(treatment);
		}
		rd.endArray();


		rd.endObject();
	}

	@SuppressWarnings("resource")
	public void save(String[] inputs) throws IOException {
		String filename;
		if (inputs.length == 1) {
			filename = database;
		} else if (inputs.length == 2) {
			filename = inputs[1];
		} else {
			err("Usage: save [filename]");
			return;
		}
		File output = new File(filename);

		try (JsonWriter wr = gson.newJsonWriter(new BufferedWriter(new FileWriter(output)))) {
			wr.beginObject();

			wr.name(PATIENTS);
			wr.beginArray();
			for (PatientDto patient : patients) {
				gson.toJson(patient, PatientDto.class, wr);
			}
			wr.endArray();

			wr.name(PROVIDERS);
			wr.beginArray();
			for (ProviderDto provider : providers) {
				gson.toJson(provider, ProviderDto.class, wr);
			}
			wr.endArray();

			/*
			 * TODO Save the treatment information.
			 */
			wr.name(TREATMENTS);
			wr.beginArray();
			for (TreatmentDto treatment : treatments) {
				gson.toJson(treatment, TreatmentDto.class, wr);
			}
			wr.endArray();


			wr.endObject();
		}
	}

	public void addPatient() throws IOException, ParseException {
		PatientDto patient = patientFactory.createPatientDto();
		patient.setId(UUID.randomUUID());
		msg("Name: ");
		patient.setName(in.readLine());
		patient.setDob(readDate("Patient DOB"));
		patients.add(patient);
	}

	public void addProvider() throws IOException {
		ProviderDto provider = providerFactory.createProviderDto();
		provider.setId(UUID.randomUUID());
		msg("NPI: ");
		provider.setNpi(in.readLine());
		msg("Name: ");
		provider.setName(in.readLine());
		providers.add(provider);
	}
	
	/* 
	 * Use this to add a list of follow-up treatments.
	 */
	public void addTreatmentList(TreatmentDto parent) throws IOException, ParseException {
		TreatmentDto treatment = addTreatment();
		while (treatment != null) {
			parent.getFollowupTreatments().add(treatment);
			treatment = addTreatment();
		}
	}

	public void addOneTreatment() throws IOException, ParseException {
		TreatmentDto treatment = addTreatment();
		if (treatment != null) {
			treatments.add(treatment);
		}
	}
	
	public TreatmentDto addTreatment() throws IOException, ParseException {
		msg("What form of treatment: [D]rug, [S]urgery, [R]adiology, [P]hysiotherapy? ");
		String line = in.readLine().toUpperCase();
		TreatmentDto treatment = switch (line) {
			case "D" -> addDrugTreatment();
			case "S" -> addSurgeryTreatment();
			case "R" -> addRadiologyTreatment();
			case "P" -> addPhysiotherapyTreatment();
			default -> null;
		};
		// TODO add other cases
		
		if (treatment != null) {
			msgln("Adding follow-up treatments...");
			addTreatmentList(treatment);
			msgln("...finished follow-up treatments");
		}
		return treatment;
	}

	private DrugTreatmentDto addDrugTreatment() throws IOException, ParseException {
		DrugTreatmentDto treatment = treatmentFactory.createDrugTreatmentDto();

		treatment.setId(UUID.randomUUID());
		msg("Patient ID: ");
		treatment.setPatientId(UUID.fromString(in.readLine()));
		msg("Provider ID: ");
		treatment.setProviderId(UUID.fromString(in.readLine()));
		msg("Diagnosis: ");
		treatment.setDiagnosis(in.readLine());
		msg("Drug: ");
		treatment.setDrug(in.readLine());
		msg("Dosage: ");
		treatment.setDosage(Float.parseFloat(in.readLine()));
		treatment.setStartDate(readDate("Start date"));
		treatment.setEndDate(readDate("End date"));
		msg("Frequency: ");
		treatment.setFrequency(Integer.parseInt(in.readLine()));

		return treatment;
	}

	private SurgeryTreatmentDto addSurgeryTreatment() throws IOException, ParseException {
		SurgeryTreatmentDto treatment = treatmentFactory.createSurgeryTreatmentDto();
		treatment.setId(UUID.randomUUID());
		msg("Patient ID: ");
		treatment.setPatientId(UUID.fromString(in.readLine()));
		msg("Provider ID: ");
		treatment.setProviderId(UUID.fromString(in.readLine()));
		msg("Diagnosis: ");
		treatment.setDiagnosis(in.readLine());
		treatment.setSurgeryDate(readDate("Surgery Date"));
		treatment.setDischargeInstructions("Discharge Intruction");

		return treatment;
	}

	private RadiologyTreatmentDto addRadiologyTreatment() throws IOException, ParseException {
		RadiologyTreatmentDto treatment = treatmentFactory.createRadiologyTreatmentDto();
		treatment.setId(UUID.randomUUID());
		msg("Patient ID: ");
		treatment.setPatientId(UUID.fromString(in.readLine()));
		msg("Provider ID: ");
		treatment.setProviderId(UUID.fromString(in.readLine()));
		msg("Diagnosis: ");
		treatment.setDiagnosis(in.readLine());
		msg("Treatment Dates: ");
		List<LocalDate> dates = new ArrayList<>();
		String option = "";
		do {
			dates.add(readDate("Treatment Dates "));
			msg("Do you want to continue? (y/n): ");
			option = in.readLine();
		} while (option.equals("y"));

		treatment.setTreatmentDates(dates);

		return treatment;
	}

	private PhysiotherapyTreatmentDto addPhysiotherapyTreatment() throws IOException, ParseException {
		PhysiotherapyTreatmentDto treatment = treatmentFactory.createPhysiotherapyTreatmentDto();
		treatment.setId(UUID.randomUUID());
		msg("Patient ID: ");
		treatment.setPatientId(UUID.fromString(in.readLine()));
		msg("Provider ID: ");
		treatment.setProviderId(UUID.fromString(in.readLine()));
		msg("Diagnosis: ");
		treatment.setDiagnosis(in.readLine());
		msg("Treatment Dates: ");
		List<LocalDate> dates = new ArrayList<>();
		String option = "";
		do {
			dates.add(readDate("Treatment Dates "));
			msg("Do you want to continue? (y/n): ");
			option = in.readLine();
		} while (option.equals("y"));

		treatment.setTreatmentDates(dates);

		return treatment;
	}

	private LocalDate readDate(String field) throws IOException {
		msg(String.format("%s (MM/dd/yyyy): ", field));
		return LocalDate.parse(in.readLine(), dateFormatter);
	}

	public void list(String[] inputs) {
		msgln("Patients:");
		for (PatientDto patient : patients) {
			msgln(gson.toJson(patient));
		}
		
		msgln("Providers:");
		for (ProviderDto provider : providers) {
			msgln(gson.toJson(provider));
		}
		
		msgln("Treatments:");
		for (TreatmentDto treatment : treatments) {
			msgln(gson.toJson(treatment));
		}
	}
	
	public void upload(String[] inputs) throws IOException {
		if (inputs.length > 1) {
			err("Usage: upload");
			return;
		}
		
		/*
		 * Build the lambda that uploads the data to the server
		 */
		IStreamingOutput output = new IStreamingOutput() {

			@Override
			public void write(OutputStream output) throws IOException {
				
				logger.info("Data upload beginning...");
				
				try (JsonWriter wr = gson.newJsonWriter(new BufferedWriter(new OutputStreamWriter(output)))) {
					wr.beginObject();


					logger.info("...uploading provider records...");
					wr.name(PROVIDERS);
					wr.beginArray();
					for (ProviderDto provider : providers) {
						logger.info("......uploading provider "+provider.getId());
						gson.toJson(provider, ProviderDto.class, wr);
					}
					wr.endArray();
					
					logger.info("...uploading patient records...");
					wr.name(PATIENTS);
					wr.beginArray();
					for (PatientDto patient : patients) {
						logger.info("......uploading patient "+patient.getId());
						gson.toJson(patient, PatientDto.class, wr);
					}
					wr.endArray();

					/*
					 * TODO Upload the treatment information.
					 */
					logger.info("....uploading treatment records...");
					wr.name(TREATMENTS);
					wr.beginArray();

					for (TreatmentDto treatment : treatments) {
						logger.info(".....uploading treatment " + treatment.getId());
						gson.toJson(treatment, TreatmentDto.class, wr);
					}

					wr.endArray();

					wr.endObject();
				}

				logger.info("...data upload completed!");

			}
			
		};
		
		/*
		 * Do the web service call, using the lambda to stream the upload.
		 */
		client.upload(output);
		
		/*
		 * Clear the database.
		 */		
		patients = new ArrayList<>();
		
		providers = new ArrayList<>();
		
		treatments = new ArrayList<>();
	}

	
	public void help(String[] inputs) {
		if (inputs.length == 1) {
			msgln("Commands are:");
			msgln("  load filename: load database from a file");
			msgln("  save filename: save database to a file");
			msgln("  addpatient: add a patient");
			msgln("  addprovider: add a provider");
			msgln("  addtreatment: add a treatment");
			msgln("  list: display database content");
			msgln("  upload: upload data to web service");
			msgln("  quit: exit the app");
		}
	}

}
