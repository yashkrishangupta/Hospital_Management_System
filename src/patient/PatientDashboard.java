package patient;

import javax.swing.*;
import java.awt.*;
import util.UITheme;

public class PatientDashboard extends JFrame {

    private final int    patientId;
    private final String patientName;

    public PatientDashboard(int patientId, String patientName) {
        this.patientId   = patientId;
        this.patientName = patientName;
        setTitle("Patient Dashboard — " + patientName);
        setSize(1024, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(UITheme.BG_DARK);
        setLayout(null);
        buildUI();
        setVisible(true);
    }

    private void buildUI() {
        // Top bar
        JPanel topBar = new JPanel(null);
        topBar.setBounds(0, 0, 1024, 72);
        topBar.setBackground(UITheme.BG_CARD);
        topBar.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, UITheme.STAT_GREEN));
        add(topBar);

        topBar.add(UITheme.createLabel("HMS", new Font("Segoe UI", Font.BOLD, 18),
                UITheme.STAT_GREEN, 22, 22, 60, 28));
        topBar.add(UITheme.createLabel("Patient Portal",
                UITheme.FONT_HEADER, UITheme.TEXT_PRIMARY, 90, 12, 400, 28));
        topBar.add(UITheme.createLabel("Welcome back, " + patientName,
                UITheme.FONT_BODY, UITheme.TEXT_MUTED, 90, 42, 500, 22));

        JButton logoutBtn = UITheme.createDangerButton("Logout", 928, 18, 80, 36);
        logoutBtn.addActionListener(e -> { dispose(); new app.Login(); });
        topBar.add(logoutBtn);

        // Info banner
        JPanel greet = UITheme.createCard(20, 88, 984, 52);
        greet.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createMatteBorder(0, 4, 0, 0, UITheme.STAT_GREEN),
            javax.swing.BorderFactory.createLineBorder(UITheme.BORDER_COLOR)));
        add(greet);
        greet.add(UITheme.createLabel("Patient ID: #" + patientId,
                UITheme.FONT_LABEL, UITheme.TEXT_MUTED, 18, 10, 200, 20));
        greet.add(UITheme.createLabel("Use the options below to book and manage your appointments.",
                UITheme.FONT_BODY, UITheme.TEXT_PRIMARY, 18, 30, 700, 20));

        // Section label
        add(UITheme.createLabel("Your Options",
                UITheme.FONT_HEADER, UITheme.TEXT_MUTED, 26, 158, 300, 26));
        add(UITheme.createSeparator(20, 185, 984));

        // 4 cards — 2x2 grid
        buildCard(20,  200, UITheme.ACCENT,
                "Book Appointment", "Choose a doctor and pick a date",
                "Book Now", true,
                e -> new BookAppointment(patientId, this));

        buildCard(522, 200, UITheme.STAT_BLUE,
                "My Appointments", "View all your scheduled visits",
                "View All", false,
                e -> new ViewAppointments(patientId));

        buildCard(20,  400, new Color(180, 120, 255),
                "My Prescriptions", "Medicines prescribed by your doctor",
                "View", false,
                e -> new ViewPrescription(patientId));

        buildCard(522, 400, UITheme.STAT_GREEN,
                "My Profile", "View details and change password",
                "Open Profile", false,
                e -> new PatientProfile(patientId));

        // Footer
        JLabel footer = UITheme.createLabel("© 2025 Hospital Management System",
                UITheme.FONT_SMALL, UITheme.TEXT_MUTED, 0, 664, 1024, 22);
        footer.setHorizontalAlignment(SwingConstants.CENTER);
        add(footer);
    }

    private void buildCard(int x, int y, Color accent,
                            String title, String subtitle,
                            String btnLabel, boolean primary,
                            java.awt.event.ActionListener action) {
        JPanel card = UITheme.createCard(x, y, 482, 170);
        card.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createMatteBorder(0, 4, 0, 0, accent),
            javax.swing.BorderFactory.createLineBorder(UITheme.BORDER_COLOR)));
        add(card);

        card.add(UITheme.createLabel(title,
                UITheme.FONT_HEADER, UITheme.TEXT_PRIMARY, 16, 24, 450, 28));
        card.add(UITheme.createLabel(subtitle,
                UITheme.FONT_SMALL, UITheme.TEXT_MUTED, 16, 58, 450, 20));

        JButton btn = primary
            ? UITheme.createAccentButton(btnLabel, 16, 110, 450, 42)
            : UITheme.createSecondaryButton(btnLabel, 16, 110, 450, 42);
        btn.addActionListener(action);
        card.add(btn);
    }
}
