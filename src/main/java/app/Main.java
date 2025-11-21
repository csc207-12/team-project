package app;

import data_access.user_storage.UserSession;
import entity.User;
import view.ApplicationPanel;
import view.LoginPanel;
import view.OutfitSuggestionPanel;
import view.WeatherApp;

public class Main {

    public static void main(String[] args) {
        LoginPanel loginPanel = new LoginPanel();

        // callback to execute after a successful login
        loginPanel.setOnLoginSuccess(() -> {
            // Now UserSession has been populated with the logged-in user
            User currentUser = UserSession.getInstance().getCurrentUser();
            System.out.println("Login successful! Current user: " + currentUser.getName());

//            // Initialize and show the weather and gemini windows here
//            // For example, launch weather app with currentUser
//            OutfitSuggestionPanel outfitPanel = new OutfitSuggestionPanel(currentUser);
//            outfitPanel.setVisible(true);
//
//            WeatherApp weatherApp = new WeatherApp(currentUser);
//            weatherApp.setVisible(true);

            // Initialize and show the main application panel here
            ApplicationPanel appPanel = new ApplicationPanel(currentUser);
            appPanel.setVisible(true);
        });

        loginPanel.setVisible(true);
    }
}
