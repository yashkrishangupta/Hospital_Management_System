package admin;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.*;
import util.UITheme;
import util.DBConnection;

public class PrescriptionWriter extends JFrame {

    private JComboBox<String> apptBox;
    private java.util.List<Integer> apptIds = new ArrayList<>();
    private DefaultTableModel medicineModel;
    private JTable medicineTable;
    private JTextArea notesArea;
    private JTextField medName, medDose, medInstructions;

    public PrescriptionWriter() {
        setTitle("Prescription Writer");
        setSize(1024, 700);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(UITheme.BG_DARK);
        setLayout(null);
        buildUI();
        loadAppointments();
        setVisible(true);
    }

    private void buildUI() {
        Color PURPLE = new Color(180, 120, 255);

        JPanel header = UITheme.createCard(0, 0, 1024, 72);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, PURPLE));
        add(header);
        header.add(UITheme.createLabel("💊  Prescription Writer",
                UITheme.FONT_HEADER, UITheme.TEXT_PRIMARY, 26, 14, 500, 26));
        header.add(UITheme.createLabel("Select an appointment, add medicines and save the prescription",
                UITheme.FONT_SMALL, UITheme.TEXT_MUTED, 26, 42, 600, 20));
        JButton closeBtn = UITheme.createDangerButton("Close", 918, 18, 88, 36);
        closeBtn.addActionListener(e -> dispose());
        header.add(closeBtn);

        // Appointment selector
        JPanel apptCard = UITheme.createCard(20, 88, 984, 58);
        add(apptCard);
        apptCard.add(UITheme.createLabel("Appointment:", UITheme.FONT_LABEL, UITheme.TEXT_MUTED, 14, 18, 120, 22));
        apptBox = UITheme.createComboBox(140, 10, 680, 38);
        apptBox.addActionListener(e -> loadExisting());
        apptCard.add(apptBox);
        JButton refreshBtn = UITheme.createSecondaryButton("↻", 828, 10, 44, 38);
        refreshBtn.addActionListener(e -> loadAppointments());
        apptCard.add(refreshBtn);
        JButton loadBtn = UITheme.createAccentButton("Load", 880, 10, 94, 38);
        loadBtn.addActionListener(e -> loadExisting());
        apptCard.add(loadBtn);

        // Medicine table
        add(UITheme.createLabel("💊  Medicines", UITheme.FONT_HEADER, UITheme.TEXT_MUTED, 22, 160, 220, 26));
        String[] cols = {"Medicine Name", "Dosage", "Instructions"};
        medicineModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return true; }
        };
        medicineTable = new JTable(medicineModel);
        UITheme.styleTable(medicineTable);
        medicineTable.setRowHeight(32);
        medicineTable.getColumnModel().getColumn(0).setPreferredWidth(260);
        medicineTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        medicineTable.getColumnModel().getColumn(2).setPreferredWidth(500);

        JScrollPane sp = UITheme.createScrollPane(medicineTable, 20, 190, 984, 190);
        add(sp);

        // Right-click delete
        JPopupMenu popup = new JPopupMenu();
        JMenuItem delItem = new JMenuItem("Remove selected row");
        delItem.setBackground(UITheme.BG_INPUT); delItem.setForeground(UITheme.ACCENT_DANGER);
        delItem.addActionListener(e -> { int r = medicineTable.getSelectedRow(); if (r >= 0) medicineModel.removeRow(r); });
        popup.add(delItem);
        medicineTable.setComponentPopupMenu(popup);

        // Add-row panel
        JPanel addRow = UITheme.createCard(20, 388, 984, 58);
        add(addRow);
        medName = makeHint(12, 10, 240, 38, "Medicine name", addRow);
        medDose = makeHint(262, 10, 190, 38, "Dosage (e.g. 500mg)", addRow);
        medInstructions = makeHint(462, 10, 360, 38, "Instructions (e.g. After meals)", addRow);
        JButton addMed = UITheme.createAccentButton("+ Add Row", 832, 10, 140, 38);
        addMed.addActionListener(e -> addRow());
        addRow.add(addMed);

        // Notes
        add(UITheme.createLabel("📝  Doctor Notes / Diagnosis", UITheme.FONT_HEADER, UITheme.TEXT_MUTED, 22, 460, 400, 26));
        notesArea = new JTextArea();
        notesArea.setBackground(UITheme.BG_INPUT); notesArea.setForeground(UITheme.TEXT_PRIMARY);
        notesArea.setCaretColor(UITheme.ACCENT); notesArea.setFont(UITheme.FONT_BODY);
        notesArea.setLineWrap(true); notesArea.setWrapStyleWord(true);
        notesArea.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        JScrollPane notesSp = new JScrollPane(notesArea);
        notesSp.setBounds(20, 490, 984, 110);
        notesSp.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR));
        notesSp.getViewport().setBackground(UITheme.BG_INPUT);
        add(notesSp);

        JButton saveBtn = UITheme.createAccentButton("💾  Save Prescription", 20, 618, 200, 30);
        saveBtn.addActionListener(e -> save());
        add(saveBtn);
        JButton clearBtn = UITheme.createSecondaryButton("Clear All", 240, 618, 110, 30);
        clearBtn.addActionListener(e -> { medicineModel.setRowCount(0); notesArea.setText(""); });
        add(clearBtn);
        add(UITheme.createLabel("💡 Right-click a medicine row to delete it",
                UITheme.FONT_SMALL, UITheme.TEXT_MUTED, 430, 632, 400, 20));
    }

    private JTextField makeHint(int x, int y, int w, int h, String hint, JPanel parent) {
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

    private void loadAppointments() {
        apptIds.clear(); apptBox.removeAllItems();
        String sql = "SELECT a.id, p.name, d.name AS doc, a.date FROM appointments a " +
                     "JOIN patients p ON a.patient_id=p.id JOIN doctors d ON a.doctor_id=d.id ORDER BY a.date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                apptIds.add(rs.getInt("id"));
                apptBox.addItem(String.format("Appt #%d  |  %s  →  Dr. %s  |  %s",
                    rs.getInt("id"), rs.getString("name"), rs.getString("doc"), rs.getString("date")));
            }
            if (apptBox.getItemCount() == 0) apptBox.addItem("No appointments found");
        } catch (SQLException ex) { UITheme.showError(this, "Error: " + ex.getMessage()); }
    }

    private void loadExisting() {
        int idx = apptBox.getSelectedIndex();
        if (idx < 0 || idx >= apptIds.size()) return;
        medicineModel.setRowCount(0); notesArea.setText("");
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                "SELECT medicine, dosage, instructions, notes FROM prescriptions WHERE appointment_id=? ORDER BY id")) {
            ps.setInt(1, apptIds.get(idx));
            ResultSet rs = ps.executeQuery(); boolean first = true;
            while (rs.next()) {
                medicineModel.addRow(new Object[]{rs.getString("medicine"), rs.getString("dosage"), rs.getString("instructions")});
                if (first) { notesArea.setText(rs.getString("notes") != null ? rs.getString("notes") : ""); first = false; }
            }
        } catch (SQLException ex) { /* table may not exist yet */ }
    }

    private void addRow() {
        String n = medName.getText().trim(), d = medDose.getText().trim();
        if (n.isEmpty() || n.equals("Medicine name")) { UITheme.showWarning(this, "Enter the medicine name."); return; }
        if (d.isEmpty() || d.equals("Dosage (e.g. 500mg)")) { UITheme.showWarning(this, "Enter the dosage."); return; }
        String i = medInstructions.getText().trim();
        medicineModel.addRow(new Object[]{n, d, i.equals("Instructions (e.g. After meals)") ? "" : i});
        reset(medName, "Medicine name"); reset(medDose, "Dosage (e.g. 500mg)");
        reset(medInstructions, "Instructions (e.g. After meals)"); medName.requestFocus();
    }

    private void reset(JTextField f, String hint) { f.setText(hint); f.setForeground(UITheme.TEXT_MUTED); }

    private void save() {
        int idx = apptBox.getSelectedIndex();
        if (idx < 0 || idx >= apptIds.size()) { UITheme.showWarning(this, "Select an appointment."); return; }
        if (medicineModel.getRowCount() == 0)  { UITheme.showWarning(this, "Add at least one medicine."); return; }
        int apptId = apptIds.get(idx);
        try (Connection conn = DBConnection.getConnection()) {
            conn.createStatement().execute("CREATE TABLE IF NOT EXISTS prescriptions (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, appointment_id INT NOT NULL, " +
                "medicine VARCHAR(150) NOT NULL, dosage VARCHAR(100) NOT NULL, " +
                "instructions VARCHAR(255), notes TEXT, created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (appointment_id) REFERENCES appointments(id) ON DELETE CASCADE)");
            try (PreparedStatement del = conn.prepareStatement("DELETE FROM prescriptions WHERE appointment_id=?")) {
                del.setInt(1, apptId); del.executeUpdate();
            }
            try (PreparedStatement ins = conn.prepareStatement(
                "INSERT INTO prescriptions (appointment_id, medicine, dosage, instructions, notes) VALUES (?,?,?,?,?)")) {
                for (int r = 0; r < medicineModel.getRowCount(); r++) {
                    ins.setInt(1, apptId); ins.setString(2, medicineModel.getValueAt(r, 0).toString());
                    ins.setString(3, medicineModel.getValueAt(r, 1).toString());
                    ins.setString(4, medicineModel.getValueAt(r, 2).toString());
                    ins.setString(5, notesArea.getText().trim()); ins.executeUpdate();
                }
            }
            UITheme.showSuccess(this, "Prescription saved for Appt #" + apptId + "!");
        } catch (SQLException ex) { UITheme.showError(this, "Save failed: " + ex.getMessage()); }
    }
}
