package use_case.weather;

import entity.DailyForecast;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DailyForecastInteractor.
 *
 * These tests use fake implementations of ForecastAPIGateway,
 * AdviceService and DailyForecastOutputBoundary, so no real HTTP is called.
 */
class DailyForecastInteractorTest {

    @Test
    void givenValidCity_interactorBuildsOutputAndCallsPresenter() throws Exception {
        // Arrange
        FakeForecastAPIGateway gateway = new FakeForecastAPIGateway();
        FakeAdviceService adviceService = new FakeAdviceService();
        CapturingOutputBoundary outputBoundary = new CapturingOutputBoundary();

        DailyForecastInteractor interactor = new DailyForecastInteractor(
                gateway,
                adviceService,
                outputBoundary
        );

        DailyForecastInputData inputData = new DailyForecastInputData("Toronto");

        // Act
        interactor.getDailyForecast(inputData);

        // Assert: presenter should have been called
        assertNotNull(outputBoundary.capturedOutput, "OutputBoundary should have been called.");

        DailyForecastOutputData out = outputBoundary.capturedOutput;

        // Basic fields
        assertTrue(out.isSuccess(), "success flag should be true");
        assertEquals("Toronto", out.getCityName());
        assertEquals("OK", out.getStatusMessage());
        assertEquals("TEST_ADVICE", out.getAdvice());

        // Slots: fake gateway returns 4 time slots for one day
        List<SlotDTO> slots = out.getSlots();
        assertNotNull(slots);
        assertEquals(4, slots.size(), "Expected 4 slots (Morning/Afternoon/Evening/Overnight)");

        // Check the first slot (Morning)
        SlotDTO first = slots.get(0);
        assertEquals("Morning", first.getLabel());
        assertEquals(10.0, first.getTemperature(), 0.0001);
        assertEquals("clear sky", first.getDescription());
        assertEquals("01d", first.getIconCode());
        assertEquals(0.0, first.getPrecipProbability());
        assertEquals(2.0, first.getWindSpeed());
        assertEquals(8.0, first.getFeelsLike());
    }

    @Test
    void givenEmptyCity_interactorReturnsFailureAndDoesNotCallGateway() throws Exception {
        // Arrange
        FakeForecastAPIGateway gateway = new FakeForecastAPIGateway();
        FakeAdviceService adviceService = new FakeAdviceService();
        CapturingOutputBoundary outputBoundary = new CapturingOutputBoundary();

        DailyForecastInteractor interactor = new DailyForecastInteractor(
                gateway,
                adviceService,
                outputBoundary
        );

        // cityName is blank -> should be treated as error
        DailyForecastInputData inputData = new DailyForecastInputData("   ");

        // Act
        interactor.getDailyForecast(inputData);

        // Assert
        assertNotNull(outputBoundary.capturedOutput, "Output should still be produced.");
        DailyForecastOutputData out = outputBoundary.capturedOutput;

        assertFalse(out.isSuccess());
        assertEquals("", out.getCityName());
        assertEquals("City name is required.", out.getStatusMessage());
        assertTrue(out.getSlots().isEmpty(), "No slots expected on error.");

        // Gateway should never be called for invalid input
        assertEquals(0, gateway.requestCount);
    }

    //Fake dependencies for testing

    /**
     * Fake ForecastAPIGateway: returns hard-coded JSON instead of calling HTTP.
     */
    private static class FakeForecastAPIGateway implements ForecastAPIGateway {

        int requestCount = 0;

        @Override
        public String request3hForecastJson(String cityName) throws Exception {
            requestCount++;

            // JSON that roughly matches OpenWeatherMap 5-day/3-hour format.
            // We include 4 entries on the same day at 09:00, 15:00, 19:00, 23:00.
            return "{"
                    + "\"city\": {\"name\": \"Toronto\", \"timezone\": 0},"
                    + "\"list\": ["
                    + "{"
                    + "  \"dt\": 1735722000,"
                    + "  \"main\": {\"temp\": 10.0, \"feels_like\": 8.0},"
                    + "  \"weather\": [{\"description\": \"clear sky\", \"icon\": \"01d\"}],"
                    + "  \"wind\": {\"speed\": 2.0},"
                    + "  \"pop\": 0.0"
                    + "},"
                    + "{"
                    + "  \"dt\": 1735743600,"
                    + "  \"main\": {\"temp\": 12.0, \"feels_like\": 11.0},"
                    + "  \"weather\": [{\"description\": \"few clouds\", \"icon\": \"02d\"}],"
                    + "  \"wind\": {\"speed\": 3.5},"
                    + "  \"pop\": 0.1"
                    + "},"
                    + "{"
                    + "  \"dt\": 1735758000,"
                    + "  \"main\": {\"temp\": 8.0, \"feels_like\": 7.0},"
                    + "  \"weather\": [{\"description\": \"overcast\", \"icon\": \"04d\"}],"
                    + "  \"wind\": {\"speed\": 4.0},"
                    + "  \"pop\": 0.2"
                    + "},"
                    + "{"
                    + "  \"dt\": 1735772400,"
                    + "  \"main\": {\"temp\": 3.0, \"feels_like\": 1.0},"
                    + "  \"weather\": [{\"description\": \"light snow\", \"icon\": \"13n\"}],"
                    + "  \"wind\": {\"speed\": 5.0},"
                    + "  \"pop\": 0.6"
                    + "}"
                    + "]"
                    + "}";
        }
    }

    /**
     * Fake AdviceService: always returns the same advice text.
     */
    private static class FakeAdviceService implements AdviceService {
        @Override
        public String makeAdvice(DailyForecast dailyForecast) {
            return "TEST_ADVICE";
        }
    }

    /**
     * OutputBoundary that just remembers the last output it received.
     */
    private static class CapturingOutputBoundary implements DailyForecastOutputBoundary {
        DailyForecastOutputData capturedOutput;

        @Override
        public void presentDailyForecast(DailyForecastOutputData outputData) {
            this.capturedOutput = outputData;
        }
    }
}
