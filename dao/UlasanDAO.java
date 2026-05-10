package dao;

import util.DBConnection;
import java.sql.*;

public class UlasanDAO {

    public void insert(int idReservasi, int idPelanggan, int idHotel, double rating, String komentar) {

        String sql = """
        INSERT INTO sistem.ulasan
        (id_reservasi,id_pelanggan,id_hotel,rating,komentar)
        VALUES (?,?,?,?,?)
        """;

        try (Connection c = DBConnection.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, idReservasi);
            ps.setInt(2, idPelanggan);
            ps.setInt(3, idHotel);
            ps.setDouble(4, rating);
            ps.setString(5, komentar);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}