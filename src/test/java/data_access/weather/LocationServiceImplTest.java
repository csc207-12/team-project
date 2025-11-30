package data_access.weather;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LocationServiceImplTest {

    @Test
    void getCurrentCityReturnsNonEmptyCity() throws Exception {
        LocationServiceImpl service = new LocationServiceImpl();

        String city = service.getCurrentCity();

        assertNotNull(city);
        assertFalse(city.trim().isEmpty());
        System.out.println("Detected city = " + city);
    }
}
