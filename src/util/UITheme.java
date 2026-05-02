package util;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

/**
 * UITheme — centralized styling for a clean hospital management UI.
 * Deep navy + white + teal accent palette. Rounded cards. Modern feel.
 */
public class UITheme {

    // ── Palette ──────────────────────────────────────────────────────
    public static final Color BG_DARK       = new Color(10,  25,  47);   // deep navy bg
    public static final Color BG_CARD       = new Color(17,  40,  72);   // card surface
    public static final Color BG_INPUT      = new Color(22,  50,  90);   // input field bg
    public static final Color ACCENT        = new Color(0,  210, 190);   // teal accent
    public static final Color ACCENT_HOVER  = new Color(0,  180, 165);
    public static final Color ACCENT_DANGER = new Color(255, 80,  90);
    public static final Color ACCENT_WARN   = new Color(255, 165,  0);
    public static final Color TEXT_PRIMARY  = new Color(220, 235, 255);
    public static final Color TEXT_MUTED    = new Color(130, 155, 190);
    public static final Color BORDER_COLOR  = new Color(30,  65, 110);
    public static final Color STAT_GREEN    = new Color(0,  220, 130);
    public static final Color STAT_BLUE     = new Color(80, 160, 255);
    public static final Color STAT_ORANGE   = new Color(255, 140,  60);

    // ── Fonts ─────────────────────────────────────────────────────────
    public static final Font FONT_TITLE  = new Font("Segoe UI Emoji", Font.BOLD,  22);
    public static final Font FONT_HEADER = new Font("Segoe UI Emoji", Font.BOLD,  15);
    public static final Font FONT_BODY   = new Font("Segoe UI Emoji", Font.PLAIN, 13);
    public static final Font FONT_SMALL  = new Font("Segoe UI Emoji", Font.PLAIN, 11);
    public static final Font FONT_LABEL  = new Font("Segoe UI Emoji", Font.BOLD,  12);
    public static final Font FONT_BTN    = new Font("Segoe UI Emoji", Font.BOLD,  13);
    public static final Font FONT_STAT   = new Font("Segoe UI Emoji", Font.BOLD,  32);

    // ── Frame helper ──────────────────────────────────────────────────
    public static JFrame createFrame(String title, int w, int h) {
        JFrame frame = new JFrame(title);
        frame.setSize(w, h);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().setBackground(BG_DARK);
        frame.setLayout(null);
        return frame;
    }

    // ── Panel helpers ─────────────────────────────────────────────────
    public static JPanel createCard(int x, int y, int w, int h) {
        JPanel p = new JPanel(null);
        p.setBounds(x, y, w, h);
        p.setBackground(BG_CARD);
        p.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        return p;
    }

    // ── Label helpers ─────────────────────────────────────────────────
    public static JLabel createLabel(String text, Font font, Color color,
                                     int x, int y, int w, int h) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI Emoji", font.getStyle(), font.getSize()));
        l.setForeground(color);
        l.setBounds(x, y, w, h);
        return l;
    }

    public static JLabel createTitleLabel(String text, int x, int y, int w, int h) {
        return createLabel(text, FONT_TITLE, TEXT_PRIMARY, x, y, w, h);
    }

    // ── TextField helper ──────────────────────────────────────────────
    public static JTextField createField(int x, int y, int w, int h) {
        JTextField f = new JTextField();
        f.setBounds(x, y, w, h);
        f.setBackground(BG_INPUT);
        f.setForeground(TEXT_PRIMARY);
        f.setCaretColor(ACCENT);
        f.setFont(FONT_BODY);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        return f;
    }

    // ── PasswordField helper ──────────────────────────────────────────
    public static JPasswordField createPasswordField(int x, int y, int w, int h) {
        JPasswordField f = new JPasswordField();
        f.setBounds(x, y, w, h);
        f.setBackground(BG_INPUT);
        f.setForeground(TEXT_PRIMARY);
        f.setCaretColor(ACCENT);
        f.setFont(FONT_BODY);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        return f;
    }

    // ── ComboBox helper ───────────────────────────────────────────────
    @SuppressWarnings("unchecked")
    public static <T> JComboBox<T> createComboBox(int x, int y, int w, int h) {
        JComboBox<T> cb = new JComboBox<>();
        cb.setBounds(x, y, w, h);
        cb.setBackground(BG_INPUT);
        cb.setForeground(TEXT_PRIMARY);
        cb.setFont(FONT_BODY);
        cb.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        cb.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBackground(isSelected ? ACCENT : BG_INPUT);
                setForeground(isSelected ? BG_DARK : TEXT_PRIMARY);
                setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
                return this;
            }
        });
        return cb;
    }

    // ── Button helpers ────────────────────────────────────────────────
    public static JButton createAccentButton(String text, int x, int y, int w, int h) {
        JButton b = new JButton(text);
        b.setBounds(x, y, w, h);
        b.setBackground(ACCENT);
        b.setForeground(BG_DARK);
        b.setFont(FONT_BTN);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { b.setBackground(ACCENT_HOVER); }
            public void mouseExited(java.awt.event.MouseEvent e)  { b.setBackground(ACCENT); }
        });
        return b;
    }

    public static JButton createSecondaryButton(String text, int x, int y, int w, int h) {
        JButton b = new JButton(text);
        b.setBounds(x, y, w, h);
        b.setBackground(BG_INPUT);
        b.setForeground(TEXT_PRIMARY);
        b.setFont(FONT_BTN);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { b.setBackground(BORDER_COLOR); }
            public void mouseExited(java.awt.event.MouseEvent e)  { b.setBackground(BG_INPUT); }
        });
        return b;
    }

    public static JButton createDangerButton(String text, int x, int y, int w, int h) {
        JButton b = new JButton(text);
        b.setBounds(x, y, w, h);
        b.setBackground(ACCENT_DANGER);
        b.setForeground(Color.WHITE);
        b.setFont(FONT_BTN);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    // ── Table helper ──────────────────────────────────────────────────
    public static void styleTable(JTable table) {
        table.setBackground(BG_CARD);
        table.setForeground(TEXT_PRIMARY);
        table.setFont(FONT_BODY);
        table.setRowHeight(32);
        table.setGridColor(BORDER_COLOR);
        table.setSelectionBackground(new Color(0, 210, 190, 60));
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(0, 40, 70));
        header.setForeground(ACCENT);
        header.setFont(FONT_LABEL);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, ACCENT));
        header.setReorderingAllowed(false);

        // Center-align all cells
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        center.setBackground(BG_CARD);
        center.setForeground(TEXT_PRIMARY);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(center);
        }
    }

    // ── ScrollPane helper ─────────────────────────────────────────────
    public static JScrollPane createScrollPane(JTable table, int x, int y, int w, int h) {
        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(x, y, w, h);
        sp.setBackground(BG_CARD);
        sp.getViewport().setBackground(BG_CARD);
        sp.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        return sp;
    }

    // ── Separator ─────────────────────────────────────────────────────
    public static JSeparator createSeparator(int x, int y, int w) {
        JSeparator sep = new JSeparator();
        sep.setBounds(x, y, w, 1);
        sep.setForeground(BORDER_COLOR);
        sep.setBackground(BORDER_COLOR);
        return sep;
    }

    // ── JOptionPane styling ───────────────────────────────────────────
    public static void showSuccess(java.awt.Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "✔  Success", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showError(java.awt.Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "✖  Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void showWarning(java.awt.Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "⚠  Warning", JOptionPane.WARNING_MESSAGE);
    }
}
