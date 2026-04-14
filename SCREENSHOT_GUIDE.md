# 📸 Panduan Screenshot untuk README

Dokumentasi README.md telah diperbarui menjadi lebih profesional dan memerlukan 5 screenshot untuk melengkapi dokumentasi. Folder `screenshots/` telah dibuat untuk menyimpan gambar-gambar tersebut.

## 📋 Daftar Screenshot yang Diperlukan

Berikut adalah screenshot yang harus Anda ambil dan upload ke folder `screenshots/`:

### 1. **01-login-screen.png**
   - **Deskripsi**: Layar login aplikasi
   - **Konten**: Form login dengan input username, password, dan tombol login
   - **Ukuran Rekomendasi**: 800x600 pixel atau lebih
   - **Tips**: 
     - Tampilkan interface login yang jelas
     - Bisa kosong atau isi dengan contoh username/password (jangan yang sebenarnya)
     - Screenshot dari aplikasi yang sedang berjalan

### 2. **02-admin-dashboard.png**
   - **Deskripsi**: Dashboard admin setelah login
   - **Konten**: Main window dengan menu sidebar modul master data
   - **Ukuran Rekomendasi**: 1024x768 pixel atau lebih
   - **Tips**: 
     - Login menggunakan akun admin (admin/admin123)
     - Tampilkan tampilan menu lengkap admin
     - Terlihat sidebar dengan opsi Master Mahasiswa, Master Dosen, Master Mata Kuliah, Setting User, dll

### 3. **03-mahasiswa-master.png**
   - **Deskripsi**: Modul Master Mahasiswa
   - **Konten**: Interface untuk manajemen data mahasiswa
   - **Ukuran Rekomendasi**: 1024x768 pixel atau lebih
   - **Tips**: 
     - Buka Master Mahasiswa dari menu
     - Tampilkan table/list mahasiswa dengan data yang terisi
     - Tunjukkan kolom NIM, Nama, Prodi, Kelas, dll
     - Sertakan tombol CRUD (Tambah, Ubah, Hapus, Lihat Data Lengkap)

### 4. **04-krs-transaksi.png**
   - **Deskripsi**: Modul Transaksi KRS
   - **Konten**: Interface input dan kelola KRS
   - **Ukuran Rekomendasi**: 1024x768 pixel atau lebih
   - **Tips**: 
     - Login sebagai operator (operator/operator123)
     - Buka menu Transaksi KRS
     - Tampilkan form input atau list KRS dengan data
     - Sertakan kolom Mahasiswa, Mata Kuliah, Semester, Status ACC, dll

### 5. **05-nilai-transaksi.png**
   - **Deskripsi**: Modul Transaksi Nilai
   - **Konten**: Interface input dan kelola nilai mahasiswa
   - **Ukuran Rekomendasi**: 1024x768 pixel atau lebih
   - **Tips**: 
     - Login sebagai operator (operator/operator123)
     - Buka menu Transaksi Nilai
     - Tampilkan form input atau list nilai dengan data
     - Tunjukkan kolom Mahasiswa, Mata Kuliah, Nilai Huruf, Bobot, IP, IPK, dll

---

## 🛠️ Cara Mengambil Screenshot

### Di Windows:
1. **Menggunakan Print Screen + Paint:**
   - Tekan `PrtScn` (Print Screen)
   - Buka Paint (Ctrl + V untuk paste)
   - Crop sesuai kebutuhan
   - Save sebagai PNG di folder `screenshots/`

2. **Menggunakan Snipping Tool (Windows 10/11):**
   - Buka Snipping Tool (cari di Start Menu)
   - Pilih "New"
   - Pilih area yang ingin di-screenshot
   - Save sebagai PNG

3. **Menggunakan Tool Pihak Ketiga:**
   - **ScreenShot** (Windows built-in)
   - **ShareX** (gratis, fitur lengkap)
   - **Greenshot** (gratis, simple)

---

## 📁 Struktur Folder Screenshot

Setelah semua screenshot diambil, struktur folder akan terlihat seperti:

```
UTS_PBO2/
└── screenshots/
    ├── 01-login-screen.png
    ├── 02-admin-dashboard.png
    ├── 03-mahasiswa-master.png
    ├── 04-krs-transaksi.png
    └── 05-nilai-transaksi.png
```

---

## 💡 Tips Profesional

1. **Resolusi Konsisten**: Usahakan semua screenshot memiliki ukuran dan resolusi yang konsisten (~1024x768 atau lebih besar)

2. **Crop & Format**: 
   - Crop area yang tidak perlu (background windows lain, dll)
   - Gunakan format PNG untuk kualitas terbaik
   - Hindari JPG karena kompres kualitas

3. **Konten Jelas**:
   - Isi form dengan data dummy yang jelas dan mudah dibaca
   - Pastikan UI terlihat jelas tanpa blur atau gangguan
   - Zoom in jika teks terlalu kecil

4. **Naming Convention**:
   - Gunakan nama file sesuai yang sudah ditentukan
   - Urutan nomor (01, 02, 03, dst) memudahkan navigasi
   - Gunakan format PNG (Portable Network Graphics)

5. **Quality Check**:
   - Verifikasi setiap screenshot terupload di folder `screenshots/`
   - Cek apakah file terbuka dengan baik di README.md
   - Pastikan tidak ada error "Image Not Found" di README

---

## ✅ Checklist Sebelum Submit

- [ ] Folder `screenshots/` sudah dibuat
- [ ] Kelima file screenshot sudah diambil dan tersimpan
- [ ] Nama file sesuai dengan yang ditentukan (01-login-screen.png, dst)
- [ ] Format file PNG (bukan JPG atau format lain)
- [ ] Resolusi minimal 800x600 pixel untuk login, 1024x768 untuk yang lain
- [ ] Gambar jelas dan tidak blur
- [ ] README.md sudah bisa menampilkan semua gambar
- [ ] Tidak ada error "Image not found" saat preview README

---

## 📝 Catatan Tambahan

- README.md sekarang menggunakan Markdown image syntax: `![Description](./screenshots/filename.png)`
- Path menggunakan `./screenshots/` (relative path) yang lebih fleksibel
- Jika ingin mengupdate screenshot nanti, cukup ganti file di folder `screenshots/`
- Dokumentasi sudah dilengkapi dengan struktur profesional dan mudah diikuti

**Selamat, dokumentasi Anda sekarang siap untuk dilengkapi dengan visual yang menarik! 🎉**
