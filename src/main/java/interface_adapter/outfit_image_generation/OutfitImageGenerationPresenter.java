package interface_adapter.outfit_image_generation;

import use_case.outfit_image_generation.OutfitImageGenerationOutputBoundary;
import use_case.outfit_image_generation.OutfitImageGenerationOutputData;

public class OutfitImageGenerationPresenter implements OutfitImageGenerationOutputBoundary {

    private final OutfitImageGenerationView view;

    public OutfitImageGenerationPresenter(OutfitImageGenerationView view) {
        this.view = view;
    }

    @Override
    public void prepareSuccessView(OutfitImageGenerationOutputData data) {
        view.onImageGenerationSuccess(data.getBase64Images());
    }

    @Override
    public void prepareFailureView(String errorMessage) {
        view.onImageGenerationFailure(errorMessage);
    }
}
