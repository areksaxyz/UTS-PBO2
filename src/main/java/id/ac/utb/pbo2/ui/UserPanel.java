package id.ac.utb.pbo2.ui;

import id.ac.utb.pbo2.service.LayananAkademik;
import id.ac.utb.pbo2.service.LayananOtentikasi;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTable;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Arrays;

public class UserPanel extends JPanel {
    private final LayananOtentikasi LayananOtentikasi = new LayananOtentikasi();
    private final LayananAkademik LayananAkademik = new LayananAkademik();
    private final JTable table = Theme.table();
    private final JTextField usernameField = Theme.textField(14);
    private final JTextField namaField = Theme.textField(18);
    private final JPasswordField passwordField = Theme.passwordField(14);
    private final JComboBox<String> roleBox = Theme.comboBox(new String[]{"ADMIN", "OPERATOR"});

    public UserPanel() {
        setLayout(new BorderLayout(0, 14));
        setBackground(Theme.BACKGROUND);
        setBorder(Theme.page().getBorder());

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(Theme.title("Setting User"), BorderLayout.NORTH);
        header.add(Theme.muted("Admin hanya dapat menambah user baru. Perubahan password dilakukan oleh akun masing-masing."), BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(14, 0));
        body.setOpaque(false);
        body.add(form(), BorderLayout.WEST);
        body.add(Theme.tableScroll(table), BorderLayout.CENTER);
        add(body, BorderLayout.CENTER);
        loadUsers();
    }

    private JPanel form() {
        JPanel panel = Theme.surface();
        panel.setLayout(new GridBagLayout());
        panel.setPreferredSize(new java.awt.Dimension(320, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        int y = 0;
        y = addField(panel, gbc, y, "Username", usernameField);
        y = addField(panel, gbc, y, "Nama Lengkap", namaField);
        y = addField(panel, gbc, y, "Role", roleBox);
        y = addField(panel, gbc, y, "Password Awal", passwordField);

        JButton button = Theme.primaryButton("Tambah User");
        button.addActionListener(event -> createUser());
        gbc.gridy = y++;
        panel.add(button, gbc);

        JPanel note = new JPanel(new GridLayout(0, 1, 0, 4));
        note.setOpaque(false);
        note.add(Theme.muted("Username tidak boleh duplikat."));
        note.add(Theme.muted("Operator tidak mendapat akses master/setting."));
        gbc.gridy = y;
        panel.add(note, gbc);
        return panel;
    }

    private int addField(JPanel panel, GridBagConstraints gbc, int y, String label, java.awt.Component component) {
        gbc.gridy = y++;
        panel.add(Theme.sectionTitle(label), gbc);
        gbc.gridy = y++;
        panel.add(component, gbc);
        return y;
    }

    private void createUser() {
        char[] password = passwordField.getPassword();
        try {
            String username = usernameField.getText().trim();
            String nama = namaField.getText().trim();
            if (username.isBlank() || nama.isBlank()) {
                throw new IllegalArgumentException("Username dan nama wajib diisi.");
            }
            if (password.length < 5) {
                throw new IllegalArgumentException("Password minimal 5 karakter.");
            }
            LayananOtentikasi.createUser(username, password, String.valueOf(roleBox.getSelectedItem()), nama);
            Theme.info(this, "User berhasil ditambahkan.");
            passwordField.setText("");
            loadUsers();
        } catch (SQLIntegrityConstraintViolationException ex) {
            Theme.error(this, new IllegalArgumentException("Username sudah digunakan."));
        } catch (Exception ex) {
            Theme.error(this, ex);
        } finally {
            Arrays.fill(password, '\0');
        }
    }

    private void loadUsers() {
        try {
            table.setModel(LayananAkademik.table("""
                    SELECT id AS ID, username AS Username, nama_lengkap AS Nama, role AS Role, created_at AS Dibuat
                    FROM users
                    ORDER BY id
                    """));
        } catch (Exception ex) {
            Theme.error(this, ex);
        }
    }
}
