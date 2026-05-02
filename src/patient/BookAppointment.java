package patient;

import javax.swing.*;
import java.sql.*;
import util.UITheme;
import util.DBConnection;

public class BookAppointment extends JFrame {

    private JComboBox<String> doctorBox;
    private JTextField dateField;
    private final int patientId;
    private java.util.List<Integer> doctorIds = new java.util.ArrayList<>();
    private PatientDashboard parent;

    public BookAppointment(int patientId, PatientDashboard parent) {
        this.patientId = patientId; this.parent = parent;
        setTitle("Book Appointment");
        setSize(1024, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(UITheme.BG_DARK);
        setLayout(null);
        buildUI(); loadDoctors(); setVisible(true);
    }

    private void buildUI() {
        JPanel header = UITheme.createCard(0, 0, 1024, 72);
        header.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, UITheme.ACCENT));
        add(header);
        header.add(UITheme.createLabel("📅  Book an Appointment",
                UITheme.FONT_HEADER, UITheme.TEXT_PRIMARY, 26, 14, 500, 26));
        header.add(UITheme.createLabel("Select a doctor and your preferred date",
                UITheme.FONT_SMALL, UITheme.TEXT_MUTED, 26, 42, 500, 20));

        // Centered form card
        JPanel card = UITheme.createCard(212, 100, 600, 360);
        add(card);

        int lx = 30, fw = 540;

        card.add(UITheme.createLabel("Select Doctor", UITheme.FONT_LABEL,
                UITheme.TEXT_MUTED, lx, 24, 300, 22));
        doctorBox = UITheme.createComboBox(lx, 50, fw, 44);
        card.add(doctorBox);

        card.add(UITheme.createLabel("Appointment Date  (YYYY-MM-DD)", UITheme.FONT_LABEL,
                UITheme.TEXT_MUTED, lx, 114, 400, 22));
        dateField = UITheme.createField(lx, 140, fw, 44);
        dateField.setText(java.time.LocalDate.now().toString());
        card.add(dateField);

        card.add(UITheme.createLabel("💡  Format: 2025-08-15   (year-month-day)",
                UITheme.FONT_SMALL, UITheme.TEXT_MUTED, lx, 194, 400, 20));

        JButton bookBtn = UITheme.createAccentButton("Confirm Booking", lx, 228, 260, 48);
        bookBtn.addActionListener(e -> confirmBooking());
        card.add(bookBtn);

        JButton cancelBtn = UITheme.createSecondaryButton("Cancel", lx + 270, 228, 270, 48);
        cancelBtn.addActionListener(e -> dispose());
        card.add(cancelBtn);

        // Info note
        JPanel noteCard = UITheme.createCard(212, 478, 600, 80);
        add(noteCard);
        noteCard.add(UITheme.createLabel("ℹ️  Your appointment will be set to Pending status.",
                UITheme.FONT_LABEL, UITheme.TEXT_MUTED, 20, 14, 560, 22));
        noteCard.add(UITheme.createLabel("The doctor will confirm or cancel it from their dashboard.",
                UITheme.FONT_SMALL, UITheme.TEXT_MUTED, 20, 40, 560, 20));
    }

    private void loadDoctors() {
        doctorIds.clear(); doctorBox.removeAllItems();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                "SELECT id, name, specialization FROM doctors ORDER BY name");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                doctorIds.add(rs.getInt("id"));
                doctorBox.addItem("Dr. " + rs.getString("name") + "  —  " + rs.getString("specialization"));
            }
            if (doctorBox.getItemCount() == 0) {
                UITheme.showWarning(this, "No doctors available. Please contact admin.");
                dispose();
            }
        } catch (SQLException ex) { UITheme.showError(this, "Error loading doctors: " + ex.getMessage()); }
    }

    private void confirmBooking() {
        if (doctorBox.getSelectedIndex() < 0) { UITheme.showWarning(this, "Please select a doctor."); return; }
        String dateStr = dateField.getText().trim();
        if (dateStr.isEmpty()) { UITheme.showWarning(this, "Please enter a date."); return; }

        java.sql.Date sqlDate;
        try { sqlDate = java.sql.Date.valueOf(dateStr); }
        catch (IllegalArgumentException e) { UITheme.showWarning(this, "Invalid date format. Use YYYY-MM-DD."); return; }

        if (sqlDate.before(new java.sql.Date(System.currentTimeMillis()))) {
            UITheme.showWarning(this, "Cannot book an appointment in the past."); return;
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO appointments (patient_id, doctor_id, date, status) VALUES (?,?,?,'Pending')")) {
            ps.setInt(1, patientId); ps.setInt(2, doctorIds.get(doctorBox.getSelectedIndex())); ps.setDate(3, sqlDate);
            ps.executeUpdate();
            UITheme.showSuccess(this, "Appointment booked!\nDoctor: " + doctorBox.getSelectedItem() +
                "\nDate: " + dateStr + "\nStatus: Pending");
            dispose();
        } catch (SQLException ex) { UITheme.showError(this, "Booking failed: " + ex.getMessage()); }
    }
}
