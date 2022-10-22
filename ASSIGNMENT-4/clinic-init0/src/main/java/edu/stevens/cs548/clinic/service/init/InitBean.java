package edu.stevens.cs548.clinic.service.init;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import edu.stevens.cs548.clinic.domain.*;

@Singleton
 @LocalBean
 @Startup
// @ApplicationScoped
// @Transactional
public class InitBean implements ITreatmentExporter<Void>{

	private static final Logger logger = Logger.getLogger(InitBean.class.getCanonicalName());
	
	private static final ZoneId ZONE_ID = ZoneOffset.UTC;
	
	private PatientFactory patientFactory = new PatientFactory();
	
	private ProviderFactory providerFactory = new ProviderFactory();
	
	private TreatmentFactory treatmentFactory = new TreatmentFactory();

	// TODO
	@Inject
	private IPatientDao patientDao;
	
	// TODO
	@Inject
	private IProviderDao providerDao;
	
	/*
	 * Initialize using EJB logic
	 */
	@PostConstruct
	/*
	 * This should work to initialize with CDI bean, but there is a bug in Payara.....
	 */
	// public void init(@Observes @Initialized(ApplicationScoped.class) ServletContext init) {
	public void init() {
		/*
		 * Put your testing logic here. Use the logger to display testing output in the server logs.
		 */
		logger.info("Kashi Vishwanath Bondugula");
		System.err.println("Kashi Vishwanath Bondugula");

		try {
			
			/*   
			 * Clear the database and populate with fresh data.
			 * 
			 * If we ensure that deletion of patients cascades deletes of treatments,
			 * then we only need to delete patients.
			 */
			
			patientDao.deletePatients();
			providerDao.deleteProviders();
			
			Patient john = patientFactory.createPatient();
			john.setPatientId(UUID.randomUUID());
			john.setName("John Doe");
			john.setDob(LocalDate.parse("1995-08-15"));
			patientDao.addPatient(john);
			
			Provider jane = providerFactory.createProvider();
			jane.setProviderId(UUID.randomUUID());
			jane.setName("Jane Doe");
			jane.setNpi("1234");
			providerDao.addProvider(jane);
			
			DrugTreatment drug01 = treatmentFactory.createDrugTreatment();
			drug01.setTreatmentId(UUID.randomUUID());
			drug01.setDiagnosis("Headache");
			drug01.setDrug("Aspirin");
			drug01.setStartDate(LocalDate.ofInstant(Instant.now(), ZONE_ID));
			drug01.setEndDate(LocalDate.ofInstant(Instant.now(), ZONE_ID));
			
			jane.addTreatment(john, drug01);


			
			// TODO add more testing, including treatments and providers

			Patient one = patientFactory.createPatient();
			one.setPatientId(UUID.randomUUID());
			one.setName("One");
			one.setDob(LocalDate.parse("1995-11-15"));
			patientDao.addPatient(one);

			Provider pone = providerFactory.createProvider();
			pone.setProviderId(UUID.randomUUID());
			pone.setName("Pone");
			pone.setNpi("12234");
			providerDao.addProvider(pone);

			SurgeryTreatment oneST = treatmentFactory.createSurgeryTreatment();
			oneST.setTreatmentId(UUID.randomUUID());
			oneST.setDiagnosis("Stomach Ache");
			oneST.setDischargeInstructions("Just send him home already");
			oneST.setSurgeryDate(LocalDate.ofInstant(Instant.now(), ZONE_ID));

			PhysiotherapyTreatment onePT = treatmentFactory.createPhysiotherapyTreatment();
			onePT.setTreatmentId(UUID.randomUUID());
			onePT.setDiagnosis("Stomach Ache");
			onePT.addTreatmentDate(LocalDate.ofInstant(Instant.now(), ZONE_ID));
			oneST.addFollowupTreatment(onePT);

			pone.addTreatment(one, oneST);
			pone.addTreatment(one, onePT);


			RadiologyTreatment oneRT = treatmentFactory.createRadiologyTreatment();
			oneRT.setTreatmentId(UUID.randomUUID());
			oneRT.setDiagnosis("Stomach Ache");
			oneRT.addTreatmentDate(LocalDate.ofInstant(Instant.now(), ZONE_ID));

			pone.addTreatment(one, oneRT);

			// Now show in the logs what has been added
			
			Collection<Patient> patients = patientDao.getPatients();
			for (Patient p : patients) {
				String dob = p.getDob().toString();
				logger.info(String.format("Patient %s, ID %s, DOB %s", p.getName(), p.getPatientId().toString(), dob));
				p.exportTreatments(this);
			}
			
			Collection<Provider> providers = providerDao.getProviders();
			for (Provider p : providers) {
				logger.info(String.format("Provider %s, ID %s", p.getName(), p.getProviderId().toString()));
				p.exportTreatments(this);
			}
			
		} catch (Exception e) {
;
			throw new IllegalStateException("Failed to add record.", e);
			
		} 
			
	}
	
//	public void shutdown(@Observes @Destroyed(ApplicationScoped.class) ServletContext init) {
//		logger.info("App shutting down....");
//	}
	
	@Override
	public Void exportDrugTreatment(UUID tid, UUID patientId, UUID providerId, String diagnosis, String drug,
			float dosage, LocalDate start, LocalDate end, int frequency, Collection<Void> followups) {
		logger.info(String.format("...Drug treatment %s, drug %s", tid.toString(), drug));
		return null;
	}

	@Override
	public Void exportRadiology(UUID tid, UUID patientId, UUID providerId, String diagnosis, List<LocalDate> dates,
			Collection<Void> followups) {
		logger.info(String.format("...Radiology treatment %s", tid.toString()));
		return null;
	}

	@Override
	public Void exportSurgery(UUID tid, UUID patientId, UUID providerId, String diagnosis, LocalDate date,
			Collection<Void> followups) {
		logger.info(String.format("...Surgery treatment %s", tid.toString()));
		return null;
	}

	@Override
	public Void exportPhysiotherapy(UUID tid, UUID patientId, UUID providerId, String diagnosis, List<LocalDate> dates, Collection<Void> followups) {
		logger.info(String.format("...Physiotherapy treatment %s", tid.toString()));
		return null;
	}


}
