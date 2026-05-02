package patient;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import util.UITheme;
import util.DBConnection;

public class ViewAppointments extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private final int patientId;

    public ViewAppointments(int patientId) {
        this.patientId = patientId;
        setTitle("My Appointments");
        setSize(1024, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(UITheme.BG_DARK);
        setLayout(null);
        buildUI(); loadData(); setVisible(true);
    }

    private void buildUI() {
        JPanel header = UITheme.createCard(0, 0, 1024, 72);
        header.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, UITheme.STAT_BLUE));
        add(header);
        header.add(UITheme.createLabel("📋  My Appointments",
                UITheme.FONT_HEADER, UITheme.TEXT_PRIMARY, 26, 14, 500, 26));
        header.add(UITheme.createLabel("All your scheduled and past visits",
                UITheme.FONT_SMALL, UITheme.TEXT_MUTED, 26, 42, 500, 20));

        JButton refreshBtn = UITheme.createAccentButton("↻ Refresh", 878, 18, 120, 36);
        refreshBtn.addActionListener(e -> loadData());
        header.add(refreshBtn);

        String[] cols = {"Appt #", "Doctor", "Specialization", "Date", "Status"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        UITheme.styleTable(table);

        // Color-code status column
        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, value, sel, foc, row, col);
                setHorizontalAlignment(CENTER); setBackground(UITheme.BG_CARD);
                String s = value == null ? "" : value.toString();
                switch (s) {
                    case "Confirmed" -> setForeground(UITheme.STAT_GREEN);
                    case "Cancelled" -> setForeground(UITheme.ACCENT_DANGER);
                    default          -> setForeground(UITheme.STAT_ORANGE);
                }
                return this;
            }
        });

        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(1).setPreferredWidth(250);
        table.getColumnModel().getColumn(2).setPreferredWidth(250);
        table.getColumnModel().getColumn(3).setPreferredWidth(160);
        table.getColumnModel().getColumn(4).setPreferredWidth(130);

        JScrollPane sp = UITheme.createScrollPane(table, 20, 88, 984, 530);
        add(sp);

        add(UITheme.createLabel("🟠 Pending  🟢 Confirmed  🔴 Cancelled",
                UITheme.FONT_SMALL, UITheme.TEXT_MUTED, 20, 640, 400, 22));
        JButton closeBtn = UITheme.createSecondaryButton("Close", 884, 630, 100, 28);
        closeBtn.addActionListener(e -> dispose()); add(closeBtn);
    }

    private void loadData() {
        model.setRowCount(0);
        String sql = "SELECT a.id, d.name AS doctor, d.specialization, a.date, a.status " +
                     "FROM appointments a JOIN doctors d ON a.doctor_id=d.id " +
                     "WHERE a.patient_id=? ORDER BY a.date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, patientId); ResultSet rs = ps.executeQuery();
            while (rs.next()) model.addRow(new Object[]{
                "#" + rs.getInt("id"), "Dr. " + rs.getString("doctor"),
                rs.getString("specialization"), rs.getString("date"), rs.getString("status")});
            if (model.getRowCount() == 0)
                UITheme.showWarning(this, "No appointments found. Book one from the dashboard!");
        } catch (SQLException ex) { UITheme.showError(this, "Error: " + ex.getMessage()); }
    }
}
