import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A thread-safe version of {@link IndexedSet} using a read/write lock.
 *
 * @param <E> element type
 * @see IndexedSet
 * @see ReadWriteLock
 */
public class ThreadSafeIndex extends InvertedIndex {
	private final ReadWriteLock lock;
	public static final Logger log = LogManager.getLogger();

	/**
	 * Initializes thread safe InvertedIndex and a new ReadWrite lock object
	 */
	public ThreadSafeIndex() {
		super();
		lock = new ReadWriteLock();
	}

	/*
	 * @see InvertedIndex#addWords
	 */
	@Override
	public void addWords(List<String> words, String file, int position) {
		lock.lockReadWrite();
		try {
			super.addWords(words, file, position);
		} finally {
			lock.unlockReadWrite();
		}
	}

	/*
	 * @see InvertedIndex#add
	 */
	@Override
	public void add(String word, String location, int position) {
		lock.lockReadWrite();
		try {
			super.add(word, location, position);
		} finally {
			lock.unlockReadWrite();
		}

	}

	@Override
	public void addAll(InvertedIndex local) {
		lock.lockReadWrite();
		try {
			super.addAll(local);
		} finally {
			lock.unlockReadWrite();
		}

	}

	/*
	 * @see InvertedIndex#sendIndex
	 */
	@Override
	public void sendIndex(Writer writer) throws IOException {
		lock.lockReadOnly();
		try {
			super.sendIndex(writer);
		} finally {
			lock.unlockReadOnly();
		}

	}

	/*
	 * @see InvertedIndex#sendLocations
	 */
	@Override
	public void sendLocations(Writer writer) throws IOException {
		lock.lockReadOnly();
		try {
			super.sendLocations(writer);
		} finally {
			lock.unlockReadOnly();
		}

	}

	/*
	 * @see InvertedIndex#exactSearch
	 */
	@Override
	public ArrayList<SearchResult> exactSearch(TreeSet<String> queryWords) {
		lock.lockReadOnly();
		try {
			return super.exactSearch(queryWords);
		} finally {
			lock.unlockReadOnly();
		}

	}

	/*
	 * @see InvertedIndex#partialSearch
	 */
	@Override
	public ArrayList<SearchResult> partialSearch(TreeSet<String> queryWords) {
		lock.lockReadOnly();
		try {
			return super.partialSearch(queryWords);
		} finally {
			lock.unlockReadOnly();
		}

	}

	/*
	 * @see InvertedIndex#contains
	 */
	@Override
	public boolean contains(String word) {
		lock.lockReadOnly();
		try {
			return super.contains(word);
		} finally {
			lock.unlockReadOnly();
		}
	}

	/*
	 * @see InvertedIndex#contains
	 */
	@Override
	public boolean contains(String word, String file) {
		lock.lockReadOnly();
		try {
			return super.contains(word, file);
		} finally {
			lock.unlockReadOnly();
		}
	}

	/*
	 * @see InvertedIndex#contains
	 */
	@Override
	public boolean contains(String word, String file, int position) {
		lock.lockReadOnly();
		try {
			return super.contains(word, file, position);
		} finally {
			lock.unlockReadOnly();
		}
	}

	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		lock.lockReadOnly();
		try {
			return super.toString();
		} finally {
			lock.unlockReadOnly();
		}
	}
}