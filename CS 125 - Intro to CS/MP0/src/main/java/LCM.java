import java.util.ArrayList;
import java.util.Scanner;

/**
 * A class that computes the least common multiple (LCM) of two integers.
 * <p>
 * The provided code is incomplete. Complete it so that it calculates LCM correctly and passes the unit tests in
 * <code>LCMTest.java</code>.
 *
 * @see <a href="https://cs125.cs.illinois.edu/MP/0/">MP0 Documentation</a>
 */
public final class LCM {

    /**
     *
     */
    public static final int LCM_INVALID = -1;

    /**
     * Returns the least common multiple of two integers.
     * <p>
     * Write this function.
     * <p>
     * If either integer is zero, you should return LCM_INVALID. Note that the least common multiple is well-defined
     * for negative numbers. For example, the LCM of -5 and -4 is 20, and the LCM of -4 and -2 is 4. You need to
     * correctly handle this case.
     *
     * @param first  an integer.
     * @param second an integer.
     * @return the least common multiple of the two integers.
     * @see <a href="https://en.wikipedia.org/wiki/Least_common_multiple">Least common multiple</a>
     */
    public static int lcm(final int first, final int second) {
        if (first == 0 || second == 0) {
            return LCM_INVALID;
        }
        int result = (first * second) / gcd(first, second);
        return Math.abs(result);
    }
    /**
     * Returns the least common multiple of two integers.
     * <p>
     * Write this function.
     * <p>
     * If either integer is zero, you should return LCM_INVALID. Note that the least common multiple is well-defined
     * for negative numbers. For example, the LCM of -5 and -4 is 20, and the LCM of -4 and -2 is 4. You need to
     * correctly handle this case.
     * @param first fuifpfo9
     * @param second igigogu
     * @return iugovouvuo
     */
    private static int gcd(final int first, final int second) {
        int a = first;
        int b = second;
        int r = a % b;
        while (r != 0) {
            a = b;
            b = r;
            r = a % b;
        }
        return b;

    }
    /* ********************************************************************************************
     * You do not need to modify code below this comment.
     * ********************************************************************************************/

    /**
     * Solicits two integers from the user at the command line and tries to compute the least common multiple of the
     * two values.
     * <p>
     * You are free to review this function, but should not modify it. Note that this function is
     * not tested by the test suite, as it is purely to aid your own interactive testing.
     *
     * @param unused unused input parameters.
     */
    public static void main(final String[] unused) {
        String inputPrompt = "Enter two integers on successive lines:";
        Scanner lineScanner = new Scanner(System.in);
        ArrayList<Integer> userInputs;

        restart : while (true) {
            System.out.println(inputPrompt);
            userInputs = new ArrayList<>();

            for (int inputCount = 0; inputCount < 2; inputCount++) {
                /*
                 * We could just use lineScanner.hasNextInt() and not initialize a separate scanner.
                 * But the default Scanner class ignores blank lines and continues to search for
                 * input until a non-empty line is entered. This approach allows us to detect empty
                 * lines and remind the user to provide a valid input.
                 */
                String nextLine = lineScanner.nextLine();
                Scanner inputScanner = new Scanner(nextLine);
                if (!(inputScanner.hasNextInt())) {
                    System.err.println("Invalid input: please enter only integers.");
                    continue restart;
                }
                userInputs.add(inputScanner.nextInt());
                /*
                 * If the line started with an integer but contains other tokens, reinitialize
                 * userInput and prompt the user again.
                 */
                if (inputScanner.hasNext()) {
                    System.err.println("Invalid input: please enter only one integer per line.");
                    continue restart;
                }
                inputScanner.close();
            }

            System.out.println(String.format("LCM is %d", lcm(userInputs.get(0), userInputs.get(1))));
            break;
        }
    }
}
