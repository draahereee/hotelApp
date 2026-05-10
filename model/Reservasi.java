package model;

import java.sql.Date;
import java.sql.Timestamp;

public class Reservasi {
    private int idReservasi;
    private int idPelanggan;
    private int idKamar;
    private Date masukKamar;
    private Date keluarKamar;
    private double hargaTotal;
    private String statusReservasi;
    private Timestamp dibuatPada;
    private Integer idPromo;

    public int getIdReservasi() { return idReservasi; }
    public void setIdReservasi(int idReservasi) { this.idReservasi = idReservasi; }

    public int getIdPelanggan() { return idPelanggan; }
    public void setIdPelanggan(int idPelanggan) { this.idPelanggan = idPelanggan; }

    public int getIdKamar() { return idKamar; }
    public void setIdKamar(int idKamar) { this.idKamar = idKamar; }

    public Date getMasukKamar() { return masukKamar; }
    public void setMasukKamar(Date masukKamar) { this.masukKamar = masukKamar; }

    public Date getKeluarKamar() { return keluarKamar; }
    public void setKeluarKamar(Date keluarKamar) { this.keluarKamar = keluarKamar; }

    public double getHargaTotal() { return hargaTotal; }
    public void setHargaTotal(double hargaTotal) { this.hargaTotal = hargaTotal; }

    public String getStatusReservasi() { return statusReservasi; }
    public void setStatusReservasi(String statusReservasi) { this.statusReservasi = statusReservasi; }

    public Timestamp getDibuatPada() { return dibuatPada; }
    public void setDibuatPada(Timestamp dibuatPada) { this.dibuatPada = dibuatPada; }

    public Integer getIdPromo() { return idPromo; }
    public void setIdPromo(Integer idPromo) { this.idPromo = idPromo; }
}