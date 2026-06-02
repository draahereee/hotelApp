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


    enum AppState {
        MAIN, CARI_HOTEL, BOOKING_HOTEL, PROMO, LAYANAN_TAMBAHAN, PESANAN_SAYA, PROFIL, LOGOUT
    }

    static Stack<AppState> navigasi = new Stack<>();
    static AppState currentState = AppState.MAIN;

    public static void main(String[] args) {
        hotels = DatabaseHelper.loadAllHotels();
        promos = DatabaseHelper.loadAllPromos();

        while (true) {
            
            while (loggedUser == null) {
                clearScreen();
                System.out.println("\n=== APLIKASI BOOKING HOTEL ===");
                System.out.println("1. Register");
                System.out.println("2. Login");
                System.out.println("3. Keluar");
                System.out.print("Pilih: ");
                
                String inputAuth = sc.nextLine().trim();

                if (inputAuth.equals("1")) {
                    register();
                } else if (inputAuth.equals("2")) {
                    login();
                } else if (inputAuth.equals("3")) {
                    System.out.println("Terima kasih! Sampai jumpa.");
                    return; 
                } else {
                    System.out.println("Pilihan tidak valid. Tekan Enter...");
                    sc.nextLine();
                }
            }

            navigasi.clear();
            currentState = AppState.MAIN;

            while (currentState != AppState.LOGOUT) {
                if (currentState == AppState.MAIN) {
                    refreshDatabase();
                }

                switch (currentState) {
                    case MAIN:
                        renderMainMenu();
                        break;
                    case CARI_HOTEL:
                        cariHotel();
                        break;
                    case BOOKING_HOTEL:
                        bookingHotel();
                        break;
                    case PROMO:
                        tampilkanPromo();
                        break;
                    case LAYANAN_TAMBAHAN:
                        layananTambahan();
                        break;
                    case PESANAN_SAYA:
                        pesananSaya();
                        break;
                    case PROFIL:
                        editProfile();
                        break;
                }
            }

            logout();
        }
    }

    static void navigasiKe(AppState tujuan) {
        navigasi.push(currentState);
        currentState = tujuan;
    }

    static void kembali() {
        if (!navigasi.isEmpty()) {
            currentState = navigasi.pop();
        } else {
            currentState = AppState.MAIN;
        }
    }

    static void printHeader(String path) {
        clearScreen();
        System.out.println("==========================================================================");
        System.out.println("                                HOTELKU - " + path);
        System.out.println("==========================================================================");
        System.out.println("                         [M] Menu Utama | [B] Kembali                     ");
        System.out.println("--------------------------------------------------------------------------");
    }

    static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    static void refreshDatabase() {
        bookings = DatabaseHelper.loadBookingsForUser(loggedUser.getIdAkun());
        for (Booking b : bookings) {
            b.refreshStatus();
            String newStatus = b.getStatus().name()
                    .replace("CONFIRMED", "dikonfirmasi")
                    .replace("CHECKED_IN", "check_in")
                    .replace("CHECKED_OUT", "check_out")
                    .replace("REFUNDED", "dibatalkan");
            DatabaseHelper.updateReservationStatus(b.getIdReservasi(), newStatus);
        }
        for (Booking b : bookings) {
            List<ServiceOrder> serv = DatabaseHelper.loadServiceOrdersForReservation(b.getIdReservasi());
            for (ServiceOrder so : serv) so.refreshStatus();
            b.getServices().clear();
            b.getServices().addAll(serv);
        }
    }

    static void renderMainMenu() {
        tampilkanDashboard();
        System.out.println("1. Cari Hotel (Jelajah)");
        System.out.println("2. Booking Hotel (Pesan Langsung)");
        System.out.println("3. Promo Tersedia");
        System.out.println("4. Layanan Tambahan (Selama Menginap)");
        System.out.println("5. Pesanan Saya (Riwayat & Refund)");
        System.out.println("6. Profil");
        System.out.println("7. Logout");
        System.out.print("Pilih (1-7): ");

        String input = sc.nextLine().trim();
        switch (input) {
            case "1": navigasiKe(AppState.CARI_HOTEL); break;
            case "2": navigasiKe(AppState.BOOKING_HOTEL); break;
            case "3": navigasiKe(AppState.PROMO); break;
            case "4": navigasiKe(AppState.LAYANAN_TAMBAHAN); break;
            case "5": navigasiKe(AppState.PESANAN_SAYA); break;
            case "6": navigasiKe(AppState.PROFIL); break;
            case "7": currentState = AppState.LOGOUT; break;
            default:
                System.out.println("Pilihan tidak tersedia. Tekan Enter...");
                sc.nextLine();
        }
    }

    static void tampilkanDashboard() {
        clearScreen();
        System.out.println("==========================================================================");
        System.out.println("                        DASHBOARD UTAMA - HotelKu                         ");
        System.out.println("==========================================================================");
        System.out.println(" Selamat datang, " + loggedUser.getNamaString() + "!");

        int dikonfirmasi = 0, checkIn = 0, checkOut = 0, refunded = 0;
        int layananDiproses = 0;
        boolean adaCheckInHariIni = false;

        for (Booking b : bookings) {
            switch (b.getStatus()) {
                case CONFIRMED: dikonfirmasi++; break;
                case CHECKED_IN:
                    checkIn++;
                    if (b.getCheckInDate().equals(LocalDate.now())) {
                        adaCheckInHariIni = true;
                    }
                    break;
                case CHECKED_OUT: checkOut++; break;
                case REFUNDED: refunded++; break;
            }

            for (ServiceOrder so : b.getServices()) {
                if (so.getStatus() == ServiceOrder.OrderStatus.DIPROSES ||
                    so.getStatus() == ServiceOrder.OrderStatus.DIANTAR) {
                    layananDiproses++;
                }
            }
        }

        if (dikonfirmasi + checkIn + checkOut + refunded > 0) {
            System.out.println("\n Ringkasan Booking:");
            if (dikonfirmasi > 0) System.out.println("    Dikonfirmasi : " + dikonfirmasi);
            if (checkIn > 0)      System.out.println("    Check-in     : " + checkIn);
            if (checkOut > 0)     System.out.println("    Check-out    : " + checkOut);
            if (refunded > 0)     System.out.println("    Direfund     : " + refunded);
        } else {
            System.out.println("\n Anda belum memiliki booking.");
        }

        if (layananDiproses > 0) {
            System.out.println(" Layanan dalam proses : " + layananDiproses + " (Cek menu 'Pesanan Saya')");
        }

        if (adaCheckInHariIni) {
            System.out.println("\n Anda check-in hari ini! Pesan layanan spa/makanan dari menu 'Layanan Tambahan'.");
        }
        if (dikonfirmasi > 0 && !adaCheckInHariIni) {
            System.out.println("\n Anda memiliki booking yang sudah dikonfirmasi. Pastikan check-in tepat waktu.");
        }

        System.out.println("\n--------------------------------------------------------------------------");
        System.out.println("Pilih menu:");
    }

    static void register() {
        clearScreen();
        System.out.println("\n--- REGISTRASI AKUN BARU ---");
        System.out.println("(Isi data berikut. Ketik 0 kapan saja untuk batal.)\n");

        String u, e, p, n, nama, alamat, jk;

        while (true) {
            System.out.print("Username (huruf kecil/angka/_, 3-20 karakter): ");
            u = sc.nextLine().trim();
            if (u.equals("0")) return;
            if (u.matches("^[a-z0-9_]{3,20}$")) break;
            System.out.println(" Username tidak valid.");
        }

        while (true) {
            System.out.print("Email (contoh: nama@domain.com): ");
            e = sc.nextLine().trim();
            if (e.equals("0")) return;
            if (e.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) break;
            System.out.println(" Format email salah.");
        }

        while (true) {
            System.out.print("Password (minimal 6 karakter): ");
            p = sc.nextLine();
            if (p.equals("0")) return;
            if (p.length() >= 6) break;
            System.out.println(" Password terlalu pendek.");
        }

        while (true) {
            System.out.print("No. HP (hanya angka, 10-13 digit): ");
            n = sc.nextLine().trim();
            if (n.equals("0")) return;
            if (n.matches("^\\d{10,13}$")) break;
            System.out.println(" No HP hanya boleh berisi angka 10-13 digit.");
        }

        while (true) {
            System.out.print("Nama Lengkap (hanya huruf & spasi): ");
            nama = sc.nextLine().trim();
            if (nama.equals("0")) return;
            if (!nama.isEmpty() && nama.matches("^[A-Za-z .'-]+$")) break;
            System.out.println(" Nama tidak valid.");
        }

        System.out.print("Alamat (opsional, ketik '-' untuk kosong): ");
        alamat = sc.nextLine().trim();
        if (alamat.equals("0")) return;
        if (alamat.equals("-")) alamat = "";

        while (true) {
            System.out.print("Jenis Kelamin (L/P): ");
            jk = sc.nextLine().trim();
            if (jk.equals("0")) return;
            if (jk.equalsIgnoreCase("L") || jk.equalsIgnoreCase("P")) break;
            System.out.println(" Masukkan 'L' atau 'P'.");
        }

        User newUser = new User(0, u, e, p, n, alamat, nama, jk);
        if (DatabaseHelper.registerUser(newUser)) {
            System.out.println("\n Registrasi berhasil! Silakan login.");
            sc.nextLine();
        } else {
            System.out.println("\n Registrasi gagal. Username atau email mungkin sudah digunakan.");
            sc.nextLine();
        }
    }

    static void login() {
        System.out.print("Username: "); String u = sc.nextLine();
        System.out.print("Password: "); String p = sc.nextLine();
        loggedUser = DatabaseHelper.loginUser(u, p);
        if (loggedUser != null) {
            System.out.println("Login berhasil! Selamat datang, " + loggedUser.getUsername() + "!");
            sc.nextLine();
        } else {
            System.out.println("Login gagal. Periksa username dan password.");
            sc.nextLine();
        }
    }

    static void logout() {
        loggedUser = null;
        System.out.println("Anda telah logout. Tekan Enter untuk kembali ke menu awal...");
        sc.nextLine();
    }

    static void cariHotel() {
        printHeader("Main > Cari Hotel");
        Set<String> cities = hotels.stream()
                .map(Hotel::getLocation)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        System.out.println("Pilih Kota Tujuan:");
        int i = 1;
        for (String city : cities) {
            System.out.println(i++ + ". " + city);
        }

        System.out.print("\nInput [Angka/M/B]: ");
        String input = sc.nextLine().trim().toUpperCase();

        if (input.equals("M")) { currentState = AppState.MAIN; return; }
        if (input.equals("B")) { kembali(); return; }

        try {
            int pilihKota = Integer.parseInt(input);

            if (pilihKota < 1 || pilihKota > cities.size()) {
                System.out.println("Pilihan tidak valid.");
                System.out.print("Tekan Enter untuk mengulang...");
                sc.nextLine();
                return;
            }

            String kotaTerpilih = new ArrayList<>(cities).get(pilihKota - 1);
            List<Hotel> hotelDiKota = hotels.stream()
                    .filter(h -> h.getLocation().equalsIgnoreCase(kotaTerpilih))
                    .collect(Collectors.toList());

            if (hotelDiKota.isEmpty()) {
                System.out.println("Tidak ada hotel di " + kotaTerpilih);
                System.out.print("Tekan Enter untuk mengulang...");
                sc.nextLine();
                return;
            }

            while (true) {
                System.out.println("\nHotel di " + kotaTerpilih + ":");
                for (int j = 0; j < hotelDiKota.size(); j++) {
                    Hotel h = hotelDiKota.get(j);
                    System.out.println((j + 1) + ". " + h.getName() + " [" + h.getStarRating() + "]");
                }

                System.out.print("\nPilih hotel (atau ketik M/B): ");
                String inputHotel = sc.nextLine().trim().toUpperCase();

                if (inputHotel.equals("M")) { currentState = AppState.MAIN; return; }
                if (inputHotel.equals("B")) { return; } 

                try {
                    int pilihHotel = Integer.parseInt(inputHotel);
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

                        System.out.println("\n[Info: Untuk memesan, silakan ke menu Booking Hotel]");
                        System.out.print("Tekan B untuk kembali ke daftar hotel, atau M ke Menu Utama: ");
                        String inputKamar = sc.nextLine().trim().toUpperCase();

                        if (inputKamar.equals("M")) { currentState = AppState.MAIN; return; }
                        if (inputKamar.equals("B")) { break; } 
                    }

                } catch (NumberFormatException e) {
                    System.out.println("Input hotel harus berupa angka, M, atau B.");
                }
            }

        } catch (NumberFormatException e) {
            System.out.println("Input tidak valid! Harap masukkan Angka, M, atau B.");
            System.out.print("Tekan Enter untuk mengulang...");
            sc.nextLine();
        }
    }

    static void bookingHotel() {
        printHeader("Main > Booking Hotel");

        System.out.print("Masukkan kota tujuan (atau ketik M/B): ");
        String inputKota = sc.nextLine().trim();
        
        if (inputKota.equalsIgnoreCase("M")) { currentState = AppState.MAIN; return; }
        if (inputKota.equalsIgnoreCase("B")) { kembali(); return; }

        List<Hotel> hotelDiKota = hotels.stream()
                .filter(h -> h.getLocation().equalsIgnoreCase(inputKota))
                .collect(Collectors.toList());

        if (hotelDiKota.isEmpty()) {
            System.out.println("Tidak ada hotel di " + inputKota + ".");
            System.out.print("Tekan Enter untuk kembali...");
            sc.nextLine();
            return;
        }

        while (true) {
            System.out.println("\nPilih Hotel:");
            for (int i = 0; i < hotelDiKota.size(); i++) {
                Hotel h = hotelDiKota.get(i);
                System.out.println((i + 1) + ". " + h.getName() + " [" + h.getStarRating() + "]");
            }

            System.out.print("\nPilih nomor hotel (atau ketik M/B): ");
            String inputHotel = sc.nextLine().trim().toUpperCase();
            
            if (inputHotel.equals("M")) { currentState = AppState.MAIN; return; }
            if (inputHotel.equals("B")) { kembali(); return; }

            try {
                int pilihHotel = Integer.parseInt(inputHotel);
                if (pilihHotel < 1 || pilihHotel > hotelDiKota.size()) {
                    System.out.println("Pilihan tidak valid.");
                    continue;
                }

                Hotel hotel = hotelDiKota.get(pilihHotel - 1);

                while (true) {
                    System.out.println("\nTipe Kamar di " + hotel.getName() + ":");
                    List<Room> rooms = hotel.getRooms();
                    for (int j = 0; j < rooms.size(); j++) {
                        Room r = rooms.get(j);
                        System.out.println((j + 1) + ". " + r.getType() + " - Rp " + r.getPricePerNight() +
                                "/malam | Stok: " + r.getStock());
                    }

                    System.out.print("\nPilih nomor kamar (atau ketik M/B): ");
                    String inputKamar = sc.nextLine().trim().toUpperCase();
                    
                    if (inputKamar.equals("M")) { currentState = AppState.MAIN; return; }
                    if (inputKamar.equals("B")) { break; } 

                    int pilihKamar = Integer.parseInt(inputKamar);
                    if (pilihKamar < 1 || pilihKamar > rooms.size()) {
                        System.out.println("Pilihan tidak valid.");
                        continue;
                    }

                    Room kamar = rooms.get(pilihKamar - 1);

                    if (kamar.getStock() <= 0) {
                        System.out.println("Maaf, stok kamar ini habis.");
                        continue;
                    }

                    System.out.print("Tanggal check-in (yyyy-mm-dd) [B untuk kembali]: ");
                    String dateInput = sc.nextLine().trim();
                    if (dateInput.equalsIgnoreCase("B")) break;
                    
                    LocalDate checkIn;
                    try {
                        checkIn = LocalDate.parse(dateInput);
                        if (checkIn.isBefore(LocalDate.now())) {
                            System.out.println("Tanggal tidak boleh di masa lalu.");
                            continue;
                        }
                    } catch (DateTimeParseException e) {
                        System.out.println("Format salah (yyyy-mm-dd).");
                        continue;
                    }

                    System.out.print("Lama menginap (malam) [B untuk kembali]: ");
                    String malamInput = sc.nextLine().trim();
                    if (malamInput.equalsIgnoreCase("B")) break;
                    
                    int malam = Integer.parseInt(malamInput);
                    if (malam < 1) {
                        System.out.println("Minimal 1 malam.");
                        continue;
                    }

                    double diskon = 0;
                    System.out.print("Punya kode promo? (kosongkan jika tidak): ");
                    String kode = sc.nextLine().trim();
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
                    int potongan = (int) (hargaDasar * diskon);
                    int total = hargaDasar - potongan;

                    System.out.println("\n--- REVIEW PESANAN ---");
                    System.out.println("Hotel     : " + hotel.getName());
                    System.out.println("Kamar     : " + kamar.getType());
                    System.out.println("Check-in  : " + checkIn + " – Check-out: " + checkIn.plusDays(malam));
                    if (potongan > 0) System.out.println("Diskon    : -Rp " + potongan);
                    System.out.println("Total     : Rp " + total);

                    System.out.print("Metode Pembayaran (transfer_bank / e_wallet) [B = Batal]: ");
                    String metode = sc.nextLine().toLowerCase().trim();
                    if (metode.equalsIgnoreCase("B")) break;
                    
                    if (!metode.equals("transfer_bank") && !metode.equals("e_wallet")) {
                        System.out.println("Metode pembayaran tidak valid.");
                        continue;
                    }

                    System.out.print("Konfirmasi pesanan? (Y/N): ");
                    if (!sc.nextLine().trim().equalsIgnoreCase("Y")) {
                        System.out.println("Pesanan dibatalkan.");
                        continue;
                    }

                    kamar.setStock(kamar.getStock() - 1); 
                    DatabaseHelper.updateStock(kamar.getIdKamar(), kamar.getStock());

                    Booking newBooking = new Booking(0, loggedUser, hotel, kamar, checkIn,
                            checkIn.plusDays(malam), total, metode, Booking.Status.CONFIRMED, promoAktif);
                    int reservasiId = DatabaseHelper.createReservation(newBooking);

                    if (reservasiId > 0) {
                        System.out.println(">>> Booking berhasil! ID reservasi: " + reservasiId);
                    } else {
                        System.out.println(">>> Gagal booking. Data dikembalikan.");
                        kamar.setStock(kamar.getStock() + 1); 
                        DatabaseHelper.updateStock(kamar.getIdKamar(), kamar.getStock()); 
                    }
                    
                    System.out.print("\nTekan Enter untuk kembali ke Menu Utama...");
                    sc.nextLine();
                    kembali(); 
                    return; 
                }

            } catch (NumberFormatException e) {
                System.out.println("Input tidak valid.");
            }
        }
    }

    static void tampilkanPromo() {
        printHeader("Main > Promo");
        System.out.println("\n--- DAFTAR PROMO ---");
        for (Promo p : promos) {
            System.out.println(p);
            System.out.println("   (Berlaku jika check-in sesuai syarat)");
        }
        
        System.out.print("\nTekan Enter untuk kembali...");
        sc.nextLine();
        kembali();
    }

    static void layananTambahan() {
        printHeader("Main > Layanan Tambahan");
        List<Booking> inap = bookings.stream()
                .filter(b -> b.getStatus() == Booking.Status.CHECKED_IN
                        && b.getUser().getIdAkun() == loggedUser.getIdAkun())
                .collect(Collectors.toList());

        if (inap.isEmpty()) {
            System.out.println("Anda tidak sedang menginap di hotel mana pun saat ini.");
            System.out.print("Tekan Enter untuk kembali...");
            sc.nextLine();
            kembali();
            return;
        }

        System.out.println("\nPilih pesanan yang sedang berjalan:");
        for (int i = 0; i < inap.size(); i++) {
            System.out.println((i + 1) + ". " + inap.get(i).info());
        }
        System.out.print("\nPilih (atau ketik M/B): ");
        String inputPesanan = sc.nextLine().trim().toUpperCase();

        if (inputPesanan.equals("M")) { currentState = AppState.MAIN; return; }
        if (inputPesanan.equals("B")) { kembali(); return; }

        try {
            int idx = Integer.parseInt(inputPesanan);
            if (idx < 1 || idx > inap.size()) {
                System.out.println("Pilihan tidak valid.");
                return;
            }
            Booking b = inap.get(idx - 1);

            System.out.println("\nKategori Layanan:");
            System.out.println("1. Spa");
            System.out.println("2. Makanan");
            System.out.println("3. Minuman");
            System.out.print("Pilih kategori (atau ketik M/B): ");
            
            String inputKategori = sc.nextLine().trim().toUpperCase();
            if (inputKategori.equals("M")) { currentState = AppState.MAIN; return; }
            if (inputKategori.equals("B")) { return; }

            int kat = Integer.parseInt(inputKategori);
            if (kat < 1 || kat > 3) {
                System.out.println("Pilihan tidak valid.");
                return;
            }

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
                System.out.print("Tekan Enter untuk kembali...");
                sc.nextLine();
                return;
            }

            System.out.println("\nDaftar " + jenis + ":");
            for (int i = 0; i < menu.size(); i++) {
                ServiceItem item = menu.get(i);
                System.out.println((i + 1) + ". " + item.getName() + " - Rp " + item.getPrice());
            }
            System.out.print("\nPilih nomor menu (atau ketik M/B): ");
            String inputMenu = sc.nextLine().trim().toUpperCase();
            
            if (inputMenu.equals("M")) { currentState = AppState.MAIN; return; }
            if (inputMenu.equals("B")) { return; }

            int pilihMenu = Integer.parseInt(inputMenu);
            if (pilihMenu < 1 || pilihMenu > menu.size()) {
                System.out.println("Pilihan tidak valid.");
                return;
            }
            ServiceItem terpilih = menu.get(pilihMenu - 1);

            System.out.print("Jumlah: ");
            int jumlah = Integer.parseInt(sc.nextLine().trim());
            if (jumlah < 1) {
                System.out.println("Jumlah minimal 1.");
                return;
            }

            int total = terpilih.getPrice() * jumlah;
            System.out.println("Total harga: Rp " + total);
            System.out.print("Konfirmasi pesan? (Y/N): ");
            if (!sc.nextLine().trim().equalsIgnoreCase("Y")) {
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
                System.out.println(">>> Pesanan " + jenis + " berhasil! Status: Diproses.");
            } else {
                System.out.println(">>> Gagal memesan " + jenis + ".");
            }

        } catch (NumberFormatException e) {
            System.out.println("Input tidak valid.");
        }
        
        System.out.print("\nTekan Enter untuk kembali...");
        sc.nextLine();
        kembali();
    }

    static void pesananSaya() {
        printHeader("Main > Pesanan Saya");
        
        String mainHeader = "%-4s | %-20s | %-10s | %-12s | %-15s | %-25s\n";
        String mainRow    = "%-4d | %-20s | %-10s | %-12s | %-15s | %-25s\n";

        System.out.printf(mainHeader, "No", "Hotel", "No. Kamar", "Status", "Total Harga", "Periode Menginap");
        System.out.println("----------------------------------------------------------------------------------------------------------");

        if (bookings.isEmpty()) {
            System.out.println("Belum ada riwayat pesanan.");
        } else {
            for (int i = 0; i < bookings.size(); i++) {
                Booking b = bookings.get(i);
                System.out.printf(mainRow, (i + 1), b.getHotel().getName(), b.getRoom().getNomorKamar(), 
                                b.getStatus(), "Rp " + b.getTotalPrice(),
                                b.getCheckInDate() + " - " + b.getCheckoutDate());
                
                if (!b.getServices().isEmpty()) {
                    System.out.println("      └── Detail Layanan Tambahan:");
                    String subHeader = "          %-15s | %-10s | %-5s | %-10s | %-10s\n";
                    System.out.printf(subHeader, "Layanan", "Harga", "Qty", "Total", "Status");
                    System.out.println("          ------------------------------------------------------------");
                    
                    for (ServiceOrder so : b.getServices()) {
                        System.out.printf(subHeader, so.getNamaLayanan(), "Rp"+so.getHarga(), so.getJumlah(), "Rp"+so.getTotalHarga(), so.getStatus());
                    }
                }
                System.out.println("----------------------------------------------------------------------------------------------------------");
            }
        }

        System.out.print("\nRefund pesanan? [Nomor Urut / M=Menu / B=Kembali]: ");
        String input = sc.nextLine().trim().toUpperCase();

        if (input.equals("M")) { currentState = AppState.MAIN; return; }
        if (input.equals("B")) { kembali(); return; }

        try {
            int idx = Integer.parseInt(input); 
            if (idx < 1 || idx > bookings.size()) {
                System.out.println("Nomor urut tidak valid.");
            } else {
                Booking b = bookings.get(idx - 1);
                if (b.isRefundable()) {
                    b.getRoom().increaseStock();
                    DatabaseHelper.updateStock(b.getRoom().getIdKamar(), b.getRoom().getStock());
                    DatabaseHelper.updateReservationStatus(b.getIdReservasi(), "dibatalkan");
                    b.setStatus(Booking.Status.REFUNDED);
                    System.out.println(">>> Refund berhasil diproses.");
                } else {
                    System.out.println(">>> Maaf, pesanan ini tidak dapat di-refund.");
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Input tidak valid.");
        }

        System.out.print("\nTekan Enter untuk kembali...");
        sc.nextLine();
        kembali(); 
    }

    static void editProfile() {
        printHeader("Main > Edit Profil");
        System.out.println("\n--- EDIT PROFIL ---");
        
        System.out.print("Nama [" + loggedUser.getNamaString() + "]: ");
        String nama = sc.nextLine().trim();
        if (!nama.isEmpty()) loggedUser.setNamaString(nama);

        System.out.print("No HP [" + loggedUser.getPhone() + "]: ");
        String hp = sc.nextLine().trim();
        if (!hp.isEmpty()) loggedUser.setPhone(hp);

        System.out.print("Alamat [" + loggedUser.getAlamatString() + "]: ");
        String alamat = sc.nextLine().trim();
        if (!alamat.isEmpty()) loggedUser.setAlamatString(alamat);

        System.out.print("Jenis Kelamin [" + loggedUser.getJenisKelamin() + "]: ");
        String jk = sc.nextLine().trim();
        if (!jk.isEmpty()) loggedUser.setJenisKelamin(jk);

        if (DatabaseHelper.updateProfile(loggedUser)) {
            System.out.println(">>> Profil diperbarui.");
        } else {
            System.out.println(">>> Gagal memperbarui profil.");
        }
        
        System.out.print("\nTekan Enter untuk kembali...");
        sc.nextLine();
        kembali();
    }
}