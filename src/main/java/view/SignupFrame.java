package view;

import interface_adapter.signup.SignupController;
import interface_adapter.signup.SignupPresenter;
import interface_adapter.signup.SignupView;
import data_access.InMemoryUserRepository;
import use_case.signup.SignupInteractor;

import javax.swing.*;
import java.awt.*;

/**
 * Swing frame that prompts user to sign up
 */
public class SignupFrame extends JFrame implements SignupView {
    private final SignupController controller;
    private final InMemoryUserRepository repository;

    private final JTextField usernameField = new JTextField(20);
    private final JPasswordField passwordField = new JPasswordField(20);
    private final JTextField locationField = new JTextField(20);
    private final JComboBox<String> genderBox = new JComboBox<>(new String[]{"Prefer not to say", "Male", "Female", "Other"});

    public SignupFrame() {
        this.repository = new InMemoryUserRepository();
        SignupPresenter presenter = new SignupPresenter(this);
        SignupInteractor interactor = new SignupInteractor(repository, presenter);
        this.controller = new SignupController(interactor);

        setTitle("Signup");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; form.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; form.add(passwordField, gbc);

        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Location:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; form.add(locationField, gbc);

        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Gender:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; form.add(genderBox, gbc);

        JButton signupButton = new JButton("Sign Up");
        signupButton.addActionListener(e -> onSignup());

        JPanel bottom = new JPanel();
        bottom.add(signupButton);

        add(form, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }

    private void onSignup() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String location = locationField.getText();
        String gender = (String) genderBox.getSelectedItem();

        controller.register(username, password, location, gender);
    }

    @Override
    public void displayMessage(String message) {
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, message));
    }

    @Override
    public void onSignupSuccess(String username) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this,
                "Registration successful! Now let's set up your style preferences.",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);

            // Close signup frame
            dispose();

            // Open style preferences frame - pass the in-memory repository
            // The StyleFrame will handle uploading to Supabase after preferences are saved
            StyleFrame styleFrame = new StyleFrame(username, repository);
            styleFrame.setVisible(true);
        });
    }
}
