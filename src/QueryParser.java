import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

public class QueryParser implements QueryParserInterface {
	/**
	 * Initializes the InvertedIndex of queries and results
	 */
	private final TreeMap<String, ArrayList<SearchResult>> resultMap;
	private final InvertedIndex index;

	/**
	 * Initializes resultMap and InvertedIndex
	 */
	public QueryParser(InvertedIndex index) {
		this.resultMap = new TreeMap<String, ArrayList<SearchResult>>();
		this.index = index;
	}

	/**
	 * cleans the line, parses it,and searches the line and adds the results into
	 * the resultMap
	 * 
	 * @param line
	 * @param exact
	 * @param stemmer
	 */
	@Override
	public void addQueryLine(String line, boolean exact) {
		TreeSet<String> sortedWords = TextFileStemmer.clean(line);
		if (sortedWords != null) {
			String query = String.join(" ", sortedWords);
			if (!query.isEmpty() || !resultMap.containsKey(query)) {
				if (exact) {
					addWords(query, index.exactSearch(sortedWords));
				} else {
					addWords(query, index.partialSearch(sortedWords));
				}
			}
		}
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
				addQueryLine(line, exact);
			}
		}
	}

	/**
	 * Adds the query and list of results
	 * 
	 * @param query   query line in the form of a string
	 * @param results the search results for the particular query
	 */
	public void addWords(String query, ArrayList<SearchResult> results) {
		resultMap.putIfAbsent(query, results);
	}

	/**
	 * Sends the resultMap of results to JSON writer
	 * 
	 * @param writer writer object to writer results to a particular file
	 * @throws IOException
	 */
	@Override
	public void sendResult(Writer writer) throws IOException {
		TreeJSONWriter.asNestedObjectSearch(this.resultMap, writer);
	}
}
