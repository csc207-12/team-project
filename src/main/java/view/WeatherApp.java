package view;

import interface_adapter.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * WeatherApp: main window composed of top controls + forecast grid + advice panel.
 */
public class WeatherApp extends JFrame {

    private final JTextField cityField = new JTextField(16);
    private final JButton searchBtn = new JButton("Search Weather");
    private final JButton locationBtn = new JButton("Use My Location");
    private final JLabel statusLabel = new JLabel(" ");

    private final ForecastPanel forecastPanel = new ForecastPanel();
    private final AdvicePanel advicePanel = new AdvicePanel();
    private final AccessoryPanel accessoryPanel = new AccessoryPanel();

    private final JComboBox<String> purposeCombo = new JComboBox<>(
            new String[]{"work","gym","travel","everyday","beach","date"}
    );

    private final DailyForecastController controller;
    private final AccessoryController accessoryController;
    private final WeatherViewModel viewModel;

    public WeatherApp(DailyForecastController controller, WeatherViewModel viewModel, AccessoryController accessoryController) {
        super("Weather");
        this.controller = controller;
        this.viewModel = viewModel;
        this.accessoryController = accessoryController;

        initUI();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
    }

    private void initUI() {
        // Top controls
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("City:"));
        top.add(cityField);
        top.add(searchBtn);
        top.add(locationBtn);
        top.add(new JLabel("Purpose:"));
        top.add(purposeCombo);

        // Center: forecast + advice + accessories
        JPanel center = new JPanel(new BorderLayout());
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(advicePanel, BorderLayout.NORTH);
        rightPanel.add(accessoryPanel, BorderLayout.CENTER);
        center.add(forecastPanel, BorderLayout.CENTER);
        center.add(rightPanel, BorderLayout.EAST);

        // Bottom status
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(statusLabel, BorderLayout.WEST);

        setLayout(new BorderLayout(6, 6));
        add(top, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        // Actions
        searchBtn.addActionListener(e -> runInBackground(() -> {
            controller.searchByCity(cityField.getText());
        }));

        locationBtn.addActionListener(e -> runInBackground(() -> {
            controller.useMyLocation();
        }));

        purposeCombo.addActionListener(e -> runInBackground(() -> {
            String purpose = (String) purposeCombo.getSelectedItem();
            String cityToUse = viewModel.getCity();
            if (cityToUse == null || cityToUse.trim().isEmpty()) cityToUse = cityField.getText();
            accessoryController.requestAccessories(cityToUse, purpose);
        }));
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

        // Set icons (keep separate so text renders immediately even if icon slower)
        if (slots != null) {
            for (int i = 0; i < slots.size(); i++) {
                String code = slots.get(i).iconCode;
                Icon icon = IconLoader.getIcon(code);
                forecastPanel.setRowIcon(i, icon);
            }
        }

        // Advice
        advicePanel.setAdviceText(viewModel.getAdviceText());

        // Accessories
        accessoryPanel.setAccessories(viewModel.getAccessories());

        // Status
        statusLabel.setText(viewModel.getStatusMessage());
    }

    private void setControlsEnabled (boolean enabled){
        searchBtn.setEnabled(enabled);
        locationBtn.setEnabled(enabled);
        cityField.setEnabled(enabled);
        purposeCombo.setEnabled(enabled);
    }
}

