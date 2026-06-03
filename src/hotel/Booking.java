package hotel;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Booking {
    public enum Status { CONFIRMED, CHECKED_IN, CHECKED_OUT, REFUNDED }

    private int idReservasi;
    private User user;
    private Hotel hotel;
    private Room room;
    private LocalDate checkInDate;
    private LocalDate checkoutDate;
    private int totalPrice;
    private String paymentMethod;
    private Status status;
    private Promo promo;
    private List<ServiceOrder> servicesOrdered;

    // Constructor lengkap (10 parameter) – digunakan saat load dari DB
    public Booking(int idReservasi, User user, Hotel hotel, Room room,
                   LocalDate checkInDate, LocalDate checkoutDate,
                   int totalPrice, String paymentMethod,
                   Status status, Promo promo) {
        this.idReservasi = idReservasi;
        this.user = user;
        this.hotel = hotel;
        this.room = room;
        this.checkInDate = checkInDate;
        this.checkoutDate = checkoutDate;
        this.totalPrice = totalPrice;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.promo = promo;
        this.servicesOrdered = new ArrayList<>();
    }

    // Constructor singkat (8 parameter) – digunakan saat buat booking baru
    public Booking(int idReservasi, User user, Hotel hotel, Room room,
                   LocalDate checkInDate, LocalDate checkoutDate,
                   int totalPrice, String paymentMethod) {
        this(idReservasi, user, hotel, room, checkInDate, checkoutDate,
             totalPrice, paymentMethod, Status.CONFIRMED, null);
    }

    // Getter
    public int getIdReservasi()          { return idReservasi; }
    public User getUser()                { return user; }
    public Hotel getHotel()              { return hotel; }
    public Room getRoom()                { return room; }
    public LocalDate getCheckInDate()    { return checkInDate; }
    public LocalDate getCheckoutDate()   { return checkoutDate; }
    public int getTotalPrice()           { return totalPrice; }
    public String getPaymentMethod()     { return paymentMethod; }
    public Status getStatus()            { return status; }
    public Promo getPromo()              { return promo; }
    public List<ServiceOrder> getServices() { return servicesOrdered; }

    // Menambah layanan (menerima ServiceOrder)
    public void addService(ServiceOrder so) {
        servicesOrdered.add(so);
    }

    // Memperbarui status semua layanan dalam booking ini
    public void refreshServiceStatuses() {
        for (ServiceOrder so : servicesOrdered) {
            so.refreshStatus();  
        }
    }

    // Memperbarui status booking berdasarkan tanggal hari ini
    public void refreshStatus() {
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime checkInDeadline = checkInDate.atTime(14, 0);
        
        // Aturan: Jika sudah jam 14:00 di hari check-in dan tamu tidak datang (belum check-in), 
        // status diubah menjadi CHECKED_OUT (pesanan hangus, tidak ada refund)
        if (status == Status.CONFIRMED && now.isAfter(checkInDeadline)) {
            status = Status.CHECKED_OUT;
        }
        // Aturan: Jika belum jam 14:00 di hari check-in, user bisa masuk menjadi CHECKED_IN
        else if (status == Status.CONFIRMED && !today.isBefore(checkInDate) && now.isBefore(checkInDeadline)) {
            status = Status.CHECKED_IN;
        }
        
        // Catatan: Auto Check-Out berdasarkan checkoutDate dihapus dari sini
        // Kita menggunakan isOperationalValid() sebagai gantinya.
    }

    // Aturan: Memastikan pesanan/layanan tidak bisa dilakukan 
    // jika tanggal hari ini sudah melewati batas checkout yang tertera di aplikasi.
    public boolean isOperationalValid() {
        LocalDate today = LocalDate.now();
        
        // Jika status sudah selesai atau batal, operasi tidak valid
        if (status == Status.CHECKED_OUT || status == Status.REFUNDED) {
            return false;
        }
        
        // Jika hari ini sudah melewati tanggal keluar kamar, kunci semua akses pesanan
        if (today.isAfter(checkoutDate)) {
            return false; 
        }
        
        return true;
    }

    // Cek apakah booking bisa direfund
    public boolean isRefundable() {
        if (status != Status.CONFIRMED) return false;
        
        // Aturan: Refund maksimal 2 jam sebelum jam 14:00 hari check-in (jam 12:00)
        LocalDateTime checkInDateTime = checkInDate.atTime(14, 0);
        LocalDateTime deadline = checkInDateTime.minusHours(2);
        
        return LocalDateTime.now().isBefore(deadline);
    }

    public void setStatus(Status s) { this.status = s; }

    // Informasi singkat booking
    public String info() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return String.format("ID %d | %s | %s | %s - %s | Status: %s | Rp %d | %s",
                idReservasi, hotel.getName(), room.getType(),
                checkInDate.format(fmt), checkoutDate.format(fmt),
                status, totalPrice, paymentMethod);
    }
}