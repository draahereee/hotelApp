# Sistem Manajemen Pemesanan Hotel (Console App)

Aplikasi berbasis Console (CLI) untuk manajemen hotel yang dibangun menggunakan bahasa pemrograman Java dengan arsitektur berlapis (Model-Service-Repository).

---

## Prasyarat (Prerequisites)

Sebelum menjalankan aplikasi ini, pastikan komputer Anda sudah terinstal:
* **Java Development Kit (JDK)** versi 11 atau yang lebih baru.
* Pastikan perintah `javac` dan `hello` (Java Runtime) sudah terdaftar di Environment Variables (PATH) komputer Anda.

---

## Cara Menjalankan Aplikasi

Anda tidak perlu melakukan kompilasi manual lewat terminal. Cukup ikuti langkah mudah di bawah ini:

### Untuk Pengguna Windows
1. Unduh atau clone repositori ini ke komputer Anda.
2. Buka folder utama proyek (folder tempat anda download repo ini)
3. Klik dua kali (Double-click) pada file **`run.bat`**.
4. Aplikasi akan otomatis mengompilasi kode sumber ke folder `bin` dan membuka UI Console.

### Untuk Pengguna Linux / macOS
Jika Anda menggunakan perangkat non-Windows, buka terminal di folder proyek lalu jalankan perintah berikut:
```bash
# 1. Buat folder output jika belum ada
mkdir -p bin

# 2. Kompilasi kode sumber secara rekursif
find src -name "*.java" | xargs javac -d bin

# 3. Jalankan aplikasi
java -cp bin hotel.Main
```

---

## Struktur Proyek

* `src/hotel/` - Berisi seluruh kode sumber utama (`.java`).
  * `model/` - Kelas entitas data (User, Hotel, Booking, dll).
  * `service/` - Logika bisnis dan layanan sistem.
  * `database/` - Pengaturan koneksi dan helper basis data.
  * `ui/` - Komponen pemformat tampilan teks console.
* `lib/` - Tempat penyimpanan library eksternal (misal: JDBC Driver).
* `bin/` - Tempat otomatis untuk file hasil kompilasi (`.class`) agar folder `src` tetap bersih.