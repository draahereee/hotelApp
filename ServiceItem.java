package hotel;

public class ServiceItem {
    private String name;
    private int price;

    public ServiceItem(String name, int price) {
        this.name = name;
        this.price = price;
    }

    public String getName()  { return name; }
    public int    getPrice() { return price; }
}