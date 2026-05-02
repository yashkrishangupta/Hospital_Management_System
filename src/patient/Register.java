package patient;

import javax.swing.*;
import java.sql.*;
import util.UITheme;
import util.DBConnection;

public class Register extends JFrame {

    private JTextField nameField, ageField, contactField, usernameField;
    private JPasswordField passwordField, confirmPassField;
    private JComboBox<String> genderBox;

    public Register() {
        setTitle("Patient Registration");
        setSize(1024, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(UITheme.BG_DARK);
        setLayout(null);
        buildUI();
        setVisible(true);
    }

    private void buildUI() {
        // Full-width header
        JPanel header = UITheme.createCard(0, 0, 1024, 72);
        header.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, UITheme.STAT_GREEN));
        add(header);
        header.add(UITheme.createLabel("🏥  Patient Registration",
                UITheme.FONT_TITLE, UITheme.TEXT_PRIMARY, 26, 14, 500, 32));
        header.add(UITheme.createLabel("Create your account to book appointments",
                UITheme.FONT_SMALL, UITheme.TEXT_MUTED, 26, 48, 500, 20));

        // Centered form card
        JPanel card = UITheme.createCard(212, 96, 600, 530);
        add(card);

        int lx = 30, fw = 540, gap = 68;
        int y = 20;

        card.add(UITheme.createLabel("Full Name", UITheme.FONT_LABEL, UITheme.TEXT_MUTED, lx, y, 300, 22));
        nameField = UITheme.createField(lx, y + 26, fw, 38);
        card.add(nameField);
        y += gap;

        card.add(UITheme.createLabel("Age", UITheme.FONT_LABEL, UITheme.TEXT_MUTED, lx, y, 100, 22));
        ageField = UITheme.createField(lx, y + 26, 240, 38);
        card.add(ageField);

        card.add(UITheme.createLabel("Gender", UITheme.FONT_LABEL, UITheme.TEXT_MUTED, lx + 270, y, 100, 22));
        genderBox = UITheme.createComboBox(lx + 270, y + 26, 270, 38);
        genderBox.addItem("Male"); genderBox.addItem("Female"); genderBox.addItem("Other");
        card.add(genderBox);
        y += gap;

        card.add(UITheme.createLabel("Contact Number", UITheme.FONT_LABEL, UITheme.TEXT_MUTED, lx, y, 300, 22));
        contactField = UITheme.createField(lx, y + 26, fw, 38);
        card.add(contactField);
        y += gap;

        card.add(UITheme.createLabel("Choose Username", UITheme.FONT_LABEL, UITheme.TEXT_MUTED, lx, y, 300, 22));
        usernameField = UITheme.createField(lx, y + 26, fw, 38);
        card.add(usernameField);
        y += gap;

        card.add(UITheme.createLabel("Password", UITheme.FONT_LABEL, UITheme.TEXT_MUTED, lx, y, 300, 22));
        passwordField = UITheme.createPasswordField(lx, y + 26, fw, 38);
        card.add(passwordField);
        y += gap;

        card.add(UITheme.createLabel("Confirm Password", UITheme.FONT_LABEL, UITheme.TEXT_MUTED, lx, y, 300, 22));
        confirmPassField = UITheme.createPasswordField(lx, y + 26, fw, 38);
        card.add(confirmPassField);
        y += gap;

        JButton regBtn = UITheme.createAccentButton("Create Account", lx, y + 26, 260, 44);
        regBtn.addActionListener(e -> doRegister());
        card.add(regBtn);

        JButton closeBtn = UITheme.createSecondaryButton("Back to Login", lx + 270, y + 26, 270, 44);
        closeBtn.addActionListener(e -> dispose());
        card.add(closeBtn);
    }

    private void doRegister() {
        String name = nameField.getText().trim(), ageStr = ageField.getText().trim();
        String gender = (String) genderBox.getSelectedItem();
        String contact = contactField.getText().trim(), username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirm  = new String(confirmPassField.getPassword());

        if (name.isEmpty() || ageStr.isEmpty() || contact.isEmpty() || username.isEmpty() || password.isEmpty()) {
            UITheme.showWarning(this, "All fields are required."); return;
        }
        int age;
        try { age = Integer.parseInt(ageStr); }
        catch (NumberFormatException e) { UITheme.showWarning(this, "Age must be a valid number."); return; }

        if (!password.equals(confirm)) { UITheme.showWarning(this, "Passwords do not match."); return; }
        if (password.length() < 4)    { UITheme.showWarning(this, "Password must be at least 4 characters."); return; }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO patients (name, age, gender, contact, username, password) VALUES (?,?,?,?,?,?)")) {
            ps.setString(1, name); ps.setInt(2, age); ps.setString(3, gender);
            ps.setString(4, contact); ps.setString(5, username); ps.setString(6, password);
            ps.executeUpdate();
            UITheme.showSuccess(this, "Account created! You can now log in as: " + username);
            dispose();
        } catch (SQLIntegrityConstraintViolationException ex) {
            UITheme.showError(this, "Username \"" + username + "\" is already taken. Try another.");
        } catch (SQLException ex) {
            UITheme.showError(this, "Registration failed: " + ex.getMessage());
        }
    }
}
