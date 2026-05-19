package hotel;
import java.time.DayOfWeek;
import java.time.LocalDate;

public class Promo {
    private String code;
    private double discountPercent; 
    private String description;


    private String condition; 

    public Promo(String code, double discountPercent, String description, String condition) {
        this.code = code;
        this.discountPercent = discountPercent;
        this.description = description;
        this.condition = condition;
    }

    public String getCode()            { return code; }
    public double getDiscountPercent() { return discountPercent; }
    public String getDescription()     { return description; }

    public boolean isValidForDate(LocalDate date) {
        if (condition.equals("SUNDAY")) {
            return date.getDayOfWeek() == DayOfWeek.SUNDAY;
        }
        return false; 
    }

    @Override
    public String toString() {
        return "Kode: " + code + " | Diskon: " + (int)(discountPercent*100) + "% | " + description;
    }
}