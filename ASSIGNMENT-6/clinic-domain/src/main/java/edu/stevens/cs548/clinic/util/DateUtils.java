package edu.stevens.cs548.clinic.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DateUtils {
	
	/*
	 * https://stackoverflow.com/a/40143687
	 */
	
	private final static ZoneId ZoneId = ZoneOffset.UTC;
	
	public static Date toDatabaseDate(LocalDate date) {
		Instant instant = date.atStartOfDay(ZoneId).toInstant();
		return Date.from(instant);
	}
	
	/*
	 * Why does java.sql.date subclass java.util.date when they are not compatible???
	 */
	public static LocalDate fromDatabaseDate(Date date) {
		if (date instanceof java.sql.Date) {
			return ((java.sql.Date) date).toLocalDate();
		} else {
			Instant instant = date.toInstant();
			return instant.atZone(ZoneId).toLocalDate();
		}
	}
	
	public static List<LocalDate> fromDatabaseDates(List<Date> dates) {
		List<LocalDate> localDates = new ArrayList<>();
		for (Date date : dates) {
			localDates.add(fromDatabaseDate(date));
		}
		return localDates;
		// return List.copyOf(dates);
	}

}
