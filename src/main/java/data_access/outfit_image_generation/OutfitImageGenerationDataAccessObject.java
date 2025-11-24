package data_access.outfit_image_generation;

import data_access.outfit_suggestion.GeminiConfig;
import entity.User;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import use_case.outfit_image_generation.OutfitImageGenerationDataAccessInterface;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class OutfitImageGenerationDataAccessObject implements OutfitImageGenerationDataAccessInterface {

    private static final MediaType JSON_MEDIA =
            MediaType.get("application/json; charset=utf-8");

    private static final String API_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-image:generateContent";

    private static final String API_KEY = GeminiConfig.API_KEY;

    private final OkHttpClient client = new OkHttpClient();
    private final User user;

    public OutfitImageGenerationDataAccessObject(User user) {
        this.user = user;
    }

    @Override
    public List<String> generateImages(List<String> outfitsRawText) {

//        List<String> cleanedOutfits = extractClothingItems(outfitsRawText);
        List<String> cleanedOutfits = outfitsRawText;
        List<String> images = new ArrayList<>();

        for (String outfit : cleanedOutfits) {
            if (outfit == null || outfit.isBlank()) continue;

            String genderPrompt = user.getGender().equalsIgnoreCase("female")
                    ? "a woman"
                    : "a man";

            String prompt =
                    "Generate a highly realistic full-body 4K street photo of "
                            + genderPrompt + " wearing the following outfit prompt: "
                            + outfit + ". Be sure to only use clothing items mentioned in the prompt."
                            + "Style the photo as urban Toronto street photography, natural lighting, high detail, neutral ethnicity.";

            String base64 = callGemini(prompt);

            if (base64 == null || base64.isBlank()) {
                System.out.println("⚠ No image returned. Retrying...");
                base64 = callGemini(prompt);
            }

            if (base64 != null && !base64.isBlank()) {
                images.add(base64);
            }
        }

        return images;
    }

    private List<String> extractClothingItems(List<String> blocks) {
        List<String> results = new ArrayList<>();

        for (String block : blocks) {
            String lower = block.toLowerCase();

            int idx = lower.indexOf("clothing items:");
            if (idx != -1) {
                String clothingLine = block.substring(idx + "clothing items:".length())
                        .split("\n")[0]
                        .trim()
                        .replace("*", "")
                        .replace("-", "")
                        .replace("•", "")
                        .trim();
                results.add(clothingLine);
                continue;
            }
//            String[] lines = block.split("\n");
//            String fallback = lines[0].trim();
//
//            if (fallback.length() < 5) {
//                fallback = "T-shirt, jeans, hoodie, sneakers";
//            }
//
//            results.add(fallback);
        }

        return results;
    }

    private String callGemini(String prompt) {
        try {
            JSONObject requestJson = new JSONObject()
                    .put("contents", new JSONArray()
                            .put(new JSONObject()
                                    .put("parts", new JSONArray()
                                            .put(new JSONObject().put("text", prompt))
                                    )
                            )
                    );

            RequestBody body = RequestBody.create(requestJson.toString(), JSON_MEDIA);

            Request request = new Request.Builder()
                    .url(API_URL + "?key=" + API_KEY)
                    .post(body)
                    .build();

            Response response = client.newCall(request).execute();

            System.out.println("Gemini API Response Code: " + response.code());

            if (!response.isSuccessful() || response.body() == null) {
                System.err.println("Gemini API Error: " + response.code());
                return null;
            }

            String resp = response.body().string();
            return extractImageFromResponse(resp);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String extractImageFromResponse(String jsonStr) {
        try {
            JSONObject root = new JSONObject(jsonStr);
            return findBase64(root);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String findBase64(Object node) {
        try {
            if (node instanceof JSONObject) {
                JSONObject json = (JSONObject) node;
                for (String key : json.keySet()) {
                    Object val = json.get(key);

                    if (key.equals("inlineData") && val instanceof JSONObject) {
                        JSONObject d = (JSONObject) val;
                        if (d.has("data")) return d.getString("data");
                    }
                    if (key.equals("media") && val instanceof JSONObject) {
                        JSONObject d = (JSONObject) val;
                        if (d.has("data")) return d.getString("data");
                    }
                    if (key.equals("fileData") && val instanceof JSONObject) {
                        JSONObject d = (JSONObject) val;
                        if (d.has("data")) return d.getString("data");
                    }

                    String found = findBase64(val);
                    if (found != null) return found;
                }
            }

            if (node instanceof JSONArray) {
                JSONArray arr = (JSONArray) node;
                for (int i = 0; i < arr.length(); i++) {
                    String found = findBase64(arr.get(i));
                    if (found != null) return found;
                }
            }

        } catch (Exception ignore) {}
        return null;
    }

    public static void saveBase64ToPNG(String base64, String filename) {
        try {
            byte[] decoded = Base64.getDecoder().decode(base64);
            Files.write(Paths.get(filename), decoded);
            System.out.println("Saved image → " + filename);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
