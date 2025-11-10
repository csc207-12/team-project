package use_case;

/**
 * Input boundary for daily forecast use case.
 */
public interface DailyForecastInputBoundary {
    void getDailyForecast(DailyForecastInputData inputData);
}
