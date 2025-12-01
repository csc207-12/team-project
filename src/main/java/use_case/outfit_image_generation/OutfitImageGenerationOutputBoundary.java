package use_case.outfit_image_generation;

public interface OutfitImageGenerationOutputBoundary {

    /**
     * Prepares the success view with the generated outfit images.
     *
     * @param data the output data containing the generated images
     */
    void prepareSuccessView(OutfitImageGenerationOutputData data);

    /**
     * Prepares the failure view with an error message.
     *
     * @param errorMessage the error message to be displayed
     */
    void prepareFailureView(String errorMessage);
}
