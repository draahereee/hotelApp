package hotel;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Booking {
    public enum Status { CONFIRMED, CHECKED_IN, CHECKED_OUT, REFUNDED }

    private String id;
    private User user;
    private Hotel hotel;
    private Room room;
    private LocalDate checkInDate;
    private int nights;
    private int totalPrice;
    private String paymentMethod;
    private Status status;
    private LocalDateTime bookingTime;  // waktu pemesanan dibuat
    private List<ServiceOrder> servicesOrdered; // gunakan ServiceOrder

    public Booking(String id, User user, Hotel hotel, Room room, LocalDate checkInDate,
                   int nights, int totalPrice, String paymentMethod) {
        this.id = id;
        this.user = user;
        this.hotel = hotel;
        this.room = room;
        this.checkInDate = checkInDate;
        this.nights = nights;
        this.totalPrice = totalPrice;
        this.paymentMethod = paymentMethod;
        this.status = Status.CONFIRMED;
        this.bookingTime = LocalDateTime.now();
        this.servicesOrdered = new ArrayList<>();
    }

    public String getId()           { return id; }
    public User getUser()           { return user; }
    public Hotel getHotel()         { return hotel; }
    public Room getRoom()           { return room; }
    public LocalDate getCheckInDate(){ return checkInDate; }
    public int getNights()          { return nights; }
    public int getTotalPrice()      { return totalPrice; }
    public Status getStatus()       { return status; }
    public List<ServiceOrder> getServices() { return servicesOrdered; }

    // Tambah layanan: otomatis buat ServiceOrder baru
    public void addService(ServiceItem serviceItem) {
        servicesOrdered.add(new ServiceOrder(serviceItem));
    }

    // Refresh status semua layanan tambahan
    public void refreshServiceStatuses() {
        for (ServiceOrder so : servicesOrdered) {
            so.refreshStatus();
        }
    }

    // Perbarui status booking berdasarkan waktu sekarang
    public void refreshStatus() {
        LocalDate today = LocalDate.now();
        if (status == Status.CONFIRMED || status == Status.CHECKED_IN) {
            if (!today.isBefore(checkInDate)) {
                if (status == Status.CONFIRMED) status = Status.CHECKED_IN;
                if (today.isAfter(checkInDate.plusDays(nights - 1))) {
                    status = Status.CHECKED_OUT;
                }
            }
        }
    }

    // Refund hanya boleh jika status CONFIRMED dan sekarang <= checkInDate - 2 jam
    public boolean isRefundable() {
        if (status != Status.CONFIRMED) return false;
        LocalDateTime checkInDateTime = checkInDate.atTime(14, 0); // asumsi check‑in jam 14:00
        LocalDateTime deadline = checkInDateTime.minusHours(2);
        return LocalDateTime.now().isBefore(deadline);
    }

    public void setStatus(Status s) { this.status = s; }

    public String info() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return String.format("ID %s | %s | %s | %s (%s) | %d malam | Rp %d | %s",
                id, hotel.getName(), room.getType(),
                checkInDate.format(fmt), status, nights, totalPrice, paymentMethod);
    }
}