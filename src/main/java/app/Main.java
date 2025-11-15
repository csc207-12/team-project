import interface_adapter.*;
import use_case.*;
import view.WeatherApp;
import data_access.*;

public class Main {
    public static void main(String[] args) {
        // ViewModel
        WeatherViewModel viewModel = new WeatherViewModel();

        // Presenter
        DailyForecastPresenter presenter = new DailyForecastPresenter(viewModel);

        // Gateways & services
        ForecastAPIGateway forecastGateway = new ForecastAPIGatewayImpl();
        LocationService locationService = new LocationServiceImpl();
        AdviceService adviceService = new RuleBasedAdviceService();

        // Interactor (use case)
        DailyForecastInputBoundary interactor =
                new DailyForecastInteractor(forecastGateway, locationService, adviceService, presenter);

        // Controller
        DailyForecastController controller = new DailyForecastController(interactor, viewModel);

        // Accessory
        use_case.AccessoryService accessoryService = new interface_adapter.RuleBasedAccessoryService();
        interface_adapter.AccessoryPresenter accessoryPresenter = new interface_adapter.AccessoryPresenter(viewModel);
        use_case.AccessoryInputBoundary accessoryInteractor = new use_case.AccessoryInteractor(
                forecastGateway, locationService, accessoryService, accessoryPresenter
        );
        interface_adapter.AccessoryController accessoryController = new interface_adapter.AccessoryController(accessoryInteractor);

        // View (Swing)
        WeatherApp app = new WeatherApp(controller, viewModel, accessoryController);
        app.setVisible(true);
    }
}
