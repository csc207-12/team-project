package use_case.login;

import entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import data_access.user_storage.InMemoryUserRepository;

import static org.junit.jupiter.api.Assertions.*;

class LoginInteractorTest {

    private InMemoryUserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = new InMemoryUserRepository();
    }

    @Test
    void successTest() {
        // Input data for successful login
        LoginInputData inputData = new LoginInputData("testuser", "testpassword");

        // Add user to repository
        User user = new User("testuser", "testpassword", "Toronto", "Male");
        userRepository.save(user);

        // Presenter to verify success
        LoginOutputBoundary outputBoundary = new LoginOutputBoundary() {
            @Override
            public void present(LoginOutputData output) {
                assertTrue(output.isSuccess());
                assertEquals("Login successful", output.getMessage());
                assertEquals("testuser", output.getUsername());
                assertNotNull(output.getUser());
                assertEquals("testuser", output.getUser().getName());
                assertEquals("testpassword", output.getUser().getPassword());
                assertEquals("Toronto", output.getUser().getLocation());
                assertEquals("Male", output.getUser().getGender());
            }
        };

        LoginInteractor interactor = new LoginInteractor(outputBoundary, userRepository);
        interactor.login(inputData);
    }

    @Test
    void failurePasswordMismatchTest() {
        // Input data with wrong password
        LoginInputData inputData = new LoginInputData("testuser", "wrongpassword");

        // Add user with correct password to repository
        User user = new User("testuser", "correctpassword", "Toronto", "Male");
        userRepository.save(user);

        // Presenter to verify failure
        LoginOutputBoundary outputBoundary = new LoginOutputBoundary() {
            @Override
            public void present(LoginOutputData output) {
                assertFalse(output.isSuccess());
                assertEquals("Invalid username or password", output.getMessage());
                assertEquals("testuser", output.getUsername());
                assertNull(output.getUser());
            }
        };

        LoginInteractor interactor = new LoginInteractor(outputBoundary, userRepository);
        interactor.login(inputData);
    }

    @Test
    void failureUserNotFoundTest() {
        // Input data with non-existent user
        LoginInputData inputData = new LoginInputData("nonexistent", "password");

        // Repository is empty - no users added

        // Presenter to verify failure
        LoginOutputBoundary outputBoundary = new LoginOutputBoundary() {
            @Override
            public void present(LoginOutputData output) {
                assertFalse(output.isSuccess());
                assertEquals("Invalid username or password", output.getMessage());
                assertEquals("nonexistent", output.getUsername());
                assertNull(output.getUser());
            }
        };

        LoginInteractor interactor = new LoginInteractor(outputBoundary, userRepository);
        interactor.login(inputData);
    }
}

