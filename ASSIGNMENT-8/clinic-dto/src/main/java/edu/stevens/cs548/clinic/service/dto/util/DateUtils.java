package edu.stevens.cs548.clinic.service.dto.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;

public class DateUtils {
	
	/*
	 * https://stackoverflow.com/a/40143687
	 */
	
	private final static ZoneId ZoneId = ZoneOffset.UTC;
	
	public static Date toDatabaseDate(LocalDate date) {
		Instant instant = date.atStartOfDay(ZoneId).toInstant();
		return Date.from(instant);
	}
	
	public static LocalDate fromDatabaseDate(Date date) {
		Instant instant = date.toInstant();
		return instant.atZone(ZoneId).toLocalDate();
	}

}
