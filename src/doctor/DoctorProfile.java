package doctor;

import javax.swing.*;
import java.sql.*;
import util.UITheme;
import util.DBConnection;

public class DoctorProfile extends JFrame {

    private final int doctorId;
    private final DoctorDashboard dashboard;
    private JTextField nameField, specField, contactField, usernameField;
    private JPasswordField oldPassField, newPassField, confirmPassField;

    public DoctorProfile(int doctorId, String name, String spec, DoctorDashboard dashboard) {
        this.doctorId = doctorId; this.dashboard = dashboard;
        setTitle("My Profile");
        setSize(1024, 700);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(UITheme.BG_PAGE);
        setLayout(null);
        buildUI(); loadProfile(); setVisible(true);
    }

    private void buildUI() {
        JPanel header = UITheme.createCard(0, 0, 1024, 72);
        header.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, UITheme.TEXT_MUTED));
        add(header);
        header.add(UITheme.createLabel("👤  My Profile", UITheme.FONT_HEADER, UITheme.TEXT_PRIMARY, 26, 14, 400, 26));
        header.add(UITheme.createLabel("View your details and change your password", UITheme.FONT_SMALL, UITheme.TEXT_MUTED, 26, 42, 500, 20));

        // Profile info card — left half
        JPanel infoCard = UITheme.createCard(20, 92, 490, 260);
        add(infoCard);
        infoCard.add(UITheme.createLabel("Profile Information", UITheme.FONT_LABEL, UITheme.ACCENT, 18, 12, 300, 22));
        infoCard.add(UITheme.createSeparator(18, 36, 455));

        int lx = 18, fw = 455, y = 50, gap = 64;
        infoCard.add(UITheme.createLabel("Full Name", UITheme.FONT_LABEL, UITheme.TEXT_MUTED, lx, y, 200, 22));
        nameField = UITheme.createField(lx, y + 24, fw, 36); nameField.setEditable(false); nameField.setBackground(UITheme.BG_PAGE); infoCard.add(nameField); y += gap;
        infoCard.add(UITheme.createLabel("Specialization", UITheme.FONT_LABEL, UITheme.TEXT_MUTED, lx, y, 200, 22));
        specField = UITheme.createField(lx, y + 24, fw, 36); specField.setEditable(false); specField.setBackground(UITheme.BG_PAGE); infoCard.add(specField); y += gap;
        infoCard.add(UITheme.createLabel("Contact", UITheme.FONT_LABEL, UITheme.TEXT_MUTED, lx, y, 110, 22));
        contactField = UITheme.createField(lx, y + 24, 215, 36); contactField.setEditable(false); contactField.setBackground(UITheme.BG_PAGE); infoCard.add(contactField);
        infoCard.add(UITheme.createLabel("Username", UITheme.FONT_LABEL, UITheme.TEXT_MUTED, lx + 240, y, 110, 22));
        usernameField = UITheme.createField(lx + 240, y + 24, 215, 36); usernameField.setEditable(false); usernameField.setBackground(UITheme.BG_PAGE); infoCard.add(usernameField);

        // Password change card — right half
        JPanel passCard = UITheme.createCard(530, 92, 474, 260);
        add(passCard);
        passCard.add(UITheme.createLabel("Change Password", UITheme.FONT_LABEL, UITheme.ACCENT, 18, 12, 300, 22));
        passCard.add(UITheme.createSeparator(18, 36, 440));

        int pw = 440; y = 50;
        passCard.add(UITheme.createLabel("Current Password", UITheme.FONT_LABEL, UITheme.TEXT_MUTED, 18, y, 200, 22));
        oldPassField = UITheme.createPasswordField(18, y + 24, pw, 36); passCard.add(oldPassField); y += gap;
        passCard.add(UITheme.createLabel("New Password", UITheme.FONT_LABEL, UITheme.TEXT_MUTED, 18, y, 200, 22));
        newPassField = UITheme.createPasswordField(18, y + 24, pw, 36); passCard.add(newPassField); y += gap;
        passCard.add(UITheme.createLabel("Confirm New Password", UITheme.FONT_LABEL, UITheme.TEXT_MUTED, 18, y, 220, 22));
        confirmPassField = UITheme.createPasswordField(18, y + 24, pw, 36); passCard.add(confirmPassField);

        JButton changeBtn = UITheme.createAccentButton("Update Password", 530, 368, 240, 44);
        changeBtn.addActionListener(e -> changePassword()); add(changeBtn);

        JButton closeBtn = UITheme.createSecondaryButton("Close", 784, 368, 220, 44);
        closeBtn.addActionListener(e -> dispose()); add(closeBtn);
    }

    private void loadProfile() {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT name, specialization, contact, username FROM doctors WHERE id=?")) {
            ps.setInt(1, doctorId); ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                nameField.setText("Dr. " + rs.getString("name")); specField.setText(rs.getString("specialization"));
                contactField.setText(rs.getString("contact") != null ? rs.getString("contact") : "");
                usernameField.setText(rs.getString("username") != null ? rs.getString("username") : "");
            }
        } catch (SQLException ex) { UITheme.showError(this, "Error loading profile: " + ex.getMessage()); }
    }

    private void changePassword() {
        String oldP = new String(oldPassField.getPassword()).trim();
        String newP = new String(newPassField.getPassword()).trim();
        String conP = new String(confirmPassField.getPassword()).trim();
        if (oldP.isEmpty() || newP.isEmpty() || conP.isEmpty()) { UITheme.showWarning(this, "Fill in all three fields."); return; }
        if (!newP.equals(conP)) { UITheme.showWarning(this, "New passwords do not match."); return; }
        if (newP.length() < 4) { UITheme.showWarning(this, "Password must be at least 4 characters."); return; }
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement v = conn.prepareStatement("SELECT id FROM doctors WHERE id=? AND password=?")) {
            v.setInt(1, doctorId); v.setString(2, oldP);
            if (!v.executeQuery().next()) { UITheme.showError(this, "Current password is incorrect."); return; }
            PreparedStatement u = conn.prepareStatement("UPDATE doctors SET password=? WHERE id=?");
            u.setString(1, newP); u.setInt(2, doctorId); u.executeUpdate();
            UITheme.showSuccess(this, "Password changed successfully!");
            oldPassField.setText(""); newPassField.setText(""); confirmPassField.setText("");
        } catch (SQLException ex) { UITheme.showError(this, "Error: " + ex.getMessage()); }
    }
}
