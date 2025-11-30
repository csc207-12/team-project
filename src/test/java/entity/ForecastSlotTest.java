package entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ForecastSlotTest {

    @Test
    void constructorAndGettersStoreAllFields() {
        ForecastSlot slot = new ForecastSlot(
                "Morning",
                10.0,
                "clear sky",
                "01d",
                0.2,
                3.5,
                8.0
        );

        assertEquals("Morning", slot.getLabel());
        assertEquals(10.0, slot.getTemperature(), 0.0001);
        assertEquals("clear sky", slot.getDescription());
        assertEquals("01d", slot.getIconCode());
        assertEquals(0.2, slot.getPrecipProbability());
        assertEquals(3.5, slot.getWindSpeed());
        assertEquals(8.0, slot.getFeelsLike());
    }

    @Test
    void allowsNullForOptionalFields() {
        ForecastSlot slot = new ForecastSlot(
                "Night",
                5.0,
                "cloudy",
                "02d",
                null,
                null,
                null
        );

        assertEquals("Night", slot.getLabel());
        assertEquals(5.0, slot.getTemperature(), 0.0001);
        assertEquals("cloudy", slot.getDescription());
        assertEquals("02d", slot.getIconCode());
        assertNull(slot.getPrecipProbability());
        assertNull(slot.getWindSpeed());
        assertNull(slot.getFeelsLike());
    }
}
