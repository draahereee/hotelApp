package dao;

import util.DBConnection;
import java.sql.*;

public class TipeKamarDAO {

    public void show(){
        try(Connection c=DBConnection.connect();
            Statement s=c.createStatement();
            ResultSet rs=s.executeQuery("SELECT * FROM sistem.tipe_kamar")){

            while(rs.next()){
                System.out.println(rs.getInt(1)+" | "+rs.getString(2)+" | "+rs.getDouble(3));
            }

        }catch(Exception e){e.printStackTrace();}
    }
}