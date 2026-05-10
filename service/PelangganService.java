package service;

import dao.PelangganDAO;
import model.Pelanggan;

public class PelangganService {

    private PelangganDAO dao = new PelangganDAO();

    public int daftar(String nama, String email, String noHp) {
        Pelanggan p = new Pelanggan();
        p.setNamaPelanggan(nama);
        p.setEmail(email);
        p.setNoHp(noHp);
        return dao.insert(p);
    }
}