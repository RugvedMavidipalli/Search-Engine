import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.Map.Entry;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

public class TreeJSONWriter {

	/**
	 * Writes several tab <code>\t</code> symbols using the provided {@link Writer}.
	 *
	 * @param times  the number of times to write the tab symbol
	 * @param writer the writer to use
	 * @throws IOException if the writer encounters any issues
	 */
	public static void indent(int times, Writer writer) throws IOException {
		for (int i = 0; i < times; i++) {
			writer.write('\t');
		}
	}

	/**
	 * Writes the element surrounded by quotes using the provided {@link Writer}.
	 *
	 * @param element the element to quote
	 * @param writer  the writer to use
	 * @throws IOException if the writer encounters any issues
	 */
	public static void quote(String element, Writer writer) throws IOException {
		writer.write('"');
		writer.write(element);
		writer.write('"');
	}

	/**
	 * Returns the set of elements formatted as a pretty JSON array of numbers.
	 *
	 * @param elements the elements to convert to JSON
	 * @return {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asArray(TreeSet, Writer, int)
	 */
	public static String asArray(TreeSet<Integer> elements) {
		try {
			StringWriter writer = new StringWriter();
			asArray(elements, writer, 0);
			return writer.toString();
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the set of elements formatted as a pretty JSON array of numbers to the
	 * specified file.
	 *
	 * @param elements the elements to convert to JSON
	 * @param path     the path to the file write to output
	 * @throws IOException if the writer encounters any issues
	 */
	public static void asArray(TreeSet<Integer> elements, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asArray(elements, writer, 0);
		}
	}

	/**
	 * Writes the set of elements formatted as a pretty JSON array of numbers using
	 * the provided {@link Writer} and indentation level.
	 *
	 * @param elements the elements to convert to JSON
	 * @param writer   the writer to use
	 * @param level    the initial indentation level
	 * @throws IOException if the writer encounters any issues
	 *
	 * @see Writer#write(String)
	 * @see Writer#append(CharSequence)
	 *
	 * @see System#lineSeparator()
	 *
	 * @see #indent(int, Writer)
	 */
	public static void asArray(TreeSet<Integer> elements, Writer writer, int level) throws IOException {
		writer.write('[');
		writer.write(System.lineSeparator());

		for (Integer element : elements.headSet(elements.last())) {
			indent(level + 1, writer);
			writer.write(element.toString());
			writer.write(',');
			writer.write(System.lineSeparator());
		}
		indent(level + 1, writer);
		writer.write(elements.last().toString());
		writer.write(System.lineSeparator());
		indent(level, writer);
		writer.write(']');

	}

	/**
	 * Returns the nested map of elements formatted as a nested pretty JSON object.
	 *
	 * @param elements the elements to convert to JSON
	 * @return {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asNestedObject(TreeMap, Writer, int)
	 */
	public static String asNestedObject(TreeMap<String, TreeSet<Integer>> elements) {
		try {
			StringWriter writer = new StringWriter();
			asNestedObject(elements, writer, 0);
			return writer.toString();
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the nested map of elements formatted as a nested pretty JSON object to
	 * the specified file.
	 *
	 * @param elements the elements to convert to JSON
	 * @param path     the path to the file write to output
	 * @throws IOException if the writer encounters any issues
	 *
	 * @see #asNestedObject(TreeMap, Writer, int)
	 */
	public static void asNestedObject(TreeMap<String, TreeSet<Integer>> elements, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asNestedObject(elements, writer, 0);
		}
	}

	/**
	 * Writes the nested map of elements as a nested pretty JSON object using the
	 * provided {@link Writer} and indentation level.
	 *
	 * @param elements the elements to convert to JSON
	 * @param writer   the writer to use
	 * @param level    the initial indentation level
	 * @throws IOException if the writer encounters any issues
	 *
	 * @see Writer#write(String)
	 * @see Writer#append(CharSequence)
	 *
	 * @see System#lineSeparator()
	 *
	 * @see #indent(int, Writer)
	 * @see #quote(String, Writer)
	 *
	 * @see #asArray(TreeSet, Writer, int)
	 */
	public static void asNestedObject(TreeMap<String, TreeSet<Integer>> elements, Writer writer, int level)
			throws IOException {
		if (elements.isEmpty() != true) {
			writer.write('{');
			writer.write(System.lineSeparator());

			for (Entry<String, TreeSet<Integer>> entry : elements.headMap(elements.lastKey()).entrySet()) {
				indent(level + 1, writer);
				quote(entry.getKey().toString(), writer);
				writer.write(':');
				writer.write(' ');
				asArray(elements.get(entry.getKey()), writer, level + 1);
				writer.write(',');
				writer.write(System.lineSeparator());
			}
			indent(level + 1, writer);
			quote(elements.lastKey().toString(), writer);
			writer.write(':');
			writer.write(' ');
			asArray(elements.get(elements.lastKey()), writer, level + 1);
			writer.write(System.lineSeparator());
			writer.write('}');
		} else {
			writer.write('{');
			writer.write(System.lineSeparator());
			writer.write('}');
		}

	}

	/**
	 * Takes the whole index map of elements and writes them in Pretty Json format
	 * to the provided output file
	 * 
	 * @param elements
	 * @param writer
	 * @throws IOException
	 */
	public static void asDoubleNestedObject(TreeMap<String, TreeMap<String, TreeSet<Integer>>> elements, Writer writer)
			throws IOException {
		int level = 0;
		if (elements.isEmpty() != true) {
			writer.write('{');
			writer.write(System.lineSeparator());
			for (Entry<String, TreeMap<String, TreeSet<Integer>>> entry : elements.headMap(elements.lastKey())
					.entrySet()) {
				indent(level + 1, writer);
				quote(entry.getKey().toString(), writer);
				writer.write(':');
				writer.write(' ');
				asNestedObject(entry.getValue(), writer, level + 1);
				writer.write(',');
				writer.write(System.lineSeparator());
			}
			indent(level + 1, writer);
			quote(elements.lastKey().toString(), writer);
			writer.write(':');
			writer.write(' ');
			asNestedObject(elements.get(elements.lastKey()), writer, level + 1);
			writer.write(System.lineSeparator());
			writer.write('}');
		} else {
			writer.write('{');
			writer.write(System.lineSeparator());
			writer.write('}');
		}
	}

	/**
	 * Writes the map of elements as a pretty JSON object using the provided
	 * {@link Writer} and indentation level.
	 *
	 * @param elements the elements to convert to JSON
	 * @param writer   the writer to use
	 * @param level    the initial indentation level
	 * @throws IOException if the writer encounters any issues
	 *
	 * @see Writer#write(String)
	 * @see Writer#append(CharSequence)
	 *
	 * @see System#lineSeparator()
	 *
	 * @see #indent(int, Writer)
	 * @see #quote(String, Writer)
	 */
	public static void asObject(TreeMap<String, Integer> elements, Writer writer) throws IOException {
		int level = 0;
		if (elements.isEmpty() != true) {
			writer.write('{');
			writer.write(System.lineSeparator());
			for (Map.Entry<String, Integer> entry : elements.headMap(elements.lastKey()).entrySet()) {
				indent(level + 1, writer);
				quote(entry.getKey().toString(), writer);
				writer.write(':');
				writer.write(' ');
				writer.write(entry.getValue().toString());
				writer.write(',');
				writer.write(System.lineSeparator());
			}
			indent(level + 1, writer);
			quote(elements.lastKey().toString(), writer);
			writer.write(':');
			writer.write(' ');
			writer.write(elements.get(elements.lastKey()).toString());
			writer.write(System.lineSeparator());
			writer.write('}');
		} else {
			writer.write('{');
			writer.write(System.lineSeparator());
			writer.write('}');
		}

	}

	/**
	 * Returns the map of elements formatted as a pretty JSON object.
	 *
	 * @param elements the elements to convert to JSON
	 * @return {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asObject(TreeMap, Writer, int)
	 */
	public static String asObject(TreeMap<String, Integer> elements) {
		try {
			StringWriter writer = new StringWriter();
			asObject(elements, writer);
			return writer.toString();
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the map of elements formatted as a pretty JSON object to the specified
	 * file.
	 *
	 * @param elements the elements to convert to JSON
	 * @param path     the path to the file write to output
	 * @throws IOException if the writer encounters any issues
	 *
	 * @see #asObject(TreeMap, Writer, int)
	 */
	public static void asObject(TreeMap<String, Integer> elements, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asObject(elements, writer);
		}
	}

	/**
	 * Returns the map of elements formatted as a pretty JSON object.
	 *
	 * @param elements the elements to convert to JSON
	 * @return {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asObject(TreeMap, Writer, int)
	 */
	public static String asDoubleNestedObject(TreeMap<String, TreeMap<String, TreeSet<Integer>>> elements) {
		try {
			StringWriter writer = new StringWriter();
			asDoubleNestedObject(elements, writer);
			return writer.toString();
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the map of elements formatted as a pretty JSON object to the specified
	 * file.
	 *
	 * @param elements the elements to convert to JSON
	 * @param path     the path to the file write to output
	 * @throws IOException if the writer encounters any issues
	 *
	 * @see #asObject(TreeMap, Writer, int)
	 */
	public static void asDoubleNestedObject(TreeMap<String, TreeMap<String, TreeSet<Integer>>> elements, Path path)
			throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asDoubleNestedObject(elements, writer);
		}
	}

	/**
	 * Takes a list of compare objects and writer and prints them in Pretty JSON
	 * format
	 * 
	 * @param treeSet
	 * @param writer
	 * @param level
	 * @throws IOException
	 */
	public static void asArraySearch(ArrayList<SearchResult> treeSet, Writer writer, int level) throws IOException {
		writer.write(System.lineSeparator());
		indent(level + 1, writer);
		int count = 0;
		for (SearchResult element : treeSet) {
			count++;
			writer.write('{');
			writer.write(System.lineSeparator());
			indent(level + 2, writer);
			quote("where", writer);
			writer.write(':');
			writer.write(' ');
			quote(element.location(), writer);
			writer.write(',');
			writer.write(System.lineSeparator());
			indent(level + 2, writer);
			quote("count", writer);
			writer.write(':');
			writer.write(' ');
			writer.write(Integer.toString(element.queryCount()));
			writer.write(',');
			writer.write(System.lineSeparator());
			indent(level + 2, writer);
			quote("score", writer);
			writer.write(':');
			writer.write(' ');
			DecimalFormat FORMATTER = new DecimalFormat("0.000000");
			writer.write(FORMATTER.format(element.score()).toString());
			indent(level + 1, writer);
			writer.write(System.lineSeparator());
			indent(level + 1, writer);
			writer.write('}');
			if (count != treeSet.size()) {
				writer.write(',');
			}
			writer.write(System.lineSeparator());
			indent(level + 1, writer);
		}

	}

	/**
	 * Writes the set of elements formatted as a pretty JSON array of elements to
	 * the specified file.
	 * 
	 * @param elements
	 * @return
	 */
	public static String asArraySearch(ArrayList<SearchResult> elements) {
		// THIS METHOD IS PROVIDED FOR YOU. DO NOT MODIFY.
		try {
			StringWriter writer = new StringWriter();
			asArraySearch(elements, writer, 0);
			return writer.toString();
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Takes results index and a writer and writes them to the file in Pretty JSON
	 * format
	 * 
	 * @param elements
	 * @param writer
	 * @throws IOException
	 */
	public static void asNestedObjectSearch(TreeMap<String, ArrayList<SearchResult>> elements, Writer writer)
			throws IOException {
		int level = 0;
		writer.write('[');
		writer.write(System.lineSeparator());
		indent(level + 1, writer);
		if (elements.isEmpty() != true) {
			writer.write('{');
			writer.write(System.lineSeparator());
			for (Entry<String, ArrayList<SearchResult>> entry : elements.entrySet()) {
				indent(level + 2, writer);
				quote("queries", writer);
				writer.write(':');
				writer.write(' ');
				quote(entry.getKey().toString(), writer);
				writer.write(',');
				writer.write(System.lineSeparator());

				indent(level + 2, writer);
				quote("results", writer);
				writer.write(':');
				writer.write(' ');
				writer.write('[');
				asArraySearch(entry.getValue(), writer, level + 2);
				if (!entry.getKey().equals(elements.lastKey())) {
					indent(0, writer);
					writer.write(']');
					writer.write(System.lineSeparator());
					indent(level + 1, writer);
					writer.write('}');
					writer.write(',');
					writer.write(System.lineSeparator());
					indent(level + 1, writer);
					writer.write('{');
				} else {
					indent(0, writer);
					writer.write(']');
				}
				writer.write(System.lineSeparator());
			}
			indent(level + 1, writer);
			writer.write('}');
			writer.write(System.lineSeparator());
			writer.write(']');
		} else {
			writer.write('{');
			writer.write(System.lineSeparator());
			writer.write('}');
		}
	}
}
