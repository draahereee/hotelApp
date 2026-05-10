package model;

public class TipeKamar {
    private int idTipe;
    private String nama;
    private double harga;
    private int kapasitas;
    private String deskripsi;

    public int getIdTipe() { return idTipe; }
    public void setIdTipe(int idTipe) { this.idTipe = idTipe; }

    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }

    public double getHarga() { return harga; }
    public void setHarga(double harga) { this.harga = harga; }

    public int getKapasitas() { return kapasitas; }
    public void setKapasitas(int kapasitas) { this.kapasitas = kapasitas; }

    public String getDeskripsi() { return deskripsi; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }
}