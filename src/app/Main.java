package app;

import javax.swing.*;

/**
 * Main entry point for the Hospital Management System.
 * Sets system look-and-feel and launches the Login screen.
 */
public class Main {
    public static void main(String[] args) {
        // Use system L&F as base (we override colors in UITheme)
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            // Remove default button focus rectangle
            UIManager.put("Button.focus", new java.awt.Color(0, 0, 0, 0));
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(Login::new);
    }
}
