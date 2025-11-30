package use_case.outfit_suggestion;

import entity.DailyForecast;
import entity.ForecastSlot;
import entity.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OutfitSuggestionInteractorTest {

    /**
     * Helper to build a DailyForecast containing ONE ForecastSlot.
     */
    private DailyForecast makeForecast(double temp, String city) {

        ForecastSlot slot = new ForecastSlot(
                "Morning",     // label
                temp,          // temperature
                "cloudy",      // description
                "10d",         // icon code
                0.1,           // precip probability
                3.0,           // wind speed
                temp           // feelsLike
        );

        List<ForecastSlot> slots = new ArrayList<>();
        slots.add(slot);

        return new DailyForecast(
                city,
                LocalDate.now(),
                slots
        );
    }

    // ---------------- MOCK DAO ----------------
    private static class MockDAO implements OutfitSuggestionDataAccessInterface {

        DailyForecast forecastToReturn;
        List<String> suggestionsToReturn;

        @Override
        public DailyForecast getWeatherForecast(String location) {
            return forecastToReturn;
        }

        @Override
        public List<String> generateOutfitSuggestions(User user, DailyForecast forecast) {
            return suggestionsToReturn;
        }
    }

    // ---------------- MOCK PRESENTER ----------------
    private static class MockPresenter implements OutfitSuggestionOutputBoundary {

        OutfitSuggestionOutputData successData;
        String failureMessage;

        @Override
        public void prepareSuccessView(OutfitSuggestionOutputData outputData) {
            this.successData = outputData;
        }

        @Override
        public void prepareFailView(String errorMessage) {
            this.failureMessage = errorMessage;
        }
    }

    // ==========================================================
    // TEST 1 — SUCCESSFUL SUGGESTION GENERATION
    // ==========================================================
    @Test
    public void testSuccess() {

        User user = new User(
                "jason",
                "123",
                "toronto",
                "Male"
        );

        MockDAO dao = new MockDAO();
        dao.forecastToReturn = makeForecast(5.0, "Toronto");

        List<String> fakeSuggestions = new ArrayList<>();
        fakeSuggestions.add("Outfit 1: Wear hoodie and jeans.");
        fakeSuggestions.add("Outfit 2: Jacket with sneakers.");
        dao.suggestionsToReturn = fakeSuggestions;

        MockPresenter presenter = new MockPresenter();

        OutfitSuggestionInteractor interactor =
                new OutfitSuggestionInteractor(user, dao, presenter);

        OutfitSuggestionInputData input =
                new OutfitSuggestionInputData("jason", "toronto");

        interactor.execute(input);

        // Assertions
        assertNull(presenter.failureMessage);
        assertNotNull(presenter.successData);

        assertEquals("jason", presenter.successData.getUsername());
        assertEquals("Toronto", presenter.successData.getCity());
        assertEquals(5.0, presenter.successData.getTemperature());

        String combined = presenter.successData.getOutfitSuggestions();
        assertTrue(combined.contains("Outfit 1"));
        assertTrue(combined.contains("Outfit 2"));
    }

    // ==========================================================
    // TEST 2 — WEATHER FORECAST IS NULL
    // ==========================================================
    @Test
    public void testWeatherNullFailure() {

        User user = new User("jason", "123", "toronto", "Male");

        MockDAO dao = new MockDAO();
        dao.forecastToReturn = null; // simulate failure

        MockPresenter presenter = new MockPresenter();

        OutfitSuggestionInteractor interactor =
                new OutfitSuggestionInteractor(user, dao, presenter);

        OutfitSuggestionInputData input =
                new OutfitSuggestionInputData("jason", "toronto");

        interactor.execute(input);

        assertNull(presenter.successData);
        assertNotNull(presenter.failureMessage);
        assertTrue(presenter.failureMessage.contains("Could not retrieve weather data"));
    }

    // ==========================================================
    // TEST 3 — LLM RETURNS EMPTY SUGGESTIONS LIST
    // ==========================================================
    @Test
    public void testAIEmptyFailure() {

        User user = new User("jason", "123", "toronto", "Male");

        MockDAO dao = new MockDAO();
        dao.forecastToReturn = makeForecast(3.0, "Toronto");
        dao.suggestionsToReturn = new ArrayList<>(); // empty list

        MockPresenter presenter = new MockPresenter();

        OutfitSuggestionInteractor interactor =
                new OutfitSuggestionInteractor(user, dao, presenter);

        OutfitSuggestionInputData input =
                new OutfitSuggestionInputData("jason", "toronto");

        interactor.execute(input);

        assertNull(presenter.successData);
        assertNotNull(presenter.failureMessage);
        assertTrue(presenter.failureMessage.contains("Could not generate outfit suggestions"));
    }
}
