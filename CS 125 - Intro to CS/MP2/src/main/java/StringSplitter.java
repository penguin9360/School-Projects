import java.util.Arrays;
import java.util.Scanner;

/**
 * A class that splits a string on character change boundaries.
 * <p>
 * The provided code is incomplete. Modify it so that it works properly and passes the tests in
 * <code>StringSplitterTest.java</code>.
 *
 * @see <a href="https://cs125.cs.illinois.edu/MP/2/">MP2 Documentation</a>
 */
public class StringSplitter {
    /**
     * @param input grobaeb
     * @return gegste
     */
    public static String[] splitString(final String input) {
        if (input == null) {
            return null;
        }
        if (input.length() == 0) {
            return new String[0];
        }
        if (input.length() == 1) {
            String[] a = new String[1];
            a[0] = input;
            return a;
        }
        String splitedString = "";
        //ArrayList<String> splitedString = new ArrayList<String>();
        for (int i = 0; i < input.length() - 1; i++) {
            if (input.charAt(i) == input.charAt(i + 1)) {
                splitedString += input.charAt(i);
            } else {
                splitedString += input.charAt(i);
                splitedString += "@_@";
            }
        }
//        if (input.charAt(input.length() - 1) != input.charAt(input.length() - 2)) {
//            //wtf
//            splitedString += "@";
//            splitedString += input.charAt(input.length() - 1);
//        }
        splitedString += input.charAt(input.length() - 1);
//        } else splitedString += input.charAt(input.length() - 1);
        return splitedString.split("@_@");
    }

    /* ********************************************************************************************
     * You do not need to modify code below this comment.
     * ********************************************************************************************/

    /**
     * Solicit a string and split it whenever the character changes.
     * <p>
     * You are free to review this function, but should not modify it. Note that this function is
     * not tested by the test suite, as it is purely to aid your own interactive testing.
     *
     * @param unused unused input arguments
     */
    public static void main(final String[] unused) {
        Scanner lineScanner = new Scanner(System.in);
        System.out.println("Enter a string to split: ");
        String input = lineScanner.nextLine();
        System.out.println("This splits into: ");
        System.out.println(Arrays.toString(splitString(input)));
    }
}
