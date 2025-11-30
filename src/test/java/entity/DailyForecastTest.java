package entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DailyForecastTest {

    @Test
    void constructorAndGettersStoreCityDateAndSlots() {
        ForecastSlot morning = new ForecastSlot(
                "Morning",
                10.0,
                "clear sky",
                "01d",
                0.0,
                2.0,
                8.0
        );
        ForecastSlot evening = new ForecastSlot(
                "Evening",
                5.0,
                "light rain",
                "10n",
                0.6,
                4.0,
                1.0
        );

        List<ForecastSlot> slots = Arrays.asList(morning, evening);
        LocalDate today = LocalDate.of(2025, 1, 1);

        DailyForecast forecast = new DailyForecast("Toronto", today, slots);

        assertEquals("Toronto", forecast.getCity());
        assertEquals(today, forecast.getDate());
        assertEquals(slots, forecast.getSlots());
        assertSame(slots, forecast.getSlots());
    }
}
