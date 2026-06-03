package hotel.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Booking implements Payable {
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

    public Booking(int idReservasi, User user, Hotel hotel, Room room,
                   LocalDate checkInDate, LocalDate checkoutDate,
                   int totalPrice, String paymentMethod) {
        this(idReservasi, user, hotel, room, checkInDate, checkoutDate,
             totalPrice, paymentMethod, Status.CONFIRMED, null);
    }

   
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

   
    public void addService(ServiceOrder so) {
        servicesOrdered.add(so);
    }

 
    public void refreshServiceStatuses() {
        for (ServiceOrder so : servicesOrdered) {
            so.refreshStatus();  
        }
    }


    public void refreshStatus() {
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime checkInTime = checkInDate.atTime(14, 0);
        LocalDateTime checkoutTime = checkoutDate.atTime(12, 0);

        if (status == Status.REFUNDED) {
            return;
        }

        if (!now.isBefore(checkoutTime)) {
            status = Status.CHECKED_OUT;
        } else if (!today.isBefore(checkInDate) && !now.isBefore(checkInTime)) {
            status = Status.CHECKED_IN;
        }
    }


    public boolean isOperationalValid() {
        LocalDate today = LocalDate.now();
        
        
        if (status == Status.CHECKED_OUT || status == Status.REFUNDED) {
            return false;
        }
        

        if (today.isAfter(checkoutDate)) {
            return false; 
        }
        
        return true;
    }

  
    public boolean isRefundable() {
        if (status != Status.CONFIRMED) return false;
        
        LocalDateTime checkInDateTime = checkInDate.atTime(14, 0);
        LocalDateTime deadline = checkInDateTime.minusHours(2);
        
        return LocalDateTime.now().isBefore(deadline);
    }

    public void setStatus(Status s) { this.status = s; }

    @Override
    public int calculateTotal() {
        int serviceTotal = 0;
        for (ServiceOrder so : servicesOrdered) {
            serviceTotal += so.calculateTotal();
        }
        return totalPrice + serviceTotal;
    }


    public String info() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return String.format("ID %d | %s | %s | %s - %s | Status: %s | Rp %d | %s",
                idReservasi, hotel.getName(), room.getType(),
                checkInDate.format(fmt), checkoutDate.format(fmt),
                status, totalPrice, paymentMethod);
    }
}
