package use_case.outfit_suggestion;

import entity.DailyForecast;
import entity.ForecastSlot;
import entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for OutfitSuggestionInteractor.
 * Tests the personalized style recommendation use case (Use Case 3).
 */
class OutfitSuggestionInteractorTest {

    private OutfitSuggestionInteractor interactor;
    private TestOutfitSuggestionPresenter presenter;
    private TestOutfitSuggestionDataAccess dataAccess;
    private User testUser;

    @BeforeEach
    void setUp() {
        // Create a test user with preferences
        testUser = new User("testUser", "password123", "Toronto", "female");

        // Add style preferences using a Map
        Map<String, Boolean> stylePrefs = new HashMap<>();
        stylePrefs.put("casual", true);
        stylePrefs.put("sporty", true);
        testUser.setStyle(stylePrefs);

        // Create mock dependencies
        presenter = new TestOutfitSuggestionPresenter();
        dataAccess = new TestOutfitSuggestionDataAccess();

        // Initialize the interactor
        interactor = new OutfitSuggestionInteractor(testUser, dataAccess, presenter);
    }

    /**
     * Test the main success path - user gets personalized outfit suggestions.
     * This is the REQUIRED test for your use case.
     */
    @Test
    void testSuccessfulOutfitSuggestion() {
        // Arrange
        OutfitSuggestionInputData inputData = new OutfitSuggestionInputData("testUser", "Toronto");

        // Act
        interactor.execute(inputData);

        // Assert
        assertTrue(presenter.wasSuccessViewCalled(),
                "Success view should have been called");
        assertFalse(presenter.wasFailViewCalled(),
                "Fail view should not have been called");

        OutfitSuggestionOutputData outputData = presenter.getOutputData();
        assertNotNull(outputData, "Output data should not be null");
        assertEquals("testUser", outputData.getUsername(), "Username should match");
        assertEquals("Toronto", outputData.getCity(), "City should match");
        assertEquals(15.0, outputData.getTemperature(), 0.01, "Temperature should match");
        assertNotNull(outputData.getOutfitSuggestions(), "Outfit suggestions should not be null");
        assertFalse(outputData.getOutfitSuggestions().isEmpty(), "Outfit suggestions should not be empty");

        // Verify that suggestions contain expected content
        String suggestions = outputData.getOutfitSuggestions();
        assertTrue(suggestions.contains("Outfit"), "Suggestions should contain outfit recommendations");
    }

    /**
     * Test when weather data cannot be retrieved (null forecast).
     */
    @Test
    void testWeatherDataNotFound() {
        // Arrange
        dataAccess.setShouldReturnNullForecast(true);
        OutfitSuggestionInputData inputData = new OutfitSuggestionInputData("testUser", "InvalidCity");

        // Act
        interactor.execute(inputData);

        // Assert
        assertTrue(presenter.wasFailViewCalled(),
                "Fail view should have been called when weather data is not found");
        assertFalse(presenter.wasSuccessViewCalled(),
                "Success view should not have been called");

        String errorMessage = presenter.getErrorMessage();
        assertNotNull(errorMessage, "Error message should not be null");
        assertTrue(errorMessage.contains("Could not retrieve weather data"),
                "Error message should indicate weather data retrieval failure");
    }

    /**
     * Test when weather forecast has no slots (empty forecast).
     */
    @Test
    void testEmptyWeatherForecast() {
        // Arrange
        dataAccess.setShouldReturnEmptyForecast(true);
        OutfitSuggestionInputData inputData = new OutfitSuggestionInputData("testUser", "Toronto");

        // Act
        interactor.execute(inputData);

        // Assert
        assertTrue(presenter.wasFailViewCalled(),
                "Fail view should have been called when forecast is empty");
        String errorMessage = presenter.getErrorMessage();
        assertTrue(errorMessage.contains("Could not retrieve weather data"),
                "Error message should indicate weather data issue");
    }

    /**
     * Test when outfit suggestions cannot be generated (null suggestions).
     */
    @Test
    void testOutfitSuggestionsNotGenerated() {
        // Arrange
        dataAccess.setShouldReturnNullSuggestions(true);
        OutfitSuggestionInputData inputData = new OutfitSuggestionInputData("testUser", "Toronto");

        // Act
        interactor.execute(inputData);

        // Assert
        assertTrue(presenter.wasFailViewCalled(),
                "Fail view should have been called when suggestions are null");
        String errorMessage = presenter.getErrorMessage();
        assertTrue(errorMessage.contains("Could not generate outfit suggestions"),
                "Error message should indicate suggestion generation failure");
    }

    /**
     * Test when outfit suggestions are empty.
     */
    @Test
    void testEmptyOutfitSuggestions() {
        // Arrange
        dataAccess.setShouldReturnEmptySuggestions(true);
        OutfitSuggestionInputData inputData = new OutfitSuggestionInputData("testUser", "Toronto");

        // Act
        interactor.execute(inputData);

        // Assert
        assertTrue(presenter.wasFailViewCalled(),
                "Fail view should have been called when suggestions are empty");
        String errorMessage = presenter.getErrorMessage();
        assertTrue(errorMessage.contains("Could not generate outfit suggestions"),
                "Error message should indicate suggestion generation failure");
    }

    /**
     * Test when an exception is thrown during execution.
     */
    @Test
    void testExceptionHandling() {
        // Arrange
        dataAccess.setShouldThrowException(true);
        OutfitSuggestionInputData inputData = new OutfitSuggestionInputData("testUser", "Toronto");

        // Act
        interactor.execute(inputData);

        // Assert
        assertTrue(presenter.wasFailViewCalled(),
                "Fail view should have been called when exception occurs");
        String errorMessage = presenter.getErrorMessage();
        assertNotNull(errorMessage, "Error message should not be null");
        assertTrue(errorMessage.contains("An error occurred"),
                "Error message should indicate an error occurred");
    }

    /**
     * Test with different location (verifies location is passed correctly).
     */
    @Test
    void testDifferentLocation() {
        // Arrange
        OutfitSuggestionInputData inputData = new OutfitSuggestionInputData("testUser", "Vancouver");

        // Act
        interactor.execute(inputData);

        // Assert
        assertTrue(presenter.wasSuccessViewCalled());
        OutfitSuggestionOutputData outputData = presenter.getOutputData();
        assertEquals("Vancouver", outputData.getCity(), "City should be Vancouver");
    }

    /**
     * Test that temperature from first forecast slot is used correctly.
     */
    @Test
    void testTemperatureFromFirstSlot() {
        // Arrange
        dataAccess.setCustomTemperature(22.5);
        OutfitSuggestionInputData inputData = new OutfitSuggestionInputData("testUser", "Toronto");

        // Act
        interactor.execute(inputData);

        // Assert
        assertTrue(presenter.wasSuccessViewCalled());
        OutfitSuggestionOutputData outputData = presenter.getOutputData();
        assertEquals(22.5, outputData.getTemperature(), 0.01,
                "Temperature should match the first slot temperature");
    }

    /**
     * Test that user preferences are used (via currentUser in constructor).
     */
    @Test
    void testUserPreferencesUsed() {
        // Arrange
        OutfitSuggestionInputData inputData = new OutfitSuggestionInputData("testUser", "Toronto");

        // Act
        interactor.execute(inputData);

        // Assert
        assertTrue(presenter.wasSuccessViewCalled());
        assertTrue(dataAccess.wasGenerateOutfitSuggestionsCalled(),
                "Data access should have been called to generate suggestions");
        assertEquals(testUser, dataAccess.getLastUserUsed(),
                "The current user should be passed to data access");
    }

    // ==================== Mock/Test Classes ====================

    /**
     * Test implementation of the presenter.
     * Captures what methods were called and what data was passed.
     */
    private static class TestOutfitSuggestionPresenter implements OutfitSuggestionOutputBoundary {

        private boolean successViewCalled = false;
        private boolean failViewCalled = false;
        private OutfitSuggestionOutputData outputData;
        private String errorMessage;

        @Override
        public void prepareSuccessView(OutfitSuggestionOutputData outputData) {
            this.successViewCalled = true;
            this.outputData = outputData;
        }

        @Override
        public void prepareFailView(String errorMessage) {
            this.failViewCalled = true;
            this.errorMessage = errorMessage;
        }

        public boolean wasSuccessViewCalled() {
            return successViewCalled;
        }

        public boolean wasFailViewCalled() {
            return failViewCalled;
        }

        public OutfitSuggestionOutputData getOutputData() {
            return outputData;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }

    /**
     * Test implementation of the data access interface.
     * Mocks the weather API and AI suggestion generation.
     */
    private static class TestOutfitSuggestionDataAccess implements OutfitSuggestionDataAccessInterface {

        private boolean shouldReturnNullForecast = false;
        private boolean shouldReturnEmptyForecast = false;
        private boolean shouldReturnNullSuggestions = false;
        private boolean shouldReturnEmptySuggestions = false;
        private boolean shouldThrowException = false;
        private double customTemperature = 15.0;
        private boolean generateOutfitSuggestionsCalled = false;
        private User lastUserUsed = null;

        @Override
        public DailyForecast getWeatherForecast(String location) {
            if (shouldThrowException) {
                throw new RuntimeException("API error");
            }

            if (shouldReturnNullForecast) {
                return null;
            }

            if (shouldReturnEmptyForecast) {
                return new DailyForecast(location, LocalDate.now(), new ArrayList<>());
            }

            // Create mock forecast with weather data
            List<ForecastSlot> slots = new ArrayList<>();

            // ForecastSlot constructor: (label, temperature, description, iconCode, precipProbability, windSpeed, feelsLike)
            ForecastSlot slot1 = new ForecastSlot(
                    "Morning",            // label
                    customTemperature,    // temperature
                    "Clear",              // description
                    "01d",                // iconCode
                    0.1,                  // precipProbability
                    10.0,                 // windSpeed
                    customTemperature - 2.0  // feelsLike
            );

            ForecastSlot slot2 = new ForecastSlot(
                    "Afternoon",
                    customTemperature + 2,
                    "Partly Cloudy",
                    "02d",
                    0.2,
                    12.0,
                    customTemperature
            );

            slots.add(slot1);
            slots.add(slot2);

            return new DailyForecast(location, LocalDate.now(), slots);
        }

        @Override
        public List<String> generateOutfitSuggestions(User user, DailyForecast forecast) {
            generateOutfitSuggestionsCalled = true;
            lastUserUsed = user;

            if (shouldThrowException) {
                throw new RuntimeException("LLM API error");
            }

            if (shouldReturnNullSuggestions) {
                return null;
            }

            if (shouldReturnEmptySuggestions) {
                return new ArrayList<>();
            }

            // Return mock outfit suggestions
            List<String> suggestions = new ArrayList<>();
            suggestions.add("Outfit 1: Light jacket with jeans and sneakers");
            suggestions.add("Outfit 2: Sweater with comfortable pants");
            suggestions.add("Outfit 3: Hoodie with joggers for a casual sporty look");

            return suggestions;
        }

        // Setter methods for controlling test behavior
        public void setShouldReturnNullForecast(boolean shouldReturnNullForecast) {
            this.shouldReturnNullForecast = shouldReturnNullForecast;
        }

        public void setShouldReturnEmptyForecast(boolean shouldReturnEmptyForecast) {
            this.shouldReturnEmptyForecast = shouldReturnEmptyForecast;
        }

        public void setShouldReturnNullSuggestions(boolean shouldReturnNullSuggestions) {
            this.shouldReturnNullSuggestions = shouldReturnNullSuggestions;
        }

        public void setShouldReturnEmptySuggestions(boolean shouldReturnEmptySuggestions) {
            this.shouldReturnEmptySuggestions = shouldReturnEmptySuggestions;
        }

        public void setShouldThrowException(boolean shouldThrowException) {
            this.shouldThrowException = shouldThrowException;
        }

        public void setCustomTemperature(double temperature) {
            this.customTemperature = temperature;
        }

        public boolean wasGenerateOutfitSuggestionsCalled() {
            return generateOutfitSuggestionsCalled;
        }

        public User getLastUserUsed() {
            return lastUserUsed;
        }
    }
}