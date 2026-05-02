package doctor;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import util.UITheme;
import util.DBConnection;

public class DoctorDashboard extends JFrame {

    private final int doctorId;
    private final String doctorName, specialization;
    private JLabel statTotal, statToday, statPending, statDone;

    public DoctorDashboard(int doctorId, String doctorName, String specialization) {
        this.doctorId = doctorId; this.doctorName = doctorName; this.specialization = specialization;
        setTitle("Doctor Dashboard — Dr. " + doctorName);
        setSize(1024, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(UITheme.BG_DARK);
        setLayout(null);
        buildUI(); loadStats(); setVisible(true);
    }

    private void buildUI() {
        JPanel topBar = new JPanel(null);
        topBar.setBounds(0, 0, 1024, 72);
        topBar.setBackground(UITheme.BG_CARD);
        topBar.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, UITheme.STAT_BLUE));
        add(topBar);

        topBar.add(UITheme.createLabel("👨‍⚕️", new Font("Segoe UI Emoji", Font.PLAIN, 28),
                UITheme.STAT_BLUE, 22, 14, 46, 44));
        topBar.add(UITheme.createLabel("Dr. " + doctorName + "  —  Doctor Portal",
                UITheme.FONT_HEADER, UITheme.TEXT_PRIMARY, 74, 12, 560, 28));
        topBar.add(UITheme.createLabel("🩺 " + specialization,
                UITheme.FONT_BODY, UITheme.TEXT_MUTED, 74, 42, 400, 22));

        JButton refresh = UITheme.createAccentButton("↻ Refresh", 760, 18, 140, 36);
        refresh.addActionListener(e -> loadStats());
        topBar.add(refresh);

        JButton logout = UITheme.createDangerButton("Logout", 920, 18, 80, 36);
        logout.addActionListener(e -> { dispose(); new app.Login(); });
        topBar.add(logout);

        // Stats
        add(UITheme.createLabel("📊  My Statistics", UITheme.FONT_HEADER, UITheme.TEXT_MUTED, 26, 88, 250, 26));
        statTotal   = buildStat(20,  120, "📅  Total Appointments",  UITheme.STAT_BLUE);
        statToday   = buildStat(270, 120, "🗓️  Today",         UITheme.STAT_ORANGE);
        statPending = buildStat(520, 120, "⏳  Pending",       UITheme.ACCENT_WARN);
        statDone    = buildStat(770, 120, "✅  Completed",     UITheme.STAT_GREEN);

        // Menu
        add(UITheme.createLabel("⚙️  My Tools", UITheme.FONT_HEADER, UITheme.TEXT_MUTED, 26, 255, 250, 26));
        add(UITheme.createSeparator(20, 282, 984));

        buildMenu("📋  My Appointments",    "View all assigned appointments",    20,  298, UITheme.STAT_BLUE,        e -> new MyAppointments(doctorId));
        buildMenu("🔄  Update Status",      "Confirm or cancel appointments",    270, 298, UITheme.ACCENT,           e -> new UpdateAppointmentStatus(doctorId));
        buildMenu("💊  Write Prescription", "Add medicines to a visit",          520, 298, new Color(180, 120, 255), e -> new DoctorPrescriptionWriter(doctorId));
        buildMenu("👥  My Patients",        "Patients who visited you",          770, 298, UITheme.STAT_GREEN,       e -> new MyPatients(doctorId));

        buildMenu("📊  Schedule Chart",     "Heatmap of your bookings",          20,  438, UITheme.STAT_ORANGE,     e -> new DoctorScheduleChart(doctorId, doctorName));
        buildMenu("👤  My Profile",         "View & edit your details",          270, 438, UITheme.TEXT_MUTED,      e -> new DoctorProfile(doctorId, doctorName, specialization, this));

        JLabel footer = UITheme.createLabel(
                "© 2025 Hospital Management System  |  Logged in as Dr. " + doctorName,
                UITheme.FONT_SMALL, UITheme.TEXT_MUTED, 0, 664, 1024, 22);
        footer.setHorizontalAlignment(SwingConstants.CENTER);
        add(footer);
    }

    private JLabel buildStat(int x, int y, String caption, Color accent) {
        JPanel card = new JPanel(null);
        card.setBounds(x, y, 224, 110);
        card.setBackground(UITheme.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 4, 0, 0, accent),
            BorderFactory.createLineBorder(UITheme.BORDER_COLOR)));
        add(card);
        JLabel num = new JLabel("—");
        num.setFont(UITheme.FONT_STAT); num.setForeground(accent);
        num.setBounds(15, 12, 200, 52); card.add(num);
        card.add(UITheme.createLabel(caption, UITheme.FONT_SMALL, UITheme.TEXT_MUTED, 15, 70, 200, 22));
        return num;
    }

    private void buildMenu(String label, String subtitle, int x, int y,
                            Color accent, java.awt.event.ActionListener action) {
        JPanel card = UITheme.createCard(x, y, 224, 120);
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        add(card);
        String[] parts = label.split("  ", 2);
        card.add(UITheme.createLabel(parts[0], new Font("Segoe UI Emoji", Font.PLAIN, 22), accent, 12, 10, 40, 36));
        card.add(UITheme.createLabel(parts.length > 1 ? parts[1] : label, UITheme.FONT_LABEL, UITheme.TEXT_PRIMARY, 12, 50, 205, 22));
        card.add(UITheme.createLabel(subtitle, UITheme.FONT_SMALL, UITheme.TEXT_MUTED, 12, 72, 205, 18));
        JButton btn = UITheme.createAccentButton("Open →", 12, 92, 200, 22);
        btn.setFont(UITheme.FONT_SMALL); btn.addActionListener(action); card.add(btn);
    }

    public void loadStats() {
        try (Connection conn = DBConnection.getConnection()) {
            statTotal.setText(q(conn, "SELECT COUNT(*) FROM appointments WHERE doctor_id=?"));
            statToday.setText(q(conn, "SELECT COUNT(*) FROM appointments WHERE doctor_id=? AND date=CURDATE()"));
            statPending.setText(q(conn, "SELECT COUNT(*) FROM appointments WHERE doctor_id=? AND status='Pending'"));
            statDone.setText(q(conn, "SELECT COUNT(*) FROM appointments WHERE doctor_id=? AND status='Confirmed'"));
        } catch (SQLException ex) {
            statTotal.setText("?"); statToday.setText("?"); statPending.setText("?"); statDone.setText("?");
        }
    }

    private String q(Connection conn, String sql) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, doctorId); ResultSet rs = ps.executeQuery();
            return rs.next() ? String.valueOf(rs.getInt(1)) : "0";
        }
    }
}
