package dao;

import model.Pelanggan;
import util.DBConnection;

import java.sql.*;

public class PelangganDAO {

    public int insert(Pelanggan p) {
        String sql = "INSERT INTO sistem.pelanggan(nama_pelanggan,email,no_hp) VALUES (?,?,?) RETURNING id_pelanggan";

        try (Connection c = DBConnection.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, p.getNamaPelanggan());
            ps.setString(2, p.getEmail());
            ps.setString(3, p.getNoHp());

            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
}