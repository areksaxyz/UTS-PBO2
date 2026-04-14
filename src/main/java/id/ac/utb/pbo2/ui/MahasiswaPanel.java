package id.ac.utb.pbo2.ui;

import id.ac.utb.pbo2.model.PenggunaSaatIni;
import id.ac.utb.pbo2.service.LayananAkademik;
import id.ac.utb.pbo2.service.LayananAkademik.DosenItem;
import id.ac.utb.pbo2.service.LayananAkademik.ProdiItem;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.HierarchyEvent;
import java.awt.Insets;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLException;

public class MahasiswaPanel extends JPanel {
    private final LayananAkademik service = new LayananAkademik();
    private final PenggunaSaatIni user;
    private final JTable table = Theme.table();
    private final JTextField searchField = Theme.textField(10);
    private final JTextField nimField = Theme.textField(10);
    private final JTextField namaField = Theme.textField(18);
    private final JComboBox<String> genderBox = Theme.comboBox(new String[]{"Laki-laki", "Perempuan"});
    private final JTextField alamatField = Theme.textField(18);
    private final JComboBox<String> provinsiBox = new JComboBox<>();
    private final JComboBox<String> kabupatenBox = new JComboBox<>();
    private final JComboBox<String> kecamatanBox = new JComboBox<>();
    private final JComboBox<String> kelurahanBox = new JComboBox<>();
    private final DatePickerField tanggalLahirPicker = new DatePickerField();
    private final JComboBox<ProdiItem> prodiBox = new JComboBox<>();
    private final JComboBox<String> kelasBox = new JComboBox<>();
    private final JComboBox<DosenItem> dosenWaliBox = new JComboBox<>();
    private final YearPickerField angkatanPicker = new YearPickerField(2023);
    private final JButton backButton = Theme.secondaryButton("Kembali");
    private final java.util.List<DosenItem> dosenItems = new java.util.ArrayList<>();

    public MahasiswaPanel(PenggunaSaatIni user) {
        this.user = user;
        table.setAutoCreateRowSorter(false);
        table.setRowSorter(null);
        setLayout(new BorderLayout(0, 14));
        setBackground(Theme.BACKGROUND);
        setBorder(Theme.page().getBorder());

        add(header(), BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(14, 0));
        body.setOpaque(false);
        body.add(formScroll(), BorderLayout.WEST);
        JScrollPane tablePane = Theme.tableScroll(table);
        optimizeScrollSpeed(tablePane);
        body.add(tablePane, BorderLayout.CENTER);
        add(body, BorderLayout.CENTER);

        prodiBox.addActionListener(event -> {
            refreshKelasOptions();
            refreshDosenWaliOptions();
        });
        angkatanPicker.setYearChangeListener(this::refreshKelasOptions);
        provinsiBox.addActionListener(event -> refreshKabupatenOptions());
        kabupatenBox.addActionListener(event -> refreshKecamatanOptions());
        kecamatanBox.addActionListener(event -> refreshKelurahanOptions());
        loadOptions();
        loadWilayahOptions();
        loadAll();
        addHierarchyListener(event -> {
            if ((event.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 && isShowing()) {
                SwingUtilities.invokeLater(this::loadAll);
            }
        });
    }

    private JScrollPane formScroll() {
        JScrollPane scrollPane = new JScrollPane(formPanel());
        scrollPane.setPreferredSize(new java.awt.Dimension(360, 0));
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        optimizeScrollSpeed(scrollPane);
        scrollPane.getViewport().setBackground(Theme.BACKGROUND);
        return scrollPane;
    }

    private void optimizeScrollSpeed(JScrollPane scrollPane) {
        scrollPane.getVerticalScrollBar().setUnitIncrement(24);
        scrollPane.getVerticalScrollBar().setBlockIncrement(96);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(24);
    }

    private JPanel header() {
        JPanel panel = new JPanel(new BorderLayout(12, 8));
        panel.setOpaque(false);
        JPanel title = new JPanel(new BorderLayout());
        title.setOpaque(false);
        title.add(Theme.title("Master Mahasiswa"), BorderLayout.NORTH);
        title.add(Theme.muted("Pencarian hanya menggunakan NIM 5 angka."), BorderLayout.CENTER);
        panel.add(title, BorderLayout.WEST);

        JPanel search = new JPanel(new BorderLayout(8, 0));
        search.setOpaque(false);
        search.add(searchField, BorderLayout.CENTER);
        JButton searchButton = Theme.primaryButton("Cari NIM");
        searchButton.addActionListener(event -> search());
        backButton.setEnabled(false);
        backButton.addActionListener(event -> {
            searchField.setText("");
            loadAll();
            backButton.setEnabled(false);
        });
        JPanel actions = new JPanel(new GridLayout(1, 2, 6, 0));
        actions.setOpaque(false);
        actions.add(searchButton);
        actions.add(backButton);
        search.add(actions, BorderLayout.EAST);
        panel.add(search, BorderLayout.CENTER);
        return panel;
    }

    private JPanel formPanel() {
        JPanel panel = Theme.surface();
        panel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        int y = 0;

        // Tambahkan label tahun akademik saat ini
        JPanel academicYearPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        academicYearPanel.setOpaque(false);
        JLabel academicYearLabel = new JLabel("Tahun Akademik: " + getCurrentAcademicYear());
        academicYearLabel.setFont(academicYearLabel.getFont().deriveFont(Font.BOLD, 12f));
        academicYearPanel.add(academicYearLabel);
        gbc.gridy = y++;
        panel.add(academicYearPanel, gbc);

        y = addField(panel, gbc, y, "NIM", nimField);
        y = addField(panel, gbc, y, "Nama", namaField);
        y = addField(panel, gbc, y, "Jenis Kelamin", genderBox);
        y = addField(panel, gbc, y, "Tanggal Lahir", tanggalLahirPicker);
        y = addField(panel, gbc, y, "Alamat", alamatField);
        y = addField(panel, gbc, y, "Provinsi", provinsiBox);
        y = addField(panel, gbc, y, "Kabupaten/Kota", kabupatenBox);
        y = addField(panel, gbc, y, "Kecamatan", kecamatanBox);
        y = addField(panel, gbc, y, "Kelurahan", kelurahanBox);
        y = addField(panel, gbc, y, "Prodi", prodiBox);
        y = addField(panel, gbc, y, "Kelas", kelasBox);
        y = addField(panel, gbc, y, "Dosen Wali", dosenWaliBox);
        y = addField(panel, gbc, y, "Angkatan", angkatanPicker);

        JButton saveButton = Theme.primaryButton("Tambah Mahasiswa");
        saveButton.setEnabled(user.isAdmin());
        saveButton.addActionListener(event -> addMahasiswa());
        gbc.gridy = y++;
        panel.add(saveButton, gbc);

        JButton deleteButton = Theme.dangerButton("Hapus Mahasiswa Terpilih");
        deleteButton.setEnabled(user.isAdmin());
        deleteButton.addActionListener(event -> deleteSelected());
        gbc.gridy = y++;
        panel.add(deleteButton, gbc);

        JButton detailButton = Theme.secondaryButton("Lihat Data Lengkap");
        detailButton.addActionListener(event -> showSelectedDetail());
        gbc.gridy = y;
        panel.add(detailButton, gbc);
        return panel;
    }

    private void loadOptions() {
        try {
            prodiBox.removeAllItems();
            for (ProdiItem prodi : service.prodiList()) {
                prodiBox.addItem(prodi);
            }
            dosenItems.clear();
            for (DosenItem dosen : service.dosenList()) {
                dosenItems.add(dosen);
            }
            refreshKelasOptions();
            refreshDosenWaliOptions();
        } catch (Exception ex) {
            Theme.error(this, ex);
        }
    }

    private void refreshDosenWaliOptions() {
        ProdiItem selected = (ProdiItem) prodiBox.getSelectedItem();
        Object previous = dosenWaliBox.getSelectedItem();
        dosenWaliBox.removeAllItems();
        for (DosenItem dosen : dosenItems) {
            if (selected != null && dosen.kodeDosen().equals(selected.kodeDosenProdi())) {
                continue;
            }
            dosenWaliBox.addItem(dosen);
            if (dosen.equals(previous)) {
                dosenWaliBox.setSelectedItem(dosen);
            }
        }
    }

    private void refreshKelasOptions() {
        ProdiItem selected = (ProdiItem) prodiBox.getSelectedItem();
        Object previous = kelasBox.getSelectedItem();
        kelasBox.removeAllItems();
        if (selected == null) {
            return;
        }
        for (String kelas : classOptions(selected.kodeProdi(), angkatanPicker.getYear())) {
            kelasBox.addItem(kelas);
            if (kelas.equals(previous)) {
                kelasBox.setSelectedItem(kelas);
            }
        }
    }

    private int addField(JPanel panel, GridBagConstraints gbc, int y, String label, java.awt.Component component) {
        gbc.gridy = y++;
        panel.add(Theme.sectionTitle(label), gbc);
        gbc.gridy = y++;
        panel.add(component, gbc);
        return y;
    }

    private void loadAll() {
        try {
            DefaultTableModel model = service.table(mahasiswaListSql(""));
            // Fallback jika JOIN tidak mengembalikan row padahal data mahasiswa ada.
            if (model.getRowCount() == 0 && service.count("mahasiswa") > 0) {
                model = service.table(mahasiswaFallbackSql(""));
            }
            applyModel(model, "loadAll");
        } catch (Exception ex) {
            Theme.error(this, ex);
        }
    }

    public void refreshData() {
        loadAll();
    }
    private void loadWilayahOptions() {
        try {
            // Load Provinsi
            provinsiBox.removeAllItems();
            provinsiBox.addItem("");
            var provinsiList = service.getWilayahProvinsi();
            for (var p : provinsiList) {
                provinsiBox.addItem(p.get("kode") + " - " + p.get("nama"));
            }
        } catch (Exception ex) {
            Theme.error(this, ex);
        }
    }

    private void refreshKabupatenOptions() {
        try {
            kabupatenBox.removeAllItems();
            kabupatenBox.addItem("");
            String selectedProvinsi = (String) provinsiBox.getSelectedItem();
            if (selectedProvinsi != null && !selectedProvinsi.isEmpty()) {
                String kodeProvinsi = selectedProvinsi.split(" - ")[0];
                var kabupatenList = service.getWilayahKabupaten(kodeProvinsi);
                for (var k : kabupatenList) {
                    kabupatenBox.addItem(k.get("kode") + " - " + k.get("nama"));
                }
            }
            kecamatanBox.removeAllItems();
            kelurahanBox.removeAllItems();
        } catch (Exception ex) {
            Theme.error(this, ex);
        }
    }

    private void refreshKecamatanOptions() {
        try {
            kecamatanBox.removeAllItems();
            kecamatanBox.addItem("");
            String selectedKabupaten = (String) kabupatenBox.getSelectedItem();
            if (selectedKabupaten != null && !selectedKabupaten.isEmpty()) {
                String kodeKabupaten = selectedKabupaten.split(" - ")[0];
                var kecamatanList = service.getWilayahKecamatan(kodeKabupaten);
                for (var k : kecamatanList) {
                    kecamatanBox.addItem(k.get("kode") + " - " + k.get("nama"));
                }
            }
            kelurahanBox.removeAllItems();
        } catch (Exception ex) {
            Theme.error(this, ex);
        }
    }

    private void refreshKelurahanOptions() {
        try {
            kelurahanBox.removeAllItems();
            kelurahanBox.addItem("");
            String selectedKecamatan = (String) kecamatanBox.getSelectedItem();
            if (selectedKecamatan != null && !selectedKecamatan.isEmpty()) {
                String kodeKecamatan = selectedKecamatan.split(" - ")[0];
                var kelurahanList = service.getWilayahKelurahan(kodeKecamatan);
                for (var k : kelurahanList) {
                    kelurahanBox.addItem(k.get("kode") + " - " + k.get("nama"));
                }
            }
        } catch (Exception ex) {
            Theme.error(this, ex);
        }
    }
    private void search() {
        String nim = searchField.getText().trim();
        try {
            LayananAkademik.validateNimSearch(nim);
            if (!service.mahasiswaExists(nim)) {
                Theme.info(this, "Maaf data tersebut tidak ada.");
                return;
            }
            DefaultTableModel model = service.table(mahasiswaListSql("WHERE m.nim = ?"), nim);
            if (model.getRowCount() == 0) {
                model = service.table(mahasiswaFallbackSql("WHERE m.nim = ?"), nim);
            }
            applyModel(model, "search");
            backButton.setEnabled(true);
        } catch (Exception ex) {
            Theme.error(this, ex);
        }
    }

    private void applyModel(DefaultTableModel model, String source) {
        model = withRowNumbers(model);
        table.setRowSorter(null);
        table.setModel(model);
        table.createDefaultColumnsFromModel();
        table.setRowHeight(30);
        table.revalidate();
        table.repaint();

        int totalMahasiswa = 0;
        try {
            totalMahasiswa = service.count("mahasiswa");
        } catch (Exception ignored) {
            // hanya untuk debug log
        }
        int nimColumn = findColumn(model, "NIM");
        String firstNim = (model.getRowCount() > 0 && nimColumn >= 0)
                ? String.valueOf(model.getValueAt(0, nimColumn))
                : "-";
        System.out.println("[MAHASISWA] " + source
                + " modelRows=" + model.getRowCount()
                + ", viewRows=" + table.getRowCount()
                + ", totalMahasiswa=" + totalMahasiswa
                + ", firstNim=" + firstNim
                + ", sorter=" + (table.getRowSorter() != null));
    }

    private DefaultTableModel withRowNumbers(DefaultTableModel model) {
        if (model.getColumnCount() > 0 && "No".equalsIgnoreCase(model.getColumnName(0))) {
            return model;
        }
        int sourceColumns = model.getColumnCount();
        String[] columns = new String[sourceColumns + 1];
        columns[0] = "No";
        for (int i = 0; i < sourceColumns; i++) {
            columns[i + 1] = model.getColumnName(i);
        }
        DefaultTableModel numbered = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        for (int row = 0; row < model.getRowCount(); row++) {
            Object[] data = new Object[sourceColumns + 1];
            data[0] = row + 1;
            for (int col = 0; col < sourceColumns; col++) {
                data[col + 1] = model.getValueAt(row, col);
            }
            numbered.addRow(data);
        }
        return numbered;
    }

    private int findColumn(DefaultTableModel model, String columnName) {
        for (int i = 0; i < model.getColumnCount(); i++) {
            if (columnName.equalsIgnoreCase(model.getColumnName(i))) {
                return i;
            }
        }
        return -1;
    }

    private String mahasiswaListSql(String whereClause) {
        return """
                SELECT m.nim AS NIM, m.nama AS Nama,
                       COALESCE(m.jenis_kelamin, '-') AS `Jenis Kelamin`,
                       COALESCE(p.nama_prodi, m.prodi, '-') AS Prodi,
                       COALESCE(m.kode_kelas, '-') AS Kelas,
                       COALESCE(REPLACE(d.nama_dosen, '_', ' '), m.kode_dosen_wali, '-') AS `Dosen Wali`,
                       CAST(m.angkatan AS UNSIGNED) AS Angkatan,
                       COALESCE(m.status, 'AKTIF') AS Status
                FROM mahasiswa m
                LEFT JOIN prodi p ON p.kode_prodi = m.kode_prodi
                LEFT JOIN dosen d ON d.kode_dosen = m.kode_dosen_wali
                """ + whereClause + """
                ORDER BY m.nim
                """;
    }

    private String mahasiswaFallbackSql(String whereClause) {
        return """
                SELECT m.nim AS NIM, m.nama AS Nama,
                       COALESCE(m.jenis_kelamin, '-') AS `Jenis Kelamin`,
                       COALESCE(m.prodi, '-') AS Prodi,
                       COALESCE(m.kode_kelas, '-') AS Kelas,
                       COALESCE(m.kode_dosen_wali, '-') AS `Dosen Wali`,
                       CAST(m.angkatan AS UNSIGNED) AS Angkatan,
                       COALESCE(m.status, 'AKTIF') AS Status
                FROM mahasiswa m
                """ + whereClause + """
                ORDER BY m.nim
                """;
    }

    private void addMahasiswa() {
        try {
            String nim = nimField.getText().trim();
            LayananAkademik.validateNim(nim);
            if (service.mahasiswaExists(nim)) {
                Theme.info(this, "NIM sudah terdaftar dan tidak boleh duplikat.");
                return;
            }
            String nama = namaField.getText().trim();
            if (nama.isBlank()) {
                throw new IllegalArgumentException("Nama wajib diisi.");
            }
            String alamat = alamatField.getText().trim();
            if (alamat.isBlank()) {
                throw new IllegalArgumentException("Alamat wajib diisi.");
            }
            ProdiItem prodi = (ProdiItem) prodiBox.getSelectedItem();
            DosenItem dosenWali = (DosenItem) dosenWaliBox.getSelectedItem();
            String kelas = (String) kelasBox.getSelectedItem();
            if (prodi == null || dosenWali == null || kelas == null) {
                throw new IllegalArgumentException("Prodi, kelas, dan dosen wali wajib dipilih.");
            }
            if (dosenWali.kodeDosen().equals(prodi.kodeDosenProdi())) {
                throw new IllegalArgumentException("Dosen wali tidak boleh sama dengan Kaprodi.");
            }

            // Extract wilayah codes
            String kodeProvinsi = null, kodeKabupaten = null, kodeKecamatan = null, kodeKelurahan = null;
            String selectedProvinsi = (String) provinsiBox.getSelectedItem();
            if (selectedProvinsi != null && !selectedProvinsi.isEmpty()) {
                kodeProvinsi = selectedProvinsi.split(" - ")[0];
            }
            String selectedKabupaten = (String) kabupatenBox.getSelectedItem();
            if (selectedKabupaten != null && !selectedKabupaten.isEmpty()) {
                kodeKabupaten = selectedKabupaten.split(" - ")[0];
            }
            String selectedKecamatan = (String) kecamatanBox.getSelectedItem();
            if (selectedKecamatan != null && !selectedKecamatan.isEmpty()) {
                kodeKecamatan = selectedKecamatan.split(" - ")[0];
            }
            String selectedKelurahan = (String) kelurahanBox.getSelectedItem();
            if (selectedKelurahan != null && !selectedKelurahan.isEmpty()) {
                kodeKelurahan = selectedKelurahan.split(" - ")[0];
            }

            service.addMahasiswa(nim, nama, String.valueOf(genderBox.getSelectedItem()),
                    tanggalLahirPicker.getSelectedDate(), alamat, kodeProvinsi, kodeKabupaten,
                    kodeKecamatan, kodeKelurahan, prodi, kelas, angkatanPicker.getYear(), dosenWali.kodeDosen());
            Theme.info(this, "Mahasiswa berhasil ditambahkan.");
            loadAll();
        } catch (SQLIntegrityConstraintViolationException ex) {
            Theme.error(this, new IllegalArgumentException("Data mahasiswa duplikat atau tidak valid."));
        } catch (Exception ex) {
            Theme.error(this, ex);
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            Theme.info(this, "Pilih mahasiswa pada tabel terlebih dahulu.");
            return;
        }
        int modelRow = table.convertRowIndexToModel(row);
        String nim = valueAt(modelRow, "NIM");
        int confirm = JOptionPane.showConfirmDialog(this,
                "Hapus mahasiswa NIM " + nim + " beserta KRS/Nilai terkait?",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            service.deleteMahasiswa(nim);
            Theme.info(this, "Mahasiswa berhasil dihapus.");
            loadAll();
        } catch (Exception ex) {
            Theme.error(this, ex);
        }
    }

    private void fillFromSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            Theme.info(this, "Pilih mahasiswa pada tabel terlebih dahulu.");
            return;
        }
        int modelRow = table.convertRowIndexToModel(row);
        nimField.setText(valueAt(modelRow, "NIM"));
        namaField.setText(valueAt(modelRow, "Nama"));
        genderBox.setSelectedItem(valueAt(modelRow, "Jenis Kelamin"));
        selectProdiByName(valueAt(modelRow, "Prodi"));
        angkatanPicker.setYear(parseAngkatan(valueAt(modelRow, "Angkatan")));
        kelasBox.setSelectedItem(valueAt(modelRow, "Kelas"));
        selectDosenByName(valueAt(modelRow, "Dosen Wali"));
    }

    private void showSelectedDetail() {
        int row = table.getSelectedRow();
        if (row < 0) {
            Theme.info(this, "Pilih mahasiswa pada tabel terlebih dahulu.");
            return;
        }
        int modelRow = table.convertRowIndexToModel(row);
        String nim = valueAt(modelRow, "NIM");
        try {
            JTable detailTable = Theme.table();
            detailTable.setModel(loadDetailModel(nim));
            detailTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            javax.swing.JDialog dialog = new javax.swing.JDialog(
                    (java.awt.Frame) javax.swing.SwingUtilities.getWindowAncestor(this),
                    "Data Lengkap Mahasiswa", true);
            JPanel content = Theme.surface();
            content.setLayout(new BorderLayout(0, 10));
            JPanel header = new JPanel(new BorderLayout());
            header.setOpaque(false);
            header.add(Theme.title("Data Lengkap Mahasiswa"), BorderLayout.WEST);
            JButton maximize = Theme.secondaryButton("Perbesar");
            maximize.addActionListener(event -> maximizeDialog(dialog));
            JPanel headerActions = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 0, 0));
            headerActions.setOpaque(false);
            headerActions.add(maximize);
            header.add(headerActions, BorderLayout.EAST);
            content.add(header, BorderLayout.NORTH);
            content.add(Theme.tableScroll(detailTable), BorderLayout.CENTER);
            dialog.setContentPane(content);
            dialog.setSize(1400, 620);
            dialog.setResizable(true);
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
        } catch (Exception ex) {
            Theme.error(this, ex);
        }
    }

    private DefaultTableModel loadDetailModel(String nim) throws Exception {
        try {
            return service.table(detailSqlWithWilayah(nim), nim);
        } catch (SQLException ex) {
            if (isUnknownColumn(ex)) {
                return service.table(detailSqlFallback(nim), nim);
            }
            throw ex;
        }
    }

    private boolean isUnknownColumn(SQLException ex) {
        return ex.getErrorCode() == 1054
                || (ex.getMessage() != null && ex.getMessage().toLowerCase().contains("unknown column"));
    }

    private String detailSqlWithWilayah(String nim) {
        return """
                SELECT m.nim AS NIM, m.nama AS Nama, m.jenis_kelamin AS `Jenis Kelamin`,
                       DATE_FORMAT(m.tanggal_lahir, '%d-%m-%Y') AS `Tanggal Lahir`,
                       TIMESTAMPDIFF(YEAR, m.tanggal_lahir, CURDATE()) AS Usia,
                       CONCAT_WS(', ', m.alamat, wl.name, wk.name, wb.name, wp.name) AS `Alamat Lengkap`,
                       CONCAT_WS(', ', wl.name, wk.name, wb.name, wp.name) AS Wilayah,
                       p.kode_prodi AS `Kode Prodi`, p.nama_prodi AS Prodi, m.kode_kelas AS Kelas,
                       CAST(m.angkatan AS UNSIGNED) AS Angkatan, d.kode_dosen AS `Kode Dosen Wali`,
                       REPLACE(d.nama_dosen, '_', ' ') AS `Dosen Wali`, p.kode_dosen_prodi AS `Kode Kaprodi`,
                       REPLACE(kp.nama_dosen, '_', ' ') AS Kaprodi,
                       (SELECT ROUND(SUM(mk.sks * n.bobot) / NULLIF(SUM(mk.sks), 0), 2)
                        FROM nilai n
                        JOIN matakuliah mk ON mk.kode_mk = n.kode_mk
                        WHERE n.nim = m.nim) AS IPK,
                       m.status AS Status,
                       m.created_at AS `Tanggal Input`
                FROM mahasiswa m
                LEFT JOIN prodi p ON p.kode_prodi = m.kode_prodi
                LEFT JOIN dosen d ON d.kode_dosen = m.kode_dosen_wali
                LEFT JOIN dosen kp ON kp.kode_dosen = p.kode_dosen_prodi
                LEFT JOIN reg_provinces wp ON wp.id = m.kode_provinsi
                LEFT JOIN reg_regencies wb ON wb.id = m.kode_kabupaten
                LEFT JOIN reg_districts wk ON wk.id = m.kode_kecamatan
                LEFT JOIN reg_villages wl ON wl.id = m.kode_kelurahan
                WHERE m.nim = ?
                ORDER BY m.nim
                """;
    }

    private String detailSqlFallback(String nim) {
        return """
                SELECT m.nim AS NIM, m.nama AS Nama, m.jenis_kelamin AS `Jenis Kelamin`,
                       DATE_FORMAT(m.tanggal_lahir, '%d-%m-%Y') AS `Tanggal Lahir`,
                       TIMESTAMPDIFF(YEAR, m.tanggal_lahir, CURDATE()) AS Usia,
                       m.alamat AS `Alamat Lengkap`,
                       '-' AS Wilayah,
                       p.kode_prodi AS `Kode Prodi`, p.nama_prodi AS Prodi, m.kode_kelas AS Kelas,
                       CAST(m.angkatan AS UNSIGNED) AS Angkatan, d.kode_dosen AS `Kode Dosen Wali`,
                       REPLACE(d.nama_dosen, '_', ' ') AS `Dosen Wali`, p.kode_dosen_prodi AS `Kode Kaprodi`,
                       REPLACE(kp.nama_dosen, '_', ' ') AS Kaprodi,
                       (SELECT ROUND(SUM(mk.sks * n.bobot) / NULLIF(SUM(mk.sks), 0), 2)
                        FROM nilai n
                        JOIN matakuliah mk ON mk.kode_mk = n.kode_mk
                        WHERE n.nim = m.nim) AS IPK,
                       m.status AS Status,
                       m.created_at AS `Tanggal Input`
                FROM mahasiswa m
                LEFT JOIN prodi p ON p.kode_prodi = m.kode_prodi
                LEFT JOIN dosen d ON d.kode_dosen = m.kode_dosen_wali
                LEFT JOIN dosen kp ON kp.kode_dosen = p.kode_dosen_prodi
                WHERE m.nim = ?
                ORDER BY m.nim
                """;
    }

    private String getCurrentAcademicYear() {
        int currentYear = java.time.Year.now().getValue();
        int currentMonth = java.time.LocalDate.now().getMonthValue();
        int semester = currentMonth >= 8 ? 2 : 1; // Semester 2 mulai Agustus
        int academicYear = currentMonth >= 8 ? currentYear : currentYear - 1;
        return academicYear + "/" + (academicYear + 1) + " Semester " + semester;
    }

    private void selectProdiByName(String namaProdi) {
        for (int i = 0; i < prodiBox.getItemCount(); i++) {
            ProdiItem item = prodiBox.getItemAt(i);
            if (item.namaProdi().equals(namaProdi)) {
                prodiBox.setSelectedIndex(i);
                return;
            }
        }
    }

    private void selectDosenByName(String namaDosen) {
        String normalizedTarget = normalizeDosenName(namaDosen);
        for (int i = 0; i < dosenWaliBox.getItemCount(); i++) {
            DosenItem item = dosenWaliBox.getItemAt(i);
            if (normalizeDosenName(item.namaDosen()).equalsIgnoreCase(normalizedTarget)) {
                dosenWaliBox.setSelectedIndex(i);
                return;
            }
        }
    }

    private String normalizeDosenName(String value) {
        return value == null ? "" : value.replace('_', ' ').trim();
    }

    private int parseAngkatan(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Angkatan tidak valid.");
        }
        String digits = value.replaceAll("[^0-9]", "");
        if (digits.isEmpty()) {
            throw new IllegalArgumentException("Angkatan tidak valid.");
        }
        if (digits.length() >= 4) {
            return Integer.parseInt(digits.substring(0, 4));
        }
        return Integer.parseInt(digits);
    }

    private void maximizeDialog(javax.swing.JDialog dialog) {
        java.awt.Rectangle bounds = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getMaximumWindowBounds();
        dialog.setBounds(bounds);
    }

    private String[] classOptions(String kodeProdi, int angkatan) {
        String year = String.format("%02d", angkatan % 100);
        return switch (kodeProdi) {
            case "TIF" -> new String[]{
                    "TIF " + year + "A",
                    "TIF " + year + "B",
                    "TIF " + year + "C",
                    "TIF " + year + "D"
            };
            case "DKV" -> new String[]{
                    "DKV " + year + "A",
                    "DKV " + year + "B",
                    "DKV " + year + "C"
            };
            case "TI" -> new String[]{
                    "TI " + year + "E",
                    "TI " + year + "F"
            };
            default -> new String[]{
                    "RETAIL " + year + "A",
                    "RETAIL " + year + "B"
            };
        };
    }

    private String valueAt(int modelRow, String columnName) {
        int col = -1;
        for (int i = 0; i < table.getModel().getColumnCount(); i++) {
            if (columnName.equals(table.getModel().getColumnName(i))) {
                col = i;
                break;
            }
        }
        if (col < 0) {
            throw new IllegalArgumentException("Kolom tidak ditemukan: " + columnName);
        }
        Object value = table.getModel().getValueAt(modelRow, col);
        return value == null ? "" : String.valueOf(value);
    }
}
