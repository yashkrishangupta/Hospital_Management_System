package app;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import util.UITheme;
import util.DBConnection;

public class Login extends JFrame {

    private JTextField     usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleBox;

    public Login() {
        setTitle("Hospital Management System — Login");
        setSize(1024, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(UITheme.BG_PAGE);
        setLayout(null);
        buildUI();
        setVisible(true);
    }

    private void buildUI() {
        // ── Full-width header ─────────────────────────────────────────
        JPanel header = new JPanel(null);
        header.setBounds(0, 0, 1024, 100);
        header.setBackground(UITheme.BG_CARD);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, UITheme.ACCENT));
        add(header);

        header.add(UITheme.createLabel("🏥", new Font("Segoe UI Emoji", Font.PLAIN, 36),
                UITheme.ACCENT, 30, 22, 55, 55));
        header.add(UITheme.createLabel("Hospital Management System",
                new Font("Segoe UI", Font.BOLD, 22), UITheme.TEXT_PRIMARY, 95, 22, 500, 32));
        header.add(UITheme.createLabel("Smart Care. Simple Management.",
                UITheme.FONT_BODY, UITheme.TEXT_MUTED, 95, 58, 400, 22));

        // ── Centered login card ───────────────────────────────────────
        JPanel card = UITheme.createCard(312, 130, 400, 430);
        add(card);

        card.add(UITheme.createLabel("Welcome Back",
                UITheme.FONT_TITLE, UITheme.TEXT_PRIMARY, 20, 25, 360, 36));
        card.add(UITheme.createLabel("Sign in to your account",
                UITheme.FONT_SMALL, UITheme.TEXT_MUTED, 20, 62, 360, 20));
        card.add(UITheme.createSeparator(20, 90, 360));

        // Role
        card.add(UITheme.createLabel("Login as", UITheme.FONT_LABEL,
                UITheme.TEXT_MUTED, 20, 108, 120, 22));
        roleBox = UITheme.createComboBox(20, 132, 360, 38);
        roleBox.addItem("Patient");
        roleBox.addItem("Doctor");
        roleBox.addItem("Admin");
        card.add(roleBox);

        // Username
        card.add(UITheme.createLabel("Username", UITheme.FONT_LABEL,
                UITheme.TEXT_MUTED, 20, 186, 200, 22));
        usernameField = UITheme.createField(20, 210, 360, 38);
        card.add(usernameField);

        // Password
        card.add(UITheme.createLabel("Password", UITheme.FONT_LABEL,
                UITheme.TEXT_MUTED, 20, 262, 200, 22));
        passwordField = UITheme.createPasswordField(20, 286, 360, 38);
        card.add(passwordField);

        // Login button
        JButton loginBtn = UITheme.createAccentButton("LOGIN", 20, 342, 360, 44);
        loginBtn.addActionListener(e -> handleLogin());
        card.add(loginBtn);

        // Register strip
        JLabel regLabel = UITheme.createLabel("New patient?",
                UITheme.FONT_SMALL, UITheme.TEXT_MUTED, 380, 580, 110, 22);
        add(regLabel);

        JButton regBtn = UITheme.createSecondaryButton("Register Here", 492, 575, 150, 32);
        regBtn.addActionListener(e -> new patient.Register());
        add(regBtn);

        JLabel footer = UITheme.createLabel("© 2025 Hospital Management System | All rights reserved",
                UITheme.FONT_SMALL, UITheme.TEXT_MUTED, 0, 620, 1024, 20);
        footer.setHorizontalAlignment(SwingConstants.CENTER);
        add(footer);
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String role     = (String) roleBox.getSelectedItem();

        if (username.isEmpty() || password.isEmpty()) {
            UITheme.showWarning(this, "Please enter both username and password.");
            return;
        }

        switch (role) {
            case "Admin" -> {
                if (username.equals("admin") && password.equals("1234")) {
                    dispose(); new admin.AdminDashboard();
                } else {
                    UITheme.showError(this, "Invalid admin credentials.");
                    passwordField.setText("");
                }
            }
            case "Doctor" -> {
                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement ps = conn.prepareStatement(
                         "SELECT id, name, specialization FROM doctors WHERE username=? AND password=?")) {
                    ps.setString(1, username); ps.setString(2, password);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        dispose();
                        new doctor.DoctorDashboard(rs.getInt("id"), rs.getString("name"), rs.getString("specialization"));
                    } else {
                        UITheme.showError(this, "Invalid doctor credentials.\nUse username like: dr.priya");
                        passwordField.setText("");
                    }
                } catch (SQLException ex) { UITheme.showError(this, "Database error: " + ex.getMessage()); }
            }
            case "Patient" -> {
                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement ps = conn.prepareStatement(
                         "SELECT id, name FROM patients WHERE username=? AND password=?")) {
                    ps.setString(1, username); ps.setString(2, password);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        dispose();
                        new patient.PatientDashboard(rs.getInt("id"), rs.getString("name"));
                    } else {
                        UITheme.showError(this, "Invalid patient credentials.");
                        passwordField.setText("");
                    }
                } catch (SQLException ex) { UITheme.showError(this, "Database error: " + ex.getMessage()); }
            }
        }
    }
}
