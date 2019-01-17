/**
 * Sorts the results based on location, wordcount, query count, score
 * 
 * @author Rugved Mavidipalli
 *
 */
public class SearchResult implements Comparable<SearchResult> {

	/**
	 * Initializes the variables
	 */
	private final String location;
	private final int wordCount;
	private int queryCount;
	private double score;

	/**
	 * Initializes a new SearchResult object.
	 *
	 * @param location   the location the word was found
	 * @param wordCount  the total word count of the location file
	 * @param queryCount the total matches for a query
	 * @param score      the score for each location
	 */
	public SearchResult(String location, int wordCount, int queryCount) {
		this.location = location;
		this.wordCount = wordCount;
		this.queryCount = queryCount;
		this.score = (double) queryCount / wordCount;
	}

	/**
	 * Returns the location
	 * 
	 * @return location
	 */
	public String location() {
		return this.location;
	}

	/**
	 * Returns the word count.
	 * 
	 * @return wordcount
	 */
	public int wordCount() {
		return this.wordCount;
	}

	/**
	 * Returns the query count.
	 * 
	 * @return queryCount
	 */
	public int queryCount() {
		return this.queryCount;
	}

	/**
	 * returns the score
	 * 
	 * @return score
	 */
	public double score() {
		return this.score;
	}

	/**
	 * Updates the count for a searchResult object and score
	 * 
	 * @param count the number of matches
	 */
	public void updateCount(int count) {
		this.queryCount = this.queryCount + count;
		this.score = (double) this.queryCount / (double) this.wordCount;
	}

	/**
	 * Sorts results based on score, query count, location
	 * 
	 * @param compare object
	 * @return sorted result
	 */
	@Override
	public int compareTo(SearchResult other) {

		Double score = other.score();
		int result = score.compareTo(this.score());
		if (result == 0) {
			result = Integer.compare(other.queryCount(), this.queryCount());
			if (result == 0) {
				result = String.CASE_INSENSITIVE_ORDER.compare(this.location(), other.location());
			}
		}
		return result;
	}

}
