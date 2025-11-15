package use_case.login;

import data_access.SupabaseUserRepository;
import entity.User;


// Interactor for handling user login
public class LoginInteractor implements LoginInputBoundary{
    private final LoginOutputBoundary outputBoundary;

    public LoginInteractor(LoginOutputBoundary outputBoundary) {
        this.outputBoundary = outputBoundary;
    }

    public void login(LoginInputData inputData) {
        boolean success = authenticate(inputData.getUsername(), inputData.getPassword());

        if (!success) {
            LoginOutputData outputData = new LoginOutputData(false, "Invalid username or password", inputData.getUsername());
            outputBoundary.present(outputData);
            return;
        }

        LoginOutputData outputData = new LoginOutputData(success, "Login successful", inputData.getUsername());

        outputBoundary.present(outputData);
    }

    private boolean authenticate(String username, String password) {
        SupabaseUserRepository userRepository = new SupabaseUserRepository();
        User possibleuser = userRepository.findByUsername(username);
        if (possibleuser == null) {
            return false;
        }
        return possibleuser.getPassword().equals(password);


    }
}
