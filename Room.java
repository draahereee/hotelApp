package hotel;

public class Room {
    private String type;
    private int pricePerNight;
    private String facilities;
    private int stock;      

    public Room(String type, int pricePerNight, String facilities, int stock) {
        this.type = type;
        this.pricePerNight = pricePerNight;
        this.facilities = facilities;
        this.stock = stock;
    }

    public String getType()         { return type; }
    public int    getPricePerNight(){ return pricePerNight; }
    public String getFacilities()   { return facilities; }
    public int    getStock()        { return stock; }

    public void decreaseStock() { if (stock > 0) stock--; }
    public void increaseStock() { stock++; }
}