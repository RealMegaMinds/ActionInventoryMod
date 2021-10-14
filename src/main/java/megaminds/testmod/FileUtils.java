package megaminds.testmod;

public class FileUtils {
	private static final String EXT = ".json";
	
	public static String addExtension(String file) {
		return file.endsWith(EXT) ? file : file + EXT;
	}
	
	public static String stripExtension(String file) {
		return file.endsWith(EXT) ? file.substring(0, file.length()-EXT.length()) : file;
	}
}