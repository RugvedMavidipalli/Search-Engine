import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * QueryParserInterface consists of methods being implimented by queryParser and
 * SearchResult Threaded
 * 
 * @author rugvedmavidipalli
 *
 */
public interface QueryParserInterface {
	/**
	 * Default implimentation of the method addQueryFile
	 * 
	 * @param path
	 * @param exact
	 * @throws IOException
	 */
	public default void addQueryFile(Path path, boolean exact) throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				addQueryLine(line, exact);
			}
		}
	}

	/**
	 * cleans, parses, stemms, and searches for a given line
	 * 
	 * @param line
	 * @param exact
	 */
	public void addQueryLine(String line, boolean exact);

	/**
	 * Sends results to the JsonWriter
	 * 
	 * @param writer
	 * @throws IOException
	 */
	public void sendResult(Writer writer) throws IOException;
}
