package hotel;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
  
    static List<User> users = new ArrayList<>();
    static List<Hotel> hotels = new ArrayList<>();
    static List<Promo> promos = new ArrayList<>();
    static List<Booking> bookings = new ArrayList<>();
    static List<ServiceItem> serviceCatalog = new ArrayList<>();

    static User loggedUser = null;
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        seedData();

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
            refreshAllBookings();
            System.out.println("\n=================================");
            System.out.println("       DASHBOARD UTAMA           ");
            System.out.println("=================================");
            System.out.println("1. Cari Hotel (Jelajah)");
            System.out.println("2. Booking Hotel (Pesan Langsung)");
            System.out.println("3. Promo Tersedia");
            System.out.println("4. Layanan Tambahan (Selama Menginap)");
            System.out.println("5. Pesanan Saya (Riwayat & Refund)");
            System.out.println("6. Logout");
            System.out.print("Pilih: ");
            menu = inputInt();
            sc.nextLine();

            switch (menu) {
                case 1: cariHotel();       break;
                case 2: bookingHotel();    break;
                case 3: tampilkanPromo();  break;
                case 4: layananTambahan(); break;
                case 5: pesananSaya();     break;
                case 6: logout();          break;
                default: System.out.println("Pilihan tidak tersedia.");
            }
        } while (menu != 6);
    }

    
    static void register() {        
        System.out.print("Username: ");     String u = sc.nextLine();
        System.out.print("Email: ");        String e = sc.nextLine();
        System.out.print("Password: ");     String p = sc.nextLine();
        System.out.print("No. Handphone: ");String n = sc.nextLine();
        // Validasi sederhana
        if (u.isEmpty() || e.isEmpty() || p.isEmpty()) {
            System.out.println("Semua field harus diisi.");
            return;
        }
        users.add(new User(u, e, p, n));
        System.out.println("Registrasi berhasil! Silakan login.");  
    }
    static void login(){         
        System.out.print("Username: "); String u = sc.nextLine();
        System.out.print("Password: "); String p = sc.nextLine();
        for (User user : users) {
            if (user.getUsername().equals(u) && user.getPassword().equals(p)) {
                loggedUser = user;
                System.out.println("🎉 Login berhasil. Selamat datang, " + u + "!");
                return;
            }
        }
        System.out.println("Username atau password salah."); }
    static void logout()   { loggedUser = null; System.out.println("Anda telah logout."); }

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
                    System.out.println((j+1) + ". " + h.getName() + " [" + h.getStarRating() + "]");
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
                        System.out.println((k+1) + ". " + r.getType() + " - Rp " + r.getPricePerNight() +
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
                System.out.println((i+1) + ". " + h.getName() + " [" + h.getStarRating() + "]");
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
                    System.out.println((j+1) + ". " + r.getType() + " - Rp " + r.getPricePerNight() +
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
                    System.out.println("Stok kamar ini habis. Silakan pilih kamar lain.");
                    continue;
                }

                LocalDate checkIn = inputDate("Tanggal check‑in (yyyy-mm-dd): ");
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
                        System.out.println("Kode tidak valid atau tidak berlaku untuk tanggal tersebut.");
                    } else {
                        System.out.println("Promo " + promoAktif.getCode() + " diterapkan! Diskon " + (int)(diskon*100) + "%");
                    }
                }

                int hargaDasar = kamar.getPricePerNight() * malam;
                int potongan = (int)(hargaDasar * diskon);
                int total = hargaDasar - potongan;

                System.out.println("\n--- REVIEW PESANAN ---");
                System.out.println("Hotel: " + hotel.getName());
                System.out.println("Kamar: " + kamar.getType());
                System.out.println("Check‑in: " + checkIn);
                System.out.println("Malam: " + malam);
                System.out.println("Harga: Rp " + hargaDasar);
                if (potongan > 0) System.out.println("Diskon: -Rp " + potongan);
                System.out.println("Total: Rp " + total);
                System.out.print("Metode Pembayaran (E‑Wallet/Transfer): ");
                String pay = sc.nextLine();

                System.out.print("Konfirmasi pesanan? (Y/N): ");
                if (!sc.nextLine().equalsIgnoreCase("Y")) {
                    System.out.println("Pesanan dibatalkan.");
                    return;
                }

                String trxId = "TRX-" + (int)(Math.random()*9000+1000);
                kamar.decreaseStock();
                bookings.add(new Booking(trxId, loggedUser, hotel, kamar, checkIn, malam, total, pay));
                System.out.println("Booking berhasil! ID: " + trxId);
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
        List<Booking> inapSekarang = new ArrayList<>();
        for (Booking b : bookings) {
            b.refreshStatus();
            if (b.getStatus() == Booking.Status.CHECKED_IN && b.getUser().getUsername().equals(loggedUser.getUsername())) {
                inapSekarang.add(b);
            }
        }
        if (inapSekarang.isEmpty()) {
            System.out.println("Anda tidak sedang menginap saat ini.");
            return;
        }

        System.out.println("\n--- LAYANAN TAMBAHAN SELAMA MENGINAP ---");
        System.out.println("Pilih pesanan yang sedang berjalan:");
        for (int i = 0; i < inapSekarang.size(); i++) {
            System.out.println((i+1) + ". " + inapSekarang.get(i).info());
        }
        System.out.print("Pilih nomor pesanan (0 = batal): ");
        int idx = inputInt(); sc.nextLine();
        if (idx == 0 || idx < 1 || idx > inapSekarang.size()) return;
        Booking b = inapSekarang.get(idx-1);

        System.out.println("\nLayanan Tersedia:");
        for (int i = 0; i < serviceCatalog.size(); i++) {
            ServiceItem s = serviceCatalog.get(i);
            System.out.println((i+1) + ". " + s.getName() + " - Rp " + s.getPrice());
        }
        System.out.print("Pilih layanan (0 = selesai): ");
        int pilihLayanan = inputInt(); sc.nextLine();
        if (pilihLayanan < 1 || pilihLayanan > serviceCatalog.size()){
            System.out.println("Pilihan layanan tidak valid.");
            return;
    }
        ServiceItem layanan = serviceCatalog.get(pilihLayanan-1);

        b.addService(layanan);
        System.out.println(" Layanan " + layanan.getName() + " ditambahkan. Status: CONFIRMED");
    }


static void pesananSaya() {
    refreshAllBookings();
    System.out.println("\n--- RIWAYAT & PESANAN ANDA ---");
    List<Booking> punyaUser = new ArrayList<>();
    for (Booking b : bookings) {
        if (b.getUser().getUsername().equals(loggedUser.getUsername())) {
            punyaUser.add(b);
        }
    }
    if (punyaUser.isEmpty()) {
        System.out.println("Belum ada pemesanan.");
        return;
    }

    for (int i = 0; i < punyaUser.size(); i++) {
        Booking b = punyaUser.get(i);
        System.out.println((i + 1) + ". " + b.info());
        if (!b.getServices().isEmpty()) {
            System.out.println("   Layanan Tambahan:");
            for (ServiceOrder so : b.getServices()) {
                System.out.println("     - " + so);
            } 
        } 
    } 

    System.out.print("\nIngin refund pesanan? (Y/N): ");
    String tanya = sc.nextLine();
    if (tanya.equalsIgnoreCase("Y")) {
        System.out.print("Masukkan nomor urut pesanan: ");
        int idx = inputInt();
        sc.nextLine();
        if (idx < 1 || idx > punyaUser.size()) {
            System.out.println("Nomor tidak valid.");
            return;
        }
        Booking b = punyaUser.get(idx - 1);
        if (b.isRefundable()) {
            b.getRoom().increaseStock();
            b.setStatus(Booking.Status.REFUNDED);
            System.out.println("Refund berhasil! Uang sebesar Rp " + b.getTotalPrice() + " akan dikembalikan.");
        } else {
            System.out.println("Refund tidak dapat dilakukan. Pastikan status CONFIRMED dan masih > 2 jam sebelum check‑in.");
        }
    }
}

    static void refreshAllBookings() {
        for (Booking b : bookings){
            b.refreshStatus();
            b.refreshServiceStatuses();
        } 
    }

    static int inputInt() {
        while (!sc.hasNextInt()) {
            System.out.print("Masukkan angka valid: ");
            sc.next();
        }
        return sc.nextInt();
    }

    static LocalDate inputDate(String prompt) {
        System.out.print(prompt);
        try {
            return LocalDate.parse(sc.nextLine(), DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            System.out.println("Format salah (yyyy-mm-dd).");
            return null;
        }
    }

    static void seedData() {
     
        users.add(new User("admin", "admin@mail.com", "123", "08123456789"));

        List<Room> roomsA = new ArrayList<>();
        roomsA.add(new Room("Superior", 600000, "WiFi, Breakfast", 3));
        roomsA.add(new Room("Deluxe",  1200000,"WiFi, Breakfast, Mini Bar", 2));

        List<Room> roomsB = new ArrayList<>();
        roomsB.add(new Room("Standard",400000, "WiFi", 5));
        roomsB.add(new Room("Suite",   1500000,"WiFi, Breakfast, Living Room", 1));
        hotels.add(new Hotel("Hotel Santika", "Jakarta", "Bintang 4", roomsA));
        hotels.add(new Hotel("Santika Premiere", "Medan", "Bintang 4", roomsB));
        promos.add(new Promo("MINGGU10", 0.10, "Diskon 10% jika check‑in hari Minggu", "SUNDAY"));
        promos.add(new Promo("LIBUR20", 0.20, "Diskon 20% hari libur (belum diatur)", "HOLIDAY"));
        serviceCatalog.add(new ServiceItem("Spa & Massage", 200000));
        serviceCatalog.add(new ServiceItem("Makan Malam Spesial", 150000));
        serviceCatalog.add(new ServiceItem("Minuman Welcome Drink", 50000));
    }
}