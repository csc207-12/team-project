package interface_adapter.purpose;

import use_case.purpose.PurposeInputBoundary;
import use_case.purpose.PurposeInputData;

public class PurposeController {

    private final PurposeInputBoundary interactor;

    public PurposeController(PurposeInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void execute(String purpose) {
        PurposeInputData inputData = new PurposeInputData(purpose);
        interactor.generateAccessories(inputData);
    }
}

