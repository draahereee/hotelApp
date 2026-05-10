package service;

import dao.ReservasiDAO;
import model.Reservasi;

import java.sql.Date;

public class ReservasiService {

    private ReservasiDAO dao = new ReservasiDAO();

    public int buat(int idPelanggan, int idKamar,
                    Date masuk, Date keluar,
                    double hargaTotal,
                    String statusReservasi,
                    Integer idPromo) {

        Reservasi r = new Reservasi();
        r.setIdPelanggan(idPelanggan);
        r.setIdKamar(idKamar);
        r.setMasukKamar(masuk);
        r.setKeluarKamar(keluar);
        r.setHargaTotal(hargaTotal);
        r.setStatusReservasi(statusReservasi); 
        r.setIdPromo(idPromo);

        return dao.insert(r);
    }
}