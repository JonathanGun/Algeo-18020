# Tubes Algeo

Anggota Kelompok:
- Florencia Wijaya - 13518020
- Jonathan Yudi Gunawan - 13518084
- Felicia Gojali - 13518101

## Struktur Folder
```
root
 ├─ bin
 |   ├─ Main.class
 |   ├─ Matrix.class
 |   ├─ MatrixSPL.class
 |   └─ MatrixSquare.class
 ├─ src
 |   ├─ Main.java
 |   ├─ Matrix.java
 |   ├─ MatrixSPL.java
 |   └─ MatrixSquare.java
 ├─ test
 |   ├─ spl1.txt
 |   ├─ spl1_sol.txt
 |   ├─ sqmat1.txt
 |   └─ sqmat1_sol.txt
 └─ doc
     ├─ laporan.docx
     └─ README.md
```

## Requirements
1. [Java SDK 12](https://www.oracle.com/technetwork/java/javase/downloads/jdk12-downloads-5295953.html)

## Progress Tracker
### Main.java
  - [x] Daftar Menu
  - [x] getSPLMethod()
  - [x] getDetMethod()
  - [x] getInvMethod()
  
 ### Matrix.java
  - [x] Constructor
  - [x] Selector
  - [x] Setter
  - [x] I/O
  - [x] Elementary Row Operation
  
 ### MatrixSPL.java
  - [x] Gauss
  - [x] Gauss-Jordan
  - [ ] Metode Invers
  - [ ] Kaidah Cramer
  - [x] Solusi Tunggal
  - [ ] Tidak ada solusi
  - [ ] Solusi banyak (bentuk parametrik)
  - [x] Interpolasi
  
 ### MatrixSquare.java
  - [x] Determinan(Cramer)
  - [x] Determinan(GaussJordan/REF)
  - [x] Determinan(Gauss)
  - [x] Invers(Adjoin/Cramer)
  - [ ] Invers(GaussJordan/OBE)
  - [ ] Cofactor
  - [ ] Adjoin
  
 ### File Testing + Kunci
  - [ ] SPL solusi tunggal (1/10)
  - [ ] SPL solusi banyak (0/10)
  - [ ] SPL tanpa solusi (0/10)
  - [ ] Determinan (0/10)
  - [ ] Matriks invers (0/10)
  - [ ] Adjoin (0/10)
  - [ ] Interpolasi (0/10)
  
 ### [Laporan](https://docs.google.com/document/d/1cTYE0Pc5u0voAtmANKqlEoI5vR4JmfMVTUiB3ySZthE/edit?usp=sharing)
  - [ ] Cover
  - [ ] Bab 1 : deskripsi masalah
  - [ ] Bab 2: Teori singkat mengenai metode eliminasi Gauss, metode eliminasi Gauss-Jordan, deteminan, matriks balikan, matriks kofaktor, matriks adjoin, kaidah Cramer, interpolasi polinom.
  - [ ] Bab 3: Implementasi program dalam Java, meliputi struktur class yang didefinisikan (atribut dan method), garis besar program, dll.
  - [ ] Bab 4: Eksperimen. Bab ini beris hasil eksekusi program terhadap contoh-contoh kasus yang diberikan berikut analisis hasil eksekusi tersebut
  - [ ] Bab 5: Kesimpulan, saran, dan refleksi (hasil yang dicapai, saran pengembangan, dan refleksi anda terhadap tugas ini).
  - [ ] Referensi (buku, web), yang dipakai/diacu di dalam Daftar Referensi.
 
 ### Lainnya
  - [x] Input from file
  - [ ] Output to file
  - [ ] GUI (opsional)

## Spesifikasi Tugas
Buatlah program dalam Bahasa Java untuk
1. Menghitung solusi SPL dengan metode eliminasi metode eliminasi Gauss, metode Eliminasi Gauss-Jordan, metode matriks balikan, dan kaidah Cramer (kaidah Cramer khusus untuk SPL dengan n pebuah dan n persamaan).
2. Menyelesaikan persoalan interpolasi.
3. Menghitung determinan matriks dengan berbagai cara yang disebutkan di atas, matriks kofaktor, dan matriks adjoin dari sebuah matriks n  n.

Spesifikasi program adalah sebagai berikut:
1. Program dapat menerima masukan (input) baik dari keyboard maupun membaca masukan dari file text. Untuk SPL, masukan dari keyboard adalah m, n, koefisien aij , dan bi. Masukan dari file berbentuk matriks augmented tanpa tanda kurung, setiap elemen matriks dipisah oleh spasi. Misalnya,
```
3 4.5 2.8 10 12
-3 7 8.3 11 -4
0.5 -10 -9 12 0
```

2. Untuk persoalan m
enghitung determinan dan matriks balikan, masukan dari keyboard adalah n dan koefisien aij. Masukan dari file berbentuk matriks, setiap elemen matriks dipisah oleh spasi. Misalnya,
```
3 4.5 2.8 10
-3 7 8.3 11
0.5 -10 -9 12
```

3. Untuk persoalan interpolasi, masukannya jika dari keyboard adalah n, (x0, y0), (x1, y1), ..., (xn, yn), dan nilai x yang akan ditaksir nilai fungsinya. Jika masukanya dari file, maka titik-titik dinyatakan pada setiap baris tanpa koma dan tanda kurung. Misalnya jika titik-titik datanya adalah (8.0, 2.0794), (9.0, 2.1972), dan (9.5, 2.2513), maka di dalam file text ditulis sebagai berikut:
```
8.0 2.0794
9.0 2.1972
9.5 2.2513
```

3. Untup persoalan SPL, luaran (output) program adalah solusi SPL. Jika solusinya tunggal, tuliskan nilainya. Jika solusinya tidak ada, tuliskan solusi tidak ada, jika solusinya banyak, maka tuliskan solusinya dalam bentuk parametrik (misalnya x4 = -2, x3 = 2s – t, x2 = s, dan x1 = t.)
4. Untuk persoalan determinan, matriks balikan, matriks kofator, dan adjoin, maka luarannya sesuai dengan persoalan masing-masing
5. Untuk persoalan polinom interpolasi, luarannya adalah persamaan polinom dan taksiran nilai fungsi pada x yang diberikan.
6. Luaran program harus dapat ditampilkan pada layar komputer dan dapat disimpan ke dalam file.
7. Bahasa program yang digunakan adalah Java.
8. Program tidak harus berbasis GUI, cukup text-based saja, namun boleh menggunakan GUI (memakai kakas Eclipse misalnya).
9. Program dapat dibuat dengan pilihan menu. Urutan menu dan isinya dipersilakan ditrancang masing-masing. Misalnya, menu:
```
MENU
1. Sistem Persamaaan Linier
2. Determinan
3. Matriks balikan
4. Matriks kofaktor
5. Adjoin
6. Interpolasi Polinom
7. Keluar
```

Untuk pilihan menu nomor 1 ada sub-menu lagi yaitu pilihan metode:

a. Metode elim inasi Gauss

b. Metode eliminasi Gauss -Jordan

c. Metode matriks balikan

d. Kaidah Cramer

Begitu juga untuk pilihan menu nomor 2 dan 3.

10. Sebagai pembanding, bandingkan solusi program anda dengan hasil dari Wolfram Alpha.
