package id.ac.utb.pbo2.ui;

import id.ac.utb.pbo2.model.PenggunaSaatIni;
import id.ac.utb.pbo2.service.LayananOtentikasi;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Arrays;

public class PasswordPanel extends JPanel {
    private final LayananOtentikasi LayananOtentikasi = new LayananOtentikasi();
    private final PenggunaSaatIni user;
    private final JPasswordField oldPassword = Theme.passwordField(18);
    private final JPasswordField newPassword = Theme.passwordField(18);
    private final JPasswordField confirmPassword = Theme.passwordField(18);

    public PasswordPanel(PenggunaSaatIni user) {
        this.user = user;
        setLayout(new BorderLayout(0, 14));
        setBackground(Theme.BACKGROUND);
        setBorder(Theme.page().getBorder());

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(Theme.title("Ganti Password"), BorderLayout.NORTH);
        header.add(Theme.muted("Perubahan password hanya berlaku untuk akun yang sedang login: " + user.getUsername()), BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);
        add(form(), BorderLayout.CENTER);
    }

    private JPanel form() {
        JPanel panel = Theme.surface();
        panel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 0, 7, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.gridx = 0;
        int y = 0;
        y = addField(panel, gbc, y, "Password Lama", oldPassword);
        y = addField(panel, gbc, y, "Password Baru", newPassword);
        y = addField(panel, gbc, y, "Konfirmasi Password Baru", confirmPassword);

        JButton button = Theme.primaryButton("Simpan Password");
        button.addActionListener(event -> changePassword());
        gbc.gridy = y;
        panel.add(button, gbc);
        return panel;
    }

    private int addField(JPanel panel, GridBagConstraints gbc, int y, String label, java.awt.Component component) {
        gbc.gridy = y++;
        panel.add(Theme.sectionTitle(label), gbc);
        gbc.gridy = y++;
        panel.add(component, gbc);
        return y;
    }

    private void changePassword() {
        char[] oldPass = oldPassword.getPassword();
        char[] newPass = newPassword.getPassword();
        char[] confirmPass = confirmPassword.getPassword();
        try {
            if (newPass.length < 5) {
                throw new IllegalArgumentException("Password baru minimal 5 karakter.");
            }
            if (!Arrays.equals(newPass, confirmPass)) {
                throw new IllegalArgumentException("Konfirmasi password baru tidak sama.");
            }
            LayananOtentikasi.changePassword(user.getId(), oldPass, newPass);
            oldPassword.setText("");
            newPassword.setText("");
            confirmPassword.setText("");
            Theme.info(this, "Password berhasil diganti.");
        } catch (Exception ex) {
            Theme.error(this, ex);
        } finally {
            Arrays.fill(oldPass, '\0');
            Arrays.fill(newPass, '\0');
            Arrays.fill(confirmPass, '\0');
        }
    }
}
