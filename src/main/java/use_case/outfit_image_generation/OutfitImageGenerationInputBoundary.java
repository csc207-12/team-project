package use_case.outfit_image_generation;

import java.util.List;

public interface OutfitImageGenerationInputBoundary {

    /**
     * Triggers the generation of images for the given list of outfit descriptions.
     *
     * @param outfits list of outfit description strings
     */
    void generateImages(List<String> outfits);
}
