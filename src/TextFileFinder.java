import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 * @author rmavidipalli TextFileFinder Class looks for text files in a given
 *         path.
 */
public class TextFileFinder {

	/**
	 * Checks if a given path is a directory.
	 * 
	 * @param path
	 * @return true if the path is a flag
	 */
	public static boolean isDirectory(Path path) {
		return Files.isDirectory(path);
	}

	/**
	 * Checks if a given path is a file
	 * 
	 * @param path
	 * @return true if the path is a file
	 */
	public static boolean isFile(Path path) {
		if (Files.isRegularFile(path) == true) {
			return true;
		}
		return false;
	}

	/**
	 * Checks if a given path is a text file
	 * 
	 * @param path
	 * @return true if path is a text file
	 */
	public static boolean isTextFile(Path path) {
		String name = path.getFileName().toString().toLowerCase();
		if (name.endsWith(".text") || name.endsWith(".txt")) {
			return true;
		}

		return false;

	}

	/**
	 * Finds the text files in a given path. If a directory is found it traverses
	 * the directory to find all text files. Returns a list of paths as an array
	 * list.
	 * 
	 * @param Path path
	 * @return ArrayList<Path> of text files
	 * @throws IOException
	 */
	public static ArrayList<Path> findTextFiles(Path path) throws IOException {
		ArrayList<Path> paths = new ArrayList<>();

		if (isDirectory(path) == true) {
			try (DirectoryStream<Path> listing = Files.newDirectoryStream(path);) {
				for (Path file : listing) {
					paths.addAll(findTextFiles(file));
				}
			}
		} else {
			if (isTextFile(path) == true) {
				paths.add(path);
			}
		}

		return paths;
	}

}
