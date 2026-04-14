package id.ac.utb.pbo2.ui;

import id.ac.utb.pbo2.model.PenggunaSaatIni;
import id.ac.utb.pbo2.service.LayananOtentikasi;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Arrays;

public class LoginFrame extends JFrame {
    private final LayananOtentikasi LayananOtentikasi = new LayananOtentikasi();
    private final JTextField usernameField = Theme.textField(18);
    private final JPasswordField passwordField = Theme.passwordField(18);
    private final JLabel statusLabel = Theme.muted("Memuat database...");
    private final JButton loginButton = Theme.primaryButton("Masuk");

    public LoginFrame() {
        setTitle("Login SIAKAD PBO2");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(430, 430));

        JPanel root = Theme.page();
        root.setLayout(new GridBagLayout());
        setContentPane(root);

        JPanel card = Theme.surface();
        card.setLayout(new BorderLayout(0, 16));
        card.setPreferredSize(new Dimension(370, 330));

        JPanel header = new JPanel(new BorderLayout(0, 8));
        header.setOpaque(false);
        header.add(Theme.title("SIAKAD PBO2"), BorderLayout.NORTH);
        header.add(Theme.muted("Masuk sesuai role untuk membuka menu yang diizinkan."), BorderLayout.CENTER);
        card.add(header, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        gbc.gridy = 0;
        form.add(Theme.sectionTitle("Username"), gbc);
        gbc.gridy++;
        form.add(usernameField, gbc);
        gbc.gridy++;
        form.add(Theme.sectionTitle("Password"), gbc);
        gbc.gridy++;
        form.add(passwordField, gbc);
        gbc.gridy++;
        form.add(statusLabel, gbc);
        card.add(form, BorderLayout.CENTER);

        loginButton.addActionListener(event -> login());
        passwordField.addActionListener(event -> login());
        loginButton.setEnabled(false);

        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.add(loginButton, BorderLayout.CENTER);
        card.add(footer, BorderLayout.SOUTH);

        root.add(card);
        pack();
        setLocationRelativeTo(null);
    }

    public void setReady() {
        statusLabel.setText("Default: admin/admin123 atau operator/operator123");
        loginButton.setEnabled(true);
    }

    public void setStatus(String text) {
        statusLabel.setText(text);
    }

    private void login() {
        String username = usernameField.getText();
        char[] password = passwordField.getPassword();
        try {
            if (username.isBlank() || password.length == 0) {
                statusLabel.setText("Username dan password wajib diisi.");
                return;
            }
            PenggunaSaatIni user = LayananOtentikasi.login(username, password);
            if (user == null) {
                statusLabel.setText("Login gagal. Username atau password salah.");
                return;
            }
            dispose();
            SwingUtilities.invokeLater(() -> new MainFrame(user).setVisible(true));
        } catch (Exception ex) {
            Theme.error(this, ex);
        } finally {
            Arrays.fill(password, '\0');
        }
    }
}
