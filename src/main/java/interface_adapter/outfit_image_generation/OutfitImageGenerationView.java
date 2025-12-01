package interface_adapter.outfit_image_generation;

import java.util.List;

/**
 * View interface for displaying the results of outfit image generation.
 */
public interface OutfitImageGenerationView {

    /**
     * Called when image generation succeeds.
     *
     * @param base64Images list of base64-encoded image strings
     */
    void onImageGenerationSuccess(List<String> base64Images);

    /**
     * Called when image generation fails.
     *
     * @param errorMessage message describing the failure
     */
    void onImageGenerationFailure(String errorMessage);
}
