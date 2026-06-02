CREATE DATABASE hotelApp;

CREATE SCHEMA sistem;

CREATE TABLE sistem.akun_pelanggan (
    id_akun SERIAL PRIMARY KEY,
    username VARCHAR(30) UNIQUE NOT NULL,
    email VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    dibuat_pada TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE sistem.pelanggan (
    id_pelanggan SERIAL PRIMARY KEY,
    id_akun INT UNIQUE NOT NULL,
    nama_pelanggan VARCHAR(50) NOT NULL,
    no_hp VARCHAR(20) UNIQUE NOT NULL,
    alamat TEXT,
    jenis_kelamin VARCHAR(20),
    dibuat_pada TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_akun)
    REFERENCES sistem.akun_pelanggan(id_akun)
    ON DELETE CASCADE
);

CREATE TABLE sistem.hotel (
    id_hotel SERIAL PRIMARY KEY,
    nama_hotel VARCHAR(50) NOT NULL,
    lokasi_hotel VARCHAR(50) NOT NULL,
    rating DECIMAL(2,1),
    deskripsi TEXT --alamat lengkap,fasilitas hotel
);

CREATE TYPE sistem.jenis_kamar AS ENUM
('Standard','Deluxe','Suite','Family Room','Single Room');

CREATE TABLE sistem.tipe_kamar (
    id_tipe SERIAL PRIMARY KEY,
    nama sistem.jenis_kamar NOT NULL,
    harga DECIMAL(10,2) NOT NULL,
    kapasitas INT NOT NULL,
    deskripsi TEXT
);

CREATE TABLE sistem.kamar (
    id_kamar SERIAL PRIMARY KEY,
    id_hotel INT NOT NULL,
    id_tipe INT NOT NULL,
    nomor_kamar VARCHAR(10) UNIQUE NOT NULL,
    stok INT NOT NULL,
    FOREIGN KEY (id_hotel)
    REFERENCES sistem.hotel(id_hotel),
    FOREIGN KEY (id_tipe)
    REFERENCES sistem.tipe_kamar(id_tipe)
);

CREATE TABLE sistem.promo (
    id_promo SERIAL PRIMARY KEY,
    kode_promo VARCHAR(20) UNIQUE NOT NULL,
    deskripsi TEXT,
    nilai_diskon DECIMAL(10,2) NOT NULL,
    berlaku_dari DATE NOT NULL,
    berlaku_hingga DATE NOT NULL,
    dibuat_pada TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TYPE sistem.status_pemesanan AS ENUM
('dikonfirmasi','check_in','check_out','dibatalkan');

CREATE TABLE sistem.reservasi (
    id_reservasi SERIAL PRIMARY KEY,
    id_pelanggan INT NOT NULL,
    id_kamar INT NOT NULL,
    id_promo INT,
    masuk_kamar DATE NOT NULL,
    keluar_kamar DATE NOT NULL,
    harga_total DECIMAL(10,2) NOT NULL,
    status_reservasi sistem.status_pemesanan not null, 
    dibuat_pada TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_pelanggan)
    REFERENCES sistem.pelanggan(id_pelanggan),
    FOREIGN KEY (id_kamar)
    REFERENCES sistem.kamar(id_kamar),
    FOREIGN KEY (id_promo)
    REFERENCES sistem.promo(id_promo)
);

CREATE TYPE sistem.cara_bayar AS ENUM
('transfer_bank','e_wallet');


CREATE TABLE sistem.pembayaran (
    id_pembayaran SERIAL PRIMARY KEY,
    id_reservasi INT NOT NULL,
    tanggal_pembayaran TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    metode_pembayaran sistem.cara_bayar NOT NULL,
    FOREIGN KEY (id_reservasi)
    REFERENCES sistem.reservasi(id_reservasi)
);

CREATE TABLE sistem.ulasan (
    id_ulasan SERIAL PRIMARY KEY,
    id_reservasi INT NOT NULL,
    id_pelanggan INT NOT NULL,
    id_hotel INT NOT NULL,
    rating DECIMAL(2,1)
    CHECK (rating BETWEEN 1.0 AND 5.0),
    komentar TEXT,
    dibuat_pada TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_reservasi)
    REFERENCES sistem.reservasi(id_reservasi),
    FOREIGN KEY (id_pelanggan)
    REFERENCES sistem.pelanggan(id_pelanggan),
    FOREIGN KEY (id_hotel)
    REFERENCES sistem.hotel(id_hotel)
);

CREATE TYPE sistem.kategori_spa AS ENUM
('Massage','Facial','Body Treatment',
'Hydrotherapy','Reflexology','Hair Spa',
'Nail Treatment','Relaxation Therapy'
,'Couple Spa','Wellness Spa');

CREATE TYPE sistem.status_layanan AS ENUM
('Tersedia','Tidak Tersedia');

CREATE TABLE sistem.layanan_spa (
    id_layanan_spa SERIAL PRIMARY KEY,
    id_hotel INT NOT NULL,
    nama_layanan VARCHAR(100) NOT NULL,
    kategori sistem.kategori_spa NOT NULL,
    deskripsi TEXT,
    harga DECIMAL(12,2) NOT NULL,
    status sistem.status_layanan DEFAULT 'Tersedia',
    FOREIGN KEY (id_hotel)
    REFERENCES sistem.hotel(id_hotel)
);

CREATE TYPE sistem.status_pesanan AS ENUM
('Diproses','Diantar','Selesai','Dibatalkan');

CREATE TABLE sistem.pemesanan_spa (
    id_pemesanan_spa SERIAL PRIMARY KEY,
    id_reservasi INT NOT NULL,
    id_layanan_spa INT NOT NULL,
    tanggal_spa TIMESTAMP NOT NULL,
    jumlah_orang INT DEFAULT 1,
    total_harga DECIMAL(12,2) NOT NULL,
    status sistem.status_pesanan DEFAULT 'Diproses',
    dibuat_pada TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_reservasi)
    REFERENCES sistem.reservasi(id_reservasi),
    FOREIGN KEY (id_layanan_spa)
    REFERENCES sistem.layanan_spa(id_layanan_spa)
);

CREATE TABLE sistem.menu_makanan (
    id_makanan SERIAL PRIMARY KEY,
    nama_makanan VARCHAR(100) NOT NULL,
    deskripsi TEXT,
    harga DECIMAL(12,2) NOT NULL,
    tersedia BOOLEAN DEFAULT TRUE
);

CREATE TABLE sistem.menu_minuman (
    id_minuman SERIAL PRIMARY KEY,
    nama_minuman VARCHAR(100) NOT NULL,
    deskripsi TEXT,
    harga DECIMAL(12,2) NOT NULL,
    tersedia BOOLEAN DEFAULT TRUE
);

CREATE TABLE sistem.pemesanan_makanan (
    id_pesanan_makanan SERIAL PRIMARY KEY,
    id_reservasi INT NOT NULL,
    id_makanan INT NOT NULL,
    jumlah INT NOT NULL CHECK (jumlah > 0),
    harga DECIMAL(12,2) NOT NULL,
    total_harga DECIMAL(12,2),
    waktu_pesan TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status sistem.status_pesanan DEFAULT 'Diproses',
    catatan TEXT,
    FOREIGN KEY (id_reservasi)
    REFERENCES sistem.reservasi(id_reservasi),
    FOREIGN KEY (id_makanan)
    REFERENCES sistem.menu_makanan(id_makanan)
);

CREATE TABLE sistem.pemesanan_minuman (
    id_pesanan_minuman SERIAL PRIMARY KEY,
    id_reservasi INT NOT NULL,
    id_minuman INT NOT NULL,
    jumlah INT NOT NULL CHECK (jumlah > 0),
    harga DECIMAL(12,2) NOT NULL,
    total_harga DECIMAL(12,2),
    waktu_pesan TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status sistem.status_pesanan DEFAULT 'Diproses',
    catatan TEXT,
    FOREIGN KEY (id_reservasi)
    REFERENCES sistem.reservasi(id_reservasi),
    FOREIGN KEY (id_minuman)
    REFERENCES sistem.menu_minuman(id_minuman)
);

INSERT INTO sistem.hotel
(nama_hotel,lokasi_hotel,rating,deskripsi)
VALUES
('Grand Medan Hotel','Medan',4.5,'Hotel pusat kota'),
('Medan City Inn','Medan',4.2,'Dekat pusat perbelanjaan'),
('Medan Luxury Stay','Medan',4.7,'Hotel mewah modern'),
('Medan Family Hotel','Medan',4.3,'Cocok untuk keluarga'),
('Medan Business Suites','Medan',4.4,'Fasilitas bisnis lengkap'),

('Danau Toba Resort','Parapat',4.7,'View danau'),
('Parapat Lake Hotel','Parapat',4.5,'Pemandangan Danau Toba'),
('Toba Sunset Inn','Parapat',4.3,'View sunset danau'),
('Parapat Family Resort','Parapat',4.4,'Resort keluarga nyaman'),
('Toba Mountain Stay','Parapat',4.2,'Dekat wisata alam'),

('Jakarta Business Hotel','Jakarta',4.3,'Hotel bisnis'),
('Jakarta Central Inn','Jakarta',4.1,'Dekat kantor pusat'),
('Jakarta Premium Suites','Jakarta',4.6,'Hotel premium modern'),
('Jakarta Budget Hotel','Jakarta',4.0,'Harga ekonomis'),
('Jakarta City View','Jakarta',4.4,'View gedung kota'),

('Bali Paradise','Bali',4.8,'Dekat pantai'),
('Bali Ocean Resort','Bali',4.7,'Pantai dan sunset'),
('Bali Family Villa','Bali',4.5,'Villa keluarga'),
('Bali Luxury Suites','Bali',4.9,'Resort mewah'),
('Bali Tropical Inn','Bali',4.4,'Nuansa tropis'),

('Bandung Cozy Stay','Bandung',4.2,'Udara sejuk'),
('Bandung Mountain View','Bandung',4.4,'View pegunungan'),
('Bandung Family Inn','Bandung',4.1,'Cocok keluarga'),
('Bandung Premium Hotel','Bandung',4.5,'Hotel modern'),
('Bandung Green Resort','Bandung',4.3,'Nuansa alam'),

('Surabaya Inn','Surabaya',4.1,'Harga terjangkau'),
('Surabaya City Hotel','Surabaya',4.3,'Dekat pusat kota'),
('Surabaya Business Stay','Surabaya',4.4,'Fasilitas meeting'),
('Surabaya Family Suites','Surabaya',4.2,'Kamar luas'),
('Surabaya Luxury Inn','Surabaya',4.6,'Hotel premium'),

('Aceh Royal Hotel','Aceh',4.4,'Hotel keluarga'),
('Aceh Grand Inn','Aceh',4.2,'Dekat wisata religi'),
('Aceh Business Hotel','Aceh',4.1,'Untuk perjalanan kerja'),
('Aceh Beach Resort','Aceh',4.5,'Dekat pantai'),
('Aceh Heritage Stay','Aceh',4.3,'Nuansa budaya Aceh'),

('Makassar Beach Hotel','Makassar',4.6,'Dekat pantai'),
('Makassar Sunset Inn','Makassar',4.4,'View sunset'),
('Makassar Luxury Stay','Makassar',4.7,'Hotel mewah'),
('Makassar Family Resort','Makassar',4.3,'Resort keluarga'),
('Makassar Business Hotel','Makassar',4.2,'Hotel bisnis'),

('Padang View Hotel','Padang',4.0,'View gunung'),
('Padang Beach Resort','Padang',4.4,'Dekat pantai'),
('Padang Family Inn','Padang',4.1,'Cocok keluarga'),
('Padang Premium Suites','Padang',4.5,'Fasilitas premium'),
('Padang Green Hotel','Padang',4.2,'Nuansa alam'),

('Yogyakarta Heritage','Yogyakarta',4.5,'Nuansa budaya'),
('Jogja Malioboro Inn','Yogyakarta',4.4,'Dekat Malioboro'),
('Jogja Royal Suites','Yogyakarta',4.6,'Hotel mewah'),
('Jogja Family Stay','Yogyakarta',4.3,'Kamar keluarga'),
('Jogja Cultural Resort','Yogyakarta',4.5,'Dekat wisata budaya');

INSERT INTO sistem.tipe_kamar
(nama,harga,kapasitas,deskripsi)VALUES
('Standard',300000,2,'Kamar standar sederhana'),
('Standard',320000,2,'Kamar standar city view'),
('Deluxe',500000,2,'Kamar deluxe nyaman'),
('Deluxe',550000,3,'Kamar deluxe balcony'),
('Suite',800000,4,'Suite dengan ruang tamu'),
('Suite',950000,4,'Suite premium mewah'),
('Family Room',900000,5,'Kamar keluarga'),
('Family Room',1000000,6,'Family room luas'),
('Single Room',200000,1,'Kamar single hemat'),
('Single Room',250000,1,'Single room modern');

INSERT INTO sistem.kamar
(id_hotel,id_tipe,nomor_kamar,stok)
VALUES
(1,1,'101',10),
(2,2,'102',8),
(3,3,'103',5),
(4,4,'104',6),
(5,5,'105',12),
(6,1,'201',9),
(7,2,'202',7),
(8,3,'203',4),
(9,4,'204',5),
(10,5,'205',11),
(11,1,'301',10),
(12,2,'302',8),
(13,3,'303',5),
(14,4,'304',6),
(15,5,'305',12),
(16,1,'401',10),
(17,2,'402',8),
(18,3,'403',5),
(19,4,'404',6),
(20,5,'405',12),
(21,1,'501',10),
(22,2,'502',8),
(23,3,'503',5),
(24,4,'504',6),
(25,5,'505',12),
(26,1,'601',10),
(27,2,'602',8),
(28,3,'603',5),
(29,4,'604',6),
(30,5,'605',12),
(31,1,'701',10),
(32,2,'702',8),
(33,3,'703',5),
(34,4,'704',6),
(35,5,'705',12),
(36,1,'801',10),
(37,2,'802',8),
(38,3,'803',5),
(39,4,'804',6),
(40,5,'805',12),
(41,1,'901',10),
(42,2,'902',8),
(43,3,'903',5),
(44,4,'904',6),
(45,5,'905',12),
(46,1,'1001',10),
(47,2,'1002',8),
(48,3,'1003',5),
(49,4,'1004',6),
(50,5,'1005',12);

INSERT INTO sistem.promo
(kode_promo,deskripsi,nilai_diskon,berlaku_dari,berlaku_hingga)
VALUES
('TAHUNBARU','Promo tahun baru',40,'2026-01-01','2026-01-10'),
('IMLEK2026','Promo Tahun Baru Imlek',25,'2026-02-10','2026-02-20'),
('VALENTINE','Promo hari kasih sayang',20,'2026-02-14','2026-02-16'),
('MUDIKAMAN','Promo mudik nyaman',15,'2026-03-15','2026-04-10'),
('IDULFITRI','Promo Idul Fitri',35,'2026-03-20','2026-04-07'),
('CUTIBERSAMA','Promo cuti bersama',18,'2026-05-10','2026-05-20'),
('SCHOOLHOLIDAY','Promo libur sekolah',30,'2026-06-15','2026-07-31'),
('MERDEKA45','Promo Hari Kemerdekaan',45,'2026-08-10','2026-08-20'),
('LONGWEEKEND','Promo long weekend',22,'2026-09-01','2026-09-30'),
('HALLOWEEN','Promo Halloween',17,'2026-10-28','2026-10-31'),
('HARBOLNAS','Promo Harbolnas',50,'2026-11-10','2026-11-12'),
('BLACKFRIDAY','Promo Black Friday',40,'2026-11-27','2026-11-30'),
('NATAL2026','Promo Hari Natal',35,'2026-12-20','2026-12-26'),
('YEAR-END','Promo akhir tahun mewah',45,'2026-12-27','2026-12-31'),
('STAY3PAY2','Menginap 3 malam bayar 2',33,'2026-04-01','2026-04-30'),
('FAMILYFUN','Promo keluarga',20,'2026-05-01','2026-06-30'),
('HONEYMOON','Promo pasangan honeymoon',28,'2026-02-01','2026-12-31'),
('BUSINESSTRIP','Promo perjalanan bisnis',15,'2026-01-01','2026-12-31'),
('BACKPACKER','Promo traveler hemat',12,'2026-03-01','2026-09-30');

INSERT INTO sistem.layanan_spa
(id_hotel,nama_layanan,kategori,deskripsi,harga,status) VALUES
(1,'Balinese Massage','Massage','Pijat relaksasi',250000,'Tersedia'),
(2,'Facial Glow','Facial','Perawatan wajah',200000,'Tersedia'),
(3,'Body Refresh','Body Treatment','Perawatan tubuh',300000,'Tersedia'),
(4,'Hydro Spa','Hydrotherapy','Terapi air hangat',350000,'Tersedia'),
(5,'Foot Reflex','Reflexology','Pijat refleksi kaki',180000,'Tersedia'),
(6,'Hair Relax','Hair Spa','Perawatan rambut',220000,'Tersedia'),
(7,'Nail Beauty','Nail Treatment','Perawatan kuku',150000,'Tersedia'),
(8,'Relax Therapy','Relaxation Therapy','Relaksasi tubuh',270000,'Tersedia'),
(9,'Couple Heaven','Couple Spa','Spa pasangan',500000,'Tersedia'),
(10,'Wellness Premium','Wellness Spa','Spa kesehatan',650000,'Tersedia');


INSERT INTO sistem.menu_makanan
(nama_makanan,deskripsi,harga,tersedia) VALUES
('Nasi Goreng','Nasi goreng spesial',45000,TRUE),
('Mie Goreng','Mie goreng seafood',40000,TRUE),
('Steak Sapi','Steak premium',120000,TRUE),
('Ayam Bakar','Ayam bakar madu',55000,TRUE),
('Sate Ayam','Sate ayam madura',50000,TRUE),
('Burger','Burger daging',60000,TRUE),
('Pizza','Pizza keju',90000,TRUE),
('Sushi','Sushi Jepang',100000,TRUE),
('Pasta','Pasta carbonara',75000,TRUE),
('Salad','Salad sehat',35000,TRUE);

INSERT INTO sistem.menu_minuman
(nama_minuman,deskripsi,harga,tersedia) VALUES
('Jus Jeruk','Fresh orange',25000,TRUE),
('Kopi Latte','Hot latte',30000,TRUE),
('Milkshake','Chocolate milkshake',35000,TRUE),
('Teh Manis','Teh dingin',15000,TRUE),
('Cappuccino','Coffee cappuccino',32000,TRUE),
('Mojito','Fresh mojito',40000,TRUE),
('Air Mineral','Botol mineral',10000,TRUE),
('Smoothie','Fruit smoothie',45000,TRUE),
('Green Tea','Japanese tea',28000,TRUE),
('Soda','Soft drink',20000,TRUE);

SELECT * FROM sistem.akun_pelanggan;

SELECT * FROM sistem.pelanggan;

SELECT * FROM sistem.hotel;

SELECT * FROM sistem.tipe_kamar;

SELECT * FROM sistem.kamar;

SELECT * FROM sistem.promo;

SELECT * FROM sistem.reservasi;

SELECT * FROM sistem.pembayaran;

SELECT * FROM sistem.ulasan;

SELECT * FROM sistem.layanan_spa;

SELECT * FROM sistem.pemesanan_spa;

SELECT * FROM sistem.menu_makanan;

SELECT * FROM sistem.pemesanan_makanan;

SELECT * FROM sistem.menu_minuman;

SELECT * FROM sistem.pemesanan_minuman;

```sql
-- =========================================================
-- TRIGGER & FUNCTION SISTEM BOOKING HOTEL
-- PostgreSQL Version
-- =========================================================

-- =========================================================
-- 1. VALIDASI TANGGAL RESERVASI
-- =========================================================

CREATE OR REPLACE FUNCTION sistem.validasi_tanggal_reservasi()
RETURNS TRIGGER AS $$
BEGIN

    IF NEW.masuk_kamar < CURRENT_DATE THEN
        RAISE EXCEPTION 'Tanggal check-in tidak boleh di masa lalu';
    END IF;

    IF NEW.keluar_kamar <= NEW.masuk_kamar THEN
        RAISE EXCEPTION 'Tanggal check-out harus lebih besar dari check-in';
    END IF;

    RETURN NEW;

END;
$$ LANGUAGE plpgsql;


CREATE TRIGGER trg_validasi_tanggal_reservasi
BEFORE INSERT OR UPDATE
ON sistem.reservasi
FOR EACH ROW
EXECUTE FUNCTION sistem.validasi_tanggal_reservasi();


-- =========================================================
-- 2. CEK STOK KAMAR
-- =========================================================

CREATE OR REPLACE FUNCTION sistem.cek_stok_kamar()
RETURNS TRIGGER AS $$
DECLARE
    stok_kamar INT;
BEGIN

    SELECT stok
    INTO stok_kamar
    FROM sistem.kamar
    WHERE id_kamar = NEW.id_kamar;

    IF stok_kamar <= 0 THEN
        RAISE EXCEPTION 'Stok kamar habis';
    END IF;

    RETURN NEW;

END;
$$ LANGUAGE plpgsql;


CREATE TRIGGER trg_cek_stok_kamar
BEFORE INSERT
ON sistem.reservasi
FOR EACH ROW
EXECUTE FUNCTION sistem.cek_stok_kamar();


-- =========================================================
-- 3. KURANGI STOK SETELAH BOOKING
-- =========================================================

CREATE OR REPLACE FUNCTION sistem.kurangi_stok_kamar()
RETURNS TRIGGER AS $$
BEGIN

    UPDATE sistem.kamar
    SET stok = stok - 1
    WHERE id_kamar = NEW.id_kamar;

    RETURN NEW;

END;
$$ LANGUAGE plpgsql;


CREATE TRIGGER trg_kurangi_stok
AFTER INSERT
ON sistem.reservasi
FOR EACH ROW
EXECUTE FUNCTION sistem.kurangi_stok_kamar();


-- =========================================================
-- 4. KEMBALIKAN STOK SAAT REFUND
-- =========================================================

CREATE OR REPLACE FUNCTION sistem.kembalikan_stok_kamar()
RETURNS TRIGGER AS $$
BEGIN

    IF NEW.status_reservasi = 'dibatalkan'
    AND OLD.status_reservasi <> 'dibatalkan' THEN

        UPDATE sistem.kamar
        SET stok = stok + 1
        WHERE id_kamar = NEW.id_kamar;

    END IF;

    RETURN NEW;

END;
$$ LANGUAGE plpgsql;


CREATE TRIGGER trg_refund_kembalikan_stok
AFTER UPDATE
ON sistem.reservasi
FOR EACH ROW
EXECUTE FUNCTION sistem.kembalikan_stok_kamar();


-- =========================================================
-- 5. HITUNG TOTAL RESERVASI
-- =========================================================

CREATE OR REPLACE FUNCTION sistem.hitung_total_reservasi()
RETURNS TRIGGER AS $$
DECLARE
    harga_kamar NUMERIC;
    jumlah_malam INT;
    diskon NUMERIC DEFAULT 0;
BEGIN

    SELECT tk.harga
    INTO harga_kamar
    FROM sistem.kamar k
    JOIN sistem.tipe_kamar tk
    ON k.id_tipe = tk.id_tipe
    WHERE k.id_kamar = NEW.id_kamar;

    jumlah_malam := NEW.keluar_kamar - NEW.masuk_kamar;

    IF NEW.id_promo IS NOT NULL THEN

        SELECT nilai_diskon
        INTO diskon
        FROM sistem.promo
        WHERE id_promo = NEW.id_promo;

    END IF;

    NEW.harga_total :=
    (harga_kamar * jumlah_malam)
    - ((harga_kamar * jumlah_malam) * (diskon / 100));

    RETURN NEW;

END;
$$ LANGUAGE plpgsql;


CREATE TRIGGER trg_hitung_total_reservasi
BEFORE INSERT
ON sistem.reservasi
FOR EACH ROW
EXECUTE FUNCTION sistem.hitung_total_reservasi();


-- =========================================================
-- 6. VALIDASI PROMO
-- =========================================================

CREATE OR REPLACE FUNCTION sistem.validasi_promo()
RETURNS TRIGGER AS $$
DECLARE
    mulai_promo DATE;
    akhir_promo DATE;
BEGIN

    IF NEW.id_promo IS NOT NULL THEN

        SELECT berlaku_dari, berlaku_hingga
        INTO mulai_promo, akhir_promo
        FROM sistem.promo
        WHERE id_promo = NEW.id_promo;

        IF CURRENT_DATE NOT BETWEEN mulai_promo AND akhir_promo THEN
            RAISE EXCEPTION 'Promo sudah tidak berlaku';
        END IF;

    END IF;

    RETURN NEW;

END;
$$ LANGUAGE plpgsql;


CREATE TRIGGER trg_validasi_promo
BEFORE INSERT
ON sistem.reservasi
FOR EACH ROW
EXECUTE FUNCTION sistem.validasi_promo();


-- =========================================================
-- 7. UPDATE STATUS CHECK-IN
-- =========================================================

CREATE OR REPLACE FUNCTION sistem.update_status_checkin()
RETURNS VOID AS $$
BEGIN

    UPDATE sistem.reservasi
    SET status_reservasi = 'check_in'
    WHERE CURRENT_DATE >= masuk_kamar
    AND CURRENT_DATE < keluar_kamar
    AND status_reservasi = 'dikonfirmasi';

END;
$$ LANGUAGE plpgsql;


-- =========================================================
-- 8. UPDATE STATUS CHECK-OUT
-- =========================================================

CREATE OR REPLACE FUNCTION sistem.update_status_checkout()
RETURNS VOID AS $$
BEGIN

    UPDATE sistem.reservasi
    SET status_reservasi = 'check_out'
    WHERE CURRENT_DATE >= keluar_kamar
    AND status_reservasi = 'check_in';

END;
$$ LANGUAGE plpgsql;


-- =========================================================
-- 9. VALIDASI SPA SAAT CHECK-IN
-- =========================================================

CREATE OR REPLACE FUNCTION sistem.validasi_spa_booking()
RETURNS TRIGGER AS $$
DECLARE
    status_booking sistem.status_pemesanan;
BEGIN

    SELECT status_reservasi
    INTO status_booking
    FROM sistem.reservasi
    WHERE id_reservasi = NEW.id_reservasi;

    IF status_booking <> 'check_in' THEN
        RAISE EXCEPTION 'Spa hanya bisa dipesan saat check-in';
    END IF;

    RETURN NEW;

END;
$$ LANGUAGE plpgsql;


CREATE TRIGGER trg_validasi_spa
BEFORE INSERT
ON sistem.pemesanan_spa
FOR EACH ROW
EXECUTE FUNCTION sistem.validasi_spa_booking();


-- =========================================================
-- 10. HITUNG TOTAL SPA
-- =========================================================

CREATE OR REPLACE FUNCTION sistem.hitung_total_spa()
RETURNS TRIGGER AS $$
DECLARE
    harga_spa NUMERIC;
BEGIN

    SELECT harga
    INTO harga_spa
    FROM sistem.layanan_spa
    WHERE id_layanan_spa = NEW.id_layanan_spa;

    NEW.total_harga := harga_spa * NEW.jumlah_orang;

    RETURN NEW;

END;
$$ LANGUAGE plpgsql;


CREATE TRIGGER trg_total_spa
BEFORE INSERT
ON sistem.pemesanan_spa
FOR EACH ROW
EXECUTE FUNCTION sistem.hitung_total_spa();


-- =========================================================
-- 11. HITUNG TOTAL MAKANAN
-- =========================================================

CREATE OR REPLACE FUNCTION sistem.hitung_total_makanan()
RETURNS TRIGGER AS $$
BEGIN

    NEW.total_harga := NEW.harga * NEW.jumlah;

    RETURN NEW;

END;
$$ LANGUAGE plpgsql;


CREATE TRIGGER trg_total_makanan
BEFORE INSERT
ON sistem.pemesanan_makanan
FOR EACH ROW
EXECUTE FUNCTION sistem.hitung_total_makanan();


-- =========================================================
-- 12. HITUNG TOTAL MINUMAN
-- =========================================================

CREATE OR REPLACE FUNCTION sistem.hitung_total_minuman()
RETURNS TRIGGER AS $$
BEGIN

    NEW.total_harga := NEW.harga * NEW.jumlah;

    RETURN NEW;

END;
$$ LANGUAGE plpgsql;


CREATE TRIGGER trg_total_minuman
BEFORE INSERT
ON sistem.pemesanan_minuman
FOR EACH ROW
EXECUTE FUNCTION sistem.hitung_total_minuman();


-- =========================================================
-- 13. CEK KETERSEDIAAN MAKANAN
-- =========================================================

CREATE OR REPLACE FUNCTION sistem.cek_makanan_tersedia()
RETURNS TRIGGER AS $$
DECLARE
    tersedia_makanan BOOLEAN;
BEGIN

    SELECT tersedia
    INTO tersedia_makanan
    FROM sistem.menu_makanan
    WHERE id_makanan = NEW.id_makanan;

    IF tersedia_makanan = FALSE THEN
        RAISE EXCEPTION 'Menu makanan tidak tersedia';
    END IF;

    RETURN NEW;

END;
$$ LANGUAGE plpgsql;


CREATE TRIGGER trg_cek_makanan
BEFORE INSERT
ON sistem.pemesanan_makanan
FOR EACH ROW
EXECUTE FUNCTION sistem.cek_makanan_tersedia();


-- =========================================================
-- 14. CEK KETERSEDIAAN MINUMAN
-- =========================================================

CREATE OR REPLACE FUNCTION sistem.cek_minuman_tersedia()
RETURNS TRIGGER AS $$
DECLARE
    tersedia_minuman BOOLEAN;
BEGIN

    SELECT tersedia
    INTO tersedia_minuman
    FROM sistem.menu_minuman
    WHERE id_minuman = NEW.id_minuman;

    IF tersedia_minuman = FALSE THEN
        RAISE EXCEPTION 'Menu minuman tidak tersedia';
    END IF;

    RETURN NEW;

END;
$$ LANGUAGE plpgsql;


CREATE TRIGGER trg_cek_minuman
BEFORE INSERT
ON sistem.pemesanan_minuman
FOR EACH ROW
EXECUTE FUNCTION sistem.cek_minuman_tersedia();


-- =========================================================
-- 15. AUTO TIMESTAMP PEMBAYARAN
-- =========================================================

CREATE OR REPLACE FUNCTION sistem.auto_timestamp_pembayaran()
RETURNS TRIGGER AS $$
BEGIN

    NEW.tanggal_pembayaran := CURRENT_TIMESTAMP;

    RETURN NEW;

END;
$$ LANGUAGE plpgsql;


CREATE TRIGGER trg_auto_timestamp_pembayaran
BEFORE INSERT
ON sistem.pembayaran
FOR EACH ROW
EXECUTE FUNCTION sistem.auto_timestamp_pembayaran();


-- =========================================================
-- 16. VIEW RIWAYAT BOOKING USER
-- =========================================================

CREATE VIEW sistem.v_riwayat_booking AS
SELECT
    r.id_reservasi,
    p.nama_pelanggan,
    h.nama_hotel,
    tk.nama AS tipe_kamar,
    r.masuk_kamar,
    r.keluar_kamar,
    r.harga_total,
    r.status_reservasi
FROM sistem.reservasi r
JOIN sistem.pelanggan p
ON r.id_pelanggan = p.id_pelanggan
JOIN sistem.kamar k
ON r.id_kamar = k.id_kamar
JOIN sistem.hotel h
ON k.id_hotel = h.id_hotel
JOIN sistem.tipe_kamar tk
ON k.id_tipe = tk.id_tipe;


-- =========================================================
-- 17. VIEW DETAIL PEMBAYARAN
-- =========================================================

CREATE VIEW sistem.v_detail_pembayaran AS
SELECT
    pb.id_pembayaran,
    pl.nama_pelanggan,
    h.nama_hotel,
    r.harga_total,
    pb.metode_pembayaran,
    pb.tanggal_pembayaran
FROM sistem.pembayaran pb
JOIN sistem.reservasi r
ON pb.id_reservasi = r.id_reservasi
JOIN sistem.pelanggan pl
ON r.id_pelanggan = pl.id_pelanggan
JOIN sistem.kamar k
ON r.id_kamar = k.id_kamar
JOIN sistem.hotel h
ON k.id_hotel = h.id_hotel;


-- =========================================================
-- 18. VIEW LAYANAN TAMBAHAN USER
-- =========================================================

CREATE VIEW sistem.v_layanan_user AS
SELECT
    r.id_reservasi,
    p.nama_pelanggan,
    ls.nama_layanan,
    ps.total_harga,
    ps.status
FROM sistem.pemesanan_spa ps
JOIN sistem.reservasi r
ON ps.id_reservasi = r.id_reservasi
JOIN sistem.pelanggan p
ON r.id_pelanggan = p.id_pelanggan
JOIN sistem.layanan_spa ls
ON ps.id_layanan_spa = ls.id_layanan_spa;
