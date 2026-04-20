package id.ac.utb.pbo2.ui;

import id.ac.utb.pbo2.model.PenggunaSaatIni;
import id.ac.utb.pbo2.service.LayananAkademik;
import id.ac.utb.pbo2.service.LayananAkademik.CourseItem;
import id.ac.utb.pbo2.service.LayananAkademik.KrsResult;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class KrsPanel extends JPanel {
    private final LayananAkademik service = new LayananAkademik();
    private final PenggunaSaatIni user;
    private final JTable dataTable = Theme.table();
    private final JTable courseTable = Theme.table();
    private final JTable validationTable = Theme.table();
    private final JTextField nimField = Theme.textField(8);
    private final JTextField tahunField = Theme.textField(9);
    private final JComboBox<Integer> semesterBox = Theme.comboBox(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8});
    private final JLabel summaryLabel = Theme.muted("Masukkan NIM 5 angka untuk melihat KRS.");
    private final List<CourseItem> availableCourses = new ArrayList<>();

    public KrsPanel(PenggunaSaatIni user) {
        this.user = user;
        setLayout(new BorderLayout(0, 14));
        setBackground(Theme.BACKGROUND);
        setBorder(Theme.page().getBorder());

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(Theme.title("Transaksi KRS"), BorderLayout.NORTH);
        header.add(Theme.muted("Operator dapat membuat KRS dengan minimal 8 mata kuliah per semester."), BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(14, 0));
        body.setOpaque(false);
        body.add(formPanel(), BorderLayout.WEST);
        JPanel tablePanel = new JPanel(new BorderLayout(0, 8));
        tablePanel.setOpaque(false);
        tablePanel.add(summaryLabel, BorderLayout.NORTH);
        JPanel splitTables = new JPanel(new GridLayout(3, 1, 0, 10));
        splitTables.setOpaque(false);
        splitTables.add(tableSection("Mahasiswa dan KRS", dataTable));
        splitTables.add(tableSection("Mata Kuliah dan Dosen", courseTable));
        splitTables.add(tableSection("Mengulang, UKT, dan ACC", validationTable));
        tablePanel.add(splitTables, BorderLayout.CENTER);
        body.add(tablePanel, BorderLayout.CENTER);
        add(body, BorderLayout.CENTER);

        tahunField.setText("2025/2026");
        semesterBox.addActionListener(event -> loadCourses());
        loadCourses();
    }

    private JPanel formPanel() {
        JPanel panel = Theme.surface();
        panel.setLayout(new GridBagLayout());
        panel.setPreferredSize(new java.awt.Dimension(340, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        int y = 0;
        y = addField(panel, gbc, y, "NIM", nimField);
        y = addField(panel, gbc, y, "Semester", semesterBox);
        y = addField(panel, gbc, y, "Tahun Akademik", tahunField);
        JButton searchButton = Theme.secondaryButton("Cari KRS NIM");
        searchButton.addActionListener(event -> loadKrs());
        gbc.gridy = y++;
        panel.add(searchButton, gbc);

        JButton addButton = Theme.primaryButton("Pilih Mata Kuliah");
        addButton.addActionListener(event -> addSelectedCourses());
        gbc.gridy = y++;
        panel.add(addButton, gbc);

        return panel;
    }

    private JPanel tableSection(String title, JTable table) {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setOpaque(false);
        panel.add(Theme.sectionTitle(title), BorderLayout.NORTH);
        panel.add(Theme.tableScroll(table), BorderLayout.CENTER);
        return panel;
    }

    private int addField(JPanel panel, GridBagConstraints gbc, int y, String label, java.awt.Component component) {
        gbc.gridy = y++;
        panel.add(Theme.sectionTitle(label), gbc);
        gbc.gridy = y++;
        panel.add(component, gbc);
        return y;
    }

    private void loadCourses() {
        try {
            availableCourses.clear();
            int semester = selectedSemester();
            String nim = nimField.getText().trim();
            if (nim.length() == 5 && service.mahasiswaExists(nim)) {
                String kodeProdi = service.studentProdi(nim);
                availableCourses.addAll(service.coursesUpToSemester(kodeProdi, semester));
            }
        } catch (Exception ex) {
            Theme.error(this, ex);
        }
    }

    private void loadKrs() {
        String nim = nimField.getText().trim();
        try {
            LayananAkademik.validateNimSearch(nim);
            if (!service.mahasiswaExists(nim)) {
                Theme.info(this, "Maaf data tersebut tidak ada.");
                return;
            }
            int semester = selectedSemester();
            int maxSemester = service.maxKrsSemester(nim);
            if (semester > maxSemester && !(maxSemester == 0 && semester == 1)) {
                Theme.info(this, "Maaf mahasiswa tersebut belum menempuh semester " + semester + ".");
                summaryLabel.setText("Mahasiswa belum menempuh semester " + semester + ".");
                dataTable.setModel(new DefaultTableModel(new String[] {
                        "ID", "NIM", "Nama", "Jenis Kelamin", "Kelas", "Prodi", "Semester KRS", "Tahun"}, 0));
                courseTable.setModel(new DefaultTableModel(new String[] {
                        "ID", "Kode", "Mata Kuliah", "SKS", "Semester MK", "Kode Dosen", "Dosen"}, 0));
                validationTable.setModel(new DefaultTableModel(new String[] {
                        "ID", "Detail", "UKT", "ACC Oleh", "Kode ACC", "Nama ACC", "Status ACC", "Status KRS"}, 0));
                return;
            }
            loadCourses();
            dataTable.setModel(service.table("""
                    SELECT id AS ID, nim AS NIM, nama AS Nama, jenis_kelamin AS `Jenis Kelamin`,
                           kelas_semester AS Kelas, nama_prodi AS Prodi,
                           semester AS `Semester KRS`, tahun_akademik AS Tahun
                    FROM v_krs_detail
                    WHERE nim = ? AND semester = ?
                    ORDER BY kode_mk
                    """, nim, semester));
            courseTable.setModel(service.table("""
                    SELECT id AS ID, kode_mk AS Kode, nama_mk AS `Mata Kuliah`, sks AS SKS,
                           semester_mk AS `Semester MK`, kode_dosen AS `Kode Dosen`, nama_dosen AS Dosen
                    FROM v_krs_detail
                    WHERE nim = ? AND semester = ?
                    ORDER BY kode_mk
                    """, nim, semester));
            validationTable.setModel(service.table("""
                    SELECT id AS ID, detail_mengulang AS Detail,
                           status_ukt AS UKT, acc_oleh AS `ACC Oleh`,
                           kode_dosen_acc AS `Kode ACC`, dosen_acc AS `Nama ACC`,
                           status_acc AS `Status ACC`, status_krs AS `Status KRS`
                    FROM v_krs_detail
                    WHERE nim = ? AND semester = ?
                    ORDER BY kode_mk
                    """, nim, semester));
            int count = service.countKrsByNimSemester(nim, semester);
            int totalSks = service.sumSksByNimSemester(nim, semester);
            summaryLabel.setText("NIM " + nim + " semester " + semester + " memiliki " + count
                    + " mata kuliah (" + totalSks + " SKS). Minimal yang disarankan: 8.");
        } catch (Exception ex) {
            Theme.error(this, ex);
        }
    }

    private void addSelectedCourses() {
        try {
            String nim = requireValidNim();
            if (availableCourses.isEmpty()) {
                throw new IllegalArgumentException("Daftar mata kuliah kosong. Periksa NIM dan semester.");
            }
            showCourseSelectionDialog(nim);
        } catch (Exception ex) {
            Theme.error(this, ex);
        }
    }

    private String requireValidNim() throws SQLException {
        String nim = nimField.getText().trim();
        LayananAkademik.validateNimSearch(nim);
        if (!service.mahasiswaExists(nim)) {
            throw new IllegalArgumentException("Maaf data tersebut tidak ada.");
        }
        LayananAkademik.validateSemester(selectedSemester());
        if (tahunField.getText().trim().isBlank()) {
            throw new IllegalArgumentException("Tahun akademik wajib diisi.");
        }
        return nim;
    }

    private int selectedSemester() {
        return (Integer) semesterBox.getSelectedItem();
    }

    private void showCourseSelectionDialog(String nim) throws SQLException {
        JDialog dialog = new JDialog((java.awt.Frame) javax.swing.SwingUtilities.getWindowAncestor(this),
                "Pilih Mata Kuliah", true);
        dialog.setSize(800, 520);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(true);

        String[] cols = {"Pilih", "Kode", "Mata Kuliah", "SKS", "Semester MK"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 0 ? Boolean.class : String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0;
            }
        };
        for (CourseItem course : availableCourses) {
            model.addRow(new Object[]{Boolean.FALSE, course.kodeMk(), course.namaMk(),
                    String.valueOf(course.sks()), String.valueOf(course.semester())});
        }
        JTable table = Theme.table();
        table.setModel(model);

        JPanel root = Theme.surface();
        root.setLayout(new BorderLayout(0, 10));
        root.add(Theme.title("Pilih Mata Kuliah (Minimal 8)"), BorderLayout.NORTH);
        root.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton saveButton = Theme.primaryButton("Simpan Pilihan");
        saveButton.addActionListener(event -> {
            int selected = 0;
            int selectedSks = 0;
            for (int i = 0; i < model.getRowCount(); i++) {
                if (Boolean.TRUE.equals(model.getValueAt(i, 0))) {
                    selected++;
                    selectedSks += Integer.parseInt(String.valueOf(model.getValueAt(i, 3)));
                }
            }
            if (selected < 8) {
                Theme.info(dialog, "Minimal pilih 8 mata kuliah.");
                return;
            }
            if (selectedSks < 24) {
                Theme.info(dialog, "Total SKS minimal 24 per semester.");
                return;
            }
            int inserted = 0;
            int skipped = 0;
            int repeated = 0;
            for (int i = 0; i < model.getRowCount(); i++) {
                if (!Boolean.TRUE.equals(model.getValueAt(i, 0))) {
                    continue;
                }
                String kodeMk = String.valueOf(model.getValueAt(i, 1));
                try {
                    KrsResult result = service.addKrs(nim, kodeMk, selectedSemester(),
                            tahunField.getText().trim(), user.getId());
                    inserted++;
                    if (result.mengulang()) {
                        repeated++;
                    }
                } catch (SQLException ex) {
                    if (ex.getErrorCode() == 1062) {
                        skipped++;
                    } else {
                        Theme.error(dialog, ex);
                        return;
                    }
                } catch (Exception ex) {
                    Theme.error(dialog, ex);
                    return;
                }
            }
            Theme.info(dialog, "Berhasil: " + inserted + "\nDuplikat: " + skipped + "\nMengulang: " + repeated);
            dialog.dispose();
            loadKrs();
        });
        root.add(saveButton, BorderLayout.SOUTH);
        dialog.setContentPane(root);
        dialog.setVisible(true);
    }
}
