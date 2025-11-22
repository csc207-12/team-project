package use_case.outfit_image_generation;

import java.util.List;

public interface OutfitImageGenerationDataAccessInterface {
    List<String> generateImages(List<String> outfits);
}
