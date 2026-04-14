package id.ac.utb.pbo2.ui;

import id.ac.utb.pbo2.service.LayananAkademik;
import id.ac.utb.pbo2.service.LayananAkademik.ProdiItem;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import java.awt.BorderLayout;
import java.awt.GridLayout;

public class MataKuliahPanel extends JPanel {
    private final LayananAkademik service = new LayananAkademik();
    private final JTable table = Theme.table();
    private final JComboBox<ProdiItem> prodiBox = new JComboBox<>();
    private final JComboBox<String> semesterBox = Theme.comboBox(new String[]{
            "Semua", "1", "2", "3", "4", "5", "6", "7", "8"
    });
    private final JLabel noteLabel = Theme.muted("Setiap semester memiliki minimal 8 mata kuliah.");

    public MataKuliahPanel() {
        setLayout(new BorderLayout(0, 14));
        setBackground(Theme.BACKGROUND);
        setBorder(Theme.page().getBorder());
        add(header(), BorderLayout.NORTH);
        add(Theme.tableScroll(table), BorderLayout.CENTER);
        loadProdi();
        semesterBox.addActionListener(event -> load());
        prodiBox.addActionListener(event -> load());
        load();
    }

    private JPanel header() {
        JPanel panel = new JPanel(new BorderLayout(12, 8));
        panel.setOpaque(false);
        JPanel title = new JPanel(new BorderLayout());
        title.setOpaque(false);
        title.add(Theme.title("Master Mata Kuliah"), BorderLayout.NORTH);
        title.add(noteLabel, BorderLayout.CENTER);
        panel.add(title, BorderLayout.WEST);

        JPanel filter = new JPanel(new GridLayout(1, 4, 8, 0));
        filter.setOpaque(false);
        filter.add(Theme.sectionTitle("Prodi"));
        filter.add(prodiBox);
        filter.add(Theme.sectionTitle("Semester"));
        filter.add(semesterBox);
        panel.add(filter, BorderLayout.EAST);
        return panel;
    }

    private void loadProdi() {
        try {
            prodiBox.removeAllItems();
            prodiBox.addItem(new ProdiItem("SEMUA", "Semua Prodi", "-", "-"));
            for (ProdiItem prodi : service.prodiList()) {
                prodiBox.addItem(prodi);
            }
        } catch (Exception ex) {
            Theme.error(this, ex);
        }
    }

    private void load() {
        try {
            Object selected = semesterBox.getSelectedItem();
            ProdiItem prodi = (ProdiItem) prodiBox.getSelectedItem();
            String kodeProdi = prodi == null ? "SEMUA" : prodi.kodeProdi();
            if ("Semua".equals(selected) && "SEMUA".equals(kodeProdi)) {
                table.setModel(service.table("""
                        SELECT mk.kode_mk AS Kode, 
                               CASE 
                                   WHEN LOCATE(' - ', mk.nama_mk) > 0 
                                   THEN SUBSTRING(mk.nama_mk, LOCATE(' - ', mk.nama_mk) + 3)
                                   ELSE mk.nama_mk 
                               END AS `Mata Kuliah`, mk.sks AS SKS,
                               mk.semester AS Semester, p.nama_prodi AS Prodi,
                               mk.kode_dosen AS `Kode Dosen`, d.nama_dosen AS Dosen
                        FROM matakuliah mk
                        JOIN dosen d ON d.kode_dosen = mk.kode_dosen
                        JOIN prodi p ON p.kode_prodi = mk.kode_prodi
                        ORDER BY mk.semester, mk.kode_mk
                        """));
                noteLabel.setText("Setiap semester memiliki minimal 8 mata kuliah.");
            } else {
                boolean allSemester = "Semua".equals(selected);
                Integer semester = allSemester ? null : Integer.parseInt(String.valueOf(selected));
                if ("SEMUA".equals(kodeProdi)) {
                    table.setModel(service.table("""
                            SELECT mk.kode_mk AS Kode, 
                                   CASE 
                                       WHEN LOCATE(' - ', mk.nama_mk) > 0 
                                       THEN SUBSTRING(mk.nama_mk, LOCATE(' - ', mk.nama_mk) + 3)
                                       ELSE mk.nama_mk 
                                   END AS `Mata Kuliah`, mk.sks AS SKS,
                                   mk.semester AS Semester, p.nama_prodi AS Prodi,
                                   mk.kode_dosen AS `Kode Dosen`, d.nama_dosen AS Dosen
                            FROM matakuliah mk
                            JOIN dosen d ON d.kode_dosen = mk.kode_dosen
                            JOIN prodi p ON p.kode_prodi = mk.kode_prodi
                            WHERE mk.semester = ?
                            ORDER BY mk.kode_mk
                            """, semester));
                } else if (allSemester) {
                    table.setModel(service.table("""
                            SELECT mk.kode_mk AS Kode, 
                                   CASE 
                                       WHEN LOCATE(' - ', mk.nama_mk) > 0 
                                       THEN SUBSTRING(mk.nama_mk, LOCATE(' - ', mk.nama_mk) + 3)
                                       ELSE mk.nama_mk 
                                   END AS `Mata Kuliah`, mk.sks AS SKS,
                                   mk.semester AS Semester, p.nama_prodi AS Prodi,
                                   mk.kode_dosen AS `Kode Dosen`, d.nama_dosen AS Dosen
                            FROM matakuliah mk
                            JOIN dosen d ON d.kode_dosen = mk.kode_dosen
                            JOIN prodi p ON p.kode_prodi = mk.kode_prodi
                            WHERE mk.kode_prodi = ?
                            ORDER BY mk.semester, mk.kode_mk
                            """, kodeProdi));
                } else {
                    table.setModel(service.table("""
                            SELECT mk.kode_mk AS Kode, 
                                   CASE 
                                       WHEN LOCATE(' - ', mk.nama_mk) > 0 
                                       THEN SUBSTRING(mk.nama_mk, LOCATE(' - ', mk.nama_mk) + 3)
                                       ELSE mk.nama_mk 
                                   END AS `Mata Kuliah`, mk.sks AS SKS,
                                   mk.semester AS Semester, p.nama_prodi AS Prodi,
                                   mk.kode_dosen AS `Kode Dosen`, d.nama_dosen AS Dosen
                            FROM matakuliah mk
                            JOIN dosen d ON d.kode_dosen = mk.kode_dosen
                            JOIN prodi p ON p.kode_prodi = mk.kode_prodi
                            WHERE mk.semester = ? AND mk.kode_prodi = ?
                            ORDER BY mk.kode_mk
                            """, semester, kodeProdi));
                }
                if (allSemester) {
                    noteLabel.setText("Prodi " + (prodi == null ? "" : prodi.namaProdi()) + " menampilkan semua semester.");
                } else {
                    int count = "SEMUA".equals(kodeProdi)
                            ? service.coursesBySemester(semester).size()
                            : service.coursesBySemester(kodeProdi, semester).size();
                    noteLabel.setText("Semester " + semester + " memiliki " + count + " mata kuliah.");
                }
            }
        } catch (Exception ex) {
            Theme.error(this, ex);
        }
    }
}
