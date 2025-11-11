package use_case.signup;

import entity.User;
import use_case.UserRepository;

/**
 * Implements the signup use case interactor.
 */
public class SignupInteractor implements SignupInputBoundary {
    private final UserRepository repository;
    private final SignupOutputBoundary presenter;

    public SignupInteractor(UserRepository repository, SignupOutputBoundary presenter) {
        this.repository = repository;
        this.presenter = presenter;
    }

    @Override
    public void register(SignupInputData input) {
        // Basic validation
        if (input.getUsername() == null || input.getUsername().trim().isEmpty()) {
            presenter.present(new SignupOutputData(false, "Username cannot be empty", null));
            return;
        }
        if (input.getPassword() == null || input.getPassword().trim().isEmpty()) {
            presenter.present(new SignupOutputData(false, "Password cannot be empty", null));
            return;
        }

        // Check existing user
        if (repository.exists(input.getUsername())) {
            presenter.present(new SignupOutputData(false, "Username already taken", null));
            return;
        }

        // Create and save user
        User user = new User(input.getUsername(), input.getPassword(), input.getLocation(), input.getGender());
        repository.save(user);
        presenter.present(new SignupOutputData(true, "Registration successful for " + input.getUsername(), input.getUsername()));
    }
}

