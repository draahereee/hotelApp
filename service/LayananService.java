package service;

import dao.LayananDAO;
import dao.LayananReservasiDAO;

public class LayananService {

    private LayananDAO layananDAO = new LayananDAO();
    private LayananReservasiDAO lrDAO = new LayananReservasiDAO();

    public void tampilSemua() {
        layananDAO.showAll();
    }

    public double getHarga(int idLayanan) {
        return layananDAO.getHarga(idLayanan);
    }

    public double hitungSubtotal(int idLayanan, int jumlah) {
        return getHarga(idLayanan) * jumlah;
    }

    public void tambahKeReservasi(int idReservasi, int idLayanan, int jumlah) {
        double subtotal = hitungSubtotal(idLayanan, jumlah);
        lrDAO.insert(idReservasi, idLayanan, jumlah, subtotal);
    }
}