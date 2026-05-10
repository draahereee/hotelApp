package dao;

import model.Reservasi;
import util.DBConnection;

import java.sql.*;

public class ReservasiDAO {

    public int insert(Reservasi r) {

        String sql = """
        INSERT INTO sistem.reservasi
        (id_pelanggan,id_kamar,masuk_kamar,keluar_kamar,harga_total,status_reservasi,id_promo)
        VALUES (?,?,?,?,?,?::sistem.status_pemesanan,?)
        RETURNING id_reservasi
        """;

        try (Connection c = DBConnection.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, r.getIdPelanggan());
            ps.setInt(2, r.getIdKamar());
            ps.setDate(3, r.getMasukKamar());
            ps.setDate(4, r.getKeluarKamar());
            ps.setDouble(5, r.getHargaTotal());
            ps.setString(6, r.getStatusReservasi());

            if (r.getIdPromo() == null)
                ps.setNull(7, Types.INTEGER);
            else
                ps.setInt(7, r.getIdPromo());

            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }
}