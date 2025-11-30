package data_access.weather;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ForecastAPIGatewayImplTest {

    @Test
    void request3hForecastJsonReturnsJsonForValidCity() throws Exception {
        ForecastAPIGatewayImpl gateway = new ForecastAPIGatewayImpl();

        String json = gateway.request3hForecastJson("Toronto");

        assertNotNull(json);
        assertFalse(json.isEmpty());

        JSONObject root = new JSONObject(json);
        assertTrue(root.has("city"), "JSON should include city object");
        assertTrue(root.has("list"), "JSON should include list array");

        String cityName = root.getJSONObject("city").optString("name");
        assertEquals("Toronto", cityName, "returned city name should be the same as required");
    }
}
