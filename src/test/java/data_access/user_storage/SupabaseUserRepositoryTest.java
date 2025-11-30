package data_access.user_storage;

import entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for SupabaseUserRepository.
 *
 * Test user info:
 * username: "testuser_repo"
 * password: "test123"
 * location: "Toronto"
 * gender: "Male"
 * jeans: true
 * sweatpants: false
 * shorts: true
 * tshirts: true
 * hoodie: true
 * sneakers: true
 */
class SupabaseUserRepositoryTest {

    private SupabaseUserRepository repository;

    // Test user that should exist in Supabase
    private static final String TEST_USERNAME = "testuser_repo";
    private static final String TEST_PASSWORD = "test123";
    private static final String TEST_LOCATION = "Toronto";
    private static final String TEST_GENDER = "Male";

    @BeforeEach
    void setUp() {
        repository = new SupabaseUserRepository();
    }

    @Test
    void successFindExistingUserTest() {
        // Find user that exists in database
        User user = repository.findByUsername(TEST_USERNAME);

        assertNotNull(user, "User should be found in database");
        assertEquals(TEST_USERNAME, user.getName());
        assertEquals(TEST_PASSWORD, user.getPassword());
        assertEquals(TEST_LOCATION, user.getLocation());
        assertEquals(TEST_GENDER, user.getGender());
        assertNotNull(user.getStyle(), "Style preferences should be loaded");
    }

    @Test
    void successFindUserWithStylePreferencesTest() {
        // Find user and verify style preferences are loaded
        User user = repository.findByUsername(TEST_USERNAME);

        assertNotNull(user);
        Map<String, Boolean> style = user.getStyle();
        assertNotNull(style);

        // Verify some expected style preferences
        assertTrue(style.containsKey("jeans"));
        assertTrue(style.containsKey("sweatpants"));
        assertTrue(style.containsKey("shorts"));
    }

    @Test
    void failureFindNonExistentUserTest() {
        // Try to find user that doesn't exist
        User user = repository.findByUsername("nonexistent_user_12345");

        assertNull(user, "Non-existent user should return null");
    }

    @Test
    void successSaveNewUserTest() {
        // Create a unique username for this test run
        String uniqueUsername = "test_save_" + System.currentTimeMillis();

        // Create new user
        User newUser = new User(uniqueUsername, "password123", "Vancouver", "Female");

        Map<String, Boolean> stylePrefs = new HashMap<>();
        stylePrefs.put("jeans", true);
        stylePrefs.put("T-shirts", true);
        stylePrefs.put("sneakers", true);
        newUser.setStyle(stylePrefs);

        // Save user
        repository.save(newUser);

        // Wait a bit for database to update
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Verify user was saved
        assertTrue(repository.exists(uniqueUsername), "Saved user should exist");

        User retrievedUser = repository.findByUsername(uniqueUsername);
        assertNotNull(retrievedUser, "Should be able to retrieve saved user");
        assertEquals(uniqueUsername, retrievedUser.getName());
        assertEquals("password123", retrievedUser.getPassword());
        assertEquals("Vancouver", retrievedUser.getLocation());
        assertEquals("Female", retrievedUser.getGender());
    }

}

