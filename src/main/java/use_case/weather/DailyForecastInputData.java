package use_case.weather;

/**
 * Input data for the "get today's daily forecast" use case.
 * cityName comes from the user's stored profile location.
 * It should not be null or empty.
 */
public class DailyForecastInputData {
    public final String cityName;

    public DailyForecastInputData(String cityName) {
        this.cityName = cityName;
    }
}