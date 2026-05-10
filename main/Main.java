package main;

import service.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        try (Scanner in = new Scanner(System.in)) {

            PelangganService pelangganService = new PelangganService();
            KamarService kamarService = new KamarService();
            ReservasiService reservasiService = new ReservasiService();
            LayananService layananService = new LayananService();
            PembayaranService pembayaranService = new PembayaranService();
            UlasanService ulasanService = new UlasanService();
            BookingService bookingService = new BookingService();

            System.out.println("===== HOTEL BOOKING SYSTEM =====");

            // 1) Registrasi
            System.out.println("\n-- Registrasi Pelanggan --");
            System.out.print("Nama: ");
            String nama = in.nextLine();
            System.out.print("Email: ");
            String email = in.nextLine();
            System.out.print("No HP: ");
            String hp = in.nextLine();

            int idPelanggan = pelangganService.daftar(nama, email, hp);
            if (idPelanggan == -1) {
                System.out.println("Gagal daftar.");
                return;
            }
            System.out.println("ID Pelanggan: " + idPelanggan);

            // 2) Pilih kamar
            System.out.println("\n-- Daftar Kamar --");
            kamarService.tampilSemua();
            System.out.print("Pilih ID Kamar: ");
            int idKamar = in.nextInt();
            in.nextLine();

            // 3) Tanggal
            System.out.print("Tanggal Masuk (YYYY-MM-DD): ");
            LocalDate masuk = LocalDate.parse(in.nextLine());
            System.out.print("Tanggal Keluar (YYYY-MM-DD): ");
            LocalDate keluar = LocalDate.parse(in.nextLine());

            // 4) Layanan
            System.out.println("\n-- Layanan Tambahan --");
            layananService.tampilSemua();
            System.out.print("Jumlah jenis layanan dipilih: ");
            int n = in.nextInt();

            int[] layananIds = new int[n];
            int[] jumlahs = new int[n];

            for (int i = 0; i < n; i++) {
                System.out.print("ID layanan ke-" + (i + 1) + ": ");
                layananIds[i] = in.nextInt();
                System.out.print("Jumlah: ");
                jumlahs[i] = in.nextInt();
            }
            in.nextLine();

            // 5) Promo
            System.out.print("Gunakan promo? (y/n): ");
            String usePromo = in.nextLine();
            Integer idPromo = null;
            if (usePromo.equalsIgnoreCase("y")) {
                System.out.print("Masukkan ID Promo: ");
                idPromo = in.nextInt();
                in.nextLine();
            }

            // 6) Hitung total
            double grandTotal = bookingService.hitungGrandTotal(
                    idKamar, masuk, keluar, layananIds, jumlahs, idPromo
            );

            System.out.println("\nRingkasan:");
            System.out.println("Layanan IDs: " + Arrays.toString(layananIds));
            System.out.println("Jumlah: " + Arrays.toString(jumlahs));
            System.out.println("Grand Total: " + grandTotal);

            // 7) Simpan reservasi (status enum: menunggu)
            int idReservasi = reservasiService.buat(
                    idPelanggan,
                    idKamar,
                    Date.valueOf(masuk),
                    Date.valueOf(keluar),
                    grandTotal,
                    "menunggu",
                    idPromo
            );

            if (idReservasi == -1) {
                System.out.println("Gagal membuat reservasi.");
                return;
            }
            System.out.println("ID Reservasi: " + idReservasi);

            // 8) Simpan layanan_reservasi (subtotal per item)
            for (int i = 0; i < layananIds.length; i++) {
                layananService.tambahKeReservasi(idReservasi, layananIds[i], jumlahs[i]);
            }

            // 9) Pembayaran
            System.out.print("\nMetode bayar (cash/e_wallet): ");
            String metode = in.nextLine();
            pembayaranService.bayar(idReservasi, grandTotal, metode);
            System.out.println("Pembayaran tersimpan (status: berhasil).");

            // 10) Ulasan
            System.out.println("\n-- Ulasan --");
            int idHotel = kamarService.getHotelId(idKamar);
            System.out.print("Rating (1.0 - 5.0): ");
            double rating = in.nextDouble();
            in.nextLine();
            System.out.print("Komentar: ");
            String komentar = in.nextLine();

            ulasanService.kirim(idReservasi, idPelanggan, idHotel, rating, komentar);

            System.out.println("\n===== SELESAI =====");
        }
    }
}