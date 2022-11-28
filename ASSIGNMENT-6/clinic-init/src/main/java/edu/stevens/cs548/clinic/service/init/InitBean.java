package edu.stevens.cs548.clinic.service.init;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Destroyed;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.servlet.ServletContext;

import edu.stevens.cs548.clinic.service.IPatientService;
import edu.stevens.cs548.clinic.service.IProviderService;
import edu.stevens.cs548.clinic.service.dto.DrugTreatmentDto;
import edu.stevens.cs548.clinic.service.dto.PatientDto;
import edu.stevens.cs548.clinic.service.dto.PatientDtoFactory;
import edu.stevens.cs548.clinic.service.dto.PhysiotherapyTreatmentDto;
import edu.stevens.cs548.clinic.service.dto.ProviderDto;
import edu.stevens.cs548.clinic.service.dto.ProviderDtoFactory;
import edu.stevens.cs548.clinic.service.dto.RadiologyTreatmentDto;
import edu.stevens.cs548.clinic.service.dto.SurgeryTreatmentDto;
import edu.stevens.cs548.clinic.service.dto.TreatmentDto;
import edu.stevens.cs548.clinic.service.dto.TreatmentDtoFactory;

@Singleton
@LocalBean
@Startup
// @ApplicationScoped
// @Transactional
public class InitBean {

	private static final Logger logger = Logger.getLogger(InitBean.class.getCanonicalName());

	private static final ZoneId ZONE_ID = ZoneOffset.UTC;

	private PatientDtoFactory patientFactory = new PatientDtoFactory();

	private ProviderDtoFactory providerFactory = new ProviderDtoFactory();

	private TreatmentDtoFactory treatmentFactory = new TreatmentDtoFactory();

	// TODO
	@Inject
	private IPatientService patientService;

	// TODO
	@Inject
	private IProviderService providerService;

	/*
	 * Initialize using EJB logic
	 */
	@PostConstruct
	/*
	 * This should work to initialize with CDI bean, but there is a bug in
	 * Payara.....
	 */
	// public void init(@Observes @Initialized(ApplicationScoped.class)
	// ServletContext init) {
	public void init() {
		/*
		 * Put your testing logic here. Use the logger to display testing output in the
		 * server logs.
		 */
		logger.info("Kashi Vishwanath Bondugula");
		System.err.println("Kashi Vishwanath Bondugula");

		try {

			/*
			 * Clear the database and populate with fresh data.
			 * 
			 * Note that the service generates the external ids, when adding the entities.
			 */

			providerService.removeAll();
			patientService.removeAll();

			PatientDto john = patientFactory.createPatientDto();
			john.setName("John Doe");
			john.setDob(LocalDate.parse("1995-08-15"));

			john.setId(patientService.addPatient(john));

			ProviderDto jane = providerFactory.createProviderDto();
			jane.setName("Jane Doe");
			jane.setNpi("1234");

			jane.setId(providerService.addProvider(jane));

			DrugTreatmentDto drug01 = treatmentFactory.createDrugTreatmentDto();
			drug01.setPatientId(john.getId());
			drug01.setPatientName(john.getName());
			drug01.setProviderId(jane.getId());
			drug01.setProviderName(jane.getName());
			drug01.setDiagnosis("Headache");
			drug01.setDrug("Aspirin");
			drug01.setDosage(20);
			drug01.setFrequency(7);
			drug01.setStartDate(LocalDate.ofInstant(Instant.now(), ZONE_ID));
			drug01.setEndDate(LocalDate.ofInstant(Instant.now(), ZONE_ID));

			providerService.addTreatment(drug01);

			// TODO add more testing, including treatments and providers
			PatientDto mary = patientFactory.createPatientDto();
			mary.setName("Mary Jane");
			mary.setDob((LocalDate.parse("1985-08-15")));

			PatientDto tom = patientFactory.createPatientDto();
			tom.setName("Tom Keen");
			tom.setDob((LocalDate.parse("1885-08-15")));

			tom.setId(patientService.addPatient(tom));
			mary.setId(patientService.addPatient(mary));

			ProviderDto star = providerFactory.createProviderDto();
			star.setName("Star");
			star.setNpi("45678");
			star.setId(providerService.addProvider(star));

			ProviderDto reddington = providerFactory.createProviderDto();
			reddington.setName("Raymond Reddington");
			reddington.setNpi("1244");

			reddington.setId(providerService.addProvider(reddington));

			SurgeryTreatmentDto surgeryTreatment1 = treatmentFactory.createSurgeryTreatmentDto();
			surgeryTreatment1.setPatientId(mary.getId());
			surgeryTreatment1.setPatientName(mary.getName());
			surgeryTreatment1.setProviderId(star.getId());
			surgeryTreatment1.setProviderName(star.getName());
			surgeryTreatment1.setDiagnosis("Headache");
			surgeryTreatment1.setSurgeryDate(LocalDate.ofInstant(Instant.now(), ZONE_ID));
			surgeryTreatment1.setDischargeInstructions("Nothing bruh");

			RadiologyTreatmentDto radiologyTreatment1 = treatmentFactory.createRadiologyTreatmentDto();
			radiologyTreatment1.setPatientId(mary.getId());
			radiologyTreatment1.setPatientName(mary.getName());
			radiologyTreatment1.setProviderId(star.getId());
			radiologyTreatment1.setProviderName(star.getName());
			radiologyTreatment1.setDiagnosis("radiology treatment diagnosis");

			List<LocalDate> treatmentDatesMary = new ArrayList<>();
			List<TreatmentDto> followUpTreatmentsMary = new ArrayList<>();

			treatmentDatesMary.add(LocalDate.ofInstant(Instant.now(), ZONE_ID));
			treatmentDatesMary.add(LocalDate.ofInstant(Instant.now(), ZONE_ID));
			radiologyTreatment1.setTreatmentDates(treatmentDatesMary);

			followUpTreatmentsMary.add(radiologyTreatment1);

			PhysiotherapyTreatmentDto physiotherapyTreatment1 = treatmentFactory.createPhysiotherapyTreatmentDto();
			physiotherapyTreatment1.setPatientId(mary.getId());
			physiotherapyTreatment1.setPatientName(mary.getName());
			physiotherapyTreatment1.setProviderId(star.getId());
			physiotherapyTreatment1.setProviderName(star.getName());
			physiotherapyTreatment1.setDiagnosis("Blah");
			physiotherapyTreatment1.setTreatmentDates(treatmentDatesMary);

			followUpTreatmentsMary.add((physiotherapyTreatment1));

			surgeryTreatment1.setFollowupTreatments(followUpTreatmentsMary);
			providerService.addTreatment(surgeryTreatment1);

			SurgeryTreatmentDto surgeryTreatment2 = treatmentFactory.createSurgeryTreatmentDto();
			surgeryTreatment2.setPatientId(tom.getId());
			surgeryTreatment2.setPatientName(tom.getName());
			surgeryTreatment2.setProviderId(reddington.getId());
			surgeryTreatment2.setProviderName(reddington.getName());
			surgeryTreatment2.setDiagnosis("Nausea");
			surgeryTreatment2.setSurgeryDate(LocalDate.ofInstant(Instant.now(), ZONE_ID));
			surgeryTreatment2.setDischargeInstructions("Something ig :(");
			providerService.addTreatment(surgeryTreatment2);

			RadiologyTreatmentDto radiologyTreatment2 = treatmentFactory.createRadiologyTreatmentDto();
			radiologyTreatment2.setPatientId(tom.getId());
			radiologyTreatment2.setPatientName(tom.getName());
			radiologyTreatment2.setProviderId(star.getId());
			radiologyTreatment2.setProviderName(star.getName());
			radiologyTreatment2.setDiagnosis("radiology treatment diagnosis for tom");
			radiologyTreatment2.setTreatmentDates(treatmentDatesMary);
			providerService.addTreatment(radiologyTreatment2);

			PhysiotherapyTreatmentDto physiotherapyTreatment2 = treatmentFactory.createPhysiotherapyTreatmentDto();
			physiotherapyTreatment2.setPatientId(john.getId());
			physiotherapyTreatment2.setPatientName(john.getName());
			physiotherapyTreatment2.setProviderId(reddington.getId());
			physiotherapyTreatment2.setProviderName(reddington.getName());
			physiotherapyTreatment2.setDiagnosis("This guy is legit");
			physiotherapyTreatment2.setTreatmentDates(treatmentDatesMary);
			providerService.addTreatment(physiotherapyTreatment2);

			PhysiotherapyTreatmentDto physiotherapyTreatment3 = treatmentFactory.createPhysiotherapyTreatmentDto();
			physiotherapyTreatment3.setPatientId(john.getId());
			physiotherapyTreatment3.setPatientName(john.getName());
			physiotherapyTreatment3.setProviderId(jane.getId());
			physiotherapyTreatment3.setProviderName(jane.getName());
			physiotherapyTreatment3.setDiagnosis("What can we say?");
			physiotherapyTreatment3.setTreatmentDates(treatmentDatesMary);
			providerService.addTreatment(physiotherapyTreatment3);

			RadiologyTreatmentDto radiologyTreatment23 = treatmentFactory.createRadiologyTreatmentDto();
			radiologyTreatment23.setPatientId(tom.getId());
			radiologyTreatment23.setPatientName(tom.getName());
			radiologyTreatment23.setProviderId(reddington.getId());
			radiologyTreatment23.setProviderName(reddington.getName());
			radiologyTreatment23.setDiagnosis("radiology treatment diagnosis for tom");
			radiologyTreatment23.setTreatmentDates(treatmentDatesMary);

			List<TreatmentDto> followUpTreatmentsTom = new ArrayList<>();

			PhysiotherapyTreatmentDto physiotherapyTreatment31 = treatmentFactory.createPhysiotherapyTreatmentDto();
			physiotherapyTreatment31.setPatientId(tom.getId());
			physiotherapyTreatment31.setPatientName(tom.getName());
			physiotherapyTreatment31.setProviderId(reddington.getId());
			physiotherapyTreatment31.setProviderName(reddington.getName());
			physiotherapyTreatment31.setDiagnosis("What can we say mannnn?");
			physiotherapyTreatment31.setTreatmentDates(treatmentDatesMary);
			followUpTreatmentsTom.add(physiotherapyTreatment31);

			radiologyTreatment23.setFollowupTreatments(followUpTreatmentsTom);
			providerService.addTreatment(radiologyTreatment23);
			// Now show in the logs what has been added

			Collection<PatientDto> patients = patientService.getPatients();
			for (PatientDto p : patients) {
				logger.info(String.format("Patient %s, ID %s, DOB %s", p.getName(), p.getId().toString(),
						p.getDob().toString()));
				logTreatments(p.getTreatments());
			}

			Collection<ProviderDto> providers = providerService.getProviders();
			for (ProviderDto p : providers) {
				logger.info(String.format("Provider %s, ID %s, NPI %s", p.getName(), p.getId().toString(), p.getNpi()));
				logTreatments(p.getTreatments());
			}

		} catch (Exception e) {

			throw new IllegalStateException("Failed to add record.", e);

		}
		
	}

	public void shutdown(@Observes @Destroyed(ApplicationScoped.class) ServletContext init) {
		logger.info("App shutting down....");
	}

	private void logTreatments(Collection<TreatmentDto> treatments) {
		for (TreatmentDto treatment : treatments) {
			if (treatment instanceof DrugTreatmentDto) {
				logTreatment((DrugTreatmentDto) treatment);
			} else if (treatment instanceof SurgeryTreatmentDto) {
				logTreatment((SurgeryTreatmentDto) treatment);
			} else if (treatment instanceof RadiologyTreatmentDto) {
				logTreatment((RadiologyTreatmentDto) treatment);
			} else if (treatment instanceof PhysiotherapyTreatmentDto) {
				logTreatment((PhysiotherapyTreatmentDto) treatment);
			}
			if (!treatment.getFollowupTreatments().isEmpty()) {
				logger.info("============= Follow-up Treatments");
				logTreatments(treatment.getFollowupTreatments());
				logger.info("============= End Follow-up Treatments");
			}
		}
	}

	private void logTreatment(DrugTreatmentDto t) {
		logger.info(String.format("...Drug treatment for %s, drug %s", t.getPatientName(), t.getDrug()));
	}

	private void logTreatment(RadiologyTreatmentDto t) {
		logger.info(String.format("...Radiology treatment for %s", t.getPatientName()));
	}

	private void logTreatment(SurgeryTreatmentDto t) {
		logger.info(String.format("...Surgery treatment for %s", t.getPatientName()));
	}

	private void logTreatment(PhysiotherapyTreatmentDto t) {
		logger.info(String.format("...Physiotherapy treatment for %s", t.getPatientName()));
	}

}
