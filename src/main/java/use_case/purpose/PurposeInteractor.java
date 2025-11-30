package use_case.purpose;

public class PurposeInteractor implements PurposeInputBoundary {

    private final PurposeAccessoryDataAccessInterface dataAccess;
    private final PurposeOutputBoundary presenter;

    public PurposeInteractor(PurposeAccessoryDataAccessInterface dataAccess,
                             PurposeOutputBoundary presenter) {
        this.dataAccess = dataAccess;
        this.presenter = presenter;
    }

    @Override
    public void generateAccessories(PurposeInputData inputData) {
        String purpose = inputData.getPurpose();
        if (purpose == null || purpose.trim().isEmpty()) {
            presenter.presentFailure("Please enter the purpose for your outing.");
            return;
        }

        try {
            String suggestions = dataAccess.generateAccessorySuggestions(purpose.trim());
            if (suggestions == null || suggestions.trim().isEmpty()) {
                presenter.presentFailure("Failed to generate accessory suggestions. Please try again.");
            } else {
                PurposeOutputData outputData = new PurposeOutputData(suggestions);
                presenter.presentSuccess(outputData);
            }
        } catch (Exception e) {
            e.printStackTrace();
            presenter.presentFailure("An error occurred while generating accessory suggestions.");
        }
    }
}
