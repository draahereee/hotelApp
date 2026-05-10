package model;

public class Hotel {
    private int idHotel;
    private String namaHotel;
    private String lokasiHotel;
    private double rating;
    private String deskripsi;

    public int getIdHotel() { return idHotel; }
    public void setIdHotel(int idHotel) { this.idHotel = idHotel; }

    public String getNamaHotel() { return namaHotel; }
    public void setNamaHotel(String namaHotel) { this.namaHotel = namaHotel; }

    public String getLokasiHotel() { return lokasiHotel; }
    public void setLokasiHotel(String lokasiHotel) { this.lokasiHotel = lokasiHotel; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public String getDeskripsi() { return deskripsi; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }
}