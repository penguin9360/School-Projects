package edu.illinois.cs.cs125.mp6.lib;

import org.testng.annotations.Test;

import junit.framework.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * Test suite for the RecognizePhoto test.
 * <p>
 * The provided test suite is correct and complete. You should not need to modify it. However, you
 * should understand it.
 *
 * @see <a href="https://cs125.cs.illinois.edu/MP/6/">MP6 Documentation</a>
 */
public class RecognizePhotoTest {

    /** Timeout for image recognition tests. */
    private static final int RECOGNIZE_TEST_TIMEOUT = 100;

    /**
     * Test width extraction.
     */
    @Test(timeOut = RECOGNIZE_TEST_TIMEOUT)
    public void testWidth() {
        for (RecognizePhotoTestInput input : PRECOMPUTED_RESULTS) {
            Assert.assertEquals(RecognizePhoto.getWidth(input.json), input.width);
        }
    }

    /**
     * Test height extraction.
     */
    @Test(timeOut = RECOGNIZE_TEST_TIMEOUT)
    public void testHeight() {
        for (RecognizePhotoTestInput input : PRECOMPUTED_RESULTS) {
            Assert.assertEquals(RecognizePhoto.getHeight(input.json), input.height);
        }
    }

    /**
     * Test type extraction.
     */
    @Test(timeOut = RECOGNIZE_TEST_TIMEOUT)
    public void testFormat() {
        for (RecognizePhotoTestInput input : PRECOMPUTED_RESULTS) {
            Assert.assertEquals(RecognizePhoto.getFormat(input.json), input.type);
        }
    }

    /**
     * Test caption extraction.
     */
    @Test(timeOut = RECOGNIZE_TEST_TIMEOUT)
    public void testCaption() {
        for (RecognizePhotoTestInput input : PRECOMPUTED_RESULTS) {
            Assert.assertEquals(RecognizePhoto.getCaption(input.json), input.caption);
        }
    }

    /**
     * Test dog identification.
     */
    @Test(timeOut = RECOGNIZE_TEST_TIMEOUT)
    public void testDog() {
        for (RecognizePhotoTestInput input : PRECOMPUTED_RESULTS) {
            double lowConfidence = input.dogConfidence - 0.05;
            if (lowConfidence < 0.0) {
                lowConfidence = 0;
            }
            Assert.assertEquals(RecognizePhoto.isADog(input.json, lowConfidence),
                    input.isADog);
            if (input.isADog) {
                double highConfidence = input.dogConfidence + 0.05;
                if (highConfidence > 1.0) {
                    highConfidence = 1.0;
                }
                Assert.assertEquals(RecognizePhoto.isADog(input.json, highConfidence),
                        false);
            }
        }
    }

    /**
     * Test cat identification.
     */
    @Test(timeOut = RECOGNIZE_TEST_TIMEOUT)
    public void testCat() {
        for (RecognizePhotoTestInput input : PRECOMPUTED_RESULTS) {
            double lowConfidence = input.catConfidence - 0.05;
            if (lowConfidence < 0.0) {
                lowConfidence = 0;
            }
            Assert.assertEquals(RecognizePhoto.isACat(input.json, lowConfidence),
                    input.isACat);
            if (input.isACat) {
                double highConfidence = input.catConfidence + 0.05;
                if (highConfidence > 1.0) {
                    highConfidence = 1.0;
                }
                Assert.assertEquals(RecognizePhoto.isACat(input.json, highConfidence),
                        false);
            }
        }
    }


    /**
     * Class for storing pre-computed results and test inputs.
     */
    public static class RecognizePhotoTestInput {

        /** JSON string to use for testing. */
        String json;

        /** Precomputed image width. */
        int width;

        /** Precomputed image height. */
        int height;

        /** Precomputed image type. */
        String type;

        /** Precomputed image caption. */
        String caption;

        /** Precomputed whether the image is a dog. */
        boolean isADog;

        /** Precomputed dog confidence level. */
        double dogConfidence;

        /** Precomputed whether the image is a cat. */
        boolean isACat;

        /** Precomputed whether the image is a cat. */
        double catConfidence;

        /** Precomputed whether the image is Rick Astley. */
        boolean isRick;

        RecognizePhotoTestInput(String setJSON, int setWidth, int setHeight,
                                String setType, String setCaption,
                                boolean setIsADog, boolean setIsACat,
                                double setDogConfidence, double setCatConfidence,
                                boolean setIsRick) {
            json = setJSON;
            width = setWidth;
            height = setHeight;
            type = setType;
            caption = setCaption;
            isADog = setIsADog;
            isACat = setIsACat;
            dogConfidence = setDogConfidence;
            catConfidence = setCatConfidence;
            isRick = setIsRick;
        }
    }

    /** Pre-computed list of inputs to use for testing. */
    private static final List<RecognizePhotoTestInput> PRECOMPUTED_RESULTS = //
            new ArrayList<>();

    static {
        PRECOMPUTED_RESULTS.add(new RecognizePhotoTestInput(null, 0, 0,
                null, null, false, false, 0.9, 0.9, false));
        /* BEGIN AUTOGENERATED CODE */
        PRECOMPUTED_RESULTS.add(new RecognizePhotoTestInput("{" +
"  \"categories\": []," +
"  \"tags\": [" +
"    {" +
"      \"name\": \"grass\"," +
"      \"confidence\": 0.9999998807907104" +
"    }," +
"    {" +
"      \"name\": \"dog\"," +
"      \"confidence\": 0.999998927116394" +
"    }," +
"    {" +
"      \"name\": \"outdoor\"," +
"      \"confidence\": 0.9979544878005981" +
"    }," +
"    {" +
"      \"name\": \"frisbee\"," +
"      \"confidence\": 0.9407489895820618" +
"    }," +
"    {" +
"      \"name\": \"running\"," +
"      \"confidence\": 0.7554459571838379" +
"    }," +
"    {" +
"      \"name\": \"carrying\"," +
"      \"confidence\": 0.5024932026863098" +
"    }" +
"  ]," +
"  \"description\": {" +
"    \"tags\": [" +
"      \"grass\"," +
"      \"dog\"," +
"      \"outdoor\"," +
"      \"frisbee\"," +
"      \"running\"," +
"      \"mouth\"," +
"      \"field\"," +
"      \"brown\"," +
"      \"small\"," +
"      \"white\"," +
"      \"green\"," +
"      \"carrying\"," +
"      \"game\"," +
"      \"holding\"," +
"      \"sitting\"," +
"      \"playing\"," +
"      \"top\"," +
"      \"park\"," +
"      \"wearing\"," +
"      \"standing\"," +
"      \"air\"," +
"      \"blue\"," +
"      \"hat\"," +
"      \"ball\"," +
"      \"laying\"" +
"    ]," +
"    \"captions\": [" +
"      {" +
"        \"text\": \"a brown and white dog carrying a frisbee in its mouth\"," +
"        \"confidence\": 0.8047039093003463" +
"      }" +
"    ]" +
"  }," +
"  \"faces\": []," +
"  \"adult\": {" +
"    \"isAdultContent\": false," +
"    \"adultScore\": 0.007987337186932564," +
"    \"isRacyContent\": false," +
"    \"racyScore\": 0.027465907856822014" +
"  }," +
"  \"color\": {" +
"    \"dominantColorForeground\": \"White\"," +
"    \"dominantColorBackground\": \"Green\"," +
"    \"dominantColors\": [" +
"      \"Green\"" +
"    ]," +
"    \"accentColor\": \"AA7B21\"," +
"    \"isBwImg\": false" +
"  }," +
"  \"imageType\": {" +
"    \"clipArtType\": 0," +
"    \"lineDrawingType\": 0" +
"  }," +
"  \"requestId\": \"25299a8f-a1f4-46ee-b2bf-fce03377a7b8\"," +
"  \"metadata\": {" +
"    \"height\": 720," +
"    \"width\": 720," +
"    \"format\": \"Jpeg\"" +
"  }" +
"}",
        720, 720,
        "Jpeg", "a brown and white dog carrying a frisbee in its mouth",
        true, false,
        0.999998927116394, 0, false));
        PRECOMPUTED_RESULTS.add(new RecognizePhotoTestInput("{" +
"  \"categories\": []," +
"  \"tags\": [" +
"    {" +
"      \"name\": \"dog\"," +
"      \"confidence\": 0.9999785423278809" +
"    }," +
"    {" +
"      \"name\": \"animal\"," +
"      \"confidence\": 0.9558623433113098" +
"    }," +
"    {" +
"      \"name\": \"brown\"," +
"      \"confidence\": 0.8315029144287109" +
"    }," +
"    {" +
"      \"name\": \"mammal\"," +
"      \"confidence\": 0.7671582102775574," +
"      \"hint\": \"animal\"" +
"    }," +
"    {" +
"      \"name\": \"wild dog\"," +
"      \"confidence\": 0.3596915006637573," +
"      \"hint\": \"animal\"" +
"    }" +
"  ]," +
"  \"description\": {" +
"    \"tags\": [" +
"      \"dog\"," +
"      \"animal\"," +
"      \"brown\"," +
"      \"standing\"," +
"      \"grass\"," +
"      \"frisbee\"," +
"      \"large\"," +
"      \"mouth\"," +
"      \"playing\"," +
"      \"holding\"," +
"      \"walking\"," +
"      \"water\"," +
"      \"white\"," +
"      \"man\"" +
"    ]," +
"    \"captions\": [" +
"      {" +
"        \"text\": \"a brown and white dog playing with a frisbee\"," +
"        \"confidence\": 0.44900767867879127" +
"      }" +
"    ]" +
"  }," +
"  \"faces\": []," +
"  \"adult\": {" +
"    \"isAdultContent\": false," +
"    \"adultScore\": 0.038170840591192245," +
"    \"isRacyContent\": false," +
"    \"racyScore\": 0.054875634610652924" +
"  }," +
"  \"color\": {" +
"    \"dominantColorForeground\": \"White\"," +
"    \"dominantColorBackground\": \"White\"," +
"    \"dominantColors\": [" +
"      \"White\"" +
"    ]," +
"    \"accentColor\": \"8C653F\"," +
"    \"isBwImg\": false" +
"  }," +
"  \"imageType\": {" +
"    \"clipArtType\": 1," +
"    \"lineDrawingType\": 0" +
"  }," +
"  \"requestId\": \"df3c064a-2a0f-4b5f-b0ed-4fdb63827dc6\"," +
"  \"metadata\": {" +
"    \"height\": 558," +
"    \"width\": 780," +
"    \"format\": \"Jpeg\"" +
"  }" +
"}",
        780, 558,
        "Jpeg", "a brown and white dog playing with a frisbee",
        true, false,
        0.9999785423278809, 0, false));
        PRECOMPUTED_RESULTS.add(new RecognizePhotoTestInput("{" +
"  \"categories\": [" +
"    {" +
"      \"name\": \"animal_cat\"," +
"      \"score\": 0.99609375" +
"    }" +
"  ]," +
"  \"tags\": [" +
"    {" +
"      \"name\": \"cat\"," +
"      \"confidence\": 0.9859598875045776" +
"    }," +
"    {" +
"      \"name\": \"indoor\"," +
"      \"confidence\": 0.9647385478019714" +
"    }," +
"    {" +
"      \"name\": \"sitting\"," +
"      \"confidence\": 0.9601377248764038" +
"    }," +
"    {" +
"      \"name\": \"bed\"," +
"      \"confidence\": 0.7588865756988525" +
"    }," +
"    {" +
"      \"name\": \"laying\"," +
"      \"confidence\": 0.6615779995918274" +
"    }" +
"  ]," +
"  \"description\": {" +
"    \"tags\": [" +
"      \"cat\"," +
"      \"indoor\"," +
"      \"sitting\"," +
"      \"bed\"," +
"      \"top\"," +
"      \"laying\"," +
"      \"table\"," +
"      \"small\"," +
"      \"book\"," +
"      \"blanket\"," +
"      \"lying\"," +
"      \"white\"," +
"      \"gray\"," +
"      \"little\"," +
"      \"grey\"," +
"      \"black\"," +
"      \"blue\"," +
"      \"stuffed\"" +
"    ]," +
"    \"captions\": [" +
"      {" +
"        \"text\": \"a cat lying on a bed\"," +
"        \"confidence\": 0.8977793863763365" +
"      }" +
"    ]" +
"  }," +
"  \"faces\": []," +
"  \"adult\": {" +
"    \"isAdultContent\": false," +
"    \"adultScore\": 0.039412908256053925," +
"    \"isRacyContent\": false," +
"    \"racyScore\": 0.02028588578104973" +
"  }," +
"  \"color\": {" +
"    \"dominantColorForeground\": \"White\"," +
"    \"dominantColorBackground\": \"White\"," +
"    \"dominantColors\": [" +
"      \"White\"" +
"    ]," +
"    \"accentColor\": \"346797\"," +
"    \"isBwImg\": false" +
"  }," +
"  \"imageType\": {" +
"    \"clipArtType\": 0," +
"    \"lineDrawingType\": 0" +
"  }," +
"  \"requestId\": \"24602164-f7d7-40b3-b867-04b481dc788a\"," +
"  \"metadata\": {" +
"    \"height\": 683," +
"    \"width\": 1024," +
"    \"format\": \"Jpeg\"" +
"  }" +
"}",
        1024, 683,
        "Jpeg", "a cat lying on a bed",
        false, true,
        0, 0.9859598875045776, false));
        PRECOMPUTED_RESULTS.add(new RecognizePhotoTestInput("{" +
"  \"categories\": [" +
"    {" +
"      \"name\": \"animal_dog\"," +
"      \"score\": 0.9921875" +
"    }" +
"  ]," +
"  \"tags\": [" +
"    {" +
"      \"name\": \"wild dog\"," +
"      \"confidence\": 0.9955096244812012," +
"      \"hint\": \"animal\"" +
"    }," +
"    {" +
"      \"name\": \"animal\"," +
"      \"confidence\": 0.9916014075279236" +
"    }," +
"    {" +
"      \"name\": \"mammal\"," +
"      \"confidence\": 0.9663373231887817," +
"      \"hint\": \"animal\"" +
"    }" +
"  ]," +
"  \"description\": {" +
"    \"tags\": [" +
"      \"canine\"," +
"      \"animal\"," +
"      \"mammal\"," +
"      \"dog\"," +
"      \"sitting\"," +
"      \"black\"," +
"      \"small\"," +
"      \"looking\"," +
"      \"standing\"," +
"      \"brown\"," +
"      \"white\"," +
"      \"field\"," +
"      \"man\"," +
"      \"cat\"," +
"      \"water\"," +
"      \"air\"," +
"      \"laying\"," +
"      \"blue\"" +
"    ]," +
"    \"captions\": [" +
"      {" +
"        \"text\": \"a dog looking at the camera\"," +
"        \"confidence\": 0.8714152690937016" +
"      }" +
"    ]" +
"  }," +
"  \"faces\": [" +
"    {" +
"      \"age\": 42," +
"      \"gender\": \"Male\"," +
"      \"faceRectangle\": {" +
"        \"top\": 0," +
"        \"left\": 177," +
"        \"width\": 637," +
"        \"height\": 627" +
"      }" +
"    }" +
"  ]," +
"  \"adult\": {" +
"    \"isAdultContent\": false," +
"    \"adultScore\": 0.025861447677016258," +
"    \"isRacyContent\": false," +
"    \"racyScore\": 0.016596786677837372" +
"  }," +
"  \"color\": {" +
"    \"dominantColorForeground\": \"Black\"," +
"    \"dominantColorBackground\": \"White\"," +
"    \"dominantColors\": []," +
"    \"accentColor\": \"81684A\"," +
"    \"isBwImg\": false" +
"  }," +
"  \"imageType\": {" +
"    \"clipArtType\": 0," +
"    \"lineDrawingType\": 0" +
"  }," +
"  \"requestId\": \"970717fb-9cd0-4483-91f2-bf50144b0ed0\"," +
"  \"metadata\": {" +
"    \"height\": 678," +
"    \"width\": 1024," +
"    \"format\": \"Jpeg\"" +
"  }" +
"}",
        1024, 678,
        "Jpeg", "a dog looking at the camera",
        false, false,
        0, 0, false));
        PRECOMPUTED_RESULTS.add(new RecognizePhotoTestInput("{" +
"  \"categories\": [" +
"    {" +
"      \"name\": \"animal_cat\"," +
"      \"score\": 0.99609375" +
"    }" +
"  ]," +
"  \"tags\": [" +
"    {" +
"      \"name\": \"cat\"," +
"      \"confidence\": 0.9993564486503601" +
"    }," +
"    {" +
"      \"name\": \"ground\"," +
"      \"confidence\": 0.9976531863212585" +
"    }," +
"    {" +
"      \"name\": \"animal\"," +
"      \"confidence\": 0.9906987547874451" +
"    }," +
"    {" +
"      \"name\": \"outdoor\"," +
"      \"confidence\": 0.9892270565032959" +
"    }," +
"    {" +
"      \"name\": \"mammal\"," +
"      \"confidence\": 0.9353016018867493," +
"      \"hint\": \"animal\"" +
"    }," +
"    {" +
"      \"name\": \"domestic cat\"," +
"      \"confidence\": 0.5286005139350891" +
"    }," +
"    {" +
"      \"name\": \"grey\"," +
"      \"confidence\": 0.43479788303375244" +
"    }," +
"    {" +
"      \"name\": \"gray\"," +
"      \"confidence\": 0.40218064188957214" +
"    }," +
"    {" +
"      \"name\": \"close\"," +
"      \"confidence\": 0.26786619424819946" +
"    }," +
"    {" +
"      \"name\": \"dirt\"," +
"      \"confidence\": 0.26047849655151367" +
"    }," +
"    {" +
"      \"name\": \"stone\"," +
"      \"confidence\": 0.2142195999622345" +
"    }" +
"  ]," +
"  \"description\": {" +
"    \"tags\": [" +
"      \"cat\"," +
"      \"animal\"," +
"      \"outdoor\"," +
"      \"mammal\"," +
"      \"sitting\"," +
"      \"grass\"," +
"      \"camera\"," +
"      \"bench\"," +
"      \"looking\"," +
"      \"standing\"," +
"      \"wooden\"," +
"      \"grey\"," +
"      \"gray\"," +
"      \"top\"," +
"      \"small\"," +
"      \"close\"," +
"      \"dirt\"," +
"      \"brown\"," +
"      \"stone\"," +
"      \"face\"," +
"      \"green\"," +
"      \"park\"," +
"      \"white\"," +
"      \"eyes\"," +
"      \"field\"," +
"      \"laying\"" +
"    ]," +
"    \"captions\": [" +
"      {" +
"        \"text\": \"a cat that is looking at the camera\"," +
"        \"confidence\": 0.8948751570015577" +
"      }" +
"    ]" +
"  }," +
"  \"faces\": []," +
"  \"adult\": {" +
"    \"isAdultContent\": false," +
"    \"adultScore\": 0.029859604313969612," +
"    \"isRacyContent\": false," +
"    \"racyScore\": 0.03664281964302063" +
"  }," +
"  \"color\": {" +
"    \"dominantColorForeground\": \"Grey\"," +
"    \"dominantColorBackground\": \"Grey\"," +
"    \"dominantColors\": [" +
"      \"Grey\"" +
"    ]," +
"    \"accentColor\": \"526979\"," +
"    \"isBwImg\": false" +
"  }," +
"  \"imageType\": {" +
"    \"clipArtType\": 0," +
"    \"lineDrawingType\": 0" +
"  }," +
"  \"requestId\": \"cb20cec3-d9d5-4e83-9534-edffff6e5e2f\"," +
"  \"metadata\": {" +
"    \"height\": 694," +
"    \"width\": 960," +
"    \"format\": \"Jpeg\"" +
"  }" +
"}",
        960, 694,
        "Jpeg", "a cat that is looking at the camera",
        false, true,
        0, 0.9993564486503601, false));
        PRECOMPUTED_RESULTS.add(new RecognizePhotoTestInput("{" +
"  \"categories\": [" +
"    {" +
"      \"name\": \"animal_cat\"," +
"      \"score\": 0.953125" +
"    }" +
"  ]," +
"  \"tags\": [" +
"    {" +
"      \"name\": \"cat\"," +
"      \"confidence\": 0.9999885559082031" +
"    }," +
"    {" +
"      \"name\": \"indoor\"," +
"      \"confidence\": 0.9646970629692078" +
"    }," +
"    {" +
"      \"name\": \"laying\"," +
"      \"confidence\": 0.95644611120224" +
"    }," +
"    {" +
"      \"name\": \"domestic cat\"," +
"      \"confidence\": 0.9460797309875488" +
"    }," +
"    {" +
"      \"name\": \"animal\"," +
"      \"confidence\": 0.9379165172576904" +
"    }," +
"    {" +
"      \"name\": \"mammal\"," +
"      \"confidence\": 0.8935969471931458," +
"      \"hint\": \"animal\"" +
"    }," +
"    {" +
"      \"name\": \"white\"," +
"      \"confidence\": 0.7176649570465088" +
"    }," +
"    {" +
"      \"name\": \"close\"," +
"      \"confidence\": 0.20941013097763062" +
"    }" +
"  ]," +
"  \"description\": {" +
"    \"tags\": [" +
"      \"cat\"," +
"      \"indoor\"," +
"      \"laying\"," +
"      \"animal\"," +
"      \"mammal\"," +
"      \"top\"," +
"      \"white\"," +
"      \"laptop\"," +
"      \"looking\"," +
"      \"lying\"," +
"      \"bed\"," +
"      \"resting\"," +
"      \"head\"," +
"      \"brown\"," +
"      \"face\"," +
"      \"sleeping\"," +
"      \"close\"," +
"      \"computer\"," +
"      \"blue\"" +
"    ]," +
"    \"captions\": [" +
"      {" +
"        \"text\": \"a cat that is lying down and looking at the camera\"," +
"        \"confidence\": 0.9245854578095404" +
"      }" +
"    ]" +
"  }," +
"  \"faces\": []," +
"  \"adult\": {" +
"    \"isAdultContent\": false," +
"    \"adultScore\": 0.016627727076411247," +
"    \"isRacyContent\": false," +
"    \"racyScore\": 0.02098678983747959" +
"  }," +
"  \"color\": {" +
"    \"dominantColorForeground\": \"Brown\"," +
"    \"dominantColorBackground\": \"White\"," +
"    \"dominantColors\": [" +
"      \"White\"," +
"      \"Brown\"" +
"    ]," +
"    \"accentColor\": \"B68B15\"," +
"    \"isBwImg\": false" +
"  }," +
"  \"imageType\": {" +
"    \"clipArtType\": 0," +
"    \"lineDrawingType\": 0" +
"  }," +
"  \"requestId\": \"705b6e1d-681f-47b0-89ad-3ffa00fec441\"," +
"  \"metadata\": {" +
"    \"height\": 449," +
"    \"width\": 960," +
"    \"format\": \"Jpeg\"" +
"  }" +
"}",
        960, 449,
        "Jpeg", "a cat that is lying down and looking at the camera",
        false, true,
        0, 0.9999885559082031, false));
        PRECOMPUTED_RESULTS.add(new RecognizePhotoTestInput("{" +
"  \"categories\": [" +
"    {" +
"      \"name\": \"animal_cat\"," +
"      \"score\": 0.99609375" +
"    }" +
"  ]," +
"  \"tags\": [" +
"    {" +
"      \"name\": \"animal\"," +
"      \"confidence\": 0.9853090643882751" +
"    }," +
"    {" +
"      \"name\": \"mammal\"," +
"      \"confidence\": 0.9753100872039795," +
"      \"hint\": \"animal\"" +
"    }," +
"    {" +
"      \"name\": \"cat\"," +
"      \"confidence\": 0.962669849395752" +
"    }," +
"    {" +
"      \"name\": \"outdoor\"," +
"      \"confidence\": 0.9575168490409851" +
"    }," +
"    {" +
"      \"name\": \"domestic cat\"," +
"      \"confidence\": 0.9392395615577698" +
"    }," +
"    {" +
"      \"name\": \"laying\"," +
"      \"confidence\": 0.6036773920059204" +
"    }," +
"    {" +
"      \"name\": \"close\"," +
"      \"confidence\": 0.31522268056869507" +
"    }" +
"  ]," +
"  \"description\": {" +
"    \"tags\": [" +
"      \"animal\"," +
"      \"mammal\"," +
"      \"cat\"," +
"      \"outdoor\"," +
"      \"grass\"," +
"      \"sitting\"," +
"      \"laying\"," +
"      \"looking\"," +
"      \"green\"," +
"      \"close\"," +
"      \"top\"," +
"      \"front\"," +
"      \"brown\"," +
"      \"face\"," +
"      \"lying\"," +
"      \"field\"," +
"      \"yellow\"," +
"      \"black\"," +
"      \"standing\"," +
"      \"eyes\"," +
"      \"mouth\"," +
"      \"blue\"" +
"    ]," +
"    \"captions\": [" +
"      {" +
"        \"text\": \"a close up of a cat\"," +
"        \"confidence\": 0.960588274559504" +
"      }" +
"    ]" +
"  }," +
"  \"faces\": []," +
"  \"adult\": {" +
"    \"isAdultContent\": false," +
"    \"adultScore\": 0.18402142822742462," +
"    \"isRacyContent\": false," +
"    \"racyScore\": 0.10651963204145432" +
"  }," +
"  \"color\": {" +
"    \"dominantColorForeground\": \"Brown\"," +
"    \"dominantColorBackground\": \"White\"," +
"    \"dominantColors\": [" +
"      \"Brown\"," +
"      \"White\"" +
"    ]," +
"    \"accentColor\": \"644732\"," +
"    \"isBwImg\": false" +
"  }," +
"  \"imageType\": {" +
"    \"clipArtType\": 0," +
"    \"lineDrawingType\": 0" +
"  }," +
"  \"requestId\": \"e4a33db3-1ed0-4c07-9e00-8f93e3ab6fde\"," +
"  \"metadata\": {" +
"    \"height\": 683," +
"    \"width\": 1024," +
"    \"format\": \"Jpeg\"" +
"  }" +
"}",
        1024, 683,
        "Jpeg", "a close up of a cat",
        false, true,
        0, 0.962669849395752, false));
        PRECOMPUTED_RESULTS.add(new RecognizePhotoTestInput("{" +
"  \"categories\": [" +
"    {" +
"      \"name\": \"animal_dog\"," +
"      \"score\": 0.99609375" +
"    }" +
"  ]," +
"  \"tags\": [" +
"    {" +
"      \"name\": \"dog\"," +
"      \"confidence\": 0.9999997615814209" +
"    }," +
"    {" +
"      \"name\": \"indoor\"," +
"      \"confidence\": 0.9962719678878784" +
"    }," +
"    {" +
"      \"name\": \"sitting\"," +
"      \"confidence\": 0.9837114214897156" +
"    }," +
"    {" +
"      \"name\": \"brown\"," +
"      \"confidence\": 0.9766542315483093" +
"    }," +
"    {" +
"      \"name\": \"animal\"," +
"      \"confidence\": 0.9186526536941528" +
"    }," +
"    {" +
"      \"name\": \"looking\"," +
"      \"confidence\": 0.9071006178855896" +
"    }," +
"    {" +
"      \"name\": \"tan\"," +
"      \"confidence\": 0.6632649898529053" +
"    }," +
"    {" +
"      \"name\": \"mammal\"," +
"      \"confidence\": 0.644765317440033," +
"      \"hint\": \"animal\"" +
"    }," +
"    {" +
"      \"name\": \"yellow\"," +
"      \"confidence\": 0.641718864440918" +
"    }," +
"    {" +
"      \"name\": \"staring\"," +
"      \"confidence\": 0.4861973524093628" +
"    }" +
"  ]," +
"  \"description\": {" +
"    \"tags\": [" +
"      \"dog\"," +
"      \"indoor\"," +
"      \"sitting\"," +
"      \"brown\"," +
"      \"animal\"," +
"      \"looking\"," +
"      \"wearing\"," +
"      \"tan\"," +
"      \"yellow\"," +
"      \"standing\"," +
"      \"staring\"," +
"      \"sticking\"," +
"      \"head\"," +
"      \"large\"," +
"      \"hanging\"," +
"      \"hat\"," +
"      \"orange\"," +
"      \"mouth\"," +
"      \"front\"," +
"      \"seat\"," +
"      \"black\"," +
"      \"laying\"," +
"      \"mirror\"," +
"      \"eyes\"," +
"      \"white\"," +
"      \"living\"," +
"      \"blue\"," +
"      \"room\"" +
"    ]," +
"    \"captions\": [" +
"      {" +
"        \"text\": \"a brown and white dog looking at the camera\"," +
"        \"confidence\": 0.8428045780333479" +
"      }" +
"    ]" +
"  }," +
"  \"faces\": []," +
"  \"adult\": {" +
"    \"isAdultContent\": false," +
"    \"adultScore\": 0.00886447262018919," +
"    \"isRacyContent\": false," +
"    \"racyScore\": 0.009068120270967484" +
"  }," +
"  \"color\": {" +
"    \"dominantColorForeground\": \"Brown\"," +
"    \"dominantColorBackground\": \"Grey\"," +
"    \"dominantColors\": [" +
"      \"Brown\"," +
"      \"Grey\"," +
"      \"Black\"" +
"    ]," +
"    \"accentColor\": \"793C18\"," +
"    \"isBwImg\": false" +
"  }," +
"  \"imageType\": {" +
"    \"clipArtType\": 0," +
"    \"lineDrawingType\": 0" +
"  }," +
"  \"requestId\": \"ae05893a-440e-4a16-b344-e34bdb1e25d4\"," +
"  \"metadata\": {" +
"    \"height\": 681," +
"    \"width\": 1024," +
"    \"format\": \"Jpeg\"" +
"  }" +
"}",
        1024, 681,
        "Jpeg", "a brown and white dog looking at the camera",
        true, false,
        0.9999997615814209, 0, false));
        PRECOMPUTED_RESULTS.add(new RecognizePhotoTestInput("{" +
"  \"categories\": [" +
"    {" +
"      \"name\": \"animal_dog\"," +
"      \"score\": 0.99609375" +
"    }" +
"  ]," +
"  \"tags\": [" +
"    {" +
"      \"name\": \"dog\"," +
"      \"confidence\": 0.9992470741271973" +
"    }," +
"    {" +
"      \"name\": \"animal\"," +
"      \"confidence\": 0.8903746604919434" +
"    }," +
"    {" +
"      \"name\": \"mammal\"," +
"      \"confidence\": 0.8541259169578552," +
"      \"hint\": \"animal\"" +
"    }," +
"    {" +
"      \"name\": \"brown\"," +
"      \"confidence\": 0.8377056121826172" +
"    }," +
"    {" +
"      \"name\": \"bath\"," +
"      \"confidence\": 0.17544159293174744" +
"    }" +
"  ]," +
"  \"description\": {" +
"    \"tags\": [" +
"      \"dog\"," +
"      \"water\"," +
"      \"animal\"," +
"      \"mammal\"," +
"      \"brown\"," +
"      \"sitting\"," +
"      \"looking\"," +
"      \"standing\"," +
"      \"small\"," +
"      \"mouth\"," +
"      \"boat\"," +
"      \"hanging\"," +
"      \"reflection\"," +
"      \"mirror\"," +
"      \"yellow\"," +
"      \"air\"," +
"      \"white\"," +
"      \"tub\"," +
"      \"bathtub\"" +
"    ]," +
"    \"captions\": [" +
"      {" +
"        \"text\": \"a dog hanging out of the water\"," +
"        \"confidence\": 0.7750095489965079" +
"      }" +
"    ]" +
"  }," +
"  \"faces\": []," +
"  \"adult\": {" +
"    \"isAdultContent\": false," +
"    \"adultScore\": 0.007349452469497919," +
"    \"isRacyContent\": false," +
"    \"racyScore\": 0.01120046153664589" +
"  }," +
"  \"color\": {" +
"    \"dominantColorForeground\": \"Brown\"," +
"    \"dominantColorBackground\": \"White\"," +
"    \"dominantColors\": [" +
"      \"White\"," +
"      \"Brown\"" +
"    ]," +
"    \"accentColor\": \"A56626\"," +
"    \"isBwImg\": false" +
"  }," +
"  \"imageType\": {" +
"    \"clipArtType\": 1," +
"    \"lineDrawingType\": 0" +
"  }," +
"  \"requestId\": \"b38f44fe-bf96-41f0-9ffd-7a9dbffc8885\"," +
"  \"metadata\": {" +
"    \"height\": 604," +
"    \"width\": 1024," +
"    \"format\": \"Jpeg\"" +
"  }" +
"}",
        1024, 604,
        "Jpeg", "a dog hanging out of the water",
        true, false,
        0.9992470741271973, 0, false));
        PRECOMPUTED_RESULTS.add(new RecognizePhotoTestInput("{" +
"  \"categories\": []," +
"  \"tags\": [" +
"    {" +
"      \"name\": \"cat\"," +
"      \"confidence\": 0.99893718957901" +
"    }," +
"    {" +
"      \"name\": \"indoor\"," +
"      \"confidence\": 0.9307380318641663" +
"    }," +
"    {" +
"      \"name\": \"animal\"," +
"      \"confidence\": 0.9198971390724182" +
"    }," +
"    {" +
"      \"name\": \"mammal\"," +
"      \"confidence\": 0.9128736853599548," +
"      \"hint\": \"animal\"" +
"    }," +
"    {" +
"      \"name\": \"laying\"," +
"      \"confidence\": 0.827452540397644" +
"    }," +
"    {" +
"      \"name\": \"domestic cat\"," +
"      \"confidence\": 0.723308265209198" +
"    }" +
"  ]," +
"  \"description\": {" +
"    \"tags\": [" +
"      \"cat\"," +
"      \"indoor\"," +
"      \"animal\"," +
"      \"mammal\"," +
"      \"laying\"," +
"      \"white\"," +
"      \"top\"," +
"      \"looking\"," +
"      \"lying\"," +
"      \"gray\"," +
"      \"brown\"," +
"      \"bed\"," +
"      \"close\"," +
"      \"sleeping\"" +
"    ]," +
"    \"captions\": [" +
"      {" +
"        \"text\": \"a close up of a cat\"," +
"        \"confidence\": 0.9077087662193325" +
"      }" +
"    ]" +
"  }," +
"  \"faces\": []," +
"  \"adult\": {" +
"    \"isAdultContent\": false," +
"    \"adultScore\": 0.023592308163642883," +
"    \"isRacyContent\": false," +
"    \"racyScore\": 0.01566370762884617" +
"  }," +
"  \"color\": {" +
"    \"dominantColorForeground\": \"White\"," +
"    \"dominantColorBackground\": \"White\"," +
"    \"dominantColors\": [" +
"      \"White\"" +
"    ]," +
"    \"accentColor\": \"934F38\"," +
"    \"isBwImg\": false" +
"  }," +
"  \"imageType\": {" +
"    \"clipArtType\": 0," +
"    \"lineDrawingType\": 0" +
"  }," +
"  \"requestId\": \"f03926ec-3b8b-4aee-9443-0e022efa0c6e\"," +
"  \"metadata\": {" +
"    \"height\": 2592," +
"    \"width\": 3888," +
"    \"format\": \"Jpeg\"" +
"  }" +
"}",
        3888, 2592,
        "Jpeg", "a close up of a cat",
        false, true,
        0, 0.99893718957901, false));
        /* END AUTOGENERATED CODE */
    }
}
