package interface_adapter;

import use_case.AccessoryOutputBoundary;
import use_case.AccessoryOutputData;

public class AccessoryPresenter implements AccessoryOutputBoundary {

    private final WeatherViewModel viewModel;

    public AccessoryPresenter(WeatherViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void presentAccessorySuggestions(AccessoryOutputData outputData) {
        viewModel.setAccessories(outputData.accessories);
        System.out.println("AccessoryOutputPresenter: accessories=" + outputData.accessories + " message=" + outputData.message);
        viewModel.setStatusMessage(outputData.message != null ? outputData.message : "");
        viewModel.setSuccess(outputData.success);
    }
}
