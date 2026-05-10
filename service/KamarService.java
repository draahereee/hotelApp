package service;

import dao.KamarDAO;

public class KamarService {

    private KamarDAO dao = new KamarDAO();

    public void tampilSemua() {
        dao.showAll();
    }

    public double getHargaPerMalam(int idKamar) {
        return dao.getHarga(idKamar);
    }

    public int getHotelId(int idKamar) {
    return dao.getHotelIdByKamar(idKamar);
    }
}