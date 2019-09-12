# Tubes Algeo

Anggota Kelompok:
- Florencia Wijaya - 13518020
- Jonathan Yudi Gunawan - 13518084
- Felicia Gojali - 13518101

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
1. Sistem Persamaaan Linie r
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
