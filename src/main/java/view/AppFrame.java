package view;

import data_access.user_storage.UserSession;
import entity.User;

import javax.swing.*;
import java.awt.*;

public class AppFrame extends JFrame {

    public AppFrame()  {
        User currentUser = UserSession.getInstance().getCurrentUser();

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // create main panel
        JPanel appPanel = new JPanel();
        appPanel.setLayout(new BoxLayout(appPanel, BoxLayout.X_AXIS));

        WeatherApp weatherApp = new WeatherApp(currentUser);
        OutfitSuggestionPanel outfitSuggestionPanel = new OutfitSuggestionPanel(currentUser);

        appPanel.add(weatherApp);
        appPanel.add(Box.createHorizontalStrut(20)); // space between panels
        appPanel.add(outfitSuggestionPanel);
        appPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        add(appPanel, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);
    }
}
