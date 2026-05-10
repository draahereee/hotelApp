package model;

import java.sql.Timestamp;

public class Pembayaran {
    private int idPembayaran;
    private int idReservasi;
    private Timestamp tanggalPembayaran;
    private double uangDibayarkan;
    private String metodePembayaran;
    private String statusPembayaran;

    public int getIdPembayaran() { return idPembayaran; }
    public void setIdPembayaran(int idPembayaran) { this.idPembayaran = idPembayaran; }

    public int getIdReservasi() { return idReservasi; }
    public void setIdReservasi(int idReservasi) { this.idReservasi = idReservasi; }

    public Timestamp getTanggalPembayaran() { return tanggalPembayaran; }
    public void setTanggalPembayaran(Timestamp t) { this.tanggalPembayaran = t; }

    public double getUangDibayarkan() { return uangDibayarkan; }
    public void setUangDibayarkan(double uangDibayarkan) { this.uangDibayarkan = uangDibayarkan; }

    public String getMetodePembayaran() { return metodePembayaran; }
    public void setMetodePembayaran(String metodePembayaran) { this.metodePembayaran = metodePembayaran; }

    public String getStatusPembayaran() { return statusPembayaran; }
    public void setStatusPembayaran(String statusPembayaran) { this.statusPembayaran = statusPembayaran; }
}