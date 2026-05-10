package model;

import java.sql.Date;
import java.sql.Timestamp;

public class Promo {
    private int idPromo;
    private String kodePromo;
    private String deskripsi;
    private String tipeDiskon;
    private double nilaiDiskon;
    private double minPemesanan;
    private Double maksDiskon;
    private Date berlakuDari;
    private Date berlakuHingga;
    private Timestamp dibuatPada;

    public int getIdPromo() { return idPromo; }
    public void setIdPromo(int idPromo) { this.idPromo = idPromo; }

    public String getKodePromo() { return kodePromo; }
    public void setKodePromo(String kodePromo) { this.kodePromo = kodePromo; }

    public String getDeskripsi() { return deskripsi; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }

    public String getTipeDiskon() { return tipeDiskon; }
    public void setTipeDiskon(String tipeDiskon) { this.tipeDiskon = tipeDiskon; }

    public double getNilaiDiskon() { return nilaiDiskon; }
    public void setNilaiDiskon(double nilaiDiskon) { this.nilaiDiskon = nilaiDiskon; }

    public double getMinPemesanan() { return minPemesanan; }
    public void setMinPemesanan(double minPemesanan) { this.minPemesanan = minPemesanan; }

    public Double getMaksDiskon() { return maksDiskon; }
    public void setMaksDiskon(Double maksDiskon) { this.maksDiskon = maksDiskon; }

    public Date getBerlakuDari() { return berlakuDari; }
    public void setBerlakuDari(Date berlakuDari) { this.berlakuDari = berlakuDari; }

    public Date getBerlakuHingga() { return berlakuHingga; }
    public void setBerlakuHingga(Date berlakuHingga) { this.berlakuHingga = berlakuHingga; }

    public Timestamp getDibuatPada() { return dibuatPada; }
    public void setDibuatPada(Timestamp dibuatPada) { this.dibuatPada = dibuatPada; }
}