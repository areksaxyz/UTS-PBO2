package id.ac.utb.pbo2.ui;

import id.ac.utb.pbo2.model.PenggunaSaatIni;
import id.ac.utb.pbo2.service.LayananAkademik;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

public class NilaiPanel extends JPanel {
    private final LayananAkademik service = new LayananAkademik();
    private final PenggunaSaatIni user;
    private final JTextField nimField = Theme.textField(8);
    private final JTextField absensiField = Theme.textField(6);
    private final JTextField tugasField = Theme.textField(6);
    private final JTextField quizField = Theme.textField(6);
    private final JTextField utsField = Theme.textField(6);
    private final JTextField uasField = Theme.textField(6);
    private final JTextField akhirField = Theme.textField(6);
    private final JTable krsTable = Theme.table();
    private final JTable inputTable = Theme.table();

    public NilaiPanel(PenggunaSaatIni user) {
        this.user = user;
        nimField.setEditable(true);
        akhirField.setEditable(false);
        installNumericFilter(absensiField);
        installNumericFilter(tugasField);
        installNumericFilter(quizField);
        installNumericFilter(utsField);
        installNumericFilter(uasField);
        nimField.addActionListener(event -> loadNilai());

        setLayout(new BorderLayout(0, 14));
        setBackground(Theme.BACKGROUND);
        setBorder(Theme.page().getBorder());

        add(header(), BorderLayout.NORTH);
        add(body(), BorderLayout.CENTER);

        krsTable.getSelectionModel().addListSelectionListener(event -> {
            if (event.getValueIsAdjusting()) {
                return;
            }
            int row = krsTable.getSelectedRow();
            if (row < 0) {
                return;
            }
            int modelRow = krsTable.convertRowIndexToModel(row);
            nimField.setText(String.valueOf(krsTable.getModel().getValueAt(modelRow, 1)));
            setField(absensiField, krsTable.getModel().getValueAt(modelRow, 10));
            setField(tugasField, krsTable.getModel().getValueAt(modelRow, 11));
            setField(quizField, krsTable.getModel().getValueAt(modelRow, 12));
            setField(utsField, krsTable.getModel().getValueAt(modelRow, 13));
            setField(uasField, krsTable.getModel().getValueAt(modelRow, 14));
            setField(akhirField, krsTable.getModel().getValueAt(modelRow, 15));
        });
    }

    private JPanel header() {
        JPanel panel = new JPanel(new BorderLayout(12, 8));
        panel.setOpaque(false);
        JPanel title = new JPanel(new BorderLayout());
        title.setOpaque(false);
        title.add(Theme.title("Transaksi Nilai"), BorderLayout.NORTH);
        title.add(Theme.muted("UAS 30%, UTS 25%, Tugas 20%, Kuis 10%, Absensi 15%."), BorderLayout.CENTER);
        panel.add(title, BorderLayout.WEST);

        JPanel search = new JPanel(new GridLayout(1, 2, 8, 0));
        search.setOpaque(false);
        JButton searchButton = Theme.primaryButton("Cari KRS");
        searchButton.addActionListener(event -> loadNilai());
        search.add(searchButton);
        JButton clearSearchButton = Theme.secondaryButton("Reset");
        clearSearchButton.addActionListener(event -> closeForm());
        search.add(clearSearchButton);
        panel.add(search, BorderLayout.CENTER);
        return panel;
    }

    private JPanel body() {
        JPanel root = new JPanel(new BorderLayout(0, 12));
        root.setOpaque(false);

        JPanel input = Theme.surface();
        input.setLayout(new BorderLayout(12, 8));
        input.add(Theme.sectionTitle("Form Input Nilai"), BorderLayout.NORTH);
        input.add(inputForm(), BorderLayout.CENTER);
        root.add(input, BorderLayout.NORTH);

        JPanel tables = new JPanel(new GridLayout(2, 1, 0, 12));
        tables.setOpaque(false);
        tables.add(Theme.tableScroll(krsTable));
        tables.add(Theme.tableScroll(inputTable));
        root.add(tables, BorderLayout.CENTER);
        return root;
    }

    private JPanel inputForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        int row = 0;
        row = addField(form, gbc, row, "NIM", nimField);
        row = addField(form, gbc, row, "Nilai Absensi", absensiField);
        row = addField(form, gbc, row, "Nilai Tugas", tugasField);
        row = addField(form, gbc, row, "Nilai Quiz", quizField);
        row = addField(form, gbc, row, "Nilai UTS", utsField);
        row = addField(form, gbc, row, "Nilai UAS", uasField);
        row = addField(form, gbc, row, "Nilai Akhir", akhirField);
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        form.add(Theme.muted("Grade dihitung otomatis saat nilai disimpan."), gbc);

        JPanel actions = new JPanel(new GridLayout(1, 3, 8, 0));
        actions.setOpaque(false);
        JButton inputButton = Theme.primaryButton("Input");
        inputButton.addActionListener(event -> saveNilai());
        JButton clearButton = Theme.secondaryButton("Clear");
        clearButton.addActionListener(event -> clearInput());
        JButton closeButton = Theme.secondaryButton("Close");
        closeButton.addActionListener(event -> closeForm());
        actions.add(inputButton);
        actions.add(clearButton);
        actions.add(closeButton);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        form.add(actions, gbc);
        return form;
    }

    private int addField(JPanel panel, GridBagConstraints gbc, int row, String label, JTextField field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        panel.add(field, gbc);
        return row + 1;
    }

    private void loadNilai() {
        String nim = nimField.getText().trim();
        try {
            LayananAkademik.validateNimSearch(nim);
            if (!service.mahasiswaExists(nim)) {
                Theme.info(this, "Maaf data tersebut tidak ada.");
                return;
            }
            nimField.setText(nim);
            krsTable.setModel(service.table("""
                    SELECT krs_id AS `ID KRS`, nim AS NIM, nama AS Nama, kelas_semester AS Kelas,
                           nama_prodi AS Prodi, kode_mk AS Kode, nama_mk AS `Mata Kuliah`, sks AS SKS,
                           semester AS Semester, tahun_akademik AS Tahun,
                           nilai_absensi AS Absensi, nilai_tugas AS Tugas, nilai_kuis AS Quiz,
                           nilai_uts AS UTS, nilai_uas AS UAS, nilai_akhir AS Akhir, nilai_huruf AS Grade
                    FROM v_nilai_detail
                    WHERE nim = ?
                    ORDER BY semester, kode_mk
                    """, nim));
            loadInputData(nim);
        } catch (Exception ex) {
            Theme.error(this, ex);
        }
    }

    private void saveNilai() {
        int row = krsTable.getSelectedRow();
        if (row < 0 && krsTable.getRowCount() > 0) {
            row = 0;
            krsTable.setRowSelectionInterval(0, 0);
        }
        if (row < 0) {
            Theme.info(this, "Pilih baris KRS terlebih dahulu.");
            return;
        }
        try {
            int modelRow = krsTable.convertRowIndexToModel(row);
            int krsId = Integer.parseInt(String.valueOf(krsTable.getModel().getValueAt(modelRow, 0)));
            double absensi = parseScore(absensiField.getText(), "Nilai absensi");
            double tugas = parseScore(tugasField.getText(), "Nilai tugas");
            double quiz = parseScore(quizField.getText(), "Nilai quiz");
            double uts = parseScore(utsField.getText(), "Nilai UTS");
            double uas = parseScore(uasField.getText(), "Nilai UAS");
            service.saveNilaiKomponen(krsId, absensi, tugas, quiz, uts, uas, user.getId());

            double akhir = service.calculateNilaiAkhir(absensi, tugas, quiz, uts, uas);
            String huruf = service.gradeFromAkhir(akhir);
            akhirField.setText(String.format("%.2f", akhir));

            Theme.info(this, "Nilai berhasil disimpan. Grade otomatis: " + huruf + ".");
            loadNilai();
        } catch (Exception ex) {
            Theme.error(this, ex);
        }
    }

    private void loadInputData(String nim) throws Exception {
        inputTable.setModel(service.table("""
                SELECT nim AS NIM, nilai_absensi AS Absensi, nilai_tugas AS Tugas,
                       nilai_kuis AS Quiz, nilai_uts AS UTS, nilai_uas AS UAS,
                       nilai_akhir AS Akhir, nilai_huruf AS Grade
                FROM v_nilai_detail
                WHERE nim = ?
                ORDER BY semester, kode_mk
                """, nim));
    }

    private double parseScore(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " wajib diisi.");
        }
        double parsed;
        try {
            parsed = Double.parseDouble(value.trim().replace(',', '.'));
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(fieldName + " harus berupa angka.");
        }
        if (parsed < 0 || parsed > 100) {
            throw new IllegalArgumentException(fieldName + " harus 0 sampai 100.");
        }
        return parsed;
    }

    private void setField(JTextField field, Object value) {
        if (value == null) {
            field.setText("");
            return;
        }
        String text = String.valueOf(value);
        field.setText("null".equalsIgnoreCase(text) ? "" : text);
    }

    private void clearInput() {
        absensiField.setText("");
        tugasField.setText("");
        quizField.setText("");
        utsField.setText("");
        uasField.setText("");
        akhirField.setText("");
        krsTable.clearSelection();
    }

    private void closeForm() {
        clearInput();
        nimField.setText("");
        krsTable.setModel(new javax.swing.table.DefaultTableModel());
        inputTable.setModel(new javax.swing.table.DefaultTableModel());
    }

    private void installNumericFilter(JTextField field) {
        if (field.getDocument() instanceof AbstractDocument document) {
            document.setDocumentFilter(new ScoreDocumentFilter());
        }
    }

    private static final class ScoreDocumentFilter extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                throws BadLocationException {
            replace(fb, offset, 0, string, attr);
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                throws BadLocationException {
            String current = fb.getDocument().getText(0, fb.getDocument().getLength());
            String incoming = text == null ? "" : text;
            String candidate = current.substring(0, offset) + incoming + current.substring(offset + length);
            if (isValid(candidate)) {
                super.replace(fb, offset, length, text, attrs);
            } else {
                Toolkit.getDefaultToolkit().beep();
            }
        }

        private boolean isValid(String value) {
            if (value == null || value.isBlank()) {
                return true;
            }
            if (!value.matches("\\d{0,3}([\\.,]\\d{0,2})?")) {
                return false;
            }
            if (value.endsWith(".") || value.endsWith(",")) {
                return true;
            }
            try {
                double parsed = Double.parseDouble(value.replace(',', '.'));
                return parsed <= 100;
            } catch (NumberFormatException ex) {
                return false;
            }
        }
    }
}
