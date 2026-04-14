package id.ac.utb.pbo2;

import id.ac.utb.pbo2.db.DatabaseBootstrap;
import id.ac.utb.pbo2.ui.LoginFrame;
import id.ac.utb.pbo2.ui.Theme;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Aplikasi {
    public static void main(String[] args) {
        configureRendering();
        Theme.apply();

        new Thread(() -> {
            try {
                DatabaseBootstrap.ensureDatabase();
                SwingUtilities.invokeLater(() -> {
                    LoginFrame frame = new LoginFrame();
                    frame.setReady();
                    frame.setVisible(true);
                });
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(
                        null,
                        "Koneksi database gagal.\n\nPastikan MySQL XAMPP sudah Start.\nDetail: " + ex.getMessage(),
                        "Database Tidak Tersambung",
                        JOptionPane.ERROR_MESSAGE
                ));
            }
        }, "DatabaseBootstrap").start();
    }

    private static void configureRendering() {
        System.setProperty("sun.java2d.d3d", "false");
        System.setProperty("sun.java2d.opengl", "false");
        System.setProperty("sun.java2d.noddraw", "true");
    }
}
