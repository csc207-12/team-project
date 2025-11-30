package app;

import data_access.user_storage.UserSession;
import entity.User;
import view.LoginPanel;
import view.OutfitSuggestionPanel;
import view.WeatherPanel;

import javax.swing.*;
import java.awt.*;

// AppBuilder constructs the application using a builder pattern
// Views create their own dependencies internally
public class AppBuilder {

    private static LoginPanel loginView;

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

                // Hide the login window instead of disposing
                loginView.setVisible(false);
            });
        }
        return this;
    }

    // Get the login view instance (used by StylePanel to return to login after signup)
    public static LoginPanel getLoginView() {
        return loginView;
    }

    // Build the main application frame with WeatherApp and OutfitSuggestionPanel
    private JFrame buildMainApplicationFrame(User currentUser) {
        JFrame frame = new JFrame("WeatherWear");
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Create main panel to hold both views
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));

        // Create the weather and outfit views
        WeatherPanel weatherPanel = new WeatherPanel(currentUser);
        OutfitSuggestionPanel outfitSuggestionPanel = new OutfitSuggestionPanel(currentUser);

        // Set up logout callback
        weatherPanel.setOnLogout(() -> {
            // Clear user session
            UserSession.getInstance().clearSession();

            // Close main app frame
            frame.dispose();

            // Clear login fields and reopen login panel
            loginView.clearFields();
            loginView.setVisible(true);
            loginView.setLocationRelativeTo(null);
        });

        // Add views to main panel
        mainPanel.add(weatherPanel);
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

