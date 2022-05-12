package hu.gdf.thesis.backend;

public class TypeConverter {
	public static boolean tryParseInt(String value) {
		try {
			Integer.parseInt(value);
			return true;
		}catch (NumberFormatException e) {
			System.err.println("Can't convert from string to integer");
			e.printStackTrace();
			return false;
		}
	}
	public static boolean tryParseBool(String value) {
		try {
			Boolean.parseBoolean(value);
			return true;
		}catch (NumberFormatException e) {
			System.err.println("Can't convert from string to boolean");
			e.printStackTrace();
			return false;
		}
	}
}