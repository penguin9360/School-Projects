import java.util.Arrays;
import java.util.Scanner;

/**
 * A class that tests whether two strings are anagrams.
 * <p>
 * The provided code is incomplete. Modify it so that it works properly and passes the tests in
 * <code>AnagramTest.java</code>.
 *
 * @see <a href="https://cs125.cs.illinois.edu/MP/2/">MP2 Documentation</a>
 * @see <a href="https://www.vocabulary.com/dictionary/anagram">Definition of anagram</a>
 */
public class Anagram {

    /**
     * Given two strings return true if they are anagrams of each other.
     * <p>
     * Two strings are anagrams if they contain the same letters but in a different order.
     * You should ignore case, punctuation, and whitespace. So only consider the letters A-Z,
     * a-z, and the numbers 0-9. And you should also require that the anagram use the same
     * letters the same number of times. (Some anagram definitions are more flexible.)
     *
     * <ul>
     *     <li>Example: "A decimal point" anagrams to "I’m a dot in place."</li>
     *     <li>Example: "rail safety" anagrams to "fairy tales".</li>
     * </ul>
     *
     * <p>
     * Either the first or second string may be null, in which case you should return false.
     * Empty strings require no special treatment.
     * <p>
     * Write this function.
     *
     * @param first the first string to use for the anagram comparison
     * @param second the second string to use for the anagram comparison
     * @return true if the two strings are anagrams ignoring case and punctuation
     * @see <a href="https://www.vocabulary.com/dictionary/anagram">Definition of anagram</a>
     */
    public static boolean areAnagrams(final String first, final String second) {
        if (first == null || second == null) {
            return false;
        }
        String firstTemp;
        firstTemp = first.replaceAll("[^A-Za-z0-9]", "");
        String secondTemp;
        secondTemp = second.replaceAll("[^A-Za-z0-9]", "");
        boolean status = true;
        if (firstTemp.length() != secondTemp.length()) {
            status = false;
        } else {
            char[] firstArray = firstTemp.toLowerCase().toCharArray();
            char[] secondArray = secondTemp.toLowerCase().toCharArray();
            Arrays.sort(firstArray);
            Arrays.sort(secondArray);
            for (int i = 0; i < firstArray.length; i++) {
                if (firstArray[i] != secondArray[i]) {
                    return false;
                } else {
                    status = true;
                }
            }
        }
        return status;
    }

    /* ********************************************************************************************
     * You do not need to modify code below this comment.
     * ********************************************************************************************/

    /**
     * Solicits two strings from the user and print if they are anagrams.
     * <p>
     * You are free to review this function, but should not modify it. Note that this function is
     * not tested by the test suite, as it is purely to aid your own interactive testing.
     *
     * @param unused unused input arguments
     */
    public static void main(final String[] unused) {
        Scanner lineScanner = new Scanner(System.in);
        System.out.println("Enter a string: ");
        String first = lineScanner.nextLine();
        System.out.println("Enter another string: ");
        String second = lineScanner.nextLine();
        if (areAnagrams(first, second)) {
            System.out.println("The two strings are anagrams");
        } else {
            System.out.println("The two strings are not anagrams");
        }
    }
}
