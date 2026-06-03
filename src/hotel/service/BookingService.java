package hotel.service;

import hotel.database.DatabaseHelper;
import hotel.model.Booking;
import hotel.model.Room;
import hotel.model.ServiceOrder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class BookingService {
    public List<Booking> loadAndRefreshBookingsForUser(int idAkun) {
        List<Booking> bookings = DatabaseHelper.loadBookingsForUser(idAkun);
        for (Booking booking : bookings) {
            booking.refreshStatus();
            DatabaseHelper.updateReservationStatus(booking.getIdReservasi(), toDatabaseStatus(booking.getStatus()));

            List<ServiceOrder> serviceOrders = DatabaseHelper.loadServiceOrdersForReservation(booking.getIdReservasi());
            for (ServiceOrder serviceOrder : serviceOrders) {
                serviceOrder.refreshStatus();
            }
            booking.getServices().clear();
            booking.getServices().addAll(serviceOrders);
        }
        return bookings;
    }

    public boolean isRoomAvailable(Room room, LocalDate checkIn, LocalDate checkout) {
        return DatabaseHelper.isRoomAvailable(room.getIdKamar(), checkIn, checkout);
    }

    public int createReservation(Booking booking) {
        return DatabaseHelper.createReservation(booking);
    }

    public boolean refund(Booking booking) {
        if (!booking.isRefundable()) {
            return false;
        }
        DatabaseHelper.updateReservationStatus(booking.getIdReservasi(), "dibatalkan");
        booking.setStatus(Booking.Status.REFUNDED);
        return true;
    }

    public String getRefundRejectionReason(Booking booking) {
        if (booking.getStatus() == Booking.Status.CHECKED_IN) {
            return "Anda sudah check-in. Tidak ada refund untuk booking yang sedang berjalan.";
        }
        if (booking.getStatus() == Booking.Status.CHECKED_OUT) {
            return "Anda sudah check-out. Tidak ada refund untuk booking yang sudah selesai.";
        }
        if (booking.getStatus() == Booking.Status.REFUNDED) {
            return "Pesanan ini sudah direfund sebelumnya.";
        }
        if (LocalDateTime.now().isAfter(booking.getCheckInDate().atTime(14, 0))) {
            return "Sudah melewati jam 14:00 check-in. Tidak ada refund untuk keterlambatan check-in.";
        }
        return "Pesanan ini tidak dapat di-refund.";
    }

    private String toDatabaseStatus(Booking.Status status) {
        switch (status) {
            case CONFIRMED:
                return "dikonfirmasi";
            case CHECKED_IN:
                return "check_in";
            case CHECKED_OUT:
                return "check_out";
            case REFUNDED:
                return "dibatalkan";
            default:
                return "dikonfirmasi";
        }
    }
}
