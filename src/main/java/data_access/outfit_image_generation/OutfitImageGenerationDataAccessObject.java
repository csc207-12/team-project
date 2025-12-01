package data_access.outfit_image_generation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import data_access.outfit_suggestion.GeminiConfig;
import entity.User;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import use_case.outfit_image_generation.OutfitImageGenerationDataAccessInterface;

/**
 * Data access object responsible for calling Gemini API and converting results into Base64 images.
 */
public class OutfitImageGenerationDataAccessObject
        implements OutfitImageGenerationDataAccessInterface {

    private static final MediaType JSON_MEDIA =
            MediaType.get("application/json; charset=utf-8");

    private static final String API_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/"
                    + "gemini-2.5-flash-image:generateContent";

    private static final String API_KEY = GeminiConfig.API_KEY;

    /**
     * Constant key for JSON inlineData, media, or fileData blocks.
     */
    private static final String FIELD_DATA = "data";

    private final OkHttpClient client = new OkHttpClient();
    private final User user;

    /**
     * Constructs a new data access object for outfit image generation.
     *
     * @param user the current logged-in user
     */
    public OutfitImageGenerationDataAccessObject(User user) {
        this.user = user;
    }

    /**
     * Generates multiple outfit images from raw text prompts.
     *
     * @param outfitsRawText list of outfit descriptions
     * @return list of base64-encoded PNG images
     */
    @Override
    public List<String> generateImages(List<String> outfitsRawText) {

        final List<String> cleanedOutfits = outfitsRawText;
        final List<String> images = new ArrayList<>();

        for (String outfit : cleanedOutfits) {
            if (outfit == null || outfit.isBlank()) {
                continue;
            }

            final String genderPrompt = buildGenderPrompt();
            final String prompt = buildPrompt(genderPrompt, outfit);

            String base64 = callGemini(prompt);
            if (base64 == null || base64.isBlank()) {
                base64 = callGemini(prompt);
            }

            if (base64 != null && !base64.isBlank()) {
                images.add(base64);
            }
        }

        return images;
    }

    /**
     * Builds the gender part of the model prompt.
     *
     * @return text like {@code "a man"} or {@code "a woman"}
     */
    private String buildGenderPrompt() {
        final String result;

        if (user.getGender().equalsIgnoreCase("female")) {
            result = "a woman";
        }
        else {
            result = "a man";
        }

        return result;
    }

    /**
     * Builds the full model prompt.
     *
     * @param gender text like "a man" or "a woman"
     * @param outfit clothing description
     * @return full Gemini prompt
     */
    private String buildPrompt(String gender, String outfit) {
        return "Generate a highly realistic full-body 4K street photo of "
                + gender
                + " wearing the following outfit prompt: "
                + outfit
                + ". Use only clothing items mentioned. "
                + "Urban Toronto street photography style.";
    }

    /**
     * Sends a POST request to Gemini with the given prompt.
     *
     * @param prompt text prompt
     * @return base64 image string, or {@code null} if the call failed
     */
    private String callGemini(String prompt) {

        String result = null;

        try {
            final JSONObject requestJson = new JSONObject()
                    .put("contents", new JSONArray()
                            .put(new JSONObject()
                                    .put("parts", new JSONArray()
                                            .put(new JSONObject().put("text", prompt)))));

            final RequestBody body = RequestBody.create(
                    requestJson.toString(),
                    JSON_MEDIA
            );

            final Request request = new Request.Builder()
                    .url(API_URL + "?key=" + API_KEY)
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {

                if (response.isSuccessful() && response.body() != null) {
                    final String resp = response.body().string();
                    result = extractImageFromResponse(resp);
                }
            }
        }
        catch (IOException ex) {
            // Avoid printStackTrace() because of MatchXpath rule.
            System.err.println("Gemini API call failed: " + ex.getMessage());
        }

        return result;
    }

    /**
     * Extracts the Base64 image from the model response.
     *
     * @param jsonStr raw JSON string
     * @return base64 encoded PNG data, or {@code null} if not found
     */
    private String extractImageFromResponse(String jsonStr) {
        final JSONObject root = new JSONObject(jsonStr);
        return findBase64(root);
    }

    /**
     * Recursively searches for a base64 data field in a JSON node.
     *
     * @param node the current JSON node
     * @return base64 string if found, otherwise {@code null}
     */
    private String findBase64(Object node) {
        String result = null;

        if (node instanceof JSONObject) {
            result = searchJsonObject((JSONObject) node);
        }
        else if (node instanceof JSONArray) {
            result = searchJsonArray((JSONArray) node);
        }

        return result;
    }

    /**
     * Searches all keys of a JSON object for image data.
     *
     * @param json JSON object to inspect
     * @return base64 string if found, otherwise {@code null}
     */
    private String searchJsonObject(JSONObject json) {
        String result = null;

        for (String key : json.keySet()) {

            if (result != null) {
                break;
            }

            if (isDataContainer(key, json)) {
                result = json.getJSONObject(key).getString(FIELD_DATA);
            }
            else {
                final Object val = json.get(key);
                final String found = findBase64(val);
                if (found != null) {
                    result = found;
                }
            }
        }

        return result;
    }

    /**
     * Checks whether the given key in the JSON object contains image data.
     *
     * @param key  JSON key to check
     * @param json JSON object
     * @return {@code true} if this key contains a data field, otherwise {@code false}
     */
    private boolean isDataContainer(String key, JSONObject json) {
        boolean result = false;

        if (json.has(key) && json.get(key) instanceof JSONObject) {
            final boolean isImageKey = "inlineData".equals(key)
                    || "media".equals(key)
                    || "fileData".equals(key);
            final boolean hasData = json.getJSONObject(key).has(FIELD_DATA);
            result = isImageKey && hasData;
        }

        return result;
    }

    /**
     * Searches a JSON array for image data.
     *
     * @param arr JSON array to inspect
     * @return base64 string if found, otherwise {@code null}
     */
    private String searchJsonArray(JSONArray arr) {
        String result = null;

        for (int i = 0; i < arr.length() && result == null; i++) {
            final String found = findBase64(arr.get(i));
            if (found != null) {
                result = found;
            }
        }

        return result;
    }

    /**
     * Saves a base64-encoded PNG image to disk.
     *
     * @param base64   base64 encoded PNG image
     * @param filename output filename (including path)
     */
    public static void saveBase64ToPng(String base64, String filename) {
        try {
            final byte[] decoded = Base64.getDecoder().decode(base64);
            Files.write(Paths.get(filename), decoded);
        }
        catch (IOException ex) {
            System.err.println("Failed to save image: " + ex.getMessage());
        }
    }
}
