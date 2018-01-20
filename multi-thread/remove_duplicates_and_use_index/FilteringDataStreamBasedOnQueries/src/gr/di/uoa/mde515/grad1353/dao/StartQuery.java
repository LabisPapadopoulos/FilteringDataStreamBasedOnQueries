package gr.di.uoa.mde515.grad1353.dao;

import java.util.List;

public class StartQuery extends QueryType {
	
	private int matchType;
	private int distance;
	private int numberOfKeywords;
	private List<String> keywordsList;

	public StartQuery(final String queryType, final int queryId, final int matchType, 
			final int distance, final int numberOfKeywords, final List<String> keywordsList) {
		super(queryType, queryId);
		this.matchType = matchType;
		this.distance = distance;
		this.numberOfKeywords = numberOfKeywords;
		this.keywordsList = keywordsList;
	}

	public int getMatchType() {
		return matchType;
	}

	public void setMatchType(int matchType) {
		this.matchType = matchType;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public int getNumberOfKeywords() {
		return numberOfKeywords;
	}

	public void setNumberOfKeywords(int numberOfKeywords) {
		this.numberOfKeywords = numberOfKeywords;
	}

	public List<String> getKeywordsList() {
		return keywordsList;
	}

	public void setKeywordsList(List<String> keywordsList) {
		this.keywordsList = keywordsList;
	}
	
	@Override
	public String toString() {
		return new StringBuilder().
				append(getQueryType()).
				append(" ").
				append(getQueryId()).
				append(" ").
				append(getMatchType()).
				append(" ").
				append(getDistance()).
				append(" ").
				append(getNumberOfKeywords()).
				append(" ").
				append(getKeywordsList()).
				toString();
	}

}
