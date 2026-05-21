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

    // Constructor singkat (tanpa status dan promo) – bisa untuk membuat objek sementara
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
            so.refreshStatus();  // asumsikan ServiceOrder punya method ini
        }
    }

    // Memperbarui status booking berdasarkan tanggal hari ini
    public void refreshStatus() {
        LocalDate today = LocalDate.now();
        if (status == Status.CONFIRMED || status == Status.CHECKED_IN) {
            if (!today.isBefore(checkInDate)) {
                if (status == Status.CONFIRMED)
                    status = Status.CHECKED_IN;
                if (today.isAfter(checkoutDate)) {
                    status = Status.CHECKED_OUT;
                }
            }
        }
    }

    // Cek apakah booking bisa direfund
    public boolean isRefundable() {
        if (status != Status.CONFIRMED) return false;
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