package app;

import javax.swing.*;

/**
 * Main entry point for the application.
 * Uses AppBuilder pattern to construct the application.
 */
public class Main {

    public static void main(String[] args) {
        AppBuilder appBuilder = new AppBuilder();
        JFrame application = appBuilder
                .addLoginView()
                .addLoginUseCase()
                .build();

        application.pack();
        application.setLocationRelativeTo(null);
        application.setVisible(true);
    }
}
