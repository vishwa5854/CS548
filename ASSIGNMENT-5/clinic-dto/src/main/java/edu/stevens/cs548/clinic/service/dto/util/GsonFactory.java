package edu.stevens.cs548.clinic.service.dto.util;

import java.time.LocalDate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import edu.stevens.cs548.clinic.service.dto.DrugTreatmentDto;
import edu.stevens.cs548.clinic.service.dto.TreatmentDto;

public class GsonFactory {
	
	public static final String TYPE_TAG = "type-tag";
	
	public static final String SURGERY_TAG = "SURGERY";
	
	public static final String DRUGTREATMENT_TAG = "DRUGTREATMENT";
	
	public static final String RADIOLOGY_TAG = "RADIOLOGY";
	
	public static final String PHYSIOTHERAPY_TAG = "PHYSIOTHERAPY";

	
	public static Gson createGson() {
		
		RuntimeTypeAdapterFactory<TreatmentDto> adapterFactory = 
			RuntimeTypeAdapterFactory.of(TreatmentDto.class, TYPE_TAG)
				.registerSubtype(DrugTreatmentDto.class, DRUGTREATMENT_TAG)
					.registerSubtype(DrugTreatmentDto.class, SURGERY_TAG)
					.registerSubtype(DrugTreatmentDto.class, RADIOLOGY_TAG)
					.registerSubtype(DrugTreatmentDto.class, PHYSIOTHERAPY_TAG)

				// TODO Register other DTO classes with adapter factory.
				;
		
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.setPrettyPrinting()
		           .registerTypeAdapter(LocalDate.class, new LocalDateSerializer())
		           .registerTypeAdapterFactory(adapterFactory);			
		
		return gsonBuilder.create();
	}

}
