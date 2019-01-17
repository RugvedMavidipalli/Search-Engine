import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * @author rmavidipalli InvertedIndexBuilder reads a file and stem's the file
 */
public class InvertedIndexBuilder {

	/**
	 * Adds all text files for given directory path and index
	 * 
	 * @param root  directory path to find text files
	 * @param index InvertedIndex to add text files
	 * @throws IOException
	 */
	public static void addFiles(Path root, InvertedIndex index) throws IOException {
		List<Path> filePaths = TextFileFinder.findTextFiles(root);
		for (Path file : filePaths) {
			InvertedIndexBuilder.addFile(file, index);
		}
	}

	/**
	 * Takes a path and index reads the file and stems the file, Lastly adds the
	 * stemwords to the index and updates the location
	 * 
	 * @param path  path to parse and into the InvertedIndex
	 * @param index the InvertedIndex
	 * @throws IOException
	 */
	public static void addFile(Path path, InvertedIndex index) throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);) {
			String line = null;
			int position = 1;
			SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
			String location = path.toString();
			while ((line = reader.readLine()) != null) {
				String[] words = TextParser.parse(line);
				for (String word : words) {
					index.add(stemmer.stem(word).toString(), location, position++);
				}
			}
		}
	}
}
