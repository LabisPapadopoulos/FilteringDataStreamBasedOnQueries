package gr.di.uoa.mde515.grad1353.threads;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import gr.di.uoa.mde515.grad1353.MatchType;
import gr.di.uoa.mde515.grad1353.dao.MatchDocument;
import gr.di.uoa.mde515.grad1353.dao.Results;
import gr.di.uoa.mde515.grad1353.dao.StartQuery;
import gr.di.uoa.mde515.grad1353.similarity.Similarity;

public class ResultsThread implements Runnable {
	
	public static final String EXACT_MATCH = "MT_EXACT_MATCH";
	public static final String HAMMING_DIST = "MT_HAMMING_DIST";
	public static final String EDIT_DIST = "MT_EDIT_DIST";
	
	// private static final Logger LOGGER = Logger.getLogger(ResultsThread.class.getName());
	private ConcurrentHashMap<Integer, StartQuery> queryHashMap;
	private Map<Integer, MatchDocument> matchDocumentHashMap;
	private Map<String, ArrayList<Integer>> exactMatchQueriesIndex;
	private BlockingQueue<Results> resultsQueue;
	private String resultLetter;
	private int matchDocumentId;
	private List<Integer> queryIdsList;
	
	public ResultsThread(final ConcurrentHashMap<Integer, StartQuery> queryHashMap, final Map<Integer, MatchDocument> matchDocumentHashMap, final BlockingQueue<Results> resultsQueue, 
			final String resultLetter, final int matchDocumentId, final List<Integer> queryIdsList, final Map<String, ArrayList<Integer>> exactMatchQueriesIndex) {
		this.queryHashMap = queryHashMap;
		this.matchDocumentHashMap = matchDocumentHashMap;
		this.exactMatchQueriesIndex = exactMatchQueriesIndex;
		this.resultsQueue = resultsQueue;
		this.resultLetter = resultLetter;
		this.matchDocumentId = matchDocumentId;
		this.queryIdsList = queryIdsList;
	}

	@Override
	public void run() {

		// Exact match using Index
		final Set<Integer> queryIdsSet = new HashSet<Integer>();
		final MatchDocument matchDocument = matchDocumentHashMap.get(matchDocumentId);
		// final Set<String> currentDocumentWordSet = matchDocument.getWordsSet();
		final List<String> currentDocumentWordSet = matchDocument.getWordsList();

		// for every word of current document
//		for (final String documentWord : currentDocumentWordSet) {
//			
//			// hits the index of exact match queries for this word and retrieves the exact match queryIds
//			final ArrayList<Integer> exactMatchQueryIdsList = exactMatchQueriesIndex.get(documentWord);
//			
//			if (exactMatchQueryIdsList != null) {
//				
//				// for every queryId of the exact match query list
//				for (final Integer exactMatchQueryId : exactMatchQueryIdsList) {
//					//LOGGER.log(Level.INFO, "Found currentQueryId: " + exactMatchQueryId);
//					
//					// retrieves the current start query to get its keyword list
//					final StartQuery currentStartQuery = queryHashMap.get(exactMatchQueryId);
//					
//					if (currentStartQuery != null) {
//						final Map<String, Boolean> exactMatchQueryWordsMap = new HashMap<String, Boolean>();
//						final List<String> queryWordsList = currentStartQuery.getKeywordsList();
//						
//						for (final String currentQueryWord : queryWordsList) {
//							//if (matchDocumentHashMap.get(matchDocumentId).getWordsSet().contains(currentQueryWord)) {
//							if (Similarity.exactMatch(currentDocumentWordSet, currentQueryWord)) {
//								exactMatchQueryWordsMap.put(currentQueryWord, Boolean.TRUE);
//							}
//						}
//						
//						if (exactMatchQueryWordsMap.size() == currentStartQuery.getNumberOfKeywords()) {
//							queryIdsSet.add(currentStartQuery.getQueryId());
//						}
//						
//					}
//				}
//			}
//			
//		}
//		
//		queryIdsList.addAll(queryIdsSet);
			
		for (final Map.Entry<Integer, StartQuery> query : queryHashMap.entrySet()) {
			final String matchType = MatchType.getMatchTypeFromInt(query.getValue().getMatchType());
			//LOGGER.log(Level.INFO, "Is going to execute matchType: " + matchType);

			if (EXACT_MATCH.equals(matchType)) {
				final Map<String, Boolean> exactMatchQueryWordsMap = new HashMap<String, Boolean>();
				final Set<String> queryWordsSet = new HashSet<String>(query.getValue().getKeywordsList());
				
				for (final String documentWord : currentDocumentWordSet) {
					
					if (Similarity.exactMatch(queryWordsSet, documentWord)) {
						//exactMatchQueryWordsMap.put(queryWord, Boolean.TRUE); //old
						exactMatchQueryWordsMap.put(documentWord, Boolean.TRUE);
					}
				}
				
				if (exactMatchQueryWordsMap.size() == query.getValue().getNumberOfKeywords())
					queryIdsList.add(query.getValue().getQueryId());
				
			} else if (HAMMING_DIST.equals(matchType)) {
				final Map<String, Integer> hammingDistanceQueryWordsMap = new HashMap<String, Integer>();
				for(final String queryWord: query.getValue().getKeywordsList()) {
					
//					final MatchDocument matchDocument = matchDocumentHashMap.get(matchDocumentId);
					
					if (matchDocument != null) {
					
						for(final String documentWord : currentDocumentWordSet) {
							final int currentHammingDistance = Similarity.hammingDistance(queryWord, documentWord);
							
							// LOGGER.log(Level.INFO, "The hamming distance of <" + queryWord + ", " + documentWord + "> is: " + currentHammingDistance);
							
							if (currentHammingDistance != -1 && currentHammingDistance <= query.getValue().getDistance()) {
								hammingDistanceQueryWordsMap.put(queryWord, currentHammingDistance);
							}
						}	
					}
					
				}
				
				if (hammingDistanceQueryWordsMap.size() == query.getValue().getNumberOfKeywords())
					queryIdsList.add(query.getValue().getQueryId());
				
			} else if (EDIT_DIST.equals(matchType)) {
				final Map<String, Integer> editDistanceQueryWordsMap = new HashMap<String, Integer>();
				for(final String queryWord : query.getValue().getKeywordsList()) {
					
					// final MatchDocument matchDocument = matchDocumentHashMap.get(matchDocumentId);
					
					if (matchDocument != null) {
						for(final String documentWord : currentDocumentWordSet) {
							
							// Upper and lower bounds of Edit Distance (source: https://en.wikipedia.org/wiki/Levenshtein_distance#Upper_and_lower_bounds)
							final int word1Length = queryWord.length();
							final int word2Length = documentWord.length();
							final int queryEditDistance = query.getValue().getDistance();
							
							int currentEditDistance = -1;
							
							if (Math.abs(word1Length - word2Length) <= queryEditDistance) // 1) It is always at least the difference of the sizes of the two strings
								currentEditDistance = Similarity.editDistance(queryWord, documentWord);
							else if (queryWord.equals(documentWord)) { // 2) It is zero if and only if the strings are equal.
								currentEditDistance = 0;
							} else if (word1Length == word2Length) { // 3) If the strings are the same size, the Hamming distance is an upper bound on the Edit distance.
								final int currentHammingDistance = Similarity.hammingDistance(queryWord, documentWord);
								if (currentHammingDistance <= queryEditDistance)
									currentEditDistance = currentHammingDistance;
								else
									currentEditDistance = -1;
							}
							
							// LOGGER.log(Level.INFO, "EDIT_DISTANCE(" + queryWord + ", " + documentWord + ") = " + currentEditDistance);
							
							if (currentEditDistance != -1 && currentEditDistance <= queryEditDistance) {
								editDistanceQueryWordsMap.put(queryWord, currentEditDistance);
							}
						}
					}
				}
				
				if (editDistanceQueryWordsMap.size() == query.getValue().getNumberOfKeywords())
					queryIdsList.add(query.getValue().getQueryId());
				
			}
		}
			
		// save more memory removing processed matchDocumentId 
		matchDocumentHashMap.remove(matchDocumentId);
		
		Collections.sort(queryIdsList);
		
		try {
			resultsQueue.put(new Results(resultLetter, matchDocumentId, queryIdsList.size(), queryIdsList));
		} catch (final InterruptedException e) { }
	
	}
}
