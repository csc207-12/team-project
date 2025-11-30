package use_case.style;

import data_access.user_storage.PendingUserStorage;
import entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import data_access.user_storage.InMemoryUserRepository;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class StyleInteractorTest {

    private InMemoryUserRepository userRepository;
    private PendingUserStorage pendingStorage;

    @BeforeEach
    void setUp() {
        userRepository = new InMemoryUserRepository();
        pendingStorage = PendingUserStorage.getInstance();
    }

    @Test
    void successTest() {
        // Create user and add to pending storage
        User pendingUser = new User("john", "password123", "Toronto", "Male");
        pendingStorage.storePendingUser(pendingUser);

        // Input data with style preferences
        Map<String, Boolean> stylePrefs = new HashMap<>();
        stylePrefs.put("jeans", true);
        stylePrefs.put("sweatpants", false);
        stylePrefs.put("shorts", true);
        StyleInputData inputData = new StyleInputData("john", stylePrefs);

        // Presenter to verify success
        StyleOutputBoundary outputBoundary = new StyleOutputBoundary() {
            @Override
            public void present(StyleOutputData output) {
                assertTrue(output.isSuccess());
                assertEquals("Style preferences saved successfully!", output.getMessage());
                assertEquals("john", output.getUsername());

                // Verify user is saved to repository with style preferences
                User savedUser = userRepository.findByUsername("john");
                assertNotNull(savedUser);
                assertEquals("john", savedUser.getName());
                assertNotNull(savedUser.getStyle());
                assertEquals(true, savedUser.getStyle().get("jeans"));
                assertEquals(false, savedUser.getStyle().get("sweatpants"));
                assertEquals(true, savedUser.getStyle().get("shorts"));

                // Verify user is removed from pending storage
                assertFalse(pendingStorage.hasPendingUser("john"));
            }
        };

        StyleInteractor interactor = new StyleInteractor(userRepository, outputBoundary);
        interactor.saveStylePreferences(inputData);
    }

    @Test
    void successPreservesUserDataTest() {
        // Create user with specific data and add to pending storage
        User pendingUser = new User("bob", "securepass", "Montreal", "Other");
        pendingStorage.storePendingUser(pendingUser);

        // Input data with style preferences
        Map<String, Boolean> stylePrefs = new HashMap<>();
        stylePrefs.put("jeans", true);
        StyleInputData inputData = new StyleInputData("bob", stylePrefs);

        // Presenter to verify user data is preserved
        StyleOutputBoundary outputBoundary = new StyleOutputBoundary() {
            @Override
            public void present(StyleOutputData output) {
                assertTrue(output.isSuccess());

                // Verify all user data is preserved after adding style
                User savedUser = userRepository.findByUsername("bob");
                assertNotNull(savedUser);
                assertEquals("bob", savedUser.getName());
                assertEquals("securepass", savedUser.getPassword());
                assertEquals("Montreal", savedUser.getLocation());
                assertEquals("Other", savedUser.getGender());
                assertNotNull(savedUser.getStyle());
                assertEquals(true, savedUser.getStyle().get("jeans"));
            }
        };

        StyleInteractor interactor = new StyleInteractor(userRepository, outputBoundary);
        interactor.saveStylePreferences(inputData);
    }

}

