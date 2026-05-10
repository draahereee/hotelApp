package dao;

import util.DBConnection;
import java.sql.*;

public class LayananDAO {

    public void showAll() {
        try (Connection c = DBConnection.connect();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery("SELECT * FROM sistem.layanan")) {

            while (rs.next()) {
                System.out.println(
                        "ID:" + rs.getInt("id_layanan") +
                        " | " + rs.getString("nama_layanan") +
                        " | Harga:" + rs.getDouble("harga")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public double getHarga(int id) {
        try (Connection c = DBConnection.connect();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT harga FROM sistem.layanan WHERE id_layanan=?")) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return rs.getDouble(1);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }
}