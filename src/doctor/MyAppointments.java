package doctor;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import util.UITheme;
import util.DBConnection;

public class MyAppointments extends JFrame {

    private final int doctorId;
    private DefaultTableModel model;
    private JTable table;
    private JComboBox<String> filterBox;

    public MyAppointments(int doctorId) {
        this.doctorId = doctorId;
        setTitle("My Appointments");
        setSize(1024, 700);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(UITheme.BG_DARK);
        setLayout(null);
        buildUI(); loadData("All"); setVisible(true);
    }

    private void buildUI() {
        JPanel header = UITheme.createCard(0, 0, 1024, 72);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, UITheme.STAT_BLUE));
        add(header);
        header.add(UITheme.createLabel("📋  My Appointments", UITheme.FONT_HEADER, UITheme.TEXT_PRIMARY, 26, 14, 500, 26));
        header.add(UITheme.createLabel("All appointments assigned to you", UITheme.FONT_SMALL, UITheme.TEXT_MUTED, 26, 42, 500, 20));
        JButton refreshBtn = UITheme.createAccentButton("↻ Refresh", 878, 18, 120, 36);
        refreshBtn.addActionListener(e -> loadData((String) filterBox.getSelectedItem()));
        header.add(refreshBtn);

        JPanel filterBar = UITheme.createCard(20, 88, 984, 52);
        add(filterBar);
        filterBar.add(UITheme.createLabel("Filter:", UITheme.FONT_LABEL, UITheme.TEXT_MUTED, 14, 14, 60, 22));
        filterBox = UITheme.createComboBox(78, 8, 200, 36);
        filterBox.addItem("All"); filterBox.addItem("Pending"); filterBox.addItem("Confirmed");
        filterBox.addItem("Cancelled"); filterBox.addItem("Today");
        filterBox.addActionListener(e -> loadData((String) filterBox.getSelectedItem()));
        filterBar.add(filterBox);

        String[] cols = {"Appt #", "Patient Name", "Date", "Status"};
        model = new DefaultTableModel(cols, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        table = new JTable(model);
        UITheme.styleTable(table);
        table.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object value, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, value, sel, foc, row, col);
                setHorizontalAlignment(CENTER); setBackground(UITheme.BG_CARD);
                String s = value == null ? "" : value.toString();
                setForeground(switch (s) { case "Confirmed" -> UITheme.STAT_GREEN; case "Cancelled" -> UITheme.ACCENT_DANGER; default -> UITheme.STAT_ORANGE; });
                return this;
            }
        });
        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(1).setPreferredWidth(500);
        table.getColumnModel().getColumn(2).setPreferredWidth(200);
        table.getColumnModel().getColumn(3).setPreferredWidth(160);

        JScrollPane sp = UITheme.createScrollPane(table, 20, 148, 984, 470);
        add(sp);
        add(UITheme.createLabel("🟠 Pending  🟢 Confirmed  🔴 Cancelled", UITheme.FONT_SMALL, UITheme.TEXT_MUTED, 20, 632, 400, 20));
        JButton closeBtn = UITheme.createSecondaryButton("Close", 884, 628, 120, 28);
        closeBtn.addActionListener(e -> dispose()); add(closeBtn);
    }

    private void loadData(String filter) {
        model.setRowCount(0);
        String where = switch (filter) {
            case "Pending" -> " AND a.status='Pending'"; case "Confirmed" -> " AND a.status='Confirmed'";
            case "Cancelled" -> " AND a.status='Cancelled'"; case "Today" -> " AND a.date=CURDATE()"; default -> "";
        };
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                "SELECT a.id, p.name, a.date, a.status FROM appointments a JOIN patients p ON a.patient_id=p.id WHERE a.doctor_id=?" + where + " ORDER BY a.date DESC")) {
            ps.setInt(1, doctorId); ResultSet rs = ps.executeQuery();
            while (rs.next()) model.addRow(new Object[]{"#" + rs.getInt("id"), rs.getString("name"), rs.getString("date"), rs.getString("status")});
        } catch (SQLException ex) { UITheme.showError(this, "Error: " + ex.getMessage()); }
    }
}
