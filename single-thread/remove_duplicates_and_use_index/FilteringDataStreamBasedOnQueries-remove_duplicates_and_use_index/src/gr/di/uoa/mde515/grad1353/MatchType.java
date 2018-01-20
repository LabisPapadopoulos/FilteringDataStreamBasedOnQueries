package gr.di.uoa.mde515.grad1353;

public enum MatchType {
	MT_EXACT_MATCH(0),
	MT_HAMMING_DIST(1),
	MT_EDIT_DIST(2);
	
	private final int value;
	
	private MatchType(final int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
	public static String getMatchTypeFromInt(final int value) {
		for (final MatchType matchType : MatchType.values()) {
			if (matchType.getValue() == value)
				return matchType.toString();
		}
		
		throw new IllegalArgumentException("The given number doesn't match any MatchType");
	}
}
