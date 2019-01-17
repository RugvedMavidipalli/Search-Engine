import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.text.StringEscapeUtils;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentLinkedQueue;

@SuppressWarnings("serial")
public class WebServelet extends HttpServlet {
	private ConcurrentLinkedQueue<String> results;
	private TreeSet<String> information;
	private static final ThreadSafeIndex index = WebServer.index;
	private static final CrawlerThreaded crawler = WebServer.crawler;

	public WebServelet() {
		super();
		results = new ConcurrentLinkedQueue<String>();
		information = new TreeSet<String>();

	}

	/*
	 * @see
	 * javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);
		PrintWriter out = response.getWriter();
		out.printf("<!DOCTYPE html>%n");
		out.printf("<html>%n");
		out.printf("<head>%n");
		out.printf("	<meta charset=\"utf-8\">%n");
		out.printf("	<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">%n");
		out.printf("	<title>%s</title>%n", "NOGOOGLE");
		out.printf(
				"	<link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/bulma/0.7.2/css/bulma.min.css\">%n");
		out.printf("	<script defer src=\"https://use.fontawesome.com/releases/v5.3.1/js/all.js\"></script>%n");
		out.printf("</head>%n");
		out.printf("%n");
		out.printf("<body>%n");
		out.printf("	<section class=\"hero is-primary is-bold\">%n");
		out.printf("	  <div class=\"hero-body\">%n");
		out.printf("	    <div class=\"container\">%n");
		out.printf("	      <h1 class=\"title\">%n");
		out.printf("	        NOGOOGLE%n");
		out.printf("	      </h1>%n");
		out.printf("		  <h4 class=\"title\">\"Most Private Search Engine\"</h4>%n");
		out.printf(
				"		  <h4 class=\"text\">We are not Google but we may sell your data to a very reputable analytics compay called Cambridge Analytica. Dont worry its \"Private\"</h4>%n");
		out.printf("	    </div>%n");
		out.printf("	  </div>%n");
		out.printf("	</section>%n");
		out.printf("%n");
		out.printf("	<section class=\"section\">%n");
		out.printf("		<div class=\"container\">%n");
		out.printf("			<h2 class=\"title\">Results</h2>%n");
		out.printf("%n");
		if (results.isEmpty()) {
			out.printf("				<p>Results show after search.</p>%n");
		} else {
			for (String x : results) {
				out.printf("				<div class=\"box\">%n");
				out.printf(x);
				out.printf("				</div>%n");
				out.printf("%n");
			}
			results.clear();
		}
		out.printf("			</div>%n");
		out.printf("%n");
		out.printf("		</div>%n");
		out.printf("	</section>%n");
		out.printf("%n");
		out.printf("	<section class=\"section\">%n");
		out.printf("		<div class=\"container\">%n");
		out.printf("			<h2 class=\"title\">Search</h2>%n");
		out.printf("%n");
		out.printf("			<form method=\"%s\" action=\"%s\">%n", "POST", request.getServletPath());
		out.printf("				<div class=\"field\">%n");
		out.printf("					<label class=\"label\">New Crawl</label>%n");
		out.printf("					<div class=\"control has-icons-left\">%n");
		out.printf(
				"						<input class=\"input\" type=\"text\" name=\"%s\" placeholder=\"Please provide a seed link.\">%n",
				"crawl");
		out.printf("				<div class=\"control\">%n");
		out.printf("			    <button class=\"button is-primary\" type=\"submit\">%n");
		out.printf("						&nbsp;%n");
		out.printf("						crawl%n");
		out.printf("					</button>%n");
		out.printf("			  </div>%n");
		out.printf("					</div>%n");
		out.printf("				</div>%n");
		out.printf("%n");
		out.printf("%n");
		out.printf("				<div class=\"field\">%n");
		out.printf("				  <label class=\"label\">Query</label>%n");
		out.printf("				  <div class=\"control\">%n");
		out.printf(
				"				    <textarea class=\"textarea\" name=\"%s\" placeholder=\"Enter your Query here (exact) - for exact search or(private) for private search.\"></textarea>%n",
				"query");
		out.printf("				  </div>%n");
		out.printf("				</div>%n");
		out.printf("%n");
		out.printf("				<div class=\"control\">%n");
		out.printf("			    <button class=\"button is-primary\" type=\"submit\">%n");
		out.printf("						&nbsp;%n");
		out.printf("						Search%n");
		out.printf("					</button>%n");
		out.printf("			  </div>%n");
		out.printf("			</form>%n");
		out.printf("		</div>%n");
		out.printf("	</section>%n");
		out.printf("	<section class=\"section\">%n");
		out.printf("		<div class=\"container\">%n");
		out.printf("			<h2 class=\"title\">Information</h2>%n");
		out.printf("%n");
		if (information.isEmpty()) {
			out.printf("				<p>No Information to Show.</p>%n");
		} else {
			for (String inf : information) {
				out.printf("				<div class=\"box\">%n");
				out.printf(inf);
				out.printf("				</div>%n");
				out.printf("%n");
			}
			information.clear();
		}
		try {
			out.printf("			<h2 class=\"title\">History</h2>%n");
			out.printf("%n");
			cookiesSetUp(response, request);
		} catch (IOException e) {
			System.err.println("Unable to set up cookies");
		}

		out.printf("			</div>%n");
		out.printf("%n");
		out.printf("		</div>%n");
		out.printf("	</section>%n");
		out.printf("%n");
		out.printf("%n");
		out.printf("	<footer class=\"footer\">%n");
		out.printf("	  <div class=\"content has-text-centered\">%n");
		out.printf("	    <p>%n");
		out.printf("	      This request was handled by thread %s.%n", Thread.currentThread().getName());
		out.printf("	    </p>%n");
		out.printf("	  </div>%n");
		out.printf("	</footer>%n");
		out.printf("</body>%n");
		out.printf("</html>%n");
		response.setStatus(HttpServletResponse.SC_OK);
	}

	/*
	 * @see
	 * javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);
		addQuery(response, request);
		cookiesSetUp(response, request);
		handleRequests(request, response);
		response.setStatus(HttpServletResponse.SC_OK);
		response.sendRedirect(request.getServletPath());
	}

	/**
	 * Searches the inverted index for a given query
	 * 
	 * @param query
	 * @param exact
	 */
	private void searcher(String query, boolean exact) {
		SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
		TreeSet<String> sortedWords = new TreeSet<>();
		TextFileStemmer.stemLine(query, sortedWords, stemmer);
		if (exact == true) {
			ArrayList<SearchResult> temp = new ArrayList<SearchResult>(index.exactSearch(sortedWords));
			for (SearchResult res : temp) {
				String result = formatResults(res, query);
				if (results.contains(result) == false) {
					results.add(result);
				}
			}
		} else {
			ArrayList<SearchResult> temp = new ArrayList<SearchResult>(index.partialSearch(sortedWords));
			for (SearchResult res : temp) {
				String result = formatResults(res, query);
				results.add(result);
			}
		}
	}

	/**
	 * Formats the search results to be displayed
	 * 
	 * @param obj   search result object
	 * @param query the query from the search engine
	 * @return result to print
	 */
	private static String formatResults(SearchResult obj, String query) {
		String link = String.format("<a href=\"%s\">%s</a>%n", obj.location(), obj.location());
		String result = "Query : " + query + "  Location : " + link;
		return result;
	}

	/**
	 * Prints the history of the user.
	 * 
	 * @param title    the title to print
	 * @param response Servlet response
	 * @throws IOException
	 */
	public static void prepareResponse(String title, HttpServletResponse response) throws IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.printf("<!DOCTYPE html>");
		out.printf("<html>%n%n");
		out.printf("<head>%n");
		out.printf("\t<meta charset=\"UTF-8\">");
		out.printf("\t<title>%s</title>%n", title);
		out.printf("</head>%n%n");
		out.printf("<body>%n%n");
	}

	/**
	 * Gets the cookies form the HTTP request, and maps the cookie name to the
	 * cookie object.
	 *
	 * @param request the HTTP request from web server
	 * @return map from cookie key to cookie value
	 */
	public Map<String, Cookie> getCookieMap(HttpServletRequest request) {
		HashMap<String, Cookie> map = new HashMap<>();
		Cookie[] cookies = request.getCookies();

		if (cookies != null) {
			for (Cookie cookie : cookies) {
				map.put(cookie.getName(), cookie);
			}
		}

		return map;
	}

	/**
	 * Clears all of the cookies included in the HTTP request.
	 *
	 * @param request  the HTTP request
	 * @param response the HTTP response
	 */
	public void clearCookies(HttpServletRequest request, HttpServletResponse response) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				cookie.setValue(null);
				cookie.setMaxAge(0);
				response.addCookie(cookie);
			}
		}
		try {
			PrintWriter out = response.getWriter();
			out.printf("<p>Your visits will not be tracked.</p>");
		} catch (IOException e) {
			System.err.println("Unable to write");
		}
		information.add("YOUR INFORMATION IS NOT TRACKED BUT DID YOU FORGET WE ARE A SUBSIDARY OF NSA,GOOGLE,FACEBOOK");
	}

	/**
	 * Gets the current long date in the format hh:mm a 'on' EEEE, MMMM dd yyyy
	 * 
	 * @return formated date
	 */
	public static String getLongDate() {
		String format = "hh:mm a 'on' EEEE, MMMM dd yyyy";
		DateFormat formatter = new SimpleDateFormat(format);
		return formatter.format(new Date());
	}

	/**
	 * Returns the current date and time in a short format.
	 *
	 * @return current date and time
	 * @see #getLongDate()
	 */
	public static String getShortDate() {
		String format = "yyyy-MM-dd hh:mm a";
		DateFormat formatter = new SimpleDateFormat(format);
		return formatter.format(Calendar.getInstance().getTime());
	}

	/**
	 * Handles different requests from the servlet
	 * 
	 * @param request  HttpServletRequest
	 * @param response HttpServletResponse
	 */
	public void handleRequests(HttpServletRequest request, HttpServletResponse response) {
		String query = request.getParameter("query");
		query = query == null ? "" : query;
		query = StringEscapeUtils.escapeHtml4(query);
		if (query != null) {
			boolean check = false;
			if (query.contains("(exact)") == true && check == false && query.contains("(private)") != true) {
				query = query.replace("(exact)", "");
				searcher(query, true);
				check = true;
			}
			if (query.contains("(exact)") == true && check == false && query.contains("(private)") == true) {
				query = query.replace("(exact)", "");
				query = query.replace("(private)", "");
				searcher(query, true);
				check = true;
				clearCookies(request, response);
			}
			if (query.contains("(private)") == true && check == false) {
				query = query.replace("(private)", "");
				searcher(query, false);
				check = true;
				clearCookies(request, response);
			}
			if (query.contains("(exact)") != true && query.contains("(private)") != true && check == false) {
				searcher(query, false);
				check = true;
			}
		}
		String crawl = request.getParameter("crawl");
		crawl = StringEscapeUtils.escapeHtml4(crawl);
		crawl = crawl == null ? "" : crawl;
		if (crawl != null || crawl != "") {
			String link = "https://" + crawl;
			try {
				if (link.length() != 8) {
					crawler.processCrawl(link, 50);
					information.add("Crawled: " + crawl);
				}
			} catch (MalformedURLException e) {
				System.err.println("Unable to crawl " + link);
			}
		}
	}

	/**
	 * Sets up cookies for the visitor
	 * 
	 * @param response
	 * @param request
	 * @throws IOException
	 */
	public void cookiesSetUp(HttpServletResponse response, HttpServletRequest request) throws IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		prepareResponse("Cookies!", response);
		final String VISIT_DATE = "Visited";
		final String VISIT_COUNT = "Count";
		Map<String, Cookie> cookies = getCookieMap(request);
		Cookie visitDate = cookies.get(VISIT_DATE);
		Cookie visitCount = cookies.get(VISIT_COUNT);
		Cookie queries = cookies.get("Queries");
		if (visitDate == null || visitCount == null) {
			visitCount = new Cookie(VISIT_COUNT, "0");
			visitDate = new Cookie(VISIT_DATE, "");
			out.printf("You have never been to this webpage before! ");
		} else {
			try {
				int count = Integer.parseInt(visitCount.getValue());
				visitCount.setValue(Integer.toString(count + 1));
				String decoded = URLDecoder.decode(visitDate.getValue(), StandardCharsets.UTF_8);
				out.printf("You have visited this website %s times. ", visitCount.getValue());
				out.printf("Last Visit: %s . ", decoded);
				if (queries != null) {
					String[] array = queries.getValue().split(",");
					for (String inf : array) {
						if (inf != null && inf.isEmpty() == false) {
							if (inf.contains(",") == true) {
								inf = inf.replace(",", "");
							}
							if (inf.contains("*") == true) {
								inf = inf.replace("*", " ");
							}
							out.printf("				<div class=\"box\">%n");
							out.printf("History :" + inf);
							out.printf("				</div>%n");
							out.printf("%n");
						}
					}
				}

			} catch (NullPointerException | IllegalArgumentException e) {
				visitCount = new Cookie(VISIT_COUNT, "0");
				visitDate = new Cookie(VISIT_DATE, "");
				out.printf("Unable to determine if you have visited this website before. ");

			}
		}
		out.printf("</p>%n");
		if (request.getIntHeader("DNT") != 1) {
			String encoded = URLEncoder.encode(getLongDate(), StandardCharsets.UTF_8);
			visitDate.setValue(encoded);
			response.addCookie(visitDate);
			response.addCookie(visitCount);
		} else {
			clearCookies(request, response);
			out.printf("<p>Your visits will not be tracked.</p>");
		}
	}

	/**
	 * Adds query to queries cookie(history)
	 * 
	 * @param response
	 * @param request
	 */
	public void addQuery(HttpServletResponse response, HttpServletRequest request) {
		Map<String, Cookie> cookies = getCookieMap(request);
		Cookie queries = cookies.get("Queries");
		if (queries == null) {
			String query = request.getParameter("query");
			query = query == null ? "" : query;
			query = StringEscapeUtils.escapeHtml4(query);
			if (query.isEmpty() == false) {
				String clean = query.replaceAll("\\s", "*");
				String decoded = URLDecoder.decode(getShortDate(), StandardCharsets.UTF_8);
				decoded = decoded.replaceAll("\\s", "*");
				queries = new Cookie("Queries", clean + "[" + decoded + "]");
				response.addCookie(queries);
			}
		} else {
			if (queries.getValue() != null && queries.getValue().isEmpty() != true) {
				String query = request.getParameter("query");
				query = query == null ? "" : query;
				query = StringEscapeUtils.escapeHtml4(query);
				String decoded = URLDecoder.decode(getShortDate(), StandardCharsets.UTF_8);
				if (query.isEmpty() == false) {
					String clean = query.replaceAll("\\s", "*");
					decoded = decoded.replaceAll("\\s", "*");
					String nQuery = queries.getValue() + "," + clean + "[" + decoded + "]";
					queries.setValue(nQuery);
					response.addCookie(queries);
				}
			}
		}
	}
}