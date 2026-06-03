package hotel.ui;

import hotel.model.Hotel;
import hotel.model.Promo;
import hotel.model.Room;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
public class UIFormatter {
    
    // ===== ANSI COLOR CODES =====
    public static final String RESET = "\u001B[0m";
    public static final String BOLD = "\u001B[1m";
    
    // Foreground Colors
    public static final String BLACK = "\u001B[30m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String MAGENTA = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";
    
    // Bright Colors
    public static final String BRIGHT_BLACK = "\u001B[90m";
    public static final String BRIGHT_RED = "\u001B[91m";
    public static final String BRIGHT_GREEN = "\u001B[92m";
    public static final String BRIGHT_YELLOW = "\u001B[93m";
    public static final String BRIGHT_BLUE = "\u001B[94m";
    public static final String BRIGHT_MAGENTA = "\u001B[95m";
    public static final String BRIGHT_CYAN = "\u001B[96m";
    public static final String BRIGHT_WHITE = "\u001B[97m";
    
    // ===== UNICODE SYMBOLS =====
    public static final String STAR_FULL = "★";
    public static final String STAR_EMPTY = "☆";
    public static final String CHECK = "✓";
    public static final String CROSS = "✗";
    public static final String ARROW_RIGHT = "→";
    public static final String BULLET = "●";
    
    /**
     * Generate star rating display
     * @param rating Dari "Bintang X.X" atau double value
     * @return Stars with unicode symbols
     */
    public static String formatStars(String starRating) {
        if (starRating == null || starRating.isEmpty()) return "";
        
        // Ekstrak angka dari "Bintang 4.5" format
        String[] parts = starRating.split(" ");
        // Jika tidak bisa di-split dengan spasi, ambil teks aslinya
        String numericPart = (parts.length >= 2) ? parts[1] : parts[0];
        
        try {
            // Validasi apakah teks tersebut adalah angka desimal yang sah
            double rating = Double.parseDouble(numericPart);
            
            // Kembalikan angka murni saja dengan format 1 angka di belakang koma (misal: "4.5")
            return String.format("%.1f", rating);
            
            // Catatan Opsional: Kalau lu tetep pengen angkanya berwarna kuning di terminal, 
            // lu bisa ganti baris return di atas pakai kode di bawah ini:
            // return BRIGHT_YELLOW + String.format("%.1f", rating) + RESET;

        } catch (NumberFormatException e) {
            // Jika gagal parse, bersihkan kata "Bintang" atau spasi yang tersisa
            return numericPart.replace("Bintang", "").trim();
        }
    }
    
    /**
     * Print separator line
     */
    public static void printSeparator(int width) {
        for (int i = 0; i < width; i++) {
            System.out.print("─");
        }
        System.out.println();
    }
    
    /**
     * Print double separator line
     */
    public static void printDoubleSeparator(int width) {
        for (int i = 0; i < width; i++) {
            System.out.print("═");
        }
        System.out.println();
    }
    
    /**
     * Display table header untuk hotel listing
     */
    public static void printHotelTableHeader() {
        System.out.println(BOLD + BLUE + "┌─────┬──────────────────────┬─────────┬────────────────────────┐" + RESET);
        System.out.println(BOLD + BLUE + "│ No  │ Nama Hotel           │ Rating  │ Deskripsi (singkat)    │" + RESET);
        System.out.println(BOLD + BLUE + "├─────┼──────────────────────┼─────────┼────────────────────────┤" + RESET);
    }
    
    /**
     * Parse dan display hotel description dengan format yang bagus
     * Format dari DB: "Alamat: Jl. XXX | Telp: (0XX) XXX | Deskripsi detail..."
     */
    public static void printHotelDescription(String fullDescription) {
        if (fullDescription == null || fullDescription.isEmpty()) return;
        
        // Parse deskripsi dari database format
        String[] parts = fullDescription.split("\\|");
        
        System.out.println(BRIGHT_BLACK + "┌" + "─".repeat(78) + "┐" + RESET);
        
        for (String part : parts) {
            String trimmed = part.trim();
            if (trimmed.isEmpty()) continue;
            
            if (trimmed.startsWith("Alamat:")) {
                System.out.println(BRIGHT_BLACK + "│ " + RESET + BLUE + trimmed + RESET + 
                    BRIGHT_BLACK + " ".repeat(Math.max(1, 76 - trimmed.length())) + "│" + RESET);
            } else if (trimmed.startsWith("Telp:")) {
                System.out.println(BRIGHT_BLACK + "│ " + RESET + CYAN + trimmed + RESET + 
                    BRIGHT_BLACK + " ".repeat(Math.max(1, 76 - trimmed.length())) + "│" + RESET);
            } else {
                // Deskripsi - wrap text jika terlalu panjang
                if (trimmed.length() > 76) {
                    printWrappedText(trimmed, 76);
                } else {
                    System.out.println(BRIGHT_BLACK + "│ " + RESET + trimmed + 
                        BRIGHT_BLACK + " ".repeat(Math.max(1, 76 - trimmed.length())) + "│" + RESET);
                }
            }
        }
        
        System.out.println(BRIGHT_BLACK + "└" + "─".repeat(78) + "┘" + RESET);
    }
    
    /**
     * Print wrapped text dengan width constraint
     */
    private static void printWrappedText(String text, int width) {
        int index = 0;
        while (index < text.length()) {
            int endIndex = Math.min(index + width, text.length());
            if (endIndex < text.length() && text.charAt(endIndex) != ' ') {
                endIndex = text.lastIndexOf(' ', endIndex);
                if (endIndex <= index) {
                    endIndex = Math.min(index + width, text.length());
                }
            }
            
            String line = text.substring(index, endIndex).trim();
            System.out.println(BRIGHT_BLACK + "│ " + RESET + line + 
                BRIGHT_BLACK + " ".repeat(Math.max(1, 76 - line.length())) + "│" + RESET);
            index = endIndex;
        }
    }
    
    
    /**
     * Display hotel row dalam table format (singkat untuk list)
     */
    public static void printHotelRow(int no, Hotel hotel) {
        String stars = formatStars(hotel.getStarRating());
        String desc = hotel.getDeskripsi() != null ? hotel.getDeskripsi() : "";
        if (desc.length() > 22) desc = desc.substring(0, 19) + "...";
        
        String nameCol = String.format("%-20s", hotel.getName());
        String descCol = String.format("%-22s", desc);
        
        System.out.printf("│ %2d  │ %s │ %s │ %s │\n", 
            no, nameCol, stars, descCol);
    }
    
    /**
     * Display table footer
     */
    public static void printHotelTableFooter() {
        System.out.println(BOLD + BLUE + "└─────┴──────────────────────┴─────────┴────────────────────────┘" + RESET);
    }
    
    /**
     * Display room card dengan formatting
     */
    public static void printRoomCard(int no, Room room, Promo promo) {
        int originalPrice = room.getPricePerNight();
        int discountedPrice = originalPrice;
        boolean hasDiscount = false;
        
        if (promo != null) {
            int discount = (int) (originalPrice * promo.getDiscountPercent());
            discountedPrice = originalPrice - discount;
            hasDiscount = true;
        }
        
        String statusColor = GREEN;
        String status = "TERSEDIA";
        
        System.out.println(BLUE + "┌─ [" + no + "] " + room.getType().toUpperCase() + " " + "─".repeat(Math.max(1, 40 - room.getType().length())) + "┐" + RESET);
        System.out.println(BLUE + "│" + RESET + " Harga : Rp " + 
            (hasDiscount ? RED + "ROMBAK " + originalPrice + RESET + " → " + GREEN + discountedPrice : GREEN + originalPrice) + RESET + "/malam");
        System.out.println(BLUE + "│" + RESET + " Fasilitas : " + room.getFacilities());
        System.out.println(BLUE + "│" + RESET + " Status : " + statusColor + status + RESET);
        if (hasDiscount) {
            System.out.println(BLUE + "│" + RESET + " " + BRIGHT_YELLOW + "🏷️ PROMO AKTIF: " + promo.getCode() + RESET);
        }
        System.out.println(BLUE + "└" + "─".repeat(50) + "┘" + RESET);
    }
    
    /**
     * Display promo card dengan formatting
     */
    public static void printPromoCard(Promo promo) {
    long daysUntilExpire = ChronoUnit.DAYS.between(LocalDate.now(), promo.getValidUntil());
    String statusColor = RESET;
    String urgentText = "";
    
    if (daysUntilExpire <= 0) {
        statusColor = RED;
        urgentText = " [EXPIRED]";
    } else if (daysUntilExpire <= 3) {
        statusColor = BRIGHT_RED;
        urgentText = " [SEGERA BERAKHIR - " + daysUntilExpire + " hari]";
    }
    
    // Konsep Lebar Tetap: Lebar teks di dalam kartu disetel mutlak 65 karakter
    int contentWidth = 65;
    
    // 1. Bingkai Atas (+ ditambah 65 isi + 2 spasi padding kiri kanan)
    System.out.println(BRIGHT_YELLOW + BOLD + "+" + "-".repeat(contentWidth + 2) + "+" + RESET);
    
    // 2. Baris Kode Promo
    System.out.println(BRIGHT_YELLOW + "| " + BOLD + String.format("%-" + contentWidth + "s", promo.getCode()) + BRIGHT_YELLOW + " |" + RESET);
    
    // 3. Garis Pembatas Tengah
    System.out.println(BRIGHT_YELLOW + "|" + "-".repeat(contentWidth + 2) + "|" + RESET);
    
    // 4. Baris Diskon (Dihitung dinamis berdasarkan panjang teks murni tanpa kode warna)
    String rawDiscount = String.format("Diskon   : %.0f%% untuk pemesanan", promo.getDiscountPercent() * 100);
    int spacesDiscount = contentWidth - rawDiscount.length();
    System.out.println(BRIGHT_YELLOW + "| " + RESET + "Diskon   : " + GREEN + BOLD + String.format("%.0f%%", promo.getDiscountPercent() * 100) + RESET + " untuk pemesanan" + " ".repeat(Math.max(0, spacesDiscount)) + BRIGHT_YELLOW + " |" + RESET);
    
    // 5. Baris Masa Berlaku (Dihitung dinamis karena urgentText panjangnya berubah-ubah)
    String rawValidity = "Berlaku  : " + LocalDate.now() + " s/d " + promo.getValidUntil() + urgentText;
    int spacesValidity = contentWidth - rawValidity.length();
    System.out.println(BRIGHT_YELLOW + "| " + RESET + "Berlaku  : " + LocalDate.now() + " s/d " + promo.getValidUntil() + statusColor + urgentText + RESET + " ".repeat(Math.max(0, spacesValidity)) + BRIGHT_YELLOW + " |" + RESET);
    
    // 6. Baris Deskripsi ("Deskripsi: " memakan 11 karakter, sisa padding untuk teks adalah 65 - 11 = 54)
    System.out.println(BRIGHT_YELLOW + "| " + RESET + "Deskripsi: " + String.format("%-54s", promo.getDescription()) + BRIGHT_YELLOW + " |" + RESET);
    
    // 7. Bingkai Bawah
    System.out.println(BRIGHT_YELLOW + BOLD + "+" + "-".repeat(contentWidth + 2) + "+" + RESET);
    }
    /**
     * Display login rules dialog
     */
    public static void printLoginRulesDialog() {
        String[] lines = {
            "╔════════════════════════════════════════════════════════════════╗",
            "║                  ATURAN LAYANAN HOTELKU                        ║",
            "╠════════════════════════════════════════════════════════════════╣",
            "║                                                                ║",
            "║  " + BOLD + "ATURAN CHECK-IN:" + RESET + "                     ║",
            "║  • Anda wajib check-in sebelum pukul 14:00 di hari check-in    ║",
            "║  • Jika terlambat, status pemesanan otomatis menjadi SELESAI   ║",
            "║  • Tidak ada refund untuk keterlambatan check-in               ║",
            "║                                                                ║",
            "║  " + BOLD + "ATURAN REFUND:" + RESET + "                       ║",  
            "║  • Refund hanya diberikan untuk pembatalan sebelum check-in    ║",
            "║                                                                ║",
            "╠════════════════════════════════════════════════════════════════╣",
        };
        
        for (String line : lines) {
            System.out.println(line);
        }
    }
    
    /**
     * Colored status text
     */
    public static String colorizeStatus(String status) {
        if (status == null) return RESET + "UNKNOWN" + RESET;
        
        switch (status.toLowerCase()) {
            case "tersedia":
            case "available":
                return GREEN + "✓ TERSEDIA" + RESET;
            case "habis":
            case "unavailable":
                return RED + "✗ HABIS" + RESET;
            case "dikonfirmasi":
            case "confirmed":
                return CYAN + "⊘ DIKONFIRMASI" + RESET;
            case "check_in":
            case "checked_in":
                return BRIGHT_CYAN + "⊕ CHECK-IN" + RESET;
            case "check_out":
            case "checked_out":
                return BRIGHT_BLUE + "⊗ CHECK-OUT" + RESET;
            case "dibatalkan":
            case "refunded":
                return BRIGHT_RED + "⊗ DIBATALKAN" + RESET;
            case "diproses":
            case "processing":
                return YELLOW + "⟳ DIPROSES" + RESET;
            case "diantar":
            case "delivered":
                return BRIGHT_GREEN + "⊙ DIANTAR" + RESET;
            case "selesai":
            case "completed":
                return BRIGHT_GREEN + "✓ SELESAI" + RESET;
            default:
                return RESET + status + RESET;
        }
    }
    
    /**
     * Format price with thousand separator
     */
    public static String formatPrice(int price) {
        return String.format("%,d", price).replace(",", ".");
    }
    
    /**
     * Create a centered text in a line
     */
    public static String centerText(String text, int width) {
        if (text.length() >= width) return text;
        int totalPadding = width - text.length();
        int leftPadding = totalPadding / 2;
        int rightPadding = totalPadding - leftPadding;
        return " ".repeat(leftPadding) + text + " ".repeat(rightPadding);
    }
}
