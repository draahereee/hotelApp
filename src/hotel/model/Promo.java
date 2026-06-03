package hotel.model;
import java.time.LocalDate;

public class Promo {
    private int idPromo;
    private String code;
    private String deskripsi;
    private double discountValue; 
    private LocalDate startDate; 
    private LocalDate endDate;

    public Promo(int idPromo, String code, double discountPercent, String descripsi, LocalDate startDate, LocalDate endDate) {
        this.idPromo = idPromo;
        this.code = code;
        this.discountValue = discountPercent;
        this.deskripsi = descripsi;
        this.startDate = startDate;
        this.endDate = endDate;
    }


    public int getIdPromo(){return idPromo; }
    public String getCode()            { return code; }
    public double getDiscountPercent() {return discountValue / 100.0;}
    public double getDiscountValue() { return discountValue; }
    public String getDescription()     { return deskripsi; }
    public LocalDate getStartDate()    { return startDate; }
    public LocalDate getValidUntil()   { return endDate; }
    public LocalDate getEndDate()      { return endDate; }

    public boolean isValidForDate(LocalDate date) {
        return (date.isEqual(startDate) || date.isAfter(startDate)) &&
               (date.isEqual(endDate) || date.isBefore(endDate));
    }

    @Override
    public String toString() {
        return "Kode: " + code + " | Diskon: " + (int)(discountValue) + "% | " + deskripsi + " | Berlaku: " + startDate + " s/d " + endDate;
    }
}
