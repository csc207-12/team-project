package use_case.multiple_outfit_suggestion;

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
 * Unit tests for MultipleOutfitSuggestionInteractor.
 * Tests the multiple outfit suggestions use case (Use Case 5).
 */
class MultipleOutfitSuggestionInteractorTest {

    private MultipleOutfitSuggestionInteractor interactor;
    private TestMultipleOutfitSuggestionPresenter presenter;
    private TestMultipleOutfitSuggestionDataAccess dataAccess;
    private User testUser;

    @BeforeEach
    void setUp() {
        // Create a test user with preferences
        testUser = new User("testUser", "password123", "Toronto", "female");

        // Add style preferences using a Map
        Map<String, Boolean> stylePrefs = new HashMap<>();
        stylePrefs.put("casual", true);
        stylePrefs.put("formal", true);
        stylePrefs.put("sporty", true);
        testUser.setStyle(stylePrefs);

        // Create mock dependencies
        presenter = new TestMultipleOutfitSuggestionPresenter();
        dataAccess = new TestMultipleOutfitSuggestionDataAccess();

        // Initialize the interactor
        interactor = new MultipleOutfitSuggestionInteractor(testUser, dataAccess, presenter);
    }

    /**
     * Test the main success path - user gets multiple personalized outfit suggestions.
     * This is the REQUIRED test for Use Case 5.
     */
    @Test
    void testSuccessfulMultipleOutfitSuggestions() {
        // Arrange
        MultipleOutfitSuggestionInputData inputData =
            new MultipleOutfitSuggestionInputData("testUser", "Toronto", 3);

        // Act
        interactor.execute(inputData);

        // Assert
        assertTrue(presenter.wasSuccessViewCalled(),
                "Success view should have been called");
        assertFalse(presenter.wasFailViewCalled(),
                "Fail view should not have been called");

        MultipleOutfitSuggestionOutputData outputData = presenter.getOutputData();
        assertNotNull(outputData, "Output data should not be null");
        assertEquals("testUser", outputData.getUsername(), "Username should match");
        assertEquals("Toronto", outputData.getCity(), "City should match");
        assertEquals(15.0, outputData.getTemperature(), 0.01, "Temperature should match");

        List<String> suggestions = outputData.getOutfitSuggestions();
        assertNotNull(suggestions, "Outfit suggestions list should not be null");
        assertEquals(3, suggestions.size(), "Should have 3 outfit suggestions");

        // Verify each suggestion is non-empty
        for (String suggestion : suggestions) {
            assertNotNull(suggestion, "Individual suggestion should not be null");
            assertFalse(suggestion.isEmpty(), "Individual suggestion should not be empty");
        }
    }

    /**
     * Test requesting a different number of suggestions.
     */
    @Test
    void testDifferentNumberOfSuggestions() {
        // Arrange - request 5 suggestions
        MultipleOutfitSuggestionInputData inputData =
            new MultipleOutfitSuggestionInputData("testUser", "Toronto", 5);

        // Act
        interactor.execute(inputData);

        // Assert
        assertTrue(presenter.wasSuccessViewCalled());
        MultipleOutfitSuggestionOutputData outputData = presenter.getOutputData();
        assertEquals(5, outputData.getOutfitSuggestions().size(),
                "Should have 5 outfit suggestions");
        assertEquals(5, dataAccess.getLastNumberOfSuggestionsRequested(),
                "Data access should have been called with 5 suggestions");
    }

    /**
     * Test requesting minimum number of suggestions (1).
     */
    @Test
    void testMinimumNumberOfSuggestions() {
        // Arrange
        MultipleOutfitSuggestionInputData inputData =
            new MultipleOutfitSuggestionInputData("testUser", "Toronto", 1);

        // Act
        interactor.execute(inputData);

        // Assert
        assertTrue(presenter.wasSuccessViewCalled());
        MultipleOutfitSuggestionOutputData outputData = presenter.getOutputData();
        assertEquals(1, outputData.getOutfitSuggestions().size(),
                "Should have 1 outfit suggestion");
    }

    /**
     * Test requesting maximum number of suggestions (10).
     */
    @Test
    void testMaximumNumberOfSuggestions() {
        // Arrange
        MultipleOutfitSuggestionInputData inputData =
            new MultipleOutfitSuggestionInputData("testUser", "Toronto", 10);

        // Act
        interactor.execute(inputData);

        // Assert
        assertTrue(presenter.wasSuccessViewCalled());
        MultipleOutfitSuggestionOutputData outputData = presenter.getOutputData();
        assertEquals(10, outputData.getOutfitSuggestions().size(),
                "Should have 10 outfit suggestions");
    }

    /**
     * Test when weather data cannot be retrieved (null forecast).
     */
    @Test
    void testWeatherDataNotFound() {
        // Arrange
        dataAccess.setShouldReturnNullForecast(true);
        MultipleOutfitSuggestionInputData inputData =
            new MultipleOutfitSuggestionInputData("testUser", "InvalidCity", 3);

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
        MultipleOutfitSuggestionInputData inputData =
            new MultipleOutfitSuggestionInputData("testUser", "Toronto", 3);

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
        MultipleOutfitSuggestionInputData inputData =
            new MultipleOutfitSuggestionInputData("testUser", "Toronto", 3);

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
     * Test when outfit suggestions list is empty.
     */
    @Test
    void testEmptyOutfitSuggestions() {
        // Arrange
        dataAccess.setShouldReturnEmptySuggestions(true);
        MultipleOutfitSuggestionInputData inputData =
            new MultipleOutfitSuggestionInputData("testUser", "Toronto", 3);

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
        MultipleOutfitSuggestionInputData inputData =
            new MultipleOutfitSuggestionInputData("testUser", "Toronto", 3);

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
        MultipleOutfitSuggestionInputData inputData =
            new MultipleOutfitSuggestionInputData("testUser", "Vancouver", 3);

        // Act
        interactor.execute(inputData);

        // Assert
        assertTrue(presenter.wasSuccessViewCalled());
        MultipleOutfitSuggestionOutputData outputData = presenter.getOutputData();
        assertEquals("Vancouver", outputData.getCity(), "City should be Vancouver");
    }

    /**
     * Test that temperature from first forecast slot is used correctly.
     */
    @Test
    void testTemperatureFromFirstSlot() {
        // Arrange
        dataAccess.setCustomTemperature(22.5);
        MultipleOutfitSuggestionInputData inputData =
            new MultipleOutfitSuggestionInputData("testUser", "Toronto", 3);

        // Act
        interactor.execute(inputData);

        // Assert
        assertTrue(presenter.wasSuccessViewCalled());
        MultipleOutfitSuggestionOutputData outputData = presenter.getOutputData();
        assertEquals(22.5, outputData.getTemperature(), 0.01,
                "Temperature should match the first slot temperature");
    }

    /**
     * Test that user preferences are passed to data access.
     */
    @Test
    void testUserPreferencesUsed() {
        // Arrange
        MultipleOutfitSuggestionInputData inputData =
            new MultipleOutfitSuggestionInputData("testUser", "Toronto", 3);

        // Act
        interactor.execute(inputData);

        // Assert
        assertTrue(presenter.wasSuccessViewCalled());
        assertTrue(dataAccess.wasGenerateMultipleOutfitSuggestionsCalled(),
                "Data access should have been called to generate suggestions");
        assertEquals(testUser, dataAccess.getLastUserUsed(),
                "The current user should be passed to data access");
    }

    /**
     * Test that forecast data is passed to data access.
     */
    @Test
    void testForecastPassedToDataAccess() {
        // Arrange
        MultipleOutfitSuggestionInputData inputData =
            new MultipleOutfitSuggestionInputData("testUser", "Toronto", 3);

        // Act
        interactor.execute(inputData);

        // Assert
        assertTrue(presenter.wasSuccessViewCalled());
        DailyForecast forecastUsed = dataAccess.getLastForecastUsed();
        assertNotNull(forecastUsed, "Forecast should have been passed to data access");
        assertEquals("Toronto", forecastUsed.getCity(), "Forecast city should match");
    }

    // ==================== Mock/Test Classes ====================

    /**
     * Test implementation of the presenter.
     * Captures what methods were called and what data was passed.
     */
    private static class TestMultipleOutfitSuggestionPresenter
            implements MultipleOutfitSuggestionOutputBoundary {

        private boolean successViewCalled = false;
        private boolean failViewCalled = false;
        private MultipleOutfitSuggestionOutputData outputData;
        private String errorMessage;

        @Override
        public void prepareSuccessView(MultipleOutfitSuggestionOutputData outputData) {
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

        public MultipleOutfitSuggestionOutputData getOutputData() {
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
    private static class TestMultipleOutfitSuggestionDataAccess
            implements MultipleOutfitSuggestionDataAccessInterface {

        private boolean shouldReturnNullForecast = false;
        private boolean shouldReturnEmptyForecast = false;
        private boolean shouldReturnNullSuggestions = false;
        private boolean shouldReturnEmptySuggestions = false;
        private boolean shouldThrowException = false;
        private double customTemperature = 15.0;
        private boolean generateMultipleOutfitSuggestionsCalled = false;
        private User lastUserUsed = null;
        private DailyForecast lastForecastUsed = null;
        private int lastNumberOfSuggestionsRequested = 0;

        @Override
        public DailyForecast getWeatherForecast(String location) {
            if (shouldThrowException) {
                throw new RuntimeException("Weather API error");
            }

            if (shouldReturnNullForecast) {
                return null;
            }

            if (shouldReturnEmptyForecast) {
                return new DailyForecast(location, LocalDate.now(), new ArrayList<>());
            }

            // Create mock forecast with weather data
            List<ForecastSlot> slots = new ArrayList<>();

            ForecastSlot slot1 = new ForecastSlot(
                    "Now",                // label
                    customTemperature,    // temperature
                    "Clear sky",          // description
                    "01d",                // iconCode
                    0.1,                  // precipProbability
                    10.0,                 // windSpeed
                    customTemperature - 2.0  // feelsLike
            );

            ForecastSlot slot2 = new ForecastSlot(
                    "In 3 hours",
                    customTemperature + 2,
                    "Partly cloudy",
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
        public List<String> generateMultipleOutfitSuggestions(
                User user, DailyForecast forecast, int numberOfSuggestions) {

            generateMultipleOutfitSuggestionsCalled = true;
            lastUserUsed = user;
            lastForecastUsed = forecast;
            lastNumberOfSuggestionsRequested = numberOfSuggestions;

            if (shouldThrowException) {
                throw new RuntimeException("AI API error");
            }

            if (shouldReturnNullSuggestions) {
                return null;
            }

            if (shouldReturnEmptySuggestions) {
                return new ArrayList<>();
            }

            // Return mock outfit suggestions based on numberOfSuggestions
            List<String> suggestions = new ArrayList<>();
            for (int i = 1; i <= numberOfSuggestions; i++) {
                suggestions.add("Outfit " + i + ": Mock outfit suggestion with " +
                        "clothing items suitable for " + forecast.getSlots().get(0).getTemperature() + "°C");
            }

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

        public boolean wasGenerateMultipleOutfitSuggestionsCalled() {
            return generateMultipleOutfitSuggestionsCalled;
        }

        public User getLastUserUsed() {
            return lastUserUsed;
        }

        public DailyForecast getLastForecastUsed() {
            return lastForecastUsed;
        }

        public int getLastNumberOfSuggestionsRequested() {
            return lastNumberOfSuggestionsRequested;
        }
    }
}
