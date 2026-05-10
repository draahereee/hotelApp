package model;

public class LayananReservasi {
    private int idLayananReservasi;
    private int idReservasi;
    private int idLayanan;
    private int jumlah;
    private double subtotal;

    public int getIdLayananReservasi() { return idLayananReservasi; }
    public void setIdLayananReservasi(int id) { this.idLayananReservasi = id; }

    public int getIdReservasi() { return idReservasi; }
    public void setIdReservasi(int idReservasi) { this.idReservasi = idReservasi; }

    public int getIdLayanan() { return idLayanan; }
    public void setIdLayanan(int idLayanan) { this.idLayanan = idLayanan; }

    public int getJumlah() { return jumlah; }
    public void setJumlah(int jumlah) { this.jumlah = jumlah; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
}