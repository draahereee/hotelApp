package hotel.model;
import java.util.ArrayList;
import java.util.List;

public class Hotel {
    private int idHotel;         
    private String name;
    private String location;      
    private String starRating;    
    private String deskripsi;     
    private List<Room> rooms;

    public Hotel(int idHotel, String name, String location, String starRating, String deskripsi, List<Room> rooms) {
        this.idHotel = idHotel;
        this.name = name;
        this.location = location;
        this.starRating = starRating;
        this.deskripsi = deskripsi;
        this.rooms = new ArrayList<>(rooms);   // defensive copy
    }

    public int getIdHotel() { return idHotel; }
    public String getName() { return name; }
    public String getLocation() { return location; }
    public String getStarRating() { return starRating; }
    public String getDeskripsi() { return deskripsi; }
    public List<Room> getRooms() { return rooms; }

    public Room getRoom(int index) {
        if (index > 0 && index <= rooms.size())
            return rooms.get(index - 1);
        return null;
    }
}
