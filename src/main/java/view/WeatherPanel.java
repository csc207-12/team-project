package view;

import data_access.weather.ForecastAPIGatewayImpl;
import entity.User;
import interface_adapter.weather.DailyForecastController;
import interface_adapter.weather.DailyForecastPresenter;
import interface_adapter.weather.RuleBasedAdviceService;
import interface_adapter.weather.WeatherViewModel;
import use_case.weather.AdviceService;
import use_case.weather.DailyForecastInputBoundary;
import use_case.weather.DailyForecastInteractor;
import use_case.weather.ForecastAPIGateway;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * WeatherPanel: main window composed of top controls + forecast grid + advice panel.
 * Now it automatically loads the forecast for the user's saved location.
 */
public class WeatherPanel extends JPanel {

    private final JLabel statusLabel = new JLabel(" ");
    private final JButton purposeBtn = new JButton("Purpose");
    private final JButton logoutBtn = new JButton("Logout");

    private final ForecastPanel forecastPanel = new ForecastPanel();
    private final AdvicePanel advicePanel = new AdvicePanel();

    private final DailyForecastController controller;
    private final WeatherViewModel viewModel;

    private Runnable onLogoutCallback;

    public WeatherPanel(User currentUser) {

        // ViewModel
        viewModel = new WeatherViewModel();

        // Presenter
        DailyForecastPresenter presenter = new DailyForecastPresenter(viewModel);

        // Gateways & services
        ForecastAPIGateway forecastGateway = new ForecastAPIGatewayImpl();
        AdviceService adviceService = new RuleBasedAdviceService();

        // Interactor (use case)
        DailyForecastInputBoundary interactor =
                new DailyForecastInteractor(forecastGateway, adviceService, presenter);

        // Controller
        controller = new DailyForecastController(interactor, viewModel);

        // Build UI and immediately load user's city
        initUI(currentUser);
        runInBackground(() -> controller.searchByCity(currentUser.getLocation().trim()));
    }

    private void initUI(User currentUser) {
        // Top controls
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("City: " + currentUser.getLocation().trim()));
        top.add(purposeBtn);
        top.add(logoutBtn);

        // Center: left forecast grid (+ right advice panel if you want)
        JPanel center = new JPanel(new BorderLayout());
        center.setPreferredSize(new Dimension(800, 500));
        center.add(forecastPanel, BorderLayout.CENTER);
        // If you want to show the advice panel as well, uncomment:
        // center.add(advicePanel, BorderLayout.EAST);

        // Bottom status
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(statusLabel, BorderLayout.WEST);

        setLayout(new BorderLayout(6, 6));
        add(top, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        // Buttons
        purposeBtn.addActionListener(e -> {
            PurposePanel purposePanel = new PurposePanel();
            purposePanel.setVisible(true);
        });

        logoutBtn.addActionListener(e -> {
            if (onLogoutCallback != null) {
                onLogoutCallback.run();
            }
        });
    }

    /** Run controller call in background, then refresh UI from ViewModel on EDT. */
    private void runInBackground(Runnable task) {
        setControlsEnabled(false);
        statusLabel.setText("Loading...");
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                task.run(); // call controller (which calls use case synchronously)
                return null;
            }

            @Override
            protected void done() {
                // Refresh forecast grid + advice + status
                refreshFromViewModel();
                setControlsEnabled(true);
            }
        }.execute();
    }

    /** Read ViewModel and render components. */
    private void refreshFromViewModel() {
        // Table
        List<WeatherViewModel.SlotView> slots = viewModel.getTodaySlots();
        forecastPanel.render(slots);

        // Set icons
        if (slots != null) {
            for (int i = 0; i < slots.size(); i++) {
                String code = slots.get(i).iconCode;
                Icon icon = IconLoader.getIcon(code);
                forecastPanel.setRowIcon(i, icon);
            }
        }

        // Advice
        advicePanel.setAdviceText(viewModel.getAdviceText());

        // Status
        statusLabel.setText(viewModel.getStatusMessage());
    }

    private void setControlsEnabled(boolean enabled) {
        purposeBtn.setEnabled(enabled);
        logoutBtn.setEnabled(enabled);
    }

    public void setOnLogout(Runnable callback) {
        this.onLogoutCallback = callback;
    }
}
