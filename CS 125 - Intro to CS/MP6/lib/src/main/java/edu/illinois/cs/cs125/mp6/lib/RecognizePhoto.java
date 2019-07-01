package edu.illinois.cs.cs125.mp6.lib;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * brttrh.
 */
public final class RecognizePhoto {

    /**
     * egeggr.
     */
    public RecognizePhoto() {

    }

    /**
     * Get the image width.
     *
     * @param json the JSON string returned by the Microsoft Cognitive Services API
     * @return the width of the image or 0 on failure
     */
    public static int getWidth(final String json) {
        if (json == null) {
            return 0;
        }
        JsonParser parser = new JsonParser();
        JsonObject result = parser.parse(json).getAsJsonObject();
        JsonObject metadata = result.get("metadata").getAsJsonObject();
        int width = metadata.get("width").getAsInt();
        return width;
    }

    /**
     * Get the image height.
     *
     * @param json the JSON string returned by the Microsoft Cognitive Services API
     * @return the width of the image or 0 on failure
     */
    public static int getHeight(final String json) {
        if (json == null) {
            return 0;
        }
        JsonParser parser = new JsonParser();
        JsonObject result = parser.parse(json).getAsJsonObject();
        JsonObject metadata = result.get("metadata").getAsJsonObject();
        int height = metadata.get("height").getAsInt();
        return height;
    }

    /**
     * Get the image file type.
     *
     * @param json the JSON string returned by the Microsoft Cognitive Services API
     * @return the type of the image or null
     */
    public static String getFormat(final String json) {
        if (json == null) {
            return json;
        }
        JsonParser parser = new JsonParser();
        JsonObject result = parser.parse(json).getAsJsonObject();
        JsonObject metadata = result.get("metadata").getAsJsonObject();
        String format = metadata.get("format").getAsString();
        return format;
    }

    /**
     * egeegeg.
     * @param json jrp[erg.
     * @return kgeope.
     */
    public static String getCaption(final String json) {
        if (json == null) {
            return json;
        }
        JsonParser parser = new JsonParser();
        JsonObject result = parser.parse(json).getAsJsonObject();
        JsonObject description = result.get("description").getAsJsonObject();
        JsonArray captions = description.get("captions").getAsJsonArray();
        String yu = captions.get(0).getAsJsonObject().get("text").getAsString();
        return yu;
    }

    /**
     * eoingioe.
     * @param json egegejgi.
     * @param minConfidence djgeiogog.
     * @return ejgroeieg.
     */
    public static boolean isADog(final String json, final double minConfidence) {
        if (json == null) {
            return false;
        }
        JsonParser parser = new JsonParser();
        JsonObject parse = parser.parse(json).getAsJsonObject();
        JsonArray tags = parse.getAsJsonArray("tags");
        for (int i = 0; i < tags.size(); i++) {
            if (tags.get(i).getAsJsonObject().get("name").getAsString().equals("dog") && tags.
                    get(i).getAsJsonObject().get("confidence").getAsDouble() >= minConfidence) {
                return true;
            }
        }
        return false;
    }

    /**
     * gehegheoeih.
     * @param json rejgoige.
     * @param minConfidence griogjreo.
     * @return regjgi.
     */
    public static boolean isACat(final String json, final double minConfidence) {
        if (json == null) {
            return false;
        }
        JsonParser parser = new JsonParser();
        JsonObject parse = parser.parse(json).getAsJsonObject();
        JsonArray tags = parse.getAsJsonArray("tags");
        for (int i = 0; i < tags.size(); i++) {
            if (tags.get(i).getAsJsonObject().get("name").getAsString().equals("cat") && tags.
                    get(i).getAsJsonObject().get("confidence").getAsDouble() >= minConfidence) {
                return true;
            }
        }
        return false;
    }

    /**
     * goeoiejie.
     * @param json gneoneo.
     * @return erjgiog.
     */
    public static boolean isRick(final String json) {
        if (json == null) {
            return false;
        }
        if (json.contains("Rick Astley")) {
            return true;
        }
        return false;
    }
}
