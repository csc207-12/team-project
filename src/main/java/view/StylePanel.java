package view;

import interface_adapter.style.StyleController;
import interface_adapter.style.StylePresenter;
import interface_adapter.style.StyleView;
import use_case.style.StyleInteractor;
import use_case.UserRepository;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * A scrollable frame for selecting clothing style preferences.
 */
public class StylePanel extends JFrame implements StyleView {
    private final StyleController controller;
    private final String username;

    // Checkboxes for each clothing item
    private final Map<String, JCheckBox> checkboxes = new HashMap<>();

    public StylePanel(String username, UserRepository repository) {
        this.username = username;

        StylePresenter presenter = new StylePresenter(this);
        StyleInteractor interactor = new StyleInteractor(repository, presenter);
        this.controller = new StyleController(interactor);

        setTitle("Select Your Style Preferences");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create main panel with sections
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel headerLabel = new JLabel("Choose the clothing items you like:");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(headerLabel);
        mainPanel.add(Box.createVerticalStrut(15));

        // Add each category
        addCategory(mainPanel, "Bottoms", new String[]{
            "jeans", "sweatpants", "shorts", "dress pants/chinos", "leggings", "skirts"
        });

        addCategory(mainPanel, "Tops", new String[]{
            "T-shirts", "long sleeve shirts", "tank tops", "polo shirts", "blouses"
        });

        addCategory(mainPanel, "Outerwear", new String[]{
            "hoodie", "sweatshirt/crewneck", "light jacket/windbreaker",
            "denim jacket", "winter coat/puffer", "raincoat", "blazer"
        });

        addCategory(mainPanel, "Footwear", new String[]{
            "sneakers", "casual shoes", "boots", "sandals/slippers", "dress shoes", "crocs"
        });

        addCategory(mainPanel, "Accessories", new String[]{
            "hats/caps/beanies", "scarf", "gloves", "belt", "sunglasses", "watch"
        });

        // Wrap main panel in scroll pane
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Add save button at bottom
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Save Preferences");
        saveButton.setFont(new Font("Arial", Font.BOLD, 14));
        saveButton.addActionListener(e -> onSave());
        buttonPanel.add(saveButton);

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setSize(500, 600);
        setLocationRelativeTo(null);
    }

    private void addCategory(JPanel parent, String categoryName, String[] items) {
        // Category label
        JLabel categoryLabel = new JLabel(categoryName + ":");
        categoryLabel.setFont(new Font("Arial", Font.BOLD, 14));
        categoryLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        parent.add(categoryLabel);
        parent.add(Box.createVerticalStrut(5));

        // Panel for checkboxes in this category
        JPanel categoryPanel = new JPanel();
        categoryPanel.setLayout(new BoxLayout(categoryPanel, BoxLayout.Y_AXIS));
        categoryPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        categoryPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));

        for (String item : items) {
            JCheckBox checkbox = new JCheckBox(item);
            checkbox.setAlignmentX(Component.LEFT_ALIGNMENT);
            checkboxes.put(item, checkbox);
            categoryPanel.add(checkbox);
        }

        parent.add(categoryPanel);
        parent.add(Box.createVerticalStrut(15));
    }

    private void onSave() {
        // Collect selected preferences
        Map<String, Boolean> stylePreferences = new HashMap<>();
        for (Map.Entry<String, JCheckBox> entry : checkboxes.entrySet()) {
            stylePreferences.put(entry.getKey(), entry.getValue().isSelected());
        }

        // Save via controller
        controller.saveStylePreferences(username, stylePreferences);
    }

    @Override
    public void onStyleSaveSuccess(String message) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose(); // Close the style frame
            LoginPanel loginPanel = new LoginPanel();
            loginPanel.setVisible(true);
        });
    }

    @Override
    public void onStyleSaveFailure(String message) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
        });
    }
}
