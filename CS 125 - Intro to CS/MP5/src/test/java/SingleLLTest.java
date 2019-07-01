import java.util.*;

import org.testng.annotations.Test;
import junit.framework.Assert;

/**
 * Test suite for the SingleLL class.
 * <p>
 * The provided test suite is correct and complete. You should not need to modify it. However, you
 * should understand it.
 *
 * @see <a href="https://cs125.cs.illinois.edu/MP/5/">MP5 Documentation</a>
 */
public class SingleLLTest {

    /** Timeout for LinkedList tests. Solution takes 109 ms. */
    private static final int LINKEDLIST_TEST_TIMEOUT = 1090;

    private SingleLL listFromArray(final int[] array) {
        SingleLL newList = new SingleLL();
        for (int i = array.length - 1; i >= 0; i--) {
            newList.unshift(array[i]);
        }
        return newList;
    }

    private boolean compareListAndArray(final int[] array, final SingleLL list) {
        SingleLL.Node current = list.getStart();
        for (int anArray : array) {
            if (current == null || current.getValue() != anArray) {
                return false;
            }
            current = current.getNext();
        }
        return current == null;
    }

    private static final int LIST_CREATION_TEST_COUNT = 1024;
    private static final int LIST_CREATION_TEST_MIN_SIZE = 32;
    private static final int LIST_CREATION_TEST_MAX_SIZE = 512;
    /**
     * Sanity check to make sure that basic list methods the test suite relies on haven't broken.
     */
    @Test(timeOut = LINKEDLIST_TEST_TIMEOUT, groups = "sanity")
    public void testListCreation() {
        Random randomGenerator = new Random();
        for (int i = 0; i < LIST_CREATION_TEST_COUNT; i++) {
            int size = randomGenerator.nextInt(LIST_CREATION_TEST_MAX_SIZE -
                    LIST_CREATION_TEST_MIN_SIZE) + LIST_CREATION_TEST_MIN_SIZE;
            int[] array = new int[size];
            SingleLL list = listFromArray(array);
            Assert.assertTrue(compareListAndArray(array, list));
        }
    }

    @Test(timeOut = LINKEDLIST_TEST_TIMEOUT, dependsOnGroups = "sanity")
    public void testListInsert() {
        for (InsertTestInput currentTest : INSERTION_TEST_CASES) {
            SingleLL list = listFromArray(currentTest.before);
            Assert.assertTrue(compareListAndArray(currentTest.before, list));
            Assert.assertEquals(currentTest.result, list.insert(currentTest.value, currentTest.location));
            Assert.assertTrue(Arrays.toString(currentTest.after) + " != " + list,
                    compareListAndArray(currentTest.after, list));
        }
    }

    @Test(timeOut = LINKEDLIST_TEST_TIMEOUT, dependsOnGroups = "sanity")
    public void testListRemove() {
        for (RemoveTestInput currentTest : REMOVAL_TEST_CASES) {
            SingleLL list = listFromArray(currentTest.before);
            Assert.assertTrue(compareListAndArray(currentTest.before, list));
            Assert.assertEquals(currentTest.result, list.remove(currentTest.location));
            Assert.assertTrue(Arrays.toString(currentTest.after) + " != " + list,
                    compareListAndArray(currentTest.after, list));
        }
    }

    @Test(timeOut = LINKEDLIST_TEST_TIMEOUT, dependsOnGroups = "sanity")
    public void testListSwaps() {
        for (SwapTestInput currentTest : SWAP_TEST_CASES) {
            SingleLL list = listFromArray(currentTest.before);
            Assert.assertTrue(compareListAndArray(currentTest.before, list));
            Assert.assertEquals(currentTest.result,
                    list.swap(currentTest.firstLocation, currentTest.secondLocation));
            Assert.assertTrue(Arrays.toString(currentTest.after) + " != " + list,
                    compareListAndArray(currentTest.after, list));
        }
    }

    /**
     * Class for testing list insertions.
     */
    public static class InsertTestInput {
        /** Array before the insertion. */
        int[] before;

        /** Integer value to insert. */
        int value;

        /** Location to insert the value. */
        int location;

        /** Whether the insert succeeded or not. */
        boolean result;

        /** Array after the insertion. */
        int[] after;

        /**
         * Full constructor for insertion testing inputs.
         *
         * @param setBefore array before the insertion
         * @param setValue int to insert
         * @param setLocation location to insert the new value
         * @param setResult whether the insertion succeeded or not
         * @param setAfter array after the insertion
         */
        InsertTestInput(int[] setBefore, int setValue, int setLocation,
                               boolean setResult, int[] setAfter) {
            this.before = setBefore;
            this.value = setValue;
            this.location = setLocation;
            this.result = setResult;
            this.after = setAfter;
        }
    }

    /**
     * Class for testing list removals.
     */
    public static class RemoveTestInput {
        /** Array before the insertion. */
        int[] before;

        /** Location to remove. */
        int location;

        /** Whether the removal succeeded or not. */
        boolean result;

        /** Array after the removal. */
        int[] after;

        /**
         * Full constructor for removal testing inputs.
         *
         * @param setBefore array before the removal
         * @param setLocation location to remove
         * @param setResult whether the removal succeeded or not
         * @param setAfter array after the removal
         */
        RemoveTestInput(int[] setBefore, int setLocation,
                               boolean setResult, int[] setAfter) {
            this.before = setBefore;
            this.location = setLocation;
            this.result = setResult;
            this.after = setAfter;
        }
    }

    /**
     * Class for testing list swaps.
     */
    public static class SwapTestInput {
        /** Array before the insertion. */
        int[] before;

        /** First location to swap */
        int firstLocation;

        /** Second location to swap */
        int secondLocation;

        /** Whether the removal succeeded or not. */
        boolean result;

        /** Array after the removal. */
        int[] after;

        /**
         * Full constructor for swap testing inputs.
         *
         * @param setBefore array before the swap
         * @param setFirstLocation first location to swap
         * @param setSecondLocation second location to swap
         * @param setResult whether the swap succeeded or not
         * @param setAfter array after the swap
         */
        SwapTestInput(int[] setBefore, int setFirstLocation, int setSecondLocation,
                               boolean setResult, int[] setAfter) {
            this.before = setBefore;
            this.firstLocation = setFirstLocation;
            this.secondLocation = setSecondLocation;
            this.result = setResult;
            this.after = setAfter;
        }
    }

    private static final List<InsertTestInput> INSERTION_TEST_CASES =
            new ArrayList<>() {
                {
                    add(new InsertTestInput(new int[] { }, 0, 0,
                            true, new int[] { 0 }));
                    add(new InsertTestInput(new int[] { 1 }, 0, 0,
                            true, new int[] { 0, 1 }));
                    add(new InsertTestInput(new int[] { 1 }, 0, 2,
                            false, new int[] { 1 }));
                    add(new InsertTestInput(new int[] { 0, 1 }, 0, -1,
                            false, new int[] { 0, 1 }));
                    /* BEGIN AUTOGENERATED CODE */
                    add(new InsertTestInput(new int[] {
                    -250,223,487,-910,888,84,489,279,330,-931,-937,455,958,922,39,590,-507,926,-629,857,960,
                    }, -670, 14, true, new int[] {
                    -250,223,487,-910,888,84,489,279,330,-931,-937,455,958,922,-670,39,590,-507,926,-629,857,960
                    }));
                    add(new InsertTestInput(new int[] {
                    -208,-594,-251,-847,792,-408,546,520,976,813,-167,269,-637,21,376,291,-822,-204,868,604,36,-411,395,998,-734,86,340,
                    }, -446, 35, false, new int[] {
                    -208,-594,-251,-847,792,-408,546,520,976,813,-167,269,-637,21,376,291,-822,-204,868,604,36,-411,395,998,-734,86,340
                    }));
                    add(new InsertTestInput(new int[] {
                    53,-227,619,314,-572,425,-880,200,759,
                    }, 858, 6, true, new int[] {
                    53,-227,619,314,-572,425,858,-880,200,759
                    }));
                    add(new InsertTestInput(new int[] {
                    -543,80,180,712,-764,614,764,-9,66,-798,634,-31,-128,50,870,-451,-829,-34,991,-218,940,
                    }, -709, 24, false, new int[] {
                    -543,80,180,712,-764,614,764,-9,66,-798,634,-31,-128,50,870,-451,-829,-34,991,-218,940
                    }));
                    add(new InsertTestInput(new int[] {
                    -55,19,331,100,-114,254,118,801,-858,-956,-925,-457,-523,-659,86,-933,1016,-467,-544,-119,-284,165,528,922,168,-122,200,571,-122,-287,
                    }, -352, 22, true, new int[] {
                    -55,19,331,100,-114,254,118,801,-858,-956,-925,-457,-523,-659,86,-933,1016,-467,-544,-119,-284,165,-352,528,922,168,-122,200,571,-122,-287
                    }));
                    add(new InsertTestInput(new int[] {
                    -56,-682,529,976,-163,-470,-773,526,-483,526,-426,-421,283,-707,-910,-206,-925,538,
                    }, -333, 23, false, new int[] {
                    -56,-682,529,976,-163,-470,-773,526,-483,526,-426,-421,283,-707,-910,-206,-925,538
                    }));
                    add(new InsertTestInput(new int[] {
                    -765,263,649,500,-999,-382,241,-593,-591,-82,-342,-1009,-694,665,121,-70,-1011,-270,
                    }, 945, 23, false, new int[] {
                    -765,263,649,500,-999,-382,241,-593,-591,-82,-342,-1009,-694,665,121,-70,-1011,-270
                    }));
                    add(new InsertTestInput(new int[] {
                    -991,-197,437,897,-623,987,-986,-457,-407,-606,
                    }, -785, 12, false, new int[] {
                    -991,-197,437,897,-623,987,-986,-457,-407,-606
                    }));
                    add(new InsertTestInput(new int[] {
                    -796,-348,409,987,-690,958,946,941,690,599,-811,-515,996,379,275,-37,-470,385,397,-902,-183,80,-829,178,39,-479,-623,-865,
                    }, -526, 14, true, new int[] {
                    -796,-348,409,987,-690,958,946,941,690,599,-811,-515,996,379,-526,275,-37,-470,385,397,-902,-183,80,-829,178,39,-479,-623,-865
                    }));
                    add(new InsertTestInput(new int[] {
                    -74,132,246,-381,291,-148,-137,-602,-722,382,325,-649,-583,76,147,-58,830,-431,-577,-457,285,-574,180,607,-834,-928,664,-279,728,304,
                    }, 624, 16, true, new int[] {
                    -74,132,246,-381,291,-148,-137,-602,-722,382,325,-649,-583,76,147,-58,624,830,-431,-577,-457,285,-574,180,607,-834,-928,664,-279,728,304
                    }));
                    add(new InsertTestInput(new int[] {
                    -741,-405,-614,315,-419,-46,-182,709,-584,948,-395,331,934,607,-284,802,-990,126,-804,-954,243,-617,-57,-43,-869,-651,546,-545,-971,879,
                    }, -371, 28, true, new int[] {
                    -741,-405,-614,315,-419,-46,-182,709,-584,948,-395,331,934,607,-284,802,-990,126,-804,-954,243,-617,-57,-43,-869,-651,546,-545,-371,-971,879
                    }));
                    add(new InsertTestInput(new int[] {
                    -600,115,118,-150,487,341,-178,741,725,949,-948,567,-835,-849,657,-266,648,62,
                    }, 677, 17, true, new int[] {
                    -600,115,118,-150,487,341,-178,741,725,949,-948,567,-835,-849,657,-266,648,677,62
                    }));
                    add(new InsertTestInput(new int[] {
                    549,-954,-100,-499,996,-93,-909,911,-593,881,380,-580,-951,796,921,584,-439,-323,-148,-287,115,-661,540,-763,-561,
                    }, -680, 21, true, new int[] {
                    549,-954,-100,-499,996,-93,-909,911,-593,881,380,-580,-951,796,921,584,-439,-323,-148,-287,115,-680,-661,540,-763,-561
                    }));
                    add(new InsertTestInput(new int[] {
                    -189,-655,973,803,349,787,-548,602,-729,-526,381,-817,467,44,819,-246,-610,
                    }, -529, 21, false, new int[] {
                    -189,-655,973,803,349,787,-548,602,-729,-526,381,-817,467,44,819,-246,-610
                    }));
                    add(new InsertTestInput(new int[] {
                    244,-251,-896,-385,-763,811,967,-585,-281,-202,-464,-879,858,-222,334,-547,781,
                    }, -829, 10, true, new int[] {
                    244,-251,-896,-385,-763,811,967,-585,-281,-202,-829,-464,-879,858,-222,334,-547,781
                    }));
                    add(new InsertTestInput(new int[] {
                    -835,346,979,-171,-574,140,48,57,532,-509,810,937,-323,
                    }, 297, 12, true, new int[] {
                    -835,346,979,-171,-574,140,48,57,532,-509,810,937,297,-323
                    }));
                    add(new InsertTestInput(new int[] {
                    684,-463,496,800,796,-55,22,381,845,64,191,586,
                    }, 597, 6, true, new int[] {
                    684,-463,496,800,796,-55,597,22,381,845,64,191,586
                    }));
                    add(new InsertTestInput(new int[] {
                    986,-952,768,-139,318,218,-851,628,411,-784,688,-254,-1013,-959,-980,131,-97,-345,-181,-55,709,940,-323,226,-213,-607,-476,-223,801,-898,-1012,-590,
                    }, 383, 43, false, new int[] {
                    986,-952,768,-139,318,218,-851,628,411,-784,688,-254,-1013,-959,-980,131,-97,-345,-181,-55,709,940,-323,226,-213,-607,-476,-223,801,-898,-1012,-590
                    }));
                    add(new InsertTestInput(new int[] {
                    215,-875,518,-751,-924,-118,351,628,215,-910,-87,-212,-132,-256,-554,124,-19,-571,-583,-699,-829,-527,-944,-652,256,672,952,606,-518,-370,
                    }, -367, 29, true, new int[] {
                    215,-875,518,-751,-924,-118,351,628,215,-910,-87,-212,-132,-256,-554,124,-19,-571,-583,-699,-829,-527,-944,-652,256,672,952,606,-518,-367,-370
                    }));
                    add(new InsertTestInput(new int[] {
                    -551,754,-288,-477,98,151,-47,-432,331,-251,-537,-623,-360,-130,-571,-738,-602,772,927,573,918,-61,359,162,-632,319,-375,
                    }, 104, 32, false, new int[] {
                    -551,754,-288,-477,98,151,-47,-432,331,-251,-537,-623,-360,-130,-571,-738,-602,772,927,573,918,-61,359,162,-632,319,-375
                    }));
                    add(new InsertTestInput(new int[] {
                    -657,904,-995,-261,801,-738,-366,-613,-870,609,-585,411,-940,-668,-857,317,
                    }, 583, 19, false, new int[] {
                    -657,904,-995,-261,801,-738,-366,-613,-870,609,-585,411,-940,-668,-857,317
                    }));
                    add(new InsertTestInput(new int[] {
                    -955,611,922,6,-17,-258,202,526,-549,545,-257,265,-1000,-140,-928,630,-964,-138,-286,
                    }, 742, 23, false, new int[] {
                    -955,611,922,6,-17,-258,202,526,-549,545,-257,265,-1000,-140,-928,630,-964,-138,-286
                    }));
                    add(new InsertTestInput(new int[] {
                    -883,-384,579,493,209,159,835,862,947,775,939,-1015,947,-24,-744,-428,-297,687,281,202,-276,355,-631,-453,-493,-683,676,138,
                    }, -469, 16, true, new int[] {
                    -883,-384,579,493,209,159,835,862,947,775,939,-1015,947,-24,-744,-428,-469,-297,687,281,202,-276,355,-631,-453,-493,-683,676,138
                    }));
                    add(new InsertTestInput(new int[] {
                    700,-420,-456,334,748,259,478,697,772,160,374,343,-475,-1002,-453,673,-299,802,687,-278,-736,-667,-219,626,-736,-1007,-556,1003,-719,393,
                    }, -846, 25, true, new int[] {
                    700,-420,-456,334,748,259,478,697,772,160,374,343,-475,-1002,-453,673,-299,802,687,-278,-736,-667,-219,626,-736,-846,-1007,-556,1003,-719,393
                    }));
                    add(new InsertTestInput(new int[] {
                    571,-331,-430,211,814,579,-976,902,922,527,
                    }, -840, 13, false, new int[] {
                    571,-331,-430,211,814,579,-976,902,922,527
                    }));
                    add(new InsertTestInput(new int[] {
                    -63,432,190,486,-263,-50,411,-992,273,-793,-898,835,
                    }, 542, 11, true, new int[] {
                    -63,432,190,486,-263,-50,411,-992,273,-793,-898,542,835
                    }));
                    add(new InsertTestInput(new int[] {
                    633,17,645,-712,-660,-932,-159,141,-981,-68,-285,-634,289,921,-128,115,536,-600,-870,
                    }, 1002, 16, true, new int[] {
                    633,17,645,-712,-660,-932,-159,141,-981,-68,-285,-634,289,921,-128,115,1002,536,-600,-870
                    }));
                    add(new InsertTestInput(new int[] {
                    615,-973,23,835,-247,-688,653,571,-656,752,-49,146,760,150,495,662,-437,-314,983,-6,527,-730,-578,422,965,-873,-777,-393,-316,-594,-677,
                    }, -782, 43, false, new int[] {
                    615,-973,23,835,-247,-688,653,571,-656,752,-49,146,760,150,495,662,-437,-314,983,-6,527,-730,-578,422,965,-873,-777,-393,-316,-594,-677
                    }));
                    add(new InsertTestInput(new int[] {
                    83,-628,-136,967,68,-523,-860,-104,620,-168,-727,894,866,790,553,809,-152,693,-773,119,408,77,193,-918,-409,961,
                    }, -528, 34, false, new int[] {
                    83,-628,-136,967,68,-523,-860,-104,620,-168,-727,894,866,790,553,809,-152,693,-773,119,408,77,193,-918,-409,961
                    }));
                    add(new InsertTestInput(new int[] {
                    99,-673,747,-621,173,477,803,-838,-121,-322,-516,-639,314,342,-566,-826,924,-682,709,373,-603,-948,1,-114,465,-1017,966,
                    }, 597, 17, true, new int[] {
                    99,-673,747,-621,173,477,803,-838,-121,-322,-516,-639,314,342,-566,-826,924,597,-682,709,373,-603,-948,1,-114,465,-1017,966
                    }));
                    add(new InsertTestInput(new int[] {
                    685,-912,-336,-288,-162,346,339,535,
                    }, 534, 10, false, new int[] {
                    685,-912,-336,-288,-162,346,339,535
                    }));
                    add(new InsertTestInput(new int[] {
                    -579,-559,157,-83,558,-233,507,-47,369,606,
                    }, -13, 14, false, new int[] {
                    -579,-559,157,-83,558,-233,507,-47,369,606
                    }));
                    add(new InsertTestInput(new int[] {
                    -27,514,448,949,-446,937,502,-491,-302,-6,939,647,
                    }, 637, 9, true, new int[] {
                    -27,514,448,949,-446,937,502,-491,-302,637,-6,939,647
                    }));
                    add(new InsertTestInput(new int[] {
                    311,93,-341,966,-145,-611,590,789,-99,907,-208,-132,-274,-990,362,492,1020,-1024,192,-670,
                    }, 894, 24, false, new int[] {
                    311,93,-341,966,-145,-611,590,789,-99,907,-208,-132,-274,-990,362,492,1020,-1024,192,-670
                    }));
                    add(new InsertTestInput(new int[] {
                    137,37,-485,205,380,-10,85,-149,-712,-569,-828,-54,615,-39,-40,381,-711,766,-961,-460,968,-856,294,-7,-719,214,165,975,-763,
                    }, -169, 29, true, new int[] {
                    137,37,-485,205,380,-10,85,-149,-712,-569,-828,-54,615,-39,-40,381,-711,766,-961,-460,968,-856,294,-7,-719,214,165,975,-763,-169
                    }));
                    add(new InsertTestInput(new int[] {
                    384,-986,-285,73,-613,-735,-832,-563,695,-204,899,849,-996,-448,-843,-361,-909,
                    }, -640, 9, true, new int[] {
                    384,-986,-285,73,-613,-735,-832,-563,695,-640,-204,899,849,-996,-448,-843,-361,-909
                    }));
                    add(new InsertTestInput(new int[] {
                    306,229,166,488,-787,576,595,-448,
                    }, -478, 10, false, new int[] {
                    306,229,166,488,-787,576,595,-448
                    }));
                    add(new InsertTestInput(new int[] {
                    -663,-650,898,-846,-773,180,890,-404,-942,-233,737,485,-637,-949,
                    }, 307, 13, true, new int[] {
                    -663,-650,898,-846,-773,180,890,-404,-942,-233,737,485,-637,307,-949
                    }));
                    add(new InsertTestInput(new int[] {
                    979,356,-683,811,750,591,-952,552,678,-632,294,463,-165,134,575,-195,-518,254,601,-585,364,1020,-758,
                    }, 769, 33, false, new int[] {
                    979,356,-683,811,750,591,-952,552,678,-632,294,463,-165,134,575,-195,-518,254,601,-585,364,1020,-758
                    }));
                    add(new InsertTestInput(new int[] {
                    888,-677,53,-280,-647,881,894,584,-75,
                    }, -743, 10, false, new int[] {
                    888,-677,53,-280,-647,881,894,584,-75
                    }));
                    add(new InsertTestInput(new int[] {
                    448,115,506,216,727,627,-789,-979,-472,315,445,246,-905,-121,-588,-828,747,476,-325,274,293,-165,834,233,771,612,-992,312,168,285,582,
                    }, -911, 16, true, new int[] {
                    448,115,506,216,727,627,-789,-979,-472,315,445,246,-905,-121,-588,-828,-911,747,476,-325,274,293,-165,834,233,771,612,-992,312,168,285,582
                    }));
                    add(new InsertTestInput(new int[] {
                    -602,577,459,-208,327,-631,-418,-339,-727,-225,780,-688,471,889,-64,531,855,-687,158,-900,853,70,716,132,
                    }, 453, 17, true, new int[] {
                    -602,577,459,-208,327,-631,-418,-339,-727,-225,780,-688,471,889,-64,531,855,453,-687,158,-900,853,70,716,132
                    }));
                    add(new InsertTestInput(new int[] {
                    -768,170,61,791,729,965,-513,692,-911,235,100,796,942,-731,870,241,-713,993,-441,246,89,
                    }, 837, 28, false, new int[] {
                    -768,170,61,791,729,965,-513,692,-911,235,100,796,942,-731,870,241,-713,993,-441,246,89
                    }));
                    add(new InsertTestInput(new int[] {
                    -901,553,387,-58,-44,-550,-250,967,199,-145,-801,-478,85,1001,-634,555,519,470,-716,-473,-778,-864,641,-966,-542,425,-654,801,405,-8,
                    }, -421, 18, true, new int[] {
                    -901,553,387,-58,-44,-550,-250,967,199,-145,-801,-478,85,1001,-634,555,519,470,-421,-716,-473,-778,-864,641,-966,-542,425,-654,801,405,-8
                    }));
                    add(new InsertTestInput(new int[] {
                    544,-551,-306,865,-740,-515,307,661,794,-865,93,857,-116,
                    }, 856, 14, false, new int[] {
                    544,-551,-306,865,-740,-515,307,661,794,-865,93,857,-116
                    }));
                    add(new InsertTestInput(new int[] {
                    -245,413,-204,-193,900,697,55,-440,292,508,369,-583,
                    }, 349, 10, true, new int[] {
                    -245,413,-204,-193,900,697,55,-440,292,508,349,369,-583
                    }));
                    add(new InsertTestInput(new int[] {
                    679,166,807,446,398,1002,-192,393,-769,38,712,492,273,-693,-158,
                    }, 447, 22, false, new int[] {
                    679,166,807,446,398,1002,-192,393,-769,38,712,492,273,-693,-158
                    }));
                    add(new InsertTestInput(new int[] {
                    -233,-743,-174,225,-268,589,-207,570,914,323,-874,-612,-524,-326,-588,
                    }, 71, 11, true, new int[] {
                    -233,-743,-174,225,-268,589,-207,570,914,323,-874,71,-612,-524,-326,-588
                    }));
                    add(new InsertTestInput(new int[] {
                    210,865,1016,-797,-364,574,-358,-1012,891,432,333,-426,-301,551,
                    }, 484, 13, true, new int[] {
                    210,865,1016,-797,-364,574,-358,-1012,891,432,333,-426,-301,484,551
                    }));
                    add(new InsertTestInput(new int[] {
                    -721,-401,128,715,738,349,298,-383,754,185,-648,-130,-5,282,
                    }, -273, 10, true, new int[] {
                    -721,-401,128,715,738,349,298,-383,754,185,-273,-648,-130,-5,282
                    }));
                    add(new InsertTestInput(new int[] {
                    497,-134,-493,692,943,815,-256,730,-157,-600,-870,678,-939,919,
                    }, -458, 14, true, new int[] {
                    497,-134,-493,692,943,815,-256,730,-157,-600,-870,678,-939,919,-458
                    }));
                    add(new InsertTestInput(new int[] {
                    -992,-33,177,-980,89,-459,93,-877,549,-1024,-185,646,-828,-92,45,-102,349,157,-827,726,717,-826,153,-104,-437,528,
                    }, 815, 27, false, new int[] {
                    -992,-33,177,-980,89,-459,93,-877,549,-1024,-185,646,-828,-92,45,-102,349,157,-827,726,717,-826,153,-104,-437,528
                    }));
                    add(new InsertTestInput(new int[] {
                    987,451,-39,306,-342,-708,-191,-111,
                    }, -305, 6, true, new int[] {
                    987,451,-39,306,-342,-708,-305,-191,-111
                    }));
                    add(new InsertTestInput(new int[] {
                    579,-171,665,-658,-121,749,-114,656,-454,-961,-518,
                    }, 512, 15, false, new int[] {
                    579,-171,665,-658,-121,749,-114,656,-454,-961,-518
                    }));
                    add(new InsertTestInput(new int[] {
                    911,-549,-950,24,-883,492,-288,890,-621,-717,-146,525,
                    }, -560, 9, true, new int[] {
                    911,-549,-950,24,-883,492,-288,890,-621,-560,-717,-146,525
                    }));
                    add(new InsertTestInput(new int[] {
                    166,-794,220,-1022,276,372,110,-641,-736,-224,-290,383,178,819,-249,-619,
                    }, 619, 19, false, new int[] {
                    166,-794,220,-1022,276,372,110,-641,-736,-224,-290,383,178,819,-249,-619
                    }));
                    add(new InsertTestInput(new int[] {
                    260,382,-789,-829,-834,894,-872,319,-885,1010,678,-301,77,-902,
                    }, -531, 13, true, new int[] {
                    260,382,-789,-829,-834,894,-872,319,-885,1010,678,-301,77,-531,-902
                    }));
                    add(new InsertTestInput(new int[] {
                    420,912,723,-99,35,353,881,542,-753,-578,-143,59,-877,147,-156,-802,455,
                    }, -546, 26, false, new int[] {
                    420,912,723,-99,35,353,881,542,-753,-578,-143,59,-877,147,-156,-802,455
                    }));
                    add(new InsertTestInput(new int[] {
                    360,663,-959,-477,-740,-195,-89,-160,-514,-544,854,179,-352,521,689,-269,-697,597,-591,1003,-124,222,
                    }, -218, 13, true, new int[] {
                    360,663,-959,-477,-740,-195,-89,-160,-514,-544,854,179,-352,-218,521,689,-269,-697,597,-591,1003,-124,222
                    }));
                    add(new InsertTestInput(new int[] {
                    400,-550,-499,-114,786,-584,742,-974,435,758,589,-985,-183,501,903,1000,997,467,-390,960,171,960,238,-118,-260,
                    }, -632, 25, true, new int[] {
                    400,-550,-499,-114,786,-584,742,-974,435,758,589,-985,-183,501,903,1000,997,467,-390,960,171,960,238,-118,-260,-632
                    }));
                    add(new InsertTestInput(new int[] {
                    -611,-675,-258,841,-881,811,-911,935,-561,800,120,
                    }, -448, 11, true, new int[] {
                    -611,-675,-258,841,-881,811,-911,935,-561,800,120,-448
                    }));
                    add(new InsertTestInput(new int[] {
                    656,-365,484,945,1006,744,527,-394,-976,145,659,-860,267,-696,
                    }, 909, 21, false, new int[] {
                    656,-365,484,945,1006,744,527,-394,-976,145,659,-860,267,-696
                    }));
                    add(new InsertTestInput(new int[] {
                    -529,-376,-824,-555,-543,-155,-292,801,-77,-905,327,978,1007,-638,92,-501,-182,-288,549,628,167,508,224,-355,
                    }, 621, 17, true, new int[] {
                    -529,-376,-824,-555,-543,-155,-292,801,-77,-905,327,978,1007,-638,92,-501,-182,621,-288,549,628,167,508,224,-355
                    }));
                    add(new InsertTestInput(new int[] {
                    -980,209,-578,170,-307,-486,-301,-223,-352,-759,-101,-510,-22,-553,-1019,-271,656,117,-882,-183,-874,-50,-148,
                    }, -946, 32, false, new int[] {
                    -980,209,-578,170,-307,-486,-301,-223,-352,-759,-101,-510,-22,-553,-1019,-271,656,117,-882,-183,-874,-50,-148
                    }));
                    /* END AUTOGENERATED CODE */
                }
            };

    private static final List<RemoveTestInput> REMOVAL_TEST_CASES =
            new ArrayList<>() {
                {
                    add(new RemoveTestInput(new int[] { 2 }, 0,
                            true, new int[] { }));
                    add(new RemoveTestInput(new int[] { 2, 1 }, 0,
                            true, new int[] { 1 }));
                    add(new RemoveTestInput(new int[] { 0, 1 }, 2,
                            false, new int[] { 0, 1 }));
                    add(new RemoveTestInput(new int[] { 1, 2 }, -1,
                            false, new int[] { 1, 2 }));
                    /* BEGIN AUTOGENERATED CODE */
                    add(new RemoveTestInput(new int[] {
                    -919,441,-794,-258,927,-463,-1001,446,-301,-97,356,593,1007,-991,180,-711,-755,410,-900,
                    }, 19, false, new int[] {
                    -919,441,-794,-258,927,-463,-1001,446,-301,-97,356,593,1007,-991,180,-711,-755,410,-900
                    }));
                    add(new RemoveTestInput(new int[] {
                    -393,-559,324,-208,614,-733,666,436,-988,0,-135,474,299,123,-628,51,-820,-924,-325,-606,-43,241,28,777,637,340,
                    }, 13, true, new int[] {
                    -393,-559,324,-208,614,-733,666,436,-988,0,-135,474,299,-628,51,-820,-924,-325,-606,-43,241,28,777,637,340
                    }));
                    add(new RemoveTestInput(new int[] {
                    -110,452,-650,-45,803,-859,-590,635,538,988,-249,-600,-972,-436,440,-313,-326,-154,-191,-902,629,
                    }, 15, true, new int[] {
                    -110,452,-650,-45,803,-859,-590,635,538,988,-249,-600,-972,-436,440,-326,-154,-191,-902,629
                    }));
                    add(new RemoveTestInput(new int[] {
                    -376,304,649,-634,682,-262,995,-1021,247,628,0,901,645,-226,365,-948,609,-655,564,774,-445,194,-822,682,215,621,-411,-410,980,150,
                    }, 16, true, new int[] {
                    -376,304,649,-634,682,-262,995,-1021,247,628,0,901,645,-226,365,-948,-655,564,774,-445,194,-822,682,215,621,-411,-410,980,150
                    }));
                    add(new RemoveTestInput(new int[] {
                    -430,622,190,598,-494,-164,-614,-199,357,-335,
                    }, 14, false, new int[] {
                    -430,622,190,598,-494,-164,-614,-199,357,-335
                    }));
                    add(new RemoveTestInput(new int[] {
                    533,-28,-954,-989,-564,-161,-138,488,608,
                    }, 13, false, new int[] {
                    533,-28,-954,-989,-564,-161,-138,488,608
                    }));
                    add(new RemoveTestInput(new int[] {
                    719,819,-640,29,362,-968,512,524,6,-1,-408,-605,-452,679,713,-985,102,105,339,-80,-581,-292,-767,-230,717,-272,858,922,367,352,662,-401,
                    }, 37, false, new int[] {
                    719,819,-640,29,362,-968,512,524,6,-1,-408,-605,-452,679,713,-985,102,105,339,-80,-581,-292,-767,-230,717,-272,858,922,367,352,662,-401
                    }));
                    add(new RemoveTestInput(new int[] {
                    -191,249,142,-989,-394,-209,-799,-123,-530,-450,-764,-687,1019,814,-438,603,-788,841,-176,-227,-74,914,376,982,-787,135,216,942,-360,-299,-179,
                    }, 36, false, new int[] {
                    -191,249,142,-989,-394,-209,-799,-123,-530,-450,-764,-687,1019,814,-438,603,-788,841,-176,-227,-74,914,376,982,-787,135,216,942,-360,-299,-179
                    }));
                    add(new RemoveTestInput(new int[] {
                    -860,877,732,-883,-913,506,-475,158,-315,-224,-734,-552,-339,-163,267,
                    }, 14, true, new int[] {
                    -860,877,732,-883,-913,506,-475,158,-315,-224,-734,-552,-339,-163
                    }));
                    add(new RemoveTestInput(new int[] {
                    -849,904,777,1013,294,4,-676,-66,39,805,-34,-831,
                    }, 11, true, new int[] {
                    -849,904,777,1013,294,4,-676,-66,39,805,-34
                    }));
                    add(new RemoveTestInput(new int[] {
                    -915,617,111,-986,-585,-309,-878,17,802,848,873,-988,-216,
                    }, 10, true, new int[] {
                    -915,617,111,-986,-585,-309,-878,17,802,848,-988,-216
                    }));
                    add(new RemoveTestInput(new int[] {
                    -195,109,-78,-71,481,649,15,486,447,-151,535,-533,-943,-241,330,817,216,-64,-467,838,-1007,-618,77,213,163,283,-4,692,-701,488,112,
                    }, 36, false, new int[] {
                    -195,109,-78,-71,481,649,15,486,447,-151,535,-533,-943,-241,330,817,216,-64,-467,838,-1007,-618,77,213,163,283,-4,692,-701,488,112
                    }));
                    add(new RemoveTestInput(new int[] {
                    -886,-940,-245,-1021,-521,511,-870,806,-217,335,88,-106,984,-603,596,-335,297,397,235,699,
                    }, 19, true, new int[] {
                    -886,-940,-245,-1021,-521,511,-870,806,-217,335,88,-106,984,-603,596,-335,297,397,235
                    }));
                    add(new RemoveTestInput(new int[] {
                    -296,994,440,608,307,-322,-726,-425,153,-469,
                    }, 15, false, new int[] {
                    -296,994,440,608,307,-322,-726,-425,153,-469
                    }));
                    add(new RemoveTestInput(new int[] {
                    746,645,-136,-549,322,395,-641,537,611,521,-663,466,216,-124,991,439,395,-310,-531,-413,-213,38,33,-377,
                    }, 28, false, new int[] {
                    746,645,-136,-549,322,395,-641,537,611,521,-663,466,216,-124,991,439,395,-310,-531,-413,-213,38,33,-377
                    }));
                    add(new RemoveTestInput(new int[] {
                    681,-577,-799,376,359,-783,-209,252,177,-148,802,-942,-674,316,103,-995,-976,720,999,-741,990,-613,-865,-889,554,-163,410,913,218,
                    }, 20, true, new int[] {
                    681,-577,-799,376,359,-783,-209,252,177,-148,802,-942,-674,316,103,-995,-976,720,999,-741,-613,-865,-889,554,-163,410,913,218
                    }));
                    add(new RemoveTestInput(new int[] {
                    -829,-673,-357,-126,-69,-712,-200,-303,114,-569,-153,732,185,1024,791,775,354,858,-277,-227,-431,-85,-232,-178,-975,557,88,844,
                    }, 15, true, new int[] {
                    -829,-673,-357,-126,-69,-712,-200,-303,114,-569,-153,732,185,1024,791,354,858,-277,-227,-431,-85,-232,-178,-975,557,88,844
                    }));
                    add(new RemoveTestInput(new int[] {
                    -701,-723,167,175,860,-154,866,-118,18,650,-739,-422,-583,-241,580,572,364,60,684,252,205,-1,
                    }, 21, true, new int[] {
                    -701,-723,167,175,860,-154,866,-118,18,650,-739,-422,-583,-241,580,572,364,60,684,252,205
                    }));
                    add(new RemoveTestInput(new int[] {
                    277,-99,83,-788,1019,-523,681,-903,89,-63,816,-406,914,-723,-122,-432,
                    }, 22, false, new int[] {
                    277,-99,83,-788,1019,-523,681,-903,89,-63,816,-406,914,-723,-122,-432
                    }));
                    add(new RemoveTestInput(new int[] {
                    -838,782,-82,207,-555,197,723,825,947,-677,-981,-802,631,380,-397,-660,-851,918,-214,
                    }, 26, false, new int[] {
                    -838,782,-82,207,-555,197,723,825,947,-677,-981,-802,631,380,-397,-660,-851,918,-214
                    }));
                    add(new RemoveTestInput(new int[] {
                    -948,-415,-54,600,-788,-328,1009,45,-153,264,331,886,-701,315,-563,
                    }, 11, true, new int[] {
                    -948,-415,-54,600,-788,-328,1009,45,-153,264,331,-701,315,-563
                    }));
                    add(new RemoveTestInput(new int[] {
                    -598,768,-353,433,-1000,458,-594,-700,-25,954,-4,-722,667,17,965,209,-316,995,
                    }, 11, true, new int[] {
                    -598,768,-353,433,-1000,458,-594,-700,-25,954,-4,667,17,965,209,-316,995
                    }));
                    add(new RemoveTestInput(new int[] {
                    -558,440,-301,-542,-986,727,452,472,-910,-791,91,-509,1004,388,148,-176,708,809,
                    }, 19, false, new int[] {
                    -558,440,-301,-542,-986,727,452,472,-910,-791,91,-509,1004,388,148,-176,708,809
                    }));
                    add(new RemoveTestInput(new int[] {
                    -892,86,-172,841,-268,849,-753,694,222,-952,936,-175,-950,-878,-1018,342,929,-395,-252,534,-536,-742,-827,800,163,826,-660,-783,952,-125,
                    }, 33, false, new int[] {
                    -892,86,-172,841,-268,849,-753,694,222,-952,936,-175,-950,-878,-1018,342,929,-395,-252,534,-536,-742,-827,800,163,826,-660,-783,952,-125
                    }));
                    add(new RemoveTestInput(new int[] {
                    856,-66,352,845,747,-153,-398,-947,-16,835,752,718,-18,889,-945,-754,260,31,821,
                    }, 18, true, new int[] {
                    856,-66,352,845,747,-153,-398,-947,-16,835,752,718,-18,889,-945,-754,260,31
                    }));
                    add(new RemoveTestInput(new int[] {
                    412,-771,548,691,-653,640,-591,-324,-277,
                    }, 13, false, new int[] {
                    412,-771,548,691,-653,640,-591,-324,-277
                    }));
                    add(new RemoveTestInput(new int[] {
                    82,-343,-575,718,475,24,-576,-689,-94,
                    }, 6, true, new int[] {
                    82,-343,-575,718,475,24,-689,-94
                    }));
                    add(new RemoveTestInput(new int[] {
                    375,342,-706,161,-465,-734,-862,-457,36,246,-660,-824,-485,897,319,
                    }, 12, true, new int[] {
                    375,342,-706,161,-465,-734,-862,-457,36,246,-660,-824,897,319
                    }));
                    add(new RemoveTestInput(new int[] {
                    -170,-597,29,140,-926,799,703,-901,986,-394,880,-129,-398,-247,-93,172,626,-375,736,28,-631,-875,128,
                    }, 19, true, new int[] {
                    -170,-597,29,140,-926,799,703,-901,986,-394,880,-129,-398,-247,-93,172,626,-375,736,-631,-875,128
                    }));
                    add(new RemoveTestInput(new int[] {
                    -198,927,587,464,31,680,582,-676,44,78,625,879,-186,-701,-379,-298,825,-384,939,-157,847,302,-385,283,-746,-870,454,
                    }, 27, false, new int[] {
                    -198,927,587,464,31,680,582,-676,44,78,625,879,-186,-701,-379,-298,825,-384,939,-157,847,302,-385,283,-746,-870,454
                    }));
                    add(new RemoveTestInput(new int[] {
                    521,-111,98,878,525,-967,466,-705,-117,54,-777,-751,633,-399,-585,-365,
                    }, 12, true, new int[] {
                    521,-111,98,878,525,-967,466,-705,-117,54,-777,-751,-399,-585,-365
                    }));
                    add(new RemoveTestInput(new int[] {
                    455,-257,-691,-230,555,-413,326,733,-536,970,929,993,
                    }, 7, true, new int[] {
                    455,-257,-691,-230,555,-413,326,-536,970,929,993
                    }));
                    add(new RemoveTestInput(new int[] {
                    361,-608,-938,-1006,1009,-535,-189,844,30,578,546,939,73,-176,-505,859,852,853,945,-267,-468,762,-402,-700,-488,583,-697,-439,
                    }, 32, false, new int[] {
                    361,-608,-938,-1006,1009,-535,-189,844,30,578,546,939,73,-176,-505,859,852,853,945,-267,-468,762,-402,-700,-488,583,-697,-439
                    }));
                    add(new RemoveTestInput(new int[] {
                    -77,-65,899,886,-926,-544,-51,192,-629,-256,-663,-595,-299,
                    }, 19, false, new int[] {
                    -77,-65,899,886,-926,-544,-51,192,-629,-256,-663,-595,-299
                    }));
                    add(new RemoveTestInput(new int[] {
                    -446,-649,-468,-254,796,228,626,-420,
                    }, 10, false, new int[] {
                    -446,-649,-468,-254,796,228,626,-420
                    }));
                    add(new RemoveTestInput(new int[] {
                    254,473,511,830,714,-198,-15,491,545,6,820,101,-32,366,-1017,-148,988,248,-652,-340,-810,1021,-442,663,-673,
                    }, 14, true, new int[] {
                    254,473,511,830,714,-198,-15,491,545,6,820,101,-32,366,-148,988,248,-652,-340,-810,1021,-442,663,-673
                    }));
                    add(new RemoveTestInput(new int[] {
                    -683,-708,268,-829,-261,-487,-995,-4,
                    }, 4, true, new int[] {
                    -683,-708,268,-829,-487,-995,-4
                    }));
                    add(new RemoveTestInput(new int[] {
                    95,808,-255,18,-940,-247,-297,-181,331,-323,-567,720,-909,809,765,715,490,-325,771,395,1013,-388,82,655,217,964,-232,384,653,
                    }, 26, true, new int[] {
                    95,808,-255,18,-940,-247,-297,-181,331,-323,-567,720,-909,809,765,715,490,-325,771,395,1013,-388,82,655,217,964,384,653
                    }));
                    add(new RemoveTestInput(new int[] {
                    -468,-991,-440,-389,-449,-122,276,18,162,210,-605,695,297,-625,476,34,4,-84,351,192,826,-12,-197,-760,-46,
                    }, 32, false, new int[] {
                    -468,-991,-440,-389,-449,-122,276,18,162,210,-605,695,297,-625,476,34,4,-84,351,192,826,-12,-197,-760,-46
                    }));
                    add(new RemoveTestInput(new int[] {
                    -685,627,580,-288,-940,997,-937,-1018,434,-128,42,-40,-226,897,492,-851,690,755,-512,699,-54,31,
                    }, 32, false, new int[] {
                    -685,627,580,-288,-940,997,-937,-1018,434,-128,42,-40,-226,897,492,-851,690,755,-512,699,-54,31
                    }));
                    add(new RemoveTestInput(new int[] {
                    386,-1010,-540,336,-952,-110,893,-117,-179,-897,697,-208,721,-788,856,410,573,-617,751,-281,318,662,-416,-849,-111,969,-300,313,636,512,-371,
                    }, 30, true, new int[] {
                    386,-1010,-540,336,-952,-110,893,-117,-179,-897,697,-208,721,-788,856,410,573,-617,751,-281,318,662,-416,-849,-111,969,-300,313,636,512
                    }));
                    add(new RemoveTestInput(new int[] {
                    -749,544,-117,1013,-768,245,836,-690,243,778,630,-485,-220,-802,-915,501,512,-505,-187,-47,-591,547,
                    }, 12, true, new int[] {
                    -749,544,-117,1013,-768,245,836,-690,243,778,630,-485,-802,-915,501,512,-505,-187,-47,-591,547
                    }));
                    add(new RemoveTestInput(new int[] {
                    183,7,567,815,347,-126,885,616,748,818,-749,684,770,-743,-56,-692,-268,-498,879,385,401,
                    }, 23, false, new int[] {
                    183,7,567,815,347,-126,885,616,748,818,-749,684,770,-743,-56,-692,-268,-498,879,385,401
                    }));
                    add(new RemoveTestInput(new int[] {
                    598,-710,-666,799,-857,-289,1014,-664,178,762,-645,-550,-89,-436,-995,459,-614,-532,530,-712,-385,-813,973,639,753,-87,699,
                    }, 21, true, new int[] {
                    598,-710,-666,799,-857,-289,1014,-664,178,762,-645,-550,-89,-436,-995,459,-614,-532,530,-712,-385,973,639,753,-87,699
                    }));
                    add(new RemoveTestInput(new int[] {
                    -824,317,-162,-407,-3,-622,-820,605,-470,321,869,854,-703,388,796,-870,-767,-44,
                    }, 15, true, new int[] {
                    -824,317,-162,-407,-3,-622,-820,605,-470,321,869,854,-703,388,796,-767,-44
                    }));
                    add(new RemoveTestInput(new int[] {
                    521,814,196,701,-436,611,-572,-590,
                    }, 4, true, new int[] {
                    521,814,196,701,611,-572,-590
                    }));
                    add(new RemoveTestInput(new int[] {
                    -384,-770,763,82,665,809,-629,-322,-692,159,951,255,-408,
                    }, 15, false, new int[] {
                    -384,-770,763,82,665,809,-629,-322,-692,159,951,255,-408
                    }));
                    add(new RemoveTestInput(new int[] {
                    854,292,-842,-553,-22,59,436,723,600,-297,888,126,
                    }, 11, true, new int[] {
                    854,292,-842,-553,-22,59,436,723,600,-297,888
                    }));
                    add(new RemoveTestInput(new int[] {
                    -300,429,840,80,965,-794,-170,350,-804,-796,-536,-116,-647,939,781,9,-541,-819,334,-549,871,947,154,-469,187,-207,537,521,582,
                    }, 41, false, new int[] {
                    -300,429,840,80,965,-794,-170,350,-804,-796,-536,-116,-647,939,781,9,-541,-819,334,-549,871,947,154,-469,187,-207,537,521,582
                    }));
                    add(new RemoveTestInput(new int[] {
                    3,-919,-374,125,-980,-891,-137,-667,79,244,977,-929,
                    }, 7, true, new int[] {
                    3,-919,-374,125,-980,-891,-137,79,244,977,-929
                    }));
                    add(new RemoveTestInput(new int[] {
                    -887,-452,-846,17,-766,515,-728,-675,-48,763,-832,600,-470,
                    }, 10, true, new int[] {
                    -887,-452,-846,17,-766,515,-728,-675,-48,763,600,-470
                    }));
                    add(new RemoveTestInput(new int[] {
                    188,-199,-678,-482,-341,-482,-469,222,-975,-95,412,111,-465,-682,806,-316,63,-484,156,77,-899,593,225,-142,189,
                    }, 28, false, new int[] {
                    188,-199,-678,-482,-341,-482,-469,222,-975,-95,412,111,-465,-682,806,-316,63,-484,156,77,-899,593,225,-142,189
                    }));
                    add(new RemoveTestInput(new int[] {
                    457,-850,-230,885,264,382,-360,273,-472,319,-74,76,-974,
                    }, 7, true, new int[] {
                    457,-850,-230,885,264,382,-360,-472,319,-74,76,-974
                    }));
                    add(new RemoveTestInput(new int[] {
                    225,-767,-514,883,-299,-810,718,463,-170,929,-277,396,-632,408,146,-468,-266,589,785,12,-989,
                    }, 22, false, new int[] {
                    225,-767,-514,883,-299,-810,718,463,-170,929,-277,396,-632,408,146,-468,-266,589,785,12,-989
                    }));
                    add(new RemoveTestInput(new int[] {
                    426,853,-764,-668,-821,-269,415,-970,227,
                    }, 12, false, new int[] {
                    426,853,-764,-668,-821,-269,415,-970,227
                    }));
                    add(new RemoveTestInput(new int[] {
                    58,635,-417,152,-152,-221,-448,39,
                    }, 4, true, new int[] {
                    58,635,-417,152,-221,-448,39
                    }));
                    add(new RemoveTestInput(new int[] {
                    -556,-778,42,-302,-1000,-410,-711,80,639,-851,-394,-163,-29,-732,920,538,238,972,357,345,943,-808,-238,606,
                    }, 27, false, new int[] {
                    -556,-778,42,-302,-1000,-410,-711,80,639,-851,-394,-163,-29,-732,920,538,238,972,357,345,943,-808,-238,606
                    }));
                    add(new RemoveTestInput(new int[] {
                    -608,-167,-33,796,942,98,468,1018,-568,-123,-883,-915,-341,-595,626,807,-942,385,271,-768,
                    }, 19, true, new int[] {
                    -608,-167,-33,796,942,98,468,1018,-568,-123,-883,-915,-341,-595,626,807,-942,385,271
                    }));
                    add(new RemoveTestInput(new int[] {
                    -890,167,612,968,378,866,206,-25,118,-168,-1009,-228,-576,-294,155,9,-832,-806,-706,361,51,
                    }, 31, false, new int[] {
                    -890,167,612,968,378,866,206,-25,118,-168,-1009,-228,-576,-294,155,9,-832,-806,-706,361,51
                    }));
                    add(new RemoveTestInput(new int[] {
                    524,870,981,121,-401,870,-507,187,-42,986,883,233,832,-701,-837,434,592,-184,
                    }, 10, true, new int[] {
                    524,870,981,121,-401,870,-507,187,-42,986,233,832,-701,-837,434,592,-184
                    }));
                    add(new RemoveTestInput(new int[] {
                    -500,-672,936,-631,-783,-951,-606,482,191,203,-546,109,971,-919,285,-1014,945,289,-25,586,105,-4,739,277,537,-516,-51,265,-963,-458,-790,-662,
                    }, 30, true, new int[] {
                    -500,-672,936,-631,-783,-951,-606,482,191,203,-546,109,971,-919,285,-1014,945,289,-25,586,105,-4,739,277,537,-516,-51,265,-963,-458,-662
                    }));
                    add(new RemoveTestInput(new int[] {
                    355,-968,-970,292,951,-331,2,-816,-935,607,617,-695,-943,-220,-780,811,53,-523,-195,646,-372,772,930,553,-693,910,361,-296,350,-32,
                    }, 28, true, new int[] {
                    355,-968,-970,292,951,-331,2,-816,-935,607,617,-695,-943,-220,-780,811,53,-523,-195,646,-372,772,930,553,-693,910,361,-296,-32
                    }));
                    add(new RemoveTestInput(new int[] {
                    38,-212,-6,-235,-618,-619,187,506,864,
                    }, 11, false, new int[] {
                    38,-212,-6,-235,-618,-619,187,506,864
                    }));
                    add(new RemoveTestInput(new int[] {
                    -634,-345,680,-16,-396,-894,873,-9,-484,-893,-171,-763,-806,397,456,567,546,508,323,619,-676,-265,-525,343,
                    }, 34, false, new int[] {
                    -634,-345,680,-16,-396,-894,873,-9,-484,-893,-171,-763,-806,397,456,567,546,508,323,619,-676,-265,-525,343
                    }));
                    /* END AUTOGENERATED CODE */
                }
            };

    private static final List<SwapTestInput> SWAP_TEST_CASES =
            new ArrayList<>() {
                {
                    add(new SwapTestInput(new int[] { 2, 1 }, 0, 1,
                            true, new int[] { 1, 2 }));
                    add(new SwapTestInput(new int[] { 2, 1, 3 }, 0, 2,
                            true, new int[] { 3, 1, 2 }));
                    add(new SwapTestInput(new int[] { 0, 1 }, 2, 1,
                            false, new int[] { 0, 1 }));
                    add(new SwapTestInput(new int[] { }, -1, 1,
                            false, new int[] { }));
                    /* BEGIN AUTOGENERATED CODE */
                    add(new SwapTestInput(new int[] {
                    392,-661,-861,-15,-658,492,104,408,505,-141,-640,-1021,695,450,694,-565,34,258,385,
                    }, 15, 18, true, new int[] {
                    392,-661,-861,-15,-658,492,104,408,505,-141,-640,-1021,695,450,694,385,34,258,-565
                    }));
                    add(new SwapTestInput(new int[] {
                    -509,-320,-273,-409,-843,-676,-473,190,808,174,-552,845,729,146,869,142,-759,312,-481,-907,524,
                    }, 17, 18, true, new int[] {
                    -509,-320,-273,-409,-843,-676,-473,190,808,174,-552,845,729,146,869,142,-759,-481,312,-907,524
                    }));
                    add(new SwapTestInput(new int[] {
                    800,792,-516,995,470,960,233,-608,847,88,554,314,217,-435,-1014,-966,435,-137,545,-524,556,-742,-199,-501,-833,-842,425,
                    }, 26, 25, true, new int[] {
                    800,792,-516,995,470,960,233,-608,847,88,554,314,217,-435,-1014,-966,435,-137,545,-524,556,-742,-199,-501,-833,425,-842
                    }));
                    add(new SwapTestInput(new int[] {
                    619,-409,-845,-817,540,-780,114,815,158,-59,-96,-521,-856,-608,417,665,162,503,684,
                    }, 20, 16, false, new int[] {
                    619,-409,-845,-817,540,-780,114,815,158,-59,-96,-521,-856,-608,417,665,162,503,684
                    }));
                    add(new SwapTestInput(new int[] {
                    -586,-685,-95,46,37,-64,737,-291,573,983,395,227,-17,51,1006,-953,111,-968,-634,1014,271,924,371,252,-565,369,120,416,-160,
                    }, 28, 25, true, new int[] {
                    -586,-685,-95,46,37,-64,737,-291,573,983,395,227,-17,51,1006,-953,111,-968,-634,1014,271,924,371,252,-565,-160,120,416,369
                    }));
                    add(new SwapTestInput(new int[] {
                    399,29,-104,71,-216,1018,938,-874,-538,-215,-192,-756,77,280,24,-446,292,172,722,-162,833,745,520,378,-394,99,-460,336,-197,-335,
                    }, 31, 32, false, new int[] {
                    399,29,-104,71,-216,1018,938,-874,-538,-215,-192,-756,77,280,24,-446,292,172,722,-162,833,745,520,378,-394,99,-460,336,-197,-335
                    }));
                    add(new SwapTestInput(new int[] {
                    -587,-978,394,258,595,-291,-325,941,778,
                    }, 9, 10, false, new int[] {
                    -587,-978,394,258,595,-291,-325,941,778
                    }));
                    add(new SwapTestInput(new int[] {
                    -771,-522,420,-797,-192,230,-421,288,780,-149,
                    }, 13, 11, false, new int[] {
                    -771,-522,420,-797,-192,230,-421,288,780,-149
                    }));
                    add(new SwapTestInput(new int[] {
                    293,-1007,-924,-941,823,-821,-62,-31,550,-708,557,-731,557,-853,-301,-592,576,-294,-969,
                    }, 16, 16, true, new int[] {
                    293,-1007,-924,-941,823,-821,-62,-31,550,-708,557,-731,557,-853,-301,-592,576,-294,-969
                    }));
                    add(new SwapTestInput(new int[] {
                    339,996,-817,-146,-907,-240,127,-570,949,658,-65,555,834,453,979,-754,-376,180,-413,-1008,-10,-469,-756,-784,-203,
                    }, 27, 23, false, new int[] {
                    339,996,-817,-146,-907,-240,127,-570,949,658,-65,555,834,453,979,-754,-376,180,-413,-1008,-10,-469,-756,-784,-203
                    }));
                    add(new SwapTestInput(new int[] {
                    745,716,-23,-859,743,-245,775,-824,733,-989,796,
                    }, 11, 8, false, new int[] {
                    745,716,-23,-859,743,-245,775,-824,733,-989,796
                    }));
                    add(new SwapTestInput(new int[] {
                    414,579,804,586,-767,993,-562,623,-345,81,420,-269,84,702,764,
                    }, 16, 16, false, new int[] {
                    414,579,804,586,-767,993,-562,623,-345,81,420,-269,84,702,764
                    }));
                    add(new SwapTestInput(new int[] {
                    942,837,751,253,-572,-999,-206,-322,-473,178,63,-929,-572,-415,850,63,570,218,52,835,809,62,
                    }, 19, 19, true, new int[] {
                    942,837,751,253,-572,-999,-206,-322,-473,178,63,-929,-572,-415,850,63,570,218,52,835,809,62
                    }));
                    add(new SwapTestInput(new int[] {
                    -574,928,73,-391,-590,881,-820,-228,729,-394,-592,-1,-681,-844,103,100,-12,-341,-130,-263,-580,-842,296,-260,
                    }, 30, 23, false, new int[] {
                    -574,928,73,-391,-590,881,-820,-228,729,-394,-592,-1,-681,-844,103,100,-12,-341,-130,-263,-580,-842,296,-260
                    }));
                    add(new SwapTestInput(new int[] {
                    74,-914,174,436,735,-67,459,1003,-979,-292,101,400,-637,-354,-588,-223,581,-815,225,-36,-208,-16,-333,84,
                    }, 24, 24, false, new int[] {
                    74,-914,174,436,735,-67,459,1003,-979,-292,101,400,-637,-354,-588,-223,581,-815,225,-36,-208,-16,-333,84
                    }));
                    add(new SwapTestInput(new int[] {
                    -914,-192,-632,197,-885,-966,510,24,394,-30,210,138,507,-78,
                    }, 11, 15, false, new int[] {
                    -914,-192,-632,197,-885,-966,510,24,394,-30,210,138,507,-78
                    }));
                    add(new SwapTestInput(new int[] {
                    524,850,638,70,-721,-576,354,648,841,-788,-542,-55,599,-762,-40,-327,-331,-782,956,566,-88,-224,35,-995,
                    }, 25, 18, false, new int[] {
                    524,850,638,70,-721,-576,354,648,841,-788,-542,-55,599,-762,-40,-327,-331,-782,956,566,-88,-224,35,-995
                    }));
                    add(new SwapTestInput(new int[] {
                    -704,440,989,-859,-166,904,-748,-766,-357,
                    }, 11, 10, false, new int[] {
                    -704,440,989,-859,-166,904,-748,-766,-357
                    }));
                    add(new SwapTestInput(new int[] {
                    -148,-986,985,893,-232,485,-1023,495,-913,854,31,760,136,-134,808,-309,-572,-879,409,-661,-424,-305,-733,183,
                    }, 24, 20, false, new int[] {
                    -148,-986,985,893,-232,485,-1023,495,-913,854,31,760,136,-134,808,-309,-572,-879,409,-661,-424,-305,-733,183
                    }));
                    add(new SwapTestInput(new int[] {
                    -806,325,102,785,-638,-867,122,-579,773,-584,-265,18,327,-4,-48,-143,-742,-655,645,
                    }, 19, 19, false, new int[] {
                    -806,325,102,785,-638,-867,122,-579,773,-584,-265,18,327,-4,-48,-143,-742,-655,645
                    }));
                    add(new SwapTestInput(new int[] {
                    731,368,532,-746,936,-728,249,-310,-1002,-678,448,741,736,990,110,-133,696,-110,-741,-872,-521,791,-148,756,738,1004,-1,-731,506,-117,-370,551,
                    }, 36, 37, false, new int[] {
                    731,368,532,-746,936,-728,249,-310,-1002,-678,448,741,736,990,110,-133,696,-110,-741,-872,-521,791,-148,756,738,1004,-1,-731,506,-117,-370,551
                    }));
                    add(new SwapTestInput(new int[] {
                    -185,-800,632,725,322,666,-991,-303,-867,-859,-146,-567,-202,-315,697,-633,7,116,217,317,622,439,1017,249,-957,-694,-991,-12,
                    }, 25, 22, true, new int[] {
                    -185,-800,632,725,322,666,-991,-303,-867,-859,-146,-567,-202,-315,697,-633,7,116,217,317,622,439,-694,249,-957,1017,-991,-12
                    }));
                    add(new SwapTestInput(new int[] {
                    -694,83,-235,930,-387,569,239,24,489,-82,-844,-406,-283,725,-460,862,-513,-468,-708,292,441,-605,-751,-856,360,-521,-670,863,666,
                    }, 32, 34, false, new int[] {
                    -694,83,-235,930,-387,569,239,24,489,-82,-844,-406,-283,725,-460,862,-513,-468,-708,292,441,-605,-751,-856,360,-521,-670,863,666
                    }));
                    add(new SwapTestInput(new int[] {
                    -405,-713,-479,-34,510,720,44,-390,-601,43,573,-758,528,482,-221,
                    }, 19, 14, false, new int[] {
                    -405,-713,-479,-34,510,720,44,-390,-601,43,573,-758,528,482,-221
                    }));
                    add(new SwapTestInput(new int[] {
                    791,505,-868,-246,-286,-291,-876,308,-310,-410,596,860,
                    }, 15, 15, false, new int[] {
                    791,505,-868,-246,-286,-291,-876,308,-310,-410,596,860
                    }));
                    add(new SwapTestInput(new int[] {
                    -670,994,-204,526,-11,-341,566,-1016,417,
                    }, 9, 10, false, new int[] {
                    -670,994,-204,526,-11,-341,566,-1016,417
                    }));
                    add(new SwapTestInput(new int[] {
                    559,1007,-600,-153,-644,-985,-3,885,-966,642,-800,681,-963,-230,-575,976,717,-342,
                    }, 21, 21, false, new int[] {
                    559,1007,-600,-153,-644,-985,-3,885,-966,642,-800,681,-963,-230,-575,976,717,-342
                    }));
                    add(new SwapTestInput(new int[] {
                    150,-1014,725,761,156,-401,-42,-332,-166,439,376,-529,943,-882,683,42,5,205,919,609,973,796,232,-157,-247,-595,789,
                    }, 21, 34, false, new int[] {
                    150,-1014,725,761,156,-401,-42,-332,-166,439,376,-529,943,-882,683,42,5,205,919,609,973,796,232,-157,-247,-595,789
                    }));
                    add(new SwapTestInput(new int[] {
                    -840,136,-993,298,-929,992,256,-386,40,757,715,-882,803,722,
                    }, 15, 17, false, new int[] {
                    -840,136,-993,298,-929,992,256,-386,40,757,715,-882,803,722
                    }));
                    add(new SwapTestInput(new int[] {
                    489,729,202,-485,769,-813,-170,-981,
                    }, 9, 7, false, new int[] {
                    489,729,202,-485,769,-813,-170,-981
                    }));
                    add(new SwapTestInput(new int[] {
                    -1006,-93,980,-982,-538,-420,695,593,-1004,945,37,-21,871,-399,-989,332,259,-773,176,320,-392,209,
                    }, 24, 28, false, new int[] {
                    -1006,-93,980,-982,-538,-420,695,593,-1004,945,37,-21,871,-399,-989,332,259,-773,176,320,-392,209
                    }));
                    add(new SwapTestInput(new int[] {
                    207,864,-1021,-402,-44,849,-807,371,-583,645,46,-172,-306,-994,205,736,-344,-851,202,166,-108,1007,393,-811,-624,43,300,823,
                    }, 30, 27, false, new int[] {
                    207,864,-1021,-402,-44,849,-807,371,-583,645,46,-172,-306,-994,205,736,-344,-851,202,166,-108,1007,393,-811,-624,43,300,823
                    }));
                    add(new SwapTestInput(new int[] {
                    -622,-100,737,130,-872,494,284,818,645,-349,-133,1005,
                    }, 9, 15, false, new int[] {
                    -622,-100,737,130,-872,494,284,818,645,-349,-133,1005
                    }));
                    add(new SwapTestInput(new int[] {
                    461,485,187,-88,834,421,695,-279,-31,624,-566,-817,81,-656,432,428,-974,-525,
                    }, 15, 19, false, new int[] {
                    461,485,187,-88,834,421,695,-279,-31,624,-566,-817,81,-656,432,428,-974,-525
                    }));
                    add(new SwapTestInput(new int[] {
                    745,349,-959,472,-431,662,-590,567,-602,-874,228,-892,987,351,612,-208,-434,-198,885,397,-241,-956,669,-976,-420,849,-195,-969,872,
                    }, 34, 36, false, new int[] {
                    745,349,-959,472,-431,662,-590,567,-602,-874,228,-892,987,351,612,-208,-434,-198,885,397,-241,-956,669,-976,-420,849,-195,-969,872
                    }));
                    add(new SwapTestInput(new int[] {
                    840,-400,-863,-546,-477,-9,553,778,179,425,643,885,-312,445,-407,167,423,-938,334,3,844,-166,-359,693,
                    }, 26, 26, false, new int[] {
                    840,-400,-863,-546,-477,-9,553,778,179,425,643,885,-312,445,-407,167,423,-938,334,3,844,-166,-359,693
                    }));
                    add(new SwapTestInput(new int[] {
                    -229,476,-343,5,-38,973,975,413,113,
                    }, 9, 9, false, new int[] {
                    -229,476,-343,5,-38,973,975,413,113
                    }));
                    add(new SwapTestInput(new int[] {
                    880,-578,559,-135,-442,65,-71,-1013,554,-329,1003,-243,503,609,531,-965,-892,-540,750,410,-493,-956,-345,897,-302,
                    }, 20, 20, true, new int[] {
                    880,-578,559,-135,-442,65,-71,-1013,554,-329,1003,-243,503,609,531,-965,-892,-540,750,410,-493,-956,-345,897,-302
                    }));
                    add(new SwapTestInput(new int[] {
                    118,289,53,-958,892,-670,-525,257,138,395,109,-286,-678,-508,609,-648,-179,135,-336,-403,108,-732,336,-359,786,759,816,-730,212,-675,-150,-696,
                    }, 29, 28, true, new int[] {
                    118,289,53,-958,892,-670,-525,257,138,395,109,-286,-678,-508,609,-648,-179,135,-336,-403,108,-732,336,-359,786,759,816,-730,-675,212,-150,-696
                    }));
                    add(new SwapTestInput(new int[] {
                    184,497,802,-687,-215,-22,-85,-603,-677,869,-881,300,553,166,-699,-650,-400,325,-66,995,830,
                    }, 22, 18, false, new int[] {
                    184,497,802,-687,-215,-22,-85,-603,-677,869,-881,300,553,166,-699,-650,-400,325,-66,995,830
                    }));
                    add(new SwapTestInput(new int[] {
                    103,-500,123,-990,-117,161,199,-852,724,-960,191,287,-129,-122,-362,386,-353,473,-756,-461,
                    }, 25, 15, false, new int[] {
                    103,-500,123,-990,-117,161,199,-852,724,-960,191,287,-129,-122,-362,386,-353,473,-756,-461
                    }));
                    add(new SwapTestInput(new int[] {
                    -388,-894,168,-647,-18,384,890,238,-514,216,-874,-451,146,829,-45,539,934,-344,340,-491,-131,
                    }, 21, 22, false, new int[] {
                    -388,-894,168,-647,-18,384,890,238,-514,216,-874,-451,146,829,-45,539,934,-344,340,-491,-131
                    }));
                    add(new SwapTestInput(new int[] {
                    -17,469,-271,-988,796,85,-688,-30,-841,-627,-980,-394,-939,249,-555,-913,-1021,885,343,
                    }, 22, 15, false, new int[] {
                    -17,469,-271,-988,796,85,-688,-30,-841,-627,-980,-394,-939,249,-555,-913,-1021,885,343
                    }));
                    add(new SwapTestInput(new int[] {
                    614,-193,670,827,604,-293,369,955,673,536,-928,-370,-152,
                    }, 16, 16, false, new int[] {
                    614,-193,670,827,604,-293,369,955,673,536,-928,-370,-152
                    }));
                    add(new SwapTestInput(new int[] {
                    -272,-77,-166,-354,284,-855,-957,108,800,373,232,-979,120,408,-59,249,
                    }, 12, 14, true, new int[] {
                    -272,-77,-166,-354,284,-855,-957,108,800,373,232,-979,-59,408,120,249
                    }));
                    add(new SwapTestInput(new int[] {
                    809,537,-771,-200,-475,-502,485,152,-640,53,92,-387,780,957,-736,-354,992,-811,770,393,388,-467,687,488,-975,-122,346,-733,-181,522,
                    }, 33, 23, false, new int[] {
                    809,537,-771,-200,-475,-502,485,152,-640,53,92,-387,780,957,-736,-354,992,-811,770,393,388,-467,687,488,-975,-122,346,-733,-181,522
                    }));
                    add(new SwapTestInput(new int[] {
                    84,751,221,-762,1001,-15,287,-128,-552,729,-596,-645,720,
                    }, 14, 15, false, new int[] {
                    84,751,221,-762,1001,-15,287,-128,-552,729,-596,-645,720
                    }));
                    add(new SwapTestInput(new int[] {
                    -81,470,860,-85,40,226,814,969,595,321,472,-916,237,677,480,-896,-752,894,972,-817,518,879,-97,-358,928,-415,-694,-452,-718,
                    }, 25, 35, false, new int[] {
                    -81,470,860,-85,40,226,814,969,595,321,472,-916,237,677,480,-896,-752,894,972,-817,518,879,-97,-358,928,-415,-694,-452,-718
                    }));
                    add(new SwapTestInput(new int[] {
                    297,-744,892,-903,571,-584,248,18,-39,-81,952,-354,-868,711,-951,
                    }, 18, 11, false, new int[] {
                    297,-744,892,-903,571,-584,248,18,-39,-81,952,-354,-868,711,-951
                    }));
                    add(new SwapTestInput(new int[] {
                    633,948,10,-812,-803,414,732,-613,146,-158,22,-654,-925,-522,-748,-582,0,-22,-312,-964,182,778,-19,440,-212,-721,-121,139,-554,-608,788,
                    }, 34, 37, false, new int[] {
                    633,948,10,-812,-803,414,732,-613,146,-158,22,-654,-925,-522,-748,-582,0,-22,-312,-964,182,778,-19,440,-212,-721,-121,139,-554,-608,788
                    }));
                    add(new SwapTestInput(new int[] {
                    76,-514,274,-903,349,-52,-1002,323,-1011,-129,465,-508,-66,398,-1015,-5,-434,595,-467,-218,977,
                    }, 18, 25, false, new int[] {
                    76,-514,274,-903,349,-52,-1002,323,-1011,-129,465,-508,-66,398,-1015,-5,-434,595,-467,-218,977
                    }));
                    add(new SwapTestInput(new int[] {
                    70,525,470,155,-772,-540,954,-446,-471,-177,758,977,-205,-700,535,-427,-703,219,621,963,246,716,844,820,934,718,-12,
                    }, 30, 25, false, new int[] {
                    70,525,470,155,-772,-540,954,-446,-471,-177,758,977,-205,-700,535,-427,-703,219,621,963,246,716,844,820,934,718,-12
                    }));
                    add(new SwapTestInput(new int[] {
                    721,68,-815,571,-180,-104,-723,207,172,-704,-840,214,875,-994,949,-929,-230,417,-286,-376,-935,721,-91,391,401,-1023,103,900,
                    }, 25, 28, false, new int[] {
                    721,68,-815,571,-180,-104,-723,207,172,-704,-840,214,875,-994,949,-929,-230,417,-286,-376,-935,721,-91,391,401,-1023,103,900
                    }));
                    add(new SwapTestInput(new int[] {
                    -854,895,-680,371,-733,263,657,-137,-654,-62,352,201,51,1014,-460,896,341,262,793,738,-169,-513,936,149,256,-410,590,
                    }, 30, 22, false, new int[] {
                    -854,895,-680,371,-733,263,657,-137,-654,-62,352,201,51,1014,-460,896,341,262,793,738,-169,-513,936,149,256,-410,590
                    }));
                    add(new SwapTestInput(new int[] {
                    966,677,351,898,-281,681,-548,884,122,
                    }, 11, 9, false, new int[] {
                    966,677,351,898,-281,681,-548,884,122
                    }));
                    add(new SwapTestInput(new int[] {
                    -467,507,-28,-479,-470,-946,-305,-159,-880,-1010,813,234,-699,582,-716,739,-786,-222,53,-737,-952,
                    }, 26, 23, false, new int[] {
                    -467,507,-28,-479,-470,-946,-305,-159,-880,-1010,813,234,-699,582,-716,739,-786,-222,53,-737,-952
                    }));
                    add(new SwapTestInput(new int[] {
                    -639,-629,716,-753,-243,-32,354,-704,16,-434,143,24,853,971,566,773,969,-809,-616,-2,192,527,-483,-783,-484,-667,-429,-682,873,
                    }, 35, 35, false, new int[] {
                    -639,-629,716,-753,-243,-32,354,-704,16,-434,143,24,853,971,566,773,969,-809,-616,-2,192,527,-483,-783,-484,-667,-429,-682,873
                    }));
                    add(new SwapTestInput(new int[] {
                    -70,-347,-880,-403,409,-750,263,-217,-552,349,695,-210,
                    }, 10, 11, true, new int[] {
                    -70,-347,-880,-403,409,-750,263,-217,-552,349,-210,695
                    }));
                    add(new SwapTestInput(new int[] {
                    -701,-242,101,216,66,887,962,-186,627,-564,664,468,-958,-631,-1014,935,222,-64,935,-921,-479,-408,488,
                    }, 28, 18, false, new int[] {
                    -701,-242,101,216,66,887,962,-186,627,-564,664,468,-958,-631,-1014,935,222,-64,935,-921,-479,-408,488
                    }));
                    add(new SwapTestInput(new int[] {
                    511,-39,204,387,-392,557,574,-140,430,602,-334,-615,952,-774,-276,-997,685,-593,734,292,-254,847,-325,-368,
                    }, 18, 30, false, new int[] {
                    511,-39,204,387,-392,557,574,-140,430,602,-334,-615,952,-774,-276,-997,685,-593,734,292,-254,847,-325,-368
                    }));
                    add(new SwapTestInput(new int[] {
                    -338,-169,335,999,-236,384,213,-939,477,389,107,-642,919,-889,
                    }, 11, 15, false, new int[] {
                    -338,-169,335,999,-236,384,213,-939,477,389,107,-642,919,-889
                    }));
                    add(new SwapTestInput(new int[] {
                    399,-985,159,-354,-649,-782,-466,-477,
                    }, 10, 7, false, new int[] {
                    399,-985,159,-354,-649,-782,-466,-477
                    }));
                    add(new SwapTestInput(new int[] {
                    277,-475,-327,-351,-465,-1024,152,357,312,299,-844,-162,
                    }, 13, 11, false, new int[] {
                    277,-475,-327,-351,-465,-1024,152,357,312,299,-844,-162
                    }));
                    add(new SwapTestInput(new int[] {
                    -43,421,346,-400,773,-570,-599,282,-434,-535,693,-678,381,11,652,975,173,362,249,-642,-777,407,
                    }, 17, 27, false, new int[] {
                    -43,421,346,-400,773,-570,-599,282,-434,-535,693,-678,381,11,652,975,173,362,249,-642,-777,407
                    }));
                    /* END AUTOGENERATED CODE */
                }
            };
}
