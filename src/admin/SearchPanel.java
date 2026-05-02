package admin;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import util.UITheme;
import util.DBConnection;

public class SearchPanel extends JFrame {

    private JTextField searchField;
    private JComboBox<String> typeBox;
    private JTable table;
    private DefaultTableModel model;

    public SearchPanel() {
        setTitle("Search — Patients & Doctors");
        setSize(1024, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(UITheme.BG_DARK);
        setLayout(null);
        buildUI();
        setVisible(true);
    }

    private void buildUI() {
        JPanel header = UITheme.createCard(0, 0, 1024, 72);
        header.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, UITheme.STAT_ORANGE));
        add(header);
        header.add(UITheme.createLabel("🔍  Search Panel",
                UITheme.FONT_HEADER, UITheme.TEXT_PRIMARY, 26, 14, 500, 26));
        header.add(UITheme.createLabel("Search patients by name/username or doctors by name/specialization",
                UITheme.FONT_SMALL, UITheme.TEXT_MUTED, 26, 42, 600, 20));

        JPanel controls = UITheme.createCard(20, 88, 984, 58);
        add(controls);

        typeBox = UITheme.createComboBox(12, 10, 160, 38);
        typeBox.addItem("Patients"); typeBox.addItem("Doctors");
        typeBox.addActionListener(e -> clearResults());
        controls.add(typeBox);

        searchField = UITheme.createField(182, 10, 620, 38);
        searchField.addActionListener(e -> doSearch());
        controls.add(searchField);

        JButton searchBtn = UITheme.createAccentButton("Search", 812, 10, 160, 38);
        searchBtn.addActionListener(e -> doSearch());
        controls.add(searchBtn);

        model = new DefaultTableModel(new String[]{"ID", "Name", "Info", "Extra"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        UITheme.styleTable(table);

        JScrollPane sp = UITheme.createScrollPane(table, 20, 158, 984, 460);
        add(sp);

        JButton closeBtn = UITheme.createSecondaryButton("Close", 884, 630, 100, 28);
        closeBtn.addActionListener(e -> dispose());
        add(closeBtn);
    }

    private void doSearch() {
        String keyword = searchField.getText().trim();
        String type = (String) typeBox.getSelectedItem();
        model.setRowCount(0);
        if (keyword.isEmpty()) { UITheme.showWarning(this, "Enter a search keyword."); return; }
        String like = "%" + keyword + "%";
        try (Connection conn = DBConnection.getConnection()) {
            if ("Patients".equals(type)) {
                model.setColumnIdentifiers(new String[]{"ID", "Name", "Age / Gender", "Contact"});
                PreparedStatement ps = conn.prepareStatement(
                    "SELECT id, name, age, gender, contact FROM patients WHERE name LIKE ? OR username LIKE ?");
                ps.setString(1, like); ps.setString(2, like);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) model.addRow(new Object[]{rs.getInt("id"), rs.getString("name"),
                    rs.getInt("age") + " / " + rs.getString("gender"), rs.getString("contact")});
            } else {
                model.setColumnIdentifiers(new String[]{"ID", "Doctor Name", "Specialization", ""});
                PreparedStatement ps = conn.prepareStatement(
                    "SELECT id, name, specialization FROM doctors WHERE name LIKE ? OR specialization LIKE ?");
                ps.setString(1, like); ps.setString(2, like);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) model.addRow(new Object[]{rs.getInt("id"),
                    "Dr. " + rs.getString("name"), rs.getString("specialization"), ""});
            }
            if (model.getRowCount() == 0) UITheme.showWarning(this, "No results found for: " + keyword);
        } catch (SQLException ex) { UITheme.showError(this, "Search error: " + ex.getMessage()); }
    }

    private void clearResults() { model.setRowCount(0); searchField.setText(""); }
}
