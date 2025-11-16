package data_access;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class GeminiClient {

    private static final String GEMINI_ENDPOINT =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";

    private final String apiKey;
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public GeminiClient(String apiKey) {
        this.apiKey = apiKey;
    }

    public String generateText(String prompt) {
        try {
            JSONObject textObj = new JSONObject().put("text", prompt);
            JSONObject partObj = new JSONObject().put("parts", new JSONArray().put(textObj));
            JSONObject contentObj = new JSONObject().put("contents", new JSONArray().put(partObj));

            String body = contentObj.toString();

            String url = GEMINI_ENDPOINT + "?key=" + apiKey;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                return "AI is not working right now (HTTP " + response.statusCode() + ").";
            }

            JSONObject root = new JSONObject(response.body());
            JSONArray candidates = root.optJSONArray("candidates");
            if (candidates == null || candidates.isEmpty()) {
                return "AI returned no candidates.";
            }

            JSONObject content = candidates.getJSONObject(0).getJSONObject("content");
            JSONArray parts = content.optJSONArray("parts");

            if (parts == null || parts.isEmpty()) {
                return "AI returned empty content.";
            }

            return parts.getJSONObject(0).optString("text", "").trim();

        } catch (Exception e) {
            e.printStackTrace();
            return "AI is not working right now (exception).";
        }
    }
}
