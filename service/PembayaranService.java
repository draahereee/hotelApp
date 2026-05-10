package service;

import dao.PembayaranDAO;

public class PembayaranService {

    private PembayaranDAO dao = new PembayaranDAO();

    public void bayar(int idReservasi, double total, String metode) {
        // metode: "cash" / "e_wallet"
        // status: "berhasil"
        dao.insert(idReservasi, total, metode, "berhasil");
    }
}