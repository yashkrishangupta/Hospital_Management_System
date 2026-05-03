package util;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

/**
 * UITheme — Hospital Management System
 * Theme: Classic Hospital (White + Blue + Green) — Polished
 */
public class UITheme {

    // ── Palette ───────────────────────────────────────────────────────
    public static final Color BG_PAGE      = new Color(236, 245, 255);  // soft blue-white bg
    public static final Color BG_CARD      = new Color(255, 255, 255);  // white cards
    public static final Color BG_INPUT     = new Color(250, 253, 255);  // near-white inputs

    public static final Color ACCENT       = new Color(  0, 102, 204);  // hospital blue #0066CC
    public static final Color ACCENT_HOVER = new Color(  0,  76, 153);  // deeper blue hover
    public static final Color ACCENT_DANGER= new Color(204,   0,   0);  // red #CC0000
    public static final Color ACCENT_WARN  = new Color(230, 130,   0);  // amber

    public static final Color TEXT_PRIMARY = new Color( 28,  28,  30);  // near-black (warmer)
    public static final Color TEXT_MUTED   = new Color( 95, 110, 130);  // blue-grey muted

    public static final Color BORDER_COLOR = new Color(190, 215, 240);  // soft blue border

    public static final Color STAT_GREEN   = new Color(  0, 150,  90);  // healthy green
    public static final Color STAT_BLUE    = ACCENT;
    public static final Color STAT_ORANGE  = new Color(220, 120,   0);  // warm orange

    public static final Color SOFT_BLUE    = new Color(220, 235, 255);  // light blue tint
    public static final Color BLUE_DARK    = new Color(  0,  60, 130);  // dark blue headings

    // ── Fonts — Segoe UI Emoji for emoji support ──────────────────────
    public static final Font FONT_TITLE  = new Font("Segoe UI Emoji", Font.BOLD,  22);
    public static final Font FONT_HEADER = new Font("Segoe UI Emoji", Font.BOLD,  15);
    public static final Font FONT_BODY   = new Font("Segoe UI Emoji", Font.PLAIN, 13);
    public static final Font FONT_SMALL  = new Font("Segoe UI Emoji", Font.PLAIN, 11);
    public static final Font FONT_LABEL  = new Font("Segoe UI Emoji", Font.BOLD,  12);
    public static final Font FONT_BTN    = new Font("Segoe UI Emoji", Font.BOLD,  13);
    public static final Font FONT_STAT   = new Font("Segoe UI Emoji", Font.BOLD,  32);

    // ── Frame ─────────────────────────────────────────────────────────
    public static JFrame createFrame(String title, int w, int h) {
        JFrame frame = new JFrame(title);
        frame.setSize(w, h);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().setBackground(BG_PAGE);
        frame.setLayout(null);
        return frame;
    }

    // ── Card Panel ────────────────────────────────────────────────────
    public static JPanel createCard(int x, int y, int w, int h) {
        JPanel p = new JPanel(null);
        p.setBounds(x, y, w, h);
        p.setBackground(BG_CARD);
        p.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        return p;
    }

    // ── Labels ────────────────────────────────────────────────────────
    public static JLabel createLabel(String text, Font font, Color color,
                                     int x, int y, int w, int h) {
        JLabel l = new JLabel(text);
        l.setFont(font);
        l.setForeground(color);
        l.setBounds(x, y, w, h);
        return l;
    }

    public static JLabel createTitleLabel(String text, int x, int y, int w, int h) {
        return createLabel(text, FONT_TITLE, BLUE_DARK, x, y, w, h);
    }

    // ── TextField ─────────────────────────────────────────────────────
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

    // ── PasswordField ─────────────────────────────────────────────────
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

    // ── ComboBox ──────────────────────────────────────────────────────
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
                setForeground(isSelected ? Color.WHITE : TEXT_PRIMARY);
                setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
                return this;
            }
        });
        return cb;
    }

    // ── Buttons ───────────────────────────────────────────────────────
    public static JButton createAccentButton(String text, int x, int y, int w, int h) {
        JButton b = new JButton(text);
        b.setBounds(x, y, w, h);
        b.setBackground(ACCENT);
        b.setForeground(Color.WHITE);
        b.setFont(FONT_BTN);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { b.setBackground(ACCENT_HOVER); }
            public void mouseExited (java.awt.event.MouseEvent e) { b.setBackground(ACCENT); }
        });
        return b;
    }

    public static JButton createSecondaryButton(String text, int x, int y, int w, int h) {
        JButton b = new JButton(text);
        b.setBounds(x, y, w, h);
        b.setBackground(Color.WHITE);
        b.setForeground(ACCENT);
        b.setFont(FONT_BTN);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createLineBorder(ACCENT, 1));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { b.setBackground(BG_PAGE); }
            public void mouseExited (java.awt.event.MouseEvent e) { b.setBackground(Color.WHITE); }
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
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { b.setBackground(ACCENT_DANGER.darker()); }
            public void mouseExited (java.awt.event.MouseEvent e) { b.setBackground(ACCENT_DANGER); }
        });
        return b;
    }

    // ── Table ─────────────────────────────────────────────────────────
    public static void styleTable(JTable table) {
        table.setBackground(Color.WHITE);
        table.setForeground(TEXT_PRIMARY);
        table.setFont(FONT_BODY);
        table.setRowHeight(32);
        table.setGridColor(BORDER_COLOR);
        table.setSelectionBackground(
            new Color(ACCENT.getRed(), ACCENT.getGreen(), ACCENT.getBlue(), 40));
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));

        JTableHeader header = table.getTableHeader();
        header.setBackground(ACCENT);
        header.setForeground(Color.WHITE);
        header.setFont(FONT_LABEL);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, ACCENT_HOVER));
        header.setReorderingAllowed(false);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        center.setBackground(Color.WHITE);
        center.setForeground(TEXT_PRIMARY);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(center);
        }
    }

    // ── ScrollPane ────────────────────────────────────────────────────
    public static JScrollPane createScrollPane(JTable table, int x, int y, int w, int h) {
        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(x, y, w, h);
        sp.setBackground(Color.WHITE);
        sp.getViewport().setBackground(Color.WHITE);
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

    // ── Alerts ────────────────────────────────────────────────────────
    public static void showSuccess(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showError(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void showWarning(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Warning", JOptionPane.WARNING_MESSAGE);
    }
}