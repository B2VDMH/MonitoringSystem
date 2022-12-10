package hu.gdf.thesis.backend;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TypeConverter {

	//If provided String value can be converted to int, return true
	public static boolean tryParseInt(String value) {
		try {
			Integer.parseInt(value);
			return true;
		} catch (NumberFormatException ex) {
			log.error("Could not convert from STRING to INTEGER, provided value: " + value, ex);
			return false;
		}
	}

	//If provided String value can be converted to boolean, return true
	public static boolean tryParseBool(String value) {
		try {
			Boolean.parseBoolean(value);
			return true;
		} catch (Exception ex) {
			log.error("Could not convert from STRING to BOOLEAN, provided value: " + value, ex);
			return false;
		}
	}
}