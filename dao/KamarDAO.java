package dao;

import util.DBConnection;

import java.sql.*;

public class KamarDAO {

    public void showAll() {
        String sql = """
            SELECT k.id_kamar, h.nama_hotel, t.nama, t.harga, k.stok
            FROM sistem.kamar k
            JOIN sistem.hotel h ON k.id_hotel = h.id_hotel
            JOIN sistem.tipe_kamar t ON k.id_tipe = t.id_tipe
            ORDER BY k.id_kamar ASC
        """;

        try (Connection c = DBConnection.connect();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {

            while (rs.next()) {
                System.out.println(
                        "ID:" + rs.getInt("id_kamar") +
                        " | Hotel:" + rs.getString("nama_hotel") +
                        " | Tipe:" + rs.getString("nama") +
                        " | Harga:" + rs.getDouble("harga") +
                        " | Stok:" + rs.getInt("stok")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public double getHarga(int idKamar) {
        String sql = """
            SELECT t.harga
            FROM sistem.kamar k
            JOIN sistem.tipe_kamar t ON k.id_tipe = t.id_tipe
            WHERE k.id_kamar = ?
        """;

        try (Connection c = DBConnection.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, idKamar);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return rs.getDouble(1);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public int getHotelIdByKamar(int idKamar) {
    String sql = "SELECT id_hotel FROM sistem.kamar WHERE id_kamar = ?";

        try (Connection c = DBConnection.connect();
            PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, idKamar);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("id_hotel");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }
}