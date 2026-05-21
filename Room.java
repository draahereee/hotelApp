package hotel;

public class Room {
    private int idKamar;          // primary key dari tabel kamar (untuk update stok)
    private int nomorKamar;
    private String type;          // dari tipe_kamar.nama
    private int pricePerNight;    // dari tipe_kamar.harga
    private String facilities;    // dari tipe_kamar.deskripsi
    private int stock;            // dari kamar.stok

    public Room(int idKamar, int nomorKamar ,String type, int pricePerNight, String facilities, int stock) {
        this.idKamar = idKamar;
        this.nomorKamar = nomorKamar;
        this.type = type;
        this.pricePerNight = pricePerNight;
        this.facilities = facilities;
        this.stock = stock;
    }

    // getter & setter
    public int getIdKamar() { return idKamar; }
    public int getNomorKamar() {return nomorKamar;}
    public String getType() { return type; }
    public int getPricePerNight() { return pricePerNight; }
    public String getFacilities() { return facilities; }
    public int getStock() { return stock; }

    public void decreaseStock() { this.stock--; }
    public void increaseStock() { this.stock++; }

    public void setStock(int stock) { this.stock = stock; }
}