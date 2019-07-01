import java.util.Arrays;
import java.util.Objects;

/**
 * Class representing a RGBA pixel.
 * <p>
 * This class stores 32-bit red, green, blue, and alpha pixel data according to the RGBA standard.
 * Red is stored in the lowest bits, followed by green, blue, and the alpha channel. 8 bits are
 * used for each value, resulting in a unsigned range of 0 to 255.
 * <p>
 * You may modify this class in certain ways, but should not make changes that could affect
 * testing.
 *
 * @see <a href="https://cs125.cs.illinois.edu/MP/4/">MP4 Documentation</a>
 * @see <a href="https://en.wikipedia.org/wiki/RGBA_color_space">RGBA pixel documentation</a>
 */
public class RGBAPixel {
    /** RGBA red shift value. */
    private static final int RED_SHIFT = 0;

    /** RGBA green shift value. */
    private static final int GREEN_SHIFT = 8;

    /** RGBA blue shift value. */
    private static final int BLUE_SHIFT = 16;

    /** RGBA alpha shift value. */
    private static final int ALPHA_SHIFT = 24;

    /** Bitmask for converting bytes to unsigned integers. */
    private static final int BYTE_MASK = 0xFF;

    /** Maximum channel value. */
    private static final int MAX_CHANNEL_VALUE = 255;

    /**
     * RGBAPixel value to use as filler when you don't have any valid data. All white with complete
     * transparency. DO NOT CHANGE THIS VALUE: the testing suite relies on it.
     */
    private static final RGBAPixel FILL_VALUE = new RGBAPixel(255, 255, 255, 0);

    /**
     * Return a new copied fill value.
     *
     * You should use this to get a fill value for your transformation functions.
     * This is because we test your output array to make sure that it is not a copy
     * of the input, and if you use the same fill pixel for all fill values
     * this test will fail.
     *
     * @return a new fill value RGBAPixel
     */
    public static RGBAPixel getFillValue() {
        return new RGBAPixel(FILL_VALUE);
    }

    /** Enumeration for get and set{Red,Green,Blue,Alpha}. */
    private enum PixelChannels {
        /** Get or set the red channel. */
        RED,

        /** Get or set the green channel. */
        GREEN,

        /** Get or set the blue channel. */
        BLUE,

        /** Get or set the alpha channel. */
        ALPHA
    }

    /**
     * Internal pixel data. Only modifier through get and set Red, Green, etc.
     */
    private int data;

    /**
     * Return the data for this pixel as an int.
     *
     * @return the data for this pixel as an int.
     */
    public int getData() {
        return this.data;
    }

    /**
     * Complete constructor for the RGBA pixel class.
     * <p>
     * Note that valid RGBA values are between 0 and 255, inclusive.
     * Parameters that are too large or too small will be trimmed appropriately.
     *
     * @param redValue the new pixel's red channel value
     * @param greenValue the new pixel's green channel value
     * @param blueValue the new pixel's blue channel value
     * @param alphaValue the new pixel's alpha channel value
     */
    public RGBAPixel(final int redValue, final int greenValue,
                     final int blueValue, final int alphaValue) {
        this.setRed(redValue);
        this.setGreen(greenValue);
        this.setBlue(blueValue);
        this.setAlpha(alphaValue);
    }

    /**
     * Complete constructor for the RGBA pixel class.
     * <p>
     * This differs from the int constructor because bytes have to be converted to
     * unsigned int values.
     *
     * @param redValue the new pixel's red channel value
     * @param greenValue the new pixel's green channel value
     * @param blueValue the new pixel's blue channel value
     * @param alphaValue the new pixel's alpha channel value
     */
    public RGBAPixel(final byte redValue, final byte greenValue,
                     final byte blueValue, final byte alphaValue) {
        this(redValue & BYTE_MASK, greenValue & BYTE_MASK,
                blueValue & BYTE_MASK, alphaValue & BYTE_MASK);
    }

    /**
     * Constructor for the RGBAPixel class that uses a single int.
     *
     * @param setData data to set.
     */
    public RGBAPixel(final int setData) {
        this.data = setData;
    }

    /**
     * Copy constructor for the RGBA pixel class.
     *
     * @param other the other RGBAPixel to copy
     */
    public RGBAPixel(final RGBAPixel other) {
        this.data = other.data;
    }

    /**
     * Get a pixel channel value: red, green, blue, or alpha.
     *
     * @param channel the channel (red, green, blue, or alpha) to return
     * @return the requested channel value
     */
    private int getComponent(final PixelChannels channel) {
        /*
         * Note that despite the fact that RED_SHIFT == 0, we preserve its
         * use below for clarity.
         */
        switch (channel) {
            case RED:
                return (data >> RED_SHIFT) & BYTE_MASK;
            case GREEN:
                return (data >> GREEN_SHIFT) & BYTE_MASK;
            case BLUE:
                return (data >> BLUE_SHIFT) & BYTE_MASK;
            case ALPHA:
                return (data >> ALPHA_SHIFT) & BYTE_MASK;
            default:
                throw new RuntimeException("Invalid channel value");
        }
    }

    /**
     * Get this pixel's red value.
     *
     * @return this pixel's red value
     */
    public int getRed() {
        return this.getComponent(PixelChannels.RED);
    }

    /**
     * Get this pixel's green value.
     *
     * @return this pixel's green value
     */
    public int getGreen() {
        return this.getComponent(PixelChannels.GREEN);
    }

    /**
     * Get this pixel's blue value.
     *
     * @return this pixel's blue value
     */
    public int getBlue() {
        return this.getComponent(PixelChannels.BLUE);
    }

    /**
     * Get this pixel's alpha value.
     *
     * @return this pixel's alpha value
     */
    public int getAlpha() {
        return this.getComponent(PixelChannels.ALPHA);
    }

    /**
     * Set a pixel channel value: red, green, blue, or alpha.
     *
     * @param channel the channel (red, green, blue, or alpha) to set
     * @param value the value to set the channel to
     * @return the new pixel value with the channel set as requested
     */
    private int setComponent(final PixelChannels channel, final int value) {
        int myValue = value;

        if (myValue < 0) {
            myValue = 0;
        } else if (myValue > MAX_CHANNEL_VALUE) {
            myValue = MAX_CHANNEL_VALUE;
        }
        switch (channel) {
            case RED:
                data &= (~(BYTE_MASK << RED_SHIFT));
                data |= ((myValue & BYTE_MASK) << RED_SHIFT);
                return this.getRed();
            case GREEN:
                data &= (~(BYTE_MASK << GREEN_SHIFT));
                data |= ((myValue & BYTE_MASK) << GREEN_SHIFT);
                return this.getGreen();
            case BLUE:
                data &= (~(BYTE_MASK << BLUE_SHIFT));
                data |= ((myValue & BYTE_MASK) << BLUE_SHIFT);
                return this.getBlue();
            case ALPHA:
                data &= (~(BYTE_MASK << ALPHA_SHIFT));
                data |= ((myValue & BYTE_MASK) << ALPHA_SHIFT);
                return this.getAlpha();
            default:
                throw new RuntimeException("Invalid component value");
        }
    }

    /**
     * Set this pixel's red value.
     * <p>
     * If a value less than zero is passed, the new value is set to 0.
     * If a value larger than 255 is passed, the new value is set to 255.
     *
     * @param setValue the new red value
     * @return this pixel's new red value
     */
    public int setRed(final int setValue) {
        return this.setComponent(PixelChannels.RED, setValue);
    }

    /**
     * Set this pixel's green value.
     * <p>
     * If a value less than zero is passed, the new value is set to 0.
     * If a value larger than 255 is passed, the new value is set to 255.
     *
     * @param setValue the new green value
     * @return this pixel's new green value
     */
    public int setGreen(final int setValue) {
        return this.setComponent(PixelChannels.GREEN, setValue);
    }

    /**
     * Set this pixel's blue value.
     * <p>
     * If a value less than zero is passed, the new value is set to 0.
     * If a value larger than 255 is passed, the new value is set to 255.
     *
     * @param setValue the new blue value
     * @return this pixel's new blue value
     */
    public int setBlue(final int setValue) {
        return this.setComponent(PixelChannels.BLUE, setValue);
    }

    /**
     * Set this pixel's alpha value.
     * <p>
     * If a value less than zero is passed, the new value is set to 0.
     * If a value larger than 255 is passed, the new value is set to 255.
     *
     * @param setValue the new alpha value
     * @return this pixel's new alpha value
     */
    public int setAlpha(final int setValue) {
        return this.setComponent(PixelChannels.ALPHA, setValue);
    }

    /*
     * toString autogenerated by IntelliJ.
     */
    @Override
    public String toString() {
        return "RGBAPixel{" + String.format("0x%08X", this.data) + '}';
    }

    /*
     * Equals autogenerated by IntelliJ.
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RGBAPixel rgbaPixel = (RGBAPixel) o;
        return this.data == rgbaPixel.data;
    }
    /*
     * hashCode autogenerated by IntelliJ.
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.data);
    }

    /**
     * Convert an array of ints to an array of RGBAPixels.
     * <p>
     * This is a convenience method for use by the testing suite.
     * Fails and returns null if top-level array has length 0, or if any inner array has length 0,
     * or if the two-dimensional array is not square.
     *
     * @param inputArray array of ints to convert to an array of RGBAPixel objects
     * @return the array of RGBAPixel objects, or null on failure.
     */
    public static RGBAPixel[][] fromIntArray(final int[][] inputArray) {
        if (inputArray == null) {
            return null;
        }
        if (inputArray.length == 0) {
            return null;
        }
        RGBAPixel[][] returnArray = new RGBAPixel[inputArray.length][inputArray[0].length];
        for (int i = 0; i < inputArray.length; i++) {
            if (inputArray[i].length == 0 || inputArray[i].length != inputArray[0].length) {
                return null;
            }
            for (int j = 0; j < inputArray[0].length; j++) {
                returnArray[i][j] = new RGBAPixel(inputArray[i][j]);
            }
        }
        return returnArray;
    }

    /**
     * Convert an array of RGBAPixels to an array of ints.
     * <p>
     * This is a convenience method for use by the testing suite.
     * Fails and returns null if top-level array has length 0, or if any inner array has length 0,
     * or if the two-dimensional array is not square.
     *
     * @param inputArray array of RGBAPixels to convert to an array of ints
     * @return the array of ints, or null on failure.
     */
    public static int[][] toIntArray(final RGBAPixel[][] inputArray) {
        if (inputArray == null) {
            return null;
        }
        if (inputArray.length == 0) {
            return null;
        }
        int[][] returnArray = new int[inputArray.length][inputArray[0].length];
        for (int i = 0; i < inputArray.length; i++) {
            if (inputArray[i].length == 0 || inputArray[i].length != inputArray[0].length) {
                return null;
            }
            for (int j = 0; j < inputArray[0].length; j++) {
                returnArray[i][j] = inputArray[i][j].getData();
            }
        }
        return returnArray;
    }

    /**
     * Perform a deep copy of a two-dimensional RGBAPixel array.
     * <p>
     * This is a convenience method largely for use by the testing suite.
     * Fails and returns null if top-level array has length 0, or if any inner array has length 0,
     * or if the two-dimensional array is not square.
     *
     * @param inputArray array of RGBAPixel objects to copy
     * @return copied array of RGBAPixel objects, or null on failure.
     */
    public static RGBAPixel[][] copyArray(final RGBAPixel[][] inputArray) {
        if (inputArray == null) {
            return null;
        }
        if (inputArray.length == 0) {
            return null;
        }
        RGBAPixel[][] returnArray = new RGBAPixel[inputArray.length][inputArray[0].length];
        for (int i = 0; i < inputArray.length; i++) {
            if (inputArray[i].length == 0 || inputArray[i].length != inputArray[0].length) {
                return null;
            }
            for (int j = 0; j < inputArray[0].length; j++) {
                returnArray[i][j] = new RGBAPixel(inputArray[i][j]);
            }
        }
        return returnArray;
    }

    /**
     * Nicely format an two-dimensional array of RGBAPixels.
     * <p>
     * Primarily intended for debugging.
     *
     * @param inputArray the two-dimensional RGBAPixel array to format
     * @return the array nicely formatted as a String for printing
     */
    public static String printArray(final RGBAPixel[][] inputArray) {
        if (inputArray == null) {
            return null;
        }
        if (inputArray.length == 0) {
            return null;
        }
        /*
         * Make sure inputArray is square
         * */
        for (int i = 0; i < inputArray.length; i++) {
            if (inputArray[i].length == 0 || inputArray[i].length != inputArray[0].length) {
                return null;
            }
        }

        /*
         * Transpose inputArray
         */
        RGBAPixel[][] transposedArray = new RGBAPixel[inputArray[0].length][inputArray.length];
        for (int i = 0; i < inputArray.length; i++) {
            for (int j = 0; j < inputArray[0].length; j++) {
                transposedArray[j][i] = inputArray[i][j];
            }
        }

        /*
         * Finally create the output string array.
         */
        String[] outputArray = new String[transposedArray.length];
        for (int i = 0; i < transposedArray.length; i++) {
            outputArray[i] = Arrays.deepToString(transposedArray[i]);
        }

        return String.join("\n", outputArray);
    }

    /**
     * Testing helper function to explain differences between two arrays.
     *
     * @param firstArray the first array to compare
     * @param secondArray the second array to compare
     * @return a String explaining differences between the two arrays
     */
    public static String diffArrays(final RGBAPixel[][] firstArray,
                                    final RGBAPixel[][] secondArray) {
        return "The two arrays must be different";
    }
}
