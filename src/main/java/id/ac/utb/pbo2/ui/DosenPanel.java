package id.ac.utb.pbo2.ui;

import id.ac.utb.pbo2.service.LayananAkademik;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.GridLayout;

public class DosenPanel extends JPanel {
    private final LayananAkademik service = new LayananAkademik();
    private final JTable table = Theme.table();
    private final JTextField kodeField = Theme.textField(10);

    public DosenPanel() {
        setLayout(new BorderLayout(0, 14));
        setBackground(Theme.BACKGROUND);
        setBorder(Theme.page().getBorder());
        add(header(), BorderLayout.NORTH);
        JPanel center = new JPanel(new BorderLayout(0, 8));
        center.setOpaque(false);
        center.add(Theme.tableScroll(table), BorderLayout.CENTER);
        JButton detailButton = Theme.secondaryButton("Lihat Data Lengkap");
        detailButton.addActionListener(event -> showSelectedDetail());
        center.add(detailButton, BorderLayout.SOUTH);
        add(center, BorderLayout.CENTER);
        loadAll();
    }

    private JPanel header() {
        JPanel panel = new JPanel(new BorderLayout(12, 8));
        panel.setOpaque(false);
        JPanel title = new JPanel(new BorderLayout());
        title.setOpaque(false);
        title.add(Theme.title("Master Dosen"), BorderLayout.NORTH);
        title.add(Theme.muted("Pencarian dosen hanya menggunakan kode dosen."), BorderLayout.CENTER);
        panel.add(title, BorderLayout.WEST);

        JPanel search = new JPanel(new BorderLayout(8, 0));
        search.setOpaque(false);
        search.add(kodeField, BorderLayout.CENTER);
        JButton searchButton = Theme.primaryButton("Cari Kode");
        searchButton.addActionListener(event -> search());
        JButton allButton = Theme.secondaryButton("Semua");
        allButton.addActionListener(event -> {
            kodeField.setText("");
            loadAll();
        });
        JPanel actions = new JPanel(new GridLayout(1, 2, 6, 0));
        actions.setOpaque(false);
        actions.add(searchButton);
        actions.add(allButton);
        search.add(actions, BorderLayout.EAST);
        panel.add(search, BorderLayout.CENTER);
        return panel;
    }

    private void loadAll() {
        try {
            table.setModel(service.table("""
                    SELECT kode_dosen AS `Kode Dosen`, nama_dosen AS Nama,
                           REPLACE(jabatan, '_', ' ') AS Jabatan, alamat AS Alamat, keahlian AS Keahlian
                    FROM dosen
                    ORDER BY kode_dosen
                    """));
        } catch (Exception ex) {
            Theme.error(this, ex);
        }
    }

    private void search() {
        String kode = kodeField.getText().trim().toUpperCase();
        if (kode.isBlank()) {
            loadAll();
            return;
        }
        try {
            if (!service.dosenExists(kode)) {
                Theme.info(this, "Maaf data tersebut tidak ada.");
                return;
            }
            table.setModel(service.table("""
                    SELECT kode_dosen AS `Kode Dosen`, nama_dosen AS Nama,
                           REPLACE(jabatan, '_', ' ') AS Jabatan, alamat AS Alamat, keahlian AS Keahlian
                    FROM dosen
                    WHERE kode_dosen = ?
                    """, kode));
        } catch (Exception ex) {
            Theme.error(this, ex);
        }
    }

    private void showSelectedDetail() {
        int row = table.getSelectedRow();
        if (row < 0) {
            Theme.info(this, "Pilih dosen pada tabel terlebih dahulu.");
            return;
        }
        String kodeDosen = String.valueOf(table.getValueAt(table.convertRowIndexToModel(row), 0));
        try {
            JTable detailTable = Theme.table();
            detailTable.setModel(service.table("""
                    SELECT d.kode_dosen AS `Kode Dosen`, d.nama_dosen AS Nama,
                           REPLACE(d.jabatan, '_', ' ') AS Jabatan,
                           d.alamat AS Alamat,
                           d.keahlian AS Keahlian,
                           COALESCE(GROUP_CONCAT(DISTINCT CONCAT(mk.kode_mk, ' - ', mk.nama_mk)
                                       ORDER BY mk.kode_mk SEPARATOR '; '), '-') AS `Daftar Mata Kuliah`,
                           COALESCE(GROUP_CONCAT(DISTINCT CONCAT(mw.nim, ' - ', mw.nama)
                                       ORDER BY mw.nim SEPARATOR '; '), '-') AS `Daftar Mahasiswa Wali`,
                           COUNT(DISTINCT mk.kode_mk) AS `Jumlah Mata Kuliah`,
                           COUNT(DISTINCT mw.nim) AS `Jumlah Mahasiswa Wali`,
                           d.created_at AS `Tanggal Input`
                    FROM dosen d
                    LEFT JOIN matakuliah mk ON mk.kode_dosen = d.kode_dosen
                    LEFT JOIN mahasiswa mw ON mw.kode_dosen_wali = d.kode_dosen
                    WHERE d.kode_dosen = ?
                    GROUP BY d.kode_dosen, d.nama_dosen, d.jabatan, d.alamat, d.keahlian, d.created_at
                    """, kodeDosen));
            javax.swing.JDialog dialog = new javax.swing.JDialog(
                    (java.awt.Frame) javax.swing.SwingUtilities.getWindowAncestor(this),
                    "Data Lengkap Dosen", true);
            JPanel content = Theme.surface();
            content.setLayout(new BorderLayout(0, 10));
            JPanel header = new JPanel(new BorderLayout());
            header.setOpaque(false);
            header.add(Theme.title("Data Lengkap Dosen"), BorderLayout.WEST);
            JButton maximize = Theme.secondaryButton("Perbesar");
            maximize.addActionListener(event -> maximizeDialog(dialog));
            JPanel headerActions = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 0, 0));
            headerActions.setOpaque(false);
            headerActions.add(maximize);
            header.add(headerActions, BorderLayout.EAST);
            content.add(header, BorderLayout.NORTH);
            content.add(Theme.tableScroll(detailTable), BorderLayout.CENTER);
            dialog.setContentPane(content);
            dialog.setSize(1200, 520);
            dialog.setResizable(true);
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
        } catch (Exception ex) {
            Theme.error(this, ex);
        }
    }

    private void maximizeDialog(javax.swing.JDialog dialog) {
        java.awt.Rectangle bounds = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getMaximumWindowBounds();
        dialog.setBounds(bounds);
    }
}
