package view;

import data_access.purpose.PurposeAccessoryDataAccessObject;
import interface_adapter.purpose.PurposeController;
import interface_adapter.purpose.PurposePresenter;
import interface_adapter.purpose.PurposeView;
import use_case.purpose.PurposeInteractor;
import use_case.purpose.PurposeAccessoryDataAccessInterface;

import javax.swing.*;
import java.awt.*;

public class PurposePanel extends JFrame implements PurposeView {

    private final PurposeController controller;

    private final JTextField purposeField = new JTextField(30);
    private final JButton getAccessoriesButton = new JButton("Get Accessory Suggestions");
    private final JTextArea suggestionsArea = new JTextArea(15, 40);
    private final JLabel statusLabel = new JLabel(" ");

    public PurposePanel() {

        // Clean architecture wiring
        PurposeAccessoryDataAccessInterface dataAccess = new PurposeAccessoryDataAccessObject();
        PurposePresenter presenter = new PurposePresenter(this);
        PurposeInteractor interactor = new PurposeInteractor(dataAccess, presenter);
        this.controller = new PurposeController(interactor);

        setTitle("Accessory Suggestions by Purpose");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Header
        JLabel headerLabel = new JLabel("Get Accessory Suggestions Based on Purpose");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(headerLabel);
        mainPanel.add(Box.createVerticalStrut(10));

        // Purpose input row
        JPanel purposePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        purposePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel purposeLabel = new JLabel("Purpose:");
        purposeLabel.setPreferredSize(new Dimension(80, 25));
        purposePanel.add(purposeLabel);
        purposePanel.add(purposeField);
        mainPanel.add(purposePanel);
        mainPanel.add(Box.createVerticalStrut(10));

        // Button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        getAccessoriesButton.setFont(new Font("Arial", Font.BOLD, 14));
        getAccessoriesButton.addActionListener(e -> onGetAccessories());
        buttonPanel.add(getAccessoriesButton);
        mainPanel.add(buttonPanel);
        mainPanel.add(Box.createVerticalStrut(15));

        // Suggestions area
        JLabel suggestionsLabel = new JLabel("Accessory Suggestions:");
        suggestionsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        suggestionsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(suggestionsLabel);
        mainPanel.add(Box.createVerticalStrut(5));

        suggestionsArea.setEditable(false);
        suggestionsArea.setLineWrap(true);
        suggestionsArea.setWrapStyleWord(true);
        suggestionsArea.setFont(new Font("Arial", Font.PLAIN, 13));
        JScrollPane scrollPane = new JScrollPane(suggestionsArea);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollPane.setPreferredSize(new Dimension(350, 450));
        mainPanel.add(scrollPane);

        add(mainPanel, BorderLayout.CENTER);

        // Status bar
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        bottomPanel.add(statusLabel, BorderLayout.WEST);
        add(bottomPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }

    private void onGetAccessories() {
        String purpose = purposeField.getText().trim();
        if (purpose.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter your purpose (e.g., business meeting, gym, coffee date).",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        getAccessoriesButton.setEnabled(false);
        statusLabel.setText("Generating accessory suggestions...");
        suggestionsArea.setText("");

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                controller.execute(purpose);
                return null;
            }

            @Override
            protected void done() {
                getAccessoriesButton.setEnabled(true);
            }
        }.execute();
    }

    @Override
    public void onPurposeAccessorySuccess(String suggestionsText) {
        SwingUtilities.invokeLater(() -> {
            suggestionsArea.setText(suggestionsText);
            suggestionsArea.setCaretPosition(0);
            statusLabel.setText("Accessory suggestions loaded successfully!");
        });
    }

    @Override
    public void onPurposeAccessoryFailure(String errorMessage) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(
                    this,
                    errorMessage,
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            statusLabel.setText("Failed to load accessory suggestions");
        });
    }
}

