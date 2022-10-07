package edu.stevens.cs548.clinic.data.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Date;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import edu.stevens.cs548.clinic.data.*;


public class App {
	
	/*
	 * JPA Properties
	 */
	private static final String JPA_SERVER_URL = "javax.persistence.jdbc.url";
	
	private static final String JPA_SERVER_USER = "javax.persistence.jdbc.user";
	
	private static final String JPA_SERVER_PASSWORD = "javax.persistence.jdbc.password";
	/*
	 * Application properties.
	 */
	private static final String DATABASE_PROPS_FILE = "/client.properties";
	
	private static final String DATABASE_SERVER_URL = "server.url";
	
	private static final String DATABASE_SERVER_USER = "server.user";
	
	private static final String DATABASE_SERVER_PASSWORD = "server.password";
	
	private static final String LOGGER_PROPS_FILE = "/logger.properties";
		

	private final Logger logger;
	
	private final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

	private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
	
	/*
	 * Application domain.
	 */
	private static final String PERSISTENCE_UNIT = "clinic-domain";

	private static final String SEARCH_PATIENT_QUERY = "SearchPatientByPatientId";

	private static final String SEARCH_PROVIDER_QUERY = "SearchProviderByProviderId";

	private final PatientFactory patientFactory = new PatientFactory();

	private final ProviderFactory providerFactory = new ProviderFactory();

	private final TreatmentFactory treatmentFactory = new TreatmentFactory();
	
	
	private String serverUrl;
	
	private String serverUser;
	
	private String serverPassword;
		
	private EntityManager entityManager;
	

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
	
	protected List<String> processArgs(String[] args) {
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
				serverUrl = opts.get("server");
			else if ("user".equals(k))
				serverUser = opts.get("user");
			else if ("password".equals(k))
				serverPassword = opts.get("password");
			else
				severe("Unrecognized option: --" + k);
		}

		return commandLineArgs;
	}
	


	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		try {
			new App(args);
		} catch (SecurityException | IOException e) {
			throw new IllegalStateException("Failure during initialization!", e);
		}
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

	public App(String[] args) throws SecurityException, IOException {
		
		Properties props = new Properties();
		InputStream inProps = getClass().getResourceAsStream(DATABASE_PROPS_FILE);
		props.load(inProps);
		inProps.close();
		serverUrl = (String) props.get(DATABASE_SERVER_URL);
		serverUser = (String) props.get(DATABASE_SERVER_USER);
		serverPassword = (String) props.get(DATABASE_SERVER_PASSWORD);
		/*
    	 * Process overrides from command line
    	 */
    	processArgs(args);
    	
		
		/*
		 * Configure logging.
		 */
    	LogManager logManager = LogManager.getLogManager();
    	inProps = getClass().getResourceAsStream(LOGGER_PROPS_FILE);
    	logManager.readConfiguration(inProps);
    	inProps.close();
		logger = logManager.getLogger(App.class.getCanonicalName());

    	connectToDatabase();

		try {

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

						else if ("addpatient".equals(cmd))
							addPatient();
						else if ("addprovider".equals(cmd))
							addProvider();
						else if ("addtreatment".equals(cmd))
							addTreatmentToDatabase();
						else if ("help".equals(cmd))
							help(inputs);
						else if ("quit".equals(cmd))
							return;
						else
							msgln("Bad input.  Type \"help\" for more information.");
					}
				} catch (Exception e) {
					System.out.println(e);
					severe(e);
				}
			}

		} finally {

			entityManager.close();

		}

	}
	
	private void connectToDatabase() {
		Map<String,String> properties = new HashMap<String,String>();
		properties.put(JPA_SERVER_URL, serverUrl);
		properties.put(JPA_SERVER_USER, serverUser);
		properties.put(JPA_SERVER_PASSWORD, serverPassword);
		
		EntityManagerFactory factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT, properties);
		entityManager = factory.createEntityManager();
	}

	public void addPatient() throws IOException {
		Patient patient = patientFactory.createPatient();
		patient.setPatientId(UUID.randomUUID());
		msg("Name: ");
		patient.setName(in.readLine());
		
		LocalDate dob = readDate("Patient DOB");
		patient.setDob(Date.valueOf(dob));

		/*
		 * Save the record in the database.
		 */
		entityManager.getTransaction().begin();
		entityManager.persist(patient);
		entityManager.getTransaction().commit();

	}

	public void addProvider() throws IOException {
		Provider provider = providerFactory.createProvider();
		provider.setProviderId(UUID.randomUUID());
		msg("NPI: ");
		provider.setNpi(in.readLine());
		msg("Name: ");
		provider.setName(in.readLine());

		/*
		 * Save the record in the database.
		 */
		entityManager.getTransaction().begin();
		entityManager.persist(provider);
		entityManager.getTransaction().commit();
	}
	
	/* 
	 * Use this to add a list of follow-up treatments.
	 */
	public List<Treatment> addTreatmentList() throws IOException, ParseException {
		List<Treatment> treatments = new ArrayList<>();
		Treatment treatment = addTreatment();
		while (treatment != null) {
			treatments.add(treatment);
                        treatment = addTreatment();
		}
		return treatments;
	}

	public void addTreatmentToDatabase() throws IOException, ParseException {
		Treatment treatment = addTreatment();
		if (treatment != null) {
			/*
			 * Save the record in the database.
			 */
			entityManager.getTransaction().begin();
			entityManager.persist(treatment);
			entityManager.getTransaction().commit();
		}
	}
	
	public Treatment addTreatment() throws IOException, ParseException {
		msg("What form of treatment: [D]rug, [S]urgery, [R]adiology, [P]hysiotherapy? ");
		String line = in.readLine();
		if ("D".equals(line)) {
			return addDrugTreatment();
		} else if ("S".equals(line)) {
			return addSurgeryTreatment();
		} else if ("R".equals(line)) {
			return addRadiologyTreatment();
		} else if ("P".equals(line)) {
			return addPhysiotherapyTreatment();
		}

		return null;
	}

	private RadiologyTreatment addRadiologyTreatment() throws IOException, ParseException {
		RadiologyTreatment treatment = treatmentFactory.createRadiologyTreatment();
		askCommonQuestions(treatment);

		msg("How many radiology appointments? ");
		int numberOfRadiologyAppointments = Integer.parseInt(in.readLine());
		Collection<java.util.Date> treatmentDates = new ArrayList<>();
		for (int i = 0; i < numberOfRadiologyAppointments; i++) {
			treatmentDates.add(Date.valueOf(readDate("Enter the date of your #" + i + "th appointment: ")));
		}
		treatment.setTreatmentDates(treatmentDates);
		treatment.setFollowupTreatments(addTreatmentList());

		return treatment;
	}

	private PhysiotherapyTreatment addPhysiotherapyTreatment() throws IOException {
		PhysiotherapyTreatment treatment = treatmentFactory.createPhysiotherapyTreatment();
		askCommonQuestions(treatment);

		msg("How many physiotherapy appointments? ");
		int numberOfAppointments = Integer.parseInt(in.readLine());
		Collection<java.util.Date> treatmentDates = new ArrayList<>();
		for (int i = 0; i < numberOfAppointments; i++) {
			treatmentDates.add(Date.valueOf(readDate("Enter the date of your #" + i + "th appointment: ")));
		}
		treatment.setTreatmentDates(treatmentDates);

		return treatment;
	}

	private DrugTreatment addDrugTreatment() throws IOException, ParseException {
		DrugTreatment treatment = treatmentFactory.createDrugTreatment();
		askCommonQuestions(treatment);

		msg("Drug: ");
		treatment.setDrug(in.readLine());
		
		msg("Dosage: ");
		treatment.setDosage(Float.parseFloat(in.readLine()));
		
		LocalDate startDate = readDate("Start date");
		treatment.setStartDate(Date.valueOf(startDate));
		
		LocalDate endDate = readDate("End date");
		treatment.setEndDate(Date.valueOf(endDate));
		
		msg("Frequency: ");
		treatment.setFrequency(Integer.parseInt(in.readLine()));

		return treatment;
	}

	private void askCommonQuestions(Treatment treatment) throws IOException {
		treatment.setTreatmentId(UUID.randomUUID());

		msg("Patient ID: ");
		UUID patientId = UUID.fromString(in.readLine());
		Patient patient = getPatient(patientId);
		treatment.setPatient(patient);
		patient.addTreatment(treatment);

		msg("Provider ID: ");
		UUID providerId = UUID.fromString(in.readLine());
		Provider provider = getProvider(providerId);
		treatment.setProvider(provider);
		provider.addTreatment(treatment);

		msg("Diagnosis: ");
		treatment.setDiagnosis(in.readLine());
	}

	private SurgeryTreatment addSurgeryTreatment() throws IOException, ParseException {
		SurgeryTreatment treatment = treatmentFactory.createSurgeryTreatment();

		askCommonQuestions(treatment);
		treatment.setSurgeryDate(
				Date.from(
						readDate("Date of surgery : ").atStartOfDay(ZoneId.systemDefault()).toInstant()
				)
		);

		msg("Discharge Instructions: ");
		treatment.setDischargeInstructions(in.readLine());
		treatment.setFollowupTreatments(addTreatmentList());

		return treatment;
	}

	private List<Treatment> setupFollowUpTreatments() throws IOException, ParseException {
		msg("How many follow up treatments? : ");
		int numberOfFollowupTreatments = Integer.parseInt(in.readLine());
		List<Treatment> followUpTreatments = new ArrayList<>();

		for (int i = 0; i < numberOfFollowupTreatments; i++) {
			msg("Follow up treatment #" + i);
			Treatment treatment = addTreatment();

			entityManager.getTransaction().begin();
			entityManager.persist(treatment);
			entityManager.getTransaction().commit();

			followUpTreatments.add(treatment);
		}
		return followUpTreatments;
	}

	private Patient getPatient(UUID patientId) throws IOException {
		TypedQuery<Patient> query = entityManager.createNamedQuery(SEARCH_PATIENT_QUERY, Patient.class);
		query.setParameter("patientId", patientId);
		List<Patient> results = query.getResultList();
		if (results.isEmpty()) {
			throw new IOException("Missing patient: " + patientId);
		}
		return results.get(0);
	}

	private Provider getProvider(UUID providerId) throws IOException {
		TypedQuery<Provider> query = entityManager.createNamedQuery(SEARCH_PROVIDER_QUERY, Provider.class);
		query.setParameter("providerId", providerId);
		List<Provider> results = query.getResultList();
		if (results.isEmpty()) {
			throw new IOException("Missing provider: " + providerId);
		}
		return results.get(0);
	}

	private LocalDate readDate(String field) throws IOException {
		msg(String.format("%s (MM/dd/yyyy): ", field));
		return LocalDate.parse(in.readLine(), dateFormatter);
	}

	public void help(String[] inputs) {
		if (inputs.length == 1) {
			msgln("Commands are:");
			msgln("  addpatient: add a patient");
			msgln("  addprovider: add a provider");
			msgln("  addtreatment: add a treatment");
			msgln("  quit: exit the app");
		}
	}

}
