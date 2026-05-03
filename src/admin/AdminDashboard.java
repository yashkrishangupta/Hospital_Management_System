package admin;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import util.UITheme;
import util.DBConnection;

public class AdminDashboard extends JFrame {

    private JLabel statDoctors, statPatients, statAppts, statToday;

    public AdminDashboard() {
        setTitle("Admin Dashboard — Hospital Management System");
        setSize(1024, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(UITheme.BG_PAGE);
        setLayout(null);
        buildUI();
        loadStats();
        setVisible(true);
    }

    private void buildUI() {
        // Top bar — full width
        JPanel topBar = new JPanel(null);
        topBar.setBounds(0, 0, 1024, 72);
        topBar.setBackground(UITheme.BG_CARD);
        topBar.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, UITheme.ACCENT));
        add(topBar);

        topBar.add(UITheme.createLabel("🏥", new Font("Segoe UI Emoji", Font.PLAIN, 28),
                UITheme.ACCENT, 22, 14, 46, 44));
        topBar.add(UITheme.createLabel("Hospital Management — Admin Portal",
                UITheme.FONT_HEADER, UITheme.TEXT_PRIMARY, 74, 12, 480, 28));
        topBar.add(UITheme.createLabel("Full administrative access",
                UITheme.FONT_SMALL, UITheme.TEXT_MUTED, 74, 42, 300, 18));

        JButton refresh = UITheme.createAccentButton("↻ Refresh Stats", 770, 18, 140, 36);
        refresh.addActionListener(e -> loadStats());
        topBar.add(refresh);

        JButton logout = UITheme.createDangerButton("Logout", 920, 18, 80, 36);
        logout.addActionListener(e -> { dispose(); new app.Login(); });
        topBar.add(logout);

        // Stats section label
        add(UITheme.createLabel("📊  Live Statistics",
                UITheme.FONT_HEADER, UITheme.TEXT_MUTED, 26, 88, 250, 26));

        // Stat cards — 4 across, evenly spaced in 1024px
        statDoctors  = buildStatCard(20,  120, "👨‍⚕️  Doctors",     UITheme.STAT_BLUE);
        statPatients = buildStatCard(270, 120, "🧑  Patients",     UITheme.STAT_GREEN);
        statAppts    = buildStatCard(520, 120, "📅  Appointments", UITheme.ACCENT);
        statToday    = buildStatCard(770, 120, "🗓️  Today",         UITheme.STAT_ORANGE);

        // Menu section
        add(UITheme.createLabel("⚙️  Management Menu",
                UITheme.FONT_HEADER, UITheme.TEXT_MUTED, 26, 255, 300, 26));
        add(UITheme.createSeparator(20, 282, 984));

        // Row 1 — 4 cards across
        buildMenuButton("➕  Add Doctor",    "Onboard a new doctor",        20,  298, UITheme.STAT_BLUE,        e -> new AddDoctor(this));
        buildMenuButton("📋  View Doctors",  "See all registered doctors",  270, 298, UITheme.STAT_BLUE,        e -> new ViewDoctors());
        buildMenuButton("👤  Add Patient",   "Register a new patient",      520, 298, UITheme.STAT_GREEN,       e -> new AddPatient(this));
        buildMenuButton("👥  View Patients", "Browse all patient records",  770, 298, UITheme.STAT_GREEN,       e -> new ViewPatients());

        // Row 2 — 4 cards across
        buildMenuButton("📅  All Appointments",     "View all appointments",       20,  438, UITheme.ACCENT,           e -> new ViewAllAppointments());
        buildMenuButton("🔍  Search",        "Search patients & doctors",   270, 438, UITheme.STAT_ORANGE,      e -> new SearchPanel());
        buildMenuButton("📊  Heatmap",       "Doctor availability calendar",520, 438, UITheme.STAT_ORANGE,      e -> new DoctorHeatmap());
        buildMenuButton("💊  Prescriptions", "Write patient prescriptions", 770, 438, new Color(180, 120, 255), e -> new PrescriptionWriter());

        // add(UITheme.createLabel("⭐  Heatmap & Prescriptions are unique features",
        //         UITheme.FONT_SMALL, new Color(255, 200, 60), 520, 422, 484, 18));

        // Footer
        JLabel footer = UITheme.createLabel(
                "© 2025 Hospital Management System  |  Logged in as: Admin",
                UITheme.FONT_SMALL, UITheme.TEXT_MUTED, 0, 664, 1024, 22);
        footer.setHorizontalAlignment(SwingConstants.CENTER);
        add(footer);
    }

    private JLabel buildStatCard(int x, int y, String caption, Color accent) {
        JPanel card = new JPanel(null);
        card.setBounds(x, y, 224, 110);
        card.setBackground(UITheme.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 4, 0, 0, accent),
            BorderFactory.createLineBorder(UITheme.BORDER_COLOR)
        ));
        add(card);

        JLabel num = new JLabel("—");
        num.setFont(UITheme.FONT_STAT);
        num.setForeground(accent);
        num.setBounds(15, 12, 195, 52);
        card.add(num);

        card.add(UITheme.createLabel(caption, UITheme.FONT_SMALL,
                UITheme.TEXT_MUTED, 15, 70, 200, 22));
        return num;
    }

    private void buildMenuButton(String label, String subtitle, int x, int y,
                                  Color accent, java.awt.event.ActionListener action) {
        JPanel card = UITheme.createCard(x, y, 224, 120);
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        add(card);

        String[] parts = label.split("  ", 2);
        card.add(UITheme.createLabel(parts[0],
                new Font("Segoe UI Emoji", Font.PLAIN, 22), accent, 12, 10, 40, 36));
        card.add(UITheme.createLabel(parts.length > 1 ? parts[1] : label,
                UITheme.FONT_LABEL, UITheme.TEXT_PRIMARY, 12, 50, 205, 22));
        card.add(UITheme.createLabel(subtitle,
                UITheme.FONT_SMALL, UITheme.TEXT_MUTED, 12, 72, 205, 18));

        JButton btn = UITheme.createAccentButton("Open →", 12, 92, 200, 22);
        btn.setFont(UITheme.FONT_SMALL);
        btn.addActionListener(action);
        card.add(btn);
    }

    public void loadStats() {
        try (Connection conn = DBConnection.getConnection()) {
            statDoctors.setText(q(conn, "SELECT COUNT(*) FROM doctors"));
            statPatients.setText(q(conn, "SELECT COUNT(*) FROM patients"));
            statAppts.setText(q(conn, "SELECT COUNT(*) FROM appointments"));
            statToday.setText(q(conn, "SELECT COUNT(*) FROM appointments WHERE date=CURDATE()"));
        } catch (SQLException ex) {
            statDoctors.setText("?"); statPatients.setText("?");
            statAppts.setText("?");   statToday.setText("?");
        }
    }

    private String q(Connection conn, String sql) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            return rs.next() ? String.valueOf(rs.getInt(1)) : "0";
        }
    }
}
