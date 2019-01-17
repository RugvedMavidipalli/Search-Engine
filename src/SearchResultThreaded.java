import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author rugvedmavidipalli
 *
 */
public class SearchResultThreaded implements QueryParserInterface {

	private final WorkQueue worker;
	private final TreeMap<String, ArrayList<SearchResult>> results;
	private final ThreadSafeIndex index;
	public static final Logger log = LogManager.getLogger();

	/**
	 * Initializes the threaded version of QueryParser. Takes a thread safe index
	 * and an instance of the work queue
	 * 
	 * @param index
	 * @param worker
	 */
	public SearchResultThreaded(ThreadSafeIndex index, WorkQueue worker) {
		this.results = new TreeMap<String, ArrayList<SearchResult>>();
		this.worker = worker;
		this.index = index;
		log.debug("SearchResultThreaded started");
		log.info("SearchResultThreaded started");
	}

	/**
	 * Parses and searches the InvertedIndex for a given query based on the selected
	 * search
	 * 
	 * @param path  file path to read queries from
	 * @param exact if exact search has to be performed
	 * @throws IOException
	 */
	@Override
	public void addQueryFile(Path path, boolean exact) throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				worker.execute(new Find(exact, line));
			}
		} finally {
			worker.finish();
		}
	}

	/**
	 * Sends results to TreeJSONWriter
	 * 
	 * @param writer
	 * @throws IOException
	 */
	@Override
	public void sendResult(Writer writer) throws IOException {
		synchronized (results) {
			TreeJSONWriter.asNestedObjectSearch(this.results, writer);
			log.debug("SENT results");
		}
	}

	/**
	 * Performs search for a given line of query
	 *
	 */
	public class Find implements Runnable {
		private final boolean exact;
		private final String queryString;

		public Find(boolean exact, String queryString) {
			this.exact = exact;
			this.queryString = queryString;
		}

		@Override
		public void run() {
			addQueryLine(queryString, exact);
		}
	}

	/*
	 * @see QueryParserInterface#addQueryLine(String, boolean)
	 */
	@Override
	public void addQueryLine(String line, boolean exact) {
		TreeSet<String> sortedWords = TextFileStemmer.clean(line);
		if (sortedWords != null) {
			String query = String.join(" ", sortedWords);
			log.debug("Working on Search {}", query);
			synchronized (results) {
				if (results.containsKey(query)) {
					return;
				}
			}
			ArrayList<SearchResult> localresults = new ArrayList<SearchResult>();
			if (exact) {
				localresults.addAll(index.exactSearch(sortedWords));
			} else {
				localresults.addAll(index.partialSearch(sortedWords));
			}
			synchronized (results) {
				results.putIfAbsent(query, localresults);
			}
		}
	}
}
