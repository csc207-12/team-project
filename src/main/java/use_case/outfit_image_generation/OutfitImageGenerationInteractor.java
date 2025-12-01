package use_case.outfit_image_generation;

import java.util.List;

/**
 * Interactor for generating outfit images using the Gemini API.
 * It coordinates between the data access layer and the presenter.
 */
public class OutfitImageGenerationInteractor implements OutfitImageGenerationInputBoundary {

    private final OutfitImageGenerationDataAccessInterface dataAccess;
    private final OutfitImageGenerationOutputBoundary presenter;

    /**
     * Constructs the interactor with its collaborators.
     *
     * @param dataAccess the data access object used to call Gemini
     * @param presenter  the presenter that prepares the output data for the view
     */
    public OutfitImageGenerationInteractor(
            OutfitImageGenerationDataAccessInterface dataAccess,
            OutfitImageGenerationOutputBoundary presenter) {

        this.dataAccess = dataAccess;
        this.presenter = presenter;
    }

    /**
     * Generates images for the given list of outfit descriptions.
     *
     * @param outfits list of outfit description strings
     */
    @Override
    public void generateImages(List<String> outfits) {
        if (outfits == null || outfits.isEmpty()) {
            presenter.prepareFailureView("No outfits provided.");
        }
        else {
            final List<String> images = dataAccess.generateImages(outfits);

            if (images == null || images.isEmpty()) {
                presenter.prepareFailureView("Gemini did not return any images.");
            }
            else {
                presenter.prepareSuccessView(new OutfitImageGenerationOutputData(images));
            }
        }
    }
}
