package interface_adapter.signup;

/** Minimal view interface used by the presenter to display messages. */
public interface SignupView {
    void displayMessage(String message);
    void onSignupSuccess(String username);
}
