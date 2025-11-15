package interface_adapter;

import use_case.AccessoryInputBoundary;
import use_case.AccessoryInputData;

public class AccessoryController {
    private final AccessoryInputBoundary interactor;

    public AccessoryController(AccessoryInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void requestAccessories(String cityName, String purpose) {
        if (purpose == null || purpose.trim().isEmpty()) {
            // Treat empty purpose as generic "everyday"
            purpose = "everyday";
        }
        interactor.getAccessorySuggestions(new AccessoryInputData(cityName, purpose.trim().toLowerCase()));
    }
}