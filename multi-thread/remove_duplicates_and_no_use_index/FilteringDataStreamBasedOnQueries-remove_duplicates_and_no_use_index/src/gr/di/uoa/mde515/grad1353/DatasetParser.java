package gr.di.uoa.mde515.grad1353;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import gr.di.uoa.mde515.grad1353.dao.MatchDocument;
import gr.di.uoa.mde515.grad1353.dao.Results;
import gr.di.uoa.mde515.grad1353.dao.StartQuery;
import gr.di.uoa.mde515.grad1353.threads.ResultsThread;

public class DatasetParser {
	
	// private static final Logger LOGGER = Logger.getLogger(DatasetParser.class.getName());
	private static final String START_QUERY = "s";
	private static final String END_QUERY = "e";
	private static final String MATCH_DOCUMENT = "m";
	private static final String RESULTS = "r";
	private static final String DELIMITER = " ";
	
	private ConcurrentHashMap<Integer, StartQuery> queryHashMap;
	private Map<Integer, MatchDocument> matchDocumentHashMap;
	private Map<String, ArrayList<Integer>> exactMatchQueriesIndex;
	private List<Thread> resultsThreadList;
	private BlockingQueue<Results> resultsQueue;
	
	public DatasetParser(final BlockingQueue<Results> resultsQueue, final List<Thread> resultsThreadList) {
		queryHashMap = new ConcurrentHashMap<Integer, StartQuery>();
		matchDocumentHashMap = new HashMap<Integer, MatchDocument>();
		exactMatchQueriesIndex = new HashMap<String, ArrayList<Integer>>();
		this.resultsThreadList = resultsThreadList;
		this.resultsQueue = resultsQueue;
	}

	public void parseInput(final String datasetLine) {
		
		if (datasetLine.startsWith(START_QUERY)) {
			
			waitForDocumentBatchExecution();
			
			// startQuery example:
			// s 1 0 0 3 edit gaither anderson
			final String[] startQueryParts = datasetLine.split(DELIMITER);
			final String queryType = startQueryParts[0];	//'s' letter
			final int queryId = Integer.parseInt(startQueryParts[1]);
			final int matchType = Integer.parseInt(startQueryParts[2]);
			final int distance = Integer.parseInt(startQueryParts[3]);
			final int numberOfKeywords = Integer.parseInt(startQueryParts[4]);
			final List<String> keywordsList = new ArrayList<>();
			
			for (int i = 1; i <= numberOfKeywords; i++) {
				final String currentWord = startQueryParts[4+i];
				keywordsList.add(currentWord);
				
//				final String currentQueryMatchType = MatchType.getMatchTypeFromInt(matchType);
//				if (ResultsThread.EXACT_MATCH.equals(currentQueryMatchType)) {
//					
//					// add index to words of startQueries
//					ArrayList<Integer> currWordList = exactMatchQueriesIndex.get(currentWord);
//					if (currWordList == null) {
//						currWordList = new ArrayList<Integer>();
//						currWordList.add(queryId);
//						exactMatchQueriesIndex.put(currentWord, currWordList);
//					} else {
//						currWordList.add(queryId);
//					}
//				}
				
			}
			
			final StartQuery startQuery = new StartQuery(queryType, queryId, matchType, distance, numberOfKeywords, keywordsList);
			
			queryHashMap.put(queryId, startQuery);
			// LOGGER.log(Level.INFO, "Found Start Query with id: " + queryId);

		} else if (datasetLine.startsWith(END_QUERY)) {
			// endQuery example:
			// e 194
			final String[] endQueryParts = datasetLine.split(DELIMITER);
			//final String queryType = endQueryParts[0];	//'e' letter
			final int queryId = Integer.parseInt(endQueryParts[1]);
			//final EndQuery endQuery = new EndQuery(queryType, queryId); 
			
			// remove queryId from Index
//			updateExactMatchQueriesIndex(queryId);
			
			// kill query with id: queryId
			queryHashMap.remove(queryId);
			// LOGGER.log(Level.INFO, "Removed End Query with id: " + queryId);
			
		} else if (datasetLine.startsWith(MATCH_DOCUMENT)) {
			// matchDocument example:
			// m 1 1428 http dbpedia resource list women national basketball ...
			final String[] matchDocumentParts = datasetLine.split(DELIMITER);
			//final String matchDocumentLetter = matchDocumentParts[0];	//'m' letter
			final int matchDocumentId = Integer.parseInt(matchDocumentParts[1]);
			final int numberOfWords = Integer.parseInt(matchDocumentParts[2]);
			final Set<String> wordsSet = new HashSet<String>();
			
			for (int i = 1; i <= numberOfWords; i++) {
				wordsSet.add(matchDocumentParts[2+i]);
			}
			
			final MatchDocument matchDocument = new MatchDocument(matchDocumentId, numberOfWords, wordsSet);
			matchDocumentHashMap.put(matchDocumentId, matchDocument);
			// LOGGER.log(Level.INFO, "Found DocumentMatch with id: " + matchDocumentId);
			
			final List<Integer> queryIdsList = new ArrayList<Integer>();

			ResultsThread resultsThread = new ResultsThread(queryHashMap, matchDocumentHashMap, resultsQueue, 
					RESULTS, matchDocumentId, queryIdsList, exactMatchQueriesIndex);
			final Thread resultsThreadThread = new Thread(resultsThread, String.valueOf(matchDocumentId));
			resultsThreadList.add(resultsThreadThread);
			resultsThreadThread.start();
			
		}
	}
	
	public void waitForDocumentBatchExecution() {
		// Wait for the previous batch of documents to be executed
		
		if (resultsThreadList.size() > 0) {
			// First join current result threads and continue execute the following queries (Start/End Queries)
			// JoinResultQueriesThread joinResultQueriesThread = new JoinResultQueriesThread(resultsThreadList);
			// final Thread joinResultQueriesThreadThread = new Thread(joinResultQueriesThread);
			// LOGGER.info("Created joinResultQueriesThread for the last batch");
			// start joinResultQueriesThread
			// joinResultQueriesThreadThread.start();
			// try {
			//	joinResultQueriesThreadThread.join();
			//	LOGGER.log(Level.INFO, "Joined threads: " + joinResultQueriesThread.getNumberOfJoinedThreads());
			// } catch (InterruptedException e) { }

			try {
				final Iterator<Thread> resultsThreadIterator = resultsThreadList.iterator();
				while(resultsThreadIterator.hasNext()) {
					final Thread currentResultThread = resultsThreadIterator.next();
					currentResultThread.join();
					resultsThreadIterator.remove();
					// LOGGER.log(Level.INFO, "Joined and removed thread with id: " + currentResultThread.getId());
				}
			} catch (final InterruptedException e) { }
		}
	}
	
	// remove queryId from Index
	public void updateExactMatchQueriesIndex(final int queryId) {
		final StartQuery deletedStartQuery = queryHashMap.get(queryId);
		final String currentDeletedQueryMatchType = MatchType.getMatchTypeFromInt(deletedStartQuery.getMatchType());
		
		// start update index only on exact match type queries
		if (ResultsThread.EXACT_MATCH.equals(currentDeletedQueryMatchType)) {
			for (final String currentWord : deletedStartQuery.getKeywordsList()) {
				final ArrayList<Integer> currentWordList = exactMatchQueriesIndex.get(currentWord);
				if (currentWordList != null) {
					final Iterator<Integer> exactMatchQueryIdsIterator = currentWordList.iterator();
					while (exactMatchQueryIdsIterator.hasNext()) {
						final Integer currentExactMatchQueryId = exactMatchQueryIdsIterator.next();
						if (deletedStartQuery.getQueryId() == currentExactMatchQueryId) {
							exactMatchQueryIdsIterator.remove();
						}
					}
				}
			}
		}
	}
	
}
