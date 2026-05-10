package dao;

import util.DBConnection;
import java.sql.*;

public class LayananReservasiDAO {

    public void insert(int idReservasi, int idLayanan, int jumlah, double subtotal) {

        String sql = """
        INSERT INTO sistem.layanan_reservasi
        (id_reservasi,id_layanan,jumlah,subtotal)
        VALUES (?,?,?,?)
        """;

        try (Connection c = DBConnection.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, idReservasi);
            ps.setInt(2, idLayanan);
            ps.setInt(3, jumlah);
            ps.setDouble(4, subtotal);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}