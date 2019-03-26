package dev.williamreed.objectboxdemo.storage;

/**
 * Data Test interface
 * <p>
 * all methods return a string of what to display after they run
 */
public interface DataTest {
    int NUM_AUTHORS = 1000;
    int NUM_BOOKS_PER_AUTHOR = 10;

    String bulkWrite();

    String bulkRead();

    String deleteAll();
}
