package hotel.model;

public class ServiceItem {
    private int idLayanan;
    private String name;
    private int price;

    public ServiceItem(int idLayanan,String name, int price) {
        this.idLayanan = idLayanan;
        this.name = name;
        this.price = price;
    }

    public int getIdLayanan() {return idLayanan; }
    public String getName()  { return name; }
    public int    getPrice() { return price; }
}
