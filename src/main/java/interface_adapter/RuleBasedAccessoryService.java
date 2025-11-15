package interface_adapter;

import entity.DailyForecast;
import entity.ForecastSlot;
import use_case.AccessoryService;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RuleBasedAccessoryService implements AccessoryService {
    @Override
    public List<String> recommendAccessories(DailyForecast forecast, String purpose) {
        List<String> out = new ArrayList<>();

        boolean willRain = forecast.getSlots().stream().anyMatch(s ->
                (s.getPrecipProbability() != null && s.getPrecipProbability() >= 0.5)
                        || s.getDescription().toLowerCase(Locale.ROOT).contains("rain")
                        || s.getDescription().toLowerCase(Locale.ROOT).contains("drizzle")
        );

        boolean windy = forecast.getSlots().stream().anyMatch(s ->
                s.getWindSpeed() != null && s.getWindSpeed() >= 10.0);

        double minTemp = forecast.getSlots().stream()
                .map(ForecastSlot::getTemperature).min(Double::compare).orElse(Double.NaN);
        double maxTemp = forecast.getSlots().stream()
                .map(ForecastSlot::getTemperature).max(Double::compare).orElse(Double.NaN);

        // Weather
        if (willRain) out.add("Umbrella");
        if (!Double.isNaN(minTemp) && minTemp < 8) out.add("Warm hat/gloves");
        if (!Double.isNaN(maxTemp) && maxTemp > 28) out.add("Water bottle");
        if (windy) out.add("Secure hat / windproof jacket");

        // Purpose
        if (purpose != null) {
            String p = purpose.toLowerCase(Locale.ROOT);
            if (p.contains("work")) out.add("Bag / briefcase");
            else if (p.contains("gym")) out.add("Gym bag");
            else if (p.contains("travel")) out.add("Suitcase / travel bag");
            else if (p.contains("date")) out.add("Small clutch / wallet");
            else if (p.contains("beach")) out.add("Sunscreen / sunglasses");
        }

        return out;
    }
}
