package dao;

import util.DBConnection;
import java.sql.*;

public class PembayaranDAO {

    public void insert(int idReservasi, double total, String metode, String status) {

        String sql = """
        INSERT INTO sistem.pembayaran
        (id_reservasi,uang_dibayarkan,metode_pembayaran,status_pembayaran)
        VALUES (?,?,?::sistem.cara_bayar,?::sistem.status_bayar)
        """;

        try (Connection c = DBConnection.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, idReservasi);
            ps.setDouble(2, total);
            ps.setString(3, metode);   // "cash" / "e_wallet"
            ps.setString(4, status);   // "berhasil"

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}