package hotel;
import java.time.LocalDateTime;

public class ServiceOrder {
    public enum OrderStatus {
        DIPROSES,DIANTAR,SELESAI,DIBATALKAN
    }

    public enum ServiceType{SPA,MAKANAN,MINUMAN}

    private int idPesanan;
    private int idReservasi;
    private ServiceType type;
    private String namaLayanan;
    private int harga;
    private int jumlah;
    private int totalHarga;
    private OrderStatus status;
    private LocalDateTime waktuPesan;

    public ServiceOrder(int idPesanan, int idReservasi, ServiceType type, String namaLayanan, int harga, int jumlah, int totalHarga, OrderStatus status, LocalDateTime waktuPesan) {
        this.idPesanan = idPesanan;
        this.idReservasi = idReservasi;
        this.type = type;
        this.namaLayanan = namaLayanan;
        this.harga = harga;
        this.jumlah = jumlah;
        this.totalHarga = totalHarga;
        this.status = status;
        this.waktuPesan = waktuPesan;
    }

    public int getIdPesanan() { return idPesanan; }
    public ServiceType getType() { return type; }
    public String getNamaLayanan() { return namaLayanan; }
    public int getHarga() { return harga; }
    public int getJumlah() { return jumlah; }
    public int getTotalHarga() { return totalHarga; }
    public OrderStatus getStatus() { return status; }
    public LocalDateTime getWaktuPesan() { return waktuPesan; }

    public void refreshStatus() {
        LocalDateTime now = LocalDateTime.now();
        if (status == OrderStatus.DIPROSES && waktuPesan.plusMinutes(1).isBefore(now)) {
            status = OrderStatus.DIANTAR;
        }
        if (status == OrderStatus.DIANTAR && waktuPesan.plusMinutes(5).isBefore(now)) {
            status = OrderStatus.SELESAI;
        }
    }

    @Override
    public String toString() {
        return type + ": " + namaLayanan + " x" + jumlah + " = Rp " + totalHarga + " (" + status + ")";
    }
}