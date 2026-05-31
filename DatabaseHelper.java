package hotel;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {

    // ======================= AUTH & PROFILE =======================
    public static User loginUser(String username, String password) {
        String sql = "SELECT a.id_akun, a.username, a.email, a.password, " +
                     "p.nama_pelanggan, p.no_hp, p.alamat, p.jenis_kelamin " +
                     "FROM sistem.akun_pelanggan a " +
                     "LEFT JOIN sistem.pelanggan p ON a.id_akun = p.id_akun " +
                     "WHERE a.username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String dbPass = rs.getString("password");
                if (dbPass.equals(password)) { // plaintext (gunakan BCrypt untuk produksi)
                    return new User(
                        rs.getInt("id_akun"),
                        rs.getString("username"),
                        rs.getString("email"),
                        dbPass,
                        rs.getString("no_hp"),
                        rs.getString("alamat"),
                        rs.getString("nama_pelanggan"),
                        rs.getString("jenis_kelamin")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean registerUser(User user) {
        String sqlAkun = "INSERT INTO sistem.akun_pelanggan (username, email, password) VALUES (?, ?, ?) RETURNING id_akun";
        String sqlPelanggan = "INSERT INTO sistem.pelanggan (id_akun, nama_pelanggan, no_hp, alamat, jenis_kelamin) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            int idAkun;
            try (PreparedStatement psAkun = conn.prepareStatement(sqlAkun)) {
                psAkun.setString(1, user.getUsername());
                psAkun.setString(2, user.getEmail());
                psAkun.setString(3, user.getPassword());
                ResultSet rs = psAkun.executeQuery();
                if (rs.next()) {
                    idAkun = rs.getInt("id_akun");
                } else {
                    throw new SQLException("Gagal insert akun");
                }
            }
            try (PreparedStatement psPelanggan = conn.prepareStatement(sqlPelanggan)) {
                psPelanggan.setInt(1, idAkun);
                psPelanggan.setString(2, user.getNamaString() != null ? user.getNamaString() : user.getUsername());
                psPelanggan.setString(3, user.getPhone());
                psPelanggan.setString(4, user.getAlamatString());
                psPelanggan.setString(5, user.getJenisKelamin());
                psPelanggan.executeUpdate();
            }
            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateProfile(User user) {
        String sql = "UPDATE sistem.pelanggan SET nama_pelanggan = ?, no_hp = ?, alamat = ?, jenis_kelamin = ? WHERE id_akun = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getNamaString());
            ps.setString(2, user.getPhone());
            ps.setString(3, user.getAlamatString());
            ps.setString(4, user.getJenisKelamin());
            ps.setInt(5, user.getIdAkun());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ======================= HOTEL & KAMAR =======================
    public static List<Hotel> loadAllHotels() {
        List<Hotel> hotelList = new ArrayList<>();
        String sql = "SELECT h.id_hotel, h.nama_hotel, h.lokasi_hotel, h.rating, h.deskripsi, " +
                     "k.id_kamar, k.nomor_kamar, t.nama AS tipe, t.harga, t.deskripsi AS fasilitas, k.stok " +
                     "FROM sistem.hotel h " +
                     "JOIN sistem.kamar k ON h.id_hotel = k.id_hotel " +
                     "JOIN sistem.tipe_kamar t ON k.id_tipe = t.id_tipe " +
                     "ORDER BY h.id_hotel, k.id_kamar";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            Hotel currentHotel = null;
            List<Room> rooms = null;
            while (rs.next()) {
                int idHotel = rs.getInt("id_hotel");
                if (currentHotel == null || currentHotel.getIdHotel() != idHotel) {
                    if (currentHotel != null) {
                        hotelList.add(new Hotel(currentHotel.getIdHotel(), currentHotel.getName(),
                                currentHotel.getLocation(), currentHotel.getStarRating(),
                                currentHotel.getDeskripsi(), rooms));
                    }
                    String nama = rs.getString("nama_hotel");
                    String lokasi = rs.getString("lokasi_hotel");
                    double rating = rs.getDouble("rating");
                    String bintang = "Bintang " + rating;
                    String desk = rs.getString("deskripsi");
                    currentHotel = new Hotel(idHotel, nama, lokasi, bintang, desk, new ArrayList<>());
                    rooms = new ArrayList<>();
                }
                int idKamar = rs.getInt("id_kamar");
                int nomor = rs.getInt("nomor_kamar");
                String tipe = rs.getString("tipe");
                int harga = rs.getInt("harga");
                String fasilitas = rs.getString("fasilitas");
                int stok = rs.getInt("stok");
                rooms.add(new Room(idKamar, nomor, tipe, harga, fasilitas, stok));
            }
            if (currentHotel != null) {
                hotelList.add(new Hotel(currentHotel.getIdHotel(), currentHotel.getName(),
                        currentHotel.getLocation(), currentHotel.getStarRating(),
                        currentHotel.getDeskripsi(), rooms));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return hotelList;
    }

    public static void updateStock(int idKamar, int newStock) {
        String sql = "UPDATE sistem.kamar SET stok = ? WHERE id_kamar = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, newStock);
            ps.setInt(2, idKamar);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ======================= PROMO =======================
    public static List<Promo> loadAllPromos() {
        List<Promo> promos = new ArrayList<>();
        String sql = "SELECT id_promo, kode_promo, deskripsi, nilai_diskon, berlaku_dari, berlaku_hingga FROM sistem.promo";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                // Perbaikan Konsep 2: Menangani potensi NullPointerException pada tanggal
                Date berlakuDariSql = rs.getDate("berlaku_dari");
                Date berlakuHinggaSql = rs.getDate("berlaku_hingga");
                LocalDate berlakuDari = (berlakuDariSql != null) ? berlakuDariSql.toLocalDate() : LocalDate.MIN;
                LocalDate berlakuHingga = (berlakuHinggaSql != null) ? berlakuHinggaSql.toLocalDate() : LocalDate.MAX;

                promos.add(new Promo(
                    rs.getInt("id_promo"),
                    rs.getString("kode_promo"),
                    rs.getDouble("nilai_diskon"),
                    rs.getString("deskripsi"),
                    berlakuDari,
                    berlakuHingga
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return promos;
    }

    // ======================= RESERVASI & PEMBAYARAN =======================
    public static int createReservation(Booking booking) {
        System.out.println("Creating reservation for user " + booking.getUser().getUsername() +
                           " in hotel " + booking.getHotel().getName() +
                           " room " + booking.getRoom().getNomorKamar());
        String sqlReservasi = "INSERT INTO sistem.reservasi (id_pelanggan, id_kamar, id_promo, masuk_kamar, keluar_kamar, harga_total, status_reservasi) " +
                              "VALUES (?, ?, ?, ?, ?, ?, ?::sistem.status_pemesanan) RETURNING id_reservasi";
        String sqlPembayaran = "INSERT INTO sistem.pembayaran (id_reservasi, metode_pembayaran) VALUES (?, ?::sistem.cara_bayar)";
        
        // Perbaikan Konsep 1: Mengurangi stok secara aman di level database untuk mencegah Overbooking
        String sqlKurangiStok = "UPDATE sistem.kamar SET stok = stok - 1 WHERE id_kamar = ? AND stok > 0";
        
        int reservasiId = 0;
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            // 1. Amankan Stok Terlebih Dahulu
            try (PreparedStatement psStok = conn.prepareStatement(sqlKurangiStok)) {
                psStok.setInt(1, booking.getRoom().getIdKamar());
                int affectedRows = psStok.executeUpdate();
                if (affectedRows == 0) {
                    System.out.println("Gagal booking: Stok kamar sudah habis!");
                    conn.rollback(); // Batalkan transaksi
                    return 0;
                }
            }

            // 2. Insert reservasi
            try (PreparedStatement ps = conn.prepareStatement(sqlReservasi)) {
                int idPelanggan = getPelangganIdByAkun(booking.getUser().getIdAkun(), conn);
                ps.setInt(1, idPelanggan);
                ps.setInt(2, booking.getRoom().getIdKamar());
                if (booking.getPromo() != null) {
                    ps.setInt(3, booking.getPromo().getIdPromo());
                } else {
                    ps.setNull(3, Types.INTEGER);
                }
                ps.setDate(4, Date.valueOf(booking.getCheckInDate()));
                ps.setDate(5, Date.valueOf(booking.getCheckoutDate()));
                ps.setInt(6, booking.getTotalPrice());
                ps.setString(7, "dikonfirmasi"); // status awal
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    reservasiId = rs.getInt("id_reservasi");
                } else {
                    throw new SQLException("Gagal insert reservasi");
                }
            }
            
            // 3. Insert pembayaran
            try (PreparedStatement psPemb = conn.prepareStatement(sqlPembayaran)) {
                psPemb.setInt(1, reservasiId);
                psPemb.setString(2, booking.getPaymentMethod());
                psPemb.executeUpdate();
            }
            
            conn.commit();
            System.out.println("Inserted reservation & payment with ID " + reservasiId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reservasiId;
    }

    public static void updateReservationStatus(int reservasiId, String newStatus) {
        String sql = "UPDATE sistem.reservasi SET status_reservasi = ?::sistem.status_pemesanan WHERE id_reservasi = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, reservasiId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // helper: dapat id_pelanggan dari id_akun (hanya dipakai internal)
    private static int getPelangganIdByAkun(int idAkun, Connection conn) throws SQLException {
        String sql = "SELECT id_pelanggan FROM sistem.pelanggan WHERE id_akun = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idAkun);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("id_pelanggan");
            throw new SQLException("Pelanggan tidak ditemukan untuk id_akun " + idAkun);
        }
    }

    // ======================= LOAD BOOKING MILIK USER =======================
    public static List<Booking> loadBookingsForUser(int idAkun) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT r.id_reservasi, r.masuk_kamar, r.keluar_kamar, r.harga_total, r.status_reservasi, " +
                     "h.id_hotel, h.nama_hotel, h.lokasi_hotel, h.rating, h.deskripsi, " +
                     "k.id_kamar, k.nomor_kamar, t.nama AS tipe, t.harga, t.deskripsi AS fasilitas, k.stok, " +
                     "p.metode_pembayaran, " +
                     "pr.id_promo, pr.kode_promo, pr.deskripsi AS promo_desc, pr.nilai_diskon, pr.berlaku_dari, pr.berlaku_hingga " +
                     "FROM sistem.reservasi r " +
                     "JOIN sistem.pelanggan pl ON r.id_pelanggan = pl.id_pelanggan " +
                     "JOIN sistem.akun_pelanggan a ON pl.id_akun = a.id_akun " +
                     "JOIN sistem.kamar k ON r.id_kamar = k.id_kamar " +
                     "JOIN sistem.hotel h ON k.id_hotel = h.id_hotel " +
                     "JOIN sistem.tipe_kamar t ON k.id_tipe = t.id_tipe " +
                     "LEFT JOIN sistem.pembayaran p ON r.id_reservasi = p.id_reservasi " +
                     "LEFT JOIN sistem.promo pr ON r.id_promo = pr.id_promo " +
                     "WHERE a.id_akun = ? " +
                     "ORDER BY r.id_reservasi DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idAkun);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                // build User minimal (hanya perlu idAkun)
                User user = new User(idAkun, "", "", "", "", "", "", "");
                
                Hotel hotel = new Hotel(
                    rs.getInt("id_hotel"),
                    rs.getString("nama_hotel"),
                    rs.getString("lokasi_hotel"),
                    "Bintang " + rs.getDouble("rating"),
                    rs.getString("deskripsi"),
                    new ArrayList<>()
                );
                
                Room room = new Room(
                    rs.getInt("id_kamar"),
                    rs.getInt("nomor_kamar"),
                    rs.getString("tipe"),
                    rs.getInt("harga"),
                    rs.getString("fasilitas"),
                    rs.getInt("stok")
                );
                
                Promo promo = null;
                if (rs.getObject("id_promo") != null) {
                    Date bdSql = rs.getDate("berlaku_dari");
                    Date bhSql = rs.getDate("berlaku_hingga");
                    promo = new Promo(
                        rs.getInt("id_promo"),
                        rs.getString("kode_promo"),
                        rs.getDouble("nilai_diskon"),
                        rs.getString("promo_desc"),
                        bdSql != null ? bdSql.toLocalDate() : LocalDate.MIN,
                        bhSql != null ? bhSql.toLocalDate() : LocalDate.MAX
                    );
                }
                
                String statusStr = rs.getString("status_reservasi");
                Booking.Status status = Booking.Status.valueOf(
                    statusStr.toUpperCase()
                            .replace("DIKONFIRMASI", "CONFIRMED")
                            .replace("CHECK_IN", "CHECKED_IN")
                            .replace("CHECK_OUT", "CHECKED_OUT")
                            .replace("DIBATALKAN", "REFUNDED")
                );

                // Perbaikan Konsep 2: Menangani potensi null dari kolom tanggal booking
                Date masukSql = rs.getDate("masuk_kamar");
                Date keluarSql = rs.getDate("keluar_kamar");
                LocalDate masuk = masukSql != null ? masukSql.toLocalDate() : LocalDate.now();
                LocalDate keluar = keluarSql != null ? keluarSql.toLocalDate() : LocalDate.now().plusDays(1);

                Booking b = new Booking(
                    rs.getInt("id_reservasi"),
                    user,
                    hotel,
                    room,
                    masuk,
                    keluar,
                    rs.getInt("harga_total"),
                    rs.getString("metode_pembayaran") != null ? rs.getString("metode_pembayaran") : "Transfer",
                    status,
                    promo
                );
                bookings.add(b);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }

    
    // load daftar spa berdasarkan hotel (mengembalikan ServiceItem)
    public static List<ServiceItem> loadSpaMenu(int hotelId) {
        List<ServiceItem> list = new ArrayList<>();
        String sql = "SELECT id_layanan_spa, nama_layanan, harga " +
                    "FROM sistem.layanan_spa " +
                    "WHERE id_hotel = ? AND status = 'Tersedia'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, hotelId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id_layanan_spa");
                String name = rs.getString("nama_layanan");
                int price = rs.getInt("harga");
                list.add(new ServiceItem(id, name, price));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public static List<ServiceItem> loadMakananMenu() {
        List<ServiceItem> list = new ArrayList<>();
        String sql = "SELECT id_makanan, nama_makanan, harga FROM sistem.menu_makanan WHERE tersedia = TRUE";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id_makanan");
                String name = rs.getString("nama_makanan");
                int price = rs.getInt("harga");
                list.add(new ServiceItem(id, name, price));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public static List<ServiceItem> loadMinumanMenu() {
        List<ServiceItem> list = new ArrayList<>();
        String sql = "SELECT id_minuman, nama_minuman, harga FROM sistem.menu_minuman WHERE tersedia = TRUE";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id_minuman");
                String name = rs.getString("nama_minuman");
                int price = rs.getInt("harga");
                list.add(new ServiceItem(id, name, price));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // Pemesanan layanan
    public static boolean orderSpa(int reservasiId, int layananSpaId, int jumlah, int hargaSatuan) {
        String sql = "INSERT INTO sistem.pemesanan_spa " +
                     "(id_reservasi, id_layanan_spa, tanggal_spa, jumlah_orang, total_harga) " +
                     "VALUES (?, ?, NOW(), ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reservasiId);
            ps.setInt(2, layananSpaId);
            ps.setInt(3, jumlah);
            ps.setInt(4, hargaSatuan * jumlah);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public static boolean orderMakanan(int reservasiId, int makananId, int jumlah, int hargaSatuan) {
        String sql = "INSERT INTO sistem.pemesanan_makanan " +
                     "(id_reservasi, id_makanan, jumlah, harga, total_harga) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reservasiId);
            ps.setInt(2, makananId);
            ps.setInt(3, jumlah);
            ps.setInt(4, hargaSatuan);
            ps.setInt(5, hargaSatuan * jumlah);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public static boolean orderMinuman(int reservasiId, int minumanId, int jumlah, int hargaSatuan) {
        String sql = "INSERT INTO sistem.pemesanan_minuman " +
                     "(id_reservasi, id_minuman, jumlah, harga, total_harga) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reservasiId);
            ps.setInt(2, minumanId);
            ps.setInt(3, jumlah);
            ps.setInt(4, hargaSatuan);
            ps.setInt(5, hargaSatuan * jumlah);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // Load semua layanan yang telah dipesan untuk satu reservasi
    public static List<ServiceOrder> loadServiceOrdersForReservation(int reservasiId) {
        List<ServiceOrder> list = new ArrayList<>();
        // Spa
        String sqlSpa = "SELECT ps.id_pemesanan_spa, ps.id_reservasi, ls.nama_layanan, ls.harga, " +
                        "ps.jumlah_orang, ps.total_harga, ps.status::text AS status, ps.tanggal_spa " +
                        "FROM sistem.pemesanan_spa ps JOIN sistem.layanan_spa ls ON ps.id_layanan_spa = ls.id_layanan_spa " +
                        "WHERE ps.id_reservasi = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlSpa)) {
            ps.setInt(1, reservasiId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ServiceOrder.OrderStatus status = statusFromString(rs.getString("status"));
                Timestamp ts = rs.getTimestamp("tanggal_spa");
                list.add(new ServiceOrder(
                    rs.getInt("id_pemesanan_spa"),
                    rs.getInt("id_reservasi"),
                    ServiceOrder.ServiceType.SPA,
                    rs.getString("nama_layanan"),
                    rs.getInt("harga"),
                    rs.getInt("jumlah_orang"),
                    rs.getInt("total_harga"),
                    status,
                    ts != null ? ts.toLocalDateTime() : LocalDateTime.now()
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }

        // Makanan
        String sqlMakan = "SELECT pm.id_pesanan_makanan, pm.id_reservasi, mm.nama_makanan, mm.harga, " +
                          "pm.jumlah, pm.total_harga, pm.status::text AS status, pm.waktu_pesan " +
                          "FROM sistem.pemesanan_makanan pm JOIN sistem.menu_makanan mm ON pm.id_makanan = mm.id_makanan " +
                          "WHERE pm.id_reservasi = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlMakan)) {
            ps.setInt(1, reservasiId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ServiceOrder.OrderStatus status = statusFromString(rs.getString("status"));
                Timestamp ts = rs.getTimestamp("waktu_pesan");
                list.add(new ServiceOrder(
                    rs.getInt("id_pesanan_makanan"),
                    rs.getInt("id_reservasi"),
                    ServiceOrder.ServiceType.MAKANAN,
                    rs.getString("nama_makanan"),
                    rs.getInt("harga"),
                    rs.getInt("jumlah"),
                    rs.getInt("total_harga"),
                    status,
                    ts != null ? ts.toLocalDateTime() : LocalDateTime.now()
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }

        // Minuman
        String sqlMinum = "SELECT pmi.id_pesanan_minuman, pmi.id_reservasi, dm.nama_minuman, dm.harga, " +
                          "pmi.jumlah, pmi.total_harga, pmi.status::text AS status, pmi.waktu_pesan " +
                          "FROM sistem.pemesanan_minuman pmi JOIN sistem.menu_minuman dm ON pmi.id_minuman = dm.id_minuman " +
                          "WHERE pmi.id_reservasi = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlMinum)) {
            ps.setInt(1, reservasiId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ServiceOrder.OrderStatus status = statusFromString(rs.getString("status"));
                Timestamp ts = rs.getTimestamp("waktu_pesan");
                list.add(new ServiceOrder(
                    rs.getInt("id_pesanan_minuman"),
                    rs.getInt("id_reservasi"),
                    ServiceOrder.ServiceType.MINUMAN,
                    rs.getString("nama_minuman"),
                    rs.getInt("harga"),
                    rs.getInt("jumlah"),
                    rs.getInt("total_harga"),
                    status,
                    ts != null ? ts.toLocalDateTime() : LocalDateTime.now()
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }

        return list;
    }

    // helper untuk konversi string status ke enum ServiceOrder.OrderStatus
    private static ServiceOrder.OrderStatus statusFromString(String status) {
        if (status == null) return ServiceOrder.OrderStatus.DIPROSES;
        switch (status.toLowerCase()) {
            case "diproses":   return ServiceOrder.OrderStatus.DIPROSES;
            case "diantar":    return ServiceOrder.OrderStatus.DIANTAR;
            case "selesai":    return ServiceOrder.OrderStatus.SELESAI;
            case "dibatalkan": return ServiceOrder.OrderStatus.DIBATALKAN;
            default:           return ServiceOrder.OrderStatus.DIPROSES;
        }
    }
}