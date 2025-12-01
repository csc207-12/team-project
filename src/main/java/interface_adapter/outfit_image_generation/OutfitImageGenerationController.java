package interface_adapter.outfit_image_generation;

import java.util.List;

import use_case.outfit_image_generation.OutfitImageGenerationInputBoundary;

/**
 * Controller that forwards outfit image generation requests to the use case layer.
 */
public class OutfitImageGenerationController {

    private final OutfitImageGenerationInputBoundary inputBoundary;

    /**
     * Constructs a controller for outfit image generation.
     *
     * @param inputBoundary the input boundary that handles image generation
     */
    public OutfitImageGenerationController(OutfitImageGenerationInputBoundary inputBoundary) {
        this.inputBoundary = inputBoundary;
    }

    /**
     * Triggers image generation for the given list of outfit descriptions.
     *
     * @param outfits list of outfit description strings
     */
    public void generateImages(List<String> outfits) {
        inputBoundary.generateImages(outfits);
    }
}
