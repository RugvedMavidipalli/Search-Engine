import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author rugvedmavidipalli
 *
 */
public class InvertedIndexBuilderThread {
	public static final Logger log = LogManager.getLogger();

	/**
	 * Creates a thread to add every single text file found in the directory path
	 * 
	 * @param root  directory path to find text files
	 * @param index InvertedIndex to add files too
	 * @param queue WorkQueue threads
	 * @throws IOException
	 */
	public static void addFiles(Path root, ThreadSafeIndex index, WorkQueue queue) {
		List<Path> filePaths;
		try {
			filePaths = TextFileFinder.findTextFiles(root);
			for (Path file : filePaths) {
				queue.execute(new Builder(file, index));
			}
		} catch (IOException e) {
			System.err.println("Unable to add file " + root.toString());
		} finally {
			queue.finish();
		}
	}

	/**
	 * Builder class stemms and adds words and file to a given InvertedIndex
	 *
	 */
	public static class Builder implements Runnable {
		private final Path path;
		private final ThreadSafeIndex index;

		public Builder(Path path, ThreadSafeIndex index) {
			this.path = path;
			this.index = index;
			log.debug("Working on {}", path.toString());
		}

		@Override
		public void run() {
			try {
				log.info("Add files {}", path.toString());
				InvertedIndex local = new InvertedIndex();
				InvertedIndexBuilder.addFile(path, local);
				index.addAll(local);
			} catch (IOException e) {
				System.err.println("Unable to add path" + path.toString());
			}
		}
	}
}
