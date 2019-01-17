# Search Engine Project

  - A Java application that processes all text files in a directory and its subdirectories, cleans and parses the text into word stems, and builds an in-memory inverted index to store the mapping from word stems to the documents and position within those documents where those word stems were found.

  - Supports exact search and partial search. In addition application can be able to track the total number of words found in each text file, parse and stem a query file, generate a sorted list of search results from the inverted index, and supports writing those results to a JSON file.

  - Supports thread-safe inverted index, and uses a work queue to build and search the inverted index using multiple threads.

  - Supports web crawling and acquiring html

  - Supports User Tracking and stores user history
