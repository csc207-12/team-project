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
public class SignupPanel extends JFrame implements SignupView {
    private final SignupController controller;
    private final InMemoryUserRepository repository;

    private final JTextField usernameField = new JTextField(20);
    private final JPasswordField passwordField = new JPasswordField(20);
    private final JTextField locationField = new JTextField(20);
    private final JComboBox<String> genderBox = new JComboBox<>(new String[]{"Prefer not to say", "Male", "Female", "Other"});

    public SignupPanel() {
        this.repository = new InMemoryUserRepository();
        SignupPresenter presenter = new SignupPresenter(this);
        SignupInteractor interactor = new SignupInteractor(repository, presenter);
        this.controller = new SignupController(interactor);

        setTitle("Signup");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        form.add(createFieldPanel("Username:", usernameField));
        form.add(Box.createVerticalStrut(10));
        form.add(createFieldPanel("Password:", passwordField));
        form.add(Box.createVerticalStrut(10));
        form.add(createFieldPanel("Location:", locationField));
        form.add(Box.createVerticalStrut(10));
        form.add(createFieldPanel("Gender:", genderBox));

        JButton signupButton = new JButton("Sign Up");
        signupButton.addActionListener(e -> onSignup());

        JPanel bottom = new JPanel();
        bottom.add(signupButton);

        add(form, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }

    private JPanel createFieldPanel(String labelText, JComponent field) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel label = new JLabel(labelText);
        label.setPreferredSize(new Dimension(80, 25));
        panel.add(label);
        panel.add(field);
        return panel;
    }

    private void onSignup() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String location = locationField.getText();
        String gender = (String) genderBox.getSelectedItem();

        controller.register(username, password, location, gender);
    }

    @Override
    public void onSignupFailure(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    @Override
    public void onSignupSuccess(String username) {

            JOptionPane.showMessageDialog(this,
                    "Registration successful! Now let's set up your style preferences.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            dispose();

            StylePanel stylePanel = new StylePanel(username, repository);
            stylePanel.setVisible(true);

    }
}
