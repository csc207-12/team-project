package use_case.outfit_image_generation;

import java.util.List;

/**
 * Data access interface for generating outfit images.
 */
public interface OutfitImageGenerationDataAccessInterface {
    /**
     * Generates images for the given list of outfit descriptions.
     *
     * @param outfits list of outfit description strings
     * @return list of base64-encoded image strings
     */
    List<String> generateImages(List<String> outfits);
}
