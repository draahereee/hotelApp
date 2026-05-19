package hotel;
import java.util.ArrayList;
import java.util.List;

public class Hotel {
    private String name;
    private String location;
    private String starRating;
    private List<Room> rooms;

    public Hotel(String name, String location, String starRating, List<Room> rooms) {
        this.name = name;
        this.location = location;
        this.starRating = starRating;
        this.rooms = new ArrayList<>(rooms); 
    }

    public String getName()      { return name; }
    public String getLocation()  { return location; }
    public String getStarRating(){ return starRating; }
    public List<Room> getRooms() { return rooms; }

    public Room getRoom(int index) {
        if (index > 0 && index <= rooms.size()) return rooms.get(index-1);
        return null;
    }
}