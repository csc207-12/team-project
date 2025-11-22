package interface_adapter.outfit_image_generation;

import java.util.List;

public interface OutfitImageGenerationView {

    void onImageGenerationSuccess(List<String> base64Images);

    void onImageGenerationFailure(String errorMessage);
}
