package gr.di.uoa.mde515.grad1353;

import java.util.List;
import java.util.Set;

public class MatchDocument {
	
	private int matchDocumentId;
	private int numberOfWords;
	private List<String> wordsList;
//	private Set<String> wordsSet;

	public MatchDocument(final int matchDocumentId, final int numberOfWords, final List<String> wordsList) {
		this.matchDocumentId = matchDocumentId;
		this.numberOfWords = numberOfWords;
		this.wordsList = wordsList;
		//this.setWordsSet(wordsSet);
	}

	public int getMatchDocumentId() {
		return matchDocumentId;
	}

	public void setMatchDocumentId(int matchDocumentId) {
		this.matchDocumentId = matchDocumentId;
	}

	public int getNumberOfWords() {
		return numberOfWords;
	}

	public void setNumberOfWords(int numberOfWords) {
		this.numberOfWords = numberOfWords;
	}

//	public Set<String> getWordsSet() {
//		return wordsSet;
//	}
//
//	public void setWordsSet(Set<String> wordsSet) {
//		this.wordsSet = wordsSet;
//	}
	
	public List<String> getWordsList() {
		return wordsList;
	}

	public void setWordsList(List<String> wordsList) {
		this.wordsList = wordsList;
	}
	
	@Override
	public String toString() {
		return new StringBuilder().
				append("m").
				append(" ").
				append(getMatchDocumentId()).
				append(" ").
				append(getWordsList()).
				// append(getWordsSet()).
				toString();
	}
}
