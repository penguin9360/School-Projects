/**
 * A class that tests whether two strings are anagrams.
 * <p>
 * The provided code is incomplete. Modify it so that it works properly and passes the tests in
 * <code>AnagramTest.java</code>.
 *
 * @see <a href="https://cs125.cs.illinois.edu/MP/2/">MP2 Documentation</a>
 * @see <a href="https://www.vocabulary.com/dictionary/anagram">Definition of anagram</a>
 */
public class TimesTable {
    /**
     * bullshit again.
     *
     * @param first  genalea
     * @param second freghoh
     * @return erhfoerg
     */
    public static int[][] createTimesTable(final int first, final int second) {
        int difference = second - first + 2;
        int i = 0;
        int j = 0;
        if (first <= 0 || second <= 0 || second <= first) {
            return null;
        }
        int[][] timestable = new int[difference][difference];
        for (i = 0; i < difference; i++) {
            for (j = 0; j < difference; j++) {
                if (j == 0 && i == 0) {
                    timestable[i][j] = 0;
                } else if (j == 0) {
                    timestable[i][j] = first + i - 1;
                } else if (i == 0) {
                    timestable[i][j] = first + j - 1;
                } else {
                    timestable[i][j] = timestable[i][0] * timestable[0][j];
                }
            }
        }
        return timestable;
    }
}
