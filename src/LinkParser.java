import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LinkParser {

	// https://developer.mozilla.org/en-US/docs/Web/HTML/Element/a
	// https://docs.oracle.com/javase/tutorial/networking/urls/creatingUrls.html
	// https://developer.mozilla.org/en-US/docs/Learn/Common_questions/What_is_a_URL

	/**
	 * Removes the fragment component of a URL (if present), and properly encodes
	 * the query string (if necessary).
	 *
	 * @param url url to clean
	 * @return cleaned url (or original url if any issues occurred)
	 */
	public static URL clean(URL url) {
		// THIS METHOD IS PROVIDED FOR YOU. DO NOT MODIFY!
		try {
			return new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(),
					url.getQuery(), null).toURL();
		} catch (MalformedURLException | URISyntaxException e) {
			return url;
		}
	}

	/**
	 * Fetches the HTML (without any HTTP headers) for the provided URL. Will return
	 * null if the link does not point to a HTML page.
	 *
	 * @param url url to fetch HTML from
	 * @return HTML as a String or null if the link was not HTML
	 *
	 * @see <a href=
	 *      "https://docs.oracle.com/javase/tutorial/networking/urls/readingURL.html">Reading
	 *      Directly from a URL</a>
	 */
	public static String fetchHTML(URL url) {
		// THIS METHOD IS PROVIDED FOR YOU. DO NOT MODIFY!

		String result = null;

		try {
			URLConnection connection = url.openConnection();
			String type = connection.getContentType();

			if (type.matches(".*\\bhtml\\b.*")) {
				try (InputStreamReader input = new InputStreamReader(connection.getInputStream());
						BufferedReader reader = new BufferedReader(input);
						Stream<String> stream = reader.lines();) {
					result = stream.collect(Collectors.joining("\n"));
				}
			}
		} catch (IOException e) {
			result = null;
		}

		return result;
	}

	/**
	 * Returns a list of all the HTTP(S) links found in the href attribute of the
	 * anchor tags in the provided HTML. The links will be converted to absolute
	 * using the base URL and cleaned (removing fragments and encoding special
	 * characters as necessary).
	 *
	 * @param base base url used to convert relative links to absolute3
	 * @param html raw html associated with the base url
	 * @return cleaned list of all http(s) links in the order they were found
	 */
	public static ArrayList<URL> listLinks(URL base, String html) {
		ArrayList<URL> links = new ArrayList<URL>();
		Pattern ret = Pattern.compile("(?msi)(\\s*(<\\s*a[^>]*href\\s*=\\s*\\\"(([^\"])*)\\\"))");
		Matcher mat = ret.matcher(html);
		while (mat.find()) {
			String url = mat.group(3);
			URL urlt;
			try {
				if (url.contains("mailto") == false) {
					urlt = new URL(base, url);
					links.add(clean(urlt));
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		return links;
	}

	public static void main(String[] args) throws MalformedURLException {
		String link = "https://www.cs.usfca.edu/~cs212/simple/";
		URL url = new URL(link);
		String html = fetchHTML(url);
		List<URL> links = listLinks(url, html);

		System.out.println("HTML");
		System.out.println(html);
		System.out.println();

		System.out.println("Links");
		links.forEach(System.out::println);
	}
}
