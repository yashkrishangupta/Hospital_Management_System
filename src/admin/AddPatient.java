package admin;

import javax.swing.*;
import java.sql.*;
import util.UITheme;
import util.DBConnection;

public class AddPatient extends JFrame {

    private JTextField nameField, ageField, contactField, usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> genderBox;
    private AdminDashboard parent;

    public AddPatient(AdminDashboard parent) {
        this.parent = parent;
        setTitle("Add Patient");
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
        JPanel header = UITheme.createCard(0, 0, 1024, 72);
        header.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, UITheme.STAT_GREEN));
        add(header);
        header.add(UITheme.createLabel("👤  Add New Patient",
                UITheme.FONT_HEADER, UITheme.TEXT_PRIMARY, 26, 14, 500, 26));
        header.add(UITheme.createLabel("Register a patient with login credentials",
                UITheme.FONT_SMALL, UITheme.TEXT_MUTED, 26, 42, 500, 20));

        JPanel card = UITheme.createCard(212, 100, 600, 500);
        add(card);

        int lx = 30, fw = 540, gap = 72;
        int y = 20;

        card.add(UITheme.createLabel("Full Name", UITheme.FONT_LABEL, UITheme.TEXT_MUTED, lx, y, 300, 22));
        nameField = UITheme.createField(lx, y + 26, fw, 40);
        card.add(nameField);
        y += gap;

        card.add(UITheme.createLabel("Age", UITheme.FONT_LABEL, UITheme.TEXT_MUTED, lx, y, 100, 22));
        ageField = UITheme.createField(lx, y + 26, 240, 40);
        card.add(ageField);

        card.add(UITheme.createLabel("Gender", UITheme.FONT_LABEL, UITheme.TEXT_MUTED, lx + 270, y, 100, 22));
        genderBox = UITheme.createComboBox(lx + 270, y + 26, 270, 40);
        genderBox.addItem("Male"); genderBox.addItem("Female"); genderBox.addItem("Other");
        card.add(genderBox);
        y += gap;

        card.add(UITheme.createLabel("Contact Number", UITheme.FONT_LABEL, UITheme.TEXT_MUTED, lx, y, 300, 22));
        contactField = UITheme.createField(lx, y + 26, fw, 40);
        card.add(contactField);
        y += gap;

        card.add(UITheme.createLabel("Username", UITheme.FONT_LABEL, UITheme.TEXT_MUTED, lx, y, 300, 22));
        usernameField = UITheme.createField(lx, y + 26, fw, 40);
        card.add(usernameField);
        y += gap;

        card.add(UITheme.createLabel("Password", UITheme.FONT_LABEL, UITheme.TEXT_MUTED, lx, y, 300, 22));
        passwordField = UITheme.createPasswordField(lx, y + 26, fw, 40);
        card.add(passwordField);
        y += gap;

        JButton saveBtn = UITheme.createAccentButton("Save Patient", lx, y, 200, 44);
        saveBtn.addActionListener(e -> savePatient());
        card.add(saveBtn);

        JButton clearBtn = UITheme.createSecondaryButton("Clear", lx + 210, y, 140, 44);
        clearBtn.addActionListener(e -> clearFields());
        card.add(clearBtn);

        JButton closeBtn = UITheme.createDangerButton("Close", lx + 360, y, 180, 44);
        closeBtn.addActionListener(e -> dispose());
        card.add(closeBtn);
    }

    private void savePatient() {
        String name = nameField.getText().trim(), ageStr = ageField.getText().trim();
        String gender = (String) genderBox.getSelectedItem();
        String contact = contactField.getText().trim(), username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (name.isEmpty() || ageStr.isEmpty() || contact.isEmpty() || username.isEmpty() || password.isEmpty()) {
            UITheme.showWarning(this, "Please fill in all fields."); return;
        }
        int age;
        try { age = Integer.parseInt(ageStr); }
        catch (NumberFormatException e) { UITheme.showWarning(this, "Age must be a valid number."); return; }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO patients (name, age, gender, contact, username, password) VALUES (?,?,?,?,?,?)")) {
            ps.setString(1, name); ps.setInt(2, age); ps.setString(3, gender);
            ps.setString(4, contact); ps.setString(5, username); ps.setString(6, password);
            ps.executeUpdate();
            UITheme.showSuccess(this, "Patient \"" + name + "\" registered successfully!");
            clearFields();
            if (parent != null) parent.loadStats();
        } catch (SQLIntegrityConstraintViolationException ex) {
            UITheme.showError(this, "Username \"" + username + "\" is already taken.");
        } catch (SQLException ex) {
            UITheme.showError(this, "Failed to add patient: " + ex.getMessage());
        }
    }

    private void clearFields() {
        nameField.setText(""); ageField.setText(""); contactField.setText("");
        usernameField.setText(""); passwordField.setText("");
        genderBox.setSelectedIndex(0); nameField.requestFocus();
    }
}
