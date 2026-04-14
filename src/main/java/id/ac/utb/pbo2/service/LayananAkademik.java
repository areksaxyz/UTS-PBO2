package id.ac.utb.pbo2.service;

import id.ac.utb.pbo2.db.BasisData;

import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class LayananAkademik {
    public DefaultTableModel table(String sql, Object... params) throws SQLException {
        try (Connection connection = BasisData.connection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            bind(statement, params);
            try (ResultSet resultSet = statement.executeQuery()) {
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();
                String[] columns = new String[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    columns[i - 1] = metaData.getColumnLabel(i);
                }
                DefaultTableModel model = new DefaultTableModel(columns, 0) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };
                while (resultSet.next()) {
                    Object[] row = new Object[columnCount];
                    for (int i = 1; i <= columnCount; i++) {
                        row[i - 1] = resultSet.getObject(i);
                    }
                    model.addRow(row);
                }
                return model;
            }
        }
    }

    public int count(String tableName) throws SQLException {
        String sql = "SELECT COUNT(*) FROM " + tableName;
        try (Connection connection = BasisData.connection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            resultSet.next();
            return resultSet.getInt(1);
        }
    }

    public int countKrsByNimSemester(String nim, int semester) throws SQLException {
        String sql = "SELECT COUNT(*) FROM krs WHERE nim = ? AND semester = ?";
        try (Connection connection = BasisData.connection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, nim);
            statement.setInt(2, semester);
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getInt(1);
            }
        }
    }

    public int maxKrsSemester(String nim) throws SQLException {
        String sql = "SELECT COALESCE(MAX(semester), 0) FROM krs WHERE nim = ?";
        try (Connection connection = BasisData.connection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, nim);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
                return 0;
            }
        }
    }

    public boolean mahasiswaExists(String nim) throws SQLException {
        return exists("SELECT 1 FROM mahasiswa WHERE nim = ?", nim);
    }

    public boolean dosenExists(String kodeDosen) throws SQLException {
        return exists("SELECT 1 FROM dosen WHERE kode_dosen = ?", kodeDosen);
    }

    public void addMahasiswa(String nim, String nama, String jenisKelamin, java.sql.Date tanggalLahir,
                             String alamat, String kodeProvinsi, String kodeKabupaten, String kodeKecamatan, String kodeKelurahan,
                             ProdiItem prodi, String kodeKelas, int angkatan, String kodeDosenWali)
            throws SQLException {
        if (kodeDosenWali.equals(prodi.kodeDosenProdi())) {
            throw new IllegalArgumentException("Dosen wali tidak boleh sama dengan Kaprodi.");
        }
        String sql = """
                INSERT INTO mahasiswa
                (nim, nama, jenis_kelamin, tanggal_lahir, alamat, kode_provinsi, kode_kabupaten, kode_kecamatan, kode_kelurahan,
                 kode_prodi, kode_kelas, kode_dosen_wali, angkatan, status)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'AKTIF')
                """;
        try (Connection connection = BasisData.connection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, nim);
            statement.setString(2, nama.trim());
            statement.setString(3, jenisKelamin);
            if (tanggalLahir != null) {
                statement.setDate(4, tanggalLahir);
            } else {
                statement.setNull(4, java.sql.Types.DATE);
            }
            statement.setString(5, alamat.trim());
            statement.setString(6, kodeProvinsi);
            statement.setString(7, kodeKabupaten);
            statement.setString(8, kodeKecamatan);
            statement.setString(9, kodeKelurahan);
            statement.setString(10, prodi.kodeProdi());
            statement.setString(11, kodeKelas);
            statement.setString(12, kodeDosenWali);
            statement.setInt(13, angkatan);
            statement.executeUpdate();
        }
    }

    public void deleteMahasiswa(String nim) throws SQLException {
        String sql = "DELETE FROM mahasiswa WHERE nim = ?";
        try (Connection connection = BasisData.connection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, nim);
            statement.executeUpdate();
        }
    }

    public List<CourseItem> coursesBySemester(int semester) throws SQLException {
        String sql = """
                SELECT kode_mk, nama_mk, sks, kode_dosen, kode_prodi
                FROM matakuliah
                WHERE semester = ?
                ORDER BY kode_mk
                """;
        List<CourseItem> result = new ArrayList<>();
        try (Connection connection = BasisData.connection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, semester);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    result.add(new CourseItem(
                            resultSet.getString("kode_mk"),
                            resultSet.getString("nama_mk"),
                            resultSet.getInt("sks"),
                            semester,
                            resultSet.getString("kode_dosen"),
                            resultSet.getString("kode_prodi")
                    ));
                }
            }
        }
        return result;
    }

    public List<CourseItem> coursesBySemester(String kodeProdi, int semester) throws SQLException {
        String sql = """
                SELECT kode_mk, nama_mk, sks, kode_dosen, kode_prodi
                FROM matakuliah
                WHERE semester = ? AND kode_prodi = ?
                ORDER BY kode_mk
                """;
        List<CourseItem> result = new ArrayList<>();
        try (Connection connection = BasisData.connection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, semester);
            statement.setString(2, kodeProdi);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    result.add(new CourseItem(
                            resultSet.getString("kode_mk"),
                            resultSet.getString("nama_mk"),
                            resultSet.getInt("sks"),
                            semester,
                            resultSet.getString("kode_dosen"),
                            resultSet.getString("kode_prodi")
                    ));
                }
            }
        }
        return result;
    }
    public List<StudentItem> students() throws SQLException {
        String sql = """
                SELECT m.nim, m.nama, m.kode_kelas, p.nama_prodi
                FROM mahasiswa m
                LEFT JOIN prodi p ON p.kode_prodi = m.kode_prodi
                ORDER BY m.nim
                """;
        List<StudentItem> result = new ArrayList<>();
        try (Connection connection = BasisData.connection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                result.add(new StudentItem(
                        resultSet.getString("nim"),
                        resultSet.getString("nama"),
                        resultSet.getString("kode_kelas"),
                        resultSet.getString("nama_prodi")
                ));
            }
        }
        return result;
    }

    public List<ProdiItem> prodiList() throws SQLException {
        String sql = """
                SELECT kode_prodi, nama_prodi, kode_kelas, kode_dosen_prodi
                FROM prodi
                ORDER BY FIELD(kode_prodi, 'TIF', 'TI', 'DKV', 'RETAIL'), kode_prodi
                """;
        List<ProdiItem> result = new ArrayList<>();
        try (Connection connection = BasisData.connection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                result.add(new ProdiItem(
                        resultSet.getString("kode_prodi"),
                        resultSet.getString("nama_prodi"),
                        resultSet.getString("kode_kelas"),
                        resultSet.getString("kode_dosen_prodi")
                ));
            }
        }
        return result;
    }

    public List<DosenItem> dosenList() throws SQLException {
        String sql = "SELECT kode_dosen, nama_dosen FROM dosen ORDER BY kode_dosen";
        List<DosenItem> result = new ArrayList<>();
        try (Connection connection = BasisData.connection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                result.add(new DosenItem(resultSet.getString("kode_dosen"), resultSet.getString("nama_dosen")));
            }
        }
        return result;
    }

    public List<CourseItem> coursesUpToSemester(int semester) throws SQLException {
        String sql = """
                SELECT kode_mk, nama_mk, sks, semester, kode_dosen, kode_prodi
                FROM matakuliah
                WHERE semester <= ?
                ORDER BY semester, kode_mk
                """;
        List<CourseItem> result = new ArrayList<>();
        try (Connection connection = BasisData.connection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, semester);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    result.add(new CourseItem(
                            resultSet.getString("kode_mk"),
                            resultSet.getString("nama_mk"),
                            resultSet.getInt("sks"),
                            resultSet.getInt("semester"),
                            resultSet.getString("kode_dosen"),
                            resultSet.getString("kode_prodi")
                    ));
                }
            }
        }
        return result;
    }

    public List<CourseItem> coursesUpToSemester(String kodeProdi, int semester) throws SQLException {
        String sql = """
                SELECT kode_mk, nama_mk, sks, semester, kode_dosen, kode_prodi
                FROM matakuliah
                WHERE semester <= ? AND kode_prodi = ?
                ORDER BY semester, kode_mk
                """;
        List<CourseItem> result = new ArrayList<>();
        try (Connection connection = BasisData.connection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, semester);
            statement.setString(2, kodeProdi);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    result.add(new CourseItem(
                            resultSet.getString("kode_mk"),
                            resultSet.getString("nama_mk"),
                            resultSet.getInt("sks"),
                            resultSet.getInt("semester"),
                            resultSet.getString("kode_dosen"),
                            resultSet.getString("kode_prodi")
                    ));
                }
            }
        }
        return result;
    }

    public KrsResult addKrs(String nim, String kodeMk, int semester, String tahunAkademik, int userId)
            throws SQLException {
        try (Connection connection = BasisData.connection()) {
            connection.setAutoCommit(false);
            try {
                CourseItem course = findCourse(connection, kodeMk);
                if (course == null) {
                    throw new SQLException("Mata kuliah tidak ditemukan.");
                }
                String studentProdi = studentProdi(connection, nim);
                if (!course.kodeProdi().equals(studentProdi)) {
                    throw new SQLException("Mata kuliah tidak sesuai prodi mahasiswa.");
                }
                if (course.semester() > semester) {
                    throw new SQLException("Mata kuliah semester " + course.semester()
                            + " belum bisa diambil pada semester " + semester + ".");
                }
                boolean mengulang = hasPreviousCourse(connection, nim, kodeMk, semester);
                boolean uktLunas = isUktPaid(connection, nim, semester, tahunAkademik);
                boolean accDosenWali = uktLunas && !mengulang;
                boolean accKaprodi = uktLunas && mengulang;
                String sql = """
                        INSERT INTO krs
                        (nim, kode_mk, kode_dosen, semester, tahun_akademik, is_mengulang,
                         ukt_lunas, acc_dosen_wali, acc_dosen_prodi, acc_at, created_by)
                        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, IF(? = 1, CURRENT_TIMESTAMP, NULL), ?)
                        """;
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setString(1, nim);
                    statement.setString(2, kodeMk);
                    statement.setString(3, course.kodeDosen());
                    statement.setInt(4, semester);
                    statement.setString(5, tahunAkademik);
                    statement.setBoolean(6, mengulang);
                    statement.setBoolean(7, uktLunas);
                    statement.setBoolean(8, accDosenWali);
                    statement.setBoolean(9, accKaprodi);
                    statement.setBoolean(10, uktLunas);
                    statement.setInt(11, userId);
                    statement.executeUpdate();
                }
                connection.commit();
                String message = uktLunas
                        ? "KRS berhasil disimpan dan otomatis ACC oleh "
                        + (accKaprodi ? "Kaprodi" : "Dosen Wali")
                        + " karena UKT sudah lunas."
                        : "KRS berhasil disimpan. Status menunggu pembayaran UKT.";
                return new KrsResult(true, mengulang, uktLunas, message);
            } catch (SQLException ex) {
                connection.rollback();
                throw ex;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    public KrsPackageResult addKrsPackage(String nim, int semester, String tahunAkademik, int userId)
            throws SQLException {
        List<CourseItem> courses = coursesBySemester(semester);
        if (courses.size() < 8) {
            throw new SQLException("Data mata kuliah semester " + semester + " kurang dari 8.");
        }

        int inserted = 0;
        int skipped = 0;
        int repeated = 0;
        for (CourseItem course : courses) {
            try {
                KrsResult result = addKrs(nim, course.kodeMk(), semester, tahunAkademik, userId);
                if (result.mengulang()) {
                    repeated++;
                }
                inserted++;
            } catch (SQLException ex) {
                if (isDuplicateError(ex)) {
                    skipped++;
                } else {
                    throw ex;
                }
            }
        }
        return new KrsPackageResult(inserted, skipped, repeated);
    }

    public void saveNilaiKomponen(int krsId, double nilaiAbsensi, double nilaiTugas,
                                  double nilaiKuis, double nilaiUts, double nilaiUas, int userId)
            throws SQLException {
        validateScore("Nilai absensi", nilaiAbsensi);
        validateScore("Nilai tugas", nilaiTugas);
        validateScore("Nilai kuis", nilaiKuis);
        validateScore("Nilai UTS", nilaiUts);
        validateScore("Nilai UAS", nilaiUas);
        double nilaiAkhir = calculateNilaiAkhir(nilaiAbsensi, nilaiTugas, nilaiKuis, nilaiUts, nilaiUas);
        String nilaiHuruf = gradeFromAkhir(nilaiAkhir);
        double bobot = gradeToBobot(nilaiHuruf);

        String selectSql = """
                SELECT id, nim, kode_mk, kode_dosen, semester, tahun_akademik
                FROM krs
                WHERE id = ?
                """;
        try (Connection connection = BasisData.connection()) {
            connection.setAutoCommit(false);
            try {
                String nim;
                String kodeMk;
                String kodeDosen;
                int semester;
                String tahunAkademik;
                try (PreparedStatement select = connection.prepareStatement(selectSql)) {
                    select.setInt(1, krsId);
                    try (ResultSet resultSet = select.executeQuery()) {
                        if (!resultSet.next()) {
                            throw new SQLException("KRS tidak ditemukan.");
                        }
                        nim = resultSet.getString("nim");
                        kodeMk = resultSet.getString("kode_mk");
                        kodeDosen = resultSet.getString("kode_dosen");
                        semester = resultSet.getInt("semester");
                        tahunAkademik = resultSet.getString("tahun_akademik");
                    }
                }

                String upsertSql = """
                        INSERT INTO nilai
                        (krs_id, nim, kode_mk, kode_dosen, semester, tahun_akademik,
                         nilai_absensi, nilai_tugas, nilai_kuis, nilai_uts, nilai_uas,
                         nilai_akhir, nilai_huruf, bobot, created_by)
                        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                        ON DUPLICATE KEY UPDATE
                            nilai_absensi = VALUES(nilai_absensi),
                            nilai_tugas = VALUES(nilai_tugas),
                            nilai_kuis = VALUES(nilai_kuis),
                            nilai_uts = VALUES(nilai_uts),
                            nilai_uas = VALUES(nilai_uas),
                            nilai_akhir = VALUES(nilai_akhir),
                            nilai_huruf = VALUES(nilai_huruf),
                            bobot = VALUES(bobot),
                            updated_at = CURRENT_TIMESTAMP
                        """;
                try (PreparedStatement upsert = connection.prepareStatement(upsertSql)) {
                    upsert.setInt(1, krsId);
                    upsert.setString(2, nim);
                    upsert.setString(3, kodeMk);
                    upsert.setString(4, kodeDosen);
                    upsert.setInt(5, semester);
                    upsert.setString(6, tahunAkademik);
                    upsert.setDouble(7, nilaiAbsensi);
                    upsert.setDouble(8, nilaiTugas);
                    upsert.setDouble(9, nilaiKuis);
                    upsert.setDouble(10, nilaiUts);
                    upsert.setDouble(11, nilaiUas);
                    upsert.setDouble(12, nilaiAkhir);
                    upsert.setString(13, nilaiHuruf);
                    upsert.setDouble(14, bobot);
                    upsert.setInt(15, userId);
                    upsert.executeUpdate();
                }
                connection.commit();
            } catch (SQLException ex) {
                connection.rollback();
                throw ex;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    public static void validateNimSearch(String nim) {
        if (!nim.matches("\\d*")) {
            throw new IllegalArgumentException("NIM hanya boleh angka.");
        }
        if (nim.length() < 5) {
            throw new IllegalArgumentException("Maaf minimal input angka adalah 5.");
        }
        if (nim.length() > 5) {
            throw new IllegalArgumentException("NIM maksimal 5 angka.");
        }
    }

    public static void validateNim(String nim) {
        if (!nim.matches("\\d{5}")) {
            throw new IllegalArgumentException("NIM wajib 5 angka.");
        }
    }

    public static void validateSemester(int semester) {
        if (semester < 1 || semester > 8) {
            throw new IllegalArgumentException("Semester maksimal adalah 8 dan minimal 1.");
        }
    }

    public double gradeToBobot(String grade) {
        return switch (grade) {
            case "A" -> 4;
            case "AB" -> 3.5;
            case "B" -> 3;
            case "BC" -> 2.5;
            case "C" -> 2;
            case "D" -> 1;
            default -> 0;
        };
    }

    public double calculateNilaiAkhir(double nilaiAbsensi, double nilaiTugas,
                                      double nilaiKuis, double nilaiUts, double nilaiUas) {
        double hasil = (nilaiUas * 0.30)
                + (nilaiUts * 0.25)
                + (nilaiTugas * 0.20)
                + (nilaiKuis * 0.10)
                + (nilaiAbsensi * 0.15);
        return Math.round(hasil * 100.0) / 100.0;
    }

    public String gradeFromAkhir(double nilaiAkhir) {
        if (nilaiAkhir >= 85) {
            return "A";
        }
        if (nilaiAkhir >= 80) {
            return "AB";
        }
        if (nilaiAkhir >= 75) {
            return "B";
        }
        if (nilaiAkhir >= 70) {
            return "BC";
        }
        if (nilaiAkhir >= 60) {
            return "C";
        }
        if (nilaiAkhir >= 50) {
            return "D";
        }
        return "E";
    }

    private void validateScore(String fieldName, double value) {
        if (value < 0 || value > 100) {
            throw new IllegalArgumentException(fieldName + " harus 0 sampai 100.");
        }
    }

    private boolean exists(String sql, Object param) throws SQLException {
        try (Connection connection = BasisData.connection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, param);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    private CourseItem findCourse(Connection connection, String kodeMk) throws SQLException {
        String sql = "SELECT kode_mk, nama_mk, sks, semester, kode_dosen, kode_prodi FROM matakuliah WHERE kode_mk = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, kodeMk);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }
                return new CourseItem(
                        resultSet.getString("kode_mk"),
                        resultSet.getString("nama_mk"),
                        resultSet.getInt("sks"),
                        resultSet.getInt("semester"),
                        resultSet.getString("kode_dosen"),
                        resultSet.getString("kode_prodi")
                );
            }
        }
    }

    public String studentProdi(String nim) throws SQLException {
        String sql = "SELECT kode_prodi FROM mahasiswa WHERE nim = ?";
        try (Connection connection = BasisData.connection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, nim);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("kode_prodi");
                }
            }
        }
        throw new SQLException("Prodi mahasiswa tidak ditemukan.");
    }

    private String studentProdi(Connection connection, String nim) throws SQLException {
        String sql = "SELECT kode_prodi FROM mahasiswa WHERE nim = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, nim);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("kode_prodi");
                }
            }
        }
        throw new SQLException("Prodi mahasiswa tidak ditemukan.");
    }

    public int sumSksByNimSemester(String nim, int semester) throws SQLException {
        String sql = """
                SELECT COALESCE(SUM(mk.sks), 0) AS total
                FROM krs k
                JOIN matakuliah mk ON mk.kode_mk = k.kode_mk
                WHERE k.nim = ? AND k.semester = ?
                """;
        try (Connection connection = BasisData.connection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, nim);
            statement.setInt(2, semester);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("total");
                }
            }
        }
        return 0;
    }

    private boolean hasPreviousCourse(Connection connection, String nim, String kodeMk, int semester)
            throws SQLException {
        String sql = "SELECT 1 FROM krs WHERE nim = ? AND kode_mk = ? AND semester < ? LIMIT 1";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, nim);
            statement.setString(2, kodeMk);
            statement.setInt(3, semester);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    private boolean isUktPaid(Connection connection, String nim, int semester, String tahunAkademik)
            throws SQLException {
        String sql = """
                SELECT 1 FROM pembayaran_ukt
                WHERE nim = ? AND semester = ? AND tahun_akademik = ? AND status_lunas = 1
                LIMIT 1
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, nim);
            statement.setInt(2, semester);
            statement.setString(3, tahunAkademik);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    private void bind(PreparedStatement statement, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            statement.setObject(i + 1, params[i]);
        }
    }

    private boolean isDuplicateError(SQLException ex) {
        return ex.getErrorCode() == 1062;
    }

    public record CourseItem(String kodeMk, String namaMk, int sks, int semester,
                             String kodeDosen, String kodeProdi) {
        @Override
        public String toString() {
            return kodeMk + " - " + namaMk + " (S" + semester + ", " + sks + " SKS)";
        }
    }

    public record StudentItem(String nim, String nama, String kodeKelas, String namaProdi) {
        @Override
        public String toString() {
            return nim + " - " + nama + " (" + kodeKelas + ")";
        }
    }

    public record ProdiItem(String kodeProdi, String namaProdi, String kodeKelas, String kodeDosenProdi) {
        @Override
        public String toString() {
            return kodeKelas + " - " + namaProdi;
        }
    }

    public record DosenItem(String kodeDosen, String namaDosen) {
        @Override
        public String toString() {
            return kodeDosen + " - " + namaDosen;
        }
    }

    public record KrsResult(boolean success, boolean mengulang, boolean uktLunas, String message) {
    }

    public record KrsPackageResult(int inserted, int skipped, int repeated) {
    }

    public List<java.util.Map<String, String>> getWilayahProvinsi() throws SQLException {
        String sql = "SELECT id as kode, name as nama FROM reg_provinces ORDER BY name";
        return getWilayahData(sql);
    }

    public List<java.util.Map<String, String>> getWilayahKabupaten(String kodeProvinsi) throws SQLException {
        String sql = "SELECT id as kode, name as nama FROM reg_regencies WHERE province_id = ? ORDER BY name";
        return getWilayahData(sql, kodeProvinsi);
    }

    public List<java.util.Map<String, String>> getWilayahKecamatan(String kodeKabupaten) throws SQLException {
        String sql = "SELECT id as kode, name as nama FROM reg_districts WHERE regency_id = ? ORDER BY name";
        return getWilayahData(sql, kodeKabupaten);
    }

    public List<java.util.Map<String, String>> getWilayahKelurahan(String kodeKecamatan) throws SQLException {
        String sql = "SELECT id as kode, name as nama FROM reg_villages WHERE district_id = ? ORDER BY name";
        return getWilayahData(sql, kodeKecamatan);
    }

    private List<java.util.Map<String, String>> getWilayahData(String sql, Object... params) throws SQLException {
        List<java.util.Map<String, String>> result = new ArrayList<>();
        try (Connection connection = BasisData.connection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            bind(statement, params);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    java.util.Map<String, String> item = new java.util.HashMap<>();
                    item.put("kode", resultSet.getString("kode"));
                    item.put("nama", resultSet.getString("nama"));
                    result.add(item);
                }
            }
        }
        return result;
    }
}
