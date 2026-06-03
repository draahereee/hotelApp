

create database hotelSystem;

create schema sistem;



create table sistem.akun_pelanggan (
    id_akun serial primary key,
    username varchar(30) unique not null,
    email varchar(50) unique not null,
    password VARCHAR(255) NOT NULL,
    dibuat_pada TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


CREATE TYPE sistem.gender AS ENUM
('laki-laki','perempuan');

CREATE TABLE sistem.pelanggan (
    id_pelanggan SERIAL PRIMARY KEY,
    id_akun INT UNIQUE NOT NULL,
    nama_pelanggan VARCHAR(50) NOT NULL,
    no_hp VARCHAR(20) UNIQUE NOT NULL,
    alamat TEXT,
    jenis_kelamin sistem.gender NOT NULL,
    dibuat_pada TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_akun)
    REFERENCES sistem.akun_pelanggan(id_akun)
    ON DELETE CASCADE
);


CREATE TABLE sistem.hotel (
    id_hotel SERIAL PRIMARY KEY,
    nama_hotel VARCHAR(50) NOT NULL,
    lokasi_hotel VARCHAR(50) NOT NULL,
    rating decimal(2,1) DEFAULT 0
    CHECK (rating BETWEEN 0 AND 5),
    deskripsi TEXT
);


CREATE TYPE sistem.jenis_kamar AS ENUM
('Standard','Deluxe','Suite','Family Room','Single Room');

CREATE TABLE sistem.tipe_kamar (
    id_tipe SERIAL PRIMARY KEY,
    nama sistem.jenis_kamar NOT NULL,
    harga DECIMAL(12,0) NOT NULL, 
    kapasitas INT NOT NULL,
    deskripsi TEXT
);


CREATE TABLE sistem.kamar (
    id_kamar SERIAL PRIMARY KEY,
    id_hotel INT NOT NULL,
    id_tipe INT NOT NULL,
    nomor_kamar VARCHAR(10) NOT NULL,
    FOREIGN KEY (id_hotel) REFERENCES sistem.hotel(id_hotel),
    FOREIGN KEY (id_tipe) REFERENCES sistem.tipe_kamar(id_tipe),
    CONSTRAINT unique_nomor_kamar_per_hotel 
    UNIQUE (id_hotel, nomor_kamar) 
);


CREATE TABLE sistem.promo (
    id_promo SERIAL PRIMARY KEY,
    kode_promo VARCHAR(20) UNIQUE NOT NULL,
    deskripsi TEXT,
    nilai_diskon DECIMAL(5,2) NOT NULL, 
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
    harga_total DECIMAL(12,0) NOT NULL, -- Diperbaiki
    status_reservasi sistem.status_pemesanan NOT NULL,
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
    id_reservasi INT UNIQUE NOT NULL,
    rating decimal(2,1)
    CHECK (rating BETWEEN 1 AND 5),
    komentar TEXT,
    dibuat_pada TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_reservasi)
    REFERENCES sistem.reservasi(id_reservasi)
);


CREATE TYPE sistem.kategori_spa AS ENUM
('Massage','Facial','Body Treatment','Hydrotherapy',
'Reflexology','Hair Spa','Nail Treatment',
'Relaxation Therapy','Couple Spa','Wellness Spa');

CREATE TYPE sistem.status_layanan AS ENUM
('Tersedia','Tidak Tersedia');

CREATE TABLE sistem.layanan_spa (
    id_layanan_spa SERIAL PRIMARY KEY,
    id_hotel INT NOT NULL,
    nama_layanan VARCHAR(100) NOT NULL,
    kategori sistem.kategori_spa NOT NULL,
    deskripsi TEXT,
    harga DECIMAL(12,0) NOT NULL, 
    status sistem.status_layanan DEFAULT 'Tersedia',
    FOREIGN KEY (id_hotel)
    REFERENCES sistem.hotel(id_hotel)
);

CREATE TYPE sistem.status_pesanan AS ENUM
('Dijadwalkan','Diantar','Selesai','Dibatalkan');

CREATE TABLE sistem.pemesanan_spa (
    id_pemesanan_spa SERIAL PRIMARY KEY,
    id_reservasi INT NOT NULL,
    id_layanan_spa INT NOT NULL,
    tanggal_spa TIMESTAMP NOT NULL,
    jumlah_orang INT DEFAULT 1,
    total_harga DECIMAL(12,0) NOT NULL,
    status sistem.status_pesanan DEFAULT 'Dijadwalkan',
    dibuat_pada TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_reservasi)
    REFERENCES sistem.reservasi(id_reservasi),
    FOREIGN KEY (id_layanan_spa)
    REFERENCES sistem.layanan_spa(id_layanan_spa)
);


CREATE TYPE sistem.jenis_fnb AS ENUM
('Makanan','Minuman');

CREATE TABLE sistem.menu_fnb (
    id_menu SERIAL PRIMARY KEY,
    nama_item VARCHAR(100) NOT NULL,
    kategori sistem.jenis_fnb NOT NULL,
    deskripsi TEXT,
    harga DECIMAL(12,0) NOT NULL, 
    tersedia BOOLEAN DEFAULT TRUE
);

CREATE TABLE sistem.pemesanan_fnb (
    id_pesanan_fnb SERIAL PRIMARY KEY,
    id_reservasi INT NOT NULL,
    id_menu INT NOT NULL,
    jumlah INT NOT NULL CHECK (jumlah > 0),
    harga DECIMAL(12,0) NOT NULL, 
    total_harga DECIMAL(12,0),
    waktu_pesan TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status sistem.status_pesanan DEFAULT 'Diantar',
    catatan TEXT,
    FOREIGN KEY (id_reservasi)
    REFERENCES sistem.reservasi(id_reservasi),
    FOREIGN KEY (id_menu)
    REFERENCES sistem.menu_fnb(id_menu)
);

--insert dummy data untuk testing

INSERT INTO sistem.hotel 
(nama_hotel,lokasi_hotel,rating,deskripsi) 
VALUES
('Grand Medan Hotel','Medan',4.5,'Alamat: Jl. Diponegoro No. 12, Medan | Telp: (061) 451-2000 | Hotel bintang 4 premium di pusat kota Medan. Dilengkapi dengan fasilitas kolam renang outdoor, spa relaksasi, gym, dan restoran internasional. Cocok untuk perjalanan bisnis maupun liburan keluarga.'),
('Medan City Inn','Medan',4.2,'Alamat: Jl. Gajah Mada No. 45, Medan | Telp: (061) 452-3111 | Akomodasi nyaman dan strategis di pusat kota Medan, hanya beberapa menit dari pusat perbelanjaan utama. Menyediakan akses Wi-Fi gratis, sarapan prasmanan, dan pelayanan ramah 24 jam.'),
('Medan Luxury Stay','Medan',4.7,'Alamat: Jl. S. Parman No. 88, Medan | Telp: (061) 455-9999 | Hunian mewah modern dengan desain interior elegan di kawasan bisnis Medan. Menawarkan kamar dengan pemandangan kota, bathtub privat, layanan kamar 24 jam, dan fasilitas spa eksklusif.'),
('Medan Family Hotel','Medan',4.3,'Alamat: Jl. Pemuda No. 7, Medan | Telp: (061) 415-4321 | Hotel ramah anak dan keluarga yang terletak di lingkungan yang tenang di Medan. Memiliki ruang keluarga yang luas, taman bermain anak, serta restoran keluarga dengan menu variatif.'),
('Medan Business Suites','Medan',4.4,'Alamat: Jl. Raden Saleh No. 3, Medan | Telp: (061) 420-5555 | Pilihan utama bagi para pelaku bisnis di kota Medan. Dilengkapi dengan pusat bisnis 24 jam, ruang rapat serbaguna, koneksi internet berkecepatan tinggi, dan akses mudah ke area perkantoran.'),

('Danau Toba Resort','Parapat',4.7,'Alamat: Jl. Sisingamangaraja No. 112, Parapat | Telp: (0625) 41234 | Resort eksklusif di tepi Danau Toba, Parapat. Menyediakan kamar dengan pemandangan langsung ke danau, fasilitas olahraga air, spa tradisional, dan area outdoor yang luas untuk bersantai.'),
('Parapat Lake Hotel','Parapat',4.5,'Alamat: Jl. Pora-Pora No. 5, Parapat | Telp: (0625) 41567 | Menawarkan keindahan panorama Danau Toba langsung dari balkon kamar Anda. Fasilitas meliputi restoran tepi danau, kolam renang hangat, dan akses dekat ke pelabuhan kapal feri.'),
('Toba Sunset Inn','Parapat',4.3,'Alamat: Jl. Pantai Bebas No. 21, Parapat | Telp: (0625) 41987 | Penginapan bernuansa hangat di Parapat yang menghadap langsung ke arah matahari terbenam Danau Toba. Tempat ideal untuk pasangan yang mencari suasana romantis dan tenang.'),
('Parapat Family Resort','Parapat',4.4,'Alamat: Jl. Marihat No. 34, Parapat | Telp: (0625) 41333 | Resort keluarga yang nyaman dengan fasilitas rekreasi lengkap di tepi Danau Toba. Dilengkapi dengan taman bermain, fasilitas BBQ, dan kamar berukuran besar untuk keluarga besar.'),
('Toba Mountain Stay','Parapat',4.2,'Alamat: Jl. Nelson Purba No. 9, Parapat | Telp: (0625) 41444 | Terletak di dataran tinggi Parapat, dikelilingi oleh udara sejuk dan perbukitan hijau. Cocok untuk pencinta alam dengan akses mudah ke jalur trekking dan spot foto pemandangan Danau Toba.'),

('Jakarta Business Hotel','Jakarta',4.3,'Alamat: Jl. MH Thamrin No. 10, Jakarta Pusat | Telp: (021) 390-1111 | Hotel bisnis modern di pusat kawasan segitiga emas Jakarta. Menyediakan ruang pertemuan canggih, lounge eksekutif, dan akses cepat ke pusat perkantoran Sudirman-Thamrin.'),
('Jakarta Central Inn','Jakarta',4.1,'Alamat: Jl. Jend. Sudirman Kav. 21, Jakarta Selatan | Telp: (021) 520-2222 | Penginapan strategis dengan harga terjangkau di pusat kota Jakarta, sangat dekat dengan berbagai kantor pusat pemerintahan dan stasiun utama. Nyaman untuk perjalanan dinas singkat.'),
('Jakarta Premium Suites','Jakarta',4.6,'Alamat: Jl. HR Rasuna Said No. 5, Jakarta Selatan | Telp: (021) 525-3333 | Apartemen dan hotel premium modern di kawasan elit Jakarta. Menawarkan sky lounge dengan pemandangan gedung pencakar langit, kolam renang infinity, layanan spa mewah, dan teknologi smart-room.'),
('Jakarta Budget Hotel','Jakarta',4.0,'Alamat: Jl. Mangga Besar Raya No. 123, Jakarta Barat | Telp: (021) 626-4444 | Pilihan cerdas bagi pelancong dengan anggaran terbatas di Jakarta. Menawarkan kamar yang bersih, minimalis, ber-AC, dengan fasilitas Wi-Fi cepat dan keamanan 24 jam.'),
('Jakarta City View','Jakarta',4.4,'Alamat: Jl. Gatot Subroto No. 44, Jakarta Pusat | Telp: (021) 570-5555 | Menyajikan pemandangan lanskap gedung-gedung bertingkat kota Jakarta yang menakjubkan. Dilengkapi dengan restoran di atap (rooftop), bar, serta gym modern untuk menjaga kebugaran.'),

('Bali Paradise','Bali',4.8,'Alamat: Jl. Raya Kuta No. 88, Badung, Bali | Telp: (0361) 751-234 | Resort tropis yang menakjubkan, hanya berjalan kaki dari pantai pasir putih Bali. Menawarkan kolam renang bergaya laguna, spa tepi pantai, dan kelas yoga harian untuk relaksasi total.'),
('Bali Ocean Resort','Bali',4.7,'Alamat: Jl. Pantai Jeger No. 12, Seminyak, Bali | Telp: (0361) 730-567 | Resort mewah di tepi pantai Bali yang menawarkan pemandangan matahari terbenam yang spektakuler. Fasilitas mencakup klub pantai privat, restoran seafood fine-dining, dan villa dengan kolam renang pribadi.'),
('Bali Family Villa','Bali',4.5,'Alamat: Jl. Danau Tamblingan No. 45, Sanur, Bali | Telp: (0361) 288-910 | Kompleks villa keluarga yang luas dengan suasana privat di Bali. Setiap villa dilengkapi dengan dapur lengkap, kolam renang pribadi, ruang keluarga terbuka, dan layanan pengasuhan anak.'),
('Bali Luxury Suites','Bali',4.9,'Alamat: Jl. Uluwatu No. 99, Ungasan, Bali | Telp: (0361) 895-4321 | Destinasi resort mewah kelas dunia di Bali dengan privasi maksimal. Menawarkan layanan butler pribadi 24 jam, spa kesehatan premium, helipad, dan pemandangan tebing laut yang memukau.'),
('Bali Tropical Inn','Bali',4.4,'Alamat: Jl. Monkey Forest, Ubud, Bali | Telp: (0361) 975-555 | Penginapan dengan arsitektur tradisional Bali yang kental, dikelilingi taman tropis yang rimbun dan asri. Memberikan suasana pedesaan yang tenang namun tetap dekat dengan pusat keramaian.'),

('Bandung Cozy Stay','Bandung',4.2,'Alamat: Jl. Ir. H. Juanda (Dago) No. 150, Bandung | Telp: (022) 250-1234 | Menawarkan tempat beristirahat yang tenang dengan udara sejuk khas pegunungan Bandung. Dilengkapi dengan kafe estetik, area api umum, dan balkon pribadi di setiap kamar.'),
('Bandung Mountain View','Bandung',4.4,'Alamat: Jl. Raya Setiabudi No. 201, Bandung | Telp: (022) 201-5678 | Terletak di dataran tinggi Bandung dengan pemandangan hijau pegunungan yang memanjakan mata. Fasilitas meliputi kolam renang air hangat, restoran dengan pemandangan terbuka, dan jalur jalan santai.'),
('Bandung Family Inn','Bandung',4.1,'Alamat: Jl. Cihampelas No. 42, Bandung | Telp: (022) 420-9101 | Akomodasi keluarga yang hangat dan ramah di Bandung, dekat dengan berbagai tempat wisata kuliner dan belanja. Menyediakan kamar tipe keluarga dan area bermain anak yang aman.'),
('Bandung Premium Hotel','Bandung',4.5,'Alamat: Jl. Asia Afrika No. 77, Bandung | Telp: (022) 423-4567 | Hotel bergaya modern minimalis dengan fasilitas premium di pusat kota Bandung. Akses mudah ke factory outlet ternama, dilengkapi pusat kebugaran, spa, dan fasilitas kuliner lokal berkualitas.'),
('Bandung Green Resort','Bandung',4.3,'Alamat: Jl. Merdeka No. 10, Bandung | Telp: (022) 421-8888 | Resort berkonsep ramah lingkungan di tengah alam Bandung yang asri. Dikelilingi pohon-pohon rindang, menawarkan aktivitas luar ruangan seperti bersepeda, yoga, dan spa berlatar alam.'),

('Surabaya Inn','Surabaya',4.1,'Alamat: Jl. Tunjungan No. 12, Surabaya | Telp: (031) 531-2345 | Penginapan dengan harga sangat terjangkau namun tetap mengutamakan kenyamanan di Surabaya. Fasilitas mencakup kamar ber-AC, Wi-Fi gratis, dan lokasi yang mudah diakses dari stasiun kereta.'),
('Surabaya City Hotel','Surabaya',4.3,'Alamat: Jl. Basuki Rahmat No. 85, Surabaya | Telp: (031) 545-6789 | Hotel modern di jantung kota Surabaya, memudahkan akses ke pusat perbelanjaan terbesar dan kawasan bersejarah. Ideal bagi pelancong kota maupun pebisnis dinas.'),
('Surabaya Business Stay','Surabaya',4.4,'Alamat: Jl. Mayjen Jonosewojo No. 3, Surabaya | Telp: (031) 739-0123 | Dirancang khusus untuk mendukung produktivitas kerja selama di Surabaya. Memiliki ruang meeting modular, koneksi internet serat optik, dan dekat dengan pusat industri serta perkantoran.'),
('Surabaya Family Suites','Surabaya',4.2,'Alamat: Jl. Raya Darmo No. 110, Surabaya | Telp: (031) 566-4567 | Menawarkan kamar tipe suite yang sangat luas untuk kenyamanan ekstra bersama keluarga di Surabaya. Dilengkapi dengan kolam renang anak, kulkas mini, dan sarapan buffet internasional.'),
('Surabaya Luxury Inn','Surabaya',4.6,'Alamat: Jl. Embong Malang No. 25, Surabaya | Telp: (031) 535-8888 | Penginapan premium dengan standar pelayanan bintang lima di Surabaya. Menawarkan fasilitas spa mewah, restoran fine-dining bergaya gourmet, dan kamar dengan dekorasi bernilai seni tinggi.'),

('Aceh Royal Hotel','Aceh',4.4,'Alamat: Jl. Teuku Umar No. 14, Banda Aceh | Telp: (0651) 22123 | Hotel keluarga bernuansa islami dan nyaman di pusat kota Aceh. Menyediakan fasilitas ramah keluarga, ruang makan yang menyajikan kuliner khas Aceh, serta dekat dengan pusat transportasi.'),
('Aceh Grand Inn','Aceh',4.2,'Alamat: Jl. T. Nyak Arief No. 45, Banda Aceh | Telp: (0651) 33456 | Terletak strategis dan sangat dekat dengan Masjid Raya Baiturrahman Aceh, menjadikannya pilihan ideal untuk wisata religi. Menyediakan suasana yang tenang, bersih, dan pelayanan yang santun.'),
('Aceh Business Hotel','Aceh',4.1,'Alamat: Jl. Mohd. Jam No. 8, Banda Aceh | Telp: (0651) 23579 | Akomodasi efisien untuk para profesional yang melakukan perjalanan kerja ke Aceh. Dilengkapi meja kerja ergonomis di setiap kamar, ruang pertemuan, dan akses Wi-Fi berkecepatan tinggi.'),
('Aceh Beach Resort','Aceh',4.5,'Alamat: Jl. Sultan Iskandar Muda, Pantai Ulee Lheue, Aceh | Telp: (0651) 44890 | Resort indah di tepi pantai Aceh, menawarkan ketenangan dengan deburan ombak dan pasir putih. Memiliki fasilitas pondok tepi pantai, restoran makanan laut segar, dan area olahraga air.'),
('Aceh Heritage Stay','Aceh',4.3,'Alamat: Jl. Pangeran Diponegoro No. 22, Banda Aceh | Telp: (0651) 21980 | Penginapan unik yang mengangkat keindahan arsitektur dan ornamen budaya tradisional Aceh. Tamu dapat menikmati teh tarik gratis di sore hari dan mempelajari sejarah lokal dari galeri hotel.'),

('Makassar Beach Hotel','Makassar',4.6,'Alamat: Jl. Penghibur No. 10, Pantai Losari, Makassar | Telp: (0411) 361-2345 | Hotel strategis di sepanjang Pantai Losari Makassar. Menawarkan kemudahan akses berjalan kaki ke ikon kota, area kuliner pisang epe, serta kamar dengan pemandangan laut lepas.'),
('Makassar Sunset Inn','Makassar',4.4,'Alamat: Jl. Somba Opu No. 125, Makassar | Telp: (0411) 362-5678 | Tempat terbaik di Makassar untuk menikmati keindahan matahari terbenam langsung dari rooftop lounge hotel. Suasana santai dengan fasilitas kamar modern dan kafe kopi lokal.'),
('Makassar Luxury Stay','Makassar',4.7,'Alamat: Jl. Urip Sumoharjo No. 20, Makassar | Telp: (0411) 449-0123 | Menghadirkan kemewahan kontemporer di kota Makassar dengan pelayanan prima. Dilengkapi dengan kolam renang indoor, pusat kebugaran mutakhir, spa, dan restoran bersertifikat internasional.'),
('Makassar Family Resort','Makassar',4.3,'Alamat: Jl. AP Pettarani No. 55, Makassar | Telp: (0411) 453-7777 | Resort tepi laut yang dirancang khusus untuk rekreasi keluarga di Makassar. Memiliki kolam renang anak bertema air, fasilitas bersepeda pantai, dan pilihan kamar tipe pondok.'),
('Makassar Business Hotel','Makassar',4.2,'Alamat: Jl. Boulevard No. 8, Panakkukang, Makassar | Telp: (0411) 425-8888 | Pilihan cerdas bagi para eksekutif di Makassar, dekat dengan kawasan pelabuhan dan pusat bisnis. Menyediakan sarana konferensi lengkap, lounge bisnis tenang, dan layanan penjemputan bandara.'),

('Padang View Hotel','Padang',4.0,'Alamat: Jl. Khatib Sulaiman No. 22, Padang | Telp: (0751) 705-1234 | Menawarkan kombinasi pemandangan kota Padang dan perbukitan hijau yang asri. Kamar yang sejuk, dilengkapi restoran yang menyajikan masakan tradisional Minang otentik dan barat.'),
('Padang Beach Resort','Padang',4.4,'Alamat: Jl. Pantai Padang No. 50, Padang | Telp: (0751) 392-5678 | Resort cantik yang menghadap langsung ke Pantai Padang. Menjadi spot favorit untuk menikmati angin laut, dilengkapi dengan kolam renang luar ruangan dan bar jus tepi kolam.'),
('Padang Family Inn','Padang',4.1,'Alamat: Jl. Jend. Sudirman No. 15, Padang | Telp: (0751) 203-9101 | Penginapan bergaya rumah tinggal yang hangat dan aman untuk keluarga di Padang. Kamar berukuran besar, staf yang ramah, serta dekat dengan destinasi wisata budaya sejarah Padang.'),
('Padang Premium Suites','Padang',4.5,'Alamat: Jl. Hayam Wuruk No. 8, Padang | Telp: (0751) 315-4567 | Hotel suite dengan fasilitas mewah dan modern di area komersial Padang. Menawarkan kenyamanan tempat tidur premium, kamar mandi marmer, pusat kebugaran, dan layanan penatu ekspres.'),
('Padang Green Hotel','Padang',4.2,'Alamat: Jl. Veteran No. 34, Padang | Telp: (0751) 288-8888 | Hotel berkonsep eco-living di Padang dengan taman hidroponik dan arsitektur ramah lingkungan. Menciptakan suasana menginap yang asri, bersih, dan bebas dari kebisingan kota.'),

('Yogyakarta Heritage','Yogyakarta',4.5,'Alamat: Jl. Prawirotaman No. 18, Yogyakarta | Telp: (0274) 375-123 | Menawarkan pengalaman menginap otentik dengan sentuhan budaya Jawa klasik yang kental di Yogyakarta. Fasilitas mencakup pertunjukan seni mingguan, kolam renang bergaya petirtaan, dan spa keraton.'),
('Jogja Malioboro Inn','Yogyakarta',4.4,'Alamat: Jl. Malioboro No. 42, Yogyakarta | Telp: (0274) 561-456 | Hanya selangkah dari jalan legendaris Malioboro Yogyakarta, memudahkan Anda berbelanja dan berwisata kuliner malam. Kamar bersih, nyaman, dengan harga bersahabat dan akses Wi-Fi 24 jam.'),
('Jogja Royal Suites','Yogyakarta',4.6,'Alamat: Jl. Laksda Adisucipto No. 88, Yogyakarta | Telp: (0274) 488-999 | Akomodasi bintang lima mewah yang memadukan kenyamanan modern dan kemegahan tradisi kerajaan Jogja. Menyediakan bathtub di setiap suite, klub kebugaran eksklusif, dan kuliner kelas atas.'),
('Jogja Family Stay','Yogyakarta',4.3,'Alamat: Jl. Palagan Tentara Pelajar Km. 7, Yogyakarta | Telp: (0274) 868-777 | Pilihan akomodasi terbaik untuk liburan keluarga di Yogyakarta. Memiliki kamar interkoneksi, dapur bersama, penyewaan mobil/sepeda, serta suasana halaman tengah yang asri ala rumah Joglo.'),
('Jogja Cultural Resort','Yogyakarta',4.5,'Alamat: Jl. AM. Sangaji No. 25, Yogyakarta | Telp: (0274) 555-888 | Resort menenangkan yang berlokasi dekat dengan situs warisan budaya bersejarah di Yogyakarta. Menawarkan workshop membatik gratis untuk tamu, restoran kuliner tradisional, dan pijat refleksi Jawa.');

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


INSERT INTO sistem.kamar (id_hotel, id_tipe, nomor_kamar) VALUES
(1,1,'101'),(1,2,'102'),(1,3,'103'),(1,4,'104'),(1,5,'105'),(1,6,'106'),(1,7,'107'),(1,8,'108'),(1,9,'109'),(1,10,'110'),
(1,1,'111'),(1,2,'112'),(1,3,'113'),(1,4,'114'),(1,5,'115'),(1,6,'116'),(1,7,'117'),(1,8,'118'),(1,9,'119'),(1,10,'120'),
(1,1,'121'),(1,2,'122'),(1,3,'123'),(1,4,'124'),(1,5,'125'),(1,6,'126'),(1,7,'127'),(1,8,'128'),(1,9,'129'),(1,10,'130'),

(2,2,'101'),(2,3,'102'),(2,4,'103'),(2,5,'104'),(2,6,'105'),(2,7,'106'),(2,8,'107'),(2,9,'108'),
(2,2,'109'),(2,3,'110'),(2,4,'111'),(2,5,'112'),(2,6,'113'),(2,7,'114'),(2,8,'115'),(2,9,'116'),
(2,2,'117'),(2,3,'118'),(2,4,'119'),(2,5,'120'),(2,6,'121'),(2,7,'122'),(2,8,'123'),(2,9,'124'),

(3,3,'101'),(3,4,'102'),(3,5,'103'),(3,6,'104'),(3,7,'105'),
(3,3,'106'),(3,4,'107'),(3,5,'108'),(3,6,'109'),(3,7,'110'),
(3,3,'111'),(3,4,'112'),(3,5,'113'),(3,6,'114'),(3,7,'115'),

(4,4,'101'),(4,5,'102'),(4,6,'103'),(4,7,'104'),(4,8,'105'),(4,9,'106'),
(4,4,'107'),(4,5,'108'),(4,6,'109'),(4,7,'110'),(4,8,'111'),(4,9,'112'),
(4,4,'113'),(4,5,'114'),(4,6,'115'),(4,7,'116'),(4,8,'117'),(4,9,'118'),

(5,5,'101'),(5,6,'102'),(5,7,'103'),(5,8,'104'),(5,9,'105'),(5,10,'106'),(5,1,'107'),(5,2,'108'),(5,3,'109'),(5,4,'110'),(5,5,'111'),(5,6,'112'),
(5,5,'113'),(5,6,'114'),(5,7,'115'),(5,8,'116'),(5,9,'117'),(5,10,'118'),(5,1,'119'),(5,2,'120'),(5,3,'121'),(5,4,'122'),(5,5,'123'),(5,6,'124'),
(5,5,'125'),(5,6,'126'),(5,7,'127'),(5,8,'128'),(5,9,'129'),(5,10,'130'),(5,1,'131'),(5,2,'132'),(5,3,'133'),(5,4,'134'),(5,5,'135'),(5,6,'136'),

(6,6,'101'),(6,7,'102'),(6,8,'103'),(6,9,'104'),(6,10,'105'),(6,1,'106'),(6,2,'107'),(6,3,'108'),(6,4,'109'),(6,5,'110'),
(6,6,'111'),(6,7,'112'),(6,8,'113'),(6,9,'114'),(6,10,'115'),(6,1,'116'),(6,2,'117'),(6,3,'118'),(6,4,'119'),(6,5,'120'),
(6,6,'121'),(6,7,'122'),(6,8,'123'),(6,9,'124'),(6,10,'125'),(6,1,'126'),(6,2,'127'),(6,3,'128'),(6,4,'129'),(6,5,'130'),

(7,7,'101'),(7,8,'102'),(7,9,'103'),(7,10,'104'),(7,1,'105'),(7,2,'106'),(7,3,'107'),(7,4,'108'),
(7,7,'109'),(7,8,'110'),(7,9,'111'),(7,10,'112'),(7,1,'113'),(7,2,'114'),(7,3,'115'),(7,4,'116'),
(7,7,'117'),(7,8,'118'),(7,9,'119'),(7,10,'120'),(7,1,'121'),(7,2,'122'),(7,3,'123'),(7,4,'124'),

(8,8,'101'),(8,9,'102'),(8,10,'103'),(8,1,'104'),(8,2,'105'),
(8,8,'106'),(8,9,'107'),(8,10,'108'),(8,1,'109'),(8,2,'110'),
(8,8,'111'),(8,9,'112'),(8,10,'113'),(8,1,'114'),(8,2,'115'),

(9,9,'101'),(9,10,'102'),(9,1,'103'),(9,2,'104'),(9,3,'105'),(9,4,'106'),
(9,9,'107'),(9,10,'108'),(9,1,'109'),(9,2,'110'),(9,3,'111'),(9,4,'112'),
(9,9,'113'),(9,10,'114'),(9,1,'115'),(9,2,'116'),(9,3,'117'),(9,4,'118'),

(10,10,'101'),(10,1,'102'),(10,2,'103'),(10,3,'104'),(10,4,'105'),(10,5,'106'),(10,6,'107'),(10,7,'108'),(10,8,'109'),(10,9,'110'),(10,10,'111'),(10,1,'112'),
(10,10,'113'),(10,1,'114'),(10,2,'115'),(10,3,'116'),(10,4,'117'),(10,5,'118'),(10,6,'119'),(10,7,'120'),(10,8,'121'),(10,9,'122'),(10,10,'123'),(10,1,'124'),
(10,10,'125'),(10,1,'126'),(10,2,'127'),(10,3,'128'),(10,4,'129'),(10,5,'130'),(10,6,'131'),(10,7,'132'),(10,8,'133'),(10,9,'134'),(10,10,'135'),(10,1,'136'),

(11,1,'101'),(11,2,'102'),(11,3,'103'),(11,4,'104'),(11,5,'105'),(11,6,'106'),(11,7,'107'),(11,8,'108'),(11,9,'109'),(11,10,'110'),
(11,1,'111'),(11,2,'112'),(11,3,'113'),(11,4,'114'),(11,5,'115'),(11,6,'116'),(11,7,'117'),(11,8,'118'),(11,9,'119'),(11,10,'120'),
(11,1,'121'),(11,2,'122'),(11,3,'123'),(11,4,'124'),(11,5,'125'),(11,6,'126'),(11,7,'127'),(11,8,'128'),(11,9,'129'),(11,10,'130'),

(12,2,'101'),(12,3,'102'),(12,4,'103'),(12,5,'104'),(12,6,'105'),(12,7,'106'),(12,8,'107'),(12,9,'108'),
(12,2,'109'),(12,3,'110'),(12,4,'111'),(12,5,'112'),(12,6,'113'),(12,7,'114'),(12,8,'115'),(12,9,'116'),
(12,2,'117'),(12,3,'118'),(12,4,'119'),(12,5,'120'),(12,6,'121'),(12,7,'122'),(12,8,'123'),(12,9,'124'),

(13,3,'101'),(13,4,'102'),(13,5,'103'),(13,6,'104'),(13,7,'105'),
(13,3,'106'),(13,4,'107'),(13,5,'108'),(13,6,'109'),(13,7,'110'),
(13,3,'111'),(13,4,'112'),(13,5,'113'),(13,6,'114'),(13,7,'115'),

(14,4,'101'),(14,5,'102'),(14,6,'103'),(14,7,'104'),(14,8,'105'),(14,9,'106'),
(14,4,'107'),(14,5,'108'),(14,6,'109'),(14,7,'110'),(14,8,'111'),(14,9,'112'),
(14,4,'113'),(14,5,'114'),(14,6,'115'),(14,7,'116'),(14,8,'117'),(14,9,'118'),

(15,5,'101'),(15,6,'102'),(15,7,'103'),(15,8,'104'),(15,9,'105'),(15,10,'106'),(15,1,'107'),(15,2,'108'),(15,3,'109'),(15,4,'110'),(15,5,'111'),(15,6,'112'),
(15,5,'113'),(15,6,'114'),(15,7,'115'),(15,8,'116'),(15,9,'117'),(15,10,'118'),(15,1,'119'),(15,2,'120'),(15,3,'121'),(15,4,'122'),(15,5,'123'),(15,6,'124'),
(15,5,'125'),(15,6,'126'),(15,7,'127'),(15,8,'128'),(15,9,'129'),(15,10,'130'),(15,1,'131'),(15,2,'132'),(15,3,'133'),(15,4,'134'),(15,5,'135'),(15,6,'136'),

(16,6,'101'),(16,7,'102'),(16,8,'103'),(16,9,'104'),(16,10,'105'),(16,1,'106'),(16,2,'107'),(16,3,'108'),(16,4,'109'),(16,5,'110'),
(16,6,'111'),(16,7,'112'),(16,8,'113'),(16,9,'114'),(16,10,'115'),(16,1,'116'),(16,2,'117'),(16,3,'118'),(16,4,'119'),(16,5,'120'),
(16,6,'121'),(16,7,'122'),(16,8,'123'),(16,9,'124'),(16,10,'125'),(16,1,'126'),(16,2,'127'),(16,3,'128'),(16,4,'129'),(16,5,'130'),

(17,7,'101'),(17,8,'102'),(17,9,'103'),(17,10,'104'),(17,1,'105'),(17,2,'106'),(17,3,'107'),(17,4,'108'),
(17,7,'109'),(17,8,'110'),(17,9,'111'),(17,10,'112'),(17,1,'113'),(17,2,'114'),(17,3,'115'),(17,4,'116'),
(17,7,'117'),(17,8,'118'),(17,9,'119'),(17,10,'120'),(17,1,'121'),(17,2,'122'),(17,3,'123'),(17,4,'124'),

(18,8,'101'),(18,9,'102'),(18,10,'103'),(18,1,'104'),(18,2,'105'),
(18,8,'106'),(18,9,'107'),(18,10,'108'),(18,1,'109'),(18,2,'110'),
(18,8,'111'),(18,9,'112'),(18,10,'113'),(18,1,'114'),(18,2,'115'),

(19,9,'101'),(19,10,'102'),(19,1,'103'),(19,2,'104'),(19,3,'105'),(19,4,'106'),
(19,9,'107'),(19,10,'108'),(19,1,'109'),(19,2,'110'),(19,3,'111'),(19,4,'112'),
(19,9,'113'),(19,10,'114'),(19,1,'115'),(19,2,'116'),(19,3,'117'),(19,4,'118'),

(20,10,'101'),(20,1,'102'),(20,2,'103'),(20,3,'104'),(20,4,'105'),(20,5,'106'),(20,6,'107'),(20,7,'108'),(20,8,'109'),(20,9,'110'),(20,10,'111'),(20,1,'112'),
(20,10,'113'),(20,1,'114'),(20,2,'115'),(20,3,'116'),(20,4,'117'),(20,5,'118'),(20,6,'119'),(20,7,'120'),(20,8,'121'),(20,9,'122'),(20,10,'123'),(20,1,'124'),
(20,10,'125'),(20,1,'126'),(20,2,'127'),(20,3,'128'),(20,4,'129'),(20,5,'130'),(20,6,'131'),(20,7,'132'),(20,8,'133'),(20,9,'134'),(20,10,'135'),(20,1,'136'),

(21,1,'101'),(21,2,'102'),(21,3,'103'),(21,4,'104'),(21,5,'105'),(21,6,'106'),(21,7,'107'),(21,8,'108'),(21,9,'109'),(21,10,'110'),
(21,1,'111'),(21,2,'112'),(21,3,'113'),(21,4,'114'),(21,5,'115'),(21,6,'116'),(21,7,'117'),(21,8,'118'),(21,9,'119'),(21,10,'120'),
(21,1,'121'),(21,2,'122'),(21,3,'123'),(21,4,'124'),(21,5,'125'),(21,6,'126'),(21,7,'127'),(21,8,'128'),(21,9,'129'),(21,10,'130'),

(22,2,'101'),(22,3,'102'),(22,4,'103'),(22,5,'104'),(22,6,'105'),(22,7,'106'),(22,8,'107'),(22,9,'108'),
(22,2,'109'),(22,3,'110'),(22,4,'111'),(22,5,'112'),(22,6,'113'),(22,7,'114'),(22,8,'115'),(22,9,'116'),
(22,2,'117'),(22,3,'118'),(22,4,'119'),(22,5,'120'),(22,6,'121'),(22,7,'122'),(22,8,'123'),(22,9,'124'),

(23,3,'101'),(23,4,'102'),(23,5,'103'),(23,6,'104'),(23,7,'105'),
(23,3,'106'),(23,4,'107'),(23,5,'108'),(23,6,'109'),(23,7,'110'),
(23,3,'111'),(23,4,'112'),(23,5,'113'),(23,6,'114'),(23,7,'115'),

(24,4,'101'),(24,5,'102'),(24,6,'103'),(24,7,'104'),(24,8,'105'),(24,9,'106'),
(24,4,'107'),(24,5,'108'),(24,6,'109'),(24,7,'110'),(24,8,'111'),(24,9,'112'),
(24,4,'113'),(24,5,'114'),(24,6,'115'),(24,7,'116'),(24,8,'117'),(24,9,'118'),

(25,5,'101'),(25,6,'102'),(25,7,'103'),(25,8,'104'),(25,9,'105'),(25,10,'106'),(25,1,'107'),(25,2,'108'),(25,3,'109'),(25,4,'110'),(25,5,'111'),(25,6,'112'),
(25,5,'113'),(25,6,'114'),(25,7,'115'),(25,8,'116'),(25,9,'117'),(25,10,'118'),(25,1,'119'),(25,2,'120'),(25,3,'121'),(25,4,'122'),(25,5,'123'),(25,6,'124'),
(25,5,'125'),(25,6,'126'),(25,7,'127'),(25,8,'128'),(25,9,'129'),(25,10,'130'),(25,1,'131'),(25,2,'132'),(25,3,'133'),(25,4,'134'),(25,5,'135'),(25,6,'136'),

(26,6,'101'),(26,7,'102'),(26,8,'103'),(26,9,'104'),(26,10,'105'),(26,1,'106'),(26,2,'107'),(26,3,'108'),(26,4,'109'),(26,5,'110'),
(26,6,'111'),(26,7,'112'),(26,8,'113'),(26,9,'114'),(26,10,'115'),(26,1,'116'),(26,2,'117'),(26,3,'118'),(26,4,'119'),(26,5,'120'),
(26,6,'121'),(26,7,'122'),(26,8,'123'),(26,9,'124'),(26,10,'125'),(26,1,'126'),(26,2,'127'),(26,3,'128'),(26,4,'129'),(26,5,'130'),

(27,7,'101'),(27,8,'102'),(27,9,'103'),(27,10,'104'),(27,1,'105'),(27,2,'106'),(27,3,'107'),(27,4,'108'),
(27,7,'109'),(27,8,'110'),(27,9,'111'),(27,10,'112'),(27,1,'113'),(27,2,'114'),(27,3,'115'),(27,4,'116'),
(27,7,'117'),(27,8,'118'),(27,9,'119'),(27,10,'120'),(27,1,'121'),(27,2,'122'),(27,3,'123'),(27,4,'124'),

(28,8,'101'),(28,9,'102'),(28,10,'103'),(28,1,'104'),(28,2,'105'),
(28,8,'106'),(28,9,'107'),(28,10,'108'),(28,1,'109'),(28,2,'110'),
(28,8,'111'),(28,9,'112'),(28,10,'113'),(28,1,'114'),(28,2,'115'),

(29,9,'101'),(29,10,'102'),(29,1,'103'),(29,2,'104'),(29,3,'105'),(29,4,'106'),
(29,9,'107'),(29,10,'108'),(29,1,'109'),(29,2,'110'),(29,3,'111'),(29,4,'112'),
(29,9,'113'),(29,10,'114'),(29,1,'115'),(29,2,'116'),(29,3,'117'),(29,4,'118'),

(30,10,'101'),(30,1,'102'),(30,2,'103'),(30,3,'104'),(30,4,'105'),(30,5,'106'),(30,6,'107'),(30,7,'108'),(30,8,'109'),(30,9,'110'),(30,10,'111'),(30,1,'112'),
(30,10,'113'),(30,1,'114'),(30,2,'115'),(30,3,'116'),(30,4,'117'),(30,5,'118'),(30,6,'119'),(30,7,'120'),(30,8,'121'),(30,9,'122'),(30,10,'123'),(30,1,'124'),
(30,10,'125'),(30,1,'126'),(30,2,'127'),(30,3,'128'),(30,4,'129'),(30,5,'130'),(30,6,'131'),(30,7,'132'),(30,8,'133'),(30,9,'134'),(30,10,'135'),(30,1,'136'),

(31,1,'101'),(31,2,'102'),(31,3,'103'),(31,4,'104'),(31,5,'105'),(31,6,'106'),(31,7,'107'),(31,8,'108'),(31,9,'109'),(31,10,'110'),
(31,1,'111'),(31,2,'112'),(31,3,'113'),(31,4,'114'),(31,5,'115'),(31,6,'116'),(31,7,'117'),(31,8,'118'),(31,9,'119'),(31,10,'120'),
(31,1,'121'),(31,2,'122'),(31,3,'123'),(31,4,'124'),(31,5,'125'),(31,6,'126'),(31,7,'127'),(31,8,'128'),(31,9,'129'),(31,10,'130'),

(32,2,'101'),(32,3,'102'),(32,4,'103'),(32,5,'104'),(32,6,'105'),(32,7,'106'),(32,8,'107'),(32,9,'108'),
(32,2,'109'),(32,3,'110'),(32,4,'111'),(32,5,'112'),(32,6,'113'),(32,7,'114'),(32,8,'115'),(32,9,'116'),
(32,2,'117'),(32,3,'118'),(32,4,'119'),(32,5,'120'),(32,6,'121'),(32,7,'122'),(32,8,'123'),(32,9,'124'),

(33,3,'101'),(33,4,'102'),(33,5,'103'),(33,6,'104'),(33,7,'105'),
(33,3,'106'),(33,4,'107'),(33,5,'108'),(33,6,'109'),(33,7,'110'),
(33,3,'111'),(33,4,'112'),(33,5,'113'),(33,6,'114'),(33,7,'115'),

(34,4,'101'),(34,5,'102'),(34,6,'103'),(34,7,'104'),(34,8,'105'),(34,9,'106'),
(34,4,'107'),(34,5,'108'),(34,6,'109'),(34,7,'110'),(34,8,'111'),(34,9,'112'),
(34,4,'113'),(34,5,'114'),(34,6,'115'),(34,7,'116'),(34,8,'117'),(34,9,'118'),

(35,5,'101'),(35,6,'102'),(35,7,'103'),(35,8,'104'),(35,9,'105'),(35,10,'106'),(35,1,'107'),(35,2,'108'),(35,3,'109'),(35,4,'110'),(35,5,'111'),(35,6,'112'),
(35,5,'113'),(35,6,'114'),(35,7,'115'),(35,8,'116'),(35,9,'117'),(35,10,'118'),(35,1,'119'),(35,2,'120'),(35,3,'121'),(35,4,'122'),(35,5,'123'),(35,6,'124'),
(35,5,'125'),(35,6,'126'),(35,7,'127'),(35,8,'128'),(35,9,'129'),(35,10,'130'),(35,1,'131'),(35,2,'132'),(35,3,'133'),(35,4,'134'),(35,5,'135'),(35,6,'136'),

(36,6,'101'),(36,7,'102'),(36,8,'103'),(36,9,'104'),(36,10,'105'),(36,1,'106'),(36,2,'107'),(36,3,'108'),(36,4,'109'),(36,5,'110'),
(36,6,'111'),(36,7,'112'),(36,8,'113'),(36,9,'114'),(36,10,'115'),(36,1,'116'),(36,2,'117'),(36,3,'118'),(36,4,'119'),(36,5,'120'),
(36,6,'121'),(36,7,'122'),(36,8,'123'),(36,9,'124'),(36,10,'125'),(36,1,'126'),(36,2,'127'),(36,3,'128'),(36,4,'129'),(36,5,'130'),

(37,7,'101'),(37,8,'102'),(37,9,'103'),(37,10,'104'),(37,1,'105'),(37,2,'106'),(37,3,'107'),(37,4,'108'),
(37,7,'109'),(37,8,'110'),(37,9,'111'),(37,10,'112'),(37,1,'113'),(37,2,'114'),(37,3,'115'),(37,4,'116'),
(37,7,'117'),(37,8,'118'),(37,9,'119'),(37,10,'120'),(37,1,'121'),(37,2,'122'),(37,3,'123'),(37,4,'124'),

(38,8,'101'),(38,9,'102'),(38,10,'103'),(38,1,'104'),(38,2,'105'),
(38,8,'106'),(38,9,'107'),(38,10,'108'),(38,1,'109'),(38,2,'110'),
(38,8,'111'),(38,9,'112'),(38,10,'113'),(38,1,'114'),(38,2,'115'),

(39,9,'101'),(39,10,'102'),(39,1,'103'),(39,2,'104'),(39,3,'105'),(39,4,'106'),
(39,9,'107'),(39,10,'108'),(39,1,'109'),(39,2,'110'),(39,3,'111'),(39,4,'112'),
(39,9,'113'),(39,10,'114'),(39,1,'115'),(39,2,'116'),(39,3,'117'),(39,4,'118'),

(40,10,'101'),(40,1,'102'),(40,2,'103'),(40,3,'104'),(40,4,'105'),(40,5,'106'),(40,6,'107'),(40,7,'108'),(40,8,'109'),(40,9,'110'),(40,10,'111'),(40,1,'112'),
(40,10,'113'),(40,1,'114'),(40,2,'115'),(40,3,'116'),(40,4,'117'),(40,5,'118'),(40,6,'119'),(40,7,'120'),(40,8,'121'),(40,9,'122'),(40,10,'123'),(40,1,'124'),
(40,10,'125'),(40,1,'126'),(40,2,'127'),(40,3,'128'),(40,4,'129'),(40,5,'130'),(40,6,'131'),(40,7,'132'),(40,8,'133'),(40,9,'134'),(40,10,'135'),(40,1,'136'),

(41,1,'101'),(41,2,'102'),(41,3,'103'),(41,4,'104'),(41,5,'105'),(41,6,'106'),(41,7,'107'),(41,8,'108'),(41,9,'109'),(41,10,'110'),
(41,1,'111'),(41,2,'112'),(41,3,'113'),(41,4,'114'),(41,5,'115'),(41,6,'116'),(41,7,'117'),(41,8,'118'),(41,9,'119'),(41,10,'120'),
(41,1,'121'),(41,2,'122'),(41,3,'123'),(41,4,'124'),(41,5,'125'),(41,6,'126'),(41,7,'127'),(41,8,'128'),(41,9,'129'),(41,10,'130'),

(42,2,'101'),(42,3,'102'),(42,4,'103'),(42,5,'104'),(42,6,'105'),(42,7,'106'),(42,8,'107'),(42,9,'108'),
(42,2,'109'),(42,3,'110'),(42,4,'111'),(42,5,'112'),(42,6,'113'),(42,7,'114'),(42,8,'115'),(42,9,'116'),
(42,2,'117'),(42,3,'118'),(42,4,'119'),(42,5,'120'),(42,6,'121'),(42,7,'122'),(42,8,'123'),(42,9,'124'),

(43,3,'101'),(43,4,'102'),(43,5,'103'),(43,6,'104'),(43,7,'105'),
(43,3,'106'),(43,4,'107'),(43,5,'108'),(43,6,'109'),(43,7,'110'),
(43,3,'111'),(43,4,'112'),(43,5,'113'),(43,6,'114'),(43,7,'115'),

(44,4,'101'),(44,5,'102'),(44,6,'103'),(44,7,'104'),(44,8,'105'),(44,9,'106'),
(44,4,'107'),(44,5,'108'),(44,6,'109'),(44,7,'110'),(44,8,'111'),(44,9,'112'),
(44,4,'113'),(44,5,'114'),(44,6,'115'),(44,7,'116'),(44,8,'117'),(44,9,'118'),

(45,5,'101'),(45,6,'102'),(45,7,'103'),(45,8,'104'),(45,9,'105'),(45,10,'106'),(45,1,'107'),(45,2,'108'),(45,3,'109'),(45,4,'110'),(45,5,'111'),(45,6,'112'),
(45,5,'113'),(45,6,'114'),(45,7,'115'),(45,8,'116'),(45,9,'117'),(45,10,'118'),(45,1,'119'),(45,2,'120'),(45,3,'121'),(45,4,'122'),(45,5,'123'),(45,6,'124'),
(45,5,'125'),(45,6,'126'),(45,7,'127'),(45,8,'128'),(45,9,'129'),(45,10,'130'),(45,1,'131'),(45,2,'132'),(45,3,'133'),(45,4,'134'),(45,5,'135'),(45,6,'136'),

(46,6,'101'),(46,7,'102'),(46,8,'103'),(46,9,'104'),(46,10,'105'),(46,1,'106'),(46,2,'107'),(46,3,'108'),(46,4,'109'),(46,5,'110'),
(46,6,'111'),(46,7,'112'),(46,8,'113'),(46,9,'114'),(46,10,'115'),(46,1,'116'),(46,2,'117'),(46,3,'118'),(46,4,'119'),(46,5,'120'),
(46,6,'121'),(46,7,'122'),(46,8,'123'),(46,9,'124'),(46,10,'125'),(46,1,'126'),(46,2,'127'),(46,3,'128'),(46,4,'129'),(46,5,'130'),

(47,7,'101'),(47,8,'102'),(47,9,'103'),(47,10,'104'),(47,1,'105'),(47,2,'106'),(47,3,'107'),(47,4,'108'),
(47,7,'109'),(47,8,'110'),(47,9,'111'),(47,10,'112'),(47,1,'113'),(47,2,'114'),(47,3,'115'),(47,4,'116'),
(47,7,'117'),(47,8,'118'),(47,9,'119'),(47,10,'120'),(47,1,'121'),(47,2,'122'),(47,3,'123'),(47,4,'124'),

(48,8,'101'),(48,9,'102'),(48,10,'103'),(48,1,'104'),(48,2,'105'),
(48,8,'106'),(48,9,'107'),(48,10,'108'),(48,1,'109'),(48,2,'110'),
(48,8,'111'),(48,9,'112'),(48,10,'113'),(48,1,'114'),(48,2,'115'),

(49,9,'101'),(49,10,'102'),(49,1,'103'),(49,2,'104'),(49,3,'105'),(49,4,'106'),
(49,9,'107'),(49,10,'108'),(49,1,'109'),(49,2,'110'),(49,3,'111'),(49,4,'112'),
(49,9,'113'),(49,10,'114'),(49,1,'115'),(49,2,'116'),(49,3,'117'),(49,4,'118'),

(50,10,'101'),(50,1,'102'),(50,2,'103'),(50,3,'104'),(50,4,'105'),(50,5,'106'),(50,6,'107'),(50,7,'108'),(50,8,'109'),(50,9,'110'),(50,10,'111'),(50,1,'112'),
(50,10,'113'),(50,1,'114'),(50,2,'115'),(50,3,'116'),(50,4,'117'),(50,5,'118'),(50,6,'119'),(50,7,'120'),(50,8,'121'),(50,9,'122'),(50,10,'123'),(50,1,'124'),
(50,10,'125'),(50,1,'126'),(50,2,'127'),(50,3,'128'),(50,4,'129'),(50,5,'130'),(50,6,'131'),(50,7,'132'),(50,8,'133'),(50,9,'134'),(50,10,'135'),(50,1,'136');




INSERT INTO sistem.promo (kode_promo,deskripsi,nilai_diskon,berlaku_dari,berlaku_hingga) VALUES
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

INSERT INTO sistem.layanan_spa (id_hotel, nama_layanan, kategori, deskripsi, harga, status) VALUES
(1,'Balinese Massage','Massage','Pijat relaksasi tradisional Bali',260000,'Tersedia'),
(1,'Facial Glow','Facial','Perawatan wajah untuk kulit bersinar',210000,'Tersedia'),
(1,'Body Refresh','Body Treatment','Perawatan lulur dan penyegaran tubuh',310000,'Tersedia'),
(2,'Facial Glow','Facial','Perawatan wajah untuk kulit bersinar',220000,'Tersedia'),
(2,'Body Refresh','Body Treatment','Perawatan lulur dan penyegaran tubuh',320000,'Tersedia'),
(2,'Hydro Spa','Hydrotherapy','Terapi relaksasi menggunakan air hangat',370000,'Tersedia'),
(3,'Body Refresh','Body Treatment','Perawatan lulur dan penyegaran tubuh',330000,'Tersedia'),
(3,'Hydro Spa','Hydrotherapy','Terapi relaksasi menggunakan air hangat',380000,'Tersedia'),
(3,'Foot Reflex','Reflexology','Pijat refleksi titik saraf kaki',210000,'Tersedia'),
(4,'Hydro Spa','Hydrotherapy','Terapi relaksasi menggunakan air hangat',390000,'Tersedia'),
(4,'Foot Reflex','Reflexology','Pijat refleksi titik saraf kaki',220000,'Tersedia'),
(4,'Hair Relax','Hair Spa','Perawatan nutrisi rambut dan kulit kepala',260000,'Tersedia'),
(5,'Foot Reflex','Reflexology','Pijat refleksi titik saraf kaki',180000,'Tersedia'),
(5,'Hair Relax','Hair Spa','Perawatan nutrisi rambut dan kulit kepala',220000,'Tersedia'),
(5,'Nail Beauty','Nail Treatment','Perawatan dan periasan kuku tangan kaki',150000,'Tersedia'),
(6,'Hair Relax','Hair Spa','Perawatan nutrisi rambut dan kulit kepala',230000,'Tersedia'),
(6,'Nail Beauty','Nail Treatment','Perawatan dan periasan kuku tangan kaki',160000,'Tersedia'),
(6,'Relax Therapy','Relaxation Therapy','Terapi relaksasi penurun stres',280000,'Tersedia'),
(7,'Nail Beauty','Nail Treatment','Perawatan dan periasan kuku tangan kaki',170000,'Tersedia'),
(7,'Relax Therapy','Relaxation Therapy','Terapi relaksasi penurun stres',290000,'Tersedia'),
(7,'Couple Heaven','Couple Spa','Paket spa romantis untuk pasangan',520000,'Tersedia'),
(8,'Relax Therapy','Relaxation Therapy','Terapi relaksasi penurun stres',300000,'Tersedia'),
(8,'Couple Heaven','Couple Spa','Paket spa romantis untuk pasangan',530000,'Tersedia'),
(8,'Wellness Premium','Wellness Spa','Spa kesehatan menyeluruh premium',680000,'Tersedia'),
(9,'Couple Heaven','Couple Spa','Paket spa romantis untuk pasangan',540000,'Tersedia'),
(9,'Wellness Premium','Wellness Spa','Spa kesehatan menyeluruh premium',690000,'Tersedia'),
(9,'Balinese Massage','Massage','Pijat relaksasi tradisional Bali',290000,'Tersedia'),
(10,'Wellness Premium','Wellness Spa','Spa kesehatan menyeluruh premium',650000,'Tersedia'),
(10,'Balinese Massage','Massage','Pijat relaksasi tradisional Bali',250000,'Tersedia'),
(10,'Facial Glow','Facial','Perawatan wajah untuk kulit bersinar',200000,'Tersedia'),
(11,'Balinese Massage','Massage','Pijat relaksasi tradisional Bali',260000,'Tersedia'),
(11,'Facial Glow','Facial','Perawatan wajah untuk kulit bersinar',210000,'Tersedia'),
(11,'Body Refresh','Body Treatment','Perawatan lulur dan penyegaran tubuh',310000,'Tersedia'),
(12,'Facial Glow','Facial','Perawatan wajah untuk kulit bersinar',220000,'Tersedia'),
(12,'Body Refresh','Body Treatment','Perawatan lulur dan penyegaran tubuh',320000,'Tersedia'),
(12,'Hydro Spa','Hydrotherapy','Terapi relaksasi menggunakan air hangat',370000,'Tersedia'),
(13,'Body Refresh','Body Treatment','Perawatan lulur dan penyegaran tubuh',330000,'Tersedia'),
(13,'Hydro Spa','Hydrotherapy','Terapi relaksasi menggunakan air hangat',380000,'Tersedia'),
(13,'Foot Reflex','Reflexology','Pijat refleksi titik saraf kaki',210000,'Tersedia'),
(14,'Hydro Spa','Hydrotherapy','Terapi relaksasi menggunakan air hangat',390000,'Tersedia'),
(14,'Foot Reflex','Reflexology','Pijat refleksi titik saraf kaki',220000,'Tersedia'),
(14,'Hair Relax','Hair Spa','Perawatan nutrisi rambut dan kulit kepala',260000,'Tersedia'),
(15,'Foot Reflex','Reflexology','Pijat refleksi titik saraf kaki',180000,'Tersedia'),
(15,'Hair Relax','Hair Spa','Perawatan nutrisi rambut dan kulit kepala',220000,'Tersedia'),
(15,'Nail Beauty','Nail Treatment','Perawatan dan periasan kuku tangan kaki',150000,'Tersedia'),
(16,'Hair Relax','Hair Spa','Perawatan nutrisi rambut dan kulit kepala',230000,'Tersedia'),
(16,'Nail Beauty','Nail Treatment','Perawatan dan periasan kuku tangan kaki',160000,'Tersedia'),
(16,'Relax Therapy','Relaxation Therapy','Terapi relaksasi penurun stres',280000,'Tersedia'),
(17,'Nail Beauty','Nail Treatment','Perawatan dan periasan kuku tangan kaki',170000,'Tersedia'),
(17,'Relax Therapy','Relaxation Therapy','Terapi relaksasi penurun stres',290000,'Tersedia'),
(17,'Couple Heaven','Couple Spa','Paket spa romantis untuk pasangan',520000,'Tersedia'),
(18,'Relax Therapy','Relaxation Therapy','Terapi relaksasi penurun stres',300000,'Tersedia'),
(18,'Couple Heaven','Couple Spa','Paket spa romantis untuk pasangan',530000,'Tersedia'),
(18,'Wellness Premium','Wellness Spa','Spa kesehatan menyeluruh premium',680000,'Tersedia'),
(19,'Couple Heaven','Couple Spa','Paket spa romantis untuk pasangan',540000,'Tersedia'),
(19,'Wellness Premium','Wellness Spa','Spa kesehatan menyeluruh premium',690000,'Tersedia'),
(19,'Balinese Massage','Massage','Pijat relaksasi tradisional Bali',290000,'Tersedia'),
(20,'Wellness Premium','Wellness Spa','Spa kesehatan menyeluruh premium',650000,'Tersedia'),
(20,'Balinese Massage','Massage','Pijat relaksasi tradisional Bali',250000,'Tersedia'),
(20,'Facial Glow','Facial','Perawatan wajah untuk kulit bersinar',200000,'Tersedia'),
(21,'Balinese Massage','Massage','Pijat relaksasi tradisional Bali',260000,'Tersedia'),
(21,'Facial Glow','Facial','Perawatan wajah untuk kulit bersinar',210000,'Tersedia'),
(21,'Body Refresh','Body Treatment','Perawatan lulur dan penyegaran tubuh',310000,'Tersedia'),
(22,'Facial Glow','Facial','Perawatan wajah untuk kulit bersinar',220000,'Tersedia'),
(22,'Body Refresh','Body Treatment','Perawatan lulur dan penyegaran tubuh',320000,'Tersedia'),
(22,'Hydro Spa','Hydrotherapy','Terapi relaksasi menggunakan air hangat',370000,'Tersedia'),
(23,'Body Refresh','Body Treatment','Perawatan lulur dan penyegaran tubuh',330000,'Tersedia'),
(23,'Hydro Spa','Hydrotherapy','Terapi relaksasi menggunakan air hangat',380000,'Tersedia'),
(23,'Foot Reflex','Reflexology','Pijat refleksi titik saraf kaki',210000,'Tersedia'),
(24,'Hydro Spa','Hydrotherapy','Terapi relaksasi menggunakan air hangat',390000,'Tersedia'),
(24,'Foot Reflex','Reflexology','Pijat refleksi titik saraf kaki',220000,'Tersedia'),
(24,'Hair Relax','Hair Spa','Perawatan nutrisi rambut dan kulit kepala',260000,'Tersedia'),
(25,'Foot Reflex','Reflexology','Pijat refleksi titik saraf kaki',180000,'Tersedia'),
(25,'Hair Relax','Hair Spa','Perawatan nutrisi rambut dan kulit kepala',220000,'Tersedia'),
(25,'Nail Beauty','Nail Treatment','Perawatan dan periasan kuku tangan kaki',150000,'Tersedia'),
(26,'Hair Relax','Hair Spa','Perawatan nutrisi rambut dan kulit kepala',230000,'Tersedia'),
(26,'Nail Beauty','Nail Treatment','Perawatan dan periasan kuku tangan kaki',160000,'Tersedia'),
(26,'Relax Therapy','Relaxation Therapy','Terapi relaksasi penurun stres',280000,'Tersedia'),
(27,'Nail Beauty','Nail Treatment','Perawatan dan periasan kuku tangan kaki',170000,'Tersedia'),
(27,'Relax Therapy','Relaxation Therapy','Terapi relaksasi penurun stres',290000,'Tersedia'),
(27,'Couple Heaven','Couple Spa','Paket spa romantis untuk pasangan',520000,'Tersedia'),
(28,'Relax Therapy','Relaxation Therapy','Terapi relaksasi penurun stres',300000,'Tersedia'),
(28,'Couple Heaven','Couple Spa','Paket spa romantis untuk pasangan',530000,'Tersedia'),
(28,'Wellness Premium','Wellness Spa','Spa kesehatan menyeluruh premium',680000,'Tersedia'),
(29,'Couple Heaven','Couple Spa','Paket spa romantis untuk pasangan',540000,'Tersedia'),
(29,'Wellness Premium','Wellness Spa','Spa kesehatan menyeluruh premium',690000,'Tersedia'),
(29,'Balinese Massage','Massage','Pijat relaksasi tradisional Bali',290000,'Tersedia'),
(30,'Wellness Premium','Wellness Spa','Spa kesehatan menyeluruh premium',650000,'Tersedia'),
(30,'Balinese Massage','Massage','Pijat relaksasi tradisional Bali',250000,'Tersedia'),
(30,'Facial Glow','Facial','Perawatan wajah untuk kulit bersinar',200000,'Tersedia'),
(31,'Balinese Massage','Massage','Pijat relaksasi tradisional Bali',260000,'Tersedia'),
(31,'Facial Glow','Facial','Perawatan wajah untuk kulit bersinar',210000,'Tersedia'),
(31,'Body Refresh','Body Treatment','Perawatan lulur dan penyegaran tubuh',310000,'Tersedia'),
(32,'Facial Glow','Facial','Perawatan wajah untuk kulit bersinar',220000,'Tersedia'),
(32,'Body Refresh','Body Treatment','Perawatan lulur dan penyegaran tubuh',320000,'Tersedia'),
(32,'Hydro Spa','Hydrotherapy','Terapi relaksasi menggunakan air hangat',370000,'Tersedia'),
(33,'Body Refresh','Body Treatment','Perawatan lulur dan penyegaran tubuh',330000,'Tersedia'),
(33,'Hydro Spa','Hydrotherapy','Terapi relaksasi menggunakan air hangat',380000,'Tersedia'),
(33,'Foot Reflex','Reflexology','Pijat refleksi titik saraf kaki',210000,'Tersedia'),
(34,'Hydro Spa','Hydrotherapy','Terapi relaksasi menggunakan air hangat',390000,'Tersedia'),
(34,'Foot Reflex','Reflexology','Pijat refleksi titik saraf kaki',220000,'Tersedia'),
(34,'Hair Relax','Hair Spa','Perawatan nutrisi rambut dan kulit kepala',260000,'Tersedia'),
(35,'Foot Reflex','Reflexology','Pijat refleksi titik saraf kaki',180000,'Tersedia'),
(35,'Hair Relax','Hair Spa','Perawatan nutrisi rambut dan kulit kepala',220000,'Tersedia'),
(35,'Nail Beauty','Nail Treatment','Perawatan dan periasan kuku tangan kaki',150000,'Tersedia'),
(36,'Hair Relax','Hair Spa','Perawatan nutrisi rambut dan kulit kepala',230000,'Tersedia'),
(36,'Nail Beauty','Nail Treatment','Perawatan dan periasan kuku tangan kaki',160000,'Tersedia'),
(36,'Relax Therapy','Relaxation Therapy','Terapi relaksasi penurun stres',280000,'Tersedia'),
(37,'Nail Beauty','Nail Treatment','Perawatan dan periasan kuku tangan kaki',170000,'Tersedia'),
(37,'Relax Therapy','Relaxation Therapy','Terapi relaksasi penurun stres',290000,'Tersedia'),
(37,'Couple Heaven','Couple Spa','Paket spa romantis untuk pasangan',520000,'Tersedia'),
(38,'Relax Therapy','Relaxation Therapy','Terapi relaksasi penurun stres',300000,'Tersedia'),
(38,'Couple Heaven','Couple Spa','Paket spa romantis untuk pasangan',530000,'Tersedia'),
(38,'Wellness Premium','Wellness Spa','Spa kesehatan menyeluruh premium',680000,'Tersedia'),
(39,'Couple Heaven','Couple Spa','Paket spa romantis untuk pasangan',540000,'Tersedia'),
(39,'Wellness Premium','Wellness Spa','Spa kesehatan menyeluruh premium',690000,'Tersedia'),
(39,'Balinese Massage','Massage','Pijat relaksasi tradisional Bali',290000,'Tersedia'),
(40,'Wellness Premium','Wellness Spa','Spa kesehatan menyeluruh premium',650000,'Tersedia'),
(40,'Balinese Massage','Massage','Pijat relaksasi tradisional Bali',250000,'Tersedia'),
(40,'Facial Glow','Facial','Perawatan wajah untuk kulit bersinar',200000,'Tersedia'),
(41,'Balinese Massage','Massage','Pijat relaksasi tradisional Bali',260000,'Tersedia'),
(41,'Facial Glow','Facial','Perawatan wajah untuk kulit bersinar',210000,'Tersedia'),
(41,'Body Refresh','Body Treatment','Perawatan lulur dan penyegaran tubuh',310000,'Tersedia'),
(42,'Facial Glow','Facial','Perawatan wajah untuk kulit bersinar',220000,'Tersedia'),
(42,'Body Refresh','Body Treatment','Perawatan lulur dan penyegaran tubuh',320000,'Tersedia'),
(42,'Hydro Spa','Hydrotherapy','Terapi relaksasi menggunakan air hangat',370000,'Tersedia'),
(43,'Body Refresh','Body Treatment','Perawatan lulur dan penyegaran tubuh',330000,'Tersedia'),
(43,'Hydro Spa','Hydrotherapy','Terapi relaksasi menggunakan air hangat',380000,'Tersedia'),
(43,'Foot Reflex','Reflexology','Pijat refleksi titik saraf kaki',210000,'Tersedia'),
(44,'Hydro Spa','Hydrotherapy','Terapi relaksasi menggunakan air hangat',390000,'Tersedia'),
(44,'Foot Reflex','Reflexology','Pijat refleksi titik saraf kaki',220000,'Tersedia'),
(44,'Hair Relax','Hair Spa','Perawatan nutrisi rambut dan kulit kepala',260000,'Tersedia'),
(45,'Foot Reflex','Reflexology','Pijat refleksi titik saraf kaki',180000,'Tersedia'),
(45,'Hair Relax','Hair Spa','Perawatan nutrisi rambut dan kulit kepala',220000,'Tersedia'),
(45,'Nail Beauty','Nail Treatment','Perawatan dan periasan kuku tangan kaki',150000,'Tersedia'),
(46,'Hair Relax','Hair Spa','Perawatan nutrisi rambut dan kulit kepala',230000,'Tersedia'),
(46,'Nail Beauty','Nail Treatment','Perawatan dan periasan kuku tangan kaki',160000,'Tersedia'),
(46,'Relax Therapy','Relaxation Therapy','Terapi relaksasi penurun stres',280000,'Tersedia'),
(47,'Nail Beauty','Nail Treatment','Perawatan dan periasan kuku tangan kaki',170000,'Tersedia'),
(47,'Relax Therapy','Relaxation Therapy','Terapi relaksasi penurun stres',290000,'Tersedia'),
(47,'Couple Heaven','Couple Spa','Paket spa romantis untuk pasangan',520000,'Tersedia'),
(48,'Relax Therapy','Relaxation Therapy','Terapi relaksasi penurun stres',300000,'Tersedia'),
(48,'Couple Heaven','Couple Spa','Paket spa romantis untuk pasangan',530000,'Tersedia'),
(48,'Wellness Premium','Wellness Spa','Spa kesehatan menyeluruh premium',680000,'Tersedia'),
(49,'Couple Heaven','Couple Spa','Paket spa romantis untuk pasangan',540000,'Tersedia'),
(49,'Wellness Premium','Wellness Spa','Spa kesehatan menyeluruh premium',690000,'Tersedia'),
(49,'Balinese Massage','Massage','Pijat relaksasi tradisional Bali',290000,'Tersedia'),
(50,'Wellness Premium','Wellness Spa','Spa kesehatan menyeluruh premium',650000,'Tersedia'),
(50,'Balinese Massage','Massage','Pijat relaksasi tradisional Bali',250000,'Tersedia'),
(50,'Facial Glow','Facial','Perawatan wajah untuk kulit bersinar',200000,'Tersedia');

INSERT INTO sistem.menu_fnb (nama_item, kategori, deskripsi, harga) VALUES
('Nasi Goreng','Makanan','Nasi goreng spesial',45000),
('Mie Goreng','Makanan','Mie goreng seafood',40000),
('Steak Sapi','Makanan','Steak premium',120000),
('Ayam Bakar','Makanan','Ayam bakar madu',55000),
('Sate Ayam','Makanan','Sate ayam madura',50000),
('Burger','Makanan','Burger daging',60000),
('Pizza','Makanan','Pizza keju',90000),
('Sushi','Makanan','Sushi Jepang',100000),
('Pasta','Makanan','Pasta carbonara',75000),
('Salad','Makanan','Salad sehat',35000),
('Jus Jeruk','Minuman','Fresh orange',25000),
('Kopi Latte','Minuman','Hot latte',30000),
('Milkshake','Minuman','Chocolate milkshake',35000),
('Teh Manis','Minuman','Teh dingin',15000),
('Cappuccino','Minuman','Coffee cappuccino',32000),
('Mojito','Minuman','Fresh mojito',40000),
('Air Mineral','Minuman','Botol mineral',10000),
('Smoothie','Minuman','Fruit smoothie',45000),
('Green Tea','Minuman','Japanese tea',28000),
('Soda','Minuman','Soft drink',20000);


-- views untuk memudahkan query
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

select * from sistem.menu_fnb;

select * from sistem.pemesanan_fnb;


-- TRIGGER & FUNCTION
-- =========================================================

-- 1. Validasi Tanggal Reservasi
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
BEFORE INSERT OR UPDATE ON sistem.reservasi
FOR EACH ROW EXECUTE FUNCTION sistem.validasi_tanggal_reservasi();


-- 2. Cegah Bentrok Tanggal (Menggantikan sistem Stok Kamar)
CREATE OR REPLACE FUNCTION sistem.cegah_bentrok_tanggal()
RETURNS TRIGGER AS $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM sistem.reservasi
        WHERE id_kamar = NEW.id_kamar
        AND status_reservasi IN ('dikonfirmasi', 'check_in')
        AND id_reservasi IS DISTINCT FROM NEW.id_reservasi -- Abaikan baris yang sedang diupdate
        AND (NEW.masuk_kamar < keluar_kamar AND NEW.keluar_kamar > masuk_kamar)
    ) THEN
        RAISE EXCEPTION 'Kamar pada tanggal tersebut sudah dipesan. Silakan pilih tanggal atau kamar lain.';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_cegah_bentrok_tanggal
BEFORE INSERT OR UPDATE ON sistem.reservasi
FOR EACH ROW EXECUTE FUNCTION sistem.cegah_bentrok_tanggal();


-- 3. Hitung Total Reservasi (Diperbaiki perhitungan tipe datanya)
CREATE OR REPLACE FUNCTION sistem.hitung_total_reservasi()
RETURNS TRIGGER AS $$
DECLARE
    harga_kamar DECIMAL(12,0);
    jumlah_malam INT;
    diskon DECIMAL(5,2) DEFAULT 0;
BEGIN
    SELECT tk.harga INTO harga_kamar
    FROM sistem.kamar k
    JOIN sistem.tipe_kamar tk ON k.id_tipe = tk.id_tipe
    WHERE k.id_kamar = NEW.id_kamar;

    jumlah_malam := NEW.keluar_kamar - NEW.masuk_kamar;

    IF NEW.id_promo IS NOT NULL THEN
        SELECT nilai_diskon INTO diskon
        FROM sistem.promo
        WHERE id_promo = NEW.id_promo;
    END IF;

    NEW.harga_total := (harga_kamar * jumlah_malam) - ((harga_kamar * jumlah_malam) * (diskon / 100.0));
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_hitung_total_reservasi
BEFORE INSERT ON sistem.reservasi
FOR EACH ROW EXECUTE FUNCTION sistem.hitung_total_reservasi();


-- 4. Validasi Promo
CREATE OR REPLACE FUNCTION sistem.validasi_promo()
RETURNS TRIGGER AS $$
DECLARE
    mulai_promo DATE;
    akhir_promo DATE;
BEGIN
    IF NEW.id_promo IS NOT NULL THEN
        SELECT berlaku_dari, berlaku_hingga INTO mulai_promo, akhir_promo
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
BEFORE INSERT ON sistem.reservasi
FOR EACH ROW EXECUTE FUNCTION sistem.validasi_promo();


-- 5. Validasi Ulasan (Diperbaiki: tidak perlu cek id_hotel & id_pelanggan secara eksplisit)
CREATE OR REPLACE FUNCTION sistem.validasi_ulasan()
RETURNS TRIGGER AS $$
DECLARE
    status_booking sistem.status_pemesanan;
BEGIN
    SELECT status_reservasi INTO status_booking
    FROM sistem.reservasi
    WHERE id_reservasi = NEW.id_reservasi;

    IF status_booking <> 'check_out' THEN
        RAISE EXCEPTION 'Ulasan hanya bisa dibuat setelah check-out';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_validasi_ulasan
BEFORE INSERT ON sistem.ulasan
FOR EACH ROW EXECUTE FUNCTION sistem.validasi_ulasan();


-- 6. Update Rating Hotel (Diperbaiki: mengambil id_hotel dari JOIN reservasi)
CREATE OR REPLACE FUNCTION sistem.update_rating_hotel()
RETURNS TRIGGER AS $$
DECLARE
    target_hotel INT;
    rata_rating DECIMAL(2,1);
    ref_reservasi INT;
BEGIN
    -- Mengambil id_reservasi berdasarkan operasi
    IF TG_OP = 'DELETE' THEN
        ref_reservasi := OLD.id_reservasi;
    ELSE
        ref_reservasi := NEW.id_reservasi;
    END IF;

    -- Dapatkan ID Hotel dari relasi reservasi -> kamar
    SELECT k.id_hotel INTO target_hotel
    FROM sistem.reservasi r
    JOIN sistem.kamar k ON r.id_kamar = k.id_kamar
    WHERE r.id_reservasi = ref_reservasi;

    -- Hitung ulang rata-rata rating
    SELECT ROUND(AVG(u.rating), 1) INTO rata_rating
    FROM sistem.ulasan u
    JOIN sistem.reservasi r ON u.id_reservasi = r.id_reservasi
    JOIN sistem.kamar k ON r.id_kamar = k.id_kamar
    WHERE k.id_hotel = target_hotel;

    -- Update ke tabel hotel
    UPDATE sistem.hotel
    SET rating = COALESCE(rata_rating, 0)
    WHERE id_hotel = target_hotel;

    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_update_rating_hotel
AFTER INSERT OR UPDATE OR DELETE ON sistem.ulasan
FOR EACH ROW EXECUTE FUNCTION sistem.update_rating_hotel();


-- 7. Validasi dan Kalkulasi SPA & FNB
CREATE OR REPLACE FUNCTION sistem.validasi_spa_booking()
RETURNS TRIGGER AS $$
DECLARE
    status_booking sistem.status_pemesanan;
BEGIN
    SELECT status_reservasi INTO status_booking
    FROM sistem.reservasi WHERE id_reservasi = NEW.id_reservasi;

    IF status_booking <> 'check_in' THEN
        RAISE EXCEPTION 'Spa hanya bisa dipesan saat check-in';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_validasi_spa
BEFORE INSERT ON sistem.pemesanan_spa
FOR EACH ROW EXECUTE FUNCTION sistem.validasi_spa_booking();

CREATE OR REPLACE FUNCTION sistem.hitung_total_spa()
RETURNS TRIGGER AS $$
DECLARE
    harga_spa DECIMAL(12,0);
BEGIN
    SELECT harga INTO harga_spa
    FROM sistem.layanan_spa WHERE id_layanan_spa = NEW.id_layanan_spa;

    NEW.total_harga := harga_spa * NEW.jumlah_orang;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_total_spa
BEFORE INSERT ON sistem.pemesanan_spa
FOR EACH ROW EXECUTE FUNCTION sistem.hitung_total_spa();

CREATE OR REPLACE FUNCTION sistem.cek_fnb_tersedia()
RETURNS TRIGGER AS $$
DECLARE
    tersedia_item BOOLEAN;
BEGIN
    SELECT tersedia INTO tersedia_item
    FROM sistem.menu_fnb WHERE id_menu = NEW.id_menu;

    IF tersedia_item = FALSE THEN
        RAISE EXCEPTION 'Menu tidak tersedia';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_cek_fnb
BEFORE INSERT ON sistem.pemesanan_fnb
FOR EACH ROW EXECUTE FUNCTION sistem.cek_fnb_tersedia();

CREATE OR REPLACE FUNCTION sistem.hitung_total_fnb()
RETURNS TRIGGER AS $$
BEGIN
    NEW.total_harga := NEW.harga * NEW.jumlah;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_total_fnb
BEFORE INSERT ON sistem.pemesanan_fnb
FOR EACH ROW EXECUTE FUNCTION sistem.hitung_total_fnb();

-- 8. Auto Timestamp Pembayaran
CREATE OR REPLACE FUNCTION sistem.auto_timestamp_pembayaran()
RETURNS TRIGGER AS $$
BEGIN
    NEW.tanggal_pembayaran := CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_auto_timestamp_pembayaran
BEFORE INSERT ON sistem.pembayaran
FOR EACH ROW EXECUTE FUNCTION sistem.auto_timestamp_pembayaran();



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
-- VIEWS
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
JOIN sistem.pelanggan p ON r.id_pelanggan = p.id_pelanggan
JOIN sistem.kamar k ON r.id_kamar = k.id_kamar
JOIN sistem.hotel h ON k.id_hotel = h.id_hotel
JOIN sistem.tipe_kamar tk ON k.id_tipe = tk.id_tipe;

CREATE VIEW sistem.v_detail_pembayaran AS
SELECT
    pb.id_pembayaran,
    pl.nama_pelanggan,
    h.nama_hotel,
    r.harga_total,
    pb.metode_pembayaran,
    pb.tanggal_pembayaran
FROM sistem.pembayaran pb
JOIN sistem.reservasi r ON pb.id_reservasi = r.id_reservasi
JOIN sistem.pelanggan pl ON r.id_pelanggan = pl.id_pelanggan
JOIN sistem.kamar k ON r.id_kamar = k.id_kamar
JOIN sistem.hotel h ON k.id_hotel = h.id_hotel;

CREATE VIEW sistem.v_layanan_user AS
SELECT
    r.id_reservasi,
    p.nama_pelanggan,
    ls.nama_layanan,
    ps.total_harga,
    ps.status
FROM sistem.pemesanan_spa ps
JOIN sistem.reservasi r ON ps.id_reservasi = r.id_reservasi
JOIN sistem.pelanggan p ON r.id_pelanggan = p.id_pelanggan
JOIN sistem.layanan_spa ls ON ps.id_layanan_spa = ls.id_layanan_spa;

CREATE VIEW sistem.v_jadwal_kamar_terpakai AS
SELECT 
    h.nama_hotel,
    tk.nama AS tipe_kamar,
    k.nomor_kamar,
    r.id_reservasi,
    p.nama_pelanggan,
    r.masuk_kamar,
    r.keluar_kamar,
    r.status_reservasi
FROM sistem.kamar k
JOIN sistem.hotel h ON k.id_hotel = h.id_hotel
JOIN sistem.tipe_kamar tk ON k.id_tipe = tk.id_tipe
JOIN sistem.reservasi r ON k.id_kamar = r.id_kamar
JOIN sistem.pelanggan p ON r.id_pelanggan = p.id_pelanggan
WHERE r.status_reservasi IN ('dikonfirmasi', 'check_in')
ORDER BY h.nama_hotel, k.nomor_kamar, r.masuk_kamar;