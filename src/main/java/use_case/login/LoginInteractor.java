package use_case.login;

import entity.User;
import data_access.user_storage.UserRepository;


// Interactor for handling user login
public class LoginInteractor implements LoginInputBoundary{
    private final LoginOutputBoundary outputBoundary;
    private final UserRepository userRepository;

    public LoginInteractor(LoginOutputBoundary outputBoundary, UserRepository userRepository) {
        this.outputBoundary = outputBoundary;
        this.userRepository = userRepository;
    }

    public void login(LoginInputData inputData) {
        User user = authenticate(inputData.getUsername(), inputData.getPassword());

        if (user == null) {
            LoginOutputData outputData = new LoginOutputData(false, "Invalid username or password", inputData.getUsername());
            outputBoundary.present(outputData);
            return;
        }

        LoginOutputData outputData = new LoginOutputData(true, "Login successful", inputData.getUsername(), user);

        outputBoundary.present(outputData);
    }

    private User authenticate(String username, String password) {
        User possibleuser = userRepository.findByUsername(username);
        if (possibleuser == null) {
            return null;
        }
        if (possibleuser.getPassword().equals(password)) {
            return possibleuser;
        }
        return null;
    }
}
