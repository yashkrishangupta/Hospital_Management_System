package doctor;

import admin.DoctorHeatmap;
import javax.swing.*;
import java.time.YearMonth;

/**
 * Lightweight wrapper — reuses DoctorHeatmap but pre-filters
 * to show only THIS doctor's schedule.
 */
public class DoctorScheduleChart extends JFrame {

    public DoctorScheduleChart(int doctorId, String doctorName) {
        // Open the shared heatmap and pre-select this doctor
        DoctorHeatmap heatmap = new DoctorHeatmap();
        heatmap.setTitle("My Schedule — Dr. " + doctorName);

        // Pre-select the doctor in the combo (index 0 = All, index N = doctor)
        SwingUtilities.invokeLater(() -> {
            javax.swing.JComboBox<?> box = findComboBox(heatmap);
            if (box != null) {
                for (int i = 0; i < box.getItemCount(); i++) {
                    String item = box.getItemAt(i).toString();
                    if (item.contains(doctorName)) {
                        box.setSelectedIndex(i);
                        break;
                    }
                }
            }
        });
        // We are just a launcher — no frame needed
        dispose();
    }

    @SuppressWarnings("unchecked")
    private javax.swing.JComboBox<?> findComboBox(java.awt.Container c) {
        for (java.awt.Component comp : c.getComponents()) {
            if (comp instanceof javax.swing.JComboBox) return (javax.swing.JComboBox<?>) comp;
            if (comp instanceof java.awt.Container) {
                javax.swing.JComboBox<?> found = findComboBox((java.awt.Container) comp);
                if (found != null) return found;
            }
        }
        return null;
    }
}
