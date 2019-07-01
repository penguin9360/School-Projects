import java.util.ArrayList;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.Test;

import junit.framework.Assert;

/**
 * Test suite for the Tree class.
 * <p>
 * The provided test suite is correct and complete. You should not need to modify it. However, you
 * should understand it.
 *
 * @see <a href="https://cs125.cs.illinois.edu/MP/5/">MP5 Documentation</a>
 */
public class TreeTest {

    /** Timeout for tree tests. Solution takes 75 ms.*/
    private static final int TREE_TEST_TIMEOUT = 750;

    /**
     * Create a tree from an array of values.
     * <p>
     * Helper function for the testing suite.
     *
     * @param treeTestInput the input to create a tree from
     * @return the tree that was created, or null if creation fails
     */
    private Tree doCreate(final TreeTestInput treeTestInput) {
        boolean createSucceeded = true;
        Tree testTree = null;
        for (int insert : treeTestInput.values) {
            if (testTree == null) {
                testTree = new Tree(insert);
            } else {
                createSucceeded = testTree.insert(insert);
                if (!createSucceeded) {
                    break;
                }
            }
        }
        if (createSucceeded) {
            return testTree;
        } else {
            return null;
        }
    }

    /**
     * Test tree creation.
     */
    @Test(timeOut = TREE_TEST_TIMEOUT, groups = {"create"})
    public void testCreate() {
        for (TreeTestInput testInput : GOOD_TREES) {
            Assert.assertNotNull(doCreate(testInput));
        }
        for (TreeTestInput testInput : BAD_TREES) {
            Assert.assertNull(doCreate(testInput));
        }
    }

    /**
     * Test tree minimum and maximum.
     */
    @Test(timeOut = TREE_TEST_TIMEOUT, dependsOnGroups = {"create.*"})
    public void testMinAndMax() {
        for (TreeTestInput testInput : GOOD_TREES) {
            Tree tree = doCreate(testInput);
            Assert.assertNotNull(tree);
            Assert.assertEquals(testInput.maximum, tree.maximum());
            Assert.assertEquals(testInput.minimum, tree.minimum());
        }
    }

    /**
     * Test tree searches.
     */
    @Test(timeOut = TREE_TEST_TIMEOUT, dependsOnGroups = {"create.*"})
    public void testSearch() {
        for (TreeTestInput testInput : GOOD_TREES) {
            Tree tree = doCreate(testInput);
            Assert.assertNotNull(tree);
            for (Map.Entry<Integer, Boolean> precomputedSearchResult : //
            testInput.searchResults.entrySet()) {
                Tree result = tree.search(precomputedSearchResult.getKey());
                Assert.assertEquals(precomputedSearchResult.getValue().booleanValue(),
                        (result != null));
            }
        }
    }

    /**
     * Test tree node depth.
     */
    @Test(timeOut = TREE_TEST_TIMEOUT, dependsOnGroups = {"create.*"})
    public void testDepth() {
        for (TreeTestInput testInput : GOOD_TREES) {
            Tree tree = doCreate(testInput);
            Assert.assertNotNull(tree);
            for (Map.Entry<Integer, Integer> precomputedDepthResult : //
            testInput.depthResults.entrySet()) {
                Tree result = tree.search(precomputedDepthResult.getKey());
                Assert.assertNotNull(result);
                Assert.assertEquals(precomputedDepthResult.getValue().intValue(), result.depth());
            }
        }
    }

    /**
     * Test tree node descendants.
     */
    @Test(timeOut = TREE_TEST_TIMEOUT, dependsOnGroups = {"create.*"})
    public void testDescendants() {
        for (TreeTestInput testInput : GOOD_TREES) {
            Tree tree = doCreate(testInput);
            Assert.assertNotNull(tree);
            for (Map.Entry<Integer, Integer> precomputedDescendantResult : //
            testInput.descendantResults.entrySet()) {
                Tree result = tree.search(precomputedDescendantResult.getKey());
                Assert.assertNotNull(result);
                Assert.assertEquals(precomputedDescendantResult.getValue().intValue(),
                        result.descendants());
            }
        }
    }

    /**
     * Class for storing trees and precomputed results for the tree tests.
     */
    public static class TreeTestInput {

        /** Ordered list of values to insert. */
        int[] values;

        /** Pre-computed maximum value in this tree. */
        int maximum;

        /** Pre-computed minimum value in this tree. */
        int minimum;

        /** Pre-computed search results. */
        Map<Integer, Boolean> searchResults;

        /** Pre-computed depth results. */
        Map<Integer, Integer> depthResults;

        /** Pre-computed descendant results. */
        Map<Integer, Integer> descendantResults;

        /**
         * Create a new tree testing input.
         *
         * @param setValues ordered list of values to insert
         */
        public TreeTestInput(final int[] setValues) {
            values = setValues;
        }

        /**
         * Create a new tree testing input with precomputed values.
         *
         * @param setValues ordered list of values to insert
         * @param setMaximum precomputed maximum value
         * @param setMinimum precomputed minimum value
         * @param setSearchResults precomputed search results
         * @param setDepthResults precomputed depth results
         * @param setDescendantResults precomputed descendant results
         */
        public TreeTestInput(final int[] setValues, final int setMaximum, final int setMinimum,
                final LinkedHashMap<Integer, Boolean> setSearchResults,
                final LinkedHashMap<Integer, Integer> setDepthResults,
                final LinkedHashMap<Integer, Integer> setDescendantResults) {
            values = setValues;
            maximum = setMaximum;
            minimum = setMinimum;
            searchResults = setSearchResults;
            depthResults = setDepthResults;
            descendantResults = setDescendantResults;
        }
    }

    /** Pre-computed list of trees to use for testing. */
    private static final List<TreeTestInput> GOOD_TREES = //
            new ArrayList<>();

    /** Pre-computed bad trees with duplicate values. */
    private static final List<TreeTestInput> BAD_TREES = //
            new ArrayList<>();

    static {
        GOOD_TREES.add(new TreeTestInput(new int[]{1}, 1, 1,
                new LinkedHashMap<>() {
                    {
                        put(1, true);
                        put(2, false);
                        put(0, false);
                    }
                },
                new LinkedHashMap<>() {
                    {
                        put(1, 0);
                    }
                },
                new LinkedHashMap<>() {
                    {
                        put(1, 0);
                    }
                }));
        GOOD_TREES.add(new TreeTestInput(new int[]{2, 1, 3}, 3, 1,
                new LinkedHashMap<>() {
                    {
                        put(2, true);
                        put(5, false);
                        put(-1, false);
                    }
                },
                new LinkedHashMap<>() {
                    {
                        put(2, 0);
                        put(1, 1);
                        put(3, 1);
                    }
                },
                new LinkedHashMap<>() {
                    {
                        put(2, 2);
                        put(1, 0);
                        put(3, 0);
                    }
                }));
        GOOD_TREES.add(new TreeTestInput(new int[]{1, 2, 3, 4, 5}, 5, 1,
                new LinkedHashMap<>() {
                    {
                        put(2, true);
                        put(5, true);
                        put(10, false);
                        put(6, false);
                    }
                },
                new LinkedHashMap<>() {
                    {
                        put(1, 0);
                        put(2, 1);
                        put(3, 2);
                        put(4, 3);
                        put(5, 4);
                    }
                },
                new LinkedHashMap<>() {
                    {
                        put(1, 4);
                        put(2, 3);
                        put(3, 2);
                        put(4, 1);
                        put(5, 0);
                    }
                }));
        /* BEGIN AUTOGENERATED CODE */
        GOOD_TREES.add(new TreeTestInput(new int[]{
                343,
                                -981,
                                612,
                                913,
                                -322,
                                559,
                                973,
                                -972,
                                1003,
                                389,
                                -392,
                                978,
                                115,
                                634,
                                -730,
                                -888,
                                -317,
                                463,
                                -615,
                                -836,
                                -690,
                                727,
                                832
                 }, 1003, -981,
                new LinkedHashMap<>() {
                    {
                        put(404, false);
                        put(-185, false);
                        put(185, false);
                        put(-58, false);
                        put(-982, false);
                        put(-304, false);
                        put(612, true);
                        put(-615, true);
                        put(-972, true);
                        put(-808, false);
                        put(779, false);
                        put(-250, false);
                        put(-690, true);
                        put(559, true);
                        put(-981, true);
                        put(-459, false);
                    }
                },
                new LinkedHashMap<>() {
                    {
                        put(343, 0);
                        put(-981, 1);
                        put(612, 1);
                        put(913, 2);
                        put(-322, 2);
                        put(559, 2);
                        put(973, 3);
                        put(-972, 3);
                        put(1003, 4);
                        put(389, 3);
                        put(-392, 4);
                        put(978, 5);
                        put(115, 3);
                        put(634, 3);
                        put(-730, 5);
                        put(-888, 6);
                        put(-317, 4);
                        put(463, 4);
                        put(-615, 6);
                        put(-836, 7);
                        put(-690, 7);
                        put(727, 4);
                        put(832, 5);
                    }
                },
                new LinkedHashMap<>() {
                    {
                        put(343, 22);
                        put(-981, 10);
                        put(612, 10);
                        put(913, 6);
                        put(-322, 9);
                        put(559, 2);
                        put(973, 2);
                        put(-972, 6);
                        put(1003, 1);
                        put(389, 1);
                        put(-392, 5);
                        put(978, 0);
                        put(115, 1);
                        put(634, 2);
                        put(-730, 4);
                        put(-888, 1);
                        put(-317, 0);
                        put(463, 0);
                        put(-615, 1);
                        put(-836, 0);
                        put(-690, 0);
                        put(727, 1);
                        put(832, 0);
                    }
                }));
        GOOD_TREES.add(new TreeTestInput(new int[]{
                303,
                                61,
                                572,
                                863,
                                -353,
                                849,
                                669,
                                750,
                                -635,
                                157,
                                -917,
                                784,
                                768,
                                -824,
                                552,
                                533,
                                -347,
                                169,
                                -807,
                                -664,
                                733,
                                678,
                                -599,
                                347,
                                348,
                                42,
                                -552,
                                -680,
                                -837,
                                52,
                                -758,
                                -260,
                                775,
                                50,
                                -620,
                                166,
                                -831,
                                -882
                 }, 863, -917,
                new LinkedHashMap<>() {
                    {
                        put(-956, false);
                        put(552, true);
                        put(-270, false);
                        put(597, false);
                        put(784, true);
                        put(348, true);
                        put(738, false);
                        put(203, false);
                        put(-483, false);
                        put(711, false);
                        put(42, true);
                        put(50, true);
                        put(-38, false);
                        put(750, true);
                        put(361, false);
                        put(372, false);
                        put(-304, false);
                        put(-797, false);
                        put(95, false);
                        put(-609, false);
                        put(-837, true);
                        put(-24, false);
                        put(839, false);
                        put(775, true);
                        put(-807, true);
                        put(166, true);
                        put(288, false);
                    }
                },
                new LinkedHashMap<>() {
                    {
                        put(303, 0);
                        put(61, 1);
                        put(572, 1);
                        put(863, 2);
                        put(-353, 2);
                        put(849, 3);
                        put(669, 4);
                        put(750, 5);
                        put(-635, 3);
                        put(157, 2);
                        put(-917, 4);
                        put(784, 6);
                        put(768, 7);
                        put(-824, 5);
                        put(552, 2);
                        put(533, 3);
                        put(-347, 3);
                        put(169, 3);
                        put(-807, 6);
                        put(-664, 7);
                        put(733, 6);
                        put(678, 7);
                        put(-599, 4);
                        put(347, 4);
                        put(348, 5);
                        put(42, 4);
                        put(-552, 5);
                        put(-680, 8);
                        put(-837, 6);
                        put(52, 5);
                        put(-758, 9);
                        put(-260, 5);
                        put(775, 8);
                        put(50, 6);
                        put(-620, 5);
                        put(166, 4);
                        put(-831, 7);
                        put(-882, 7);
                    }
                },
                new LinkedHashMap<>() {
                    {
                        put(303, 37);
                        put(61, 22);
                        put(572, 13);
                        put(863, 8);
                        put(-353, 18);
                        put(849, 7);
                        put(669, 6);
                        put(750, 5);
                        put(-635, 12);
                        put(157, 2);
                        put(-917, 8);
                        put(784, 2);
                        put(768, 1);
                        put(-824, 7);
                        put(552, 3);
                        put(533, 2);
                        put(-347, 4);
                        put(169, 1);
                        put(-807, 3);
                        put(-664, 2);
                        put(733, 1);
                        put(678, 0);
                        put(-599, 2);
                        put(347, 1);
                        put(348, 0);
                        put(42, 3);
                        put(-552, 0);
                        put(-680, 1);
                        put(-837, 2);
                        put(52, 1);
                        put(-758, 0);
                        put(-260, 0);
                        put(775, 0);
                        put(50, 0);
                        put(-620, 0);
                        put(166, 0);
                        put(-831, 0);
                        put(-882, 0);
                    }
                }));
        GOOD_TREES.add(new TreeTestInput(new int[]{
                -436,
                                203,
                                -968,
                                -224,
                                -242,
                                -294,
                                587,
                                -299,
                                970,
                                -665,
                                347,
                                158
                 }, 970, -968,
                new LinkedHashMap<>() {
                    {
                        put(203, true);
                        put(587, true);
                        put(418, false);
                        put(75, false);
                        put(481, false);
                        put(-492, false);
                        put(-607, false);
                        put(-968, true);
                        put(-436, true);
                        put(970, true);
                        put(470, false);
                        put(-718, false);
                        put(-242, true);
                        put(-948, false);
                        put(692, false);
                        put(-633, false);
                        put(-299, true);
                        put(-153, false);
                        put(-665, true);
                        put(726, false);
                        put(-270, false);
                        put(465, false);
                        put(-982, false);
                    }
                },
                new LinkedHashMap<>() {
                    {
                        put(-436, 0);
                        put(203, 1);
                        put(-968, 1);
                        put(-224, 2);
                        put(-242, 3);
                        put(-294, 4);
                        put(587, 2);
                        put(-299, 5);
                        put(970, 3);
                        put(-665, 2);
                        put(347, 3);
                        put(158, 3);
                    }
                },
                new LinkedHashMap<>() {
                    {
                        put(-436, 11);
                        put(203, 8);
                        put(-968, 1);
                        put(-224, 4);
                        put(-242, 2);
                        put(-294, 1);
                        put(587, 2);
                        put(-299, 0);
                        put(970, 0);
                        put(-665, 0);
                        put(347, 0);
                        put(158, 0);
                    }
                }));
        GOOD_TREES.add(new TreeTestInput(new int[]{
                1002,
                                -321,
                                224,
                                -231,
                                981,
                                -335,
                                -420,
                                -262,
                                305,
                                -929,
                                -523,
                                -1021,
                                842,
                                602,
                                -882,
                                189,
                                -344,
                                -550,
                                727,
                                825,
                                -438,
                                -805,
                                -717,
                                1011,
                                601,
                                398,
                                -843,
                                98,
                                -143,
                                163,
                                906
                 }, 1011, -1021,
                new LinkedHashMap<>() {
                    {
                        put(-119, false);
                        put(398, true);
                        put(-1021, true);
                        put(932, false);
                        put(98, true);
                        put(-344, true);
                        put(1002, true);
                        put(706, false);
                        put(189, true);
                        put(906, true);
                        put(185, false);
                        put(-32, false);
                        put(-231, true);
                        put(58, false);
                        put(-335, true);
                    }
                },
                new LinkedHashMap<>() {
                    {
                        put(1002, 0);
                        put(-321, 1);
                        put(224, 2);
                        put(-231, 3);
                        put(981, 3);
                        put(-335, 2);
                        put(-420, 3);
                        put(-262, 4);
                        put(305, 4);
                        put(-929, 4);
                        put(-523, 5);
                        put(-1021, 5);
                        put(842, 5);
                        put(602, 6);
                        put(-882, 6);
                        put(189, 4);
                        put(-344, 4);
                        put(-550, 7);
                        put(727, 7);
                        put(825, 8);
                        put(-438, 6);
                        put(-805, 8);
                        put(-717, 9);
                        put(1011, 1);
                        put(601, 7);
                        put(398, 8);
                        put(-843, 9);
                        put(98, 5);
                        put(-143, 6);
                        put(163, 6);
                        put(906, 6);
                    }
                },
                new LinkedHashMap<>() {
                    {
                        put(1002, 30);
                        put(-321, 28);
                        put(224, 15);
                        put(-231, 5);
                        put(981, 8);
                        put(-335, 11);
                        put(-420, 10);
                        put(-262, 0);
                        put(305, 7);
                        put(-929, 8);
                        put(-523, 6);
                        put(-1021, 0);
                        put(842, 6);
                        put(602, 4);
                        put(-882, 4);
                        put(189, 3);
                        put(-344, 0);
                        put(-550, 3);
                        put(727, 1);
                        put(825, 0);
                        put(-438, 0);
                        put(-805, 2);
                        put(-717, 0);
                        put(1011, 0);
                        put(601, 1);
                        put(398, 0);
                        put(-843, 0);
                        put(98, 2);
                        put(-143, 0);
                        put(163, 0);
                        put(906, 0);
                    }
                }));
        GOOD_TREES.add(new TreeTestInput(new int[]{
                386,
                                919,
                                390,
                                -830,
                                -154,
                                -908,
                                -215,
                                -521,
                                657,
                                976
                 }, 976, -908,
                new LinkedHashMap<>() {
                    {
                        put(57, false);
                        put(-361, false);
                        put(179, false);
                        put(976, true);
                        put(651, false);
                        put(217, false);
                        put(-689, false);
                        put(919, true);
                        put(-873, false);
                        put(390, true);
                        put(164, false);
                        put(-521, true);
                        put(-215, true);
                        put(126, false);
                        put(-511, false);
                    }
                },
                new LinkedHashMap<>() {
                    {
                        put(386, 0);
                        put(919, 1);
                        put(390, 2);
                        put(-830, 1);
                        put(-154, 2);
                        put(-908, 2);
                        put(-215, 3);
                        put(-521, 4);
                        put(657, 3);
                        put(976, 2);
                    }
                },
                new LinkedHashMap<>() {
                    {
                        put(386, 9);
                        put(919, 3);
                        put(390, 1);
                        put(-830, 4);
                        put(-154, 2);
                        put(-908, 0);
                        put(-215, 1);
                        put(-521, 0);
                        put(657, 0);
                        put(976, 0);
                    }
                }));
        GOOD_TREES.add(new TreeTestInput(new int[]{
                174,
                                -456,
                                630,
                                -974,
                                731,
                                680,
                                568
                 }, 731, -974,
                new LinkedHashMap<>() {
                    {
                        put(630, true);
                        put(-710, false);
                        put(-174, false);
                        put(-854, false);
                        put(174, true);
                        put(194, false);
                        put(731, true);
                        put(-456, true);
                        put(623, false);
                        put(680, true);
                        put(-298, false);
                        put(-732, false);
                        put(-363, false);
                        put(-357, false);
                        put(-117, false);
                    }
                },
                new LinkedHashMap<>() {
                    {
                        put(174, 0);
                        put(-456, 1);
                        put(630, 1);
                        put(-974, 2);
                        put(731, 2);
                        put(680, 3);
                        put(568, 2);
                    }
                },
                new LinkedHashMap<>() {
                    {
                        put(174, 6);
                        put(-456, 1);
                        put(630, 3);
                        put(-974, 0);
                        put(731, 1);
                        put(680, 0);
                        put(568, 0);
                    }
                }));
        GOOD_TREES.add(new TreeTestInput(new int[]{
                141,
                                236,
                                -232,
                                -995,
                                -820,
                                932,
                                -12,
                                -966,
                                974,
                                -20,
                                700,
                                227,
                                263,
                                -500,
                                234,
                                42,
                                88,
                                -707,
                                29,
                                -296,
                                -888,
                                -130,
                                264
                 }, 974, -995,
                new LinkedHashMap<>() {
                    {
                        put(-578, false);
                        put(-296, true);
                        put(-685, false);
                        put(-731, false);
                        put(88, true);
                        put(-981, false);
                        put(-820, true);
                        put(264, true);
                        put(-995, true);
                        put(337, false);
                        put(-500, true);
                        put(-707, true);
                        put(-20, true);
                        put(141, true);
                        put(-888, true);
                        put(232, false);
                        put(962, false);
                        put(-12, true);
                        put(-652, false);
                        put(845, false);
                        put(309, false);
                        put(652, false);
                    }
                },
                new LinkedHashMap<>() {
                    {
                        put(141, 0);
                        put(236, 1);
                        put(-232, 1);
                        put(-995, 2);
                        put(-820, 3);
                        put(932, 2);
                        put(-12, 2);
                        put(-966, 4);
                        put(974, 3);
                        put(-20, 3);
                        put(700, 3);
                        put(227, 2);
                        put(263, 4);
                        put(-500, 4);
                        put(234, 3);
                        put(42, 3);
                        put(88, 4);
                        put(-707, 5);
                        put(29, 4);
                        put(-296, 5);
                        put(-888, 5);
                        put(-130, 4);
                        put(264, 5);
                    }
                },
                new LinkedHashMap<>() {
                    {
                        put(141, 22);
                        put(236, 7);
                        put(-232, 13);
                        put(-995, 6);
                        put(-820, 5);
                        put(932, 4);
                        put(-12, 5);
                        put(-966, 1);
                        put(974, 0);
                        put(-20, 1);
                        put(700, 2);
                        put(227, 1);
                        put(263, 1);
                        put(-500, 2);
                        put(234, 0);
                        put(42, 2);
                        put(88, 0);
                        put(-707, 0);
                        put(29, 0);
                        put(-296, 0);
                        put(-888, 0);
                        put(-130, 0);
                        put(264, 0);
                    }
                }));
        GOOD_TREES.add(new TreeTestInput(new int[]{
                -429,
                                -779,
                                689,
                                454,
                                -384,
                                -778,
                                299,
                                -913,
                                -166,
                                -965,
                                205,
                                -750,
                                -373,
                                -312,
                                476,
                                -124,
                                -362
                 }, 689, -965,
                new LinkedHashMap<>() {
                    {
                        put(698, false);
                        put(-446, false);
                        put(-779, true);
                        put(598, false);
                        put(-384, true);
                        put(755, false);
                        put(-373, true);
                        put(476, true);
                        put(205, true);
                        put(-243, false);
                        put(452, false);
                        put(70, false);
                        put(532, false);
                        put(580, false);
                        put(-100, false);
                        put(-429, true);
                        put(602, false);
                        put(-859, false);
                        put(-362, true);
                        put(410, false);
                    }
                },
                new LinkedHashMap<>() {
                    {
                        put(-429, 0);
                        put(-779, 1);
                        put(689, 1);
                        put(454, 2);
                        put(-384, 3);
                        put(-778, 2);
                        put(299, 4);
                        put(-913, 2);
                        put(-166, 5);
                        put(-965, 3);
                        put(205, 6);
                        put(-750, 3);
                        put(-373, 6);
                        put(-312, 7);
                        put(476, 3);
                        put(-124, 7);
                        put(-362, 8);
                    }
                },
                new LinkedHashMap<>() {
                    {
                        put(-429, 16);
                        put(-779, 4);
                        put(689, 10);
                        put(454, 9);
                        put(-384, 7);
                        put(-778, 1);
                        put(299, 6);
                        put(-913, 1);
                        put(-166, 5);
                        put(-965, 0);
                        put(205, 1);
                        put(-750, 0);
                        put(-373, 2);
                        put(-312, 1);
                        put(476, 0);
                        put(-124, 0);
                        put(-362, 0);
                    }
                }));
        GOOD_TREES.add(new TreeTestInput(new int[]{
                -843,
                                -456,
                                -630,
                                -767,
                                1012,
                                -740,
                                105,
                                -445,
                                296,
                                -497,
                                69,
                                -174,
                                -34,
                                532,
                                796,
                                -150,
                                -225,
                                -889,
                                -43,
                                360,
                                863,
                                234
                 }, 1012, -889,
                new LinkedHashMap<>() {
                    {
                        put(532, true);
                        put(-814, false);
                        put(-767, true);
                        put(-630, true);
                        put(-515, false);
                        put(105, true);
                        put(-150, true);
                        put(-805, false);
                        put(-497, true);
                        put(793, false);
                        put(-445, true);
                        put(482, false);
                        put(824, false);
                        put(20, false);
                    }
                },
                new LinkedHashMap<>() {
                    {
                        put(-843, 0);
                        put(-456, 1);
                        put(-630, 2);
                        put(-767, 3);
                        put(1012, 2);
                        put(-740, 4);
                        put(105, 3);
                        put(-445, 4);
                        put(296, 4);
                        put(-497, 3);
                        put(69, 5);
                        put(-174, 6);
                        put(-34, 7);
                        put(532, 5);
                        put(796, 6);
                        put(-150, 8);
                        put(-225, 7);
                        put(-889, 1);
                        put(-43, 9);
                        put(360, 6);
                        put(863, 7);
                        put(234, 5);
                    }
                },
                new LinkedHashMap<>() {
                    {
                        put(-843, 21);
                        put(-456, 19);
                        put(-630, 3);
                        put(-767, 1);
                        put(1012, 14);
                        put(-740, 0);
                        put(105, 13);
                        put(-445, 6);
                        put(296, 5);
                        put(-497, 0);
                        put(69, 5);
                        put(-174, 4);
                        put(-34, 2);
                        put(532, 3);
                        put(796, 1);
                        put(-150, 1);
                        put(-225, 0);
                        put(-889, 0);
                        put(-43, 0);
                        put(360, 0);
                        put(863, 0);
                        put(234, 0);
                    }
                }));
        GOOD_TREES.add(new TreeTestInput(new int[]{
                960,
                                -174,
                                -503,
                                132,
                                588,
                                -805,
                                940,
                                -767,
                                -405,
                                867,
                                196,
                                878,
                                -72,
                                -194,
                                -320,
                                1005,
                                725,
                                -106,
                                56,
                                -296,
                                -700,
                                212,
                                402,
                                272,
                                -1021,
                                305,
                                933,
                                965,
                                -381,
                                110,
                                903,
                                -185,
                                -841,
                                -533,
                                314,
                                600,
                                468,
                                -40,
                                -544,
                                144,
                                345,
                                -326,
                                337,
                                388,
                                817,
                                -204,
                                524,
                                -215,
                                299,
                                -792,
                                -578
                 }, 1005, -1021,
                new LinkedHashMap<>() {
                    {
                        put(-251, false);
                        put(-215, true);
                        put(-381, true);
                        put(132, true);
                        put(56, true);
                        put(468, true);
                        put(-194, true);
                        put(903, true);
                        put(109, false);
                        put(-674, false);
                        put(-413, false);
                        put(-504, false);
                        put(-503, true);
                        put(-792, true);
                        put(-686, false);
                        put(333, false);
                        put(144, true);
                        put(725, true);
                        put(-678, false);
                        put(-231, false);
                    }
                },
                new LinkedHashMap<>() {
                    {
                        put(960, 0);
                        put(-174, 1);
                        put(-503, 2);
                        put(132, 2);
                        put(588, 3);
                        put(-805, 3);
                        put(940, 4);
                        put(-767, 4);
                        put(-405, 3);
                        put(867, 5);
                        put(196, 4);
                        put(878, 6);
                        put(-72, 3);
                        put(-194, 4);
                        put(-320, 5);
                        put(1005, 1);
                        put(725, 6);
                        put(-106, 4);
                        put(56, 4);
                        put(-296, 6);
                        put(-700, 5);
                        put(212, 5);
                        put(402, 6);
                        put(272, 7);
                        put(-1021, 4);
                        put(305, 8);
                        put(933, 7);
                        put(965, 2);
                        put(-381, 6);
                        put(110, 5);
                        put(903, 8);
                        put(-185, 5);
                        put(-841, 5);
                        put(-533, 6);
                        put(314, 9);
                        put(600, 7);
                        put(468, 7);
                        put(-40, 5);
                        put(-544, 7);
                        put(144, 5);
                        put(345, 10);
                        put(-326, 7);
                        put(337, 11);
                        put(388, 11);
                        put(817, 7);
                        put(-204, 7);
                        put(524, 8);
                        put(-215, 8);
                        put(299, 9);
                        put(-792, 5);
                        put(-578, 8);
                    }
                },
                new LinkedHashMap<>() {
                    {
                        put(960, 50);
                        put(-174, 47);
                        put(-503, 18);
                        put(132, 27);
                        put(588, 21);
                        put(-805, 8);
                        put(940, 7);
                        put(-767, 5);
                        put(-405, 8);
                        put(867, 6);
                        put(196, 12);
                        put(878, 2);
                        put(-72, 4);
                        put(-194, 7);
                        put(-320, 5);
                        put(1005, 1);
                        put(725, 2);
                        put(-106, 0);
                        put(56, 2);
                        put(-296, 2);
                        put(-700, 3);
                        put(212, 10);
                        put(402, 9);
                        put(272, 6);
                        put(-1021, 1);
                        put(305, 5);
                        put(933, 1);
                        put(965, 0);
                        put(-381, 1);
                        put(110, 0);
                        put(903, 0);
                        put(-185, 0);
                        put(-841, 0);
                        put(-533, 2);
                        put(314, 3);
                        put(600, 0);
                        put(468, 1);
                        put(-40, 0);
                        put(-544, 1);
                        put(144, 0);
                        put(345, 2);
                        put(-326, 0);
                        put(337, 0);
                        put(388, 0);
                        put(817, 0);
                        put(-204, 1);
                        put(524, 0);
                        put(-215, 0);
                        put(299, 0);
                        put(-792, 0);
                        put(-578, 0);
                    }
                }));
        GOOD_TREES.add(new TreeTestInput(new int[]{
                -110,
                                484,
                                1024,
                                604,
                                -344,
                                -165,
                                839,
                                530,
                                -365,
                                820,
                                -767,
                                -855,
                                473,
                                136,
                                -735,
                                -1,
                                427,
                                965,
                                -820,
                                910,
                                -114,
                                -489,
                                684,
                                343,
                                -385,
                                486,
                                653,
                                -500,
                                -357,
                                724,
                                -302,
                                373,
                                387,
                                945,
                                -925,
                                568,
                                -601,
                                -673,
                                699,
                                -685,
                                396,
                                -449,
                                870,
                                -99,
                                111,
                                683,
                                785,
                                -716,
                                -269,
                                737,
                                607
                 }, 1024, -925,
                new LinkedHashMap<>() {
                    {
                        put(30, false);
                        put(671, false);
                        put(870, true);
                        put(-99, true);
                        put(820, true);
                        put(530, true);
                        put(-471, false);
                        put(-657, false);
                        put(396, true);
                        put(45, false);
                        put(-581, false);
                        put(699, true);
                        put(29, false);
                        put(473, true);
                        put(653, true);
                        put(768, false);
                        put(-357, true);
                        put(136, true);
                        put(684, true);
                        put(-767, true);
                        put(-114, true);
                        put(373, true);
                        put(435, false);
                        put(-302, true);
                        put(-601, true);
                    }
                },
                new LinkedHashMap<>() {
                    {
                        put(-110, 0);
                        put(484, 1);
                        put(1024, 2);
                        put(604, 3);
                        put(-344, 1);
                        put(-165, 2);
                        put(839, 4);
                        put(530, 4);
                        put(-365, 2);
                        put(820, 5);
                        put(-767, 3);
                        put(-855, 4);
                        put(473, 2);
                        put(136, 3);
                        put(-735, 4);
                        put(-1, 4);
                        put(427, 4);
                        put(965, 5);
                        put(-820, 5);
                        put(910, 6);
                        put(-114, 3);
                        put(-489, 5);
                        put(684, 6);
                        put(343, 5);
                        put(-385, 6);
                        put(486, 5);
                        put(653, 7);
                        put(-500, 6);
                        put(-357, 3);
                        put(724, 7);
                        put(-302, 3);
                        put(373, 6);
                        put(387, 7);
                        put(945, 7);
                        put(-925, 5);
                        put(568, 5);
                        put(-601, 7);
                        put(-673, 8);
                        put(699, 8);
                        put(-685, 9);
                        put(396, 8);
                        put(-449, 7);
                        put(870, 7);
                        put(-99, 5);
                        put(111, 5);
                        put(683, 8);
                        put(785, 8);
                        put(-716, 10);
                        put(-269, 4);
                        put(737, 9);
                        put(607, 8);
                    }
                },
                new LinkedHashMap<>() {
                    {
                        put(-110, 50);
                        put(484, 29);
                        put(1024, 18);
                        put(604, 17);
                        put(-344, 19);
                        put(-165, 3);
                        put(839, 13);
                        put(530, 2);
                        put(-365, 14);
                        put(820, 8);
                        put(-767, 12);
                        put(-855, 2);
                        put(473, 9);
                        put(136, 8);
                        put(-735, 8);
                        put(-1, 2);
                        put(427, 4);
                        put(965, 3);
                        put(-820, 0);
                        put(910, 2);
                        put(-114, 0);
                        put(-489, 7);
                        put(684, 7);
                        put(343, 3);
                        put(-385, 1);
                        put(486, 0);
                        put(653, 2);
                        put(-500, 4);
                        put(-357, 0);
                        put(724, 3);
                        put(-302, 1);
                        put(373, 2);
                        put(387, 1);
                        put(945, 0);
                        put(-925, 0);
                        put(568, 0);
                        put(-601, 3);
                        put(-673, 2);
                        put(699, 0);
                        put(-685, 1);
                        put(396, 0);
                        put(-449, 0);
                        put(870, 0);
                        put(-99, 0);
                        put(111, 0);
                        put(683, 0);
                        put(785, 1);
                        put(-716, 0);
                        put(-269, 0);
                        put(737, 0);
                        put(607, 0);
                    }
                }));
        GOOD_TREES.add(new TreeTestInput(new int[]{
                83,
                                -138,
                                873,
                                -382,
                                887,
                                988,
                                -606,
                                657,
                                -543,
                                -756,
                                847,
                                -268,
                                -28,
                                564,
                                -200
                 }, 988, -756,
                new LinkedHashMap<>() {
                    {
                        put(-200, true);
                        put(-61, false);
                        put(519, false);
                        put(-543, true);
                        put(-756, true);
                        put(485, false);
                        put(302, false);
                        put(887, true);
                        put(83, true);
                        put(847, true);
                        put(-267, false);
                        put(570, false);
                        put(-660, false);
                        put(-677, false);
                        put(-419, false);
                        put(-268, true);
                    }
                },
                new LinkedHashMap<>() {
                    {
                        put(83, 0);
                        put(-138, 1);
                        put(873, 1);
                        put(-382, 2);
                        put(887, 2);
                        put(988, 3);
                        put(-606, 3);
                        put(657, 2);
                        put(-543, 4);
                        put(-756, 4);
                        put(847, 3);
                        put(-268, 3);
                        put(-28, 2);
                        put(564, 3);
                        put(-200, 4);
                    }
                },
                new LinkedHashMap<>() {
                    {
                        put(83, 14);
                        put(-138, 7);
                        put(873, 5);
                        put(-382, 5);
                        put(887, 1);
                        put(988, 0);
                        put(-606, 2);
                        put(657, 2);
                        put(-543, 0);
                        put(-756, 0);
                        put(847, 0);
                        put(-268, 1);
                        put(-28, 0);
                        put(564, 0);
                        put(-200, 0);
                    }
                }));
        GOOD_TREES.add(new TreeTestInput(new int[]{
                285,
                                53,
                                141,
                                -501,
                                -980,
                                -838,
                                -37,
                                -465,
                                808,
                                -531,
                                -456,
                                -621,
                                879,
                                910,
                                17,
                                579,
                                923,
                                -48,
                                255,
                                875,
                                -344,
                                307,
                                672,
                                176,
                                498,
                                -360,
                                11,
                                236,
                                -511,
                                -847,
                                613,
                                -389,
                                635,
                                -390,
                                -508,
                                1009
                 }, 1009, -980,
                new LinkedHashMap<>() {
                    {
                        put(53, true);
                        put(-13, false);
                        put(11, true);
                        put(1009, true);
                        put(610, false);
                        put(-587, false);
                        put(713, false);
                        put(-389, true);
                        put(-378, false);
                        put(672, true);
                        put(236, true);
                        put(-208, false);
                        put(-505, false);
                        put(-847, true);
                        put(-501, true);
                        put(-980, true);
                        put(-48, true);
                        put(-508, true);
                        put(307, true);
                        put(261, false);
                        put(-269, false);
                        put(-344, true);
                        put(-643, false);
                        put(563, false);
                        put(326, false);
                        put(-838, true);
                    }
                },
                new LinkedHashMap<>() {
                    {
                        put(285, 0);
                        put(53, 1);
                        put(141, 2);
                        put(-501, 2);
                        put(-980, 3);
                        put(-838, 4);
                        put(-37, 3);
                        put(-465, 4);
                        put(808, 1);
                        put(-531, 5);
                        put(-456, 5);
                        put(-621, 6);
                        put(879, 2);
                        put(910, 3);
                        put(17, 4);
                        put(579, 2);
                        put(923, 4);
                        put(-48, 6);
                        put(255, 3);
                        put(875, 3);
                        put(-344, 7);
                        put(307, 3);
                        put(672, 3);
                        put(176, 4);
                        put(498, 4);
                        put(-360, 8);
                        put(11, 5);
                        put(236, 5);
                        put(-511, 6);
                        put(-847, 5);
                        put(613, 4);
                        put(-389, 9);
                        put(635, 5);
                        put(-390, 10);
                        put(-508, 7);
                        put(1009, 5);
                    }
                },
                new LinkedHashMap<>() {
                    {
                        put(285, 35);
                        put(53, 22);
                        put(141, 3);
                        put(-501, 17);
                        put(-980, 6);
                        put(-838, 5);
                        put(-37, 9);
                        put(-465, 6);
                        put(808, 11);
                        put(-531, 3);
                        put(-456, 5);
                        put(-621, 0);
                        put(879, 4);
                        put(910, 2);
                        put(17, 1);
                        put(579, 5);
                        put(923, 1);
                        put(-48, 4);
                        put(255, 2);
                        put(875, 0);
                        put(-344, 3);
                        put(307, 1);
                        put(672, 2);
                        put(176, 1);
                        put(498, 0);
                        put(-360, 2);
                        put(11, 0);
                        put(236, 0);
                        put(-511, 1);
                        put(-847, 0);
                        put(613, 1);
                        put(-389, 1);
                        put(635, 0);
                        put(-390, 0);
                        put(-508, 0);
                        put(1009, 0);
                    }
                }));
        GOOD_TREES.add(new TreeTestInput(new int[]{
                81,
                                -646,
                                -956,
                                -155,
                                -832,
                                -240,
                                -327,
                                -892,
                                -658,
                                681,
                                -909,
                                -405,
                                287,
                                111,
                                -359,
                                563,
                                -756,
                                -854,
                                -700,
                                654,
                                -37,
                                -443,
                                876,
                                -135,
                                22,
                                -867,
                                267,
                                423,
                                735,
                                722,
                                -733,
                                -761,
                                653,
                                596,
                                -482,
                                -474,
                                -613
                 }, 876, -956,
                new LinkedHashMap<>() {
                    {
                        put(-405, true);
                        put(287, true);
                        put(319, false);
                        put(22, true);
                        put(735, true);
                        put(563, true);
                        put(-909, true);
                        put(654, true);
                        put(-806, false);
                        put(1003, false);
                        put(-541, false);
                        put(-59, false);
                        put(-327, true);
                        put(-155, true);
                        put(214, false);
                        put(-37, true);
                        put(267, true);
                    }
                },
                new LinkedHashMap<>() {
                    {
                        put(81, 0);
                        put(-646, 1);
                        put(-956, 2);
                        put(-155, 2);
                        put(-832, 3);
                        put(-240, 3);
                        put(-327, 4);
                        put(-892, 4);
                        put(-658, 4);
                        put(681, 1);
                        put(-909, 5);
                        put(-405, 5);
                        put(287, 2);
                        put(111, 3);
                        put(-359, 6);
                        put(563, 3);
                        put(-756, 5);
                        put(-854, 5);
                        put(-700, 6);
                        put(654, 4);
                        put(-37, 3);
                        put(-443, 6);
                        put(876, 2);
                        put(-135, 4);
                        put(22, 4);
                        put(-867, 6);
                        put(267, 4);
                        put(423, 4);
                        put(735, 3);
                        put(722, 4);
                        put(-733, 7);
                        put(-761, 6);
                        put(653, 5);
                        put(596, 6);
                        put(-482, 7);
                        put(-474, 8);
                        put(-613, 8);
                    }
                },
                new LinkedHashMap<>() {
                    {
                        put(81, 36);
                        put(-646, 23);
                        put(-956, 10);
                        put(-155, 11);
                        put(-832, 9);
                        put(-240, 7);
                        put(-327, 6);
                        put(-892, 3);
                        put(-658, 4);
                        put(681, 11);
                        put(-909, 0);
                        put(-405, 5);
                        put(287, 7);
                        put(111, 1);
                        put(-359, 0);
                        put(563, 4);
                        put(-756, 3);
                        put(-854, 1);
                        put(-700, 1);
                        put(654, 2);
                        put(-37, 2);
                        put(-443, 3);
                        put(876, 2);
                        put(-135, 0);
                        put(22, 0);
                        put(-867, 0);
                        put(267, 0);
                        put(423, 0);
                        put(735, 1);
                        put(722, 0);
                        put(-733, 0);
                        put(-761, 0);
                        put(653, 1);
                        put(596, 0);
                        put(-482, 2);
                        put(-474, 0);
                        put(-613, 0);
                    }
                }));
        GOOD_TREES.add(new TreeTestInput(new int[]{
                -483,
                                850,
                                -641,
                                -244,
                                -488,
                                266,
                                -835,
                                -635,
                                984,
                                -512,
                                234,
                                -473,
                                -146,
                                -51,
                                -762,
                                -122,
                                280,
                                -613,
                                203,
                                490,
                                -652,
                                -17,
                                -402,
                                451,
                                -234,
                                499,
                                516,
                                -591,
                                698,
                                415,
                                995,
                                638,
                                -674,
                                -671,
                                -576,
                                238,
                                244,
                                -872,
                                -423,
                                1015,
                                -800,
                                560,
                                -789,
                                -896,
                                -775,
                                -79,
                                108,
                                -83,
                                39,
                                -655,
                                295,
                                816,
                                126,
                                756,
                                -460
                 }, 1015, -896,
                new LinkedHashMap<>() {
                    {
                        put(756, true);
                        put(-671, true);
                        put(-674, true);
                        put(691, false);
                        put(-473, true);
                        put(-156, false);
                        put(-655, true);
                        put(415, true);
                        put(332, false);
                        put(-860, false);
                        put(-1003, false);
                        put(770, false);
                        put(424, false);
                        put(-775, true);
                        put(-122, true);
                        put(39, true);
                        put(244, true);
                        put(-166, false);
                        put(-652, true);
                        put(734, false);
                        put(280, true);
                        put(-633, false);
                        put(-635, true);
                    }
                },
                new LinkedHashMap<>() {
                    {
                        put(-483, 0);
                        put(850, 1);
                        put(-641, 1);
                        put(-244, 2);
                        put(-488, 2);
                        put(266, 3);
                        put(-835, 2);
                        put(-635, 3);
                        put(984, 2);
                        put(-512, 4);
                        put(234, 4);
                        put(-473, 3);
                        put(-146, 5);
                        put(-51, 6);
                        put(-762, 3);
                        put(-122, 7);
                        put(280, 4);
                        put(-613, 5);
                        put(203, 7);
                        put(490, 5);
                        put(-652, 4);
                        put(-17, 8);
                        put(-402, 4);
                        put(451, 6);
                        put(-234, 6);
                        put(499, 6);
                        put(516, 7);
                        put(-591, 6);
                        put(698, 8);
                        put(415, 7);
                        put(995, 3);
                        put(638, 9);
                        put(-674, 5);
                        put(-671, 6);
                        put(-576, 7);
                        put(238, 5);
                        put(244, 6);
                        put(-872, 3);
                        put(-423, 5);
                        put(1015, 4);
                        put(-800, 4);
                        put(560, 10);
                        put(-789, 5);
                        put(-896, 4);
                        put(-775, 6);
                        put(-79, 8);
                        put(108, 9);
                        put(-83, 9);
                        put(39, 10);
                        put(-655, 7);
                        put(295, 8);
                        put(816, 9);
                        put(126, 10);
                        put(756, 10);
                        put(-460, 6);
                    }
                },
                new LinkedHashMap<>() {
                    {
                        put(-483, 54);
                        put(850, 35);
                        put(-641, 17);
                        put(-244, 31);
                        put(-488, 5);
                        put(266, 26);
                        put(-835, 10);
                        put(-635, 4);
                        put(984, 2);
                        put(-512, 3);
                        put(234, 13);
                        put(-473, 3);
                        put(-146, 10);
                        put(-51, 8);
                        put(-762, 7);
                        put(-122, 2);
                        put(280, 11);
                        put(-613, 2);
                        put(203, 4);
                        put(490, 10);
                        put(-652, 3);
                        put(-17, 3);
                        put(-402, 2);
                        put(451, 2);
                        put(-234, 0);
                        put(499, 6);
                        put(516, 5);
                        put(-591, 1);
                        put(698, 4);
                        put(415, 1);
                        put(995, 1);
                        put(638, 1);
                        put(-674, 2);
                        put(-671, 1);
                        put(-576, 0);
                        put(238, 1);
                        put(244, 0);
                        put(-872, 1);
                        put(-423, 1);
                        put(1015, 0);
                        put(-800, 2);
                        put(560, 0);
                        put(-789, 1);
                        put(-896, 0);
                        put(-775, 0);
                        put(-79, 1);
                        put(108, 2);
                        put(-83, 0);
                        put(39, 0);
                        put(-655, 0);
                        put(295, 0);
                        put(816, 1);
                        put(126, 0);
                        put(756, 0);
                        put(-460, 0);
                    }
                }));
        GOOD_TREES.add(new TreeTestInput(new int[]{
                -115,
                                455,
                                -573,
                                817,
                                -83,
                                -872,
                                405,
                                594,
                                137,
                                -514,
                                -463,
                                -178,
                                -351,
                                349,
                                124,
                                495,
                                442,
                                -740,
                                954,
                                -232,
                                6,
                                592,
                                -963,
                                -474,
                                582,
                                -674,
                                -288,
                                -234,
                                -818
                 }, 954, -963,
                new LinkedHashMap<>() {
                    {
                        put(-381, false);
                        put(-340, false);
                        put(582, true);
                        put(-740, true);
                        put(-115, true);
                        put(-674, true);
                        put(349, true);
                        put(-474, true);
                        put(137, true);
                        put(39, false);
                        put(-351, true);
                        put(-288, true);
                        put(266, false);
                        put(541, false);
                        put(-477, false);
                        put(6, true);
                        put(495, true);
                        put(153, false);
                        put(492, false);
                        put(442, true);
                        put(-314, false);
                        put(8, false);
                        put(-463, true);
                        put(747, false);
                        put(-382, false);
                    }
                },
                new LinkedHashMap<>() {
                    {
                        put(-115, 0);
                        put(455, 1);
                        put(-573, 1);
                        put(817, 2);
                        put(-83, 2);
                        put(-872, 2);
                        put(405, 3);
                        put(594, 3);
                        put(137, 4);
                        put(-514, 2);
                        put(-463, 3);
                        put(-178, 4);
                        put(-351, 5);
                        put(349, 5);
                        put(124, 5);
                        put(495, 4);
                        put(442, 4);
                        put(-740, 3);
                        put(954, 3);
                        put(-232, 6);
                        put(6, 6);
                        put(592, 5);
                        put(-963, 3);
                        put(-474, 4);
                        put(582, 6);
                        put(-674, 4);
                        put(-288, 7);
                        put(-234, 8);
                        put(-818, 4);
                    }
                },
                new LinkedHashMap<>() {
                    {
                        put(-115, 28);
                        put(455, 13);
                        put(-573, 13);
                        put(817, 5);
                        put(-83, 6);
                        put(-872, 4);
                        put(405, 5);
                        put(594, 3);
                        put(137, 3);
                        put(-514, 7);
                        put(-463, 6);
                        put(-178, 4);
                        put(-351, 3);
                        put(349, 0);
                        put(124, 1);
                        put(495, 2);
                        put(442, 0);
                        put(-740, 2);
                        put(954, 0);
                        put(-232, 2);
                        put(6, 0);
                        put(592, 1);
                        put(-963, 0);
                        put(-474, 0);
                        put(582, 0);
                        put(-674, 0);
                        put(-288, 1);
                        put(-234, 0);
                        put(-818, 0);
                    }
                }));
        GOOD_TREES.add(new TreeTestInput(new int[]{
                -864,
                                123,
                                995,
                                -512,
                                -827,
                                -4,
                                14,
                                813,
                                -162,
                                904,
                                -536,
                                -3,
                                494,
                                -486,
                                -819,
                                83,
                                497,
                                837,
                                128
                 }, 995, -864,
                new LinkedHashMap<>() {
                    {
                        put(-623, false);
                        put(-536, true);
                        put(537, false);
                        put(-819, true);
                        put(-24, false);
                        put(-3, true);
                        put(484, false);
                        put(343, false);
                        put(123, true);
                        put(128, true);
                        put(837, true);
                        put(-486, true);
                        put(597, false);
                        put(705, false);
                        put(833, false);
                        put(83, true);
                        put(27, false);
                        put(522, false);
                    }
                },
                new LinkedHashMap<>() {
                    {
                        put(-864, 0);
                        put(123, 1);
                        put(995, 2);
                        put(-512, 2);
                        put(-827, 3);
                        put(-4, 3);
                        put(14, 4);
                        put(813, 3);
                        put(-162, 4);
                        put(904, 4);
                        put(-536, 4);
                        put(-3, 5);
                        put(494, 4);
                        put(-486, 5);
                        put(-819, 5);
                        put(83, 5);
                        put(497, 5);
                        put(837, 5);
                        put(128, 5);
                    }
                },
                new LinkedHashMap<>() {
                    {
                        put(-864, 18);
                        put(123, 17);
                        put(995, 6);
                        put(-512, 9);
                        put(-827, 2);
                        put(-4, 5);
                        put(14, 2);
                        put(813, 5);
                        put(-162, 1);
                        put(904, 1);
                        put(-536, 1);
                        put(-3, 0);
                        put(494, 2);
                        put(-486, 0);
                        put(-819, 0);
                        put(83, 0);
                        put(497, 0);
                        put(837, 0);
                        put(128, 0);
                    }
                }));
        GOOD_TREES.add(new TreeTestInput(new int[]{
                -807,
                                -260,
                                874,
                                484,
                                -657,
                                -606,
                                -505,
                                204,
                                705,
                                853,
                                -838,
                                444,
                                408,
                                533,
                                -578,
                                -41,
                                609,
                                -729,
                                365,
                                830,
                                302,
                                475,
                                271,
                                99,
                                -483,
                                -567,
                                115
                 }, 874, -838,
                new LinkedHashMap<>() {
                    {
                        put(-505, true);
                        put(-657, true);
                        put(-729, true);
                        put(102, false);
                        put(-260, true);
                        put(115, true);
                        put(875, false);
                        put(740, false);
                        put(734, false);
                        put(830, true);
                        put(275, false);
                        put(874, true);
                        put(-137, false);
                        put(-567, true);
                        put(475, true);
                        put(-606, true);
                        put(463, false);
                        put(101, false);
                        put(250, false);
                        put(444, true);
                        put(997, false);
                        put(206, false);
                        put(853, true);
                        put(-962, false);
                    }
                },
                new LinkedHashMap<>() {
                    {
                        put(-807, 0);
                        put(-260, 1);
                        put(874, 2);
                        put(484, 3);
                        put(-657, 2);
                        put(-606, 3);
                        put(-505, 4);
                        put(204, 4);
                        put(705, 4);
                        put(853, 5);
                        put(-838, 1);
                        put(444, 5);
                        put(408, 6);
                        put(533, 5);
                        put(-578, 5);
                        put(-41, 5);
                        put(609, 6);
                        put(-729, 3);
                        put(365, 7);
                        put(830, 6);
                        put(302, 8);
                        put(475, 6);
                        put(271, 9);
                        put(99, 6);
                        put(-483, 5);
                        put(-567, 6);
                        put(115, 7);
                    }
                },
                new LinkedHashMap<>() {
                    {
                        put(-807, 26);
                        put(-260, 24);
                        put(874, 16);
                        put(484, 15);
                        put(-657, 6);
                        put(-606, 4);
                        put(-505, 3);
                        put(204, 9);
                        put(705, 4);
                        put(853, 1);
                        put(-838, 0);
                        put(444, 5);
                        put(408, 3);
                        put(533, 1);
                        put(-578, 1);
                        put(-41, 2);
                        put(609, 0);
                        put(-729, 0);
                        put(365, 2);
                        put(830, 0);
                        put(302, 1);
                        put(475, 0);
                        put(271, 0);
                        put(99, 1);
                        put(-483, 0);
                        put(-567, 0);
                        put(115, 0);
                    }
                }));
        GOOD_TREES.add(new TreeTestInput(new int[]{
                664,
                                575,
                                -258,
                                -82,
                                640,
                                -40,
                                -75,
                                -384,
                                -27,
                                180,
                                148,
                                1007,
                                506,
                                -590,
                                -853,
                                -991,
                                -767,
                                323,
                                372,
                                10,
                                606,
                                -901,
                                -548,
                                160,
                                -234,
                                -110,
                                -224,
                                735,
                                -1022,
                                79,
                                574,
                                202,
                                950,
                                -689,
                                674,
                                -841,
                                893,
                                -195,
                                1001,
                                -176,
                                -925,
                                -617,
                                355,
                                -476,
                                111,
                                -765,
                                -422,
                                -752,
                                801,
                                487,
                                792,
                                550,
                                -481,
                                589,
                                -78
                 }, 1007, -1022,
                new LinkedHashMap<>() {
                    {
                        put(291, false);
                        put(893, true);
                        put(-722, false);
                        put(-90, false);
                        put(-183, false);
                        put(406, false);
                        put(-176, true);
                        put(-689, true);
                        put(759, false);
                        put(-617, true);
                        put(-239, false);
                        put(-384, true);
                        put(-921, false);
                        put(-110, true);
                        put(-762, false);
                        put(497, false);
                        put(550, true);
                        put(40, false);
                        put(792, true);
                        put(764, false);
                        put(-82, true);
                        put(-69, false);
                    }
                },
                new LinkedHashMap<>() {
                    {
                        put(664, 0);
                        put(575, 1);
                        put(-258, 2);
                        put(-82, 3);
                        put(640, 2);
                        put(-40, 4);
                        put(-75, 5);
                        put(-384, 3);
                        put(-27, 5);
                        put(180, 6);
                        put(148, 7);
                        put(1007, 1);
                        put(506, 7);
                        put(-590, 4);
                        put(-853, 5);
                        put(-991, 6);
                        put(-767, 6);
                        put(323, 8);
                        put(372, 9);
                        put(10, 8);
                        put(606, 3);
                        put(-901, 7);
                        put(-548, 5);
                        put(160, 8);
                        put(-234, 4);
                        put(-110, 5);
                        put(-224, 6);
                        put(735, 2);
                        put(-1022, 7);
                        put(79, 9);
                        put(574, 8);
                        put(202, 9);
                        put(950, 3);
                        put(-689, 7);
                        put(674, 3);
                        put(-841, 7);
                        put(893, 4);
                        put(-195, 7);
                        put(1001, 4);
                        put(-176, 8);
                        put(-925, 8);
                        put(-617, 8);
                        put(355, 10);
                        put(-476, 6);
                        put(111, 10);
                        put(-765, 8);
                        put(-422, 7);
                        put(-752, 9);
                        put(801, 5);
                        put(487, 10);
                        put(792, 6);
                        put(550, 9);
                        put(-481, 7);
                        put(589, 4);
                        put(-78, 6);
                    }
                },
                new LinkedHashMap<>() {
                    {
                        put(664, 54);
                        put(575, 45);
                        put(-258, 41);
                        put(-82, 23);
                        put(640, 2);
                        put(-40, 17);
                        put(-75, 1);
                        put(-384, 16);
                        put(-27, 14);
                        put(180, 13);
                        put(148, 4);
                        put(1007, 7);
                        put(506, 7);
                        put(-590, 15);
                        put(-853, 10);
                        put(-991, 3);
                        put(-767, 5);
                        put(323, 4);
                        put(372, 2);
                        put(10, 2);
                        put(606, 1);
                        put(-901, 1);
                        put(-548, 3);
                        put(160, 0);
                        put(-234, 4);
                        put(-110, 3);
                        put(-224, 2);
                        put(735, 6);
                        put(-1022, 0);
                        put(79, 1);
                        put(574, 1);
                        put(202, 0);
                        put(950, 4);
                        put(-689, 3);
                        put(674, 0);
                        put(-841, 0);
                        put(893, 2);
                        put(-195, 1);
                        put(1001, 0);
                        put(-176, 0);
                        put(-925, 0);
                        put(-617, 0);
                        put(355, 0);
                        put(-476, 2);
                        put(111, 0);
                        put(-765, 1);
                        put(-422, 0);
                        put(-752, 0);
                        put(801, 1);
                        put(487, 0);
                        put(792, 0);
                        put(550, 0);
                        put(-481, 0);
                        put(589, 0);
                        put(-78, 0);
                    }
                }));
        GOOD_TREES.add(new TreeTestInput(new int[]{
                179,
                                -663,
                                437,
                                -719,
                                899,
                                -287,
                                93,
                                834,
                                -763,
                                -315,
                                -139,
                                -355,
                                271,
                                -750,
                                1019,
                                -583,
                                977,
                                370,
                                -677,
                                806,
                                -917,
                                99,
                                -354,
                                880,
                                136,
                                -863,
                                83,
                                301,
                                -690,
                                424,
                                -490,
                                889,
                                180,
                                -621,
                                -226,
                                497,
                                734,
                                702,
                                -246,
                                163,
                                414,
                                -770,
                                -817,
                                603,
                                969,
                                -116,
                                757,
                                -367,
                                290,
                                -377,
                                -584,
                                -479,
                                1006,
                                -460,
                                510,
                                -733,
                                -618,
                                -16,
                                652,
                                736,
                                -491,
                                -929
                 }, 1019, -929,
                new LinkedHashMap<>() {
                    {
                        put(414, true);
                        put(83, true);
                        put(180, true);
                        put(-233, false);
                        put(-750, true);
                        put(-618, true);
                        put(431, false);
                        put(-325, false);
                        put(889, true);
                        put(-754, false);
                        put(-246, true);
                        put(899, true);
                        put(370, true);
                        put(116, false);
                        put(437, true);
                        put(-485, false);
                        put(-1011, false);
                        put(702, true);
                    }
                },
                new LinkedHashMap<>() {
                    {
                        put(179, 0);
                        put(-663, 1);
                        put(437, 1);
                        put(-719, 2);
                        put(899, 2);
                        put(-287, 2);
                        put(93, 3);
                        put(834, 3);
                        put(-763, 3);
                        put(-315, 3);
                        put(-139, 4);
                        put(-355, 4);
                        put(271, 2);
                        put(-750, 4);
                        put(1019, 3);
                        put(-583, 5);
                        put(977, 4);
                        put(370, 3);
                        put(-677, 3);
                        put(806, 4);
                        put(-917, 4);
                        put(99, 4);
                        put(-354, 5);
                        put(880, 4);
                        put(136, 5);
                        put(-863, 5);
                        put(83, 5);
                        put(301, 4);
                        put(-690, 4);
                        put(424, 4);
                        put(-490, 6);
                        put(889, 5);
                        put(180, 3);
                        put(-621, 6);
                        put(-226, 5);
                        put(497, 5);
                        put(734, 6);
                        put(702, 7);
                        put(-246, 6);
                        put(163, 6);
                        put(414, 5);
                        put(-770, 6);
                        put(-817, 7);
                        put(603, 8);
                        put(969, 5);
                        put(-116, 6);
                        put(757, 7);
                        put(-367, 7);
                        put(290, 5);
                        put(-377, 8);
                        put(-584, 7);
                        put(-479, 9);
                        put(1006, 5);
                        put(-460, 10);
                        put(510, 9);
                        put(-733, 5);
                        put(-618, 8);
                        put(-16, 7);
                        put(652, 9);
                        put(736, 8);
                        put(-491, 7);
                        put(-929, 5);
                    }
                },
                new LinkedHashMap<>() {
                    {
                        put(179, 61);
                        put(-663, 35);
                        put(437, 24);
                        put(-719, 10);
                        put(899, 16);
                        put(-287, 23);
                        put(93, 9);
                        put(834, 11);
                        put(-763, 7);
                        put(-315, 12);
                        put(-139, 5);
                        put(-355, 11);
                        put(271, 6);
                        put(-750, 1);
                        put(1019, 3);
                        put(-583, 9);
                        put(977, 2);
                        put(370, 4);
                        put(-677, 1);
                        put(806, 8);
                        put(-917, 4);
                        put(99, 2);
                        put(-354, 0);
                        put(880, 1);
                        put(136, 1);
                        put(-863, 2);
                        put(83, 2);
                        put(301, 1);
                        put(-690, 0);
                        put(424, 1);
                        put(-490, 5);
                        put(889, 0);
                        put(180, 0);
                        put(-621, 2);
                        put(-226, 1);
                        put(497, 7);
                        put(734, 6);
                        put(702, 3);
                        put(-246, 0);
                        put(163, 0);
                        put(414, 0);
                        put(-770, 1);
                        put(-817, 0);
                        put(603, 2);
                        put(969, 0);
                        put(-116, 1);
                        put(757, 1);
                        put(-367, 3);
                        put(290, 0);
                        put(-377, 2);
                        put(-584, 1);
                        put(-479, 1);
                        put(1006, 0);
                        put(-460, 0);
                        put(510, 0);
                        put(-733, 0);
                        put(-618, 0);
                        put(-16, 0);
                        put(652, 0);
                        put(736, 0);
                        put(-491, 0);
                        put(-929, 0);
                    }
                }));
        GOOD_TREES.add(new TreeTestInput(new int[]{
                -833,
                                80,
                                -794,
                                -626,
                                -904,
                                60,
                                -740,
                                738,
                                -401,
                                723,
                                -680,
                                784,
                                100,
                                303,
                                -705,
                                -729,
                                -784,
                                -355,
                                -754,
                                43,
                                510,
                                399,
                                -579,
                                -82,
                                1021,
                                -749,
                                513,
                                -1023,
                                -652
                 }, 1021, -1023,
                new LinkedHashMap<>() {
                    {
                        put(-783, false);
                        put(510, true);
                        put(-729, true);
                        put(-411, false);
                        put(-426, false);
                        put(-813, false);
                        put(-705, true);
                        put(-414, false);
                        put(-1023, true);
                        put(784, true);
                        put(43, true);
                        put(586, false);
                        put(100, true);
                        put(-401, true);
                        put(493, false);
                        put(303, true);
                        put(980, false);
                        put(60, true);
                        put(-833, true);
                        put(-794, true);
                        put(-684, false);
                        put(-781, false);
                    }
                },
                new LinkedHashMap<>() {
                    {
                        put(-833, 0);
                        put(80, 1);
                        put(-794, 2);
                        put(-626, 3);
                        put(-904, 1);
                        put(60, 4);
                        put(-740, 4);
                        put(738, 2);
                        put(-401, 5);
                        put(723, 3);
                        put(-680, 5);
                        put(784, 3);
                        put(100, 4);
                        put(303, 5);
                        put(-705, 6);
                        put(-729, 7);
                        put(-784, 5);
                        put(-355, 6);
                        put(-754, 6);
                        put(43, 7);
                        put(510, 6);
                        put(399, 7);
                        put(-579, 6);
                        put(-82, 8);
                        put(1021, 4);
                        put(-749, 7);
                        put(513, 7);
                        put(-1023, 2);
                        put(-652, 6);
                    }
                },
                new LinkedHashMap<>() {
                    {
                        put(-833, 28);
                        put(80, 25);
                        put(-794, 15);
                        put(-626, 14);
                        put(-904, 1);
                        put(60, 5);
                        put(-740, 7);
                        put(738, 8);
                        put(-401, 4);
                        put(723, 5);
                        put(-680, 3);
                        put(784, 1);
                        put(100, 4);
                        put(303, 3);
                        put(-705, 1);
                        put(-729, 0);
                        put(-784, 2);
                        put(-355, 2);
                        put(-754, 1);
                        put(43, 1);
                        put(510, 2);
                        put(399, 0);
                        put(-579, 0);
                        put(-82, 0);
                        put(1021, 0);
                        put(-749, 0);
                        put(513, 0);
                        put(-1023, 0);
                        put(-652, 0);
                    }
                }));
        GOOD_TREES.add(new TreeTestInput(new int[]{
                -848,
                                289,
                                142,
                                -16,
                                -303,
                                -899,
                                1003,
                                -441,
                                320,
                                -808,
                                -30,
                                859,
                                178,
                                101,
                                -373,
                                307,
                                -362,
                                616,
                                641,
                                709,
                                -570,
                                935,
                                92,
                                187,
                                -940,
                                987,
                                468,
                                -352,
                                -350,
                                -583,
                                466,
                                -5,
                                770,
                                -769,
                                804,
                                510,
                                -63,
                                434,
                                -787,
                                799,
                                -241
                 }, 1003, -940,
                new LinkedHashMap<>() {
                    {
                        put(859, true);
                        put(-352, true);
                        put(24, false);
                        put(674, false);
                        put(178, true);
                        put(516, false);
                        put(-605, false);
                        put(-362, true);
                        put(594, false);
                        put(-315, false);
                        put(908, false);
                        put(466, true);
                        put(935, true);
                        put(-5, true);
                        put(595, false);
                        put(501, false);
                        put(-63, true);
                        put(-603, false);
                        put(-340, false);
                        put(-124, false);
                        put(-373, true);
                        put(230, false);
                        put(-954, false);
                        put(830, false);
                        put(-570, true);
                        put(705, false);
                        put(-614, false);
                        put(-350, true);
                    }
                },
                new LinkedHashMap<>() {
                    {
                        put(-848, 0);
                        put(289, 1);
                        put(142, 2);
                        put(-16, 3);
                        put(-303, 4);
                        put(-899, 1);
                        put(1003, 2);
                        put(-441, 5);
                        put(320, 3);
                        put(-808, 6);
                        put(-30, 5);
                        put(859, 4);
                        put(178, 3);
                        put(101, 4);
                        put(-373, 6);
                        put(307, 4);
                        put(-362, 7);
                        put(616, 5);
                        put(641, 6);
                        put(709, 7);
                        put(-570, 7);
                        put(935, 5);
                        put(92, 5);
                        put(187, 4);
                        put(-940, 2);
                        put(987, 6);
                        put(468, 6);
                        put(-352, 8);
                        put(-350, 9);
                        put(-583, 8);
                        put(466, 7);
                        put(-5, 6);
                        put(770, 8);
                        put(-769, 9);
                        put(804, 9);
                        put(510, 7);
                        put(-63, 6);
                        put(434, 8);
                        put(-787, 10);
                        put(799, 10);
                        put(-241, 7);
                    }
                },
                new LinkedHashMap<>() {
                    {
                        put(-848, 40);
                        put(289, 37);
                        put(142, 20);
                        put(-16, 17);
                        put(-303, 13);
                        put(-899, 1);
                        put(1003, 15);
                        put(-441, 9);
                        put(320, 14);
                        put(-808, 4);
                        put(-30, 2);
                        put(859, 12);
                        put(178, 1);
                        put(101, 2);
                        put(-373, 3);
                        put(307, 0);
                        put(-362, 2);
                        put(616, 9);
                        put(641, 4);
                        put(709, 3);
                        put(-570, 3);
                        put(935, 1);
                        put(92, 1);
                        put(187, 0);
                        put(-940, 0);
                        put(987, 0);
                        put(468, 3);
                        put(-352, 1);
                        put(-350, 0);
                        put(-583, 2);
                        put(466, 1);
                        put(-5, 0);
                        put(770, 2);
                        put(-769, 1);
                        put(804, 1);
                        put(510, 0);
                        put(-63, 1);
                        put(434, 0);
                        put(-787, 0);
                        put(799, 0);
                        put(-241, 0);
                    }
                }));
        GOOD_TREES.add(new TreeTestInput(new int[]{
                -982,
                                494,
                                -615,
                                585,
                                319,
                                184,
                                831,
                                -74,
                                116,
                                512,
                                -867,
                                -916,
                                347,
                                -512,
                                413,
                                896,
                                359,
                                455,
                                -809,
                                -389,
                                -275,
                                248,
                                964,
                                280,
                                803,
                                57,
                                -595,
                                245,
                                -420,
                                451,
                                603
                 }, 964, -982,
                new LinkedHashMap<>() {
                    {
                        put(896, true);
                        put(-934, false);
                        put(603, true);
                        put(-981, false);
                        put(831, true);
                        put(152, false);
                        put(319, true);
                        put(560, false);
                        put(-809, true);
                        put(266, false);
                        put(642, false);
                        put(359, true);
                        put(130, false);
                        put(455, true);
                        put(-853, false);
                        put(413, true);
                        put(-74, true);
                        put(57, true);
                    }
                },
                new LinkedHashMap<>() {
                    {
                        put(-982, 0);
                        put(494, 1);
                        put(-615, 2);
                        put(585, 2);
                        put(319, 3);
                        put(184, 4);
                        put(831, 3);
                        put(-74, 5);
                        put(116, 6);
                        put(512, 3);
                        put(-867, 3);
                        put(-916, 4);
                        put(347, 4);
                        put(-512, 6);
                        put(413, 5);
                        put(896, 4);
                        put(359, 6);
                        put(455, 6);
                        put(-809, 4);
                        put(-389, 7);
                        put(-275, 8);
                        put(248, 5);
                        put(964, 5);
                        put(280, 6);
                        put(803, 4);
                        put(57, 7);
                        put(-595, 7);
                        put(245, 6);
                        put(-420, 8);
                        put(451, 7);
                        put(603, 5);
                    }
                },
                new LinkedHashMap<>() {
                    {
                        put(-982, 30);
                        put(494, 29);
                        put(-615, 21);
                        put(585, 6);
                        put(319, 17);
                        put(184, 11);
                        put(831, 4);
                        put(-74, 7);
                        put(116, 1);
                        put(512, 0);
                        put(-867, 2);
                        put(-916, 0);
                        put(347, 4);
                        put(-512, 4);
                        put(413, 3);
                        put(896, 1);
                        put(359, 0);
                        put(455, 1);
                        put(-809, 0);
                        put(-389, 2);
                        put(-275, 0);
                        put(248, 2);
                        put(964, 0);
                        put(280, 0);
                        put(803, 1);
                        put(57, 0);
                        put(-595, 0);
                        put(245, 0);
                        put(-420, 0);
                        put(451, 0);
                        put(603, 0);
                    }
                }));
        GOOD_TREES.add(new TreeTestInput(new int[]{
                764,
                                889,
                                35,
                                620,
                                -115,
                                -814,
                                -175,
                                832,
                                840,
                                471,
                                737,
                                760,
                                -870,
                                252,
                                -287,
                                -266,
                                626,
                                -433,
                                796,
                                766,
                                -567,
                                321,
                                -774,
                                141,
                                -71,
                                -539,
                                -770,
                                -375,
                                517
                 }, 889, -870,
                new LinkedHashMap<>() {
                    {
                        put(-515, false);
                        put(-116, false);
                        put(-282, false);
                        put(913, false);
                        put(141, true);
                        put(737, true);
                        put(-115, true);
                        put(-814, true);
                        put(517, true);
                        put(650, false);
                        put(-744, false);
                        put(-1010, false);
                        put(506, false);
                        put(626, true);
                        put(295, false);
                        put(840, true);
                        put(-71, true);
                        put(-287, true);
                        put(692, false);
                        put(116, false);
                        put(-516, false);
                        put(471, true);
                        put(-244, false);
                        put(-346, false);
                        put(832, true);
                        put(321, true);
                        put(-774, true);
                    }
                },
                new LinkedHashMap<>() {
                    {
                        put(764, 0);
                        put(889, 1);
                        put(35, 1);
                        put(620, 2);
                        put(-115, 2);
                        put(-814, 3);
                        put(-175, 4);
                        put(832, 2);
                        put(840, 3);
                        put(471, 3);
                        put(737, 3);
                        put(760, 4);
                        put(-870, 4);
                        put(252, 4);
                        put(-287, 5);
                        put(-266, 6);
                        put(626, 4);
                        put(-433, 6);
                        put(796, 3);
                        put(766, 4);
                        put(-567, 7);
                        put(321, 5);
                        put(-774, 8);
                        put(141, 5);
                        put(-71, 3);
                        put(-539, 8);
                        put(-770, 9);
                        put(-375, 7);
                        put(517, 4);
                    }
                },
                new LinkedHashMap<>() {
                    {
                        put(764, 28);
                        put(889, 4);
                        put(35, 22);
                        put(620, 8);
                        put(-115, 12);
                        put(-814, 10);
                        put(-175, 8);
                        put(832, 3);
                        put(840, 0);
                        put(471, 4);
                        put(737, 2);
                        put(760, 0);
                        put(-870, 0);
                        put(252, 2);
                        put(-287, 7);
                        put(-266, 0);
                        put(626, 0);
                        put(-433, 5);
                        put(796, 1);
                        put(766, 0);
                        put(-567, 3);
                        put(321, 0);
                        put(-774, 1);
                        put(141, 0);
                        put(-71, 0);
                        put(-539, 0);
                        put(-770, 0);
                        put(-375, 0);
                        put(517, 0);
                    }
                }));
        GOOD_TREES.add(new TreeTestInput(new int[]{
                -910,
                                -292,
                                -77,
                                685,
                                528,
                                589,
                                56,
                                662,
                                707,
                                60,
                                -469,
                                -590,
                                769,
                                -173,
                                -191,
                                972,
                                89,
                                -447,
                                776,
                                -520,
                                -187,
                                -161,
                                819,
                                -1005,
                                -384,
                                -829,
                                -400,
                                68,
                                -976,
                                8,
                                -467,
                                -602,
                                361,
                                -540,
                                274,
                                -261,
                                743,
                                -786,
                                -509,
                                830,
                                -10,
                                438,
                                829,
                                -270
                 }, 972, -1005,
                new LinkedHashMap<>() {
                    {
                        put(972, true);
                        put(60, true);
                        put(-602, true);
                        put(-563, false);
                        put(-976, true);
                        put(730, false);
                        put(830, true);
                        put(-1005, true);
                        put(365, false);
                        put(438, true);
                        put(-292, true);
                        put(-843, false);
                        put(-384, true);
                        put(-786, true);
                        put(-261, true);
                        put(-32, false);
                        put(947, false);
                        put(-930, false);
                        put(-874, false);
                        put(-755, false);
                        put(274, true);
                    }
                },
                new LinkedHashMap<>() {
                    {
                        put(-910, 0);
                        put(-292, 1);
                        put(-77, 2);
                        put(685, 3);
                        put(528, 4);
                        put(589, 5);
                        put(56, 5);
                        put(662, 6);
                        put(707, 4);
                        put(60, 6);
                        put(-469, 2);
                        put(-590, 3);
                        put(769, 5);
                        put(-173, 3);
                        put(-191, 4);
                        put(972, 6);
                        put(89, 7);
                        put(-447, 3);
                        put(776, 7);
                        put(-520, 4);
                        put(-187, 5);
                        put(-161, 4);
                        put(819, 8);
                        put(-1005, 1);
                        put(-384, 4);
                        put(-829, 4);
                        put(-400, 5);
                        put(68, 8);
                        put(-976, 2);
                        put(8, 6);
                        put(-467, 4);
                        put(-602, 5);
                        put(361, 8);
                        put(-540, 5);
                        put(274, 9);
                        put(-261, 5);
                        put(743, 6);
                        put(-786, 6);
                        put(-509, 5);
                        put(830, 9);
                        put(-10, 7);
                        put(438, 9);
                        put(829, 10);
                        put(-270, 6);
                    }
                },
                new LinkedHashMap<>() {
                    {
                        put(-910, 43);
                        put(-292, 40);
                        put(-77, 27);
                        put(685, 20);
                        put(528, 11);
                        put(589, 1);
                        put(56, 8);
                        put(662, 0);
                        put(707, 7);
                        put(60, 5);
                        put(-469, 11);
                        put(-590, 6);
                        put(769, 6);
                        put(-173, 5);
                        put(-191, 3);
                        put(972, 4);
                        put(89, 4);
                        put(-447, 3);
                        put(776, 3);
                        put(-520, 2);
                        put(-187, 0);
                        put(-161, 0);
                        put(819, 2);
                        put(-1005, 1);
                        put(-384, 1);
                        put(-829, 2);
                        put(-400, 0);
                        put(68, 0);
                        put(-976, 0);
                        put(8, 1);
                        put(-467, 0);
                        put(-602, 1);
                        put(361, 2);
                        put(-540, 0);
                        put(274, 0);
                        put(-261, 1);
                        put(743, 0);
                        put(-786, 0);
                        put(-509, 0);
                        put(830, 1);
                        put(-10, 0);
                        put(438, 0);
                        put(829, 0);
                        put(-270, 0);
                    }
                }));
        /* END AUTOGENERATED CODE */

        BAD_TREES.add(new TreeTestInput(new int[]{1, 1, 3}));
        /* BEGIN AUTOGENERATED CODE */
        BAD_TREES.add(new TreeTestInput(new int[]{
            78,
            -662,
            592,
            348,
            906,
            -330,
            919,
            -678,
            905,
            443,
            200,
            -646,
            -925,
            -852,
            754,
            -513,
            -782,
            332,
            -812,
            -708,
            -666,
            13,
            965,
            -760,
            946,
            969,
            220,
            -890,
            -878,
            948,
            82,
            -563,
            -261,
            924,
            475,
            182,
            946,
            -790,
            870,
            304,
            406
        }));
        BAD_TREES.add(new TreeTestInput(new int[]{
            156,
            -360,
            972,
            730,
            -280,
            -815,
            113,
            64,
            373,
            533,
            808,
            -806,
            471,
            666,
            -349,
            814,
            486,
            -799,
            -649,
            214,
            -410,
            -953,
            -739,
            308,
            990,
            -223,
            -363,
            -372,
            -188,
            -404,
            -267,
            -8,
            -808,
            -58,
            -808,
            868,
            -640,
            -196,
            44
        }));
        BAD_TREES.add(new TreeTestInput(new int[]{
            195,
            561,
            -635,
            -800,
            742,
            -965,
            561,
            62,
            518,
            870,
            770,
            154,
            360,
            562,
            741,
            53,
            -488,
            -464,
            -266,
            486,
            -846,
            -282,
            -346,
            646,
            74,
            363,
            -255,
            516,
            -653,
            415,
            -862,
            -289,
            -165,
            -789,
            -892,
            293,
            -1022,
            947,
            391,
            743,
            485,
            -945,
            -477,
            -600,
            7,
            -194,
            -542,
            -262,
            -9,
            422,
            -31,
            323,
            -268
        }));
        BAD_TREES.add(new TreeTestInput(new int[]{
            -932,
            602,
            -605,
            -69,
            72,
            353,
            -936,
            -962,
            -914,
            325,
            57,
            -256,
            -1013,
            278,
            50,
            -207,
            -4,
            -981,
            -631,
            -680,
            830,
            95,
            39,
            -226,
            -952,
            228,
            614,
            -364,
            532,
            462,
            423,
            -722,
            -183,
            234,
            -574,
            845,
            64,
            -521,
            -323,
            504,
            466,
            -798,
            -596,
            -873,
            -69,
            877,
            -868,
            670,
            -595
        }));
        BAD_TREES.add(new TreeTestInput(new int[]{
            -332,
            803,
            993,
            604,
            412,
            499,
            27,
            433,
            -439,
            935,
            -620,
            893,
            -224,
            -543,
            -492,
            -930,
            844,
            -232,
            463,
            72,
            -447,
            337,
            764,
            407,
            -942,
            460,
            -794,
            -281,
            -846,
            -427,
            329,
            443,
            -812,
            134,
            35,
            -349,
            -675,
            -843,
            -506,
            377,
            786,
            -747,
            180,
            732,
            104,
            -722,
            -812,
            -607,
            911,
            47,
            -340,
            265,
            100,
            -342
        }));
        /* END AUTOGENERATED CODE */
    }
}
