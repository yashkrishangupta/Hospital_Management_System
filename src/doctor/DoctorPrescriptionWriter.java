package doctor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.*;
import util.UITheme;
import util.DBConnection;

public class DoctorPrescriptionWriter extends JFrame {

    private final int doctorId;
    private JComboBox<String> apptBox;
    private java.util.List<Integer> apptIds = new ArrayList<>();
    private DefaultTableModel medModel;
    private JTable medTable;
    private JTextArea notesArea;
    private JTextField medName, medDose, medInstr;

    public DoctorPrescriptionWriter(int doctorId) {
        this.doctorId = doctorId;
        setTitle("Write Prescription");
        setSize(1024, 700);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(UITheme.BG_PAGE);
        setLayout(null);
        buildUI(); loadMyAppointments(); setVisible(true);
    }

    private void buildUI() {
        Color PURPLE = new Color(180, 120, 255);

        JPanel header = UITheme.createCard(0, 0, 1024, 72);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, PURPLE));
        add(header);
        header.add(UITheme.createLabel("💊  Write Prescription",
                UITheme.FONT_HEADER, UITheme.TEXT_PRIMARY, 26, 14, 500, 26));
        header.add(UITheme.createLabel("Select one of your appointments and add medicines",
                UITheme.FONT_SMALL, UITheme.TEXT_MUTED, 26, 42, 600, 20));
        JButton closeBtn = UITheme.createDangerButton("Close", 918, 18, 88, 36);
        closeBtn.addActionListener(e -> dispose()); header.add(closeBtn);

        // Appointment selector
        JPanel apptCard = UITheme.createCard(20, 88, 984, 56);
        add(apptCard);
        apptCard.add(UITheme.createLabel("Appointment:", UITheme.FONT_LABEL, UITheme.TEXT_MUTED, 14, 16, 120, 22));
        apptBox = UITheme.createComboBox(140, 10, 700, 36); apptCard.add(apptBox);
        JButton loadBtn = UITheme.createAccentButton("Load", 848, 10, 124, 36);
        loadBtn.addActionListener(e -> loadExisting()); apptCard.add(loadBtn);

        // Medicine table
        add(UITheme.createLabel("💊  Medicines", UITheme.FONT_HEADER, UITheme.TEXT_MUTED, 22, 158, 220, 26));
        String[] cols = {"Medicine Name", "Dosage", "Instructions"};
        medModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return true; }
        };
        medTable = new JTable(medModel);
        UITheme.styleTable(medTable); medTable.setRowHeight(32);
        medTable.getColumnModel().getColumn(0).setPreferredWidth(260);
        medTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        medTable.getColumnModel().getColumn(2).setPreferredWidth(500);

        JScrollPane sp = UITheme.createScrollPane(medTable, 20, 188, 984, 188);
        add(sp);

        // Right-click delete
        JPopupMenu popup = new JPopupMenu();
        JMenuItem del = new JMenuItem("Remove row");
        del.setBackground(UITheme.BG_INPUT); del.setForeground(UITheme.ACCENT_DANGER);
        del.addActionListener(e -> { int r = medTable.getSelectedRow(); if (r >= 0) medModel.removeRow(r); });
        popup.add(del); medTable.setComponentPopupMenu(popup);

        // Add-row bar
        JPanel addRow = UITheme.createCard(20, 382, 984, 56);
        add(addRow);
        medName  = hint(12,  10, 240, 36, "Medicine name", addRow);
        medDose  = hint(262, 10, 190, 36, "Dosage", addRow);
        medInstr = hint(462, 10, 360, 36, "Instructions", addRow);
        JButton addMed = UITheme.createAccentButton("+ Add Row", 832, 10, 140, 36);
        addMed.addActionListener(e -> addRow()); addRow.add(addMed);

        // Notes
        add(UITheme.createLabel("📝  Doctor Notes / Diagnosis",
                UITheme.FONT_HEADER, UITheme.TEXT_MUTED, 22, 452, 400, 26));
        notesArea = new JTextArea();
        notesArea.setBackground(UITheme.BG_INPUT); notesArea.setForeground(UITheme.TEXT_PRIMARY);
        notesArea.setCaretColor(UITheme.ACCENT); notesArea.setFont(UITheme.FONT_BODY);
        notesArea.setLineWrap(true); notesArea.setWrapStyleWord(true);
        notesArea.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        JScrollPane notesSp = new JScrollPane(notesArea);
        notesSp.setBounds(20, 482, 984, 110);
        notesSp.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR));
        notesSp.getViewport().setBackground(UITheme.BG_INPUT); add(notesSp);

        JButton saveBtn = UITheme.createAccentButton("💾  Save Prescription", 20, 618, 200, 30);
        saveBtn.addActionListener(e -> save()); add(saveBtn);
        JButton clearBtn = UITheme.createSecondaryButton("Clear All", 240, 618, 110, 30);
        clearBtn.addActionListener(e -> { medModel.setRowCount(0); notesArea.setText(""); }); add(clearBtn);
        add(UITheme.createLabel("💡 Right-click a row to delete it",
                UITheme.FONT_SMALL, UITheme.TEXT_MUTED, 380, 632, 400, 20));
    }

    private JTextField hint(int x, int y, int w, int h, String hint, JPanel parent) {
        JTextField f = UITheme.createField(x, y, w, h);
        f.setForeground(UITheme.TEXT_MUTED); f.setText(hint);
        f.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (f.getText().equals(hint)) { f.setText(""); f.setForeground(UITheme.TEXT_PRIMARY); }
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                if (f.getText().trim().isEmpty()) { f.setText(hint); f.setForeground(UITheme.TEXT_MUTED); }
            }
        });
        parent.add(f); return f;
    }

    private void loadMyAppointments() {
        apptIds.clear(); apptBox.removeAllItems();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                "SELECT a.id, p.name, a.date, a.status FROM appointments a JOIN patients p ON a.patient_id=p.id WHERE a.doctor_id=? ORDER BY a.date DESC")) {
            ps.setInt(1, doctorId); ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                apptIds.add(rs.getInt("id"));
                apptBox.addItem(String.format("Appt #%d  |  %s  |  %s  [%s]",
                    rs.getInt("id"), rs.getString("name"), rs.getString("date"), rs.getString("status")));
            }
            if (apptBox.getItemCount() == 0) apptBox.addItem("No appointments");
        } catch (SQLException ex) { UITheme.showError(this, "Error: " + ex.getMessage()); }
    }

    private void loadExisting() {
        int idx = apptBox.getSelectedIndex();
        if (idx < 0 || idx >= apptIds.size()) return;
        medModel.setRowCount(0); notesArea.setText("");
        try (Connection conn = DBConnection.getConnection()) {
            ensureTable(conn);
            PreparedStatement ps = conn.prepareStatement(
                "SELECT medicine, dosage, instructions, notes FROM prescriptions WHERE appointment_id=? ORDER BY id");
            ps.setInt(1, apptIds.get(idx)); ResultSet rs = ps.executeQuery(); boolean first = true;
            while (rs.next()) {
                medModel.addRow(new Object[]{rs.getString("medicine"), rs.getString("dosage"), rs.getString("instructions")});
                if (first) { notesArea.setText(rs.getString("notes") != null ? rs.getString("notes") : ""); first = false; }
            }
        } catch (SQLException ex) { /* fresh */ }
    }

    private void addRow() {
        String n = medName.getText().trim(), d = medDose.getText().trim(), i = medInstr.getText().trim();
        if (n.isEmpty() || n.equals("Medicine name")) { UITheme.showWarning(this, "Enter medicine name."); return; }
        if (d.isEmpty() || d.equals("Dosage"))        { UITheme.showWarning(this, "Enter dosage."); return; }
        medModel.addRow(new Object[]{n, d, i.equals("Instructions") ? "" : i});
        rst(medName, "Medicine name"); rst(medDose, "Dosage"); rst(medInstr, "Instructions");
        medName.requestFocus();
    }

    private void rst(JTextField f, String h) { f.setText(h); f.setForeground(UITheme.TEXT_MUTED); }

    private void save() {
        int idx = apptBox.getSelectedIndex();
        if (idx < 0 || idx >= apptIds.size()) { UITheme.showWarning(this, "Select an appointment."); return; }
        if (medModel.getRowCount() == 0)       { UITheme.showWarning(this, "Add at least one medicine."); return; }
        int apptId = apptIds.get(idx);
        try (Connection conn = DBConnection.getConnection()) {
            ensureTable(conn);
            try (PreparedStatement del = conn.prepareStatement("DELETE FROM prescriptions WHERE appointment_id=?")) {
                del.setInt(1, apptId); del.executeUpdate();
            }
            try (PreparedStatement ins = conn.prepareStatement(
                "INSERT INTO prescriptions (appointment_id, medicine, dosage, instructions, notes) VALUES (?,?,?,?,?)")) {
                for (int r = 0; r < medModel.getRowCount(); r++) {
                    ins.setInt(1, apptId); ins.setString(2, medModel.getValueAt(r, 0).toString());
                    ins.setString(3, medModel.getValueAt(r, 1).toString());
                    ins.setString(4, medModel.getValueAt(r, 2).toString());
                    ins.setString(5, notesArea.getText().trim()); ins.executeUpdate();
                }
            }
            UITheme.showSuccess(this, "Prescription saved for Appt #" + apptId + "!");
        } catch (SQLException ex) { UITheme.showError(this, "Save failed: " + ex.getMessage()); }
    }

    private void ensureTable(Connection conn) throws SQLException {
        conn.createStatement().execute("CREATE TABLE IF NOT EXISTS prescriptions (" +
            "id INT AUTO_INCREMENT PRIMARY KEY, appointment_id INT NOT NULL, " +
            "medicine VARCHAR(150) NOT NULL, dosage VARCHAR(100) NOT NULL, " +
            "instructions VARCHAR(255), notes TEXT, created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY (appointment_id) REFERENCES appointments(id) ON DELETE CASCADE)");
    }
}
