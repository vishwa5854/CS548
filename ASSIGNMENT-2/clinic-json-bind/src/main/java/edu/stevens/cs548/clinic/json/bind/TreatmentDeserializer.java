package edu.stevens.cs548.clinic.json.bind;

import java.io.IOException;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;

import edu.stevens.cs548.clinic.service.dto.*;
import edu.stevens.cs548.clinic.service.dto.TreatmentDto.TreatmentType;

/**
 * Class for deserializing a treatment.
 * 
 * The gson.fromJson operation requires a specification of the (concrete) type of the 
 * object being deserialized, which of course is not known until we are reading the input.
 * We parse the data into a JsonElement object, read the tag on that, then deserialize 
 * the object into a Java object of the concrete class.
 * 
 * We cannot register this as custom deserialization logic in Gson because that would lead
 * to a cycle where attempting to deserialize a treatment causes the custom deserializer to 
 * trigger deserialization on the same object.
 * 
 * @author dduggan
 *
 */
public class TreatmentDeserializer {
	
	private Gson gson;
			
	private TypeAdapter<JsonElement> jsonElementAdapter;
	
	public static TreatmentDeserializer getTreatmentDeserializer(Gson gson) {
		return new TreatmentDeserializer(gson);
	}
	
	private TreatmentDeserializer(Gson gson) {
		this.gson = gson;
		this.jsonElementAdapter = gson.getAdapter(JsonElement.class);
	}
	
	/*
	 * Deserialization has go to in two stages: parse the JSON data to an untyped
	 * JSON object, then examine the type tag to see what kind of object it is,
	 * and build the concreteinstance of TreatmentDto.
	 */
	public TreatmentDto deserialize(JsonReader rd) throws JsonParseException, IOException {
		return deserialize(parse(rd));
	}
	
	/*
	 * Parse the input stream into an untyped JSON object.
	 */
	private JsonElement parse(JsonReader rd) throws JsonParseException, IOException {
		JsonElement json = jsonElementAdapter.read(rd);
		return json;
	}
	
	/*
	 * Deserialize an untyped JSON object into a treatment DTO.
	 */
	private TreatmentDto deserialize(JsonElement json)
			throws JsonParseException {

		if (!json.isJsonObject()) {
			throw new JsonParseException("Non-object in token stream where treatment DTO expected: "+json);
		}
		
		if (!json.getAsJsonObject().has(TreatmentDto.TYPE_TAG)) {
			throw new JsonParseException("Missing type tag for treatment DTO: "+json);
		}
		
		JsonElement typeElem = json.getAsJsonObject().get(TreatmentDto.TYPE_TAG);
		if (!typeElem.isJsonPrimitive()) {
			throw new JsonParseException("Type tag for treatment is not primitive: "+typeElem);
		}
		if (!typeElem.getAsJsonPrimitive().isString()) {
			throw new JsonParseException("Type tag for treatment is not a string: "+typeElem);
		}
		if (!TreatmentType.isValid(typeElem.getAsJsonPrimitive().getAsString())) {
			throw new JsonParseException("Type tag for treatment is not a valid tag value: "+typeElem);
		}
		TreatmentType typeTag = TreatmentType.parse(typeElem.getAsJsonPrimitive().getAsString());

		if (typeTag.name().equals(TreatmentType.SURGERY.name())) {
			return this.gson.fromJson(json, SurgeryTreatmentDto.class);
		} else if (typeTag.name().equals((TreatmentType.RADIOLOGY.name()))) {
			return this.gson.fromJson(json, RadiologyTreatmentDto.class);
		} else if (typeTag.name().equals(TreatmentType.DRUGTREATMENT.name())) {
			return this.gson.fromJson(json, DrugTreatmentDto.class);
		} else if (typeTag.name().equals(TreatmentType.PHYSIOTHERAPY.name())) {
			return this.gson.fromJson(json, PhysiologyTreatmentDto.class);
		}

		throw new IllegalStateException("Unimplemented TreatmentDeserializer");
	}
}
