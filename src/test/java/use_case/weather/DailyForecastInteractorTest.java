package use_case.weather;

import entity.DailyForecast;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for DailyForecastInteractor.
 * This test focuses on the "auto location" path:
 * inputData.cityName is null, so the interactor must call LocationService.
 */
class DailyForecastInteractorTest {

    @Test
    void givenNoCityName_interactorUsesLocationServiceAndBuildsOutput() throws Exception {
        // Arrange: fake dependencies
        FakeForecastAPIGateway gateway = new FakeForecastAPIGateway();
        FakeLocationService locationService = new FakeLocationService();
        FakeAdviceService adviceService = new FakeAdviceService();
        CapturingOutputBoundary outputBoundary = new CapturingOutputBoundary();

        DailyForecastInteractor interactor = new DailyForecastInteractor(
                gateway,
                locationService,
                adviceService,
                outputBoundary
        );

        // InputData with null city name -> should fall back to LocationService
        DailyForecastInputData inputData = new DailyForecastInputData(null);

        // Act: run the use case
        interactor.getDailyForecast(inputData);

        // Assert: the presenter was called and output is consistent
        DailyForecastOutputData out = outputBoundary.capturedOutput;
        assertNotNull(out, "OutputBoundary should have been called.");

        // Basic fields
        assertEquals("Toronto", out.getCityName());
        assertTrue(out.isSuccess(), "Success flag should be true.");
        assertEquals("OK", out.getStatusMessage());

        // Slots list
        List<SlotDTO> slots = out.getSlots();
        assertNotNull(slots);
        assertFalse(slots.isEmpty());
        // The interactor always tries to build 4 time-of-day slots.
        assertEquals(4, slots.size());

        // Check first slot (Morning)
        SlotDTO first = slots.get(0);
        assertEquals("Morning", first.getLabel());
        assertEquals(10.0, first.getTemperature(), 0.0001);
        assertEquals("clear sky", first.getDescription());
        assertEquals("01d", first.getIconCode());
        assertEquals(0.0, first.getPrecipProbability());
        assertEquals(2.0, first.getWindSpeed());
        assertEquals(8.0, first.getFeelsLike());

        // Advice text from FakeAdviceService
        assertEquals("TEST_ADVICE", out.getAdvice());
    }

    // ========== Fake dependencies used only in this test ==========

    /**
     * Fake ForecastAPIGateway: returns hard-coded JSON instead of calling HTTP.
     */
    private static class FakeForecastAPIGateway implements ForecastAPIGateway {
        @Override
        public String request3hForecastJson(String cityName) {
            // Minimal JSON with two 3-hour entries on the same day in Toronto.
            // Timezone 0 (UTC) is enough for the interactor logic.
            return "{\n" +
                    "  \"city\": {\"name\": \"Toronto\", \"timezone\": 0},\n" +
                    "  \"list\": [\n" +
                    "    {\n" +
                    "      \"dt\": 1735712400,\n" + // 2025-01-01T09:00:00Z
                    "      \"main\": {\"temp\": 10.0, \"feels_like\": 8.0},\n" +
                    "      \"weather\": [{\"description\": \"clear sky\", \"icon\": \"01d\"}],\n" +
                    "      \"wind\": {\"speed\": 2.0},\n" +
                    "      \"pop\": 0.0\n" +
                    "    },\n" +
                    "    {\n" +
                    "      \"dt\": 1735734000,\n" + // 2025-01-01T15:00:00Z
                    "      \"main\": {\"temp\": 5.0, \"feels_like\": 1.0},\n" +
                    "      \"weather\": [{\"description\": \"light rain\", \"icon\": \"10n\"}],\n" +
                    "      \"wind\": {\"speed\": 4.0},\n" +
                    "      \"pop\": 0.6\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}";
        }
    }

    /**
     * Fake LocationService: pretends the current city is always Toronto.
     */
    private static class FakeLocationService implements LocationService {
        @Override
        public String getCurrentCity() {
            return "Toronto";
        }
    }

    /**
     * Fake AdviceService: always returns the same advice text.
     */
    private static class FakeAdviceService implements AdviceService {
        @Override
        public String makeAdvice(DailyForecast forecast) {
            return "TEST_ADVICE";
        }
    }

    /**
     * OutputBoundary that just remembers the last output it received.
     * This is how the test inspects what the interactor produced.
     */
    private static class CapturingOutputBoundary implements DailyForecastOutputBoundary {
        DailyForecastOutputData capturedOutput;

        @Override
        public void presentDailyForecast(DailyForecastOutputData outputData) {
            this.capturedOutput = outputData;
        }
    }
}
