/**
 * A class that represents a DNA sequence.
 * <p>
 * Internally the class represents DNA as a string. You are responsible for implementing the longest
 * common subsequence method and a straightforward constructor.
 * <p>
 * The DNA class does not need to provide a setter for the sequence, meaning that it can be allowed
 * to not change after the instance is created. As a result, it should not provide an empty
 * constructor.
 *
 * @see <a href="https://cs125.cs.illinois.edu/MP/5/">MP5 Documentation</a>
 */
public class DNA {

    /**
     * Sequence of base pairs for this DNA instance.
     */
    private String sequence;

    /**
     * Gets the sequence of base pairs for this DNA instance.
     *
     * @return the sequence of base pairs for this DNA instance
     */
    public String getSequence() {
        return sequence;
    }

    /**
     * Create a new DNA instance from the given sequence of base pairs.
     * <p>
     * The constructor should validate and normalize its inputs. All characters in the string should
     * be from the set A, T, C, and G (this in DNA, not RNA). You should accept lower-case inputs
     * but convert them to upper-case for the purposes of later comparison.
     *
     * @param setSequence the sequence of base pairs to initialize the instance with
     */
    public DNA(final String setSequence) {
        sequence = setSequence.toUpperCase();
    }

    /**
     * @param str1 rgvegegre.
     * @param str2 ergregreg.
     * @return ergergre.
     */
    public static String gcs(final String str1, final String str2) {
        if (str1.equals("") || str2.equals("")) {
            return "";
        } else {
            char first1 = str1.charAt(0);
            char first2 = str2.charAt(0);
            String rest1 = str1.substring(1, str1.length());
            String rest2 = str2.substring(1, str2.length());
            if (first1 == first2) {
                return first1 + gcs(rest1, rest2);
            } else {
                String gcs1 = gcs(str1, rest2);
                String gcs2 = gcs(rest1, str2);
                if (gcs1.length() > gcs2.length()) {
                    return gcs1;
                } else {
                    return gcs2;
                }
            }
        }
    }

    /**
     * @param firstSequence regerge.
     * @param secondSequece ebbthr.
     * @return gerh.
     */
    public static DNA getLongestCommonSubsequence(final DNA firstSequence,
                                                  final DNA secondSequece) {
        return new DNA(gcs(firstSequence.getSequence(), secondSequece.getSequence()));
    }

}
