package service;

import dao.PromoDAO;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class BookingService {

    private KamarService kamarService = new KamarService();
    private LayananService layananService = new LayananService();
    private PromoDAO promoDAO = new PromoDAO();

    public long hitungMalam(LocalDate masuk, LocalDate keluar) {
        long malam = ChronoUnit.DAYS.between(masuk, keluar);
        return Math.max(malam, 1);
    }

    public double hitungTotalKamar(int idKamar, long malam) {
        double hargaPerMalam = kamarService.getHargaPerMalam(idKamar);
        return hargaPerMalam * malam;
    }

    public double hitungTotalLayanan(int[] layananIds, int[] jumlahs) {
        double total = 0;
        for (int i = 0; i < layananIds.length; i++) {
            total += layananService.hitungSubtotal(layananIds[i], jumlahs[i]);
        }
        return total;
    }

    public double applyPromo(Integer idPromo, double totalSementara) {
        if (idPromo == null) return totalSementara;
        double diskon = promoDAO.getDiskon(idPromo, totalSementara);
        double hasil = totalSementara - diskon;
        return Math.max(hasil, 0);
    }

    public double hitungGrandTotal(int idKamar,
                                   LocalDate masuk,
                                   LocalDate keluar,
                                   int[] layananIds,
                                   int[] jumlahs,
                                   Integer idPromo) {

        long malam = hitungMalam(masuk, keluar);

        double totalKamar = hitungTotalKamar(idKamar, malam);
        double totalLayanan = hitungTotalLayanan(layananIds, jumlahs);

        double sementara = totalKamar + totalLayanan;

        return applyPromo(idPromo, sementara);
    }
}