package use_case;

import entity.DailyForecast;
import entity.ForecastSlot;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Interactor for accessory recommendation use case.
 */
public class AccessoryInteractor implements AccessoryInputBoundary {

    private final ForecastAPIGateway forecastGateway;
    private final LocationService locationService;
    private final AccessoryService accessoryService;
    private final AccessoryOutputBoundary presenter;

    public AccessoryInteractor(ForecastAPIGateway forecastGateway,
                               LocationService locationService,
                               AccessoryService accessoryService,
                               AccessoryOutputBoundary presenter) {
        this.forecastGateway = forecastGateway;
        this.locationService = locationService;
        this.accessoryService = accessoryService;
        this.presenter = presenter;
    }

    @Override
    public void getAccessorySuggestions(AccessoryInputData inputData) {
        String resolvedCity = (inputData.cityName == null || inputData.cityName.trim().isEmpty())
                ? null : inputData.cityName.trim();

        System.out.println("AccessoryInteractor: requested purpose='" + inputData.purpose + "' city='" + resolvedCity + "'");

        try {
            if (resolvedCity == null) {
                resolvedCity = locationService.getCurrentCity();
            }

            String json = forecastGateway.request3hForecastJson(resolvedCity);
            JSONObject root = new JSONObject(json);
            JSONObject cityObj = root.getJSONObject("city");
            int tzShiftSec = cityObj.optInt("timezone", 0);
            ZoneOffset zoneOffset = ZoneOffset.ofTotalSeconds(tzShiftSec);

            LocalDate cityToday = Instant.now().atOffset(zoneOffset).toLocalDate();
            JSONArray arr = root.getJSONArray("list");

            List<JSONObject> todays = new ArrayList<>();
            for (int i = 0; i < arr.length(); i++) {
                JSONObject item = arr.getJSONObject(i);
                long dt = item.getLong("dt");
                LocalDateTime local = Instant.ofEpochSecond(dt).atOffset(zoneOffset).toLocalDateTime();
                if (local.toLocalDate().equals(cityToday)) {
                    item.put("_localHour", local.getHour());
                    item.put("_localDateTime", local.toString());
                    todays.add(item);
                }
            }

            if (todays.isEmpty()) {
                presenter.presentAccessorySuggestions(new AccessoryOutputData(
                        resolvedCity, new ArrayList<>(), false, "No forecast entries for today."));
                return;
            }

            ForecastSlot morning   = pickSlotForTargetHour(todays, zoneOffset, cityToday,  9, "Morning");
            ForecastSlot afternoon = pickSlotForTargetHour(todays, zoneOffset, cityToday, 15, "Afternoon");
            ForecastSlot evening   = pickSlotForTargetHour(todays, zoneOffset, cityToday, 19, "Evening");
            ForecastSlot overnight = pickSlotForTargetHour(todays, zoneOffset, cityToday, 23, "Overnight");

            List<ForecastSlot> slots = new ArrayList<>();
            if (morning != null)   slots.add(morning);
            if (afternoon != null) slots.add(afternoon);
            if (evening != null)   slots.add(evening);
            if (overnight != null) slots.add(overnight);

            DailyForecast forecast = new DailyForecast(resolvedCity, cityToday, slots);

            List<String> accessories = accessoryService.recommendAccessories(forecast, inputData.purpose);

            presenter.presentAccessorySuggestions(new AccessoryOutputData(
                    resolvedCity, accessories, true, "OK"
            ));

        } catch (Exception e) {
            e.printStackTrace();
            presenter.presentAccessorySuggestions(new AccessoryOutputData(
                    resolvedCity == null ? "" : resolvedCity,
                    new ArrayList<>(), false, e.getMessage()
            ));
        }
    }

    private ForecastSlot pickSlotForTargetHour(List<JSONObject> todays,
                                               ZoneOffset zoneOffset,
                                               LocalDate cityToday,
                                               int targetHour,
                                               String label) {
        JSONObject best = todays.stream()
                .min(Comparator.comparingInt(o -> {
                    int h = o.optInt("_localHour", -1);
                    return Math.abs(h - targetHour);
                }))
                .orElse(null);

        if (best == null) return null;

        JSONObject main = best.getJSONObject("main");
        double temp = main.getDouble("temp");
        Double feelsLike = main.has("feels_like") ? main.getDouble("feels_like") : null;

        JSONArray weatherArr = best.getJSONArray("weather");
        JSONObject w0 = weatherArr.getJSONObject(0);
        String desc = w0.getString("description");
        String icon = w0.getString("icon");

        Double pop = best.has("pop") ? best.getDouble("pop") : null;
        Double wind = null;
        if (best.has("wind")) {
            JSONObject windObj = best.getJSONObject("wind");
            if (windObj.has("speed")) wind = windObj.getDouble("speed");
        }

        return new ForecastSlot(label, temp, desc, icon, pop, wind, feelsLike);
    }
}
