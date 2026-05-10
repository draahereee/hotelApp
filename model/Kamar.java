package model;

public class Kamar {
    private int idKamar;
    private int idHotel;
    private int idTipe;
    private int stok;

    public int getIdKamar() { return idKamar; }
    public void setIdKamar(int idKamar) { this.idKamar = idKamar; }

    public int getIdHotel() { return idHotel; }
    public void setIdHotel(int idHotel) { this.idHotel = idHotel; }

    public int getIdTipe() { return idTipe; }
    public void setIdTipe(int idTipe) { this.idTipe = idTipe; }

    public int getStok() { return stok; }
    public void setStok(int stok) { this.stok = stok; }
}