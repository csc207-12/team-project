package data_access;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class PromptLoader {

    public static String loadPrompt(String filename) {
        try {
            InputStream is = PromptLoader.class.getClassLoader().getResourceAsStream(filename);
            if (is == null) {
                throw new RuntimeException("Prompt file not found: " + filename);
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }

            return sb.toString();

        } catch (Exception e) {
            throw new RuntimeException("Failed to load prompt: " + filename, e);
        }
    }
}
