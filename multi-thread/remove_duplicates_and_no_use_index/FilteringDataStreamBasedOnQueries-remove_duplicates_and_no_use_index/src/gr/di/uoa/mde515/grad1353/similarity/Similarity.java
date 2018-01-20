package gr.di.uoa.mde515.grad1353.similarity;

import java.util.Set;

public class Similarity {
	
	private Similarity() {}

	public static boolean exactMatch(final Set<String> wordsSet, final String token) {
		return wordsSet.contains(token);
	}
	
	public static boolean exactMatch(final String str1, final String str2) {
		return str1.equals(str2);
	}
	
	// http://theoryofprogramming.com/2016/11/06/dynamic-programming-edit-distance/
	public static int editDistance(String word1, String word2) {
        if (word1.length() < word2.length()) {
            String temp = word1;
            word1 = word2;
            word2 = temp;
        }

        // b is the shorter string
        int[] previous = new int[word2.length() + 1];

        for (int i = 0; i <= word2.length(); ++i) {
            previous[i] = i;
        }

        int[] current = new int[word2.length() + 1];

        for (int i = 1; i <= word1.length(); ++i) {
            current[0] = i;

            for (int j = 1; j <= word2.length(); ++j) {
                if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
                    current[j] = previous[j - 1];
                } else {
                    current[j] = 1 + Math.min(previous[j - 1],
                                  Math.min(previous[j], current[j - 1]));
                }
            }

            for (int j = 0; j <= word2.length(); ++j) {
                previous[j] = current[j];
                current[j] = 0;
            }
        }

        return previous[word2.length()];
    }

	public static int hammingDistance(final String word1, final String word2) {
		int hammingDistance = 0;
		
		if (word1.length() != word2.length())
			return -1;
		
		for (int i = 0; i < word1.length(); i++) {
			if (word1.charAt(i) != word2.charAt(i))
				hammingDistance++;
		}
		
		return hammingDistance;
	}
}
