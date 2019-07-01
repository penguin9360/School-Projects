//import java.awt.image.RGBImageFilter;

/**
 * eilrierhfi.
 */
public class Transform {
    /**
     * irngogno.
     */
    public static final int DEFAULT_COLOR_SHIFT = 32;
    /**
     * jepjgpe.
     */
    public static final int DEFAULT_POSITION_SHIFT = 16;
    /**
     * fwoeihwei.
     */
    public static final int DEFAULT_RESIZE_AMOUNT = 2;

    /**
     *  fewfwe.
     */
    public Transform() {

    }

    /**
     *
     * @param originalImage gieogieo.
     * @param amount fwmfw.
     * @return wfeiojfw.
     */
    public static RGBAPixel[][] expandHorizontal(final RGBAPixel[][] originalImage,
                                                 final int amount) {
        int count = 0;
        RGBAPixel[][] tempnewImage =
                new RGBAPixel[amount * originalImage.length][originalImage[0].length];
        RGBAPixel[][] newImage = new RGBAPixel[originalImage.length][originalImage[0].length];
        for (int j = 0; j < tempnewImage[0].length; j++) {
            for (int i = 0; i < tempnewImage.length; i += amount) {
                tempnewImage[i][j] = originalImage[i / amount][j];
                while (count < amount) {
                    tempnewImage[i + count][j] = tempnewImage[i][j];
                    count++;
                }
                count = 0;
            }
        }
        int centerOfTemp = tempnewImage.length / 2;
        int topLeftOfNew = centerOfTemp - (originalImage.length / 2);
        for (int a = 0; a < originalImage.length; a++) {
            for (int b = 0; b < originalImage[0].length; b++) {
                newImage[a][b] = tempnewImage[a + topLeftOfNew][b];
            }
        }
        return newImage;
    }

    /**
     *
     * @param originalImage gieogieo.
     * @param amount fwmfw.
     * @return wfeiojfw.
     */
    public static RGBAPixel[][] expandVertical(final RGBAPixel[][] originalImage,
                                               final int amount) {
        int count = 0;
        RGBAPixel[][] tempnewImage =
                new RGBAPixel[originalImage.length][amount * originalImage[0].length];
        RGBAPixel[][] newImage = new RGBAPixel[originalImage.length][originalImage[0].length];
        for (int i = 0; i < tempnewImage.length; i++) {
            for (int j = 0; j < tempnewImage[0].length; j += amount) {
                tempnewImage[i][j] = originalImage[i][j / amount];
                while (count < amount) {
                    tempnewImage[i][j + count] = tempnewImage[i][j];
                    count++;
                }
                count = 0;
            }
        }
        int centerOfTemp = tempnewImage[0].length / 2;
        int topLeftOfNew = centerOfTemp - (originalImage[0].length / 2);
        for (int a = 0; a < originalImage[0].length; a++) {
            for (int b = 0; b < originalImage.length; b++) {
                newImage[b][a] = tempnewImage[b][a + topLeftOfNew];
            }
        }
        return newImage;
    }


    /**
     *
     * @param originalImage gieogieo.
     * @return wfeiojfw.
     */
    public static RGBAPixel[][] flipHorizontal(final RGBAPixel[][] originalImage) {
        RGBAPixel[][] newImage = new RGBAPixel[originalImage.length][originalImage[0].length];
        if (originalImage.length != 0 && originalImage[0].length != 0) {
            for (int i = 0; i < originalImage.length; i++) {
                for (int j = 0; j < originalImage[0].length; j++) {
                    newImage[i][j] = originalImage[originalImage.length - 1 - i][j];
                }
            }
        }
        return newImage;
    }

    /**
     *
     * @param originalImage gieogieo.
     * @return wfeiojfw.
     */
    public static RGBAPixel[][] flipVertical(final RGBAPixel[][] originalImage) {
        RGBAPixel[][] newImage = new RGBAPixel[originalImage.length][originalImage[0].length];
        if (originalImage.length != 0 && originalImage[0].length != 0) {
            for (int i = 0; i < originalImage.length; i++) {
                for (int j = 0; j < originalImage[0].length; j++) {
                    newImage[i][j] = originalImage[i][originalImage[0].length - 1 - j];
                }
            }
        }
        return newImage;
    }

    /**
     *
     * @param originalImage gieogieo.
     * @return wfeiojfw.
     */
    public static RGBAPixel[][] greenScreen(final RGBAPixel[][] originalImage) {
        RGBAPixel[][] newImage = new RGBAPixel[originalImage.length][originalImage[0].length];
        for (int i = 0; i < originalImage.length; i++) {
            for (int j = 0; j < originalImage[0].length; j++) {
                newImage[i][j] = originalImage[i][j];
                if (newImage[i][j].getGreen() > newImage[i][j].getRed()
                        && newImage[i][j].getGreen() > newImage[i][j].getBlue()) {
                    newImage[i][j] = RGBAPixel.getFillValue();
                }
            }
        }
        return newImage;
    }

    /**
     *
     * @param originalImage rgfwrgw.
     * @param amount regregregre.
     * @return trhryjs.
     */
    public static RGBAPixel[][] lessAlpha(final RGBAPixel[][] originalImage, final int amount) {
        RGBAPixel[][] newImage = new RGBAPixel[originalImage.length][originalImage[0].length];
        for (int i = 0; i < originalImage.length; i++) {
            for (int j = 0; j < originalImage[0].length; j++) {
                newImage[i][j] = new RGBAPixel(originalImage[i][j]);
                int alpha = newImage[i][j].getAlpha() - amount;
                newImage[i][j].setAlpha(alpha);
            }
        }
        return newImage;
    }

    /**
     *
     * @param originalImage gieogieo.
     * @param amount fwmfw.
     * @return wfeiojfw.
     */
    public static RGBAPixel[][] lessBlue(final RGBAPixel[][] originalImage, final int amount) {
        RGBAPixel[][] newImage = new RGBAPixel[originalImage.length][originalImage[0].length];
        for (int i = 0; i < originalImage.length; i++) {
            for (int j = 0; j < originalImage[0].length; j++) {
                newImage[i][j] = new RGBAPixel(originalImage[i][j]);
                int blue = newImage[i][j].getBlue() - amount;
                newImage[i][j].setBlue(blue);
            }
        }
        return newImage;
    }

    /**
     *
     * @param originalImage gieogieo.
     * @param amount fwmfw.
     * @return wfeiojfw.
     */
    public static RGBAPixel[][] lessGreen(final RGBAPixel[][] originalImage, final int amount) {
        RGBAPixel[][] newImage = new RGBAPixel[originalImage.length][originalImage[0].length];
        for (int i = 0; i < originalImage.length; i++) {
            for (int j = 0; j < originalImage[0].length; j++) {
                newImage[i][j] = new RGBAPixel(originalImage[i][j]);
                int green = newImage[i][j].getGreen() - amount;
                newImage[i][j].setGreen(green);
            }
        }
        return newImage;
    }

    /**
     *
     * @param originalImage gieogieo.
     * @param amount fwmfw.
     * @return wfeiojfw.
     */
    public static RGBAPixel[][] lessRed(final RGBAPixel[][] originalImage, final int amount) {
        RGBAPixel[][] newImage = new RGBAPixel[originalImage.length][originalImage[0].length];
        for (int i = 0; i < originalImage.length; i++) {
            for (int j = 0; j < originalImage[0].length; j++) {
                newImage[i][j] = new RGBAPixel(originalImage[i][j]);
                int red = newImage[i][j].getRed() - amount;
                newImage[i][j].setRed(red);
            }
        }
        return newImage;
    }
    /**
     *
     * @param originalImage gieogieo.
     * @param amount fwmfw.
     * @return wfeiojfw.
     */
    public static RGBAPixel[][] moreAlpha(final RGBAPixel[][] originalImage, final int amount) {
        RGBAPixel[][] newImage = new RGBAPixel[originalImage.length][originalImage[0].length];
        for (int i = 0; i < originalImage.length; i++) {
            for (int j = 0; j < originalImage[0].length; j++) {
                newImage[i][j] = new RGBAPixel(originalImage[i][j]);
                int alpha = newImage[i][j].getAlpha() + amount;
                newImage[i][j].setAlpha(alpha);
            }
        }
        return newImage;
    }

    /**
     *
     * @param originalImage gieogieo.
     * @param amount fwmfw.
     * @return wfeiojfw.
     */
    public static RGBAPixel[][] moreBlue(final RGBAPixel[][] originalImage, final int amount) {
        RGBAPixel[][] newImage = new RGBAPixel[originalImage.length][originalImage[0].length];
        for (int i = 0; i < originalImage.length; i++) {
            for (int j = 0; j < originalImage[0].length; j++) {
                newImage[i][j] = new RGBAPixel(originalImage[i][j]);
                int blue = newImage[i][j].getBlue() + amount;
                newImage[i][j].setBlue(blue);
            }
        }
        return newImage;
    }

    /**
     *
     * @param originalImage gieogieo.
     * @param amount fwmfw.
     * @return wfeiojfw.
     */
    public static RGBAPixel[][] moreGreen(final RGBAPixel[][] originalImage, final int amount) {
        RGBAPixel[][] newImage = new RGBAPixel[originalImage.length][originalImage[0].length];
        for (int i = 0; i < originalImage.length; i++) {
            for (int j = 0; j < originalImage[0].length; j++) {
                newImage[i][j] = new RGBAPixel(originalImage[i][j]);
                int green = newImage[i][j].getGreen() + amount;
                newImage[i][j].setGreen(green);
            }
        }
        return newImage;
    }

    /**
     *
     * @param originalImage gieogieo.
     * @param amount fwmfw.
     * @return wfeiojfw.
     */
    public static RGBAPixel[][] moreRed(final RGBAPixel[][] originalImage, final int amount) {
        RGBAPixel[][] newImage = new RGBAPixel[originalImage.length][originalImage[0].length];
        for (int i = 0; i < originalImage.length; i++) {
            for (int j = 0; j < originalImage[0].length; j++) {
                newImage[i][j] = new RGBAPixel(originalImage[i][j]);
                int red = newImage[i][j].getRed() + amount;
                newImage[i][j].setRed(red);
            }
        }
        return newImage;
    }

    /**
     *
     * @param originalImage gieogieo.
     * @return wfeiojfw.
     */
    public static RGBAPixel[][] mystery(final RGBAPixel[][] originalImage) {
        return null;
    }

    /**
     *
     * @param originalImage gieogieo.
     * @return wfeiojfw.
     */
    public static RGBAPixel[][] rotateLeft(final RGBAPixel[][] originalImage) {
        RGBAPixel[][] newImage = new RGBAPixel[originalImage.length][originalImage[0].length];
        double centerX = (originalImage.length - 1) / 2.0;
        double centerY = (originalImage[0].length - 1) / 2.0;
        if (originalImage.length == 0 || originalImage[0].length == 0) {
            return null;
        }
        for (int i = 0; i < originalImage.length; i++) {
            for (int j = 0; j < originalImage[0].length; j++) {
                double relativeX = i - centerX;
                double relativeY = j - centerY;
                int newX = (int) Math.round(centerX + relativeY);
                int newY = (int) Math.round(centerY - relativeX);
                if (newX >= 0 && newX <= originalImage.length - 1
                        && newY >= 0  && newY <= originalImage[0].length - 1) {
                    newImage[newX][newY] = originalImage[i][j];
                }
            }
        }
        for (int a = 0; a < newImage.length; a++) {
            for (int b = 0; b < newImage[0].length; b++) {
                if (newImage[a][b] == null) {
                    newImage[a][b] = RGBAPixel.getFillValue();
                }
            }
        }
        return newImage;
    }

    /**
     *
     * @param originalImage gieogieo.
     * @return wfeiojfw.
     */
    public static RGBAPixel[][] rotateRight(final RGBAPixel[][] originalImage) {
        RGBAPixel[][] newImage = new RGBAPixel[originalImage.length][originalImage[0].length];
        double centerX = (originalImage.length - 1) / 2.0;
        double centerY = (originalImage[0].length - 1) / 2.0;
        if (originalImage.length == 0 || originalImage[0].length == 0) {
            return null;
        }
        for (int i = 0; i < originalImage.length; i++) {
            for (int j = 0; j < originalImage[0].length; j++) {
                double relativeX = i - centerX;
                double relativeY = j - centerY;
                int newX = (int) Math.round(centerX - relativeY);
                int newY = (int) Math.round(centerY + relativeX);
                if (newX >= 0 && newX <= originalImage.length - 1 && newY >= 0
                        && newY <= originalImage[0].length - 1) {
                    newImage[newX][newY] = originalImage[i][j];
                }
            }
        }
        for (int a = 0; a < newImage.length; a++) {
            for (int b = 0; b < newImage[0].length; b++) {
                if (newImage[a][b] == null) {
                    newImage[a][b] = RGBAPixel.getFillValue();
                }
            }
        }
        return newImage;
    }


    /**
     *
     * @param originalImage gieogieo.
     * @param amount fwmfw.
     * @return wfeiojfw.
     */
    public static RGBAPixel[][] shiftDown(final RGBAPixel[][] originalImage, final int amount) {
        return shiftUp(originalImage, -amount);
    }

    /**
     *
     * @param originalImage gieogieo.
     * @param amount fwmfw.
     * @return wfeiojfw.
     */
    public static RGBAPixel[][] shiftLeft(final RGBAPixel[][] originalImage, final int amount) {
        RGBAPixel[][] newImage = new RGBAPixel[originalImage.length][originalImage[0].length];
        for (int i = 0; i < originalImage.length; i++) {
            for (int j = 0; j < originalImage[0].length; j++) {
                if (i + amount < originalImage.length && i + amount >= 0) {
                    newImage[i][j] = originalImage[i + amount][j];
                } else {
                    newImage[i][j] = RGBAPixel.getFillValue();
                }
            }
        }
        return newImage;
    }

    /**
     *
     * @param originalImage gieogieo.
     * @param amount fwmfw.
     * @return wfeiojfw.
     */
    public static RGBAPixel[][] shiftUp(final RGBAPixel[][] originalImage, final int amount) {
        RGBAPixel[][] newImage = new RGBAPixel[originalImage.length][originalImage[0].length];
        for (int i = 0; i < originalImage.length; i++) {
            for (int j = 0; j < originalImage[0].length; j++) {
                if (j + amount < originalImage[0].length && j + amount >= 0) {
                    newImage[i][j] = originalImage[i][j + amount];
                } else {
                    newImage[i][j] = RGBAPixel.getFillValue();
                }
            }
        }
        return newImage;
    }

    /**
     *
     * @param originalImage gieogieo.
     * @param amount fwmfw.
     * @return wfeiojfw.
     */
    public static RGBAPixel[][] shiftRight(final RGBAPixel[][] originalImage, final int amount) {
        RGBAPixel[][] newImage = new RGBAPixel[originalImage.length][originalImage[0].length];
        return shiftLeft(originalImage, -amount);
    }

    /**
     *
     * @param originalImage gieogieo.
     * @param amount fwmfw.
     * @return wfeiojfw.
     */
    public static RGBAPixel[][] shrinkHorizontal(final RGBAPixel[][] originalImage,
                                                 final int amount) {
        return originalImage;
    }

    /**
     *
     * @param originalImage gieogieo.
     * @param amount fwmfw.
     * @return wfeiojfw.
     */
    public static RGBAPixel[][] shrinkVertical(final RGBAPixel[][] originalImage,
                                               final int amount) {
        return originalImage;
    }
}
