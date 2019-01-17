import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.ServletContextHandler;

public class WebServer {
	/* Initializes the variables */
	public static final int PORT = 8080;
	public static ThreadSafeIndex index;
	public static CrawlerThreaded crawler;

	/**
	 * Initializes the WebServer on a given index and crawler
	 * 
	 * @param index
	 * @param crawler
	 */
	public WebServer(ThreadSafeIndex index, CrawlerThreaded crawler) {
		WebServer.index = index;
		WebServer.crawler = crawler;
	}

	/**
	 * Starts a web server on a given port
	 * 
	 * @param port to start the server on
	 */
	public void createSite(int port) {
		// Server server = new Server(PORT);
		ServletContextHandler servletContext = null;
		// turn on sessions and set context
		servletContext = new ServletContextHandler(ServletContextHandler.SESSIONS);
		servletContext.setContextPath("/");
		servletContext.addServlet(WebServelet.class, "/");
		// setup handler order
		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[] { servletContext });
		Server server = new Server(port);
		server.setHandler(handlers);
		try {
			server.start();
			server.join();
		} catch (Exception e) {
			System.err.println("Unable to start Web Server");
		}
	}
}