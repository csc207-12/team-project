package use_case;

import entity.DailyForecast;
import java.util.List;

public interface AccessoryService {
    List<String> recommendAccessories(DailyForecast forecast, String purpose);
}
