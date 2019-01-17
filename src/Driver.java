import java.io.BufferedWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Parses command line arguments and creates a inverted of stemmed words and
 * their respective file paths and positions the word was found
 * 
 * @author Rugved Mavidipalli
 */
public class Driver {
	/**
	 * Parses the command-line arguments to build and use an in-memory search engine
	 * from files or the web.
	 * 
	 * @param args the command-line arguments to parse
	 * @return 0 if everything went well
	 */
	public static void main(String[] args) {
		ArgumentMap argument = new ArgumentMap(args);
		QueryParserInterface query;
		InvertedIndex index;

		/* Multithreaded code */
		if (argument.hasFlag("-port") == true) {
			int port = Integer.parseInt(argument.getString("-port"));
			if (Integer.parseInt(argument.getString("-port")) < 1) {
				port = 8080;
			}
			int threads = 5;
			if (argument.hasFlag("-threads") == true) {
				threads = Integer.parseInt(argument.getString("-threads"));
				if (threads < 1) {
					threads = 5;
				}
			}
			ThreadSafeIndex threadIndex = new ThreadSafeIndex();
			index = threadIndex;
			WorkQueue worker = new WorkQueue(threads);
			query = new SearchResultThreaded(threadIndex, worker);
			CrawlerThreaded crawler = new CrawlerThreaded(threadIndex, worker);
			WebServer website = new WebServer(threadIndex, crawler);
			if (argument.hasFlag("-path") == true) {
				Path path = argument.getPath("-path");
				try {
					InvertedIndexBuilderThread.addFiles(path, threadIndex, worker);
				} catch (NullPointerException e) {
					System.err.println("Please provide a value with the -path flag.");
				}
			}
			if (argument.hasFlag("-url") == true) {
				try {
					crawler.processCrawl(argument.getString("-url"), Integer.parseInt(argument.getString("-limit")));
				} catch (NumberFormatException e) {
					System.err.println("Invalid limit" + argument.getString("-limit") + "please provide a valid url");
				} catch (MalformedURLException e) {
					System.err.println("Invalid url" + argument.getString("-url") + "please provide a valid url");
				}
			}
			website.createSite(port);
		} else {
			/* Single threaded code */
			index = new InvertedIndex();
			query = new QueryParser(index);
			if (argument.hasFlag("-path") == true) {
				Path path = argument.getPath("-path");
				try {
					InvertedIndexBuilder.addFiles(path, index);
				} catch (NullPointerException e) {
					System.err.println("Please provide a value with the -path flag.");
				} catch (NoSuchFileException e) {
					System.err.println("Could not find path: " + path);
				} catch (IOException e) {
					System.err.println("Could not build index from path: " + path);
				}
			}
		}
		if (argument.hasFlag("-search") == true) {
			try {
				query.addQueryFile(argument.getPath("-search"), argument.hasFlag("-exact"));
			} catch (Exception e) {
				System.err.println("Could not search from path: " + argument.getPath("-search"));
			}
		}
		if (argument.hasFlag("-index") == true) {
			Path output = argument.getPath("-index", Paths.get("index.json"));
			try (BufferedWriter writer = Files.newBufferedWriter(output, StandardCharsets.UTF_8);) {
				index.sendIndex(writer);
			} catch (IOException e) {
				System.err.println("Unable to write index to JSON file: " + output);
			}
		}
		if (argument.hasFlag("-results") == true) {
			Path output = argument.getPath("-results", Paths.get("results.json"));
			try (BufferedWriter writer = Files.newBufferedWriter(output, StandardCharsets.UTF_8);) {
				query.sendResult(writer);
			} catch (IOException e) {
				System.err.println("Unable to write index to JSON file: " + output);
			}
		}
		if (argument.hasFlag("-locations") == true) {
			Path output = argument.getPath("-locations", Paths.get("locations.json"));
			try (BufferedWriter writer = Files.newBufferedWriter(output, StandardCharsets.UTF_8);) {
				index.sendLocations(writer);
			} catch (IOException e) {
				System.err.println("Unable to write index to JSON file: " + output);
			}
		}
	}
}
