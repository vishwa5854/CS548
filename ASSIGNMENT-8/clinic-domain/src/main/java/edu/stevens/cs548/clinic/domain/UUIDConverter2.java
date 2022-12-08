package edu.stevens.cs548.clinic.domain;

import java.util.UUID;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

// @Converter
public class UUIDConverter2 implements AttributeConverter<UUID,UUID>{

	@Override
	public UUID convertToDatabaseColumn(UUID attribute) {
		return attribute;
	}

	@Override
	public UUID convertToEntityAttribute(UUID dbData) {
		return dbData;
	}

}
