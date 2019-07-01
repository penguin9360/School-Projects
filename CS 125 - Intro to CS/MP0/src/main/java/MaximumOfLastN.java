import java.util.ArrayList;
import java.util.Scanner;

/**
 * A class that computes the maximum of N values at the end of an array of doubles.
 * <p>
 * The provided code is incomplete and may contain errors. Modify it so that it produces the correct
 * output passes the unit tests in <code>MaximumOfLastNTest.java</code>.
 *
 * @see <a href="https://cs125.cs.illinois.edu/MP/0/">MP0 Documentation</a>
 */
public class MaximumOfLastN {
    /**
     * Returns the maximum of the last N double values stored in the provided array.
     * <p>
     * If n is negative or zero, the function should return 0.0. If the array is empty,
     * the function should also return 0.0.
     * <p>
     * Write this function.
     *
     * @param doubles an array of doubles
     * @param n number of values at the end of the array to consider when calculating the maximum
     * @return the maximum of the last N values in the array
     */
    public static double maximumOfLastN(final double[] doubles, final int n) {
            if (doubles.length == 0) {
                return 0.0;
            }
            if (n <= 0) {
                return 0.0;
            }
        double max = doubles[doubles.length - 1];
            for (int i = doubles.length - 1; i >= doubles.length - n; i--) {
                if (i < 0) {
                    break;
                }
                if (doubles[i] > max) {
                    max = doubles[i];
                }
            }
            return max;
    }

    /* ********************************************************************************************
     * You do not need to modify code below this comment.
     * ********************************************************************************************/

    /**
     * Solicits an integer N and multiple double values from the user and then computes the maximum of the last N
     * values passed.
     * <p>
     * You are free to review this function, but should not modify it. Note that this function is
     * <i>not used or tested by the test suite</i> as it is purely to aid your own interactive
     * testing.
     *
     * @param unused unused input parameters.
     */
    @SuppressWarnings("resource")
    public static void main(final String[] unused) {
        Scanner lineScanner = new Scanner(System.in);

        int n;
        ArrayList<Double> userInputs = new ArrayList<>();

        System.out.println("Enter an integer:");
        while (true) {
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
                continue;
            }
            n = inputScanner.nextInt();
            /*
             * If the line started with an integer but contains other tokens, reinitialize
             * userInput and prompt the user again.
             */
            if (inputScanner.hasNext()) {
                System.err.println("Invalid input: please enter only one integer per line.");
                continue;
            }
            inputScanner.close();
            break;
        }

        System.out.println("Enter doubles on successive lines, with a blank when you are done:");
        while (true) {
            String nextLine = lineScanner.nextLine();
            if (nextLine.trim().length() == 0) {
                break;
            }
            Scanner inputScanner = new Scanner(nextLine);
            if (!(inputScanner.hasNextDouble())) {
                System.err.println("Invalid input: please enter only doubles.");
                continue;
            }
            userInputs.add(inputScanner.nextDouble());
            /*
             * If the line started with an integer but contains other tokens, reinitialize
             * userInput and prompt the user again.
             */
            if (inputScanner.hasNext()) {
                System.err.println("Invalid input: please enter only one double per line.");
                continue;
            }
            inputScanner.close();
        }
        double[] inputs = userInputs.stream().mapToDouble(Double::doubleValue).toArray();
        System.out.println(String.format("The maximum of the last %d values is %f", n, maximumOfLastN(inputs, n)));
    }
}
