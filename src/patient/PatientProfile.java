package patient;

import javax.swing.*;
import java.sql.*;
import util.UITheme;
import util.DBConnection;

public class PatientProfile extends JFrame {

    private final int patientId;
    private JTextField nameField, ageField, genderField, contactField, usernameField;
    private JPasswordField oldPassField, newPassField, confirmPassField;

    public PatientProfile(int patientId) {
        this.patientId = patientId;
        setTitle("My Profile");
        setSize(1024, 700);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(UITheme.BG_PAGE);
        setLayout(null);
        buildUI();
        loadProfile();
        setVisible(true);
    }

    private void buildUI() {
        // Header
        JPanel header = UITheme.createCard(0, 0, 1024, 72);
        header.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, UITheme.STAT_GREEN));
        add(header);
        header.add(UITheme.createLabel("My Profile",
                UITheme.FONT_HEADER, UITheme.TEXT_PRIMARY, 26, 14, 400, 26));
        header.add(UITheme.createLabel("View your details and change your password",
                UITheme.FONT_SMALL, UITheme.TEXT_MUTED, 26, 42, 500, 20));

        // ── Profile info card — left half ─────────────────────────────
        JPanel infoCard = UITheme.createCard(20, 92, 490, 310);
        add(infoCard);
        infoCard.add(UITheme.createLabel("Profile Information",
                UITheme.FONT_LABEL, UITheme.ACCENT, 18, 12, 300, 22));
        infoCard.add(UITheme.createSeparator(18, 36, 455));

        int lx = 18, fw = 455, y = 50, gap = 62;

        infoCard.add(UITheme.createLabel("Full Name", UITheme.FONT_LABEL, UITheme.TEXT_MUTED, lx, y, 200, 22));
        nameField = UITheme.createField(lx, y + 24, fw, 36);
        nameField.setEditable(false); nameField.setBackground(UITheme.BG_PAGE);
        infoCard.add(nameField);
        y += gap;

        // Age + Gender on same row
        infoCard.add(UITheme.createLabel("Age", UITheme.FONT_LABEL, UITheme.TEXT_MUTED, lx, y, 100, 22));
        ageField = UITheme.createField(lx, y + 24, 200, 36);
        ageField.setEditable(false); ageField.setBackground(UITheme.BG_PAGE);
        infoCard.add(ageField);

        infoCard.add(UITheme.createLabel("Gender", UITheme.FONT_LABEL, UITheme.TEXT_MUTED, lx + 230, y, 100, 22));
        genderField = UITheme.createField(lx + 230, y + 24, 225, 36);
        genderField.setEditable(false); genderField.setBackground(UITheme.BG_PAGE);
        infoCard.add(genderField);
        y += gap;

        infoCard.add(UITheme.createLabel("Contact", UITheme.FONT_LABEL, UITheme.TEXT_MUTED, lx, y, 200, 22));
        contactField = UITheme.createField(lx, y + 24, fw, 36);
        contactField.setEditable(false); contactField.setBackground(UITheme.BG_PAGE);
        infoCard.add(contactField);
        y += gap;

        infoCard.add(UITheme.createLabel("Username", UITheme.FONT_LABEL, UITheme.TEXT_MUTED, lx, y, 200, 22));
        usernameField = UITheme.createField(lx, y + 24, fw, 36);
        usernameField.setEditable(false); usernameField.setBackground(UITheme.BG_PAGE);
        infoCard.add(usernameField);

        // ── Password change card — right half ─────────────────────────
        JPanel passCard = UITheme.createCard(530, 92, 474, 310);
        add(passCard);
        passCard.add(UITheme.createLabel("Change Password",
                UITheme.FONT_LABEL, UITheme.ACCENT, 18, 12, 300, 22));
        passCard.add(UITheme.createSeparator(18, 36, 440));

        int pw = 440; y = 50;

        passCard.add(UITheme.createLabel("Current Password", UITheme.FONT_LABEL, UITheme.TEXT_MUTED, 18, y, 200, 22));
        oldPassField = UITheme.createPasswordField(18, y + 24, pw, 36);
        passCard.add(oldPassField);
        y += gap;

        passCard.add(UITheme.createLabel("New Password", UITheme.FONT_LABEL, UITheme.TEXT_MUTED, 18, y, 200, 22));
        newPassField = UITheme.createPasswordField(18, y + 24, pw, 36);
        passCard.add(newPassField);
        y += gap;

        passCard.add(UITheme.createLabel("Confirm New Password", UITheme.FONT_LABEL, UITheme.TEXT_MUTED, 18, y, 220, 22));
        confirmPassField = UITheme.createPasswordField(18, y + 24, pw, 36);
        passCard.add(confirmPassField);
        y += gap;

        passCard.add(UITheme.createLabel("Password must be at least 4 characters.",
                UITheme.FONT_SMALL, UITheme.TEXT_MUTED, 18, y, 440, 20));

        // ── Buttons ───────────────────────────────────────────────────
        JButton changeBtn = UITheme.createAccentButton("Update Password", 530, 418, 240, 44);
        changeBtn.addActionListener(e -> changePassword());
        add(changeBtn);

        JButton closeBtn = UITheme.createSecondaryButton("Close", 784, 418, 220, 44);
        closeBtn.addActionListener(e -> dispose());
        add(closeBtn);

        // ── Info note ─────────────────────────────────────────────────
        JPanel noteCard = UITheme.createCard(20, 478, 984, 60);
        add(noteCard);
        noteCard.add(UITheme.createLabel("Note: To update your name, age, gender or contact — please contact Admin.",
                UITheme.FONT_LABEL, UITheme.TEXT_MUTED, 20, 10, 940, 22));
        noteCard.add(UITheme.createLabel("Only your password can be changed here.",
                UITheme.FONT_SMALL, UITheme.TEXT_MUTED, 20, 34, 940, 20));

        // Footer
        JLabel footer = UITheme.createLabel("© 2025 Hospital Management System",
                UITheme.FONT_SMALL, UITheme.TEXT_MUTED, 0, 664, 1024, 22);
        footer.setHorizontalAlignment(SwingConstants.CENTER);
        add(footer);
    }

    private void loadProfile() {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                "SELECT name, age, gender, contact, username FROM patients WHERE id=?")) {
            ps.setInt(1, patientId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                nameField.setText(rs.getString("name"));
                ageField.setText(String.valueOf(rs.getInt("age")));
                genderField.setText(rs.getString("gender"));
                contactField.setText(rs.getString("contact") != null ? rs.getString("contact") : "");
                usernameField.setText(rs.getString("username") != null ? rs.getString("username") : "");
            }
        } catch (SQLException ex) {
            UITheme.showError(this, "Error loading profile: " + ex.getMessage());
        }
    }

    private void changePassword() {
        String oldP = new String(oldPassField.getPassword()).trim();
        String newP = new String(newPassField.getPassword()).trim();
        String conP = new String(confirmPassField.getPassword()).trim();

        if (oldP.isEmpty() || newP.isEmpty() || conP.isEmpty()) {
            UITheme.showWarning(this, "Fill in all three password fields."); return;
        }
        if (!newP.equals(conP)) {
            UITheme.showWarning(this, "New passwords do not match."); return;
        }
        if (newP.length() < 4) {
            UITheme.showWarning(this, "Password must be at least 4 characters."); return;
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement verify = conn.prepareStatement(
                "SELECT id FROM patients WHERE id=? AND password=?")) {
            verify.setInt(1, patientId);
            verify.setString(2, oldP);
            if (!verify.executeQuery().next()) {
                UITheme.showError(this, "Current password is incorrect."); return;
            }
            PreparedStatement upd = conn.prepareStatement(
                "UPDATE patients SET password=? WHERE id=?");
            upd.setString(1, newP);
            upd.setInt(2, patientId);
            upd.executeUpdate();

            UITheme.showSuccess(this, "Password changed successfully!");
            oldPassField.setText(""); newPassField.setText(""); confirmPassField.setText("");

        } catch (SQLException ex) {
            UITheme.showError(this, "Error: " + ex.getMessage());
        }
    }
}
