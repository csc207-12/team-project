package use_case.signup;

import data_access.user_storage.PendingUserStorage;
import entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import data_access.user_storage.InMemoryUserRepository;

import static org.junit.jupiter.api.Assertions.*;

class SignupInteractorTest {

    private InMemoryUserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = new InMemoryUserRepository();
    }

    @Test
    void successTest() {
        // Input data for new user
        SignupInputData inputData = new SignupInputData("newuser", "password123", "Toronto", "Male");

        // Presenter to verify success and pending storage
        SignupOutputBoundary outputBoundary = new SignupOutputBoundary() {
            @Override
            public void present(SignupOutputData output) {
                assertTrue(output.isSuccess());
                assertEquals("Registration successful for newuser", output.getMessage());
                assertEquals("newuser", output.getUsername());

                // Verify user stored in pending storage
                PendingUserStorage storage = PendingUserStorage.getInstance();
                assertTrue(storage.hasPendingUser("newuser"));
                User pendingUser = storage.getPendingUser("newuser");
                assertNotNull(pendingUser);
                assertEquals("newuser", pendingUser.getName());
                assertEquals("password123", pendingUser.getPassword());
                assertEquals("Toronto", pendingUser.getLocation());
                assertEquals("Male", pendingUser.getGender());
            }
        };

        SignupInteractor interactor = new SignupInteractor(userRepository, outputBoundary);
        interactor.register(inputData);
    }

    @Test
    void failureUsernameAlreadyExistsTest() {
        // Input data with existing username
        SignupInputData inputData = new SignupInputData("Amey", "password", "Toronto", "Male");

        // Add existing user to repository
        User existingUser = new User("Amey", "pwd", "Vancouver", "Male");
        userRepository.save(existingUser);

        // Presenter to verify failure
        SignupOutputBoundary failurePresenter = new SignupOutputBoundary() {
            @Override
            public void present(SignupOutputData output) {
                if (output.isSuccess()) {
                    fail("Use case success is unexpected.");
                } else {
                    assertEquals("Username already taken", output.getMessage());
                }
            }
        };

        SignupInteractor interactor = new SignupInteractor(userRepository, failurePresenter);
        interactor.register(inputData);
    }

    @Test
    void failureEmptyUsernameTest() {
        // Input data with empty username
        SignupInputData inputData = new SignupInputData("", "password123", "Toronto", "Male");

        // Presenter to verify failure
        SignupOutputBoundary outputBoundary = new SignupOutputBoundary() {
            @Override
            public void present(SignupOutputData output) {
                assertFalse(output.isSuccess());
                assertEquals("Username cannot be empty", output.getMessage());
                assertNull(output.getUsername());
            }
        };

        SignupInteractor interactor = new SignupInteractor(userRepository, outputBoundary);
        interactor.register(inputData);
    }

    @Test
    void failureEmptyPasswordTest() {
        // Input data with empty password
        SignupInputData inputData = new SignupInputData("newuser", "", "Toronto", "Male");

        // Presenter to verify failure
        SignupOutputBoundary outputBoundary = new SignupOutputBoundary() {
            @Override
            public void present(SignupOutputData output) {
                assertFalse(output.isSuccess());
                assertEquals("Password cannot be empty", output.getMessage());
                assertNull(output.getUsername());
            }
        };

        SignupInteractor interactor = new SignupInteractor(userRepository, outputBoundary);
        interactor.register(inputData);
    }
}

