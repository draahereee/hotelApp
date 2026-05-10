package service;

import dao.UlasanDAO;

public class UlasanService {

    private UlasanDAO dao = new UlasanDAO();

    public void kirim(int idReservasi, int idPelanggan, int idHotel,
                      double rating, String komentar) {

        dao.insert(idReservasi, idPelanggan, idHotel, rating, komentar);
    }
}