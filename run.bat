@echo off
title Running Hotel Booking Application
cls

echo =======================================================
echo     MENGKOMPILASI KODE JAVA (COMPILE)...
echo =======================================================
:: Membuat folder bin jika belum ada
if not exist bin mkdir bin

:: Proses compile seluruh file java di dalam src
javac -d bin -cp "lib\postgresql-42.7.11.jar" src\hotel\*.java src\hotel\database\*.java src\hotel\model\*.java src\hotel\service\*.java src\hotel\ui\*.java

:: Cek apakah compile berhasil
if %errorlevel% neq 0 (
    echo.
    echo [ERROR] Gagal melakukan kompilasi! Periksa kembali kode Java Anda.
    pause
    exit /b %errorlevel%
)

echo [SUKSES] Kompilasi berhasil selesai!
echo.
echo =======================================================
echo     MENJALANKAN APLIKASI HOTELBOOKING (RUN)...
echo =======================================================
:: Menjalankan program utama
java -cp "bin;lib\postgresql-42.7.11.jar" hotel.Main

echo.
echo =======================================================
echo     Aplikasi telah ditutup.
echo =======================================================
pause