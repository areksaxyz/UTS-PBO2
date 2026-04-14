package id.ac.utb.pbo2.ui;

import id.ac.utb.pbo2.service.LayananAkademik;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.GridLayout;

public class StudentListPanel extends JPanel {
    private final LayananAkademik service = new LayananAkademik();
    private final JTextField nimField = Theme.textField(10);
    private final JTable table = Theme.table();

    public StudentListPanel() {
        setLayout(new BorderLayout(0, 14));
        setBackground(Theme.BACKGROUND);
        setBorder(Theme.page().getBorder());
        add(header(), BorderLayout.NORTH);
        add(Theme.tableScroll(table), BorderLayout.CENTER);
        loadAll();
    }

    private JPanel header() {
        JPanel panel = new JPanel(new BorderLayout(12, 8));
        panel.setOpaque(false);
        JPanel title = new JPanel(new BorderLayout());
        title.setOpaque(false);
        title.add(Theme.title("List Mahasiswa"), BorderLayout.NORTH);
        title.add(Theme.muted("Daftar NIM terdaftar untuk membantu transaksi KRS dan nilai."), BorderLayout.CENTER);
        panel.add(title, BorderLayout.WEST);

        JPanel search = new JPanel(new GridLayout(1, 3, 8, 0));
        search.setOpaque(false);
        search.add(nimField);
        JButton searchButton = Theme.primaryButton("Cari NIM");
        searchButton.addActionListener(event -> search());
        JButton resetButton = Theme.secondaryButton("Semua");
        resetButton.addActionListener(event -> {
            nimField.setText("");
            loadAll();
        });
        search.add(searchButton);
        search.add(resetButton);
        panel.add(search, BorderLayout.CENTER);
        return panel;
    }

    private void loadAll() {
        try {
            table.setModel(service.table(studentListSql("")));
        } catch (Exception ex) {
            Theme.error(this, ex);
        }
    }

    private void search() {
        String nim = nimField.getText().trim();
        try {
            LayananAkademik.validateNimSearch(nim);
            if (!service.mahasiswaExists(nim)) {
                Theme.info(this, "Maaf data tersebut tidak ada.");
                return;
            }
            table.setModel(service.table(studentListSql("WHERE m.nim = ?"), nim));
        } catch (Exception ex) {
            Theme.error(this, ex);
        }
    }

    private String studentListSql(String whereClause) {
        return """
                SELECT m.nim AS NIM, m.nama AS Nama, m.jenis_kelamin AS `Jenis Kelamin`,
                       p.nama_prodi AS Prodi, m.kode_kelas AS Kelas,
                       d.nama_dosen AS `Dosen Wali`, CAST(m.angkatan AS UNSIGNED) AS Angkatan,
                       COALESCE(k.semester_aktif, u_latest.semester_ukt, 1) AS `Semester Aktif`,
                       IF(COALESCE(u_active.status_lunas, 0) = 1, 'Lunas', 'Belum Lunas') AS `UKT Semester Aktif`
                FROM mahasiswa m
                LEFT JOIN prodi p ON p.kode_prodi = m.kode_prodi
                LEFT JOIN dosen d ON d.kode_dosen = m.kode_dosen_wali
                LEFT JOIN (
                    SELECT nim, MAX(semester) AS semester_aktif
                    FROM krs
                    GROUP BY nim
                ) k ON k.nim = m.nim
                LEFT JOIN (
                    SELECT nim, MAX(semester) AS semester_ukt
                    FROM pembayaran_ukt
                    WHERE tahun_akademik = '2025/2026'
                    GROUP BY nim
                ) u_latest ON u_latest.nim = m.nim
                LEFT JOIN pembayaran_ukt u_active ON u_active.nim = m.nim
                    AND u_active.semester = COALESCE(k.semester_aktif, u_latest.semester_ukt, 1)
                    AND u_active.tahun_akademik = '2025/2026'
                """ + whereClause + """
                ORDER BY m.nim
                """;
    }
}
