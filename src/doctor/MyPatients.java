package doctor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import util.UITheme;
import util.DBConnection;

public class MyPatients extends JFrame {

    private final int doctorId;
    private DefaultTableModel model;

    public MyPatients(int doctorId) {
        this.doctorId = doctorId;
        setTitle("My Patients");
        setSize(1024, 700);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(UITheme.BG_PAGE);
        setLayout(null);
        buildUI(); loadData(); setVisible(true);
    }

    private void buildUI() {
        JPanel header = UITheme.createCard(0, 0, 1024, 72);
        header.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, UITheme.STAT_GREEN));
        add(header);
        header.add(UITheme.createLabel("👥  My Patients", UITheme.FONT_HEADER, UITheme.TEXT_PRIMARY, 26, 14, 500, 26));
        header.add(UITheme.createLabel("All patients who have booked an appointment with you", UITheme.FONT_SMALL, UITheme.TEXT_MUTED, 26, 42, 600, 20));
        JButton refreshBtn = UITheme.createAccentButton("↻ Refresh", 878, 18, 120, 36);
        refreshBtn.addActionListener(e -> loadData()); header.add(refreshBtn);

        String[] cols = {"Patient ID", "Name", "Age", "Gender", "Contact", "Visits"};
        model = new DefaultTableModel(cols, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        JTable table = new JTable(model);
        UITheme.styleTable(table);
        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(1).setPreferredWidth(280);
        table.getColumnModel().getColumn(2).setPreferredWidth(70);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        table.getColumnModel().getColumn(4).setPreferredWidth(200);
        table.getColumnModel().getColumn(5).setPreferredWidth(80);

        JScrollPane sp = UITheme.createScrollPane(table, 20, 88, 984, 530);
        add(sp);
        JButton closeBtn = UITheme.createSecondaryButton("Close", 884, 630, 100, 28);
        closeBtn.addActionListener(e -> dispose()); add(closeBtn);
    }

    private void loadData() {
        model.setRowCount(0);
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                "SELECT p.id, p.name, p.age, p.gender, p.contact, COUNT(a.id) AS visits " +
                "FROM patients p JOIN appointments a ON p.id=a.patient_id WHERE a.doctor_id=? GROUP BY p.id ORDER BY visits DESC")) {
            ps.setInt(1, doctorId); ResultSet rs = ps.executeQuery();
            while (rs.next()) model.addRow(new Object[]{rs.getInt("id"), rs.getString("name"),
                rs.getInt("age"), rs.getString("gender"), rs.getString("contact"), rs.getInt("visits")});
            if (model.getRowCount() == 0) UITheme.showWarning(this, "No patients yet.");
        } catch (SQLException ex) { UITheme.showError(this, "Error: " + ex.getMessage()); }
    }
}
