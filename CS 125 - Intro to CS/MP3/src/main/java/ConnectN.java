import java.util.Arrays;
import java.util.Objects;

/**
 * hrfperfpoei.
 */
public class ConnectN {
    /**
     * graawearg.
     */
    public static final int MAX_HEIGHT = 16;
    /**
     * greeaeg.
     */
    public static final int MAX_WIDTH = 16;
    /**
     * geragregregre.
     */
    public static final int MIN_HEIGHT = 6;
    /**
     * gregregregreer.
     */
    public static final int MIN_N = 4;
    /**
     * gregreregree.
     */
    public static final int MIN_WIDTH = 6;

    /**
     * gre.
     */
    public static final int STUPID_CHECKSTYLE = 31;
    /**
     * grewgreg.
     */
    private int width;
    /**
     * egfwegwg.
     */
    private int height;
    /**
     * wfwgrwe.
     */
    private int n;
    /**
     * rgoegrgh.
     */
    public String title;

    /**
     * wefwefp.
     */
    private boolean gameOver;

    /**
     * ergegre.
     */
    private Player[][] board;

    /**
     * ougobuj.
     */
    private Player winner;

    /**
     *
     */
    private int score = 0;

    /**
     * greer.
     */
    static int totalGames;
    /**
     * wffw.
     */
    static int id;

    /**
     * dgser.
     */
    public ConnectN() {
        ++totalGames;
        id = totalGames - 1;
        width = 0;
        height = 0;
        n = 0;
    }

    /**
     * @param setWidth  ergre.
     * @param setHeight ergaregerg.
     */
    public ConnectN(final int setWidth, final int setHeight) {
        this.n = 0;
        ++totalGames;
        id = totalGames - 1;
        if (setWidth < MIN_WIDTH || setWidth > MAX_WIDTH) {
            this.width = 0;
        } else {
            this.width = setWidth;
        }
        if (setHeight < MIN_HEIGHT || setHeight > MAX_HEIGHT) {
            this.height = 0;
        } else {
            this.height = setHeight;
        }
        board = new Player[width][height];
        //boardInitialized = true;
    }

    /**
     * @param setWidth  the width for the new ConnectN board.
     * @param setHeight ergerg.
     * @param setN      reggsre.
     */

    public ConnectN(final int setWidth, final int setHeight, final int setN) {
        ++totalGames;
        id = totalGames - 1;
        if (setWidth < MIN_WIDTH || setWidth > MAX_WIDTH) {
            this.width = 0;
        } else {
            this.width = setWidth;
        }
        if (setHeight < MIN_HEIGHT || setHeight > MAX_HEIGHT) {
            this.height = 0;
        } else {
            this.height = setHeight;
        }
        if (setN < MIN_N || setN > (Math.max(setHeight, setWidth) - 1)
                || width == 0 || height == 0) {
            this.n = 0;
        } else {
            this.n = setN;
            board = new Player[width][height];
            //boardInitialized = true;
        }
    }

    /**
     * veerer.
     *
     * @param otherBoard ferfrevre
     */
    public ConnectN(final ConnectN otherBoard) {
        ++totalGames;
        id = totalGames - 1;
        width = otherBoard.width;
        height = otherBoard.height;
        n = otherBoard.n;
        board = new Player[width][height];
    }

    /**
     * @return total # of games.
     */
    public static int getTotalGames() {
        return totalGames;
    }

    /**
     * @return dgerg.
     */
    private boolean boardInitialized;

    /**
     * @return gege
     */
    public int getWidth() {
//        width = this.width;
        return width;
    }

    /**
     * @return dgedrgerd.
     */
    public int getHeight() {
        return height;
    }

    /**
     * @return drge.
     */
    public int getN() {
        return n;
    }

    /**
     * @param setWidth gegr.e
     * @return gregr.
     */
    public boolean setWidth(final int setWidth) {
        if (!boardInitialized && (setWidth <= MAX_WIDTH && setWidth >= MIN_WIDTH)) {
            this.width = setWidth;
            if (n > (Math.max(height, width) - 1)) {
                n = 0;
            }
            if (width != 0 && height != 0) {
                board = new Player[width][height];
            }
            return true;
        }
        return false;
    }

    /**
     * @param setHeight gere
     * @return nyumnyum.
     */
    public boolean setHeight(final int setHeight) {
        if (!boardInitialized && (setHeight <= MAX_HEIGHT && setHeight >= MIN_HEIGHT)) {
            this.height = setHeight;
            if (n > (Math.max(height, width) - 1)) {
                n = 0;
            }
            if (width != 0 && height != 0) {
                board = new Player[width][height];
            }
            return true;
        }
        return false;
    }

    /**
     * @param newN eg.
     * @return io, i, i, .
     */
    public boolean setN(final int newN) {
        if (!boardInitialized && (width != 0 && height != 0)) {
            if (newN < MIN_N) {
                return false;
            } else if (newN > (Math.max(height, width) - 1)) {
                return false;
            }
            this.n = newN;
            return true;
        }
        return false;
    }

    /**
     * @return sfwef
     */
    public int getID() {
        return id;
    }

    /**
     * @param player gsrsg.
     * @param setX   grresg.
     * @param setY   grgres.
     * @return egerg.
     */
    public boolean setBoardAt(final Player player, final int setX, final int setY) {
        if (board == null) {
            System.out.println("1");
            return false;
        } else if (gameOver) {
            System.out.println("2");
            return false;
        } else if (player == null) {
            System.out.println("3");
            return false;
        } else if (width == 0 || height == 0 || n == 0) {
            System.out.println("4");
            return false;
        } else if (setX > width || setX < 0 || setY > height || setY < 0) {
            System.out.println("5");
            return false;
        } else if (setY == 0) {
            if (board[setX][0] == null && winner == null) {
                board[setX][setY] = player;
                boardInitialized = true;
                System.out.println("6");
                return true;
            } else {
                System.out.println("7");
                return false;
            }
        } else if (board[setX][setY - 1] == null) {
            System.out.println("8");
            return false;
        } else if (board[setX][setY] != null) {
            return false;
        }
        boardInitialized = true;
        int i;
        int j;
        int countY = 1;
        int countX = 1;
        if (winner == null) {
            board[setX][setY] = player;
        }
        for (i = setX; i < board.length - 1; i++) {
            if ((board[i][setY] == board[i + 1][setY])) {
                countX++;
                System.out.println((board[i][setY]));
            } else {
                break;
            }
        }
        for (i = setX; i > 0; i--) {
            if ((board[i][setY] == board[i - 1][setY])) {
                countX++;
                System.out.println((board[i][setY]));
            } else {
                break;
            }
        }
        for (j = setY; j < board[0].length - 1; j++) {
            if ((board[setX][j] == board[setX][j + 1])) {
                countY++;
                System.out.println((board[setX][j]));
            } else {
                break;
            }
        }
        for (j = setY; j > 0; j--) {
            if ((board[setX][j] == board[setX][j - 1])) {
                countY++;
                System.out.println((board[setX][j]));
            } else {
                break;
            }
        }
        if (countX < n && countY < n) {
            gameOver = false;
        }
        return true;
    }

    /**
     * @param player beer.
     * @param setX   regrg.
     * @return egegre.
     */
    public boolean setBoardAt(final Player player, final int setX) {
        if (gameOver) {
            return false;
        } else if (player == null) {
            return false;
        } else if (width == 0 || height == 0 || n == 0) {
            return false;
        } else if (setX > width || setX < 0) {
            return false;
        }
        /*int i = 0;
        for (; i < board.length; i++) {
            if (board[setX][i] == null) {
                board[setX][i] = player;
                return true;
            }
        }
        boardInitialized = true;
        return false;*/
        for (int h = 0; h < getHeight(); ++h) {
            if (setBoardAt(player, setX, h)) {
                boardInitialized  = true;
                return true;
            }
        }
        return false;
    }

    /**
     * @param getX hrth.
     * @param getY htrhtr.
     * @return rthrh.
     */
    public Player getBoardAt(final int getX, final int getY) {
        if (!boardInitialized) {
            return null;
        } else if (getX > MAX_WIDTH || getX < 0 || getY > MAX_HEIGHT || getY < 0) {
            return null;
        } else if (board[getX][getY] == null) {
            return null;
        }
        return board[getX][getY];
    }

    /**
     * @return svsvsf.
     */
    public Player[][] getBoard() {
        if (width == 0 || height == 0 || board == null) {
            return null;
        } else {
            Player[][] newBoard = new Player[this.width][this.height];
            for (int i = 0; i < this.width; i++) {
                for (int j = 0; j < this.height; j++) {
                    if (board[i][j] != null) {
                        newBoard[i][j] = new Player(board[i][j]);
                    }
                }
            }
            return newBoard;
        }
    }

    /**
     * @return sfsfew.
     */
    public Player getWinner() {
        if (!boardInitialized) {
            return null;
        }
        Player omffffffg = null;
        for (int i = 0; i < width; i++) {
            int yCount = 1;
            for (int j = 0; j < height - 1; j++) {
                if (board[i][j] == board[i][j + 1] && board[i][j] != null) {
                    yCount++;
                } else {
                    yCount = 1;
                }
                if (yCount == n) {
                    winner = board[i][j];
                    //winner.addScore();
                    gameOver = true;
                    boardInitialized = false;
                    omffffffg = board[i][j];
                }
            }
        }
        for (int k = 0; k < height; k++) {
            int xCount = 1;
            for (int l = 0; l < width - 1; l++) {
                if (board[l][k] == board[l + 1][k] && board[l][k] != null) {
                    xCount++;
                } else {
                    xCount = 1;
                }
                if (xCount == n) {
                    winner = board[l][k];
                    //winner.addScore();
                    gameOver = true;
                    boardInitialized = false;
                    omffffffg = board[l][k];
                }
            }
        }
        if (omffffffg != null) {
            omffffffg.addScore();
        } else {
            gameOver = false;
            boardInitialized = true;
            winner = null;
        }
        return omffffffg;
    }

    /**
     * @param width  dvsvs.
     * @param height fwefwe.
     * @param n      fyidutkrfliddi.
     * @return swef.
     */
    public static ConnectN create(final int width, final int height, final int n) {
        if (width < MIN_WIDTH || width > MAX_WIDTH) {
            return null;
        }
        if (height < MIN_HEIGHT || height > MAX_HEIGHT) {
            return null;
        } else if (n < MIN_N || n > (Math.max(height, width) - 1)) {
            return null;
        }
        return new ConnectN(width, height, n);
    }

    /**
     * @param number wefwef.
     * @param width  wefwef.
     * @param height wefwf.
     * @param n      wefwef.
     * @return wefwfe.
     */
    public static ConnectN[] createMany(final int number, final int width,
                                        final int height, final int n) {
        if (width < MIN_WIDTH || width > MAX_WIDTH) {
            return null;
        }
        if (height < MIN_HEIGHT || height > MAX_HEIGHT) {
            return null;
        } else if (n < MIN_N || n > (Math.max(height, width) - 1)) {
            return null;
        }
        ConnectN a = new ConnectN(width, height, n);
        ConnectN[] b = new ConnectN[number];
        for (int i = 0; i < number; i++) {
            b[i] = a;
        }
        return b;
    }

    /**
     * @param firstBoard  svwfwef.
     * @param secondBoard weffwe.
     * @return wfewefw.
     */
    public static boolean compareBoards(final ConnectN firstBoard, final ConnectN secondBoard) {
        if (firstBoard == null) {
            return secondBoard == null;
        }
        if (secondBoard == null) {
            return firstBoard == null;
        }
        if (firstBoard.getHeight() != secondBoard.getHeight()) {
            return false;
        }
        if (firstBoard.getWidth() != secondBoard.getWidth()) {
            return false;
        }
        if (firstBoard.getN() != secondBoard.getN()) {
            return false;
        }
        return Arrays.deepEquals(firstBoard.getBoard(), secondBoard.getBoard());
    }

    /**
     * @param boards fwefwe.
     * @return wefwef.
     */
    public static boolean compareBoards(final ConnectN... boards) {
        if (boards.length < 2) {
            return true;
        }
        for (int i = 1; i < boards.length; i++) {
            if (!compareBoards(boards[i], boards[i - 1])) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param o efwefw.
     * @return wefwf.
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ConnectN)) {
            return false;
        }
        ConnectN connectN = (ConnectN) o;
        return getWidth() == connectN.getWidth()
                && getHeight() == connectN.getHeight()
                && getN() == connectN.getN()
                && gameOver == connectN.gameOver
                && boardInitialized == connectN.boardInitialized
                && Objects.equals(title, connectN.title)
                && Arrays.equals(getBoard(), connectN.getBoard())
                && Objects.equals(getWinner(), connectN.getWinner());
    }

    /**
     * @return ffsf.
     */
    @Override
    public int hashCode() {

        int result = Objects.hash(getWidth(), getHeight(), getN(), title, gameOver, getWinner(),
                boardInitialized);
        result = STUPID_CHECKSTYLE * result + Arrays.hashCode(getBoard());
        return result;
    }
}
