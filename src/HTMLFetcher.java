import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HTMLFetcher {

	/**
	 * Given a map of headers (as returned either by
	 * {@link URLConnection#getHeaderFields()} or by
	 * {@link HttpsFetcher#fetchURL(URL)}, determines if the content type of the
	 * response is HTML.
	 *
	 * @param headers map of HTTP headers
	 * @return true if the content type is html
	 *
	 * @see URLConnection#getHeaderFields()
	 * @see HttpsFetcher#fetchURL(URL)
	 */
	public static boolean isHTML(Map<String, List<String>> headers) {
		List<String> type = headers.get("Content-Type");
		if (type != null) {
			for (String types : type) {
				if (types.contains("html")) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Given a map of headers (as returned either by
	 * {@link URLConnection#getHeaderFields()} or by
	 * {@link HttpsFetcher#fetchURL(URL)}, returns the status code as an int value.
	 * Returns -1 if any issues encountered.
	 *
	 * @param headers map of HTTP headers
	 * @return status code or -1 if unable to determine
	 *
	 * @see URLConnection#getHeaderFields()
	 * @see HttpsFetcher#fetchURL(URL)
	 */
	public static int getStatusCode(Map<String, List<String>> headers) {
		for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
			List<String> type = entry.getValue();
			if (type != null) {
				for (String types : type) {
					if (types.contains("200")) {
						return 200;
					}
					if (types.contains("410")) {
						return 410;
					}
					if (types.contains("404")) {
						return 404;
					}
				}
			}
		}
		return -1;
	}

	/**
	 * Given a map of headers (as returned either by
	 * {@link URLConnection#getHeaderFields()} or by
	 * {@link HttpsFetcher#fetchURL(URL)}, returns whether the status code
	 * represents a redirect response *and* the location header is properly
	 * included.
	 *
	 * @param headers map of HTTP headers
	 * @return true if the HTTP status code is a redirect and the location header is
	 *         non-empty
	 *
	 * @see URLConnection#getHeaderFields()
	 * @see HttpsFetcher#fetchURL(URL)
	 */
	public static boolean isRedirect(Map<String, List<String>> headers) {
		for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
			List<String> type = entry.getValue();
			if (type != null) {
				for (String types : type) {
					if (types.contains("301") || types.contains("302")) {
						if (headers.containsKey("Location")) {
							return true;
						}
						if (isHTML(headers) == true) {
							return true;

						} else {
							return false;
						}
					}
				}
			}
		}
		return false;
	}

	/**
	 * Uses {@link HttpsFetcher#fetchURL(URL)} to fetch the headers and content of
	 * the specified url. If the response was HTML, returns the HTML as a single
	 * {@link String}. If the response was a redirect and the value of redirects is
	 * greater than 0, will return the result of the redirect (decrementing the
	 * number of allowed redirects). Otherwise, will return {@code null}.
	 *
	 * @param url       the url to fetch and return as html
	 * @param redirects the number of times to follow a redirect response
	 * @return the html as a single String if the response code was ok, otherwise
	 *         null
	 * @throws IOException
	 *
	 * @see #isHTML(Map)
	 * @see #getStatusCode(Map)
	 * @see #isRedirect(Map)
	 */
	public static String fetchHTML(URL url, int redirects) throws IOException {
		Map<String, List<String>> results = new HashMap<>(HttpsFetcher.fetchURL(url));
		if (redirects < 0) {
			return null;
		}
		if (redirects > 0) {
			boolean check = false;
			while (check == false && redirects != 0) {
				if (results.get("Location") == null) {
					results = HttpsFetcher.fetchURL(url);
				} else {
					results = HttpsFetcher.fetchURL(results.get("Location").get(0));
				}
				if (getStatusCode(results) == 200) {
					check = true;
				}
				redirects--;
			}
			if (getStatusCode(results) == 200 && isHTML(results)) {
				List<String> lines = results.get("Content");
				return String.join(System.lineSeparator(), lines);
			} else {
				return null;
			}
		} else {
			if (getStatusCode(results) == 200 && isHTML(results)) {
				List<String> lines = results.get("Content");
				return String.join(System.lineSeparator(), lines);
			} else {
				return null;
			}
		}
	}

	/**
	 * @see #fetchHTML(URL, int)
	 */
	public static String fetchHTML(String url) throws IOException {
		return fetchHTML(new URL(url), 0);
	}

	/**
	 * @see #fetchHTML(URL, int)
	 */
	public static String fetchHTML(String url, int redirects) throws IOException {
		return fetchHTML(new URL(url), redirects);
	}

	/**
	 * @see #fetchHTML(URL, int)
	 */
	public static String fetchHTML(URL url) throws IOException {
		return fetchHTML(url, 0);
	}

}
