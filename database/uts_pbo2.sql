CREATE DATABASE IF NOT EXISTS uts_pbo2
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE uts_pbo2;

CREATE TABLE IF NOT EXISTS users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(40) NOT NULL UNIQUE,
  password_hash CHAR(64) NOT NULL,
  role ENUM('ADMIN','OPERATOR') NOT NULL,
  nama_lengkap VARCHAR(100) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS dosen (
  kode_dosen VARCHAR(10) PRIMARY KEY,
  nama_dosen VARCHAR(100) NOT NULL,
  keahlian VARCHAR(100) NOT NULL,
  alamat VARCHAR(180) NOT NULL DEFAULT '-',
  jabatan ENUM('DOSEN','DOSEN_WALI','KAPRODI') NOT NULL DEFAULT 'DOSEN',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS prodi (
  kode_prodi VARCHAR(10) PRIMARY KEY,
  nama_prodi VARCHAR(80) NOT NULL UNIQUE,
  kode_kelas VARCHAR(10) NOT NULL UNIQUE,
  kode_dosen_prodi VARCHAR(10) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS mahasiswa (
  nim CHAR(5) PRIMARY KEY,
  nama VARCHAR(100) NOT NULL,
  jenis_kelamin ENUM('Laki-laki','Perempuan') NOT NULL DEFAULT 'Laki-laki',
  alamat VARCHAR(180) NOT NULL DEFAULT '-',
  prodi VARCHAR(80) NOT NULL DEFAULT 'Teknik Informatika',
  kode_prodi VARCHAR(10) NOT NULL DEFAULT 'TIF',
  kode_kelas VARCHAR(24) NOT NULL DEFAULT 'TIF 25A CID',
  kode_dosen_wali VARCHAR(10) NOT NULL DEFAULT 'DSN001',
  angkatan YEAR NOT NULL,
  status ENUM('AKTIF','NONAKTIF') NOT NULL DEFAULT 'AKTIF',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT chk_mahasiswa_nim CHECK (nim REGEXP '^[0-9]{5}$')
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS matakuliah (
  kode_mk VARCHAR(16) PRIMARY KEY,
  nama_mk VARCHAR(120) NOT NULL,
  sks TINYINT NOT NULL,
  semester TINYINT NOT NULL,
  kode_dosen VARCHAR(10) NOT NULL,
  kode_prodi VARCHAR(10) NOT NULL DEFAULT 'TIF',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT chk_mk_sks CHECK (sks BETWEEN 1 AND 6),
  CONSTRAINT chk_mk_semester CHECK (semester BETWEEN 1 AND 8),
  UNIQUE KEY uq_mk_prodi_semester_nama (kode_prodi, semester, nama_mk)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

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
  UNIQUE KEY uq_krs_mahasiswa_mk_semester_tahun (nim, kode_mk, semester, tahun_akademik),
  INDEX idx_krs_nim (nim),
  INDEX idx_krs_semester (semester)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS pembayaran_ukt (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nim CHAR(5) NOT NULL,
  semester TINYINT NOT NULL,
  tahun_akademik VARCHAR(9) NOT NULL,
  status_lunas TINYINT(1) NOT NULL DEFAULT 0,
  tanggal_bayar DATE NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT chk_ukt_semester CHECK (semester BETWEEN 1 AND 8),
  UNIQUE KEY uq_ukt_mahasiswa_semester_tahun (nim, semester, tahun_akademik)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

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
  UNIQUE KEY uq_nilai_mahasiswa_mk_semester_tahun (nim, kode_mk, semester, tahun_akademik)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE mahasiswa ADD COLUMN IF NOT EXISTS kode_prodi VARCHAR(10) NOT NULL DEFAULT 'TIF';
ALTER TABLE mahasiswa ADD COLUMN IF NOT EXISTS jenis_kelamin ENUM('Laki-laki','Perempuan') NOT NULL DEFAULT 'Laki-laki';
ALTER TABLE mahasiswa ADD COLUMN IF NOT EXISTS alamat VARCHAR(180) NOT NULL DEFAULT '-';
ALTER TABLE mahasiswa ADD COLUMN IF NOT EXISTS kode_kelas VARCHAR(24) NOT NULL DEFAULT 'TIF 25A CID';
ALTER TABLE mahasiswa ADD COLUMN IF NOT EXISTS kode_dosen_wali VARCHAR(10) NOT NULL DEFAULT 'DSN001';
ALTER TABLE mahasiswa MODIFY COLUMN kode_kelas VARCHAR(24) NOT NULL DEFAULT 'TIF 25A CID';
ALTER TABLE dosen ADD COLUMN IF NOT EXISTS alamat VARCHAR(180) NOT NULL DEFAULT '-';
ALTER TABLE dosen ADD COLUMN IF NOT EXISTS jabatan ENUM('DOSEN','DOSEN_WALI','KAPRODI') NOT NULL DEFAULT 'DOSEN';
ALTER TABLE matakuliah ADD COLUMN IF NOT EXISTS kode_prodi VARCHAR(10) NOT NULL DEFAULT 'TIF';
ALTER TABLE matakuliah MODIFY COLUMN kode_mk VARCHAR(16);
ALTER TABLE matakuliah MODIFY COLUMN nama_mk VARCHAR(120) NOT NULL;
ALTER TABLE krs MODIFY COLUMN kode_mk VARCHAR(16);
ALTER TABLE nilai MODIFY COLUMN kode_mk VARCHAR(16);
ALTER TABLE krs ADD COLUMN IF NOT EXISTS ukt_lunas TINYINT(1) NOT NULL DEFAULT 0;
ALTER TABLE krs ADD COLUMN IF NOT EXISTS acc_dosen_wali TINYINT(1) NOT NULL DEFAULT 0;
ALTER TABLE krs ADD COLUMN IF NOT EXISTS acc_dosen_prodi TINYINT(1) NOT NULL DEFAULT 0;
ALTER TABLE krs ADD COLUMN IF NOT EXISTS acc_at TIMESTAMP NULL;
UPDATE matakuliah
SET nama_mk = TRIM(
  REPLACE(
    REPLACE(
      REPLACE(
        REPLACE(nama_mk, 'Teknik Informatika - ', ''),
      'Teknik Industri - ', ''),
    'DKV - ', ''),
  'Retail - ', '')
);

INSERT IGNORE INTO users (username, password_hash, role, nama_lengkap) VALUES
('admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'ADMIN', 'Administrator'),
('operator', 'ec6e1c25258002eb1c67d15c7f45da7945fa4c58778fd7d88faa5e53e3b4698d', 'OPERATOR', 'Operator Akademik');

INSERT IGNORE INTO dosen (kode_dosen, nama_dosen, keahlian, alamat, jabatan) VALUES
('DSN001','Iwan Ridwan','Pemrograman Berorientasi Objek','Jl. Asia Afrika No. 12 Bandung','DOSEN'),
('DSN002','Rina Lestari','Basis Data','Jl. Cibaduyut Raya No. 45 Bandung','DOSEN'),
('DSN003','Agus Pratama','Algoritma','Jl. Buah Batu No. 21 Bandung','DOSEN'),
('DSN004','Siti Nurhaliza','Jaringan Komputer','Jl. Dipatiukur No. 88 Bandung','DOSEN'),
('DSN005','Dedi Setiawan','Sistem Operasi','Jl. Soekarno Hatta No. 101 Bandung','DOSEN'),
('DSN006','Maya Kartika','Rekayasa Perangkat Lunak','Jl. Antapani Lama No. 7 Bandung','DOSEN'),
('DSN007','Budi Santoso','Matematika Komputasi','Jl. Gegerkalong Hilir No. 16 Bandung','DOSEN'),
('DSN008','Nadia Faradila','Kecerdasan Buatan','Jl. Cikutra Barat No. 31 Bandung','DOSEN'),
('DSN009','Rizky Ramadhan','Keamanan Informasi','Jl. Setiabudi No. 9 Bandung','DOSEN'),
('DSN010','Fitri Amalia','Interaksi Manusia Komputer','Jl. Pasteur No. 14 Bandung','DOSEN'),
('DSN011','Hendra Wijaya','Mobile Programming','Jl. Kebon Jati No. 6 Bandung','DOSEN'),
('DSN012','Lukman Hakim','Cloud Computing','Jl. Sudirman No. 19 Bandung','DOSEN'),
('DSN013','Aulia Rahman','Data Mining','Jl. Merdeka No. 5 Bandung','KAPRODI'),
('DSN014','Putri Maharani','Manajemen Proyek TI','Jl. Dago No. 11 Bandung','KAPRODI'),
('DSN015','Fajar Nugroho','Statistika','Jl. Pahlawan No. 3 Bandung','KAPRODI'),
('DSN016','Citra Permata','Etika Profesi','Jl. Braga No. 8 Bandung','KAPRODI');

INSERT INTO prodi (kode_prodi, nama_prodi, kode_kelas, kode_dosen_prodi) VALUES
('TIF','Teknik Informatika','TIF','DSN013'),
('TI','Teknik Industri','TI','DSN014'),
('DKV','DKV','DKV','DSN015'),
('RETAIL','Retail','RETAIL','DSN016')
ON DUPLICATE KEY UPDATE
  nama_prodi = VALUES(nama_prodi),
  kode_kelas = VALUES(kode_kelas),
  kode_dosen_prodi = VALUES(kode_dosen_prodi);

INSERT IGNORE INTO mahasiswa
(nim, nama, prodi, kode_prodi, kode_kelas, kode_dosen_wali, angkatan, status) VALUES
('23001','Aditya Pratama','Teknik Informatika','TIF','TIF','DSN001',2023,'AKTIF'),
('23002','Bella Safitri','Teknik Industri','TI','TI','DSN002',2023,'AKTIF'),
('23003','Candra Wijaya','DKV','DKV','DKV','DSN003',2023,'AKTIF'),
('23004','Dewi Lestari','Retail','RETAIL','RETAIL','DSN004',2023,'AKTIF'),
('23005','Eka Saputra','Teknik Informatika','TIF','TIF','DSN005',2023,'AKTIF'),
('23006','Farhan Maulana','Teknik Industri','TI','TI','DSN006',2023,'AKTIF'),
('23007','Gita Amelia','DKV','DKV','DKV','DSN007',2023,'AKTIF'),
('23008','Hafiz Ramadhan','Retail','RETAIL','RETAIL','DSN008',2023,'AKTIF'),
('23009','Indah Puspita','Teknik Informatika','TIF','TIF','DSN009',2023,'AKTIF'),
('23010','Joko Nugraha','Teknik Industri','TI','TI','DSN010',2023,'AKTIF'),
('23011','Kartika Sari','DKV','DKV','DKV','DSN011',2023,'AKTIF'),
('23012','Laras Wulandari','Retail','RETAIL','RETAIL','DSN012',2023,'AKTIF'),
('23013','Muhammad Iqbal','Teknik Informatika','TIF','TIF','DSN013',2023,'AKTIF'),
('23014','Nabila Putri','Teknik Industri','TI','TI','DSN014',2023,'AKTIF'),
('23015','Oki Firmansyah','DKV','DKV','DKV','DSN015',2023,'AKTIF'),
('23016','Pandu Herlambang','Retail','RETAIL','RETAIL','DSN016',2023,'AKTIF'),
('23017','Qori Azizah','Teknik Informatika','TIF','TIF','DSN001',2023,'AKTIF'),
('23018','Rafi Alfarizi','Teknik Industri','TI','TI','DSN002',2023,'AKTIF'),
('23019','Salsa Maharani','DKV','DKV','DKV','DSN003',2023,'AKTIF'),
('23020','Tegar Pamungkas','Retail','RETAIL','RETAIL','DSN004',2023,'AKTIF'),
('23021','Ulfa Nuraini','Teknik Informatika','TIF','TIF','DSN005',2023,'AKTIF'),
('23022','Vina Apriliyani','Teknik Industri','TI','TI','DSN006',2023,'AKTIF'),
('23023','Wahyu Saputra','DKV','DKV','DKV','DSN007',2023,'AKTIF'),
('23024','Yuni Kartika','Retail','RETAIL','RETAIL','DSN008',2023,'AKTIF');

UPDATE mahasiswa
SET jenis_kelamin = IF(MOD(CAST(nim AS UNSIGNED) - 23001, 2) = 0, 'Laki-laki', 'Perempuan'),
  alamat = CASE MOD(CAST(nim AS UNSIGNED) - 23001, 8)
    WHEN 0 THEN 'Jl. Asia Afrika No. 12 Bandung'
    WHEN 1 THEN 'Jl. Cibaduyut Raya No. 45 Bandung'
    WHEN 2 THEN 'Jl. Buah Batu No. 21 Bandung'
    WHEN 3 THEN 'Jl. Dipatiukur No. 88 Bandung'
    WHEN 4 THEN 'Jl. Soekarno Hatta No. 101 Bandung'
    WHEN 5 THEN 'Jl. Antapani Lama No. 7 Bandung'
    WHEN 6 THEN 'Jl. Gegerkalong Hilir No. 16 Bandung'
    ELSE 'Jl. Cikutra Barat No. 31 Bandung'
  END,
  prodi = CASE MOD(CAST(nim AS UNSIGNED) - 23001, 4)
    WHEN 0 THEN 'Teknik Informatika'
    WHEN 1 THEN 'Teknik Industri'
    WHEN 2 THEN 'DKV'
    ELSE 'Retail'
  END,
  kode_prodi = CASE MOD(CAST(nim AS UNSIGNED) - 23001, 4)
    WHEN 0 THEN 'TIF'
    WHEN 1 THEN 'TI'
    WHEN 2 THEN 'DKV'
    ELSE 'RETAIL'
  END,
  angkatan = CASE MOD(CAST(nim AS UNSIGNED) - 23001, 4)
    WHEN 0 THEN 2025
    WHEN 1 THEN IF(MOD(CAST(nim AS UNSIGNED) - 23001, 8) = 1, 2021, 2022)
    WHEN 2 THEN IF(MOD(CAST(nim AS UNSIGNED) - 23001, 8) = 2, 2024, 2023)
    ELSE IF(MOD(CAST(nim AS UNSIGNED) - 23001, 8) = 3, 2024, 2025)
  END,
  kode_kelas = CASE MOD(CAST(nim AS UNSIGNED) - 23001, 4)
    WHEN 0 THEN CONCAT('TIF 25A ', IF(MOD(FLOOR((CAST(nim AS UNSIGNED) - 23001) / 4), 2) = 0, 'CID', 'CNS'))
    WHEN 1 THEN CONCAT('TI ', IF(MOD(CAST(nim AS UNSIGNED) - 23001, 8) = 1, '21', '22'), 'F')
    WHEN 2 THEN CONCAT('DKV ', IF(MOD(CAST(nim AS UNSIGNED) - 23001, 8) = 2, '24', '23'),
                       CASE MOD(CAST(nim AS UNSIGNED) - 23001, 3)
                         WHEN 0 THEN 'A'
                         WHEN 1 THEN 'B'
                         ELSE 'C'
                       END)
    ELSE CONCAT('RETAIL ', IF(MOD(CAST(nim AS UNSIGNED) - 23001, 8) = 3, '24', '25'), 'B')
  END,
  kode_dosen_wali = CONCAT('DSN', LPAD(MOD(CAST(nim AS UNSIGNED) - 23001, 12) + 1, 3, '0')),
  status = 'AKTIF'
WHERE nim BETWEEN '23001' AND '23024';

INSERT IGNORE INTO matakuliah (kode_mk, nama_mk, sks, semester, kode_dosen)
SELECT
  CONCAT('MK', s.semester, LPAD(n.nomor, 2, '0')),
  CONCAT('Mata Kuliah Semester ', s.semester, ' - ', n.nomor),
  IF(s.semester = 8 AND n.nomor = 1, 6, 3),
  s.semester,
  CONCAT('DSN', LPAD(MOD(((s.semester - 1) * 8) + (n.nomor - 1), 16) + 1, 3, '0'))
FROM
  (SELECT 1 semester UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
   UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8) s
CROSS JOIN
  (SELECT 1 nomor UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
   UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8) n;

INSERT INTO pembayaran_ukt (nim, semester, tahun_akademik, status_lunas, tanggal_bayar)
SELECT seed.nim, seed.semester, seed.tahun_akademik, seed.status_lunas, seed.tanggal_bayar
FROM (
  SELECT m.nim, s.semester, '2025/2026' AS tahun_akademik,
         IF(s.semester = 1,
            MOD(CAST(m.nim AS UNSIGNED) - 23001, 4) <> 3,
            MOD(CAST(m.nim AS UNSIGNED) - 23001, 3) = 0) AS status_lunas,
         IF(IF(s.semester = 1,
               MOD(CAST(m.nim AS UNSIGNED) - 23001, 4) <> 3,
               MOD(CAST(m.nim AS UNSIGNED) - 23001, 3) = 0), CURRENT_DATE, NULL) AS tanggal_bayar
  FROM mahasiswa m
  CROSS JOIN (SELECT 1 semester UNION ALL SELECT 2) s
) seed
ON DUPLICATE KEY UPDATE
  status_lunas = VALUES(status_lunas),
  tanggal_bayar = VALUES(tanggal_bayar);

INSERT IGNORE INTO krs
(nim, kode_mk, kode_dosen, semester, tahun_akademik, is_mengulang,
 ukt_lunas, acc_dosen_wali, acc_dosen_prodi, acc_at, created_by)
SELECT m.nim, mk.kode_mk, mk.kode_dosen, 1, '2025/2026', 0,
       IF(u.status_lunas = 1, 1, 0),
       IF(u.status_lunas = 1, 1, 0),
       0,
       IF(u.status_lunas = 1, CURRENT_TIMESTAMP, NULL),
       (SELECT id FROM users WHERE username = 'operator' LIMIT 1)
FROM mahasiswa m
JOIN matakuliah mk ON mk.semester = 1
LEFT JOIN pembayaran_ukt u ON u.nim = m.nim
  AND u.semester = 1
  AND u.tahun_akademik = '2025/2026';

UPDATE krs k
LEFT JOIN pembayaran_ukt u ON u.nim = k.nim
  AND u.semester = k.semester
  AND u.tahun_akademik = k.tahun_akademik
SET k.ukt_lunas = IF(COALESCE(u.status_lunas, 0) = 1, 1, 0),
    k.acc_dosen_wali = IF(COALESCE(u.status_lunas, 0) = 1 AND k.is_mengulang = 0, 1, 0),
    k.acc_dosen_prodi = IF(COALESCE(u.status_lunas, 0) = 1 AND k.is_mengulang = 1, 1, 0),
    k.acc_at = IF(COALESCE(u.status_lunas, 0) = 1, COALESCE(k.acc_at, CURRENT_TIMESTAMP), NULL);

INSERT IGNORE INTO krs
(nim, kode_mk, kode_dosen, semester, tahun_akademik, is_mengulang,
 ukt_lunas, acc_dosen_wali, acc_dosen_prodi, acc_at, created_by)
SELECT r.nim, r.kode_mk, mk.kode_dosen, 2, '2025/2026', 1,
       IF(u.status_lunas = 1, 1, 0),
       0,
       IF(u.status_lunas = 1, 1, 0),
       IF(u.status_lunas = 1, CURRENT_TIMESTAMP, NULL),
       (SELECT id FROM users WHERE username = 'operator' LIMIT 1)
FROM (
  SELECT '23001' nim, 'MK101' kode_mk UNION ALL
  SELECT '23003' nim, 'MK101' kode_mk UNION ALL
  SELECT '23006', 'MK102' UNION ALL
  SELECT '23009', 'MK103' UNION ALL
  SELECT '23012', 'MK101' UNION ALL
  SELECT '23015', 'MK102' UNION ALL
  SELECT '23018', 'MK103'
) r
JOIN matakuliah mk ON mk.kode_mk = r.kode_mk
LEFT JOIN pembayaran_ukt u ON u.nim = r.nim
  AND u.semester = 2
  AND u.tahun_akademik = '2025/2026';

INSERT IGNORE INTO nilai
(krs_id, nim, kode_mk, kode_dosen, semester, tahun_akademik,
 nilai_absensi, nilai_tugas, nilai_kuis, nilai_uts, nilai_uas, nilai_akhir,
 nilai_huruf, bobot, created_by)
SELECT
  k.id, k.nim, k.kode_mk, k.kode_dosen, k.semester, k.tahun_akademik,
  t.score, t.score, t.score, t.score, t.score, t.score,
  t.nilai_huruf, t.bobot,
  (SELECT id FROM users WHERE username = 'operator' LIMIT 1)
FROM krs k
JOIN (
  SELECT id,
         CASE MOD(CAST(nim AS UNSIGNED) + CAST(SUBSTRING(kode_mk, 4, 2) AS UNSIGNED), 5)
           WHEN 0 THEN 'A'
           WHEN 1 THEN 'B'
           WHEN 2 THEN 'C'
           WHEN 3 THEN 'D'
           ELSE 'E'
         END AS nilai_huruf,
         CASE MOD(CAST(nim AS UNSIGNED) + CAST(SUBSTRING(kode_mk, 4, 2) AS UNSIGNED), 5)
           WHEN 0 THEN 4
           WHEN 1 THEN 3
           WHEN 2 THEN 2
           WHEN 3 THEN 1
           ELSE 0
         END AS bobot,
         CASE MOD(CAST(nim AS UNSIGNED) + CAST(SUBSTRING(kode_mk, 4, 2) AS UNSIGNED), 5)
           WHEN 0 THEN 90
           WHEN 1 THEN 77
           WHEN 2 THEN 65
           WHEN 3 THEN 55
           ELSE 45
         END AS score
  FROM krs
) t ON t.id = k.id;

CREATE OR REPLACE VIEW v_krs_detail AS
SELECT k.id, k.nim, m.nama, m.jenis_kelamin, m.alamat, m.kode_kelas, p.nama_prodi, m.kode_dosen_wali,
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
JOIN dosen d ON d.kode_dosen = k.kode_dosen;

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
LEFT JOIN nilai n ON n.krs_id = k.id;

CREATE OR REPLACE VIEW ipk AS
SELECT k.id AS krs_id, k.nim, k.kode_mk, k.kode_dosen, k.semester,
       k.tahun_akademik, k.is_mengulang, n.nilai_huruf AS nilai
FROM krs k
LEFT JOIN nilai n ON n.krs_id = k.id;

CREATE OR REPLACE VIEW v_ip_semester AS
SELECT n.nim, m.nama, n.semester,
       ROUND(SUM(mk.sks * n.bobot) / SUM(mk.sks), 2) AS ip,
       SUM(mk.sks) AS total_sks,
       COUNT(*) AS jumlah_matakuliah
FROM nilai n
JOIN mahasiswa m ON m.nim = n.nim
JOIN matakuliah mk ON mk.kode_mk = n.kode_mk
GROUP BY n.nim, m.nama, n.semester;

CREATE OR REPLACE VIEW v_ipk AS
SELECT n.nim, m.nama,
       ROUND(SUM(mk.sks * n.bobot) / SUM(mk.sks), 2) AS ipk,
       SUM(mk.sks) AS total_sks,
       COUNT(*) AS jumlah_matakuliah
FROM nilai n
JOIN mahasiswa m ON m.nim = n.nim
JOIN matakuliah mk ON mk.kode_mk = n.kode_mk
GROUP BY n.nim, m.nama;
