package dao;

import util.DBConnection;
import java.sql.*;

public class PromoDAO {

    public double getDiskon(int idPromo, double total) {

        String sql = "SELECT tipe_diskon, nilai_diskon, maks_diskon FROM sistem.promo WHERE id_promo=?";

        try (Connection c = DBConnection.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, idPromo);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String tipe = rs.getString("tipe_diskon");
                double nilai = rs.getDouble("nilai_diskon");
                double maks = rs.getDouble("maks_diskon");

                double diskon = 0;

                if (tipe.equals("persentase")) {
                    diskon = total * nilai / 100;
                    if (maks > 0 && diskon > maks) diskon = maks;
                } else {
                    diskon = nilai;
                }

                return diskon;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }
}