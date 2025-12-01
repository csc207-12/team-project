package use_case.purpose;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PurposeInteractor.
 * Tests the accessory recommendation use case (Use Case 6).
 */
class PurposeInteractorTest {

    private PurposeInteractor interactor;
    private TestPurposePresenter presenter;
    private TestPurposeDataAccess dataAccess;

    @BeforeEach
    void setUp() {
        presenter = new TestPurposePresenter();
        dataAccess = new TestPurposeDataAccess();
        interactor = new PurposeInteractor(dataAccess, presenter);
    }

    /**
     * Test the main success path (REQUIRED test for Use Case 6).
     */
    @Test
    void testSuccessfulAccessoryGeneration() {
        // Arrange
        PurposeInputData inputData = new PurposeInputData("gym");

        // Act
        interactor.generateAccessories(inputData);

        // Assert
        assertTrue(presenter.wasSuccessCalled(),
                "Success view should be called");
        assertFalse(presenter.wasFailureCalled(),
                "Failure view should not be called");

        PurposeOutputData outputData = presenter.getOutputData();
        assertNotNull(outputData, "Output data should not be null");

        String suggestions = outputData.getAccessoriesText();
        assertNotNull(suggestions, "Suggestions should not be null");
        assertFalse(suggestions.isEmpty(), "Suggestions should not be empty");

        assertEquals("gym", dataAccess.getLastPurposeUsed(),
                "Purpose passed to the data access interface should match input");
    }

    /**
     * Test empty purpose input.
     */
    @Test
    void testEmptyPurpose() {
        // Arrange
        PurposeInputData inputData = new PurposeInputData("");

        // Act
        interactor.generateAccessories(inputData);

        // Assert
        assertTrue(presenter.wasFailureCalled(),
                "Failure view should be called on empty input");
        assertTrue(presenter.getErrorMessage().contains("Please enter"),
                "Error should indicate missing purpose");
    }

    @Test
    void testNullPurpose() {
        // Arrange
        PurposeInputData inputData = new PurposeInputData(null);

        // Act
        interactor.generateAccessories(inputData);

        // Assert
        assertTrue(presenter.wasFailureCalled(),
                "Failure view should be called when purpose is null");
        assertTrue(presenter.getErrorMessage().contains("Please enter"),
                "Error message should indicate missing purpose");
    }

    /**
     * Test when the data access returns null suggestions.
     */
    @Test
    void testNullSuggestions() {
        // Arrange
        dataAccess.setReturnNullSuggestions(true);
        PurposeInputData inputData = new PurposeInputData("work");

        // Act
        interactor.generateAccessories(inputData);

        // Assert
        assertTrue(presenter.wasFailureCalled(),
                "Failure view should be called when suggestions are null");
        assertTrue(presenter.getErrorMessage().contains("Failed to generate"),
                "Error message should indicate suggestion generation failure");
    }

    /**
     * Test when the data access returns an empty string.
     */
    @Test
    void testEmptySuggestions() {
        // Arrange
        dataAccess.setReturnEmptySuggestions(true);
        PurposeInputData inputData = new PurposeInputData("travel");

        // Act
        interactor.generateAccessories(inputData);

        // Assert
        assertTrue(presenter.wasFailureCalled(),
                "Failure view should be called when suggestions are empty");
        assertTrue(presenter.getErrorMessage().contains("Failed to generate"),
                "Error message should indicate empty suggestions");
    }

    /**
     * Test when the data access interface throws an exception.
     */
    @Test
    void testExceptionThrown() {
        // Arrange
        dataAccess.setShouldThrowException(true);
        PurposeInputData inputData = new PurposeInputData("school");

        // Act
        interactor.generateAccessories(inputData);

        // Assert
        assertTrue(presenter.wasFailureCalled(),
                "Failure view should be called when an exception occurs");
        assertTrue(presenter.getErrorMessage().contains("error occurred"),
                "Error message should indicate exception");
    }



    // ===================== Mock Implementations =====================

    private static class TestPurposePresenter implements PurposeOutputBoundary {

        private boolean successCalled = false;
        private boolean failureCalled = false;
        private PurposeOutputData outputData = null;
        private String errorMessage = null;

        @Override
        public void presentSuccess(PurposeOutputData outputData) {
            this.successCalled = true;
            this.outputData = outputData;
        }

        @Override
        public void presentFailure(String errorMessage) {
            this.failureCalled = true;
            this.errorMessage = errorMessage;
        }

        public boolean wasSuccessCalled() {
            return successCalled;
        }

        public boolean wasFailureCalled() {
            return failureCalled;
        }

        public PurposeOutputData getOutputData() {
            return outputData;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }


    private static class TestPurposeDataAccess implements PurposeAccessoryDataAccessInterface {

        private boolean returnNullSuggestions = false;
        private boolean returnEmptySuggestions = false;
        private boolean shouldThrowException = false;

        private String lastPurposeUsed;

        @Override
        public String generateAccessorySuggestions(String purpose) {

            lastPurposeUsed = purpose;

            if (shouldThrowException) {
                throw new RuntimeException("Error generating suggestions");
            }
            if (returnNullSuggestions) {
                return null;
            }
            if (returnEmptySuggestions) {
                return "";
            }

            return "Recommended accessories for " + purpose + ": bag, hat, water bottle.";
        }

        // Setter methods for test configuration
        public void setReturnNullSuggestions(boolean value) {
            this.returnNullSuggestions = value;
        }

        public void setReturnEmptySuggestions(boolean value) {
            this.returnEmptySuggestions = value;
        }

        public void setShouldThrowException(boolean value) {
            this.shouldThrowException = value;
        }

        public String getLastPurposeUsed() {
            return lastPurposeUsed;
        }
    }
}
