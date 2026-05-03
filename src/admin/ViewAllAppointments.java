package admin;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.sql.*;
import util.UITheme;
import util.DBConnection;

public class ViewAllAppointments extends JFrame {

    private JTable table;
    private DefaultTableModel model;

    public ViewAllAppointments() {
        setTitle("All Appointments");
        setSize(1024, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(UITheme.BG_PAGE);
        setLayout(null);
        buildUI();
        loadData();
        setVisible(true);
    }

    private void buildUI() {
        JPanel header = UITheme.createCard(0, 0, 1024, 72);
        header.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, UITheme.ACCENT));
        add(header);
        header.add(UITheme.createLabel("All Appointments",
                UITheme.FONT_HEADER, UITheme.TEXT_PRIMARY, 26, 14, 500, 26));
        header.add(UITheme.createLabel("Complete appointment log across all patients and doctors",
                UITheme.FONT_SMALL, UITheme.TEXT_MUTED, 26, 42, 600, 20));

        JButton refreshBtn = UITheme.createAccentButton("Refresh", 878, 18, 120, 36);
        refreshBtn.addActionListener(e -> loadData());
        header.add(refreshBtn);

        String[] cols = {"Appt ID", "Patient Name", "Doctor Name", "Specialization", "Date", "Status"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        UITheme.styleTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Color-code status column
        table.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, value, sel, foc, row, col);
                setHorizontalAlignment(CENTER);
                setBackground(UITheme.BG_CARD);
                String s = value == null ? "" : value.toString();
                setForeground(switch (s) {
                    case "Confirmed" -> UITheme.STAT_GREEN;
                    case "Cancelled" -> UITheme.ACCENT_DANGER;
                    default          -> UITheme.STAT_ORANGE;
                });
                return this;
            }
        });

        JScrollPane sp = UITheme.createScrollPane(table, 20, 88, 984, 500);
        add(sp);

        // Action bar
        JPanel actionBar = UITheme.createCard(20, 596, 984, 58);
        add(actionBar);

        actionBar.add(UITheme.createLabel("Select a row to delete:", UITheme.FONT_LABEL,
                UITheme.TEXT_MUTED, 14, 18, 220, 22));

        JButton deleteBtn = UITheme.createDangerButton("Delete Appointment", 200, 10, 200, 38);
        deleteBtn.addActionListener(e -> deleteSelected());
        actionBar.add(deleteBtn);

        actionBar.add(UITheme.createLabel("Deleting an appointment also removes its prescription.",
                UITheme.FONT_SMALL, UITheme.TEXT_MUTED, 414, 18, 440, 22));

        JButton closeBtn = UITheme.createSecondaryButton("Close", 864, 15, 100, 28);
        closeBtn.addActionListener(e -> dispose());
        actionBar.add(closeBtn);
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            UITheme.showWarning(this, "Please select an appointment row first.");
            return;
        }

        int    id      = (int) model.getValueAt(row, 0);
        String patient = model.getValueAt(row, 1).toString();
        String doctor  = model.getValueAt(row, 2).toString();
        String date    = model.getValueAt(row, 4).toString();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete appointment #" + id + "?\n" +
                "Patient : " + patient + "\n" +
                "Doctor  : " + doctor  + "\n" +
                "Date    : " + date,
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM appointments WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
            UITheme.showSuccess(this, "Appointment #" + id + " deleted successfully.");
            loadData();
        } catch (SQLException ex) {
            UITheme.showError(this, "Delete failed: " + ex.getMessage());
        }
    }

    private void loadData() {
        model.setRowCount(0);
        String sql = "SELECT a.id, p.name AS patient, d.name AS doctor, d.specialization, a.date, a.status " +
                     "FROM appointments a JOIN patients p ON a.patient_id=p.id " +
                     "JOIN doctors d ON a.doctor_id=d.id ORDER BY a.date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                model.addRow(new Object[]{rs.getInt("id"), rs.getString("patient"),
                    "Dr. " + rs.getString("doctor"), rs.getString("specialization"),
                    rs.getString("date"), rs.getString("status")});
            }
        } catch (SQLException ex) {
            UITheme.showError(this, "Error: " + ex.getMessage());
        }
    }
}
