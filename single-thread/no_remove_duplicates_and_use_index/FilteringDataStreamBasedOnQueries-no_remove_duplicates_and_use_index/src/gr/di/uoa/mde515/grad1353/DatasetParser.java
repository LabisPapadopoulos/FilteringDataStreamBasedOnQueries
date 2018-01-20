package gr.di.uoa.mde515.grad1353;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import gr.di.uoa.mde515.grad1353.query.StartQuery;
import gr.di.uoa.mde515.grad1353.similarity.Similarity;

public class DatasetParser {
	
	// private static final Logger LOGGER = Logger.getLogger(DatasetParser.class.getName());
	private static final String START_QUERY = "s";
	private static final String END_QUERY = "e";
	private static final String MATCH_DOCUMENT = "m";
	private static final String RESULTS_LETTER = "r";
	private static final String DELIMITER = " ";
	private static final String EXACT_MATCH = "MT_EXACT_MATCH";
	private static final String HAMMING_DIST = "MT_HAMMING_DIST";
	private static final String EDIT_DIST = "MT_EDIT_DIST";
	
	private Map<Integer, StartQuery> queryHashMap;
	private Map<Integer, MatchDocument> matchDocumentHashMap;
	private Map<String, ArrayList<Integer>> exactMatchQueriesIndex;
	
	public DatasetParser() {
		queryHashMap = new HashMap<Integer, StartQuery>();
		matchDocumentHashMap = new HashMap<Integer, MatchDocument>();
		exactMatchQueriesIndex = new HashMap<String, ArrayList<Integer>>();
	}

	public Results parseInput(final String datasetLine) {
		
		if (datasetLine.startsWith(START_QUERY)) {
			// example:
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
				
				final String currentQueryMatchType = MatchType.getMatchTypeFromInt(matchType);
				if (EXACT_MATCH.equals(currentQueryMatchType)) {

					// add index to words of startQueries
					ArrayList<Integer> currWordList = exactMatchQueriesIndex.get(currentWord);
					if (currWordList == null) {
						currWordList = new ArrayList<Integer>();
						currWordList.add(queryId);
						exactMatchQueriesIndex.put(currentWord, currWordList);
					} else {
						currWordList.add(queryId);
					}
				}
				
			}
			
			final StartQuery startQuery = new StartQuery(queryType, queryId, matchType, distance, numberOfKeywords, keywordsList);
			
			queryHashMap.put(queryId, startQuery);
			// LOGGER.log(Level.INFO, "Found Start Query with id: " + queryId);
			
		} else if (datasetLine.startsWith(END_QUERY)) {
			// example:
			// e 194
			final String[] endQueryParts = datasetLine.split(DELIMITER);
			// final String queryType = endQueryParts[0];	//'e' letter
			final int queryId = Integer.parseInt(endQueryParts[1]);
			// final EndQuery endQuery = new EndQuery(queryType, queryId);
			
			// remove queryId from Index
			updateExactMatchQueriesIndex(queryId);
			
			//kill query with id: queryId
			queryHashMap.remove(queryId);
			// LOGGER.log(Level.INFO, "Found End Query with id: " + queryId);
			
		} else if (datasetLine.startsWith(MATCH_DOCUMENT)) {
			// example:
			// m 1 1428 http dbpedia resource list women national basketball ...
			final String[] matchDocumentParts = datasetLine.split(DELIMITER);
			// final String matchDocumentLetter = matchDocumentParts[0];	//'m' letter
			final int matchDocumentId = Integer.parseInt(matchDocumentParts[1]);
			final int numberOfWords = Integer.parseInt(matchDocumentParts[2]);
			
			// final Set<String> wordsSet = new HashSet<String>();
			// no remove duplicates from documents words
			final List<String> wordsList = new ArrayList<String>();

			for (int i = 1; i <= numberOfWords; i++) {
				wordsList.add(matchDocumentParts[2+i]);
			}
			
			final MatchDocument matchDocument = new MatchDocument(matchDocumentId, numberOfWords, wordsList);
			matchDocumentHashMap.put(matchDocumentId, matchDocument);
			// LOGGER.log(Level.INFO, "Found DocumentMatch with id: " + matchDocumentId);			

			return retrieveResults(matchDocumentId);

		}

		return null;
	}
	
	// remove queryId from Index
	private void updateExactMatchQueriesIndex(final int queryId) {
		final StartQuery deletedStartQuery = queryHashMap.get(queryId);
		final String currentDeletedQueryMatchType = MatchType.getMatchTypeFromInt(deletedStartQuery.getMatchType());
		
		// start update index only on exact match type queries
		if (EXACT_MATCH.equals(currentDeletedQueryMatchType)) {
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
	
	private Results retrieveResults(final int matchDocumentId) {

		final List<Integer> queryIdsList = new ArrayList<Integer>();
		
		// Exact match using Index
		final Set<Integer> queryIdsSet = new HashSet<Integer>();
		final MatchDocument matchDocument = matchDocumentHashMap.get(matchDocumentId);
		// final Set<String> currentDocumentWordSet = matchDocument.getWordsSet();
		final List<String> currentDocumentWordSet = matchDocument.getWordsList();

		// for every word of current document
		for (final String documentWord : currentDocumentWordSet) {
			
			// hits the index of exact match queries for this word and retrieves the exact match queryIds
			final ArrayList<Integer> exactMatchQueryIdsList = exactMatchQueriesIndex.get(documentWord);
			
			if (exactMatchQueryIdsList != null) {
				
				// for every queryId of the exact match query list
				for (final Integer exactMatchQueryId : exactMatchQueryIdsList) {
					// LOGGER.log(Level.INFO, "Found currentQueryId: " + exactMatchQueryId);
					
					// retrieves the current start query to get its keyword list
					final StartQuery currentStartQuery = queryHashMap.get(exactMatchQueryId);
					
					if (currentStartQuery != null) {
						final Map<String, Boolean> exactMatchQueryWordsMap = new HashMap<String, Boolean>();
						final List<String> queryWordsList = currentStartQuery.getKeywordsList();
						
						for (final String currentQueryWord : queryWordsList) {
							//if (matchDocumentHashMap.get(matchDocumentId).getWordsSet().contains(currentQueryWord)) {
							if (Similarity.exactMatch(currentDocumentWordSet, currentQueryWord)) {
								exactMatchQueryWordsMap.put(currentQueryWord, Boolean.TRUE);
							}
						}
						
						if (exactMatchQueryWordsMap.size() == currentStartQuery.getNumberOfKeywords()) {
							queryIdsSet.add(currentStartQuery.getQueryId());
						}
						
					}
				}
			}
			
		}
		
		queryIdsList.addAll(queryIdsSet);
		
		for (final Map.Entry<Integer, StartQuery> query : queryHashMap.entrySet()) {
			final String matchType = MatchType.getMatchTypeFromInt(query.getValue().getMatchType());
			//LOGGER.log(Level.INFO, "Is going to execute matchType: " + matchType);
			
			/*
			if (EXACT_MATCH.equals(matchType)) {
				final Map<String, Boolean> exactMatchQueryWordsMap = new HashMap<String, Boolean>();
				for(final String queryWord : query.getValue().getKeywordsList()) {
					for (final String documentWord : currentDocumentWordSet) {
						if (Similarity.exactMatch(queryWord, documentWord)) {
							exactMatchQueryWordsMap.put(queryWord, Boolean.TRUE);
						}
					}
				}
				
				if (exactMatchQueryWordsMap.size() == query.getValue().getNumberOfKeywords())
					queryIdsList.add(query.getValue().getQueryId());
				
			} else 
			*/	
			
			if (HAMMING_DIST.equals(matchType)) {
				final Map<String, Integer> hammingDistanceQueryWordsMap = new HashMap<String, Integer>();
				for(final String queryWord: query.getValue().getKeywordsList()) {
					for(final String documentWord : currentDocumentWordSet) {
						final int currentHammingDistance = Similarity.hammingDistance(queryWord, documentWord);
						
						// LOGGER.log(Level.INFO, "The hamming distance of <" + queryWord + ", " + documentWord + "> is: " + currentHammingDistance);
						
						if (currentHammingDistance != -1 && currentHammingDistance <= query.getValue().getDistance()) {
							hammingDistanceQueryWordsMap.put(queryWord, currentHammingDistance);
						}
					}
				}
				
				if (hammingDistanceQueryWordsMap.size() == query.getValue().getNumberOfKeywords())
					queryIdsList.add(query.getValue().getQueryId());
				
			} else if (EDIT_DIST.equals(matchType)) {
				final Map<String, Integer> editDistanceQueryWordsMap = new HashMap<String, Integer>();
				for(final String queryWord : query.getValue().getKeywordsList()) {
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
				
				if (editDistanceQueryWordsMap.size() == query.getValue().getNumberOfKeywords())
					queryIdsList.add(query.getValue().getQueryId());
				
			}
			
		} //end for
		
		// save more memory removing processed matchDocumentId 
		matchDocumentHashMap.remove(matchDocumentId);
		
		Collections.sort(queryIdsList);
		
		return new Results(RESULTS_LETTER, matchDocumentId, queryIdsList.size(), queryIdsList);
	}
}
