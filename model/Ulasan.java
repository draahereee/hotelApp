package model;

import java.sql.Timestamp;

public class Ulasan {
    private int idUlasan;
    private int idReservasi;
    private int idPelanggan;
    private int idHotel;
    private double rating;
    private String komentar;
    private Timestamp dibuatPada;

    public int getIdUlasan() { return idUlasan; }
    public void setIdUlasan(int idUlasan) { this.idUlasan = idUlasan; }

    public int getIdReservasi() { return idReservasi; }
    public void setIdReservasi(int idReservasi) { this.idReservasi = idReservasi; }

    public int getIdPelanggan() { return idPelanggan; }
    public void setIdPelanggan(int idPelanggan) { this.idPelanggan = idPelanggan; }

    public int getIdHotel() { return idHotel; }
    public void setIdHotel(int idHotel) { this.idHotel = idHotel; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public String getKomentar() { return komentar; }
    public void setKomentar(String komentar) { this.komentar = komentar; }

    public Timestamp getDibuatPada() { return dibuatPada; }
    public void setDibuatPada(Timestamp dibuatPada) { this.dibuatPada = dibuatPada; }
}