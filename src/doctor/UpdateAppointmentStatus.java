package doctor;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import util.UITheme;
import util.DBConnection;

public class UpdateAppointmentStatus extends JFrame {

    private final int doctorId;
    private DefaultTableModel model;
    private JTable table;
    private java.util.List<Integer> apptIds = new java.util.ArrayList<>();

    public UpdateAppointmentStatus(int doctorId) {
        this.doctorId = doctorId;
        setTitle("Update Appointment Status");
        setSize(1024, 700);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(UITheme.BG_DARK);
        setLayout(null);
        buildUI(); loadData(); setVisible(true);
    }

    private void buildUI() {
        JPanel header = UITheme.createCard(0, 0, 1024, 72);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, UITheme.ACCENT));
        add(header);
        header.add(UITheme.createLabel("🔄  Update Appointment Status", UITheme.FONT_HEADER, UITheme.TEXT_PRIMARY, 26, 14, 500, 26));
        header.add(UITheme.createLabel("Select a row then click Confirm or Cancel", UITheme.FONT_SMALL, UITheme.TEXT_MUTED, 26, 42, 500, 20));
        JButton refreshBtn = UITheme.createAccentButton("↻ Refresh", 770, 18, 140, 36);
        refreshBtn.addActionListener(e -> loadData()); header.add(refreshBtn);
        JButton closeBtn = UITheme.createDangerButton("Close", 920, 18, 80, 36);
        closeBtn.addActionListener(e -> dispose()); header.add(closeBtn);

        String[] cols = {"Appt #", "Patient", "Date", "Current Status"};
        model = new DefaultTableModel(cols, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        table = new JTable(model);
        UITheme.styleTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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
        table.getColumnModel().getColumn(1).setPreferredWidth(450);
        table.getColumnModel().getColumn(2).setPreferredWidth(200);
        table.getColumnModel().getColumn(3).setPreferredWidth(220);

        JScrollPane sp = UITheme.createScrollPane(table, 20, 88, 984, 480);
        add(sp);

        JPanel actionBar = UITheme.createCard(20, 578, 984, 60);
        add(actionBar);
        actionBar.add(UITheme.createLabel("Selected appointment:", UITheme.FONT_LABEL, UITheme.TEXT_MUTED, 14, 19, 200, 22));

        JButton confirmBtn = UITheme.createAccentButton("✔  Confirm", 220, 11, 200, 38);
        confirmBtn.addActionListener(e -> updateStatus("Confirmed")); actionBar.add(confirmBtn);

        JButton cancelBtn = UITheme.createDangerButton("✖  Cancel Appt", 430, 11, 220, 38);
        cancelBtn.addActionListener(e -> updateStatus("Cancelled")); actionBar.add(cancelBtn);

        JButton pendingBtn = UITheme.createSecondaryButton("↺  Reset Pending", 660, 11, 200, 38);
        pendingBtn.addActionListener(e -> updateStatus("Pending")); actionBar.add(pendingBtn);
    }

    private void loadData() {
        model.setRowCount(0); apptIds.clear();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                "SELECT a.id, p.name, a.date, a.status FROM appointments a JOIN patients p ON a.patient_id=p.id WHERE a.doctor_id=? ORDER BY a.date DESC")) {
            ps.setInt(1, doctorId); ResultSet rs = ps.executeQuery();
            while (rs.next()) { apptIds.add(rs.getInt("id"));
                model.addRow(new Object[]{"#" + rs.getInt("id"), rs.getString("name"), rs.getString("date"), rs.getString("status")}); }
        } catch (SQLException ex) { UITheme.showError(this, "Error: " + ex.getMessage()); }
    }

    private void updateStatus(String status) {
        int row = table.getSelectedRow();
        if (row < 0) { UITheme.showWarning(this, "Please select an appointment row first."); return; }
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE appointments SET status=? WHERE id=?")) {
            ps.setString(1, status); ps.setInt(2, apptIds.get(row)); ps.executeUpdate();
            UITheme.showSuccess(this, "Appointment #" + apptIds.get(row) + " marked as " + status + "."); loadData();
        } catch (SQLException ex) { UITheme.showError(this, "Update failed: " + ex.getMessage()); }
    }
}
