package patient;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import util.UITheme;
import util.DBConnection;

public class ViewPrescription extends JFrame {

    private final int patientId;
    private JComboBox<String> apptBox;
    private java.util.List<Integer> apptIds = new java.util.ArrayList<>();
    private DefaultTableModel medModel;
    private JTextArea notesArea;

    public ViewPrescription(int patientId) {
        this.patientId = patientId;
        setTitle("My Prescriptions");
        setSize(1024, 700);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(UITheme.BG_PAGE);
        setLayout(null);
        buildUI(); loadMyAppointments(); setVisible(true);
    }

    private void buildUI() {
        JPanel header = UITheme.createCard(0, 0, 1024, 72);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(180, 120, 255)));
        add(header);
        header.add(UITheme.createLabel("💊  My Prescriptions",
                UITheme.FONT_HEADER, UITheme.TEXT_PRIMARY, 26, 14, 500, 26));
        header.add(UITheme.createLabel("Select an appointment to view medicines prescribed by the doctor",
                UITheme.FONT_SMALL, UITheme.TEXT_MUTED, 26, 42, 650, 20));
        JButton closeBtn = UITheme.createDangerButton("Close", 910, 18, 88, 36);
        closeBtn.addActionListener(e -> dispose()); header.add(closeBtn);

        // Appointment selector
        JPanel selCard = UITheme.createCard(20, 88, 984, 56);
        add(selCard);
        selCard.add(UITheme.createLabel("Appointment:", UITheme.FONT_LABEL,
                UITheme.TEXT_MUTED, 14, 16, 120, 22));
        apptBox = UITheme.createComboBox(140, 10, 700, 36);
        selCard.add(apptBox);
        JButton loadBtn = UITheme.createAccentButton("View", 848, 10, 124, 36);
        loadBtn.addActionListener(e -> loadPrescription()); selCard.add(loadBtn);

        // Medicine table
        add(UITheme.createLabel("💊  Prescribed Medicines",
                UITheme.FONT_HEADER, UITheme.TEXT_MUTED, 22, 158, 350, 26));
        String[] cols = {"Medicine", "Dosage", "Instructions"};
        medModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable medTable = new JTable(medModel);
        UITheme.styleTable(medTable);
        medTable.getColumnModel().getColumn(0).setPreferredWidth(240);
        medTable.getColumnModel().getColumn(1).setPreferredWidth(180);
        medTable.getColumnModel().getColumn(2).setPreferredWidth(540);

        JScrollPane sp = UITheme.createScrollPane(medTable, 20, 188, 984, 220);
        add(sp);

        // Doctor notes
        add(UITheme.createLabel("📝  Doctor's Notes",
                UITheme.FONT_HEADER, UITheme.TEXT_MUTED, 22, 422, 300, 26));
        notesArea = new JTextArea();
        notesArea.setEditable(false);
        notesArea.setBackground(UITheme.BG_INPUT); notesArea.setForeground(UITheme.TEXT_PRIMARY);
        notesArea.setFont(UITheme.FONT_BODY); notesArea.setLineWrap(true); notesArea.setWrapStyleWord(true);
        notesArea.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        JScrollPane notesSp = new JScrollPane(notesArea);
        notesSp.setBounds(20, 452, 984, 170);
        notesSp.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR));
        notesSp.getViewport().setBackground(UITheme.BG_INPUT);
        add(notesSp);

        JLabel footer = UITheme.createLabel("© 2025 Hospital Management System",
                UITheme.FONT_SMALL, UITheme.TEXT_MUTED, 0, 642, 1024, 22);
        footer.setHorizontalAlignment(SwingConstants.CENTER); add(footer);
    }

    private void loadMyAppointments() {
        apptIds.clear(); apptBox.removeAllItems();
        String sql = "SELECT a.id, d.name AS doctor, d.specialization, a.date " +
                     "FROM appointments a JOIN doctors d ON a.doctor_id=d.id " +
                     "WHERE a.patient_id=? ORDER BY a.date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, patientId); ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                apptIds.add(rs.getInt("id"));
                apptBox.addItem(String.format("Appt #%d  |  Dr. %s  (%s)  |  %s",
                    rs.getInt("id"), rs.getString("doctor"), rs.getString("specialization"), rs.getString("date")));
            }
            if (apptBox.getItemCount() == 0) apptBox.addItem("No appointments found");
        } catch (SQLException ex) { UITheme.showError(this, "Error: " + ex.getMessage()); }
    }

    private void loadPrescription() {
        int idx = apptBox.getSelectedIndex();
        if (idx < 0 || idx >= apptIds.size()) return;
        medModel.setRowCount(0); notesArea.setText("");
        try (Connection conn = DBConnection.getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet tables = meta.getTables(null, null, "prescriptions", null);
            if (!tables.next()) { UITheme.showWarning(this, "No prescription written yet."); return; }
            PreparedStatement ps = conn.prepareStatement(
                "SELECT medicine, dosage, instructions, notes FROM prescriptions WHERE appointment_id=? ORDER BY id");
            ps.setInt(1, apptIds.get(idx)); ResultSet rs = ps.executeQuery();
            boolean found = false;
            while (rs.next()) {
                found = true;
                medModel.addRow(new Object[]{rs.getString("medicine"), rs.getString("dosage"), rs.getString("instructions")});
                notesArea.setText(rs.getString("notes") != null ? rs.getString("notes") : "");
            }
            if (!found) UITheme.showWarning(this, "No prescription written for this appointment yet.");
        } catch (SQLException ex) { UITheme.showError(this, "Error: " + ex.getMessage()); }
    }
}
