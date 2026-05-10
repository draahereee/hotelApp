-- sistem pemesanan hotel secara online
create database hotelApp;

create schema sistem;

create table sistem.pelanggan (
    id_pelanggan serial primary key,
    nama_pelanggan varchar(50) not null,
    email varchar(50) unique not null,
    no_hp varchar(20) unique not null,
    dibuat_pada timestamp default current_timestamp
);

create table sistem.hotel (
    id_hotel serial primary key,
    nama_hotel varchar(50) not null,
    lokasi_hotel varchar(50) not null,
    rating decimal(2,1),
    deskripsi text
);

create table sistem.tipe_kamar (
   	id_tipe serial primary key,
    nama varchar(50),
    harga decimal(10,2),
    kapasitas int,
    deskripsi text
);

create table sistem.kamar (
    id_kamar serial primary key,
    id_hotel int not null,
    id_tipe int not null,
    stok int not null,
    foreign key(id_hotel)
	references sistem.hotel(id_hotel),
	foreign key(id_tipe)
	references sistem.tipe_kamar(id_tipe)
);

create type sistem.status_pemesanan as enum
('menunggu', 'dikonfirmasi', 'check_in', 'check_out', 'dibatalkan');

create table sistem.reservasi (
    id_reservasi serial primary key,
    id_pelanggan int not null,
    id_kamar int not null,
    masuk_kamar date not null,
    keluar_kamar date not null,
    harga_total decimal(10,2) not null,
    status_reservasi sistem.status_pemesanan not null
	default 'menunggu',
    dibuat_pada timestamp default current_timestamp,
    foreign key (id_pelanggan)
	references sistem.pelanggan(id_pelanggan),
    foreign key (id_kamar)
	references sistem.kamar(id_kamar)
);

create table sistem.layanan (
    id_layanan serial primary key,
    nama_layanan varchar(50) not null,
    harga decimal (10,2) not null
); 

create table sistem.layanan_reservasi (
    id_layanan_reservasi serial primary key,
    id_reservasi int not null,
    id_layanan int not null,
    jumlah int not null,
    subtotal decimal(10,2) not null,
    foreign key (id_reservasi)
	references sistem.reservasi(id_reservasi),
    foreign key (id_layanan)
	references sistem.layanan(id_layanan)
);

create type sistem.cara_bayar as enum
('cash', 'e_wallet');

create type sistem.status_bayar as enum
('menunggu', 'berhasil', 'gagal', 'dikembalikan');

create table sistem.pembayaran (
    id_pembayaran serial primary key,
    id_reservasi int not null,
    tanggal_pembayaran timestamp default current_timestamp,
    uang_dibayarkan decimal(10,2) not null,
    metode_pembayaran sistem.cara_bayar not null,
    status_pembayaran sistem.status_bayar not null
	default 'menunggu',
    foreign key (id_reservasi)
	references sistem.reservasi(id_reservasi)
);

create table sistem.ulasan (
    id_ulasan serial primary key,
    id_reservasi int not null,
    id_pelanggan int not null,
    id_hotel int not null,
    rating decimal(2,1) check (rating between 1.0 and 5.0),
    komentar text,
    dibuat_pada timestamp default current_timestamp,
    foreign key (id_reservasi) references sistem.reservasi(id_reservasi),
    foreign key (id_pelanggan) references sistem.pelanggan(id_pelanggan),
    foreign key (id_hotel) references sistem.hotel(id_hotel)
);

create type sistem.tipe_diskon as enum
('persentase', 'nominal');

create table sistem.promo (
    id_promo serial primary key,
    kode_promo varchar(20) unique not null,
    deskripsi text,
    tipe_diskon sistem.tipe_diskon not null,
    nilai_diskon decimal(10,2) not null,
    min_pemesanan decimal(10,2) default 0,
    maks_diskon decimal(10,2),
    berlaku_dari date not null,
    berlaku_hingga date not null,
    dibuat_pada timestamp default current_timestamp
);

alter table sistem.reservasi
add column id_promo int,
add foreign key (id_promo) references sistem.promo(id_promo);

select*from sistem.pelanggan;
select*from sistem.hotel;
select*from sistem.tipe_kamar;
select*from sistem.reservasi;
select*from sistem.layanan;
select*from sistem.layanan_reservasi;
select*from sistem.pembayaran;
select*from sistem.kamar;
select*from sistem.ulasan;
select*from sistem.promo;

-- HOTEL
INSERT INTO sistem.hotel (nama_hotel, lokasi_hotel, rating, deskripsi) 
VALUES
('Grand Medan Hotel','Medan',4.5,'Hotel pusat kota dekat mall'),
('Danau Toba Resort','Parapat',4.7,'View langsung ke Danau Toba'),
('Jakarta Business Hotel','Jakarta',4.3,'Cocok untuk perjalanan bisnis'),
('Bali Paradise','Bali',4.8,'Resort mewah dekat pantai'),
('Bandung Cozy Stay','Bandung',4.2,'Suasana dingin dan nyaman');

-- TIPE KAMAR
INSERT INTO sistem.tipe_kamar (nama, harga, kapasitas, deskripsi) 
VALUES
('Standard',300000,2,'Kamar standar'),
('Deluxe',500000,2,'Lebih luas dan nyaman'),
('Suite',800000,4,'Kamar premium dengan ruang tamu'),
('Family Room',900000,5,'Untuk keluarga besar'),
('Single Room',200000,1,'Untuk satu orang');

-- KAMAR
INSERT INTO sistem.kamar (id_hotel, id_tipe, stok) 
VALUES
(1,1,10),
(1,2,5),
(2,3,3),
(2,4,2),
(3,2,7),
(3,1,8),
(4,3,4),
(4,4,3),
(5,1,6),
(5,5,10);

-- PROMO
INSERT INTO sistem.promo
(kode_promo, deskripsi, tipe_diskon, nilai_diskon, min_pemesanan, maks_diskon, berlaku_dari, berlaku_hingga)
VALUES
('DISC10','Diskon 10%','persentase',10,0,50000,'2024-01-01','2026-12-31'),
('HEMAT50','Potongan 50rb','nominal',50000,200000,null,'2024-01-01','2026-12-31'),
('BIGSALE','Diskon 20% max 100rb','persentase',20,0,100000,'2024-01-01','2026-12-31'),
('WELCOME','Diskon member baru','nominal',30000,0,null,'2024-01-01','2026-12-31');

-- LAYANAN 
INSERT INTO sistem.layanan (nama_layanan, harga) 
VALUES
('Sarapan',50000),
('Laundry',30000),
('Antar Jemput Bandara',150000),
('Spa',200000),
('Extra Bed',100000),
('Makan Malam',80000);