package id.ac.utb.pbo2.db;

import id.ac.utb.pbo2.config.AppConfig;
import id.ac.utb.pbo2.util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public final class DatabaseBootstrap {
    private static final String[] FIRST_NAMES = {
            "Aditya", "Bella", "Candra", "Dewi", "Eka", "Farhan", "Gita", "Hafiz", "Indah", "Joko",
            "Kartika", "Laras", "Muhammad", "Nabila", "Oki", "Pandu", "Qori", "Rafi", "Salsa", "Tegar",
            "Ulfa", "Vina", "Wahyu", "Yuni", "Agus", "Rina", "Siti", "Dedi", "Maya", "Budi",
            "Nadia", "Rizky", "Fitri", "Hendra", "Lukman", "Aulia", "Putri", "Fajar", "Citra", "Rama"
    };
    private static final String[] LAST_NAMES = {
            "Pratama", "Safitri", "Wijaya", "Lestari", "Saputra", "Maulana", "Amelia", "Ramadhan", "Puspita", "Nugraha",
            "Sari", "Wulandari", "Iqbal", "Putri", "Firmansyah", "Herlambang", "Azizah", "Alfarizi", "Maharani", "Pamungkas",
            "Nuraini", "Apriliyani", "Santoso", "Ridwan", "Setiawan", "Rahman", "Nugroho", "Permata", "Hakim", "Kartika"
    };
    private static final String[] STREET_NAMES = {
            "Asia Afrika", "Cibaduyut Raya", "Buah Batu", "Dipatiukur", "Soekarno Hatta", "Antapani Lama",
            "Gegerkalong Hilir", "Cikutra Barat", "Setiabudi", "Pasteur", "Kebon Jati", "Sudirman",
            "Merdeka", "Dago", "Pahlawan", "Terusan Buah Batu", "Karapitan", "Braga"
    };
    private static final String[] KEAHLIAN = {
            "Pemrograman Berorientasi Objek", "Basis Data", "Algoritma", "Jaringan Komputer",
            "Sistem Operasi", "Rekayasa Perangkat Lunak", "Kecerdasan Buatan", "Keamanan Informasi",
            "Interaksi Manusia Komputer", "Cloud Computing", "Data Mining", "Machine Learning",
            "Sistem Produksi", "Ergonomi Industri", "Manajemen Operasi", "Perencanaan Produksi",
            "Manajemen Rantai Pasok", "Pengendalian Kualitas", "Optimasi Industri", "Keselamatan Kerja",
            "Desain Grafis", "Tipografi", "Ilustrasi Digital", "Fotografi",
            "UI UX Design", "Motion Graphics", "Branding dan Identitas Visual", "Art Direction",
            "Manajemen Retail", "Merchandising", "Digital Marketing Retail", "Analitik Penjualan",
            "Customer Relationship Management", "Manajemen Gudang", "Kewirausahaan", "Etika Profesi"
    };

    private static final String[][] TIF_COURSE_NAMES = {
            {"Algoritma Dasar", "Matematika Diskrit", "Pengantar Teknologi Informasi", "Logika Informatika",
                    "Bahasa Inggris TI", "Dasar Basis Data", "Praktikum Algoritma", "Pendidikan Pancasila"},
            {"Struktur Data", "Pemrograman Dasar", "Aljabar Linear", "Sistem Digital",
                    "Statistika Dasar", "Komunikasi Data", "Praktikum Basis Data", "Kewarganegaraan"},
            {"Pemrograman Berorientasi Objek 1", "Basis Data Lanjut", "Jaringan Komputer",
                    "Sistem Operasi", "Analisis Sistem", "Pemrograman Web 1", "Probabilitas",
                    "Praktikum Jaringan"},
            {"Pemrograman Berorientasi Objek 2", "Rekayasa Perangkat Lunak", "Pemrograman Web 2",
                    "Interaksi Manusia Komputer", "Manajemen Basis Data", "Komputasi Numerik",
                    "Praktikum PBO", "Etika Profesi"},
            {"Mobile Programming", "Data Mining", "Kecerdasan Buatan", "Keamanan Informasi",
                    "Cloud Computing", "Pengujian Perangkat Lunak", "Arsitektur Enterprise", "Praktikum Mobile"},
            {"Sistem Terdistribusi", "Machine Learning", "Manajemen Proyek TI", "Big Data",
                    "DevOps", "Audit Sistem Informasi", "UI UX Design", "Praktikum Machine Learning"},
            {"Metodologi Penelitian", "Teknopreneurship", "Pemrograman Game", "Internet of Things",
                    "Computer Vision", "Natural Language Processing", "Sistem Pendukung Keputusan", "Kerja Praktik"},
            {"Skripsi", "Seminar Proposal", "Kapita Selekta", "Profesional TI",
                    "Data Warehouse", "Forensik Digital", "Enterprise Security", "Manajemen Layanan TI"}
    };

    private static final String[][] TI_COURSE_NAMES = {
            {"Pengantar Teknik Industri", "Gambar Teknik Industri", "Fisika Dasar Industri", "Kimia Industri",
                    "Kalkulus Industri", "Pengantar Manajemen", "Ergonomi Dasar", "Pendidikan Pancasila"},
            {"Statistika Industri", "Mekanika Teknik", "Proses Manufaktur", "Analisis Biaya Industri",
                    "Riset Operasi Dasar", "Ekonomi Teknik", "Praktikum Manufaktur", "Kewarganegaraan"},
            {"Perencanaan Produksi", "Perancangan Sistem Kerja", "Pengendalian Kualitas",
                    "Simulasi Sistem Industri", "Manajemen Rantai Pasok", "Pemodelan Sistem",
                    "Probabilitas Industri", "Praktikum Ergonomi"},
            {"Perencanaan Tata Letak Fasilitas", "Sistem Logistik", "Pengendalian Persediaan",
                    "Otomasi Industri", "Manajemen Proyek Industri", "Analisis Keputusan",
                    "Praktikum Pengukuran Kerja", "Etika Profesi Industri"},
            {"Lean Manufacturing", "Six Sigma", "Manajemen Pemeliharaan", "Sistem Informasi Industri",
                    "Human Factor Engineering", "Peramalan Industri", "Keselamatan Kesehatan Kerja",
                    "Praktikum Six Sigma"},
            {"Sistem Terintegrasi", "Perencanaan Kapasitas", "Analisis Risiko Operasional",
                    "Manajemen Mutu Terpadu", "Optimasi Rantai Pasok", "Audit Energi Industri",
                    "Praktikum Simulasi", "Kewirausahaan Industri"},
            {"Metodologi Penelitian Industri", "Manajemen Inovasi Produk", "Analitik Bisnis Industri",
                    "Sistem Produksi Berkelanjutan", "Perencanaan Strategis Industri", "Praktik Lapangan Industri",
                    "Seminar Industri", "Kapita Selekta Industri"},
            {"Tugas Akhir Teknik Industri", "Seminar Proposal Industri", "Manajemen Operasi Lanjut",
                    "Sistem Enterprise Industri", "Analisis Data Industri", "Pengambilan Keputusan Multikriteria",
                    "Profesional Teknik Industri", "Manajemen Layanan Industri"}
    };

    private static final String[][] DKV_COURSE_NAMES = {
            {"Dasar-dasar Desain", "Nirmana 2D", "Gambar Bentuk", "Tipografi Dasar",
                    "Sejarah Seni Rupa", "Pengantar DKV", "Bahasa Inggris Desain", "Pendidikan Pancasila"},
            {"Nirmana 3D", "Ilustrasi Dasar", "Fotografi Dasar", "Tipografi Lanjut",
                    "Komposisi Visual", "Komunikasi Visual", "Praktikum Fotografi", "Kewarganegaraan"},
            {"Desain Grafis 1", "Ilustrasi Digital", "Branding Dasar", "Desain Editorial",
                    "Animasi Dasar", "Psikologi Persepsi", "Metode Kreatif", "Praktikum Desain Grafis"},
            {"Desain Grafis 2", "Desain UI Dasar", "Motion Graphics", "Desain Kemasan",
                    "Desain Iklan", "Manajemen Produksi Kreatif", "Etika Profesi Desain", "Praktikum Motion"},
            {"Desain UI UX Lanjut", "Videografi", "Copywriting Kreatif", "Desain Identitas Visual",
                    "Strategi Branding", "Desain Infografis", "Riset Desain", "Praktikum UI UX"},
            {"Desain Pengalaman Pengguna", "Creative Entrepreneurship", "Art Direction",
                    "Desain Kampanye Digital", "Storytelling Visual", "Produksi Konten Multimedia",
                    "Hukum Hak Cipta", "Praktikum Kampanye"},
            {"Metodologi Penelitian Desain", "Desain Service Experience", "Portofolio Profesional",
                    "Manajemen Studio Kreatif", "Desain Berbasis Data", "Praktik Studio DKV",
                    "Seminar DKV", "Kapita Selekta DKV"},
            {"Tugas Akhir DKV", "Seminar Proposal DKV", "Kurasi Portofolio",
                    "Strategi Komunikasi Merek", "Desain Sosial", "Trend Forecasting",
                    "Profesional Kreatif", "Manajemen Proyek Kreatif"}
    };

    private static final String[][] RETAIL_COURSE_NAMES = {
            {"Pengantar Bisnis Retail", "Dasar Akuntansi Retail", "Perilaku Konsumen",
                    "Matematika Bisnis", "Pengantar Manajemen Retail", "Komunikasi Bisnis",
                    "Bahasa Inggris Retail", "Pendidikan Pancasila"},
            {"Merchandising Dasar", "Dasar Pemasaran", "Pengantar Ekonomi",
                    "Sistem Informasi Retail", "Manajemen Toko", "Statistika Bisnis",
                    "Praktikum Display Toko", "Kewarganegaraan"},
            {"Manajemen Persediaan", "Rantai Pasok Retail", "Category Management",
                    "E-commerce Retail", "Customer Relationship Management", "Analisis Data Penjualan",
                    "Metode Riset Pasar", "Praktikum Kasir dan POS"},
            {"Visual Merchandising", "Manajemen Operasional Retail", "Manajemen Gudang",
                    "Digital Marketing Retail", "Manajemen Keuangan Retail", "Perencanaan Promosi",
                    "Etika Bisnis Retail", "Praktikum Omnichannel"},
            {"Retail Analytics", "Manajemen Franchising", "Buying and Planning",
                    "Strategi Harga Retail", "Manajemen SDM Retail", "Manajemen Risiko Retail",
                    "Perilaku Shopper", "Praktikum Analitik Retail"},
            {"Supply Chain Lanjut", "Retail Technology", "Store Layout Planning",
                    "Manajemen Merek Privat", "Negosiasi Pemasok", "Manajemen Kinerja Toko",
                    "Kewirausahaan Retail", "Praktikum Strategi Harga"},
            {"Metodologi Penelitian Bisnis Retail", "Manajemen Proyek Retail", "Retail Sustainability",
                    "Strategi Omnichannel", "Analisis Lokasi Toko", "Praktik Lapangan Retail",
                    "Seminar Retail", "Kapita Selekta Retail"},
            {"Tugas Akhir Retail", "Seminar Proposal Retail", "Strategi Ekspansi Retail",
                    "Manajemen Layanan Pelanggan", "Business Intelligence Retail", "Hukum Perdagangan",
                    "Profesional Retail", "Manajemen Inovasi Retail"}
    };

    private static final String[][] PRODI = {
            {"TIF", "Teknik Informatika", "TIF", "DSN013"},
            {"TI", "Teknik Industri", "TI", "DSN014"},
            {"DKV", "DKV", "DKV", "DSN015"},
            {"RETAIL", "Retail", "RETAIL", "DSN016"}
    };

    public interface ProgressListener {
        void message(String message);
    }

    private DatabaseBootstrap() {
    }

    public static void ensureDatabase() throws SQLException {
        ensureDatabase(null);
    }

    public static void ensureDatabase(ProgressListener listener) throws SQLException {
        report(listener, "Membuka koneksi BasisData...");
        try (Connection server = BasisData.serverConnection();
             Statement statement = server.createStatement()) {
            statement.executeUpdate("CREATE DATABASE IF NOT EXISTS " + AppConfig.DB_NAME
                    + " CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
        }

        try (Connection connection = BasisData.connection()) {
            report(listener, "Membuat schema dan view...");
            createTables(connection);
            normalizeMatakuliahNames(connection);
            ensureWilayahData(connection);
            createViews(connection);

            if (count(connection, "users") == 0) {
                seedUsers(connection);
            }

            if (needsCoreSeed(connection)) {
                report(listener, "Melengkapi data inti...");
                resetSeedDataIfNeeded(connection);
                seedDosen(connection);
                seedProdi(connection);
                seedMahasiswa(connection);
                updateDosenJabatan(connection);
                seedMataKuliah(connection);
                seedPembayaranUkt(connection);
                seedKrsAndNilai(connection);
            } else {
                report(listener, "Sinkronisasi data referensi...");
                seedDosen(connection);
                seedMataKuliah(connection);
                updateDosenJabatan(connection);
            }
            ensureMahasiswaBirthDates(connection);
            ensureMahasiswaWilayah(connection);
            ensureNilaiVariation(connection);
            report(listener, "Database sudah siap.");
        }
    }

    private static void report(ProgressListener listener, String message) {
        if (listener != null) {
            listener.message(message);
        }
    }

    private static void resetSeedDataIfNeeded(Connection connection) throws SQLException {
        int dosenCount = count(connection, "dosen");
        int mahasiswaCount = count(connection, "mahasiswa");
        int matakuliahCount = count(connection, "matakuliah");
        if (dosenCount >= 100 && mahasiswaCount >= 200 && matakuliahCount >= 256) {
            return;
        }
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("SET FOREIGN_KEY_CHECKS=0");
            statement.executeUpdate("TRUNCATE TABLE nilai");
            statement.executeUpdate("TRUNCATE TABLE krs");
            statement.executeUpdate("TRUNCATE TABLE pembayaran_ukt");
            statement.executeUpdate("TRUNCATE TABLE matakuliah");
            statement.executeUpdate("TRUNCATE TABLE mahasiswa");
            statement.executeUpdate("TRUNCATE TABLE dosen");
            statement.executeUpdate("TRUNCATE TABLE prodi");
            statement.executeUpdate("SET FOREIGN_KEY_CHECKS=1");
        }
    }

    private static boolean needsCoreSeed(Connection connection) throws SQLException {
        return count(connection, "dosen") < 100
                || count(connection, "mahasiswa") < 200
                || count(connection, "matakuliah") < 256
                || count(connection, "krs") < 192
                || count(connection, "nilai") < 192;
    }

    private static int count(Connection connection, String table) throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM " + table)) {
            resultSet.next();
            return resultSet.getInt(1);
        }
    }

    private static void createWilayahTables(Statement statement) throws SQLException {
        // Hapus tabel lama (legacy) bila masih ada.
        statement.executeUpdate("DROP TABLE IF EXISTS wilayah_kelurahan");
        statement.executeUpdate("DROP TABLE IF EXISTS wilayah_kecamatan");
        statement.executeUpdate("DROP TABLE IF EXISTS wilayah_kabupaten");
        statement.executeUpdate("DROP TABLE IF EXISTS wilayah_provinsi");

        statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS reg_provinces (
                    id CHAR(2) PRIMARY KEY,
                    name VARCHAR(255) NOT NULL
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
                """);

        statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS reg_regencies (
                    id CHAR(4) PRIMARY KEY,
                    province_id CHAR(2) NOT NULL,
                    name VARCHAR(255) NOT NULL,
                    FOREIGN KEY (province_id) REFERENCES reg_provinces(id)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
                """);

        statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS reg_districts (
                    id CHAR(6) PRIMARY KEY,
                    regency_id CHAR(4) NOT NULL,
                    name VARCHAR(255) NOT NULL,
                    FOREIGN KEY (regency_id) REFERENCES reg_regencies(id)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
                """);

        statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS reg_villages (
                    id CHAR(10) PRIMARY KEY,
                    district_id CHAR(6) NOT NULL,
                    name VARCHAR(255) NOT NULL,
                    FOREIGN KEY (district_id) REFERENCES reg_districts(id)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
                """);
    }

    private static void ensureWilayahData(Connection connection) throws SQLException {
        int provinces = count(connection, "reg_provinces");
        int regencies = count(connection, "reg_regencies");
        int districts = count(connection, "reg_districts");
        int villages = count(connection, "reg_villages");
        if (provinces >= 37 && regencies >= 514 && districts >= 7277 && villages >= 83761) {
            return;
        }
        seedWilayah(connection);
    }

    private static void createTables(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            createWilayahTables(statement);

            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS users (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        username VARCHAR(40) NOT NULL UNIQUE,
                        password_hash VARCHAR(255) NOT NULL,
                        role ENUM('ADMIN','OPERATOR') NOT NULL,
                        nama_lengkap VARCHAR(100) NOT NULL,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
                    """);
            statement.executeUpdate("ALTER TABLE users MODIFY COLUMN password_hash VARCHAR(255) NOT NULL");

            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS prodi (
                        kode_prodi VARCHAR(10) PRIMARY KEY,
                        nama_prodi VARCHAR(80) NOT NULL UNIQUE,
                        kode_kelas VARCHAR(10) NOT NULL UNIQUE,
                        kode_dosen_prodi VARCHAR(10) NOT NULL,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
                    """);

            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS mahasiswa (
                        nim CHAR(5) PRIMARY KEY,
                        nama VARCHAR(100) NOT NULL,
                        jenis_kelamin ENUM('Laki-laki','Perempuan') NOT NULL DEFAULT 'Laki-laki',
                        tanggal_lahir DATE NULL,
                        alamat VARCHAR(180) NOT NULL DEFAULT '-',
                        prodi VARCHAR(80) NOT NULL DEFAULT 'Teknik Informatika',
                        kode_provinsi CHAR(2) NULL,
                        kode_kabupaten CHAR(4) NULL,
                        kode_kecamatan CHAR(6) NULL,
                        kode_kelurahan CHAR(10) NULL,
                        kode_prodi VARCHAR(10) NOT NULL DEFAULT 'TIF',
                        kode_kelas VARCHAR(24) NOT NULL DEFAULT 'TIF 25A CID',
                        kode_dosen_wali VARCHAR(10) NOT NULL DEFAULT 'DSN001',
                        angkatan YEAR NOT NULL,
                        status ENUM('AKTIF','NONAKTIF') NOT NULL DEFAULT 'AKTIF',
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        FOREIGN KEY (kode_provinsi) REFERENCES reg_provinces(id),
                        FOREIGN KEY (kode_kabupaten) REFERENCES reg_regencies(id),
                        FOREIGN KEY (kode_kecamatan) REFERENCES reg_districts(id),
                        FOREIGN KEY (kode_kelurahan) REFERENCES reg_villages(id),
                        CONSTRAINT chk_mahasiswa_nim CHECK (nim REGEXP '^[0-9]{5}$')
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
                    """);

            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS dosen (
                        kode_dosen VARCHAR(10) PRIMARY KEY,
                        nama_dosen VARCHAR(100) NOT NULL,
                        keahlian VARCHAR(100) NOT NULL,
                        alamat VARCHAR(180) NOT NULL,
                        jabatan ENUM('DOSEN','DOSEN_WALI','KAPRODI') NOT NULL DEFAULT 'DOSEN',
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
                    """);

            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS matakuliah (
                        kode_mk VARCHAR(16) PRIMARY KEY,
                        nama_mk VARCHAR(120) NOT NULL,
                        sks TINYINT NOT NULL,
                        semester TINYINT NOT NULL,
                        kode_dosen VARCHAR(10) NOT NULL,
                        kode_prodi VARCHAR(10) NOT NULL,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        CONSTRAINT chk_mk_sks CHECK (sks BETWEEN 1 AND 6),
                        CONSTRAINT chk_mk_semester CHECK (semester BETWEEN 1 AND 8),
                        CONSTRAINT fk_mk_dosen FOREIGN KEY (kode_dosen) REFERENCES dosen(kode_dosen),
                        CONSTRAINT fk_mk_prodi FOREIGN KEY (kode_prodi) REFERENCES prodi(kode_prodi),
                        UNIQUE KEY uq_mk_prodi_semester_nama (kode_prodi, semester, nama_mk)
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
                    """);

            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS krs (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        nim CHAR(5) NOT NULL,
                        kode_mk VARCHAR(16) NOT NULL,
                        kode_dosen VARCHAR(10) NOT NULL,
                        semester TINYINT NOT NULL,
                        tahun_akademik VARCHAR(9) NOT NULL,
                        is_mengulang TINYINT(1) NOT NULL DEFAULT 0,
                        ukt_lunas TINYINT(1) NOT NULL DEFAULT 0,
                        acc_dosen_wali TINYINT(1) NOT NULL DEFAULT 0,
                        acc_dosen_prodi TINYINT(1) NOT NULL DEFAULT 0,
                        acc_at TIMESTAMP NULL,
                        created_by INT NULL,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        CONSTRAINT chk_krs_semester CHECK (semester BETWEEN 1 AND 8),
                        CONSTRAINT chk_krs_single_acc CHECK (acc_dosen_wali + acc_dosen_prodi <= 1),
                        CONSTRAINT fk_krs_mahasiswa FOREIGN KEY (nim) REFERENCES mahasiswa(nim) ON DELETE CASCADE,
                        CONSTRAINT fk_krs_mk FOREIGN KEY (kode_mk) REFERENCES matakuliah(kode_mk),
                        CONSTRAINT fk_krs_dosen FOREIGN KEY (kode_dosen) REFERENCES dosen(kode_dosen),
                        CONSTRAINT fk_krs_user FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL,
                        UNIQUE KEY uq_krs_mahasiswa_mk_semester_tahun (nim, kode_mk, semester, tahun_akademik),
                        INDEX idx_krs_nim (nim),
                        INDEX idx_krs_semester (semester)
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
                    """);

            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS pembayaran_ukt (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        nim CHAR(5) NOT NULL,
                        semester TINYINT NOT NULL,
                        tahun_akademik VARCHAR(9) NOT NULL,
                        status_lunas TINYINT(1) NOT NULL DEFAULT 0,
                        tanggal_bayar DATE NULL,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        CONSTRAINT chk_ukt_semester CHECK (semester BETWEEN 1 AND 8),
                        CONSTRAINT fk_ukt_mahasiswa FOREIGN KEY (nim) REFERENCES mahasiswa(nim) ON DELETE CASCADE,
                        UNIQUE KEY uq_ukt_mahasiswa_semester_tahun (nim, semester, tahun_akademik)
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
                    """);

            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS nilai (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        krs_id INT NOT NULL UNIQUE,
                        nim CHAR(5) NOT NULL,
                        kode_mk VARCHAR(16) NOT NULL,
                        kode_dosen VARCHAR(10) NOT NULL,
                        semester TINYINT NOT NULL,
                        tahun_akademik VARCHAR(9) NOT NULL,
                        nilai_absensi DECIMAL(5,2) NOT NULL DEFAULT 0,
                        nilai_tugas DECIMAL(5,2) NOT NULL DEFAULT 0,
                        nilai_kuis DECIMAL(5,2) NOT NULL DEFAULT 0,
                        nilai_uts DECIMAL(5,2) NOT NULL DEFAULT 0,
                        nilai_uas DECIMAL(5,2) NOT NULL DEFAULT 0,
                        nilai_akhir DECIMAL(5,2) NOT NULL DEFAULT 0,
                        nilai_huruf ENUM('A','AB','B','BC','C','D','E') NOT NULL,
                        bobot DECIMAL(3,2) NOT NULL,
                        created_by INT NULL,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                        CONSTRAINT chk_nilai_semester CHECK (semester BETWEEN 1 AND 8),
                        CONSTRAINT chk_nilai_bobot CHECK (bobot BETWEEN 0 AND 4),
                        CONSTRAINT fk_nilai_krs FOREIGN KEY (krs_id) REFERENCES krs(id) ON DELETE CASCADE,
                        CONSTRAINT fk_nilai_mahasiswa FOREIGN KEY (nim) REFERENCES mahasiswa(nim) ON DELETE CASCADE,
                        CONSTRAINT fk_nilai_mk FOREIGN KEY (kode_mk) REFERENCES matakuliah(kode_mk),
                        CONSTRAINT fk_nilai_dosen FOREIGN KEY (kode_dosen) REFERENCES dosen(kode_dosen),
                        CONSTRAINT fk_nilai_user FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL,
                        UNIQUE KEY uq_nilai_mahasiswa_mk_semester_tahun (nim, kode_mk, semester, tahun_akademik)
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
                    """);

            statement.executeUpdate("ALTER TABLE mahasiswa ADD COLUMN IF NOT EXISTS jenis_kelamin ENUM('Laki-laki','Perempuan') NOT NULL DEFAULT 'Laki-laki'");
            statement.executeUpdate("ALTER TABLE mahasiswa ADD COLUMN IF NOT EXISTS tanggal_lahir DATE NULL");
            statement.executeUpdate("ALTER TABLE mahasiswa ADD COLUMN IF NOT EXISTS alamat VARCHAR(180) NOT NULL DEFAULT '-'");
            statement.executeUpdate("ALTER TABLE mahasiswa ADD COLUMN IF NOT EXISTS prodi VARCHAR(80) NOT NULL DEFAULT 'Teknik Informatika'");
            statement.executeUpdate("ALTER TABLE mahasiswa ADD COLUMN IF NOT EXISTS kode_provinsi CHAR(2) NULL");
            statement.executeUpdate("ALTER TABLE mahasiswa ADD COLUMN IF NOT EXISTS kode_kabupaten CHAR(4) NULL");
            statement.executeUpdate("ALTER TABLE mahasiswa ADD COLUMN IF NOT EXISTS kode_kecamatan CHAR(6) NULL");
            statement.executeUpdate("ALTER TABLE mahasiswa ADD COLUMN IF NOT EXISTS kode_kelurahan CHAR(10) NULL");
            statement.executeUpdate("ALTER TABLE mahasiswa ADD COLUMN IF NOT EXISTS kode_prodi VARCHAR(10) NOT NULL DEFAULT 'TIF'");
            statement.executeUpdate("ALTER TABLE mahasiswa ADD COLUMN IF NOT EXISTS kode_kelas VARCHAR(24) NOT NULL DEFAULT 'TIF 25A CID'");
            statement.executeUpdate("ALTER TABLE mahasiswa ADD COLUMN IF NOT EXISTS kode_dosen_wali VARCHAR(10) NOT NULL DEFAULT 'DSN001'");
            statement.executeUpdate("ALTER TABLE mahasiswa MODIFY COLUMN kode_kelas VARCHAR(24) NOT NULL DEFAULT 'TIF 25A CID'");
            statement.executeUpdate("ALTER TABLE dosen ADD COLUMN IF NOT EXISTS alamat VARCHAR(180) NOT NULL DEFAULT '-'" );
            statement.executeUpdate("ALTER TABLE dosen ADD COLUMN IF NOT EXISTS jabatan ENUM('DOSEN','DOSEN_WALI','KAPRODI') NOT NULL DEFAULT 'DOSEN'");
            statement.executeUpdate("ALTER TABLE matakuliah ADD COLUMN IF NOT EXISTS kode_prodi VARCHAR(10) NOT NULL DEFAULT 'TIF'");
            statement.executeUpdate("ALTER TABLE krs ADD COLUMN IF NOT EXISTS ukt_lunas TINYINT(1) NOT NULL DEFAULT 0");
            statement.executeUpdate("ALTER TABLE krs ADD COLUMN IF NOT EXISTS acc_dosen_wali TINYINT(1) NOT NULL DEFAULT 0");
            statement.executeUpdate("ALTER TABLE krs ADD COLUMN IF NOT EXISTS acc_dosen_prodi TINYINT(1) NOT NULL DEFAULT 0");
            statement.executeUpdate("ALTER TABLE krs ADD COLUMN IF NOT EXISTS acc_at TIMESTAMP NULL");
            statement.executeUpdate("ALTER TABLE nilai ADD COLUMN IF NOT EXISTS nilai_absensi DECIMAL(5,2) NOT NULL DEFAULT 0");
            statement.executeUpdate("ALTER TABLE nilai ADD COLUMN IF NOT EXISTS nilai_tugas DECIMAL(5,2) NOT NULL DEFAULT 0");
            statement.executeUpdate("ALTER TABLE nilai ADD COLUMN IF NOT EXISTS nilai_kuis DECIMAL(5,2) NOT NULL DEFAULT 0");
            statement.executeUpdate("ALTER TABLE nilai ADD COLUMN IF NOT EXISTS nilai_uts DECIMAL(5,2) NOT NULL DEFAULT 0");
            statement.executeUpdate("ALTER TABLE nilai ADD COLUMN IF NOT EXISTS nilai_uas DECIMAL(5,2) NOT NULL DEFAULT 0");
            statement.executeUpdate("ALTER TABLE nilai ADD COLUMN IF NOT EXISTS nilai_akhir DECIMAL(5,2) NOT NULL DEFAULT 0");
            statement.executeUpdate("ALTER TABLE nilai MODIFY COLUMN nilai_huruf ENUM('A','AB','B','BC','C','D','E') NOT NULL");
            statement.executeUpdate("ALTER TABLE nilai MODIFY COLUMN bobot DECIMAL(3,2) NOT NULL");
            statement.executeUpdate("""
                    UPDATE nilai
                    SET nilai_akhir = CASE nilai_huruf
                        WHEN 'A' THEN 90
                        WHEN 'AB' THEN 82
                        WHEN 'B' THEN 77
                        WHEN 'BC' THEN 72
                        WHEN 'C' THEN 65
                        WHEN 'D' THEN 55
                        ELSE 45
                    END
                    WHERE nilai_akhir = 0
                    """);
            statement.executeUpdate("UPDATE nilai SET nilai_absensi = nilai_akhir WHERE nilai_absensi = 0");
            statement.executeUpdate("UPDATE nilai SET nilai_tugas = nilai_akhir WHERE nilai_tugas = 0");
            statement.executeUpdate("UPDATE nilai SET nilai_kuis = nilai_akhir WHERE nilai_kuis = 0");
            statement.executeUpdate("UPDATE nilai SET nilai_uts = nilai_akhir WHERE nilai_uts = 0");
            statement.executeUpdate("UPDATE nilai SET nilai_uas = nilai_akhir WHERE nilai_uas = 0");
        }
        ensureKodeMkLength(connection);
    }

    private static void ensureKodeMkLength(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            try {
                statement.executeUpdate("ALTER TABLE krs DROP FOREIGN KEY fk_krs_mk");
            } catch (SQLException ignored) {
            }
            try {
                statement.executeUpdate("ALTER TABLE nilai DROP FOREIGN KEY fk_nilai_mk");
            } catch (SQLException ignored) {
            }
            statement.executeUpdate("ALTER TABLE matakuliah MODIFY COLUMN kode_mk VARCHAR(16)");
            statement.executeUpdate("ALTER TABLE krs MODIFY COLUMN kode_mk VARCHAR(16)");
            statement.executeUpdate("ALTER TABLE nilai MODIFY COLUMN kode_mk VARCHAR(16)");
            try {
                statement.executeUpdate("""
                        ALTER TABLE krs
                        ADD CONSTRAINT fk_krs_mk FOREIGN KEY (kode_mk)
                        REFERENCES matakuliah(kode_mk)
                        """);
            } catch (SQLException ignored) {
            }
            try {
                statement.executeUpdate("""
                        ALTER TABLE nilai
                        ADD CONSTRAINT fk_nilai_mk FOREIGN KEY (kode_mk)
                        REFERENCES matakuliah(kode_mk)
                        """);
            } catch (SQLException ignored) {
            }
        }
    }

    private static void normalizeMatakuliahNames(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            if (indexExists(connection, "matakuliah", "uq_mk_semester_nama")) {
                statement.executeUpdate("ALTER TABLE matakuliah DROP INDEX uq_mk_semester_nama");
            }
            if (indexExists(connection, "matakuliah", "nama_mk")) {
                statement.executeUpdate("ALTER TABLE matakuliah DROP INDEX nama_mk");
            }
            statement.executeUpdate("ALTER TABLE matakuliah MODIFY COLUMN nama_mk VARCHAR(120) NOT NULL");

            statement.executeUpdate("""
                    UPDATE matakuliah
                    SET nama_mk = TRIM(
                        REPLACE(
                            REPLACE(
                                REPLACE(
                                    REPLACE(nama_mk, 'Teknik Informatika - ', ''),
                                'Teknik Industri - ', ''),
                            'DKV - ', ''),
                        'Retail - ', '')
                    )
                    """);

            if (!indexExists(connection, "matakuliah", "uq_mk_prodi_semester_nama")) {
                statement.executeUpdate("""
                        ALTER TABLE matakuliah
                        ADD CONSTRAINT uq_mk_prodi_semester_nama
                        UNIQUE (kode_prodi, semester, nama_mk)
                        """);
            }
        }
    }

    private static boolean indexExists(Connection connection, String tableName, String indexName) throws SQLException {
        String sql = """
                SELECT 1
                FROM information_schema.statistics
                WHERE table_schema = DATABASE()
                  AND table_name = ?
                  AND index_name = ?
                LIMIT 1
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, tableName);
            statement.setString(2, indexName);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    private static boolean columnExists(Connection connection, String tableName, String columnName) throws SQLException {
        String sql = """
                SELECT 1
                FROM information_schema.columns
                WHERE table_schema = DATABASE()
                  AND table_name = ?
                  AND column_name = ?
                LIMIT 1
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, tableName);
            statement.setString(2, columnName);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    private static void ensureMahasiswaBirthDates(Connection connection) throws SQLException {
        if (!columnExists(connection, "mahasiswa", "tanggal_lahir")) {
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("ALTER TABLE mahasiswa ADD COLUMN tanggal_lahir DATE NULL");
            }
        }

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("""
                    UPDATE mahasiswa
                    SET tanggal_lahir = DATE_ADD(
                            DATE_ADD(
                                MAKEDATE(
                                    CASE
                                        WHEN CAST(angkatan AS UNSIGNED) BETWEEN 2000 AND (YEAR(CURDATE()) + 1)
                                            THEN CAST(angkatan AS UNSIGNED) - 18
                                        ELSE YEAR(CURDATE()) - 19
                                    END, 1
                                ),
                                INTERVAL (CAST(RIGHT(nim, 2) AS UNSIGNED) % 12) MONTH
                            ),
                            INTERVAL ((CAST(RIGHT(nim, 3) AS UNSIGNED) % 27) + 1) DAY
                        )
                    WHERE tanggal_lahir IS NULL
                    """);
        }
    }

    private static void ensureMahasiswaWilayah(Connection connection) throws SQLException {
        if (!columnExists(connection, "mahasiswa", "kode_provinsi")
                || !columnExists(connection, "mahasiswa", "kode_kabupaten")
                || !columnExists(connection, "mahasiswa", "kode_kecamatan")
                || !columnExists(connection, "mahasiswa", "kode_kelurahan")) {
            return;
        }

        List<String[]> wilayah = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement("""
                SELECT p.id AS provinsi, r.id AS kabupaten, d.id AS kecamatan, v.id AS kelurahan
                FROM reg_villages v
                JOIN reg_districts d ON d.id = v.district_id
                JOIN reg_regencies r ON r.id = d.regency_id
                JOIN reg_provinces p ON p.id = r.province_id
                ORDER BY v.id
                """);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                wilayah.add(new String[]{
                        resultSet.getString("provinsi"),
                        resultSet.getString("kabupaten"),
                        resultSet.getString("kecamatan"),
                        resultSet.getString("kelurahan")
                });
            }
        }
        if (wilayah.isEmpty()) {
            return;
        }

        List<String> nimTarget = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement("""
                SELECT nim
                FROM mahasiswa
                WHERE kode_provinsi IS NULL
                   OR kode_kabupaten IS NULL
                   OR kode_kecamatan IS NULL
                   OR kode_kelurahan IS NULL
                ORDER BY nim
                """);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                nimTarget.add(resultSet.getString("nim"));
            }
        }
        if (nimTarget.isEmpty()) {
            return;
        }

        try (PreparedStatement statement = connection.prepareStatement("""
                UPDATE mahasiswa
                SET kode_provinsi = ?, kode_kabupaten = ?, kode_kecamatan = ?, kode_kelurahan = ?
                WHERE nim = ?
                """)) {
            for (String nim : nimTarget) {
                int seed;
                try {
                    seed = Math.floorMod(Integer.parseInt(nim), wilayah.size());
                } catch (NumberFormatException ex) {
                    seed = Math.floorMod(nim.hashCode(), wilayah.size());
                }
                String[] loc = wilayah.get(seed);
                statement.setString(1, loc[0]);
                statement.setString(2, loc[1]);
                statement.setString(3, loc[2]);
                statement.setString(4, loc[3]);
                statement.setString(5, nim);
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    private static void createViews(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("""
                    CREATE OR REPLACE VIEW v_krs_detail AS
                    SELECT k.id, k.nim, m.nama, m.kode_kelas, p.nama_prodi, m.kode_dosen_wali,
                           m.jenis_kelamin, m.alamat,
                           dw.nama_dosen AS dosen_wali, p.kode_dosen_prodi,
                           dp.nama_dosen AS dosen_prodi, k.kode_mk, mk.nama_mk, mk.sks,
                           mk.semester AS semester_mk, k.semester,
                           k.tahun_akademik, k.kode_dosen, d.nama_dosen,
                           IF(k.is_mengulang = 1, 'Ya', 'Tidak') AS mengulang,
                           (SELECT MAX(prev.semester)
                            FROM krs prev
                            WHERE prev.nim = k.nim
                              AND prev.kode_mk = k.kode_mk
                              AND prev.semester < k.semester) AS semester_sebelumnya,
                           CASE
                               WHEN p.kode_prodi = 'TIF' THEN CONCAT('TIF ',
                                   RIGHT(CAST(m.angkatan AS UNSIGNED), 2),
                                   CASE MOD(k.semester - 1, 4)
                                       WHEN 0 THEN 'A'
                                       WHEN 1 THEN 'D'
                                       WHEN 2 THEN 'B'
                                       ELSE 'C'
                                   END)
                               WHEN p.kode_prodi = 'TI' THEN CONCAT('TI ',
                                   RIGHT(CAST(m.angkatan AS UNSIGNED), 2),
                                   CASE MOD(k.semester - 1, 2)
                                       WHEN 0 THEN 'E'
                                       ELSE 'F'
                                   END)
                               WHEN p.kode_prodi = 'DKV' THEN CONCAT('DKV ',
                                   RIGHT(CAST(m.angkatan AS UNSIGNED), 2),
                                   CASE MOD(k.semester - 1, 3)
                                       WHEN 0 THEN 'A'
                                       WHEN 1 THEN 'B'
                                       ELSE 'C'
                                   END)
                               ELSE CONCAT('RETAIL ',
                                   RIGHT(CAST(m.angkatan AS UNSIGNED), 2),
                                   CASE MOD(k.semester - 1, 2)
                                       WHEN 0 THEN 'A'
                                       ELSE 'B'
                                   END)
                           END AS kelas_semester,
                           CASE
                               WHEN k.is_mengulang = 1 THEN CONCAT('Mengulang ', k.kode_mk, ' dari semester ',
                                   COALESCE((SELECT MAX(prev.semester)
                                             FROM krs prev
                                             WHERE prev.nim = k.nim
                                               AND prev.kode_mk = k.kode_mk
                                               AND prev.semester < k.semester), mk.semester),
                                   ' pada semester ', k.semester)
                               ELSE 'Tidak mengulang'
                           END AS detail_mengulang,
                           IF(k.ukt_lunas = 1, 'Lunas', 'Belum Lunas') AS status_ukt,
                           CASE
                               WHEN k.acc_dosen_wali = 1 THEN 'Dosen Wali'
                               WHEN k.acc_dosen_prodi = 1 THEN 'Kaprodi'
                               ELSE '-'
                           END AS acc_oleh,
                           CASE
                               WHEN k.acc_dosen_wali = 1 THEN m.kode_dosen_wali
                               WHEN k.acc_dosen_prodi = 1 THEN p.kode_dosen_prodi
                               ELSE '-'
                           END AS kode_dosen_acc,
                           CASE
                               WHEN k.acc_dosen_wali = 1 THEN dw.nama_dosen
                               WHEN k.acc_dosen_prodi = 1 THEN dp.nama_dosen
                               ELSE '-'
                           END AS dosen_acc,
                           IF(k.ukt_lunas = 1 AND (k.acc_dosen_wali = 1 OR k.acc_dosen_prodi = 1), 'ACC', 'Belum ACC') AS status_acc,
                           CASE
                               WHEN k.ukt_lunas = 1 AND (k.acc_dosen_wali = 1 OR k.acc_dosen_prodi = 1) THEN 'Disetujui'
                               WHEN k.ukt_lunas = 0 THEN 'Menunggu UKT'
                               ELSE 'Menunggu ACC'
                           END AS status_krs
                    FROM krs k
                    JOIN mahasiswa m ON m.nim = k.nim
                    LEFT JOIN prodi p ON p.kode_prodi = m.kode_prodi
                    LEFT JOIN dosen dw ON dw.kode_dosen = m.kode_dosen_wali
                    LEFT JOIN dosen dp ON dp.kode_dosen = p.kode_dosen_prodi
                    JOIN matakuliah mk ON mk.kode_mk = k.kode_mk
                    JOIN dosen d ON d.kode_dosen = k.kode_dosen
                    """);

            statement.executeUpdate("""
                    CREATE OR REPLACE VIEW v_nilai_detail AS
                    SELECT k.id AS krs_id, k.nim, m.nama, m.jenis_kelamin, m.kode_kelas, p.nama_prodi,
                           k.kode_mk, mk.nama_mk, mk.sks,
                           k.semester, k.tahun_akademik, k.kode_dosen, d.nama_dosen,
                           IF(k.is_mengulang = 1, 'Ya', 'Tidak') AS mengulang,
                           IF(k.ukt_lunas = 1, 'Lunas', 'Belum Lunas') AS status_ukt,
                           IF(k.ukt_lunas = 1 AND (k.acc_dosen_wali = 1 OR k.acc_dosen_prodi = 1), 'Disetujui', 'Belum Lengkap') AS status_krs,
                           n.nilai_absensi, n.nilai_tugas, n.nilai_kuis, n.nilai_uts, n.nilai_uas,
                           n.nilai_akhir, n.nilai_huruf, n.bobot,
                           (SELECT SUM(mk2.sks)
                            FROM krs k2
                            JOIN matakuliah mk2 ON mk2.kode_mk = k2.kode_mk
                            WHERE k2.nim = k.nim AND k2.semester = k.semester
                            AND k2.tahun_akademik = k.tahun_akademik) AS total_sks_semester,
                           (SELECT COUNT(*)
                            FROM krs k2
                            WHERE k2.nim = k.nim AND k2.semester = k.semester
                            AND k2.tahun_akademik = k.tahun_akademik) AS total_mk_semester,
                           (SELECT v.kelas_semester FROM v_krs_detail v WHERE v.id = k.id) AS kelas_semester
                    FROM krs k
                    JOIN mahasiswa m ON m.nim = k.nim
                    LEFT JOIN prodi p ON p.kode_prodi = m.kode_prodi
                    JOIN matakuliah mk ON mk.kode_mk = k.kode_mk
                    JOIN dosen d ON d.kode_dosen = k.kode_dosen
                    LEFT JOIN nilai n ON n.krs_id = k.id
                    """);

            statement.executeUpdate("""
                    CREATE OR REPLACE VIEW ipk AS
                    SELECT k.id AS krs_id, k.nim, k.kode_mk, k.kode_dosen, k.semester,
                           k.tahun_akademik, k.is_mengulang, n.nilai_huruf AS nilai
                    FROM krs k
                    LEFT JOIN nilai n ON n.krs_id = k.id
                    """);

            statement.executeUpdate("""
                    CREATE OR REPLACE VIEW v_ip_semester AS
                    SELECT n.nim, m.nama, n.semester,
                           ROUND(SUM(mk.sks * n.bobot) / SUM(mk.sks), 2) AS ip,
                           SUM(mk.sks) AS total_sks,
                           COUNT(*) AS jumlah_matakuliah
                    FROM nilai n
                    JOIN mahasiswa m ON m.nim = n.nim
                    JOIN matakuliah mk ON mk.kode_mk = n.kode_mk
                    GROUP BY n.nim, m.nama, n.semester
                    """);

            statement.executeUpdate("""
                    CREATE OR REPLACE VIEW v_ipk AS
                    SELECT n.nim, m.nama,
                           ROUND(SUM(mk.sks * n.bobot) / SUM(mk.sks), 2) AS ipk,
                           SUM(mk.sks) AS total_sks,
                           COUNT(*) AS jumlah_matakuliah
                    FROM nilai n
                    JOIN mahasiswa m ON m.nim = n.nim
                    JOIN matakuliah mk ON mk.kode_mk = n.kode_mk
                    GROUP BY n.nim, m.nama
                    """);
        }
    }

    private static void seedUsers(Connection connection) throws SQLException {
        String sql = """
                INSERT IGNORE INTO users (username, password_hash, role, nama_lengkap)
                VALUES (?, ?, ?, ?)
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            addUser(statement, "admin", PasswordUtil.hash("admin123"), "ADMIN", "Administrator");
            addUser(statement, "operator", PasswordUtil.hash("operator123"), "OPERATOR", "Operator Akademik");
        }
    }

    private static void addUser(PreparedStatement statement, String username, String passwordHash,
                                String role, String namaLengkap) throws SQLException {
        statement.setString(1, username);
        statement.setString(2, passwordHash);
        statement.setString(3, role);
        statement.setString(4, namaLengkap);
        statement.executeUpdate();
    }

    private static void seedDosen(Connection connection) throws SQLException {
        String sql = """
                INSERT INTO dosen (kode_dosen, nama_dosen, keahlian, alamat, jabatan)
                VALUES (?, ?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE
                    nama_dosen = VALUES(nama_dosen),
                    keahlian = VALUES(keahlian),
                    alamat = VALUES(alamat),
                    jabatan = VALUES(jabatan)
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (DosenSeed dosen : dosenSeeds()) {
                statement.setString(1, dosen.kode());
                statement.setString(2, dosen.nama());
                statement.setString(3, dosen.keahlian());
                statement.setString(4, dosen.alamat());
                statement.setString(5, "DOSEN");
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    private static void seedProdi(Connection connection) throws SQLException {
        String sql = """
                INSERT INTO prodi (kode_prodi, nama_prodi, kode_kelas, kode_dosen_prodi)
                VALUES (?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE
                    nama_prodi = VALUES(nama_prodi),
                    kode_kelas = VALUES(kode_kelas),
                    kode_dosen_prodi = VALUES(kode_dosen_prodi)
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (String[] prodi : PRODI) {
                statement.setString(1, prodi[0]);
                statement.setString(2, prodi[1]);
                statement.setString(3, prodi[2]);
                statement.setString(4, prodi[3]);
                statement.addBatch();
            }
            statement.executeBatch();
        }

        String updateSql = """
                UPDATE mahasiswa m
                JOIN prodi p ON p.nama_prodi = m.prodi
                SET m.kode_prodi = p.kode_prodi,
                    m.kode_kelas = p.kode_kelas
                WHERE m.kode_prodi IS NULL OR m.kode_prodi = '' OR m.kode_kelas IS NULL OR m.kode_kelas = ''
                """;
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(updateSql);
        }
    }

    private static void seedWilayah(Connection connection) throws SQLException {
        try {
            java.nio.file.Path sqlPath = java.nio.file.Paths.get("database", "wilayah_indonesia.sql");
            String sqlContent = java.nio.file.Files.readString(sqlPath);
            String[] chunks = sqlContent.split(";");

            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("SET FOREIGN_KEY_CHECKS=0");
                statement.executeUpdate("TRUNCATE TABLE reg_villages");
                statement.executeUpdate("TRUNCATE TABLE reg_districts");
                statement.executeUpdate("TRUNCATE TABLE reg_regencies");
                statement.executeUpdate("TRUNCATE TABLE reg_provinces");
                statement.executeUpdate("SET FOREIGN_KEY_CHECKS=1");

                for (String chunk : chunks) {
                    String stmt = chunk.trim();
                    if (stmt.isEmpty() || stmt.startsWith("--") || stmt.startsWith("/*")) {
                        continue;
                    }
                    String normalized = stmt.toUpperCase();
                    if (!normalized.startsWith("INSERT INTO `REG_")
                            && !normalized.startsWith("INSERT INTO REG_")) {
                        continue;
                    }
                    String insertIgnore = stmt.replaceFirst("(?i)^INSERT\\s+INTO", "INSERT IGNORE INTO");
                    executeInsertInBatches(statement, insertIgnore, 100);
                }
            }
        } catch (java.io.IOException e) {
            throw new SQLException("Failed to read wilayah_indonesia.sql", e);
        }
    }

    private static void executeInsertInBatches(Statement statement, String insertSql, int batchSize) throws SQLException {
        int valuesIndex = insertSql.toUpperCase().indexOf("VALUES");
        if (valuesIndex < 0) {
            statement.executeUpdate(insertSql);
            return;
        }

        String prefix = insertSql.substring(0, valuesIndex + 6).trim();
        String valuesPart = insertSql.substring(valuesIndex + 6).trim();
        if (valuesPart.endsWith(")")) {
            // remove final closing parenthesis if the statement ends with it
            valuesPart = valuesPart.substring(0, valuesPart.length() - 1).trim();
        }

        String[] rowParts = valuesPart.split("\\),\\s*\\(");
        List<String> rows = new ArrayList<>();
        for (String rowPart : rowParts) {
            String row = rowPart.trim();
            if (!row.startsWith("(")) {
                row = "(" + row;
            }
            if (!row.endsWith(")")) {
                row = row + ")";
            }
            rows.add(row);
        }

        for (int i = 0; i < rows.size(); i += batchSize) {
            int end = Math.min(i + batchSize, rows.size());
            String batchSql = prefix + " " + String.join(", ", rows.subList(i, end));
            statement.executeUpdate(batchSql);
        }
    }

    private static void seedMahasiswa(Connection connection) throws SQLException {
        String sql = """
                INSERT INTO mahasiswa
                (nim, nama, jenis_kelamin, alamat, prodi, kode_prodi, kode_kelas, kode_dosen_wali, angkatan, status)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 'AKTIF')
                ON DUPLICATE KEY UPDATE
                    nama = VALUES(nama),
                    jenis_kelamin = VALUES(jenis_kelamin),
                    alamat = VALUES(alamat),
                    prodi = VALUES(prodi),
                    kode_prodi = VALUES(kode_prodi),
                    kode_kelas = VALUES(kode_kelas),
                    kode_dosen_wali = VALUES(kode_dosen_wali),
                    angkatan = VALUES(angkatan),
                    status = 'AKTIF'
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (MahasiswaSeed seed : mahasiswaSeeds()) {
                statement.setString(1, seed.nim());
                statement.setString(2, seed.nama());
                statement.setString(3, seed.jenisKelamin());
                statement.setString(4, seed.alamat());
                statement.setString(5, seed.namaProdi());
                statement.setString(6, seed.kodeProdi());
                statement.setString(7, seed.kodeKelas());
                statement.setString(8, seed.kodeDosenWali());
                statement.setInt(9, seed.angkatan());
                statement.addBatch();
            }
            statement.executeBatch();
        }

        String updateSeedSql = """
                UPDATE mahasiswa
                SET jenis_kelamin = ?,
                    alamat = ?,
                    prodi = ?,
                    kode_prodi = ?,
                    kode_kelas = ?,
                    kode_dosen_wali = ?,
                    angkatan = ?,
                    status = 'AKTIF'
                WHERE nim = ?
                """;
        try (PreparedStatement statement = connection.prepareStatement(updateSeedSql)) {
            for (MahasiswaSeed seed : mahasiswaSeeds()) {
                statement.setString(1, seed.jenisKelamin());
                statement.setString(2, seed.alamat());
                statement.setString(3, seed.namaProdi());
                statement.setString(4, seed.kodeProdi());
                statement.setString(5, seed.kodeKelas());
                statement.setString(6, seed.kodeDosenWali());
                statement.setInt(7, seed.angkatan());
                statement.setString(8, seed.nim());
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    private static void seedMataKuliah(Connection connection) throws SQLException {
        String sql = """
                INSERT INTO matakuliah (kode_mk, nama_mk, sks, semester, kode_dosen, kode_prodi)
                VALUES (?, ?, ?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE
                    nama_mk = VALUES(nama_mk),
                    sks = VALUES(sks),
                    semester = VALUES(semester),
                    kode_dosen = VALUES(kode_dosen),
                    kode_prodi = VALUES(kode_prodi)
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (String[] prodi : PRODI) {
                String kodeProdi = prodi[0];
                for (int semester = 1; semester <= 8; semester++) {
                    for (int index = 0; index < 8; index++) {
                        statement.setString(1, kodeMk(kodeProdi, semester, index + 1));
                        statement.setString(2, courseNameForProdi(kodeProdi, semester, index + 1));
                        statement.setInt(3, index == 0 && semester == 8 ? 6 : 3);
                        statement.setInt(4, semester);
                        statement.setString(5, kodeDosenForCourse(kodeProdi, semester, index + 1));
                        statement.setString(6, kodeProdi);
                        statement.addBatch();
                    }
                }
            }
            statement.executeBatch();
        }
    }

    private static void updateDosenJabatan(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("UPDATE dosen SET jabatan = 'DOSEN'");
            statement.executeUpdate("""
                    UPDATE dosen
                    SET jabatan = 'KAPRODI'
                    WHERE kode_dosen IN (SELECT kode_dosen_prodi FROM prodi)
                    """);
            statement.executeUpdate("""
                    UPDATE dosen
                    SET jabatan = 'DOSEN_WALI'
                    WHERE kode_dosen IN (SELECT DISTINCT kode_dosen_wali FROM mahasiswa)
                      AND kode_dosen NOT IN (SELECT kode_dosen_prodi FROM prodi)
                    """);
        }
    }

    private static void seedPembayaranUkt(Connection connection) throws SQLException {
        String sql = """
                INSERT INTO pembayaran_ukt (nim, semester, tahun_akademik, status_lunas, tanggal_bayar)
                VALUES (?, ?, '2025/2026', ?, IF(? = 1, CURRENT_DATE, NULL))
                ON DUPLICATE KEY UPDATE
                    status_lunas = VALUES(status_lunas),
                    tanggal_bayar = VALUES(tanggal_bayar)
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            int i = 0;
            for (MahasiswaSeed seed : mahasiswaSeeds()) {
                for (int semester = 1; semester <= 2; semester++) {
                    boolean paid = isPaidSeed(i, semester);
                    statement.setString(1, seed.nim());
                    statement.setInt(2, semester);
                    statement.setBoolean(3, paid);
                    statement.setBoolean(4, paid);
                    statement.addBatch();
                }
                i++;
            }
            statement.executeBatch();
        }

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("""
                    UPDATE krs k
                    LEFT JOIN pembayaran_ukt u ON u.nim = k.nim
                        AND u.semester = k.semester
                        AND u.tahun_akademik = k.tahun_akademik
                    SET k.ukt_lunas = IF(COALESCE(u.status_lunas, 0) = 1, 1, 0),
                        k.acc_dosen_wali = IF(COALESCE(u.status_lunas, 0) = 1 AND k.is_mengulang = 0, 1, 0),
                        k.acc_dosen_prodi = IF(COALESCE(u.status_lunas, 0) = 1 AND k.is_mengulang = 1, 1, 0),
                        k.acc_at = IF(COALESCE(u.status_lunas, 0) = 1, COALESCE(k.acc_at, CURRENT_TIMESTAMP), NULL)
                    """);
            statement.executeUpdate("""
                    UPDATE krs k
                    JOIN pembayaran_ukt u ON u.nim = k.nim
                        AND u.semester = k.semester
                        AND u.tahun_akademik = k.tahun_akademik
                        AND u.status_lunas = 1
                    SET k.ukt_lunas = 1,
                        k.acc_dosen_wali = IF(k.is_mengulang = 0, 1, 0),
                        k.acc_dosen_prodi = IF(k.is_mengulang = 1, 1, 0),
                        k.acc_at = COALESCE(k.acc_at, CURRENT_TIMESTAMP)
                    """);
        }
    }

    private static void seedKrsAndNilai(Connection connection) throws SQLException {
        connection.setAutoCommit(false);
        try {
            int operatorId = userId(connection, "operator");
            List<MahasiswaSeed> seeds = mahasiswaSeeds();
            for (int i = 0; i < seeds.size(); i++) {
                MahasiswaSeed seed = seeds.get(i);
                for (int semester = 1; semester <= 3; semester++) {
                    for (int courseNumber = 1; courseNumber <= 8; courseNumber++) {
                        int mkSemester = semester;
                        boolean forcedRepeat = semester == 3 && isRepeatStudentSeed(i) && courseNumber == 1;
                        if (forcedRepeat) {
                            mkSemester = 1;
                        }
                        String kodeMk = kodeMk(seed.kodeProdi(), mkSemester, courseNumber);
                        int krsId = insertKrs(connection, seed.nim(), kodeMk,
                                kodeDosenForCourse(seed.kodeProdi(), mkSemester, courseNumber),
                                semester, "2025/2026", forcedRepeat, operatorId);
                        GradeComponents komponen = seededGradeComponents(seed.nim(), semester, courseNumber, kodeMk);
                        insertNilai(connection, krsId, seed.nim(), kodeMk,
                                kodeDosenForCourse(seed.kodeProdi(), mkSemester, courseNumber),
                                semester, "2025/2026",
                                komponen.nilaiAbsensi(), komponen.nilaiTugas(), komponen.nilaiKuis(),
                                komponen.nilaiUts(), komponen.nilaiUas(), operatorId);
                    }
                }
            }
            connection.commit();
        } catch (SQLException ex) {
            connection.rollback();
            throw ex;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    private record DosenSeed(String kode, String nama, String keahlian, String alamat) {
    }

    private record MahasiswaSeed(String nim, String nama, String jenisKelamin, String alamat,
                                 String kodeProdi, String namaProdi, int angkatan,
                                 String kodeKelas, String kodeDosenWali) {
    }

    private record GradeComponents(double nilaiAbsensi, double nilaiTugas, double nilaiKuis,
                                   double nilaiUts, double nilaiUas) {
    }

    private static List<DosenSeed> DOSEN_SEEDS;
    private static List<MahasiswaSeed> MAHASISWA_SEEDS;

    private static List<DosenSeed> dosenSeeds() {
        if (DOSEN_SEEDS != null) {
            return DOSEN_SEEDS;
        }
        List<DosenSeed> list = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            String kode = String.format("DSN%03d", i);
            String nama = nameForIndex(i * 3);
            String keahlian = KEAHLIAN[i % KEAHLIAN.length];
            String alamat = addressForIndex(i + 20, "Bandung");
            list.add(new DosenSeed(kode, nama, keahlian, alamat));
        }
        DOSEN_SEEDS = list;
        return list;
    }

    private static List<MahasiswaSeed> mahasiswaSeeds() {
        if (MAHASISWA_SEEDS != null) {
            return MAHASISWA_SEEDS;
        }
        List<MahasiswaSeed> list = new ArrayList<>();
        int[] angkatanYears = {2023, 2024, 2025};
        int[] sequencePerYear = {1, 1, 1};
        int studentIndex = 0;
        for (String[] prodi : PRODI) {
            for (int i = 0; i < 50; i++) {
                int yearIndex = i % angkatanYears.length;
                int year = angkatanYears[yearIndex];
                int seq = sequencePerYear[yearIndex]++;
                String nim = String.format("%02d%03d", year % 100, seq);
                String nama = nameForIndex(studentIndex);
                String gender = genderForIndex(studentIndex);
                String alamat = addressForIndex(studentIndex, "Bandung");
                String kodeKelas = kelasForStudent(studentIndex, prodi[0], year);
                String kodeDosenWali = kodeDosenWaliForStudent(studentIndex);
                list.add(new MahasiswaSeed(nim, nama, gender, alamat,
                        prodi[0], prodi[1], year, kodeKelas, kodeDosenWali));
                studentIndex++;
            }
        }
        MAHASISWA_SEEDS = list;
        return list;
    }

    private static int userId(Connection connection, String username) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("SELECT id FROM users WHERE username = ?")) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("id");
                }
            }
        }
        throw new SQLException("User seed tidak ditemukan: " + username);
    }

    private static int insertKrs(Connection connection, String nim, String kodeMk, String kodeDosen, int semester,
                                 String tahunAkademik, boolean mengulang, int createdBy) throws SQLException {
        boolean uktLunas = isUktPaid(connection, nim, semester, tahunAkademik);
        boolean accDosenWali = uktLunas && !mengulang;
        boolean accKaprodi = uktLunas && mengulang;
        String insertSql = """
                INSERT IGNORE INTO krs
                (nim, kode_mk, kode_dosen, semester, tahun_akademik, is_mengulang,
                 ukt_lunas, acc_dosen_wali, acc_dosen_prodi, acc_at, created_by)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, IF(? = 1, CURRENT_TIMESTAMP, NULL), ?)
                """;
        try (PreparedStatement statement = connection.prepareStatement(insertSql)) {
            statement.setString(1, nim);
            statement.setString(2, kodeMk);
            statement.setString(3, kodeDosen);
            statement.setInt(4, semester);
            statement.setString(5, tahunAkademik);
            statement.setBoolean(6, mengulang);
            statement.setBoolean(7, uktLunas);
            statement.setBoolean(8, accDosenWali);
            statement.setBoolean(9, accKaprodi);
            statement.setBoolean(10, uktLunas);
            statement.setInt(11, createdBy);
            statement.executeUpdate();
        }

        String selectSql = """
                SELECT id FROM krs
                WHERE nim = ? AND kode_mk = ? AND semester = ? AND tahun_akademik = ?
                """;
        try (PreparedStatement statement = connection.prepareStatement(selectSql)) {
            statement.setString(1, nim);
            statement.setString(2, kodeMk);
            statement.setInt(3, semester);
            statement.setString(4, tahunAkademik);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("id");
                }
            }
        }
        throw new SQLException("KRS gagal dibuat untuk NIM " + nim + " dan mata kuliah " + kodeMk);
    }

    private static boolean isUktPaid(Connection connection, String nim, int semester, String tahunAkademik)
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

    private static void insertNilai(Connection connection, int krsId, String nim, String kodeMk, String kodeDosen,
                                    int semester, String tahunAkademik,
                                    double nilaiAbsensi, double nilaiTugas, double nilaiKuis,
                                    double nilaiUts, double nilaiUas, int createdBy)
            throws SQLException {
        double nilaiAkhir = round2((nilaiUas * 0.30)
                + (nilaiUts * 0.25)
                + (nilaiTugas * 0.20)
                + (nilaiKuis * 0.10)
                + (nilaiAbsensi * 0.15));
        String nilaiHuruf = gradeFromAkhir(nilaiAkhir);
        double bobot = gradeToBobot(nilaiHuruf);
        String sql = """
                INSERT IGNORE INTO nilai
                (krs_id, nim, kode_mk, kode_dosen, semester, tahun_akademik,
                 nilai_absensi, nilai_tugas, nilai_kuis, nilai_uts, nilai_uas, nilai_akhir,
                 nilai_huruf, bobot, created_by)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, krsId);
            statement.setString(2, nim);
            statement.setString(3, kodeMk);
            statement.setString(4, kodeDosen);
            statement.setInt(5, semester);
            statement.setString(6, tahunAkademik);
            statement.setDouble(7, nilaiAbsensi);
            statement.setDouble(8, nilaiTugas);
            statement.setDouble(9, nilaiKuis);
            statement.setDouble(10, nilaiUts);
            statement.setDouble(11, nilaiUas);
            statement.setDouble(12, nilaiAkhir);
            statement.setString(13, nilaiHuruf);
            statement.setDouble(14, bobot);
            statement.setInt(15, createdBy);
            statement.executeUpdate();
        }
    }

    private static void ensureNilaiVariation(Connection connection) throws SQLException {
        String selectSql = """
                SELECT id, nim, semester, kode_mk
                FROM nilai
                WHERE nilai_absensi = nilai_tugas
                  AND nilai_tugas = nilai_kuis
                  AND nilai_kuis = nilai_uts
                  AND nilai_uts = nilai_uas
                  AND nilai_absensi IN (45, 55, 65, 72, 77, 82, 90)
                """;
        String updateSql = """
                UPDATE nilai
                SET nilai_absensi = ?, nilai_tugas = ?, nilai_kuis = ?, nilai_uts = ?, nilai_uas = ?,
                    nilai_akhir = ?, nilai_huruf = ?, bobot = ?
                WHERE id = ?
                """;
        try (PreparedStatement select = connection.prepareStatement(selectSql);
             ResultSet resultSet = select.executeQuery();
             PreparedStatement update = connection.prepareStatement(updateSql)) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String nim = resultSet.getString("nim");
                int semester = resultSet.getInt("semester");
                String kodeMk = resultSet.getString("kode_mk");
                int courseNumber = parseCourseNumber(kodeMk);
                GradeComponents komponen = seededGradeComponents(nim, semester, courseNumber, kodeMk);
                double nilaiAkhir = round2((komponen.nilaiUas() * 0.30)
                        + (komponen.nilaiUts() * 0.25)
                        + (komponen.nilaiTugas() * 0.20)
                        + (komponen.nilaiKuis() * 0.10)
                        + (komponen.nilaiAbsensi() * 0.15));
                String nilaiHuruf = gradeFromAkhir(nilaiAkhir);
                double bobot = gradeToBobot(nilaiHuruf);
                update.setDouble(1, komponen.nilaiAbsensi());
                update.setDouble(2, komponen.nilaiTugas());
                update.setDouble(3, komponen.nilaiKuis());
                update.setDouble(4, komponen.nilaiUts());
                update.setDouble(5, komponen.nilaiUas());
                update.setDouble(6, nilaiAkhir);
                update.setString(7, nilaiHuruf);
                update.setDouble(8, bobot);
                update.setInt(9, id);
                update.addBatch();
            }
            update.executeBatch();
        }
    }

    private static GradeComponents seededGradeComponents(String nim, int semester, int courseNumber, String kodeMk) {
        int seed = Math.abs((nim + "|" + semester + "|" + courseNumber + "|" + kodeMk).hashCode());
        double target = seededFinalTarget(nim, semester, courseNumber, kodeMk, seed);
        double nilaiAbsensi = round2(clampScore(target + 5 + seedNoise(seed, 1, -3.5, 3.5)));
        double nilaiTugas = round2(clampScore(target + 2 + seedNoise(seed, 2, -5.0, 5.0)));
        double nilaiKuis = round2(clampScore(target - 2 + seedNoise(seed, 3, -6.0, 6.0)));
        double nilaiUts = round2(clampScore(target + seedNoise(seed, 4, -4.5, 4.5)));
        double nilaiUas = round2(clampScore(target + 3 + seedNoise(seed, 5, -4.0, 4.0)));
        return new GradeComponents(nilaiAbsensi, nilaiTugas, nilaiKuis, nilaiUts, nilaiUas);
    }

    private static double seededFinalTarget(String nim, int semester, int courseNumber, String kodeMk, int seed) {
        int nimNumber;
        try {
            nimNumber = Integer.parseInt(nim);
        } catch (NumberFormatException ex) {
            nimNumber = Math.abs(nim.hashCode());
        }
        double studentBias = ((nimNumber % 11) - 5) * 0.9;
        double semesterTrend = (semester - 1) * 1.8;
        double courseBias = ((courseNumber % 7) - 3) * 0.5;
        double kodeBias = (Math.floorMod(kodeMk.hashCode(), 9) - 4) * 0.4;
        double randomLike = seedNoise(seed, 0, -3.0, 3.0);
        return clampScore(71 + studentBias + semesterTrend + courseBias + kodeBias + randomLike);
    }

    private static double clampScore(double value) {
        return Math.max(45, Math.min(98, value));
    }

    private static double seedNoise(int seed, int salt, double min, double max) {
        int mixed = seed ^ (salt * 0x45d9f3b);
        mixed ^= (mixed >>> 16);
        mixed *= 0x7feb352d;
        mixed ^= (mixed >>> 15);
        mixed *= 0x846ca68b;
        mixed ^= (mixed >>> 16);
        int normalized = Math.floorMod(mixed, 10_000);
        double ratio = normalized / 9_999.0;
        return min + ((max - min) * ratio);
    }

    private static int parseCourseNumber(String kodeMk) {
        if (kodeMk == null || kodeMk.length() < 2) {
            return 1;
        }
        String suffix = kodeMk.substring(kodeMk.length() - 2);
        try {
            int parsed = Integer.parseInt(suffix);
            return parsed > 0 ? parsed : 1;
        } catch (NumberFormatException ex) {
            return 1;
        }
    }

    private static String kodeMk(String kodeProdi, int semester, int courseNumber) {
        return "MK" + kodeProdi + semester + String.format("%02d", courseNumber);
    }

    private static String courseNameForProdi(String kodeProdi, int semester, int courseNumber) {
        String[][] names = switch (kodeProdi) {
            case "TI" -> TI_COURSE_NAMES;
            case "DKV" -> DKV_COURSE_NAMES;
            case "RETAIL" -> RETAIL_COURSE_NAMES;
            default -> TIF_COURSE_NAMES;
        };
        return names[semester - 1][courseNumber - 1];
    }

    private static String kodeDosenForCourse(String kodeProdi, int semester, int courseNumber) {
        int seed = Math.abs((kodeProdi.hashCode() * 31) + (semester * 17) + (courseNumber * 7));
        int index = (seed % 100) + 1;
        return String.format("DSN%03d", index);
    }

    private static String kodeDosenWaliForStudent(int index) {
        int dosenIndex = (index % 80) + 1;
        if (dosenIndex >= 13 && dosenIndex <= 16) {
            dosenIndex += 4;
        }
        return String.format("DSN%03d", dosenIndex);
    }

    private static String genderForIndex(int index) {
        return index % 2 == 0 ? "Laki-laki" : "Perempuan";
    }

    private static String nameForIndex(int index) {
        String first = FIRST_NAMES[index % FIRST_NAMES.length];
        String last = LAST_NAMES[(index * 3) % LAST_NAMES.length];
        return first + " " + last;
    }

    private static String addressForIndex(int index, String city) {
        String street = STREET_NAMES[index % STREET_NAMES.length];
        int number = (index % 120) + 1;
        return "Jl. " + street + " No. " + number + " " + city;
    }

    private static String kelasForStudent(int index, String kodeProdi, int angkatan) {
        String year = String.format("%02d", angkatan % 100);
        return switch (kodeProdi) {
            case "TIF" -> "TIF " + year + switch (index % 4) {
                case 0 -> "A";
                case 1 -> "B";
                case 2 -> "C";
                default -> "D";
            };
            case "DKV" -> "DKV " + year + (index % 3 == 0 ? "A" : index % 3 == 1 ? "B" : "C");
            case "TI" -> "TI " + year + (index % 2 == 0 ? "E" : "F");
            default -> "RETAIL " + year + (index % 2 == 0 ? "A" : "B");
        };
    }

    private static boolean isPaidSeed(int index, int semester) {
        if (semester == 1) {
            return index % 4 != 3;
        }
        return index % 3 == 0;
    }

    private static boolean isRepeatStudentSeed(int index) {
        return index % 9 == 0;
    }

    private static double gradeToBobot(String grade) {
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

    private static String gradeFromAkhir(double nilaiAkhir) {
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

    private static double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
