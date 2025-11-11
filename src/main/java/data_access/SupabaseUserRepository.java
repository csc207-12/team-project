package data_access;

import entity.User;
import org.json.JSONArray;
import use_case.UserRepository;
import okhttp3.*;


import java.io.IOException;
import java.util.Map;

/**
 * Repository that saves user profiles to Supabase database using OkHttp.
 * No local caching - all operations go directly to Supabase.
 */
public class SupabaseUserRepository implements UserRepository {
    private final String supabaseUrl;
    private final String apiKey;
    private final OkHttpClient client;
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public SupabaseUserRepository() {
        this.supabaseUrl = SupabaseConfig.SUPABASE_URL;
        this.apiKey = SupabaseConfig.SUPABASE_API_KEY;
        this.client = new OkHttpClient();
    }

    @Override
    public void save(User user) {
        // Upload directly to Supabase
        uploadToSupabase(user);
    }

    @Override
    public User findByUsername(String username) {
        return null;
    }

    @Override
    public boolean exists(String username) {
        // Note: This would require implementing a query to Supabase
        // For now, returning false to allow new signups
        // TODO: Implement existence check via Supabase query if needed
        return false;
    }

    private void uploadToSupabase(User user) {
        try {
            // Build the JSON payload with all user data
            String jsonPayload = buildJsonPayload(user);

            // Build the request
            String url = supabaseUrl + "/rest/v1/" + SupabaseConfig.TABLE_NAME;
            RequestBody body = RequestBody.create(jsonPayload, JSON);

            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("apikey", apiKey)
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .addHeader("Prefer", "return=minimal")
                    .post(body)
                    .build();

            // Execute the request
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    System.out.println("Successfully uploaded user profile to Supabase: " + user.getName());
                } else {
                    System.err.println("Failed to upload to Supabase. Response code: " + response.code());
                    if (response.body() != null) {
                        System.err.println("Response body: " + response.body().string());
                    }
                }
            }

        } catch (IOException e) {
            System.err.println("Error uploading to Supabase: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String buildJsonPayload(User user) {
        StringBuilder json = new StringBuilder();
        json.append("{");

        // Add basic user info
        json.append("\"username\":\"").append(escapeJson(user.getName())).append("\",");
        json.append("\"password\":\"").append(escapeJson(user.getPassword())).append("\",");
        json.append("\"location\":\"").append(escapeJson(user.getLocation())).append("\",");
        json.append("\"gender\":\"").append(escapeJson(user.getGender())).append("\"");

        // Add style preferences as booleans in the specified order
        if (user.getStyle() != null && !user.getStyle().isEmpty()) {
            Map<String, Boolean> style = user.getStyle();

            // Bottoms
            json.append(",\"jeans\":").append(getStyleValue(style, "jeans"));
            json.append(",\"sweatpants\":").append(getStyleValue(style, "sweatpants"));
            json.append(",\"shorts\":").append(getStyleValue(style, "shorts"));
            json.append(",\"dress_pants_chinos\":").append(getStyleValue(style, "dress pants/chinos"));
            json.append(",\"leggings\":").append(getStyleValue(style, "leggings"));
            json.append(",\"skirts\":").append(getStyleValue(style, "skirts"));

            // Tops
            json.append(",\"tshirts\":").append(getStyleValue(style, "T-shirts"));
            json.append(",\"long_sleeve_shirts\":").append(getStyleValue(style, "long sleeve shirts"));
            json.append(",\"tank_tops\":").append(getStyleValue(style, "tank tops"));
            json.append(",\"polo_shirts\":").append(getStyleValue(style, "polo shirts"));
            json.append(",\"blouses\":").append(getStyleValue(style, "blouses"));

            // Outerwear
            json.append(",\"hoodie\":").append(getStyleValue(style, "hoodie"));
            json.append(",\"sweatshirt_crewneck\":").append(getStyleValue(style, "sweatshirt/crewneck"));
            json.append(",\"light_jacket_windbreaker\":").append(getStyleValue(style, "light jacket/windbreaker"));
            json.append(",\"denim_jacket\":").append(getStyleValue(style, "denim jacket"));
            json.append(",\"winter_coat_puffer\":").append(getStyleValue(style, "winter coat/puffer"));
            json.append(",\"raincoat\":").append(getStyleValue(style, "raincoat"));
            json.append(",\"blazer\":").append(getStyleValue(style, "blazer"));

            // Footwear
            json.append(",\"sneakers\":").append(getStyleValue(style, "sneakers"));
            json.append(",\"casual_shoes\":").append(getStyleValue(style, "casual shoes"));
            json.append(",\"boots\":").append(getStyleValue(style, "boots"));
            json.append(",\"sandals_slippers\":").append(getStyleValue(style, "sandals/slippers"));
            json.append(",\"dress_shoes\":").append(getStyleValue(style, "dress shoes"));
            json.append(",\"crocs\":").append(getStyleValue(style, "crocs"));

            // Accessories
            json.append(",\"hats_caps_beanies\":").append(getStyleValue(style, "hats/caps/beanies"));
            json.append(",\"scarf\":").append(getStyleValue(style, "scarf"));
            json.append(",\"gloves\":").append(getStyleValue(style, "gloves"));
            json.append(",\"belt\":").append(getStyleValue(style, "belt"));
            json.append(",\"sunglasses\":").append(getStyleValue(style, "sunglasses"));
            json.append(",\"watch\":").append(getStyleValue(style, "watch"));
        }

        json.append("}");
        return json.toString();
    }

    private boolean getStyleValue(Map<String, Boolean> style, String key) {
        Boolean value = style.get(key);
        return value != null && value;
    }

    private String escapeJson(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
}
