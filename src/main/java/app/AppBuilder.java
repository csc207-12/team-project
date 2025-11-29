package app;

import data_access.user_storage.UserSession;
import entity.User;
import view.LoginPanel;
import view.OutfitSuggestionPanel;
import view.WeatherApp;

import javax.swing.*;
import java.awt.*;

// AppBuilder constructs the application using a builder pattern
// Views create their own dependencies internally
public class AppBuilder {

    private LoginPanel loginView;

    public AppBuilder addLoginView() {
        loginView = new LoginPanel();
        return this;
    }

    public AppBuilder addLoginUseCase() {
        if (loginView != null) {
            loginView.setOnLoginSuccess(() -> {
                // Get the logged-in user from UserSession
                User currentUser = UserSession.getInstance().getCurrentUser();
                System.out.println("Login successful! Current user: " + currentUser.getName());

                // Build and show the main application frame
                JFrame mainAppFrame = buildMainApplicationFrame(currentUser);
                mainAppFrame.setVisible(true);

                // Close the login window
                loginView.dispose();
            });
        }
        return this;
    }

    // Build the main application frame with WeatherApp and OutfitSuggestionPanel
    private JFrame buildMainApplicationFrame(User currentUser) {
        JFrame frame = new JFrame("Weather & Outfit Application");
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Create main panel to hold both views
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));

        // Create the weather and outfit views
        WeatherApp weatherApp = new WeatherApp(currentUser);
        OutfitSuggestionPanel outfitSuggestionPanel = new OutfitSuggestionPanel(currentUser);

        // Add views to main panel
        mainPanel.add(weatherApp);
        mainPanel.add(Box.createHorizontalStrut(20)); // space between panels
        mainPanel.add(outfitSuggestionPanel);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        frame.add(mainPanel, BorderLayout.CENTER);
        frame.pack();
        frame.setLocationRelativeTo(null);

        return frame;
    }

    public JFrame build() {
        // Return the login view as the initial screen
        return loginView;
    }
}

