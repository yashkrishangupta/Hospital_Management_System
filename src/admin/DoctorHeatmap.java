package admin;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.*;
import java.time.format.TextStyle;
import java.util.*;
import util.UITheme;
import util.DBConnection;

/**
 * Doctor Availability Heatmap
 * ────────────────────────────
 * Shows a monthly calendar grid where each cell is colored by
 * appointment load (cool→warm gradient). Hover a cell to see
 * the exact count + busiest doctor. Navigate months freely.
 */
public class DoctorHeatmap extends JFrame {

    private YearMonth   currentMonth;
    private HeatmapPanel heatPanel;
    private JLabel       monthLabel;
    private JComboBox<String> doctorBox;
    private java.util.List<Integer> doctorIds = new ArrayList<>();

    // date → appointment count for the selected doctor (or all)
    private final Map<LocalDate, Integer> countMap = new HashMap<>();
    private int maxCount = 1;

    public DoctorHeatmap() {
        currentMonth = YearMonth.now();
        setTitle("Doctor Availability Heatmap");
        setSize(1024, 700);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(UITheme.BG_DARK);
        setLayout(null);
        buildUI();
        loadDoctors();
        loadData();
        setVisible(true);
    }

    private void buildUI() {
        // ── Header ────────────────────────────────────────────────────
        JPanel header = UITheme.createCard(0, 0, 1024, 72);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, UITheme.STAT_ORANGE));
        add(header);

        header.add(UITheme.createLabel("📊  Doctor Availability Heatmap",
                UITheme.FONT_HEADER, UITheme.TEXT_PRIMARY, 26, 14, 600, 26));
        header.add(UITheme.createLabel(
                "Warmer colors = more booked. Hover a date to inspect.",
                UITheme.FONT_SMALL, UITheme.TEXT_MUTED, 26, 44, 600, 20));

        JButton closeBtn = UITheme.createDangerButton("Close", 918, 18, 88, 36);
        closeBtn.addActionListener(e -> dispose());
        header.add(closeBtn);

        // ── Controls bar ──────────────────────────────────────────────
        JPanel controls = UITheme.createCard(20, 88, 984, 56);
        add(controls);

        controls.add(UITheme.createLabel("Doctor:", UITheme.FONT_LABEL,
                UITheme.TEXT_MUTED, 12, 16, 70, 24));

        doctorBox = UITheme.createComboBox(82, 10, 480, 36);
        doctorBox.addActionListener(e -> loadData());
        controls.add(doctorBox);

        JButton prev = UITheme.createSecondaryButton("◀ Prev", 580, 10, 100, 36);
        prev.addActionListener(e -> { currentMonth = currentMonth.minusMonths(1); loadData(); });
        controls.add(prev);

        monthLabel = UITheme.createLabel("", UITheme.FONT_HEADER,
                UITheme.ACCENT, 688, 14, 150, 28);
        controls.add(monthLabel);

        JButton next = UITheme.createSecondaryButton("Next ▶", 846, 10, 126, 36);
        next.addActionListener(e -> { currentMonth = currentMonth.plusMonths(1); loadData(); });
        controls.add(next);

        // ── Heatmap panel ─────────────────────────────────────────────
        heatPanel = new HeatmapPanel();
        heatPanel.setBounds(20, 158, 984, 460);
        add(heatPanel);

        // ── Legend ────────────────────────────────────────────────────
        LegendPanel legend = new LegendPanel();
        legend.setBounds(20, 628, 984, 36);
        add(legend);
    }

    private void loadDoctors() {
        doctorIds.clear();
        doctorBox.removeAllItems();
        doctorBox.addItem("All Doctors");
        doctorIds.add(-1);

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT id, name, specialization FROM doctors ORDER BY name");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                doctorIds.add(rs.getInt("id"));
                doctorBox.addItem("Dr. " + rs.getString("name")
                        + "  —  " + rs.getString("specialization"));
            }
        } catch (SQLException ex) {
            UITheme.showError(this, "Error loading doctors: " + ex.getMessage());
        }
    }

    void loadData() {
        countMap.clear();
        maxCount = 1;

        monthLabel.setText(currentMonth.getMonth()
                .getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                + " " + currentMonth.getYear());

        int selIdx   = doctorBox.getSelectedIndex();
        int doctorId = (selIdx >= 0 && selIdx < doctorIds.size()) ? doctorIds.get(selIdx) : -1;

        String sql = "SELECT date, COUNT(*) AS cnt FROM appointments "
                + "WHERE YEAR(date)=? AND MONTH(date)=?"
                + (doctorId > 0 ? " AND doctor_id=?" : "")
                + " GROUP BY date";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, currentMonth.getYear());
            ps.setInt(2, currentMonth.getMonthValue());
            if (doctorId > 0) ps.setInt(3, doctorId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                LocalDate d   = rs.getDate("date").toLocalDate();
                int       cnt = rs.getInt("cnt");
                countMap.put(d, cnt);
                if (cnt > maxCount) maxCount = cnt;
            }
        } catch (SQLException ex) {
            UITheme.showError(this, "Error loading heatmap data: " + ex.getMessage());
        }

        heatPanel.repaint();
    }

    // ── Inner: calendar grid ──────────────────────────────────────────
    private class HeatmapPanel extends JPanel {

        private static final int COLS = 7;
        private LocalDate hoveredDate = null;

        HeatmapPanel() {
            setBackground(UITheme.BG_CARD);
            setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR));
            setLayout(null);

            addMouseMotionListener(new MouseMotionAdapter() {
                @Override public void mouseMoved(MouseEvent e) {
                    hoveredDate = dateAt(e.getX(), e.getY());
                    repaint();
                }
            });
            addMouseListener(new MouseAdapter() {
                @Override public void mouseExited(MouseEvent e) {
                    hoveredDate = null; repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int W = getWidth(), H = getHeight();
            int headerH = 30;
            int cellW   = W / COLS;
            int rows    = 6;
            int cellH   = (H - headerH) / rows;

            // Day-of-week headers
            String[] days = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
            g2.setFont(UITheme.FONT_LABEL);
            for (int c = 0; c < COLS; c++) {
                int x = c * cellW;
                g2.setColor(new Color(0, 40, 70));
                g2.fillRect(x, 0, cellW, headerH);
                g2.setColor(UITheme.ACCENT);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(days[c],
                        x + (cellW - fm.stringWidth(days[c])) / 2,
                        headerH - 8);
            }

            // Calendar cells
            LocalDate first  = currentMonth.atDay(1);
            int startCol     = first.getDayOfWeek().getValue() % 7; // Sun=0
            int daysInMonth  = currentMonth.lengthOfMonth();
            LocalDate today  = LocalDate.now();

            for (int day = 1; day <= daysInMonth; day++) {
                int idx  = startCol + day - 1;
                int row  = idx / COLS;
                int col  = idx % COLS;
                int x    = col * cellW;
                int y    = headerH + row * cellH;

                LocalDate date   = currentMonth.atDay(day);
                int       count  = countMap.getOrDefault(date, 0);
                boolean   hovered = date.equals(hoveredDate);

                // Background fill — heatmap gradient
                Color fill = heatColor(count);
                g2.setColor(fill);
                g2.fillRect(x + 1, y + 1, cellW - 2, cellH - 2);

                // Today highlight border
                if (date.equals(today)) {
                    g2.setColor(UITheme.ACCENT);
                    g2.setStroke(new BasicStroke(2f));
                    g2.drawRect(x + 2, y + 2, cellW - 5, cellH - 5);
                    g2.setStroke(new BasicStroke(1f));
                }

                // Hovered cell overlay
                if (hovered) {
                    g2.setColor(new Color(255, 255, 255, 30));
                    g2.fillRect(x + 1, y + 1, cellW - 2, cellH - 2);
                }

                // Day number
                g2.setFont(UITheme.FONT_LABEL);
                g2.setColor(count > 0 ? Color.WHITE : UITheme.TEXT_MUTED);
                g2.drawString(String.valueOf(day), x + 8, y + 20);

                // Appointment count badge
                if (count > 0) {
                    String badge = count + " appt" + (count > 1 ? "s" : "");
                    g2.setFont(UITheme.FONT_SMALL);
                    g2.setColor(new Color(255, 255, 255, 210));
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(badge,
                            x + (cellW - fm.stringWidth(badge)) / 2,
                            y + cellH - 10);
                }

                // Tooltip popup on hover
                if (hovered && count > 0) {
                    drawTooltip(g2, x, y, cellW, cellH, date, count);
                }

                // Grid line
                g2.setColor(UITheme.BORDER_COLOR);
                g2.drawRect(x, y, cellW, cellH);
            }

            // Empty cells
            g2.setColor(new Color(UITheme.BG_DARK.getRed(),
                    UITheme.BG_DARK.getGreen(), UITheme.BG_DARK.getBlue(), 180));
            // pre-month
            for (int c = 0; c < startCol; c++) {
                g2.fillRect(c * cellW + 1, headerH + 1, cellW - 2, cellH - 2);
            }
        }

        private void drawTooltip(Graphics2D g2, int cellX, int cellY,
                                  int cellW, int cellH,
                                  LocalDate date, int count) {
            String line1 = date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH)
                    + ", " + date.getDayOfMonth() + " "
                    + date.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            String line2 = count + " appointment" + (count > 1 ? "s" : "") + " booked";

            g2.setFont(UITheme.FONT_BODY);
            FontMetrics fm  = g2.getFontMetrics();
            int tw  = Math.max(fm.stringWidth(line1), fm.stringWidth(line2)) + 20;
            int th  = 50;

            int tx = cellX + cellW + 4;
            if (tx + tw > getWidth()) tx = cellX - tw - 4;
            int ty = cellY + cellH / 2 - th / 2;
            if (ty + th > getHeight()) ty = getHeight() - th - 4;

            // Shadow
            g2.setColor(new Color(0, 0, 0, 80));
            g2.fillRoundRect(tx + 3, ty + 3, tw, th, 10, 10);
            // Box
            g2.setColor(new Color(15, 35, 60));
            g2.fillRoundRect(tx, ty, tw, th, 10, 10);
            g2.setColor(UITheme.ACCENT);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(tx, ty, tw, th, 10, 10);
            g2.setStroke(new BasicStroke(1f));
            // Text
            g2.setFont(UITheme.FONT_LABEL);
            g2.setColor(UITheme.TEXT_PRIMARY);
            g2.drawString(line1, tx + 10, ty + 18);
            g2.setFont(UITheme.FONT_SMALL);
            g2.setColor(UITheme.STAT_ORANGE);
            g2.drawString(line2, tx + 10, ty + 36);
        }

        /** Map count → heat color (cool navy → teal → amber → red) */
        private Color heatColor(int count) {
            if (count == 0) return new Color(20, 40, 65);
            float t = Math.min(1f, (float) count / maxCount);
            if (t < 0.33f) {
                // navy → teal
                float u = t / 0.33f;
                return blend(new Color(20, 80, 120), new Color(0, 180, 160), u);
            } else if (t < 0.66f) {
                // teal → amber
                float u = (t - 0.33f) / 0.33f;
                return blend(new Color(0, 180, 160), new Color(255, 160, 30), u);
            } else {
                // amber → red
                float u = (t - 0.66f) / 0.34f;
                return blend(new Color(255, 160, 30), new Color(230, 50, 60), u);
            }
        }

        private Color blend(Color a, Color b, float t) {
            return new Color(
                (int)(a.getRed()   + t * (b.getRed()   - a.getRed())),
                (int)(a.getGreen() + t * (b.getGreen() - a.getGreen())),
                (int)(a.getBlue()  + t * (b.getBlue()  - a.getBlue()))
            );
        }

        /** Return which LocalDate the pixel (px,py) falls on, or null. */
        private LocalDate dateAt(int px, int py) {
            int W       = getWidth();
            int headerH = 30;
            int cellW   = W / COLS;
            int rows    = 6;
            int cellH   = (getHeight() - headerH) / rows;
            if (py < headerH) return null;

            int col = px / cellW;
            int row = (py - headerH) / cellH;
            int idx = row * COLS + col;

            LocalDate first    = currentMonth.atDay(1);
            int       startCol = first.getDayOfWeek().getValue() % 7;
            int       day      = idx - startCol + 1;
            if (day < 1 || day > currentMonth.lengthOfMonth()) return null;
            return currentMonth.atDay(day);
        }
    }

    // ── Inner: color legend bar ───────────────────────────────────────
    private static class LegendPanel extends JPanel {
        LegendPanel() { setBackground(UITheme.BG_DARK); }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int barW = 260, barH = 14, barX = (getWidth() - barW) / 2, barY = 6;

            // Gradient bar
            GradientPaint gp = new GradientPaint(
                    barX, 0, new Color(20, 80, 120),
                    barX + barW, 0, new Color(230, 50, 60));
            g2.setPaint(gp);
            g2.fillRoundRect(barX, barY, barW, barH, 6, 6);

            g2.setFont(UITheme.FONT_SMALL);
            g2.setColor(UITheme.TEXT_MUTED);
            g2.drawString("Available", barX - 58, barY + 11);
            g2.drawString("Very Busy",  barX + barW + 5, barY + 11);
        }
    }
}
