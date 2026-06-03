package hotel.service;

import hotel.database.DatabaseHelper;
import hotel.model.ServiceItem;
import hotel.model.ServiceOrder;

import java.util.Collections;
import java.util.List;

public class AdditionalServiceService {
    public List<ServiceItem> loadMenu(int category, int hotelId) {
        if (category == 1) {
            return DatabaseHelper.loadSpaMenu(hotelId);
        }
        if (category == 2) {
            return DatabaseHelper.loadMakananMenu();
        }
        if (category == 3) {
            return DatabaseHelper.loadMinumanMenu();
        }
        return Collections.emptyList();
    }

    public String getCategoryName(int category) {
        if (category == 1) return "Spa";
        if (category == 2) return "Makanan";
        if (category == 3) return "Minuman";
        return "";
    }

    public ServiceOrder.ServiceType getServiceType(int category) {
        if (category == 1) return ServiceOrder.ServiceType.SPA;
        if (category == 2) return ServiceOrder.ServiceType.MAKANAN;
        return ServiceOrder.ServiceType.MINUMAN;
    }

    public boolean order(int category, int reservasiId, int itemId, int jumlah, int hargaSatuan) {
        if (category == 1) {
            return DatabaseHelper.orderSpa(reservasiId, itemId, jumlah, hargaSatuan);
        }
        if (category == 2) {
            return DatabaseHelper.orderMakanan(reservasiId, itemId, jumlah, hargaSatuan);
        }
        if (category == 3) {
            return DatabaseHelper.orderMinuman(reservasiId, itemId, jumlah, hargaSatuan);
        }
        return false;
    }
}
