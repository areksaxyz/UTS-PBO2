# ðŸ“š SIAKAD - Sistem Informasi Akademik Desktop

**Aplikasi Manajemen Akademik Terintegrasi Berbasis Java Swing**

[![Status](https://img.shields.io/badge/status-active-success)]()
[![Java Version](https://img.shields.io/badge/java-17+-orange)]()
[![License](https://img.shields.io/badge/license-MIT-blue)]()

## Fitur Utama

- Autentikasi login berbasis role (Admin dan Operator)
- Master Mahasiswa (CRUD) - akses Admin
- Master Dosen (CRUD) - akses Admin
- Master Mata Kuliah (CRUD) - akses Admin
- Setting User (tambah user) - akses Admin
- Transaksi KRS - akses Operator
- Transaksi Nilai - akses Operator
- Ganti Password - akses Admin dan Operator

## Hak Akses Pengguna

### Admin

- Dashboard
- Master Mahasiswa
- Master Dosen
- Master Mata Kuliah
- Setting User
- Ganti Password (dapat mengelola semua akun)

### Operator

- Dashboard
- Transaksi KRS
- Transaksi Nilai
- Ganti Password (akun sendiri saja)

## Screenshot Aplikasi

> Status saat ini: screenshot untuk **Master Dosen**, **Master Mata Kuliah**, dan **Ganti Password** belum tersedia di folder `screenshots/`.

### Layar Login
![Login Screen](./screenshots/01-login-screen.png)

*Akses: Admin & Operator*

### Dashboard Admin
![Admin Dashboard](./screenshots/02-admin-dashboard.png)

*Akses: Admin*

### Modul Master Mahasiswa
![Student Master](./screenshots/03-mahasiswa-master.png)

*Akses: Admin*

### Modul Master Dosen
`Target file: ./screenshots/06-dosen-master.png`

*Akses: Admin*

### Modul Master Mata Kuliah
`Target file: ./screenshots/07-matakuliah-master.png`

*Akses: Admin*

### Modul Ganti Password
`Target file: ./screenshots/08-ganti-password.png`

*Akses: Admin (semua akun), Operator (akun sendiri saja)*

### Transaksi KRS
![KRS Transaction](./screenshots/04-krs-transaksi.png)

*Akses: Operator*

### Transaksi Nilai
![Grade Transaction](./screenshots/05-nilai-transaksi.png)

*Akses: Operator*

### Ringkasan Akses Fitur

| Fitur | Admin | Operator |
|------|:-----:|:--------:|
| Dashboard | Ya | Ya |
| Master Mahasiswa | Ya | Tidak |
| Master Dosen | Ya | Tidak |
| Master Mata Kuliah | Ya | Tidak |
| Setting User | Ya | Tidak |
| Transaksi KRS | Tidak | Ya |
| Transaksi Nilai | Tidak | Ya |
| Ganti Password | Ya | Ya |

---

## Use Case & Alur Kerja Aplikasi

### ðŸ“‹ Use Case Diagram

Diagram berikut menunjukkan semua use case dalam sistem SIAKAD dan interaksi antara aktor (Admin/Operator) dengan sistem:

```mermaid
graph TB
    subgraph SIAKAD["ðŸ« SIAKAD System"]
        Login["ðŸ” Login"]
        MgMahasiswa["ðŸ‘¥ Manage Mahasiswa"]
        MgDosen["ðŸ‘¨â€ðŸ« Manage Dosen"]
        MgMataKuliah["ðŸ“š Manage Mata Kuliah"]
        MgUser["âš™ï¸ Manage User"]
        InputKRS["ðŸ“ Input KRS"]
        InputNilai["ðŸŽ“ Input Nilai"]
        ViewReport["ðŸ“Š View Report/IP IPK"]
        ChangePassword["ðŸ”‘ Change Password"]
        Logout["ðŸ‘‹ Logout"]
    end
    
    Admin["ðŸ‘¤ Admin"]
    Operator["ðŸ‘¥ Operator"]
    System["âš™ï¸ Database System"]
    
    Admin -->|Can Do| Login
    Operator -->|Can Do| Login
    Login -->|Access| MgMahasiswa
    Login -->|Access| MgDosen
    Login -->|Access| MgMataKuliah
    Login -->|Access| MgUser
    Login -->|Access| InputKRS
    Login -->|Access| InputNilai
    Login -->|Access| ViewReport
    Login -->|Access| ChangePassword
    
    Admin -->|Full Access| MgMahasiswa
    Admin -->|Full Access| MgDosen
    Admin -->|Full Access| MgMataKuliah
    Admin -->|Full Access| MgUser
    
    Operator -->|Limited Access| InputKRS
    Operator -->|Limited Access| InputNilai
    Operator -->|Can View| ViewReport
    
    MgMahasiswa -->|CRUD| System
    MgDosen -->|CRUD| System
    MgMataKuliah -->|CRUD| System
    MgUser -->|Create| System
    InputKRS -->|Store| System
    InputNilai -->|Store| System
    ViewReport -->|Query| System
    ChangePassword -->|Update| System
    Logout -->|End Session| System
    
    classDef actor fill:#e1f5ff,stroke:#01579b,stroke-width:2px
    classDef usecase fill:#fff3e0,stroke:#e65100,stroke-width:2px
    classDef system fill:#f3e5f5,stroke:#4a148c,stroke-width:2px
    
    class Admin,Operator actor
    class Login,MgMahasiswa,MgDosen,MgMataKuliah,MgUser,InputKRS,InputNilai,ViewReport,ChangePassword,Logout usecase
    class System system
```

**Penjelasan:**
- ðŸ‘¤ **Admin**: Akses penuh ke semua modul master data
- ðŸ‘¥ **Operator**: Akses terbatas ke transaksi KRS dan Nilai saja
- âš™ï¸ **Database System**: Backend yang menyimpan dan mengelola data

---

### ðŸ”€ Activity Flow Diagram

Diagram berikut menunjukkan alur lengkap dari saat aplikasi dijalankan hingga ditutup:

```mermaid
graph TD
    Start([ðŸŸ¢ Mulai Aplikasi]) --> LaunchApp["Launch SIAKAD App"]
    LaunchApp --> CheckDB{"Database Terhubung?"}
    
    CheckDB -->|âŒ Tidak| CreateDB["ðŸ“¦ Create Database & Tables"]
    CreateDB --> InsertSeed["ðŸŒ± Insert Seed Data"]
    InsertSeed --> ShowLogin["Tampilkan Login Form"]
    
    CheckDB -->|âœ… Ya| ShowLogin
    
    ShowLogin --> EnterCred["ðŸ‘¤ Input Username & Password"]
    EnterCred --> ValidateLogin{"Login Valid?"}
    
    ValidateLogin -->|âŒ Gagal| ErrMsg["âŒ Tampilkan Error"]
    ErrMsg --> EnterCred
    
    ValidateLogin -->|âœ… Sukses| CheckRole{"Cek Role?"}
    
    CheckRole -->|Admin| AdminMenu["ðŸ“Š Admin Dashboard"]
    CheckRole -->|Operator| OpMenu["ðŸ“‹ Operator Dashboard"]
    
    AdminMenu --> AdminChoice{"Pilih Modul?"}
    AdminChoice -->|Master Mahasiswa| MhsAdmin["âž• Lihat/Tambah/Ubah/Hapus Mahasiswa"]
    AdminChoice -->|Master Dosen| DosenAdmin["âž• Lihat/Tambah/Ubah/Hapus Dosen"]
    AdminChoice -->|Master Mata Kuliah| MKAdmin["âž• Lihat/Tambah/Ubah/Hapus Mata Kuliah"]
    AdminChoice -->|Setting User| UserAdmin["âž• Tambah User Baru"]
    AdminChoice -->|Ganti Password| PwdAdmin["ðŸ”‘ Ubah Password"]
    AdminChoice -->|Logout| Logout["ðŸ‘‹ Logout"]
    
    OpMenu --> OpChoice{"Pilih Modul?"}
    OpChoice -->|Transaksi KRS| KrsOp["ðŸ“ Input/Edit KRS"]
    OpChoice -->|Transaksi Nilai| NilaiOp["ðŸŽ“ Input/Edit Nilai"]
    OpChoice -->|View Report| ReportOp["ðŸ“Š Cek IP/IPK"]
    OpChoice -->|Ganti Password| PwdOp["ðŸ”‘ Ubah Password"]
    OpChoice -->|Logout| Logout
    
    MhsAdmin --> SaveCheck{"Simpan Perubahan?"}
    DosenAdmin --> SaveCheck
    MKAdmin --> SaveCheck
    UserAdmin --> SaveCheck
    KrsOp --> SaveCheck
    NilaiOp --> SaveCheck
    ReportOp --> BackMenu{"Kembali ke Menu?"}
    PwdAdmin --> BackMenu
    PwdOp --> BackMenu
    
    SaveCheck -->|âœ… Ya| DBUpdate["ðŸ’¾ Update Database"]
    SaveCheck -->|âŒ Tidak| AdminMenu
    SaveCheck -->|âŒ Tidak| OpMenu
    
    DBUpdate --> BackMenu
    BackMenu -->|âœ… Ya| AdminMenu
    BackMenu -->|âœ… Ya| OpMenu
    BackMenu -->|âŒ Tidak| Logout
    
    Logout --> ClearSession["ðŸ—‘ï¸ Clear Session"]
    ClearSession --> CloseCon["ðŸ”Œ Close Database Connection"]
    CloseCon --> End([ðŸ”´ Aplikasi Ditutup])
    
    style Start fill:#90EE90,stroke:#228B22,stroke-width:3px
    style End fill:#FFB6C6,stroke:#DC143C,stroke-width:3px
    style AdminMenu fill:#E3F2FD,stroke:#1976D2,stroke-width:2px
    style OpMenu fill:#FFF3E0,stroke:#F57C00,stroke-width:2px
    style DBUpdate fill:#F3E5F5,stroke:#7B1FA2,stroke-width:2px
    style ErrMsg fill:#FFEBEE,stroke:#C62828,stroke-width:2px
```

**Alur Utama:**
1. ðŸŸ¢ **Launch**: Aplikasi dimulai dan check koneksi database
2. ðŸ“¦ **Setup**: Jika database belum ada, sistem auto-create dengan seed data
3. ðŸ” **Login**: User memasukkan credentials
4. âœ… **Validasi**: Sistem validasi login dan cek role
5. ðŸ“Š **Dashboard**: Menampilkan menu sesuai role (Admin/Operator)
6. ðŸ”„ **Operasi**: User memilih modul dan melakukan CRUD/Transaksi
7. ðŸ’¾ **Simpan**: Perubahan disimpan ke database
8. ðŸ‘‹ **Logout**: Session berakhir dan koneksi ditutup

---

### ðŸ”— Sequence Diagram - Transaksi KRS (Admin & Operator)

Diagram berikut menunjukkan interaksi detail antara **Admin** dan **Operator** saat melakukan transaksi KRS:

```mermaid
sequenceDiagram
    actor Admin as ðŸ‘¨â€ðŸ’¼ Admin
    actor Operator as ðŸ‘¤ Operator
    participant UI as ðŸ–¥ï¸ KRS Panel
    participant Service as ðŸ“‹ LayananAkademik
    participant DB as ðŸ’¾ Database
    
    Operator ->> UI: 1. Buka Input KRS
    UI ->> Service: 2. Ambil daftar mahasiswa
    Service ->> DB: 3. Query mahasiswa
    DB -->> Service: â—€ï¸ Data mahasiswa
    Service -->> UI: â—€ï¸ List mahasiswa
    UI -->> Operator: â—€ï¸ Tampilkan form
    
    rect rgb(230, 245, 250)
        Note over Operator,UI: Operator: Pilih Mahasiswa & Paket KRS
        Operator ->> UI: 4. Pilih mahasiswa + semester
        UI ->> Service: 5. Ambil mata kuliah semester {N}
        Service ->> DB: 6. Query mk by semester
        DB -->> Service: â—€ï¸ 8 mata kuliah
        Service -->> UI: â—€ï¸ Daftar mata kuliah
        UI -->> Operator: â—€ï¸ Preview KRS
    end
    
    rect rgb(245, 235, 220)
        Note over Operator,DB: Operator: Validasi & Simpan KRS
        Operator ->> UI: 7. Klik "Simpan KRS"
        UI ->> Service: 8. Validate data (SKS, kapasitas, dll)
        Service ->> DB: 9. Check existing KRS
        DB -->> Service: â—€ï¸ Check result
        alt Validasi Gagal
            Service -->> UI: âŒ Error message
            UI -->> Operator: â—€ï¸ Tampilkan error
        else Validasi Sukses
            Service ->> DB: 10. Insert KRS records
            DB -->> Service: âœ… Success
            Service ->> DB: 11. Calculate IP otomatis
            DB -->> Service: âœ… IP updated
            Service -->> UI: âœ… Simpan berhasil
            UI -->> Operator: â—€ï¸ Refresh list
        end
    end
    
    rect rgb(245, 230, 245)
        Note over Admin,DB: Admin: Review & Approval
        Admin ->> UI: 12. View pending KRS
        UI ->> Service: 13. Get KRS awaiting approval
        Service ->> DB: 14. Query KRS status
        DB -->> Service: â—€ï¸ List KRS pending
        Service -->> UI: â—€ï¸ Display KRS
        UI -->> Admin: â—€ï¸ Tampilkan KRS untuk approval
        Admin ->> UI: 15. Approve/Reject KRS
        UI ->> Service: 16. Update status KRS
        Service ->> DB: 17. Update ACC field
        DB -->> Service: âœ… Updated
        Service -->> UI: âœ… Status updated
        UI -->> Admin: â—€ï¸ Confirmation
    end
    
    Operator ->> UI: 18. Kembali ke menu
    UI -->> Operator: â—€ï¸ Back to Operator Dashboard
```

**Penjelasan Alur:**

| # | Actor | Deskripsi |
|---|-------|-----------|
| **1-11** | ðŸ‘¤ **Operator** | Input KRS: pilih mahasiswa, ambil MK per semester, validasi, simpan ke DB |
| **12-17** | ðŸ‘¨â€ðŸ’¼ **Admin** | Review & Approval: lihat KRS pending, review, approve/reject, update status ACC |
| **18** | ðŸ‘¤ **Operator** | Kembali ke dashboard setelah selesai |

**Warna Box:**
- ðŸ”µ **Biru Muda** = Aksi Operator (Input data)
- ðŸŸ¤ **Coklat Muda** = Operator (Validasi & Simpan)
- ðŸŸ£ **Ungu Muda** = Aksi Admin (Review & Approval)

---

## ðŸ—ï¸ Arsitektur Sistem

### Struktur Folder

```
UTS_PBO2/
â”œâ”€â”€ src/main/java/id/ac/utb/pbo2/
â”‚   â”œâ”€â”€ Aplikasi.java                    # Entry point aplikasi
â”‚   â”œâ”€â”€ DatabaseCheck.java               # Validasi database
â”‚   â”œâ”€â”€ config/
â”‚   â”‚   â””â”€â”€ AppConfig.java               # Konfigurasi aplikasi
â”‚   â”œâ”€â”€ db/
â”‚   â”‚   â”œâ”€â”€ BasisData.java               # Koneksi database
â”‚   â”‚   â””â”€â”€ DatabaseBootstrap.java       # Inisialisasi database
â”‚   â”œâ”€â”€ model/
â”‚   â”‚   â””â”€â”€ PenggunaSaatIni.java         # Model user session
â”‚   â”œâ”€â”€ service/
â”‚   â”‚   â”œâ”€â”€ LayananAkademik.java         # Business logic akademik
â”‚   â”‚   â””â”€â”€ LayananOtentikasi.java       # Autentikasi & validasi
â”‚   â”œâ”€â”€ ui/
â”‚   â”‚   â”œâ”€â”€ LoginFrame.java              # Form login
â”‚   â”‚   â”œâ”€â”€ MainFrame.java               # Window utama
â”‚   â”‚   â”œâ”€â”€ DashboardPanel.java          # Dashboard awal
â”‚   â”‚   â”œâ”€â”€ MahasiswaPanel.java          # Master mahasiswa
â”‚   â”‚   â”œâ”€â”€ DosenPanel.java              # Master dosen
â”‚   â”‚   â”œâ”€â”€ MataKuliahPanel.java         # Master mata kuliah
â”‚   â”‚   â”œâ”€â”€ UserPanel.java               # Setting user
â”‚   â”‚   â”œâ”€â”€ KrsPanel.java                # Transaksi KRS
â”‚   â”‚   â”œâ”€â”€ NilaiPanel.java              # Transaksi nilai
â”‚   â”‚   â”œâ”€â”€ PasswordPanel.java           # Ubah password
â”‚   â”‚   â”œâ”€â”€ DatePickerField.java         # Custom date picker
â”‚   â”‚   â”œâ”€â”€ YearPickerField.java         # Custom year picker
â”‚   â”‚   â”œâ”€â”€ StudentListPanel.java        # List view mahasiswa
â”‚   â”‚   â””â”€â”€ Theme.java                   # UI theme & styling
â”‚   â””â”€â”€ util/
â”‚       â””â”€â”€ PasswordUtil.java            # Utility enkrip password
â”œâ”€â”€ database/
â”‚   â”œâ”€â”€ uts_pbo2.sql                     # Schema & seed data
â”‚   â””â”€â”€ wilayah_indonesia.sql            # Data wilayah (optional)
â”œâ”€â”€ scripts/
â”‚   â”œâ”€â”€ build.bat                        # Build script
â”‚   â””â”€â”€ run.bat                          # Run script
â”œâ”€â”€ lib/
â”‚   â””â”€â”€ mysql-connector-j-8.4.0.jar      # JDBC driver
â”œâ”€â”€ pom.xml                              # Maven configuration
â””â”€â”€ README.md                            # Dokumentasi ini
```

### Technology Stack

```
â”Œâ”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”
â”‚   Java Swing UI (AWT/Swing)        â”‚
â”‚   (LoginFrame, MainFrame, Panels)  â”‚
â””â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”¬â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”˜
               â”‚
â”Œâ”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â–¼â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”
â”‚   Business Logic Layer              â”‚
â”‚   (LayananAkademik, Validasi, etc) â”‚
â””â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”¬â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”˜
               â”‚
â”Œâ”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â–¼â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”
â”‚   Database Abstraction              â”‚
â”‚   (BasisData, JDBC Connection)     â”‚
â””â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”¬â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”˜
               â”‚
â”Œâ”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â–¼â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”
â”‚   MySQL/MariaDB Database            â”‚
â”‚   (uts_pbo2 Schema)                â”‚
â””â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”˜
```

---

## âœ… Validasi & Business Rules

### Validasi Input
- ðŸ“ **NIM**: Wajib 5 digit angka
- ðŸ‘¥ **Prodi**: Hanya `Teknik Informatika`, `Teknik Industri`, `DKV`, `RETAIL`
- ðŸ·ï¸ **Kode Kelas**: Otomatis mengikuti prodi (TIF, TI, DKV, RETAIL)
- ðŸ“… **Format Tahun**: Popup kalender untuk pilih angkatan
- ðŸ”’ **Status Mahasiswa**: Otomatis AKTIF saat tambah data
- ðŸ“ **Password**: Terenkripsi dengan secure hashing
- âœ… **Error Messages**:
  - "Maaf minimal input angka adalah 5." (NIM < 5 digit)
  - "Maaf data tersebut tidak ada." (NIM valid tapi tidak ditemukan)

### Business Logic
- ðŸ” Pencarian mahasiswa **hanya** berdasarkan NIM
- ðŸ”„ Tombol `Kembali` di Master Mahasiswa untuk reset tampilan semua data
- ðŸ“ Format kelas detail: `TIF 25A CID`, `DKV 24C`, `TI 21F`
- ðŸ“Š Konversi nilai: A(4.0), B(3.0), C(2.0), D(1.0), E(0.0)
- ðŸŽ“ IP = rata-rata nilai semester berlaku
- ðŸ“ˆ IPK = rata-rata nilai kumulatif dari semua semester
- ðŸ”„ **KRS Mengulang**: Otomatis ditandai jika mahasiswa pernah ambil mata kuliah yang sama

### Advanced Features
- ðŸ’° **UKT Integration**: KRS otomatis disetujui jika UKT semester lunas
- ðŸ‘¨â€ðŸ« **Approval Workflow**: ACC dosen wali + dosen prodi
- ðŸ“‹ **Semester Filter**: Master Mata Kuliah auto-filter saat semester dipilih
- ðŸ“¦ **Bulk Enroll**: Tambah paket KRS otomatis memasukkan 8 mata kuliah sesuai semester
- ðŸ“Š **KRS Table Divided**: Tiga section - Mahasiswa/KRS, Mata Kuliah/Dosen, Mengulang/UKT/ACC

### Data Integrity
- âœ… Primary Key: Mencegah duplikasi
- âœ… Unique Key: Validasi unikitas kolom penting
- âœ… Validasi Aplikasi: Double-check di layer business logic
- âœ… View IPK: Snapshot dari KRS + Nilai tanpa duplikasi

---

## ðŸ“Š Data Seed & Statistik

**Bawaan Database (siap pakai):**
- ðŸ‘¥ **24 Mahasiswa** - Bervariasi dengan UKT lunas/belum lunas
- ðŸ‘¨â€ðŸ« **16 Dosen** - Sebagai pengampu dan wali kelas
- ðŸ“š **64 Mata Kuliah** - 8 per semester (semester 1-8)
- 4ï¸âƒ£ **4 Program Studi** - TIF, TI, DKV, RETAIL
- ðŸ“‹ **199 Data KRS** - Termasuk data mengulang
- ðŸŽ“ **199 Nilai** - Sesuai dengan KRS
- ðŸ’³ **48 Pembayaran UKT** - Bervariasi lunas/belum

---

## ðŸ”§ Troubleshooting

| Masalah | Solusi |
|---------|--------|
| **"JDK tidak ditemukan"** | Pastikan JAVA_HOME diset atau javac di PATH |
| **"Connection refused"** | Pastikan MySQL/MariaDB running di XAMPP |
| **"Database not found"** | Jalankan aplikasi sekali - database auto-created |
| **"Port 3306 sudah in-use"** | Ubah DB_PORT di environment variable |
| **"GUI tidak merespons"** | Increase heap size: `set JAVA_OPTS=-Xmx1024m` |

### Verifikasi Database

Jalankan command berikut untuk memverifikasi seed data:

```batch
scripts\build.bat
```

Kemudian:

```batch
java -cp "target\classes;lib\mysql-connector-j-8.4.0.jar" id.ac.utb.pbo2.DatabaseCheck
```

**Expected Output:**
```
mahasiswa=24
dosen=16
prodi=4
matakuliah=64
pembayaran_ukt=48
krs=199
nilai=199
matakuliah_semester_1=8
matakuliah_semester_2=8
...
matakuliah_semester_8=8
```

---

## ðŸ“ž Support & Kontribusi

Untuk laporan bug atau saran fitur, silakan buat issue atau hubungi tim development.

---

## ðŸ“„ Lisensi

Project ini dikembangkan sebagai tugas **UTS PBO2** dengan referensi pada best practices enterprise Java applications.

---

**Last Updated:** April 2026
**Version:** 1.0.0


