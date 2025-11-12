package interface_adapter.signup;

import use_case.signup.SignupOutputBoundary;
import use_case.signup.SignupOutputData;

/** Presenter that transforms output data into a view call. */
public class SignupPresenter implements SignupOutputBoundary {
    private final SignupView view;

    public SignupPresenter(SignupView view) {
        this.view = view;
    }

    @Override
    public void present(SignupOutputData output) {
        if (output.isSuccess()) {
            view.onSignupSuccess(output.getUsername());
        } else {
            view.onSignupFailure(output.getMessage());
        }
    }
}
