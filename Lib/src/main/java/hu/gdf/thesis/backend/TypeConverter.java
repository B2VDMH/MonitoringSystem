package hu.gdf.thesis.backend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TypeConverter {
	private static final Logger LOGGER = LoggerFactory.getLogger(TypeConverter.class);
	public static boolean tryParseInt(String value) {
		try {
			Integer.parseInt(value);
			return true;
		}catch (NumberFormatException ex) {
			LOGGER.error("Could not convert from STRING to INTEGER, provided value: " + value, ex);
			return false;
		}
	}
	public static boolean tryParseBool(String value) {
		try {
			Boolean.parseBoolean(value);
			return true;
		}catch (NumberFormatException ex) {
			LOGGER.error("Could not convert from STRING to BOOLEAN, provided value: " + value, ex);
			return false;
		}
	}
}