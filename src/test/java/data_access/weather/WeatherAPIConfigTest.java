package data_access.weather;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WeatherAPIConfigTest {

    @Test
    void apiKeyIsConfigured() {
        assertNotNull(WeatherAPIConfig.API_KEY);
        assertFalse(WeatherAPIConfig.API_KEY.trim().isEmpty());
}
}
