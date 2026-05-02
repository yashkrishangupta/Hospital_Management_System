package admin;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import util.UITheme;
import util.DBConnection;

public class ViewPatients extends JFrame {

    private JTable table;
    private DefaultTableModel model;

    public ViewPatients() {
        setTitle("All Patients");
        setSize(1024, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(UITheme.BG_DARK);
        setLayout(null);
        buildUI();
        loadData();
        setVisible(true);
    }

    private void buildUI() {
        JPanel header = UITheme.createCard(0, 0, 1024, 72);
        header.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, UITheme.STAT_GREEN));
        add(header);
        header.add(UITheme.createLabel("Registered Patients",
                UITheme.FONT_HEADER, UITheme.TEXT_PRIMARY, 26, 14, 500, 26));
        header.add(UITheme.createLabel("All patients registered in the system",
                UITheme.FONT_SMALL, UITheme.TEXT_MUTED, 26, 42, 500, 20));

        JButton refreshBtn = UITheme.createAccentButton("Refresh", 878, 18, 120, 36);
        refreshBtn.addActionListener(e -> loadData());
        header.add(refreshBtn);

        String[] cols = {"ID", "Name", "Age", "Gender", "Contact", "Username"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        UITheme.styleTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(220);
        table.getColumnModel().getColumn(2).setPreferredWidth(60);
        table.getColumnModel().getColumn(3).setPreferredWidth(90);
        table.getColumnModel().getColumn(4).setPreferredWidth(180);
        table.getColumnModel().getColumn(5).setPreferredWidth(180);

        JScrollPane sp = UITheme.createScrollPane(table, 20, 88, 984, 500);
        add(sp);

        // Action bar
        JPanel actionBar = UITheme.createCard(20, 596, 984, 58);
        add(actionBar);

        actionBar.add(UITheme.createLabel("Select a row to delete:", UITheme.FONT_LABEL,
                UITheme.TEXT_MUTED, 14, 18, 220, 22));

        JButton deleteBtn = UITheme.createDangerButton("Delete Patient", 200, 10, 180, 38);
        deleteBtn.addActionListener(e -> deleteSelected());
        actionBar.add(deleteBtn);

        JButton closeBtn = UITheme.createSecondaryButton("Close", 864, 15, 100, 28);
        closeBtn.addActionListener(e -> dispose());
        actionBar.add(closeBtn);
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            UITheme.showWarning(this, "Please select a patient row first.");
            return;
        }

        int    id   = (int) model.getValueAt(row, 0);
        String name = model.getValueAt(row, 1).toString();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete patient: " + name + "?\n" +
                "This will also delete all their appointments and prescriptions.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM patients WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
            UITheme.showSuccess(this, name + " has been deleted.");
            loadData();
        } catch (SQLException ex) {
            UITheme.showError(this, "Delete failed: " + ex.getMessage());
        }
    }

    private void loadData() {
        model.setRowCount(0);
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                "SELECT id, name, age, gender, contact, username FROM patients ORDER BY id");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                model.addRow(new Object[]{rs.getInt("id"), rs.getString("name"),
                    rs.getInt("age"), rs.getString("gender"),
                    rs.getString("contact"), rs.getString("username")});
            }
        } catch (SQLException ex) {
            UITheme.showError(this, "Error loading patients: " + ex.getMessage());
        }
    }
}
