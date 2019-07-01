import java.util.LinkedHashMap;
import java.util.Map;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test suite for the Quizzer class.
 * <p>
 * The provided test suite is correct and complete. You should not need to modify it. However, you
 * should understand it. You may need to augment or write test suites for later MPs.
 *
 * @see <a href="https://cs125.cs.illinois.edu/MP/0/">MP0 Documentation</a>
 */
@Test
public class QuizzerTest {

    /** Testing timeout. Solution takes 38 ms. */
    private static final int TEST_TIMEOUT = 500;

    /**
     * Test with precomputed values.
     */
    @Test(timeOut = TEST_TIMEOUT)
    public void test() {
        for (Map.Entry<QuizzerInput, Integer> precomputed : PRECOMPUTED_RESULTS.entrySet()) {
            QuizzerInput input = precomputed.getKey();
            int output = precomputed.getValue();
            Assert.assertEquals(
                    Quizzer.computeScore(input.diversityAnswerCorrect,
                            input.illiacAnswerCorrect, input.mosaicAnswerCorrect,
                            input.variableAnswerCorrect),
                    output, String.format("Result for %s is incorrect", input.toString()));
        }
    }

    /** Testing class. */
    public static class QuizzerInput {

        /** Whether the diversity answer is correct. */
        final Boolean diversityAnswerCorrect;

        /** Whether the ILLIAC answer is correct. */
        final Boolean illiacAnswerCorrect;

        /** Whether the mosaic answer is correct. */
        final Boolean mosaicAnswerCorrect;

        /** Whether the variable answer is correct. */
        final Boolean variableAnswerCorrect;

        /**
         * Create a new quizzer input.
         *
         * @param setDiversityAnswerCorrect whether the diversity answer is correct
         * @param setIlliacAnswerCorrect whether the ILLIAC answer is correct
         * @param setMosaicAnswerCorrect whether the Mosaic answer is correct
         * @param setVariableAnswerCorrect whether the variable answer is correct
         */
        QuizzerInput(final Boolean setDiversityAnswerCorrect,
                     final Boolean setIlliacAnswerCorrect, final Boolean setMosaicAnswerCorrect,
                     final Boolean setVariableAnswerCorrect) {
            super();
            this.diversityAnswerCorrect = setDiversityAnswerCorrect;
            this.illiacAnswerCorrect = setIlliacAnswerCorrect;
            this.mosaicAnswerCorrect = setMosaicAnswerCorrect;
            this.variableAnswerCorrect = setVariableAnswerCorrect;
        }

        @Override
        public final String toString() {
            return "QuizzerInput [diversityAnswerCorrect="
                    + diversityAnswerCorrect + ", illiacAnswerCorrect=" + illiacAnswerCorrect
                    + ", mosaicAnswerCorrect=" + mosaicAnswerCorrect + ", variableAnswerCorrect="
                    + variableAnswerCorrect + "]";
        }

        @Override
        public final int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result
                    + ((diversityAnswerCorrect == null) ? 0 : diversityAnswerCorrect.hashCode());
            result = prime * result
                    + ((illiacAnswerCorrect == null) ? 0 : illiacAnswerCorrect.hashCode());
            result = prime * result
                    + ((mosaicAnswerCorrect == null) ? 0 : mosaicAnswerCorrect.hashCode());
            result = prime * result
                    + ((variableAnswerCorrect == null) ? 0 : variableAnswerCorrect.hashCode());
            return result;
        }

        @Override
        public final boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            QuizzerInput other = (QuizzerInput) obj;
            if (diversityAnswerCorrect == null) {
                if (other.diversityAnswerCorrect != null) {
                    return false;
                }
            } else if (!diversityAnswerCorrect.equals(other.diversityAnswerCorrect)) {
                return false;
            }
            if (illiacAnswerCorrect == null) {
                if (other.illiacAnswerCorrect != null) {
                    return false;
                }
            } else if (!illiacAnswerCorrect.equals(other.illiacAnswerCorrect)) {
                return false;
            }
            if (mosaicAnswerCorrect == null) {
                if (other.mosaicAnswerCorrect != null) {
                    return false;
                }
            } else if (!mosaicAnswerCorrect.equals(other.mosaicAnswerCorrect)) {
                return false;
            }
            if (variableAnswerCorrect == null) {
                return other.variableAnswerCorrect == null;
            } else {
                return variableAnswerCorrect.equals(other.variableAnswerCorrect);
            }
        }
    }

    /** Pre-computed testing values. */
    private static final Map<QuizzerInput, Integer> PRECOMPUTED_RESULTS =
            new LinkedHashMap<QuizzerInput, Integer>() {
                {
                    put(new QuizzerInput(false, false, false, false), 0);
                    put(new QuizzerInput(false, false, false, false), 0);

                    put(new QuizzerInput(true, false, false, false), 10);
                    put(new QuizzerInput(false, true, false, false), 10);

                    put(new QuizzerInput(true, true, true, false), 30);
                    put(new QuizzerInput(true, true, false, true), 30);
                    put(new QuizzerInput(true, false, true, true), 30);

                    put(new QuizzerInput(true, true, true, true), 40);
                }
            };
}
