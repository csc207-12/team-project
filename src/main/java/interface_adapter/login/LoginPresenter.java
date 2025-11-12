package interface_adapter.login;

import use_case.login.LoginOutputBoundary;
import use_case.login.LoginOutputData;

public class LoginPresenter implements LoginOutputBoundary {
    private final LoginView view;

    public LoginPresenter(LoginView view) {
        this.view = view;
    }

    public void present(LoginOutputData outputData) {
        if (outputData.isSuccess()) {
            view.onLoginSuccess(outputData.getUsername());
        } else {
            view.onLoginFailure(outputData.getMessage());
        }
    }
}
