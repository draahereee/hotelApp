package model;

import java.sql.Timestamp;

public class Pelanggan {
    private int idPelanggan;
    private String namaPelanggan;
    private String email;
    private String noHp;
    private Timestamp dibuatPada;

    public int getIdPelanggan() { return idPelanggan; }
    public void setIdPelanggan(int idPelanggan) { this.idPelanggan = idPelanggan; }

    public String getNamaPelanggan() { return namaPelanggan; }
    public void setNamaPelanggan(String namaPelanggan) { this.namaPelanggan = namaPelanggan; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNoHp() { return noHp; }
    public void setNoHp(String noHp) { this.noHp = noHp; }

    public Timestamp getDibuatPada() { return dibuatPada; }
    public void setDibuatPada(Timestamp dibuatPada) { this.dibuatPada = dibuatPada; }
}