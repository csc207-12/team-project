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

        // View (Swing)
        WeatherApp app = new WeatherApp(controller, viewModel);
        app.setVisible(true);
    }
}
