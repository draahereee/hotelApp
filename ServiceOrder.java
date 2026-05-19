package hotel;
import java.time.LocalDateTime;

public class ServiceOrder {
    public enum ServiceStatus {
        CONFIRMED, DIANTAR, SELESAI
    }

    private ServiceItem item;
    private LocalDateTime orderTime;
    private ServiceStatus status;

    public ServiceOrder(ServiceItem item) {
        this.item = item;
        this.orderTime = LocalDateTime.now();
        this.status = ServiceStatus.CONFIRMED;
    }

    public ServiceItem getItem() { return item; }
    public LocalDateTime getOrderTime() { return orderTime; }
    public ServiceStatus getStatus() { return status; }

    public void refreshStatus() {
        LocalDateTime now = LocalDateTime.now();
        if (status == ServiceStatus.CONFIRMED && orderTime.plusMinutes(1).isBefore(now)) {
            status = ServiceStatus.DIANTAR;
        }
        if (status == ServiceStatus.DIANTAR && orderTime.plusMinutes(5).isBefore(now)) {
            status = ServiceStatus.SELESAI;
        }
    }

    @Override
    public String toString() {
        return item.getName() + " (Rp " + item.getPrice() + ") - " + status;
    }
}