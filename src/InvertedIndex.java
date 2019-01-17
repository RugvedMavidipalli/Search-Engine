import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Data structure to store strings and their positions.
 */
public class InvertedIndex {

	/**
	 * Stores a mapping of words to the positions the words were found and file they
	 * were found in.
	 */
	private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> index;
	private final TreeMap<String, Integer> locations;

	/**
	 * Initializes the index.
	 */
	public InvertedIndex() {
		this.index = new TreeMap<String, TreeMap<String, TreeSet<Integer>>>();
		this.locations = new TreeMap<String, Integer>();
	}

	/**
	 * Converts an index to current index
	 * 
	 * @param convertIndex
	 */
	public InvertedIndex(InvertedIndex convertIndex) {
		this.index = convertIndex.index;
		this.locations = new TreeMap<String, Integer>();
	}

	/**
	 * Takes a list of stemmed words and adds the words, file, and their respective
	 * positions in a given file.
	 * 
	 * @param words list of words to add
	 * @param file  file name
	 * 
	 * @return void
	 */
	public void addWords(List<String> words, String file, int position) {
		for (String word : words) {
			add(word, file, position++);
		}
	}

	/**
	 * Takes a word, location, position and adds it to the index
	 * 
	 * @param word     word to add
	 * @param location file the words are from
	 * @param position the postion the word was found
	 */
	public void add(String word, String location, int position) {
		index.putIfAbsent(word, new TreeMap<String, TreeSet<Integer>>());
		index.get(word).putIfAbsent(location, new TreeSet<>());
		index.get(word).get(location).add(position);
		locations.put(location, locations.getOrDefault(location, 0) + 1);
	}

	/**
	 * Calls and provides the InvertedIndex to the TreeJSONWriter to write the
	 * InvertedIndex to the file
	 * 
	 * @param writer writer object and file to write the InvertedIndex to
	 * @return void
	 * @throws IOException
	 */
	public void sendIndex(Writer writer) throws IOException {
		TreeJSONWriter.asDoubleNestedObject(this.index, writer);
	}

	/**
	 * Sends locations to Json Writer
	 * 
	 * @param writer writer object and file to write the locations to
	 * @return void
	 * @throws IOException
	 */
	public void sendLocations(Writer writer) throws IOException {
		TreeJSONWriter.asObject(this.locations, writer);
	}

	/**
	 * Checks if word exists in index
	 * 
	 * @param word word to check in the index
	 * @return True if words exists else false
	 */
	public boolean contains(String word) {
		return index.containsKey(word);
	}

	/**
	 * Checks if a word and file exists in the index
	 * 
	 * @param word word to check in the index
	 * @param file file to check in the inverted index
	 * @return True if word and file exist else false
	 */
	public boolean contains(String word, String file) {
		return index.containsKey(word) && index.get(word).containsKey(file);
	}

	/**
	 * Checks if a word, file and position exist in the index
	 * 
	 * @param word     word word to check in the index
	 * @param file     file to check in the inverted index
	 * @param position postion to check for in the inverted index
	 * @return True if exist else false
	 */
	public boolean contains(String word, String file, int position) {
		return contains(word, file) && (index.get(word).get(file).contains(position));
	}

	/**
	 * Takes the query map and finds all the words that match exactly and returns
	 * the results
	 * 
	 * @param queryWords stemmed words from a single query line
	 * @return Arraylist of results
	 */
	public ArrayList<SearchResult> exactSearch(TreeSet<String> queryWords) {
		HashMap<String, SearchResult> check = new HashMap<String, SearchResult>();
		ArrayList<SearchResult> results = new ArrayList<SearchResult>();
		for (String word : queryWords) {
			if (index.containsKey(word)) {
				searchHelper(word, check, results);
			}
		}
		Collections.sort(results);
		return results;
	}

	/**
	 * Builds and adds SearchResult objects to the results list.
	 * 
	 * @param query   the search query
	 * @param check   data structure that contains the query and search results
	 * @param results the array list of results
	 * @return results list with SearchResults for the particular query
	 */
	private ArrayList<SearchResult> searchHelper(String query, HashMap<String, SearchResult> check,
			ArrayList<SearchResult> results) {
		for (String file : index.get(query).keySet()) {
			if (check.containsKey(file)) {
				check.get(file).updateCount(index.get(query).get(file).size());
			} else {
				SearchResult result = new SearchResult(file, locations.get(file), index.get(query).get(file).size());
				check.put(file, result);
				results.add(result);
			}
		}
		return results;
	}

	/**
	 * Takes a query and returns all the partial search results in the index
	 * 
	 * @param queryWords the query words from a single line to search for
	 * @return parital search results Arraylist<SearchResult>
	 */
	public ArrayList<SearchResult> partialSearch(TreeSet<String> queryWords) {
		HashMap<String, SearchResult> check = new HashMap<String, SearchResult>();
		ArrayList<SearchResult> results = new ArrayList<SearchResult>();
		for (String word : queryWords) {
			for (Entry<String, TreeMap<String, TreeSet<Integer>>> stem : index.tailMap(word).entrySet()) {
				if (stem.getKey().startsWith(word) == true) {
					searchHelper(stem.getKey(), check, results);
				} else {
					break;
				}
			}
		}
		Collections.sort(results);
		return results;
	}

	/**
	 * Adds a local InvertedIndex in to the Global InvertedIndex
	 * 
	 * @param local
	 */
	public void addAll(InvertedIndex local) {
		for (String word : local.index.keySet()) {
			if (!index.containsKey(word)) {
				index.put(word, local.index.get(word));
			} else {
				for (String file : local.index.get(word).keySet()) {
					if (!index.get(word).containsKey(file)) {
						index.get(word).put(file, local.index.get(word).get(file));
					} else {
						index.get(word).get(file).addAll(local.index.get(word).get(file));
					}
				}
			}
		}
		for (String location : local.locations.keySet()) {
			if (this.locations.containsKey(location)) {
				this.locations.put(location, this.locations.getOrDefault(location, 0) + local.locations.get(location));
			} else {
				this.locations.put(location, local.locations.get(location));
			}
		}

	}

}
