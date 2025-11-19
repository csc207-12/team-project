package interface_adapter.purpose;

import use_case.purpose.PurposeOutputBoundary;
import use_case.purpose.PurposeOutputData;

public class PurposePresenter implements PurposeOutputBoundary {

    private final PurposeView view;

    public PurposePresenter(PurposeView view) {
        this.view = view;
    }

    @Override
    public void presentSuccess(PurposeOutputData data) {
        String text = data.getAccessoriesText();
        view.onPurposeAccessorySuccess(text);
    }

    @Override
    public void presentFailure(String errorMessage) {
        view.onPurposeAccessoryFailure(errorMessage);
    }
}

