import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

public class TextFileStemmer {
	/**
	 * Returns a list of cleaned and stemmed words parsed from the provided line.
	 * Uses the English {@link SnowballStemmer.ALGORITHM} for stemming.
	 *
	 * @param line the line of words to clean, split, and stem
	 * @return list of cleaned and stemmed words
	 *
	 * @see SnowballStemmer
	 * @see SnowballStemmer.ALGORITHM#ENGLISH
	 * @see #stemLine(String, Stemmer)
	 */
	public static List<String> stemLine(String line) {
		return stemLine(line, new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH));
	}

	/**
	 * Returns a list of cleaned and stemmed words parsed from the provided line.
	 *
	 * @param line    the line of words to clean, split, and stem
	 * @param stemmer the stemmer to use
	 * @return list of cleaned and stemmed words
	 *
	 * @see Stemmer#stem(CharSequence)
	 * @see TextParser#parse(String)
	 */
	public static List<String> stemLine(String line, Stemmer stemmer) {
		String[] words = TextParser.parse(line);
		List<String> val = new ArrayList<String>();
		for (String word : words) {
			val.add(stemmer.stem(word).toString());
		}
		return val;
	}

	/**
	 * Parses and stems the given line and adds the stemmed words to the give
	 * Collection data structure
	 * 
	 * @param line      line to be stemmed
	 * @param container data structure to add the stemmed words
	 * @param stemmer   stemmer to stem the words
	 */
	public static void stemLine(String line, Collection<String> container, Stemmer stemmer) {
		String[] words = TextParser.parse(line);
		for (String word : words) {
			container.add(stemmer.stem(word).toString());
		}
	}

	/**
	 * Takes a line, cleans, stems and returns a tree set of sorted stem words
	 * 
	 * @param line to parse and stem
	 * @return
	 */
	public static TreeSet<String> clean(String line) {
		SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
		TreeSet<String> sortedWords = new TreeSet<>();
		TextFileStemmer.stemLine(line, sortedWords, stemmer);
		if (!sortedWords.isEmpty()) {
			return sortedWords;
		}
		return null;
	}
}
