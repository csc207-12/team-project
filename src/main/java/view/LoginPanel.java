package view;

import interface_adapter.login.LoginController;
import interface_adapter.login.LoginPresenter;
import interface_adapter.login.LoginView;
import use_case.login.LoginInteractor;

import javax.swing.*;
import java.awt.*;

public class LoginPanel extends JFrame implements LoginView {
    private final LoginController controller;

    private final JTextField usernameField = new JTextField(20);
    private final JPasswordField passwordField = new JPasswordField(20);

    public LoginPanel() {
        LoginPresenter presenter = new LoginPresenter(this);
        LoginInteractor interactor = new LoginInteractor(presenter);
        this.controller = new LoginController(interactor);

        setTitle("Login");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(300, 150);
        setLayout(new BorderLayout());

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        form.add(createFieldPanel("Username:", usernameField));
        form.add(Box.createVerticalStrut(20));
        form.add(createFieldPanel("Password:", passwordField));
        form.add(Box.createVerticalStrut(10));

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> onLogin());

        JButton signupButton = new JButton("Sign Up");
        signupButton.addActionListener(e -> onSignup());

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottom.add(loginButton);
        bottom.add(Box.createHorizontalStrut(10));
        bottom.add(signupButton);

        add(form, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);

    }

    public void onLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        controller.login(username, password);
    }

    public void onSignup() {
        dispose();
        SignupPanel signupPanel = new SignupPanel();
        signupPanel.setVisible(true);
    }

    private JPanel createFieldPanel(String labelText, JComponent field) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel label = new JLabel(labelText);
        label.setPreferredSize(new Dimension(80, 25));
        panel.add(label);
        panel.add(field);
        return panel;
    }

    public void onLoginSuccess(String username) {
        JOptionPane.showMessageDialog(this, "Login successful! Welcome, " + username + "!");


    }

    public void onLoginFailure(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

}
