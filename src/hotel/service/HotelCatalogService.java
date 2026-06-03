package hotel.service;

import hotel.database.DatabaseHelper;
import hotel.model.Hotel;
import hotel.model.Promo;
import hotel.model.Room;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class HotelCatalogService {
    public List<Hotel> loadHotels() {
        return DatabaseHelper.loadAllHotels();
    }

    public List<Promo> loadPromos() {
        return DatabaseHelper.loadAllPromos();
    }

    public Set<String> getAvailableCities(List<Hotel> hotels) {
        return hotels.stream()
                .map(Hotel::getLocation)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public List<Hotel> findHotelsByCity(List<Hotel> hotels, String city) {
        return hotels.stream()
                .filter(h -> h.getLocation().equalsIgnoreCase(city))
                .collect(Collectors.toList());
    }

    public List<Room> sortRooms(List<Room> rooms, String sortInput) {
        List<Room> sortedRooms = new ArrayList<>(rooms);
        if (sortInput.equals("2")) {
            sortedRooms.sort((a, b) -> Integer.compare(a.getPricePerNight(), b.getPricePerNight()));
        } else if (sortInput.equals("3")) {
            sortedRooms.sort((a, b) -> Integer.compare(b.getPricePerNight(), a.getPricePerNight()));
        }
        return sortedRooms;
    }

    public Promo findValidPromo(List<Promo> promos, String code, LocalDate checkIn) {
        for (Promo promo : promos) {
            if (promo.getCode().equalsIgnoreCase(code) && promo.isValidForDate(checkIn)) {
                return promo;
            }
        }
        return null;
    }
}
