import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import opennlp.tools.stemmer.snowball.SnowballStemmer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Crawls a url and indexes the html found in the given inverted index
 *
 */
public class CrawlerThreaded {
	/* Intializes the variables needed by CrawlerThreaded */
	/* CompltedUrls is a hashSet of urls that have been completed */
	private final HashSet<URL> completedUrls;
	/* WorkQueue */
	private final WorkQueue worker;
	/* ThreadSafe Inverted Index */
	private final ThreadSafeIndex index;
	/* Logger */
	public static final Logger log = LogManager.getLogger();

	/**
	 * Initializes crawler with a thread safe index and work queue
	 * 
	 * @param index
	 * @param worker
	 */
	public CrawlerThreaded(ThreadSafeIndex index, WorkQueue worker) {
		this.index = index;
		this.worker = worker;
		completedUrls = new HashSet<>();
		log.debug("Crawler Started");
		log.info("Crawler Started");
	}

	/**
	 * Initiates a crawl give the seed url and limit.
	 * 
	 * @param seed  url to start the crawl from
	 * @param limit limit on number of urls to be crawled
	 * @throws MalformedURLException if invalid url is provided
	 */
	public void processCrawl(String seed, int limit) throws MalformedURLException {
		URL seedUrl = new URL(seed);
		String baseUrl = seedUrl.getProtocol() + "://" + seedUrl.getHost() + seedUrl.getPath();
		URL base = new URL(baseUrl);
		completedUrls.add(seedUrl);
		worker.execute(new TheCrawler(seedUrl, base, limit));
		worker.finish();
	}

	/**
	 * Cleans the given html, parses it and adds it to the inverted index
	 * 
	 * @param processedLink the link to be added into the inverted index
	 * @param html          the html to be cleaned and parsed
	 */
	public void addLink(URL processedLink, String html) {
		int position = 1;
		SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
		String location = processedLink.toString();
		String[] words = TextParser.parse(html);
		for (String word : words) {
			index.add(stemmer.stem(word).toString(), location, position++);
		}
	}

	/**
	 * Executes a crawl given the baseUrl, limit and url to be crawled
	 *
	 */
	public class TheCrawler implements Runnable {
		private final URL baseURL;
		private final URL processUrl;
		private final int limit;

		public TheCrawler(URL processUrl, URL baseURL, int limit) {
			this.baseURL = baseURL;
			this.processUrl = processUrl;
			this.limit = limit;
			log.debug("Crawler on {}", processUrl.toString());
			log.info("Crawler on {}", processUrl.toString());
		}

		@Override
		public void run() {
			crawl(processUrl, baseURL, limit);
		}
	}

	/**
	 * Crawls a give url and acquires new links if completedUrls have not reached
	 * the limit provided
	 * 
	 * @param seedUrl the seed url to start the crawl from
	 * @param baseURL base url of the seed url
	 * @param limit   the number of urls to be crawled
	 */
	private void crawl(URL seedUrl, URL baseURL, int limit) {
		String html;
		try {
			html = HTMLFetcher.fetchHTML(seedUrl);
			int status = HTMLFetcher.getStatusCode(HttpsFetcher.fetchURL(seedUrl));
			if (html != null) {
				ArrayList<URL> links = LinkParser.listLinks(baseURL, html);
				synchronized (completedUrls) {
					for (URL link : links) {
						if (completedUrls.contains(link) == false && completedUrls.size() < limit) {
							completedUrls.add(link);
							String baseUrl = seedUrl.getProtocol() + "://" + seedUrl.getHost() + seedUrl.getPath();
							URL base;
							try {
								base = new URL(baseUrl);
								worker.execute(new TheCrawler(link, base, limit));
							} catch (MalformedURLException e) {
								System.err.println("Invalid url " + link.toString() + "unable to crawl");
							}
						}
					}
				}
			}
			if (status == -1) {
				html = getHtml(seedUrl);
				if (html != null) {
					addLink(seedUrl, html);
				}
			} else {
				if (html != null) {
					String cleanedHtml = HTMLCleaner.stripHTML(html);
					if (cleanedHtml != null) {
						addLink(seedUrl, cleanedHtml);
					}
				}
			}
		} catch (IOException ex) {
			System.err.println("Unable to fetch html for url " + seedUrl.toString());
		}
	}

	/**
	 * Fetches html for a given Url and handles redirects if the link redirects to
	 * another link
	 * 
	 * @param processUrl
	 * @return
	 */
	public String getHtml(URL processUrl) {
		String html = "";
		try {
			Map<String, List<String>> headers = HttpsFetcher.fetchURL(processUrl.toString());
			if (HTMLFetcher.isRedirect(headers)) {
				html = HTMLFetcher.fetchHTML(processUrl.toString(), 3);
			} else {
				html = HTMLFetcher.fetchHTML(processUrl.toString(), 0);
			}
		} catch (IOException e) {
			System.err.println("Unable to fetch html for url " + processUrl.toString());
		}
		if (html != null) {
			String cleanedHtml = HTMLCleaner.stripHTML(html);
			return cleanedHtml;
		}
		return null;
	}

}
