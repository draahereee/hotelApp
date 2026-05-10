package dao;

import util.DBConnection;
import java.sql.*;

public class HotelDAO {

    public void show(){
        try(Connection c=DBConnection.connect();
            Statement s=c.createStatement();
            ResultSet rs=s.executeQuery("SELECT * FROM sistem.hotel")){

            while(rs.next()){
                System.out.println("ID:"+rs.getInt("id_hotel")+" | "+rs.getString("nama"));
            }

        }catch(Exception e){e.printStackTrace();}
    }
}