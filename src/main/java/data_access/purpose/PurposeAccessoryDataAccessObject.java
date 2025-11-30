package data_access.purpose;

import data_access.outfit_suggestion.GeminiConfig;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import use_case.purpose.PurposeAccessoryDataAccessInterface;

public class PurposeAccessoryDataAccessObject implements PurposeAccessoryDataAccessInterface {

    private final OkHttpClient client;
    private static final MediaType JSON_MEDIA_TYPE =
            MediaType.get("application/json; charset=utf-8");

    private static final String GEMINI_API_BASE =
            "https://generativelanguage.googleapis.com/v1/models/gemini-2.5-flash-lite:generateContent";

    public PurposeAccessoryDataAccessObject() {
        this.client = new OkHttpClient();
    }

    @Override
    public String generateAccessorySuggestions(String purpose) {
        try {
            String prompt = buildPrompt(purpose);
            String jsonBody = buildGeminiRequest(prompt);

            String url = GEMINI_API_BASE + "?key=" + GeminiConfig.API_KEY;

            RequestBody body = RequestBody.create(jsonBody, JSON_MEDIA_TYPE);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    System.err.println("Gemini API failed: " + response.code());
                    if (response.body() != null) {
                        System.err.println("Response: " + response.body().string());
                    }
                    return null;
                }

                if (response.body() == null) {
                    System.err.println("Gemini API returned empty body");
                    return null;
                }

                String responseBody = response.body().string();
                return parseGeminiResponse(responseBody);
            }
        } catch (Exception e) {
            System.err.println("Error generating accessory suggestions: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private String buildPrompt(String purpose) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("You are a personal fashion stylist.\n");
        prompt.append("The user is going out for the following purpose:\n");
        prompt.append("\"").append(purpose).append("\"\n\n");
        prompt.append("Your task:\n");
        prompt.append("- Suggest accessories ONLY, not clothing.\n");
        prompt.append("- Focus on items like: bags, hats, watches, sunglasses, jewelry, scarves, belts, gloves, umbrellas, hair accessories, etc.\n");
        prompt.append("- Do NOT suggest shirts, pants, dresses, shoes, or any main clothing items.\n");
        prompt.append("- Tailor the accessories to the purpose.\n\n");
        prompt.append("Response format:\n");
        prompt.append("- Use a short title line like: \"Recommended accessories:\"\n");
        prompt.append("- Then give 5 bullet points.\n");
        prompt.append("- Each bullet: one accessory + a brief explanation.\n");
        prompt.append("- Use simple plain text; bullets can start with \"- \".\n");

        return prompt.toString();
    }

    private String buildGeminiRequest(String prompt) {
        JSONObject request = new JSONObject();

        JSONArray contents = new JSONArray();
        JSONObject content = new JSONObject();

        JSONArray parts = new JSONArray();
        JSONObject part = new JSONObject();
        part.put("text", prompt);
        parts.put(part);

        content.put("parts", parts);
        contents.put(content);

        request.put("contents", contents);

        return request.toString();
    }

    private String parseGeminiResponse(String responseBody) {
        try {
            JSONObject json = new JSONObject(responseBody);

            JSONArray candidates = json.getJSONArray("candidates");
            if (candidates.length() == 0) {
                return null;
            }

            JSONObject firstCandidate = candidates.getJSONObject(0);
            JSONObject content = firstCandidate.getJSONObject("content");
            JSONArray parts = content.getJSONArray("parts");

            if (parts.length() == 0) {
                return null;
            }

            return parts.getJSONObject(0).getString("text");

        } catch (Exception e) {
            System.err.println("Error parsing Gemini purpose response: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
