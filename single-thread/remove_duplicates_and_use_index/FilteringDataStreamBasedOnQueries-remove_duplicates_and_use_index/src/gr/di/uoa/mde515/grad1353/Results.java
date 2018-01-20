package gr.di.uoa.mde515.grad1353;

import java.util.List;

public class Results {
	
	private static final String BLANK_SPACE = " ";
	
	private String resultLetter;
	private int matchDocumentId;
	private int numberOfQueryIds;
	private List<Integer> queryIds;

	public Results(final String resultLetter, final int matchDocumentId, final int numberOfQueryIds, final List<Integer> queryIds) {
		this.resultLetter = resultLetter;
		this.matchDocumentId = matchDocumentId;
		this.numberOfQueryIds = numberOfQueryIds;
		this.queryIds = queryIds;
	}
	
	public String getResultLetter() {
		return resultLetter;
	}

	public void setResultLetter(String resultLetter) {
		this.resultLetter = resultLetter;
	}
	
	public int getMatchDocumentId() {
		return matchDocumentId;
	}

	public void setMatchDocumentId(int matchDocumentId) {
		this.matchDocumentId = matchDocumentId;
	}
	
	public int getNumberOfQueryIds() {
		return numberOfQueryIds;
	}

	public void setNumberOfQueryIds(int numberOfQueryIds) {
		this.numberOfQueryIds = numberOfQueryIds;
	}
	
	public List<Integer> getQueryIds() {
		return queryIds;
	}

	public void setQueryIds(List<Integer> queryIds) {
		this.queryIds = queryIds;
	}
	
	@Override
	public String toString() {
		final StringBuilder resultsStringBuilder = new StringBuilder().
				append(getResultLetter()).
				append(BLANK_SPACE).
				append(getMatchDocumentId()).
				append(BLANK_SPACE).
				append(getNumberOfQueryIds()).
				append(BLANK_SPACE);
		
		final StringBuilder queryIdsBuilder = new StringBuilder();
		int currentSize = 1;
		
		for (final Integer currentQueryId : getQueryIds()) {
			if (currentSize != getQueryIds().size()) {
				queryIdsBuilder.append(currentQueryId).append(BLANK_SPACE);
				currentSize++;
			} else {
				queryIdsBuilder.append(currentQueryId);
			}
		}
		
		resultsStringBuilder.append(queryIdsBuilder);
		
		return resultsStringBuilder.toString();
	}
}
