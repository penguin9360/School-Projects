import java.lang.reflect.Field;
import java.util.concurrent.ThreadLocalRandom;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test suite for the ConnectN class.
 * <p>
 * The provided test suite is correct and complete. You should not need to modify it. However, you
 * should understand it. You will need to augment or write test suites for later MPs.
 *
 * @see <a href="https://cs125.cs.illinois.edu/MP/3/">MP3 Documentation</a>
 */
@SuppressWarnings("checkstyle:magicnumber")
public class ConnectNTest {

    /** Timeout for all tests. These should be quite quick. */
    private static final int TEST_TIMEOUT = 100;

    /**
     * Test getting and setting the title.
     */
    @Test(priority = 1, timeOut = TEST_TIMEOUT)
    public void testTitle() {
        ConnectN board = new ConnectN();

        /*
         * We should be able to set the title.
         */
        try {
            board.title = "New Board";
            Assert.assertEquals(board.title, "New Board");
        } catch (Exception e) {
            Assert.fail("Should be able to set title", e);
        }

        /*
         * Title should not be static.
         */
        ConnectN anotherBoard = new ConnectN();
        Assert.assertNotEquals(anotherBoard.title, "New Board");
        try {
            anotherBoard.title = "Second Board";
            Assert.assertEquals(anotherBoard.title, "Second Board");
        } catch (Exception e) {
            Assert.fail("Should be able to set title", e);
        }
        Assert.assertEquals(board.title, "New Board");
        try {
            board.title = "Another Title";
            Assert.assertEquals(board.title, "Another Title");
        } catch (Exception e) {
            Assert.fail("Should be able to set title", e);
        }
        Assert.assertEquals(anotherBoard.title, "Second Board");

        /*
         * Make sure title is public.
         */
        Class<?> connectNClass = board.getClass();
        Field[] publicFields = connectNClass.getFields();
        boolean foundBoardTitle = false, foundAnotherBoardTitle = false;
        for (Field field : publicFields) {
            try {
                String publicTitle = (String) field.get(board);
                foundBoardTitle = publicTitle.equals("Another Title");
                publicTitle = (String) field.get(anotherBoard);
                foundAnotherBoardTitle = publicTitle.equals("Second Board");
            } catch (Exception ignored) {
            }
        }
        Assert.assertTrue(foundBoardTitle, "Board title is not public");
        Assert.assertTrue(foundAnotherBoardTitle, "Board title is not public");
    }

    /**
     * Test simple width getters and setters.
     */
    @Test(priority = 1, timeOut = TEST_TIMEOUT)
    public void testGetAndSetWidth() {
        final String setBoardWidth = "Should be able to set board width";
        final String notSetCorrectly = "Board width not set correctly";
        final String invalidBoardWidth = "Should not be able to set invalid board width";
        final String invalidReset = "Invalid set should not reset previous width";
        final String widthIsStatic = "Board width should not be static";
        final String widthIsPublic = "Board width should not be public";

        ConnectN board = new ConnectN();
        Assert.assertEquals(ConnectN.MAX_WIDTH, 16, "Read the spec, MAX_WIDTH is wrong");


        /*
         * Test valid widths.
         */
        Assert.assertTrue(board.setWidth(7), setBoardWidth);
        Assert.assertEquals(board.getWidth(), 7, notSetCorrectly);
        Assert.assertTrue(board.setWidth(13), setBoardWidth);
        Assert.assertEquals(board.getWidth(), 13, notSetCorrectly);


        /*
         * Test invalid widths.
         */
        Assert.assertFalse(board.setWidth(0), invalidBoardWidth);
        Assert.assertEquals(board.getWidth(), 13, invalidReset);
        Assert.assertFalse(board.setWidth(-9), invalidBoardWidth);
        Assert.assertEquals(board.getWidth(), 13, invalidReset);
        Assert.assertFalse(board.setWidth(3), invalidBoardWidth);
        Assert.assertEquals(board.getWidth(), 13, invalidReset);
        Assert.assertFalse(board.setWidth(2001), invalidBoardWidth);
        Assert.assertEquals(board.getWidth(), 13, invalidReset);


        /*
         * Make sure width still works.
         */
        Assert.assertTrue(board.setWidth(7), setBoardWidth);
        Assert.assertEquals(board.getWidth(), 7, notSetCorrectly);

        /*
         * Make sure width is not static.
         */
        ConnectN anotherBoard = new ConnectN();
        Assert.assertTrue(anotherBoard.setWidth(9), setBoardWidth);
        Assert.assertEquals(anotherBoard.getWidth(), 9, notSetCorrectly);
        Assert.assertEquals(board.getWidth(), 7, widthIsStatic);
        Assert.assertTrue(board.setWidth(11), setBoardWidth);
        Assert.assertEquals(anotherBoard.getWidth(), 9, widthIsStatic);
        Assert.assertEquals(board.getWidth(), 11, notSetCorrectly);

        /*
         * Make sure width is not public.
         */
        Class<?> connectNClass = board.getClass();
        Field[] publicFields = connectNClass.getFields();
        for (Field field : publicFields) {
            try {
                int publicWidth = field.getInt(board);
                Assert.assertNotEquals(publicWidth, 11, widthIsPublic);
                publicWidth = field.getInt(anotherBoard);
                Assert.assertNotEquals(publicWidth, 9, widthIsPublic);
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * Test simple height getters and setters.
     */
    @Test(priority = 1, timeOut = TEST_TIMEOUT)
    public void testGetAndSetHeight() {
        final String setBoardHeight = "Should be able to set board height";
        final String notSetCorrectly = "Board height not set correctly";
        final String invalidBoardHeight = "Should not be able to set invalid board height";
        final String invalidReset = "Invalid set should not reset previous height";
        final String heightIsStatic = "Board height should not be static";
        final String heightIsPublic = "Board height should not be public";

        /*
         * Test MAX_HEIGHT.
         */
        ConnectN board = new ConnectN();
        Assert.assertEquals(ConnectN.MAX_HEIGHT, 16, "Read the spec, MAX_HEIGHT is wrong");

        /*
         * Test valid heights.
         */
        Assert.assertTrue(board.setHeight(6), setBoardHeight);
        Assert.assertEquals(board.getHeight(), 6, notSetCorrectly);
        Assert.assertTrue(board.setHeight(12), setBoardHeight);
        Assert.assertEquals(board.getHeight(), 12, notSetCorrectly);

        /*
         * Test invalid heights.
         */
        Assert.assertFalse(board.setHeight(0), invalidBoardHeight);
        Assert.assertEquals(board.getHeight(), 12, invalidReset);
        Assert.assertFalse(board.setHeight(-1), invalidBoardHeight);
        Assert.assertEquals(board.getHeight(), 12, invalidReset);
        Assert.assertFalse(board.setHeight(4), invalidBoardHeight);
        Assert.assertEquals(board.getHeight(), 12, invalidReset);
        Assert.assertFalse(board.setHeight(1000), invalidBoardHeight);
        Assert.assertEquals(board.getHeight(), 12, invalidReset);

        /*
         * Make sure height still works.
         */
        Assert.assertTrue(board.setHeight(6), setBoardHeight);
        Assert.assertEquals(board.getHeight(), 6, notSetCorrectly);

        /*
         * Make sure height is not static.
         */
        ConnectN anotherBoard = new ConnectN();
        Assert.assertTrue(anotherBoard.setHeight(8), setBoardHeight);
        Assert.assertEquals(anotherBoard.getHeight(), 8, notSetCorrectly);
        Assert.assertEquals(board.getHeight(), 6, heightIsStatic);
        Assert.assertTrue(board.setHeight(10), setBoardHeight);
        Assert.assertEquals(anotherBoard.getHeight(), 8, heightIsStatic);
        Assert.assertEquals(board.getHeight(), 10, notSetCorrectly);

        /*
         * Make sure height is not public.
         */
        Class<?> connectNClass = board.getClass();
        Field[] publicFields = connectNClass.getFields();
        for (Field field : publicFields) {
            try {
                int publicHeight = field.getInt(board);
                Assert.assertNotEquals(publicHeight, 10, heightIsPublic);
                publicHeight = field.getInt(anotherBoard);
                Assert.assertNotEquals(publicHeight, 8, heightIsPublic);
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * Test simple N getters and setters.
     */
    @Test(priority = 1, timeOut = TEST_TIMEOUT)
    public void testGetAndSetN() {
        final String earlySetN = "Should not be able to set board N value before width and height";
        final String setBoardN = "Should be able to set board N value";
        final String notSetCorrectly = "Board N value not set correctly";
        final String invalidBoardN = "Should not be able to set invalid board N value";
        final String invalidReset = "Invalid set should not reset previous N value";
        final String invalidDimensionReset = "Changing width and height should not reset N";
        final String missingDimensionReset = "Changing width and height should reset N";
        final String nIsStatic = "Board N value should not be static";
        final String nIsPublic = "Board N value should not be public";

        /*
         * Test MAX_WIDTH.
         */
        ConnectN board = new ConnectN();
        Assert.assertEquals(ConnectN.MIN_N, 4, "Read the spec, MIN_N is wrong");


        /*
         * Test setting N before width and height.
         */
        Assert.assertFalse(board.setN(4), earlySetN);
        Assert.assertTrue(board.setWidth(8));
        Assert.assertFalse(board.setN(4), earlySetN);
        Assert.assertTrue(board.setHeight(8));

        /*
         * Test valid N.
         */
        Assert.assertTrue(board.setN(4), setBoardN);
        Assert.assertEquals(board.getN(), 4, notSetCorrectly);
        Assert.assertTrue(board.setN(6), setBoardN);
        Assert.assertEquals(board.getN(), 6, notSetCorrectly);
        Assert.assertTrue(board.setN(7), setBoardN);
        Assert.assertEquals(board.getN(), 7, notSetCorrectly);

        /*
         * Test invalid N.
         */
        Assert.assertFalse(board.setN(10), invalidBoardN);
        Assert.assertEquals(board.getN(), 7, invalidReset);
        Assert.assertFalse(board.setN(1), invalidBoardN);
        Assert.assertEquals(board.getN(), 7, invalidReset);
        Assert.assertFalse(board.setN(0), invalidBoardN);
        Assert.assertEquals(board.getN(), 7, invalidReset);
        Assert.assertFalse(board.setN(-1), invalidBoardN);
        Assert.assertEquals(board.getN(), 7, invalidReset);
        Assert.assertFalse(board.setN(8), invalidBoardN);
        Assert.assertEquals(board.getN(), 7, invalidReset);
        Assert.assertTrue(board.setWidth(9));
        Assert.assertFalse(board.setN(9), invalidBoardN);
        Assert.assertEquals(board.getN(), 7, invalidReset);
        Assert.assertTrue(board.setWidth(8));

        /*
         * Make sure that changing widths and heights resets N as needed.
         */
        Assert.assertEquals(board.getN(), 7);
        Assert.assertTrue(board.setWidth(9));
        Assert.assertEquals(board.getWidth(), 9);
        Assert.assertEquals(board.getN(), 7, invalidDimensionReset);
        Assert.assertTrue(board.setHeight(9));
        Assert.assertEquals(board.getHeight(), 9);
        Assert.assertEquals(board.getN(), 7, invalidDimensionReset);
        Assert.assertTrue(board.setWidth(7));
        Assert.assertEquals(board.getWidth(), 7);
        Assert.assertEquals(board.getN(), 7, invalidDimensionReset);
        Assert.assertTrue(board.setHeight(6));
        Assert.assertEquals(board.getHeight(), 6);
        Assert.assertEquals(board.getN(), 0, missingDimensionReset);
        Assert.assertTrue(board.setWidth(10));
        Assert.assertTrue(board.setHeight(10));
        Assert.assertTrue(board.setN(6), setBoardN);
        Assert.assertEquals(board.getN(), 6, notSetCorrectly);
        Assert.assertTrue(board.setHeight(6));
        Assert.assertTrue(board.setWidth(6));
        Assert.assertEquals(board.getHeight(), 6);
        Assert.assertEquals(board.getWidth(), 6);
        Assert.assertEquals(board.getN(), 0);
        Assert.assertTrue(board.setHeight(7));
        Assert.assertTrue(board.setWidth(7));
        Assert.assertEquals(board.getHeight(), 7);
        Assert.assertEquals(board.getWidth(), 7);
        Assert.assertTrue(board.setN(6));
        Assert.assertEquals(board.getN(), 6);
        Assert.assertTrue(board.setWidth(6));
        Assert.assertTrue(board.setHeight(6));
        Assert.assertEquals(board.getWidth(), 6);
        Assert.assertEquals(board.getHeight(), 6);
        Assert.assertEquals(board.getN(), 0);
        Assert.assertTrue(board.setN(4));
        Assert.assertEquals(board.getN(), 4);



        /*
         * Make sure N is not static.
         */
        ConnectN anotherBoard = new ConnectN();
        anotherBoard.setWidth(10);
        anotherBoard.setHeight(10);
        Assert.assertTrue(anotherBoard.setN(8), setBoardN);
        Assert.assertEquals(anotherBoard.getN(), 8, notSetCorrectly);
        Assert.assertEquals(board.getN(), 4, nIsStatic);
        Assert.assertTrue(board.setN(5), setBoardN);
        Assert.assertEquals(anotherBoard.getN(), 8, nIsStatic);
        Assert.assertEquals(board.getN(), 5, notSetCorrectly);

        /*
         * Make sure N is not public.
         */
        Class<?> connectNClass = board.getClass();
        Field[] publicFields = connectNClass.getFields();
        for (Field field : publicFields) {
            try {
                int publicWidth = field.getInt(board);
                Assert.assertNotEquals(publicWidth, 5, nIsPublic);
                publicWidth = field.getInt(anotherBoard);
                Assert.assertNotEquals(publicWidth, 8, nIsPublic);
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * Test ConnectN constructors.
     */
    @Test(priority = 1, timeOut = TEST_TIMEOUT)
    public void testConstructors() {
        final String emptyConstructorSetFields = //
                "Empty constructor should not initialize width, height, or N";
        final String widthHeightConstructorMissedFields = //
                "Width and height constructor should initialize width and height";
        final String widthHeightConstructorInvalidFields = //
                "Width and height constructor should ignore invalid values";
        final String widthHeightConstructorSetN = //
                "Width and height constructor should not set N";
        final String completeConstructorMissedFields = //
                "Complete constructor should initialize width, height, and N";
        final String completeConstructorInvalidFields = //
                "Complete constructor should ignore invalid values";
        final String copyConstructorMissedFields = //
                "Copy constructor should copy width, height, and N";

        /*
         * Test empty constructor.
         */
        ConnectN board = new ConnectN();
        Assert.assertEquals(board.getWidth(), 0, emptyConstructorSetFields);
        Assert.assertEquals(board.getHeight(), 0, emptyConstructorSetFields);
        Assert.assertEquals(board.getN(), 0, emptyConstructorSetFields);

        /*
         * Test width and height constructor with valid values.
         */
        board = new ConnectN(6, 8);
        Assert.assertEquals(board.getWidth(), 6, widthHeightConstructorMissedFields);
        Assert.assertEquals(board.getHeight(), 8, widthHeightConstructorMissedFields);
        Assert.assertEquals(board.getN(), 0, widthHeightConstructorSetN);

        /*
         * Test width and height constructor with invalid values.
         */
        board = new ConnectN(-1, 8);
        Assert.assertEquals(board.getWidth(), 0, widthHeightConstructorInvalidFields);
        Assert.assertEquals(board.getHeight(), 8, widthHeightConstructorMissedFields);
        Assert.assertEquals(board.getN(), 0, widthHeightConstructorSetN);
        board = new ConnectN(8, 1000);
        Assert.assertEquals(board.getWidth(), 8, widthHeightConstructorMissedFields);
        Assert.assertEquals(board.getHeight(), 0, widthHeightConstructorInvalidFields);
        Assert.assertEquals(board.getN(), 0, widthHeightConstructorSetN);
        board = new ConnectN(1000, -1);
        Assert.assertEquals(board.getWidth(), 0, widthHeightConstructorInvalidFields);
        Assert.assertEquals(board.getHeight(), 0, widthHeightConstructorInvalidFields);
        Assert.assertEquals(board.getN(), 0, widthHeightConstructorSetN);

        /*
         * Test complete constructor with valid values.
         */
        board = new ConnectN(6, 8, 4);
        Assert.assertEquals(board.getWidth(), 6, completeConstructorMissedFields);
        Assert.assertEquals(board.getHeight(), 8, completeConstructorMissedFields);
        Assert.assertEquals(board.getN(), 4, completeConstructorMissedFields);
        board = new ConnectN(6, 6, 5);
        Assert.assertEquals(board.getWidth(), 6, completeConstructorMissedFields);
        Assert.assertEquals(board.getHeight(), 6, completeConstructorMissedFields);
        Assert.assertEquals(board.getN(), 5, completeConstructorMissedFields);

        /*
         * Test complete constructor with invalid values.
         */
        board = new ConnectN(6, 8, -1);
        Assert.assertEquals(board.getWidth(), 6, completeConstructorMissedFields);
        Assert.assertEquals(board.getHeight(), 8, completeConstructorMissedFields);
        Assert.assertEquals(board.getN(), 0, completeConstructorInvalidFields);
        board = new ConnectN(6, -1, 2);
        Assert.assertEquals(board.getWidth(), 6, completeConstructorInvalidFields);
        Assert.assertEquals(board.getHeight(), 0, completeConstructorInvalidFields);
        Assert.assertEquals(board.getN(), 0, completeConstructorInvalidFields);
        board = new ConnectN(-1, 10, 6);
        Assert.assertEquals(board.getWidth(), 0, completeConstructorInvalidFields);
        Assert.assertEquals(board.getHeight(), 10, completeConstructorInvalidFields);
        Assert.assertEquals(board.getN(), 0, completeConstructorInvalidFields);
        board = new ConnectN(7, -1, 100);
        Assert.assertEquals(board.getWidth(), 7, completeConstructorInvalidFields);
        Assert.assertEquals(board.getHeight(), 0, completeConstructorInvalidFields);
        Assert.assertEquals(board.getN(), 0, completeConstructorInvalidFields);
        board = new ConnectN(-1, 13, 1001);
        Assert.assertEquals(board.getWidth(), 0, completeConstructorInvalidFields);
        Assert.assertEquals(board.getHeight(), 13, completeConstructorInvalidFields);
        Assert.assertEquals(board.getN(), 0, completeConstructorInvalidFields);
        board = new ConnectN(10, 9, 10);
        Assert.assertEquals(board.getWidth(), 10, completeConstructorMissedFields);
        Assert.assertEquals(board.getHeight(), 9, completeConstructorMissedFields);
        Assert.assertEquals(board.getN(), 0, completeConstructorInvalidFields);

        /*
         * Test complete constructor with valid values.
         */
        board = new ConnectN(8, 7, 6);
        ConnectN anotherBoard = new ConnectN(board);
        Assert.assertEquals(board.getWidth(), 8, completeConstructorMissedFields);
        Assert.assertEquals(anotherBoard.getWidth(), 8, copyConstructorMissedFields);
        Assert.assertEquals(board.getHeight(), 7, completeConstructorMissedFields);
        Assert.assertEquals(anotherBoard.getHeight(), 7, copyConstructorMissedFields);
        Assert.assertEquals(board.getN(), 6, completeConstructorMissedFields);
        Assert.assertEquals(anotherBoard.getN(), 6, copyConstructorMissedFields);
    }

    /**
     * Test getting and setting the board at a specific position.
     */
    @Test(priority = 1, timeOut = TEST_TIMEOUT)
    @SuppressWarnings("checkstyle:methodlength")
    public void testGetAndSetBoard() {
        final String validSet = "Set at this position should succeed";
        final String validGet = "Get at this position should return a player";
        final String nullGet = "Get at this position should return null";
        final String invalidSet = "Set at this position should fail";
        final String gameBoardInitialized = "Game board not properly initialized";
        final String gameBoardCopy = "Game board returned a copy";
        final String changeAfterStart = "Can't change dimensions afters start";
        final String uninitializedBoardGet = //
                "Calls to getBoard before initialization should return null";

        /*
         * Test valid sets and gets.
         */
        ConnectN board = new ConnectN(10, 9, 6);
        Player player = new Player("Chuchu");
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 9; y++) {
                Assert.assertEquals(board.getBoardAt(x, y), null, nullGet);
            }
        }
        Assert.assertEquals(board.getBoardAt(0, 0), null, nullGet);
        Assert.assertTrue(board.setBoardAt(player, 0, 0), validSet);
        Assert.assertEquals(board.getBoardAt(0, 0), player, validGet);

        Assert.assertFalse(board.setWidth(9), changeAfterStart);
        Assert.assertEquals(board.getWidth(), 10, changeAfterStart);
        Assert.assertFalse(board.setHeight(8), changeAfterStart);
        Assert.assertEquals(board.getHeight(), 9, changeAfterStart);
        Assert.assertFalse(board.setN(7), changeAfterStart);
        Assert.assertEquals(board.getN(), 6, changeAfterStart);

        Assert.assertEquals(board.getBoardAt(9, 0), null, nullGet);
        Assert.assertTrue(board.setBoardAt(player, 9, 0), validSet);
        Assert.assertEquals(board.getBoardAt(9, 0), player, validGet);

        Assert.assertEquals(board.getBoardAt(7, 0), null, nullGet);
        Assert.assertTrue(board.setBoardAt(player, 7, 0), validSet);
        Assert.assertEquals(board.getBoardAt(7, 0), player, validGet);

        Assert.assertEquals(board.getBoardAt(5, 0), null, nullGet);
        Assert.assertTrue(board.setBoardAt(player, 5, 0), validSet);
        Assert.assertEquals(board.getBoardAt(5, 0), player, validGet);

        Assert.assertEquals(board.getBoardAt(7, 1), null, nullGet);
        Assert.assertTrue(board.setBoardAt(player, 7, 1), validSet);
        Assert.assertEquals(board.getBoardAt(7, 1), player, validGet);

        Assert.assertEquals(board.getBoardAt(7, 2), null, nullGet);
        Assert.assertTrue(board.setBoardAt(player, 7, 2), validSet);
        Assert.assertEquals(board.getBoardAt(7, 2), player, validGet);

        /*
         * Test invalid dimensions.
         */
        Assert.assertEquals(board.getBoardAt(-1, 0), null, nullGet);
        Assert.assertEquals(board.getBoardAt(0, -1), null, nullGet);
        Assert.assertEquals(board.getBoardAt(100, 4), null, nullGet);
        Assert.assertEquals(board.getBoardAt(5, 99), null, nullGet);

        /*
         * Test invalid sets.
         */
        board = new ConnectN(8, 7, 4);
        player = new Player("Chuchu");
        for (int x = 0; x < 7; x++) {
            for (int y = 0; y < 7; y++) {
                Assert.assertEquals(board.getBoardAt(x, y), null, nullGet);
            }
        }
        Assert.assertFalse(board.setBoardAt(player, 0, 1), invalidSet);
        Assert.assertFalse(board.setBoardAt(player, 9, 0), invalidSet);
        Assert.assertFalse(board.setBoardAt(player, 7, 6), invalidSet);
        Assert.assertFalse(board.setBoardAt(player, 3, 7), invalidSet);
        Assert.assertFalse(board.setBoardAt(player, 3, 1), invalidSet);
        Assert.assertFalse(board.setBoardAt(player, 3, 2), invalidSet);
        for (int x = 0; x < 7; x++) {
            for (int y = 0; y < 7; y++) {
                Assert.assertEquals(board.getBoardAt(x, y), null, nullGet);
            }
        }

        Assert.assertTrue(board.setBoardAt(player, 3, 0), validSet);
        Assert.assertFalse(board.setBoardAt(player, 3, 0), invalidSet);
        Assert.assertEquals(board.getBoardAt(3, 0), player, validGet);

        Assert.assertTrue(board.setBoardAt(player, 3, 1), validSet);
        Assert.assertFalse(board.setBoardAt(player, 3, 0), invalidSet);
        Assert.assertEquals(board.getBoardAt(3, 0), player, validGet);
        Assert.assertFalse(board.setBoardAt(player, 3, 1), invalidSet);
        Assert.assertEquals(board.getBoardAt(3, 1), player, validGet);

        /*
         * Test drop sets.
         */
        board = new ConnectN(10, 6, 8);
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 6; y++) {
                Assert.assertEquals(board.getBoardAt(x, y), null, nullGet);
            }
        }
        player = new Player("Chuchu");
        Player otherPlayer = new Player("Xyz");
        for (int y = 0; y < 6; y++) {
            Assert.assertEquals(board.getBoardAt(0, y), null, nullGet);
            Assert.assertTrue(board.setBoardAt(player, 0), validSet);
            Assert.assertEquals(board.getBoardAt(0, y), player, validGet);
        }
        Assert.assertFalse(board.setBoardAt(player, 0), invalidSet);
        for (int y = 0; y < 6; y++) {
            Assert.assertFalse(board.setBoardAt(otherPlayer, 0, y), invalidSet);
            Assert.assertEquals(board.getBoardAt(0, y), player, validGet);
        }
        for (int y = 0; y < 6; y++) {
            Assert.assertEquals(board.getBoardAt(5, y), null, nullGet);
            Assert.assertTrue(board.setBoardAt(player, 5), validSet);
            Assert.assertEquals(board.getBoardAt(5, y), player, validGet);
        }
        for (int y = 0; y < 6; y++) {
            Assert.assertFalse(board.setBoardAt(otherPlayer, 5, y), invalidSet);
            Assert.assertEquals(board.getBoardAt(5, y), player, validGet);
        }

        /*
         * Test valid board getters.
         */
        ConnectN game = new ConnectN();
        Assert.assertEquals(game.getBoard(), null, uninitializedBoardGet);
        game.setWidth(10);
        Assert.assertEquals(game.getBoard(), null, uninitializedBoardGet);
        game.setHeight(10);
        Assert.assertNotEquals(game.getBoard(), null, //
                "After dimensions are set getBoard should succeed");

        game = new ConnectN(10, 6, 8);
        player = new Player("Chuchu");
        Player[][] gameBoard = game.getBoard();
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 6; y++) {
                Assert.assertEquals(gameBoard[x][y], null, gameBoardInitialized);
            }
        }
        Assert.assertTrue(game.setBoardAt(player, 0));
        Assert.assertEquals(game.getBoardAt(0, 0), player);
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 6; y++) {
                Assert.assertEquals(gameBoard[x][y], null, gameBoardCopy);
            }
        }
        gameBoard = game.getBoard();
        Assert.assertEquals(gameBoard[0][0], player);
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 6; y++) {
                if (x == 0 && y == 0) {
                    continue;
                }
                Assert.assertEquals(gameBoard[x][y], null, gameBoardCopy);
            }
        }
        gameBoard[0][0].setName("Xyz");
        Player[][] realBoard = game.getBoard();
        Assert.assertEquals(realBoard[0][0].getName(), "Chuchu", gameBoardCopy);

        gameBoard[0][1] = player;
        realBoard = game.getBoard();
        Assert.assertEquals(realBoard[0][1], null, gameBoardCopy);

        gameBoard[0][0] = null;
        realBoard = game.getBoard();
        Assert.assertEquals(realBoard[0][0], player, gameBoardCopy);

        /*
         * Test bad moves.
         */
        Assert.assertFalse(game.setBoardAt(player, -1, 0), "setX cannot be < 0");
        Assert.assertFalse(game.setBoardAt(player, 0, -1), "setY cannot be < 0");
        Assert.assertFalse(game.setBoardAt(player, -1), "setX cannot be < 0");
    }

    /**
     * Test that the game detects and returns a winner properly.
     */
    @Test(priority = 0, timeOut = TEST_TIMEOUT)
    public void testWinner() {
        final String gameShouldNotBeOver = "No winner should be declared yet";
        final String gameShouldBeOver = "The game should be over now";
        final String scoreCount = "Game should increase the winner's score count";
        final String afterGame = "No moves are allowed after a game ends";

        Player chuchu = new Player("Chuchu");
        Player xyz = new Player("xyz");

        /*
         * Test uninitialized games.
         */
        ConnectN board = new ConnectN();
        Assert.assertEquals(board.getWinner(), null, gameShouldNotBeOver);
        board.setWidth(10);
        Assert.assertEquals(board.getWinner(), null, gameShouldNotBeOver);
        board.setHeight(8);
        Assert.assertEquals(board.getWinner(), null, gameShouldNotBeOver);
        board.setN(4);
        Assert.assertEquals(board.getWinner(), null, gameShouldNotBeOver);

        /*
         * Test simple games with a winner.
         */
        for (int count = 0; count < 32; count++) {
            board = new ConnectN(10, 10, 5);
            int randomX = ThreadLocalRandom.current().nextInt(0, 10);
            for (int i = 0; i < 5; i++) {
                Assert.assertEquals(board.getWinner(), null, gameShouldNotBeOver);
                Assert.assertTrue(board.setBoardAt(chuchu, randomX));
            }
            Assert.assertEquals(board.getWinner(), chuchu, gameShouldBeOver);
            Assert.assertEquals(chuchu.getScore(), count + 1, scoreCount);
            Assert.assertEquals(xyz.getScore(), 0, scoreCount);
            Assert.assertFalse(board.setBoardAt(chuchu, 0), afterGame);
            Assert.assertFalse(board.setBoardAt(xyz, 1), afterGame);
        }
        for (int count = 0; count < 32; count++) {
            board = new ConnectN(10, 10, 5);
            int randomX = ThreadLocalRandom.current().nextInt(0, 5);
            for (int i = 0; i < 5; i++) {
                Assert.assertEquals(board.getWinner(), null, gameShouldNotBeOver);
                Assert.assertTrue(board.setBoardAt(chuchu, randomX + i, 0));
            }
            Assert.assertEquals(board.getWinner(), chuchu, gameShouldBeOver);
            Assert.assertEquals(chuchu.getScore(), 32 + count + 1, scoreCount);
            Assert.assertEquals(xyz.getScore(), 0, scoreCount);
            Assert.assertFalse(board.setBoardAt(chuchu, 0), afterGame);
            Assert.assertFalse(board.setBoardAt(xyz, 1), afterGame);
        }
        /*
         * Test a game with no winner.
         */
        board = new ConnectN(8, 6, 7);
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 6; y++) {
                if (x % 2 == 0) {
                    Assert.assertTrue(board.setBoardAt(chuchu, x, y));
                    Assert.assertEquals(board.getBoardAt(x, y), chuchu);
                } else {
                    Assert.assertTrue(board.setBoardAt(xyz, x, y));
                    Assert.assertEquals(board.getBoardAt(x, y), xyz);
                }
                Assert.assertEquals(board.getWinner(), null, gameShouldNotBeOver);
            }
        }
        Assert.assertEquals(chuchu.getScore(), 2 * 32, scoreCount);
        Assert.assertEquals(xyz.getScore(), 0, scoreCount);

        /*
         * Test corner cases.
         */
        board = new ConnectN(8, 8, 4);
        Assert.assertTrue(board.setBoardAt(chuchu, 0, 0));
        Assert.assertTrue(board.setBoardAt(chuchu, 0, 1));
        Assert.assertTrue(board.setBoardAt(chuchu, 0, 2));
        Assert.assertTrue(board.setBoardAt(xyz, 0, 3));
        Assert.assertTrue(board.setBoardAt(chuchu, 0, 4));
        Assert.assertTrue(board.setBoardAt(chuchu, 0, 5));
        Assert.assertTrue(board.setBoardAt(chuchu, 0, 6));

        board = new ConnectN(8, 8, 4);
        Assert.assertTrue(board.setBoardAt(chuchu, 0, 0));
        Assert.assertTrue(board.setBoardAt(chuchu, 1, 0));
        Assert.assertTrue(board.setBoardAt(chuchu, 2, 0));
        Assert.assertTrue(board.setBoardAt(xyz, 3, 0));
        Assert.assertTrue(board.setBoardAt(chuchu, 4, 0));
        Assert.assertTrue(board.setBoardAt(chuchu, 5, 0));
        Assert.assertTrue(board.setBoardAt(chuchu, 6, 0));
    }

    /**
     * Test the equality works.
     */
    @SuppressWarnings("unlikely-arg-type")
    @Test(priority = 1, timeOut = TEST_TIMEOUT)
    public void testEquals() {
        /*
         * Basic equality tests.
         */
        ConnectN firstBoard = new ConnectN(6, 6, 4);
        Assert.assertTrue(firstBoard.equals(firstBoard), "Board should equal itself");
        Assert.assertFalse(firstBoard.equals(null), "Board should not equal null");
        Assert.assertFalse(firstBoard.equals(new String("Test")),
                "Board should not equal another type");
        ConnectN[] boards = {firstBoard};
        Assert.assertFalse(firstBoard.equals(boards), "Board should not equal an array");
        Assert.assertTrue(firstBoard.equals(boards[0]), "Board should equal itself");

        /*
         * Dimensional equality tests.
         */
        firstBoard = new ConnectN(10, 8, 4);
        ConnectN secondBoard = new ConnectN(10, 8, 4);
        Assert.assertFalse(firstBoard.equals(secondBoard),
                "Board should not equal another board with identical dimensions.");
    }

    /**
     * Test static methods, including create and compare.
     */
    @Test(priority = 1, timeOut = TEST_TIMEOUT)
    public void testStaticMethods() {
        final String validCreate = "Factory methods should create boards given valid parameters";
        final String invalidCreate = //
                "Factory methods should not create boards given invalid parameters";
        final String invalidCompare = "Factory methods should compare boards properly";

        /*
         * Test simple create with valid parameters.
         */
        ConnectN board = ConnectN.create(8, 10, 4);
        Assert.assertNotEquals(board, null, validCreate);
        Assert.assertEquals(board.getClass(), ConnectN.class, validCreate);
        Assert.assertEquals(board.getWidth(), 8, validCreate);
        Assert.assertEquals(board.getHeight(), 10, validCreate);
        Assert.assertEquals(board.getN(), 4, validCreate);

        /*
         * Test simple create with invalid parameters.
         */
        board = ConnectN.create(4, 10, 4);
        Assert.assertEquals(board, null, invalidCreate);
        board = ConnectN.create(10, 4, 6);
        Assert.assertEquals(board, null, invalidCreate);
        board = ConnectN.create(10, 10, 2);
        Assert.assertEquals(board, null, invalidCreate);
        board = ConnectN.create(10, 10, -1);
        Assert.assertEquals(board, null, invalidCreate);
        board = ConnectN.create(-1, 10, 4);
        Assert.assertEquals(board, null, invalidCreate);
        board = ConnectN.create(8, -1, 6);
        Assert.assertEquals(board, null, invalidCreate);
        board = ConnectN.create(10, 8, 10);
        Assert.assertEquals(board, null, invalidCreate);
        board = ConnectN.create(8, 10, 10);
        Assert.assertEquals(board, null, invalidCreate);

        /*
         * Test multi create with valid parameters.
         */
        ConnectN[] boards = ConnectN.createMany(6, 8, 10, 4);
        Assert.assertNotEquals(boards, null, validCreate);
        Assert.assertEquals(boards.getClass(), ConnectN[].class, validCreate);
        for (ConnectN arrayBoard : boards) {
            Assert.assertEquals(arrayBoard.getClass(), ConnectN.class, validCreate);
            Assert.assertEquals(arrayBoard.getWidth(), 8, validCreate);
            Assert.assertEquals(arrayBoard.getHeight(), 10, validCreate);
            Assert.assertEquals(arrayBoard.getN(), 4, validCreate);
        }

        /*
         * Test multi create with invalid parameters.
         */
        boards = ConnectN.createMany(10, 4, 10, 4);
        Assert.assertEquals(board, null, invalidCreate);
        boards = ConnectN.createMany(4, 10, 4, 6);
        Assert.assertEquals(board, null, invalidCreate);
        boards = ConnectN.createMany(3, 10, 10, 2);
        Assert.assertEquals(board, null, invalidCreate);
        boards = ConnectN.createMany(7, 10, 10, -1);
        Assert.assertEquals(board, null, invalidCreate);
        boards = ConnectN.createMany(9, -1, 10, 4);
        Assert.assertEquals(board, null, invalidCreate);
        boards = ConnectN.createMany(20, 8, -1, 6);
        Assert.assertEquals(board, null, invalidCreate);
        boards = ConnectN.createMany(1, 10, 8, 10);
        Assert.assertEquals(board, null, invalidCreate);
        boards = ConnectN.createMany(5, 8, 10, 10);
        Assert.assertEquals(board, null, invalidCreate);
        boards = ConnectN.createMany(0, 8, 10, 4);
        Assert.assertEquals(board, null, invalidCreate);

        /*
         * Test simple compare boards.
         */
        board = new ConnectN(10, 8, 6);
        ConnectN anotherBoard = new ConnectN(10, 8, 8);
        Assert.assertTrue(ConnectN.compareBoards(board, board));
        Assert.assertTrue(ConnectN.compareBoards(board, new ConnectN(board)));
        Assert.assertFalse(ConnectN.compareBoards(board, null));
        Assert.assertFalse(ConnectN.compareBoards(null, board));
        Assert.assertTrue(ConnectN.compareBoards(new ConnectN(), new ConnectN()));
        Assert.assertTrue(ConnectN.compareBoards(new ConnectN(10, 0), new ConnectN(10, 0)));
        Assert.assertTrue(ConnectN.compareBoards(new ConnectN(10, 6, 0), new ConnectN(10, 6, 0)));
        Assert.assertTrue(ConnectN.compareBoards(new ConnectN(10, 6, 4), new ConnectN(10, 6, 4)));
        Assert.assertFalse(ConnectN.compareBoards(board, anotherBoard), invalidCompare);
        Assert.assertTrue(anotherBoard.setN(6));
        Assert.assertTrue(ConnectN.compareBoards(board, anotherBoard), invalidCompare);
        Assert.assertTrue(board.setHeight(10));
        Assert.assertFalse(ConnectN.compareBoards(board, anotherBoard), invalidCompare);

        /*
         * Test slightly more complex compare boards.
         */
        board = new ConnectN(10, 8, 6);
        anotherBoard = new ConnectN(10, 8, 6);
        Player firstPlayer = new Player("Chuchu");
        Player secondPlayer = new Player("Xyz");
        Assert.assertTrue(ConnectN.compareBoards(board, anotherBoard));
        Assert.assertTrue(board.setBoardAt(firstPlayer, 0, 0));
        Assert.assertFalse(ConnectN.compareBoards(board, anotherBoard));
        Assert.assertTrue(anotherBoard.setBoardAt(firstPlayer, 0, 0));
        Assert.assertTrue(ConnectN.compareBoards(board, anotherBoard));
        Assert.assertTrue(board.setBoardAt(firstPlayer, 1, 0));
        Assert.assertTrue(anotherBoard.setBoardAt(secondPlayer, 1, 0));
        Assert.assertFalse(ConnectN.compareBoards(board, anotherBoard));

        /*
         * Test multi compare boards.
         */
        boards = ConnectN.createMany(6, 10, 10, 4);
        Assert.assertTrue(ConnectN.compareBoards(boards[0]), invalidCompare);
        Assert.assertTrue(ConnectN.compareBoards(boards), invalidCompare);
        Assert.assertTrue(ConnectN.compareBoards(boards[0], boards[2], boards[3]), invalidCompare);
        boards[1] = ConnectN.create(8, 8, 4);
        Assert.assertFalse(ConnectN.compareBoards(boards), invalidCompare);
        Assert.assertTrue(ConnectN.compareBoards(boards[0], boards[2], boards[3]), invalidCompare);
    }

    /**
     * Test that the ConnectN class maintains the game count correctly.
     */
    @Test(priority = 0, timeOut = TEST_TIMEOUT)
    public void testGameCount() {
        for (int i = 0; i < 13; i++) {
            Assert.assertEquals(ConnectN.getTotalGames(), i);
            ConnectN board = new ConnectN(12, 10, 8);
            Assert.assertEquals(board.getID(), i, "Board ID not set properly");
            Assert.assertEquals(ConnectN.getTotalGames(), i + 1);
        }

        /*
         * Make sure game count is not public.
         */
        Field[] publicFields = ConnectN.class.getFields();
        for (Field field : publicFields) {
            try {
                int publicCount = field.getInt(ConnectN.class);
                Assert.assertNotEquals(publicCount, 13, "Game count should not be public");
            } catch (Exception ignored) {
            }
        }
    }
}
