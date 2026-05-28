package hotel;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    static List<Hotel> hotels = new ArrayList<>();
    static List<Promo> promos = new ArrayList<>();
    static List<Booking> bookings = new ArrayList<>();
    static User loggedUser = null;
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        hotels = DatabaseHelper.loadAllHotels();
        promos = DatabaseHelper.loadAllPromos();

        // Autentikasi
        while (loggedUser == null) {
            System.out.println("\n=== APLIKASI BOOKING HOTEL ===");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Keluar");
            System.out.print("Pilih: ");
            int pilih = inputInt();
            sc.nextLine();

            if (pilih == 1) {
                register();
            } else if (pilih == 2) {
                login();
            } else if (pilih == 3) {
                System.out.println("Terima kasih! Sampai jumpa.");
                return;
            } else {
                System.out.println("Pilihan tidak valid.");
            }
        }

        int menu;
        do {
            // Muat ulang booking user dari DB
            bookings = DatabaseHelper.loadBookingsForUser(loggedUser.getIdAkun());

            // Refresh status booking dan sinkronkan ke DB
            for (Booking b : bookings) {
                b.refreshStatus();
                String newStatus = b.getStatus().name()
                        .replace("CONFIRMED", "dikonfirmasi")
                        .replace("CHECKED_IN", "check_in")     // ← perbaikan underscore
                        .replace("CHECKED_OUT", "check_out")   // ← perbaikan underscore
                        .replace("REFUNDED", "dibatalkan");
                DatabaseHelper.updateReservationStatus(b.getIdReservasi(), newStatus);
            }

            // Muat layanan tambahan & refresh status layanan
            for (Booking b : bookings) {
                List<ServiceOrder> serv = DatabaseHelper.loadServiceOrdersForReservation(b.getIdReservasi());
                for (ServiceOrder so : serv) {
                    so.refreshStatus();
                }
                b.getServices().clear();
                b.getServices().addAll(serv);
            }

            System.out.println("\n=================================");
            System.out.println("       DASHBOARD UTAMA           ");
            System.out.println("=================================");
            System.out.println("1. Cari Hotel (Jelajah)");
            System.out.println("2. Booking Hotel (Pesan Langsung)");
            System.out.println("3. Promo Tersedia");
            System.out.println("4. Layanan Tambahan (Selama Menginap)");
            System.out.println("5. Pesanan Saya (Riwayat & Refund)");
            System.out.println("6. Profil");
            System.out.println("7. Logout");
            System.out.print("Pilih: ");
            menu = inputInt();
            sc.nextLine();

            switch (menu) {
                case 1: cariHotel();       break;
                case 2: bookingHotel();    break;
                case 3: tampilkanPromo();  break;
                case 4: layananTambahan(); break;
                case 5: pesananSaya();     break;
                case 6: editProfile();     break;
                case 7: logout();          break;
                default: System.out.println("Pilihan tidak tersedia.");
            }
        } while (menu != 7);
    }

    static void register() {
        System.out.print("Username: ");     String u = sc.nextLine();
        System.out.print("Email: ");        String e = sc.nextLine();
        System.out.print("Password: ");     String p = sc.nextLine();
        System.out.print("No. Handphone: ");String n = sc.nextLine();
        System.out.print("Nama Lengkap: "); String nama = sc.nextLine();
        System.out.print("Alamat: ");       String alamat = sc.nextLine();
        System.out.print("Jenis Kelamin: "); String jk = sc.nextLine();

        // Urutan: idAkun, username, email, password, phone, alamat, nama, jenisKelamin
        User newUser = new User(0, u, e, p, n, alamat, nama, jk); // perbaikan urutan
        if (DatabaseHelper.registerUser(newUser)) {
            System.out.println("Registrasi berhasil! Silakan login.");
        } else {
            System.out.println("Registrasi gagal.");
        }
    }

    static void login() {
        System.out.print("Username: "); String u = sc.nextLine();
        System.out.print("Password: "); String p = sc.nextLine();
        loggedUser = DatabaseHelper.loginUser(u, p);
        if (loggedUser != null) {
            System.out.println("Login berhasil! Selamat datang, " + loggedUser.getUsername() + "!");
        } else {
            System.out.println("Login gagal. Periksa username dan password.");
        }
    }

    static void logout() {
        loggedUser = null;
        System.out.println("Anda telah logout.");
    }

    static void cariHotel() {
        Set<String> cities = hotels.stream()
                .map(Hotel::getLocation)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        while (true) {
            System.out.println("\n--- CARI HOTEL (JELAJAH) ---");
            System.out.println("Pilih Kota Tujuan:");
            int i = 1;
            for (String city : cities) {
                System.out.println(i++ + ". " + city);
            }
            System.out.println("0. Kembali ke Menu Utama");
            System.out.print("Pilih: ");
            int pilihKota = inputInt();
            sc.nextLine();
            if (pilihKota == 0) return;
            if (pilihKota < 1 || pilihKota > cities.size()) {
                System.out.println("Pilihan tidak valid.");
                continue;
            }
            String kotaTerpilih = new ArrayList<>(cities).get(pilihKota - 1);

            List<Hotel> hotelDiKota = hotels.stream()
                    .filter(h -> h.getLocation().equalsIgnoreCase(kotaTerpilih))
                    .collect(Collectors.toList());

            if (hotelDiKota.isEmpty()) {
                System.out.println("Tidak ada hotel di " + kotaTerpilih);
                continue;
            }

            while (true) {
                System.out.println("\nHotel di " + kotaTerpilih + ":");
                for (int j = 0; j < hotelDiKota.size(); j++) {
                    Hotel h = hotelDiKota.get(j);
                    System.out.println((j + 1) + ". " + h.getName() + " [" + h.getStarRating() + "]");
                }
                System.out.println("0. Ganti Kota");
                System.out.print("Pilih hotel untuk lihat detail: ");
                int pilihHotel = inputInt();
                sc.nextLine();
                if (pilihHotel == 0) break;
                if (pilihHotel < 1 || pilihHotel > hotelDiKota.size()) {
                    System.out.println("Pilihan tidak valid.");
                    continue;
                }
                Hotel hotel = hotelDiKota.get(pilihHotel - 1);

                while (true) {
                    System.out.println("\nTipe Kamar di " + hotel.getName() + ":");
                    List<Room> rooms = hotel.getRooms();
                    for (int k = 0; k < rooms.size(); k++) {
                        Room r = rooms.get(k);
                        System.out.println((k + 1) + ". " + r.getType() + " - Rp " + r.getPricePerNight() +
                                "/malam | Stok: " + r.getStock() + " | " + r.getFacilities());
                    }
                    System.out.println("0. Kembali ke Daftar Hotel");
                    System.out.print("(Hanya lihat) Pilih 0: ");
                    int pilih = inputInt();
                    sc.nextLine();
                    if (pilih == 0) break;
                    System.out.println("Silakan pilih 0 untuk kembali.");
                }
            }
        }
    }

    static void bookingHotel() {
        System.out.println("\n--- BOOKING HOTEL ---");
        System.out.print("Masukkan kota tujuan: ");
        String kota = sc.nextLine();
        List<Hotel> hotelDiKota = hotels.stream()
                .filter(h -> h.getLocation().equalsIgnoreCase(kota))
                .collect(Collectors.toList());

        if (hotelDiKota.isEmpty()) {
            System.out.println("Tidak ada hotel di " + kota + ".");
            return;
        }

        while (true) {
            System.out.println("\nPilih Hotel:");
            for (int i = 0; i < hotelDiKota.size(); i++) {
                Hotel h = hotelDiKota.get(i);
                System.out.println((i + 1) + ". " + h.getName() + " [" + h.getStarRating() + "]");
            }
            System.out.println("0. Batal & Kembali ke Menu Utama");
            System.out.print("Pilih: ");
            int pilihHotel = inputInt();
            sc.nextLine();
            if (pilihHotel == 0) return;
            if (pilihHotel < 1 || pilihHotel > hotelDiKota.size()) {
                System.out.println("Pilihan tidak valid.");
                continue;
            }
            Hotel hotel = hotelDiKota.get(pilihHotel - 1);

            while (true) {
                System.out.println("\nTipe Kamar Tersedia:");
                List<Room> rooms = hotel.getRooms();
                for (int j = 0; j < rooms.size(); j++) {
                    Room r = rooms.get(j);
                    System.out.println((j + 1) + ". " + r.getType() + " - Rp " + r.getPricePerNight() +
                            "/malam | Stok: " + r.getStock() + " | " + r.getFacilities());
                }
                System.out.println("0. Pilih Hotel Lain");
                System.out.print("Pilih kamar untuk dipesan: ");
                int pilihKamar = inputInt();
                sc.nextLine();
                if (pilihKamar == 0) break;
                if (pilihKamar < 1 || pilihKamar > rooms.size()) {
                    System.out.println("Pilihan tidak valid.");
                    continue;
                }
                Room kamar = rooms.get(pilihKamar - 1);
                if (kamar.getStock() <= 0) {
                    System.out.println("Stok kamar ini habis.");
                    continue;
                }

                // Input tanggal
                System.out.print("Tanggal check‑in (yyyy-mm-dd): ");
                LocalDate checkIn = inputDate();  // panggil method tanpa parameter
                if (checkIn == null) continue;
                if (checkIn.isBefore(LocalDate.now())) {
                    System.out.println("Tanggal tidak boleh di masa lalu.");
                    continue;
                }

                System.out.print("Lama menginap (malam): ");
                int malam = inputInt();
                sc.nextLine();
                if (malam < 1) {
                    System.out.println("Minimal 1 malam.");
                    continue;
                }

                // Promo
                double diskon = 0;
                System.out.print("Punya kode promo? (kosongkan jika tidak): ");
                String kode = sc.nextLine();
                Promo promoAktif = null;
                if (!kode.isEmpty()) {
                    for (Promo p : promos) {
                        if (p.getCode().equalsIgnoreCase(kode) && p.isValidForDate(checkIn)) {
                            promoAktif = p;
                            diskon = p.getDiscountPercent();
                            break;
                        }
                    }
                    if (promoAktif == null) {
                        System.out.println("Kode tidak valid atau tidak berlaku.");
                    } else {
                        System.out.println("Promo diterapkan: " + promoAktif.getCode());
                    }
                }

                int hargaDasar = kamar.getPricePerNight() * malam;
                int potongan = (int)(hargaDasar * diskon);
                int total = hargaDasar - potongan;

                System.out.println("\n--- REVIEW PESANAN ---");
                System.out.println("Hotel: " + hotel.getName());
                System.out.println("Kamar: " + kamar.getType());
                System.out.println("Check‑in: " + checkIn + " – Check‑out: " + checkIn.plusDays(malam));
                if (potongan > 0) System.out.println("Diskon: -Rp " + potongan);
                System.out.println("Total: Rp " + total);

                System.out.print("Metode Pembayaran (transfer_bank / e_wallet): ");
                String metode = sc.nextLine().toLowerCase().trim();
                if (!metode.equals("transfer_bank") && !metode.equals("e_wallet")) {
                    System.out.println("Metode pembayaran hanya 'transfer_bank' atau 'e_wallet'.");
                    continue;
                }

                // --- PERBAIKAN LOGIKA KONFIRMASI PESANAN ---
                System.out.print("Konfirmasi pesanan? (Y/N): ");
                String konfirmasi = sc.nextLine().trim(); // Langsung ambil input bersih

                if (!konfirmasi.equalsIgnoreCase("Y")) {
                    System.out.println("Pesanan dibatalkan.");
                    return;
                }

                    
                Booking newBooking = new Booking(0, loggedUser, hotel, kamar, checkIn,
                        checkIn.plusDays(malam), total, metode, Booking.Status.CONFIRMED, promoAktif);
                int reservasiId = DatabaseHelper.createReservation(newBooking);
                if (reservasiId > 0) {
                    System.out.println("Booking berhasil! ID reservasi: " + reservasiId);
                } else {
                    System.out.println("Gagal booking.");
                }
                return;
            }
        }
    }

    static void tampilkanPromo() {
        System.out.println("\n--- DAFTAR PROMO ---");
        for (Promo p : promos) {
            System.out.println(p);
            System.out.println("   (Berlaku jika check‑in sesuai syarat)");
        }
    }

    static void layananTambahan() {
        List<Booking> inap = bookings.stream()
                .filter(b -> b.getStatus() == Booking.Status.CHECKED_IN
                        && b.getUser().getIdAkun() == loggedUser.getIdAkun())
                .collect(Collectors.toList());

        if (inap.isEmpty()) {
            System.out.println("Anda tidak sedang menginap saat ini.");
            return;
        }

        System.out.println("\n--- LAYANAN TAMBAHAN SELAMA MENGINAP ---");
        System.out.println("Pilih pesanan yang sedang berjalan:");
        for (int i = 0; i < inap.size(); i++) {
            System.out.println((i + 1) + ". " + inap.get(i).info());
        }
        System.out.print("Pilih (0 = batal): ");
        int idx = inputInt();
        sc.nextLine();
        if (idx == 0 || idx < 1 || idx > inap.size()) return;
        Booking b = inap.get(idx - 1);

        System.out.println("\nKategori Layanan:");
        System.out.println("1. Spa");
        System.out.println("2. Makanan");
        System.out.println("3. Minuman");
        System.out.print("Pilih kategori (0 = batal): ");
        int kat = inputInt();
        sc.nextLine();
        if (kat == 0 || kat < 1 || kat > 3) return;

        List<ServiceItem> menu = null;
        String jenis = "";
        if (kat == 1) {
            menu = DatabaseHelper.loadSpaMenu(b.getHotel().getIdHotel());
            jenis = "Spa";
        } else if (kat == 2) {
            menu = DatabaseHelper.loadMakananMenu();
            jenis = "Makanan";
        } else if (kat == 3) {
            menu = DatabaseHelper.loadMinumanMenu();
            jenis = "Minuman";
        }

        if (menu == null || menu.isEmpty()) {
            System.out.println("Maaf, tidak ada " + jenis + " tersedia.");
            return;
        }

        System.out.println("\nDaftar " + jenis + ":");
        for (int i = 0; i < menu.size(); i++) {
            ServiceItem item = menu.get(i);
            System.out.println((i + 1) + ". " + item.getName() + " - Rp " + item.getPrice());
        }
        System.out.print("Pilih nomor (0 = batal): ");
        int pilihMenu = inputInt();
        sc.nextLine();
        if (pilihMenu == 0 || pilihMenu < 1 || pilihMenu > menu.size()) return;
        ServiceItem terpilih = menu.get(pilihMenu - 1);

        System.out.print("Jumlah: ");
        int jumlah = inputInt();
        sc.nextLine();
        if (jumlah < 1) {
            System.out.println("Jumlah minimal 1.");
            return;
        }

        int total = terpilih.getPrice() * jumlah;
        System.out.println("Total harga: Rp " + total);
        System.out.print("Konfirmasi pesan? (Y/N): ");
        if (!sc.nextLine().equalsIgnoreCase("Y")) {
            System.out.println("Pesanan dibatalkan.");
            return;
        }

        boolean sukses = false;
        if (kat == 1) {
            sukses = DatabaseHelper.orderSpa(b.getIdReservasi(), terpilih.getIdLayanan(), jumlah, terpilih.getPrice());
        } else if (kat == 2) {
            sukses = DatabaseHelper.orderMakanan(b.getIdReservasi(), terpilih.getIdLayanan(), jumlah, terpilih.getPrice());
        } else if (kat == 3) {
            sukses = DatabaseHelper.orderMinuman(b.getIdReservasi(), terpilih.getIdLayanan(), jumlah, terpilih.getPrice());
        }

        if (sukses) {
            ServiceOrder so = new ServiceOrder(0, b.getIdReservasi(),
                    kat == 1 ? ServiceOrder.ServiceType.SPA :
                    kat == 2 ? ServiceOrder.ServiceType.MAKANAN : ServiceOrder.ServiceType.MINUMAN,
                    terpilih.getName(), terpilih.getPrice(), jumlah, total,
                    ServiceOrder.OrderStatus.DIPROSES, LocalDateTime.now());
            b.addService(so);
            System.out.println("Pesanan " + jenis + " berhasil! Status: Diproses.");
        } else {
            System.out.println("Gagal memesan " + jenis + ".");
        }
    }

    static void pesananSaya() {
        System.out.println("\n--- PESANAN SAYA ---");
        for (int i = 0; i < bookings.size(); i++) {
            Booking b = bookings.get(i);
            System.out.println((i + 1) + ". " + b.info());
            if (!b.getServices().isEmpty()) {
                System.out.println("   Layanan:");
                for (ServiceOrder so : b.getServices()) {
                    System.out.println("     " + so);
                }
            }
        }
        System.out.print("Refund? (Y/N): ");
        if (!sc.nextLine().equalsIgnoreCase("Y")) return;
        System.out.print("Nomor urut: ");
        int idx = inputInt();
        sc.nextLine();
        if (idx < 1 || idx > bookings.size()) return;
        Booking b = bookings.get(idx - 1);
        if (b.isRefundable()) {
            b.getRoom().increaseStock();
            DatabaseHelper.updateStock(b.getRoom().getIdKamar(), b.getRoom().getStock());
            DatabaseHelper.updateReservationStatus(b.getIdReservasi(), "dibatalkan");
            b.setStatus(Booking.Status.REFUNDED);
            System.out.println("Refund berhasil.");
        } else {
            System.out.println("Tidak bisa refund.");
        }
    }

    static void editProfile() {
        System.out.println("\n--- EDIT PROFIL ---");
        System.out.print("Nama [" + loggedUser.getNamaString() + "]: ");
        String nama = sc.nextLine();
        if (!nama.isEmpty()) loggedUser.setNamaString(nama);

        System.out.print("No HP [" + loggedUser.getPhone() + "]: ");
        String hp = sc.nextLine();
        if (!hp.isEmpty()) loggedUser.setPhone(hp);

        System.out.print("Alamat [" + loggedUser.getAlamatString() + "]: ");
        String alamat = sc.nextLine();
        if (!alamat.isEmpty()) loggedUser.setAlamatString(alamat);

        System.out.print("Jenis Kelamin [" + loggedUser.getJenisKelamin() + "]: ");
        String jk = sc.nextLine();
        if (!jk.isEmpty()) loggedUser.setJenisKelamin(jk);

        if (DatabaseHelper.updateProfile(loggedUser))
            System.out.println("Profil diperbarui.");
        else
            System.out.println("Gagal memperbarui profil.");
    }

    // ========== HELPER ==========
    static int inputInt() {
        while (!sc.hasNextInt()) {
            System.out.print("Angka valid: ");
            sc.next();
        }
        return sc.nextInt();
    }

    static LocalDate inputDate() {
        String d = sc.nextLine();
        try {
            return LocalDate.parse(d);
        } catch (DateTimeParseException e) {
            System.out.println("Format salah (yyyy-mm-dd).");
            return null;
        }
    }
}