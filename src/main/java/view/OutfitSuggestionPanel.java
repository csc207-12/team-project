package view;

import interface_adapter.outfit_suggestion.OutfitSuggestionController;
import interface_adapter.outfit_suggestion.OutfitSuggestionPresenter;
import interface_adapter.outfit_suggestion.OutfitSuggestionView;
import use_case.outfit_suggestion.OutfitSuggestionInteractor;
import entity.User;
import data_access.outfit_suggestion.OutfitSuggestionDataAccessObject;

import javax.swing.*;
import java.awt.*;

public class OutfitSuggestionPanel extends JFrame implements OutfitSuggestionView {

    private final OutfitSuggestionController controller;
    private final User currentUser;

    private final JTextArea suggestionsArea = new JTextArea(15, 40);
    private final JLabel statusLabel = new JLabel(" ");

    public OutfitSuggestionPanel(User currentUser) {
        this.currentUser = currentUser;
        OutfitSuggestionPresenter presenter = new OutfitSuggestionPresenter(this);
        OutfitSuggestionDataAccessObject dataAccess = new OutfitSuggestionDataAccessObject();
        OutfitSuggestionInteractor interactor = new OutfitSuggestionInteractor(
                currentUser,  // first parameter
                dataAccess,      // second parameter
                presenter        // third parameter
        );
        this.controller = new OutfitSuggestionController(interactor);

        setTitle("Get Outfit Suggestions for " + currentUser.getName()); // personalized title
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // create main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // header
        JLabel headerLabel = new JLabel("Get Personalized Outfit Suggestions");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(headerLabel);
        mainPanel.add(Box.createVerticalStrut(10));

        // input field (just location cuz user is already logged in)

//        mainPanel.add(createFieldPanel("Location:", locationField));
//        mainPanel.add(Box.createVerticalStrut(15));

        // button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton getSuggestionsButton = new JButton("Get Outfit Suggestions");
        getSuggestionsButton.setFont(new Font("Arial", Font.BOLD, 14));
        getSuggestionsButton.addActionListener(e -> onGetSuggestions(getSuggestionsButton));
        JButton generateImagesButton = new JButton("Generate Outfit Images");
        generateImagesButton.setFont(new Font("Arial", Font.BOLD, 14));
        generateImagesButton.addActionListener(e -> openImageGallery());
        buttonPanel.add(getSuggestionsButton);
        buttonPanel.add(generateImagesButton);
        add(buttonPanel, BorderLayout.NORTH);

        suggestionsArea.setEditable(false);
        suggestionsArea.setFont(new Font("Arial", Font.PLAIN, 13));
        suggestionsArea.setLineWrap(true);
        suggestionsArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(suggestionsArea);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(scrollPane);
        scrollPane.setPreferredSize(new Dimension(350, 450));

        add(mainPanel, BorderLayout.CENTER);

        // status bar at bottom
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.add(statusLabel, BorderLayout.WEST);
        add(statusPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }

    private void onGetSuggestions(JButton button) {
        button.setEnabled(false);
        statusLabel.setText("Loading...");

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                controller.execute(currentUser.getName(), currentUser.getLocation());
                return null;
            }

            @Override
            protected void done() {
                button.setEnabled(true);
            }
        }.execute();
    }


    private java.util.List<String> parseOutfits(String suggestions) {
        java.util.List<String> results = new java.util.ArrayList<>();

        String[] blocks = suggestions.split("\\*\\*Outfit [0-9]+:\\*\\*");

        for (int i = 1; i < blocks.length; i++) {
            String b = blocks[i].trim();

            if (i < blocks.length - 1) {
                b = b.split("\\*\\*Outfit")[0].trim();
            }

            if (!b.isBlank()) {
                results.add("Outfit " + i + "\n" + b);
            }
        }

        return results;
    }

    private void openImageGallery() {
        java.util.List<String> outfitBlocks = parseOutfits(suggestionsArea.getText());

        view.OutfitImageGalleryPanel gallery =
                new view.OutfitImageGalleryPanel(currentUser, outfitBlocks);

        gallery.setVisible(true);
    }

    @Override
    public void onOutfitSuggestionSuccess(
            String suggestions, String username, double temperature, String city) {

        SwingUtilities.invokeLater(() -> {
            StringBuilder formatted = new StringBuilder();
            formatted.append("Weather in ").append(city).append(": ")
                    .append(String.format("%.1f", temperature)).append("°C\n\n");

            formatted.append(suggestions);
            suggestionsArea.setText(formatted.toString());
            suggestionsArea.setCaretPosition(0);
            statusLabel.setText("Suggestions loaded successfully!");
        });
    }

    @Override
    public void onOutfitSuggestionFailure(String errorMessage) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this,
                    errorMessage,
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            statusLabel.setText("Failed to load suggestions");
        });
    }
}