package hotel.model;

public class Room {
    private int idKamar;          // primary key dari tabel kamar
    private String nomorKamar;    // nomor kamar (VARCHAR di database)
    private String type;          // dari tipe_kamar.nama
    private int pricePerNight;    // dari tipe_kamar.harga
    private String facilities;    // dari tipe_kamar.deskripsi

    public Room(int idKamar, String nomorKamar, String type, int pricePerNight, String facilities) {
        this.idKamar = idKamar;
        this.nomorKamar = nomorKamar;
        this.type = type;
        this.pricePerNight = pricePerNight;
        this.facilities = facilities;
    }

   
    public int getIdKamar() { return idKamar; }
    public String getNomorKamar() { return nomorKamar; }
    public String getType() { return type; }
    public int getPricePerNight() { return pricePerNight; }
    public String getFacilities() { return facilities; }
}
