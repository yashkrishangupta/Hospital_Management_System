package admin;

import javax.swing.*;
import java.sql.*;
import util.UITheme;
import util.DBConnection;

public class AddDoctor extends JFrame {

    private JTextField nameField, specField, contactField, usernameField;
    private JPasswordField passwordField;
    private AdminDashboard parent;

    public AddDoctor(AdminDashboard parent) {
        this.parent = parent;
        setTitle("Add Doctor");
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
        header.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, UITheme.STAT_BLUE));
        add(header);
        header.add(UITheme.createLabel("➕  Add New Doctor",
                UITheme.FONT_HEADER, UITheme.TEXT_PRIMARY, 26, 14, 500, 26));
        header.add(UITheme.createLabel("Register a doctor and set their login credentials",
                UITheme.FONT_SMALL, UITheme.TEXT_MUTED, 26, 42, 500, 20));

        // Centered card
        JPanel card = UITheme.createCard(212, 100, 600, 480);
        add(card);

        int lx = 30, fw = 540, gap = 72;
        int y = 24;

        card.add(UITheme.createLabel("Doctor Name", UITheme.FONT_LABEL, UITheme.TEXT_MUTED, lx, y, 300, 22));
        nameField = UITheme.createField(lx, y + 26, fw, 40);
        card.add(nameField);
        y += gap;

        card.add(UITheme.createLabel("Specialization", UITheme.FONT_LABEL, UITheme.TEXT_MUTED, lx, y, 300, 22));
        specField = UITheme.createField(lx, y + 26, fw, 40);
        card.add(specField);
        y += gap;

        card.add(UITheme.createLabel("Contact Number", UITheme.FONT_LABEL, UITheme.TEXT_MUTED, lx, y, 300, 22));
        contactField = UITheme.createField(lx, y + 26, fw, 40);
        card.add(contactField);
        y += gap;

        card.add(UITheme.createLabel("Login Username", UITheme.FONT_LABEL, UITheme.TEXT_MUTED, lx, y, 300, 22));
        usernameField = UITheme.createField(lx, y + 26, fw, 40);
        usernameField.setText("dr.");
        card.add(usernameField);
        y += gap;

        card.add(UITheme.createLabel("Login Password", UITheme.FONT_LABEL, UITheme.TEXT_MUTED, lx, y, 300, 22));
        passwordField = UITheme.createPasswordField(lx, y + 26, fw, 40);
        passwordField.setText("1234");
        card.add(passwordField);
        y += gap;

        JButton saveBtn = UITheme.createAccentButton("Save Doctor", lx, y, 200, 44);
        saveBtn.addActionListener(e -> saveDoctor());
        card.add(saveBtn);

        JButton clearBtn = UITheme.createSecondaryButton("Clear", lx + 210, y, 140, 44);
        clearBtn.addActionListener(e -> clearFields());
        card.add(clearBtn);

        JButton closeBtn = UITheme.createDangerButton("Close", lx + 360, y, 180, 44);
        closeBtn.addActionListener(e -> dispose());
        card.add(closeBtn);
    }

    private void saveDoctor() {
        String name = nameField.getText().trim(), spec = specField.getText().trim();
        String contact = contactField.getText().trim(), username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (name.isEmpty() || spec.isEmpty() || username.isEmpty() || password.isEmpty()) {
            UITheme.showWarning(this, "Name, Specialization, Username and Password are required."); return;
        }
        if (password.length() < 4) { UITheme.showWarning(this, "Password must be at least 4 characters."); return; }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO doctors (name, specialization, contact, username, password) VALUES (?,?,?,?,?)")) {
            ps.setString(1, name); ps.setString(2, spec); ps.setString(3, contact);
            ps.setString(4, username); ps.setString(5, password);
            ps.executeUpdate();
            UITheme.showSuccess(this, "Dr. " + name + " added!\nLogin: " + username + " / " + password);
            clearFields();
            if (parent != null) parent.loadStats();
        } catch (SQLIntegrityConstraintViolationException ex) {
            UITheme.showError(this, "Username \"" + username + "\" is already taken.");
        } catch (SQLException ex) {
            UITheme.showError(this, "Failed to add doctor: " + ex.getMessage());
        }
    }

    private void clearFields() {
        nameField.setText(""); specField.setText(""); contactField.setText("");
        usernameField.setText("dr."); passwordField.setText("1234");
        nameField.requestFocus();
    }
}
