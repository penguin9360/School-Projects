import java.util.Random;
/**
 * Class that implements random dice.
 * <p>
 * Have a look through the file to see how a simple object class is constructed. The dice class has
 * a random number generator and an int number of sides. We can roll the die to get a random value
 * in the game.
 *
 * @see <a href="https://cs125.cs.illinois.edu/lab/4/">Lab 4 Description</a>
 * @see <a href="https://english.stackexchange.com/questions/167104/singular-of-dice"> Dice is
 *      Singular of Dice</a>
 */
public class Dice {

    /** Private random number generator. */
    Random myRand;

    /** Number of sides for this dice. */
    int numSides;

    /**
     * Create a new dice with a given number of sides.
     *
     * @param sides the number of sides our dice should have
     */
    public Dice(final int sides) {
        this.myRand = new Random();
        this.numSides = sides;
    }

    /**
     * Rolls our simulated dice.
     * <p>
     * Uses a pseudorandom number generator to determine the side to return.
     *
     * @return the number the die rolled
     */
    public int roll() {
        return (myRand.nextInt(numSides) + 1);
    }
}
