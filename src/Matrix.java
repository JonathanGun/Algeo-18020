import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileWriter;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.text.DecimalFormat;

/**
* Class Matrix untuk menghitung solusi SPL, determinan, dan
* operasi lain terhadap matriks
*
* @author   Jonathan Yudi Gunawan, Felicia Gojali, Florencia Wijaya
* @version  1.0
* @since    2019-09-26
*/
public class Matrix{
    /**
    * array 2 dimensi bertipe BigDecimal untuk menyimpan elemen matriks
    */
    private BigDecimal[][] tabInt;

    /**
    * jumlah baris matriks
    */
    private int rows;

    /**
    * jumlah kolom matriks
    */
    private int cols;

    /**
    * konstanta pengali matriks
    */
    private BigDecimal scalar;

    /**
    * apakah kolom ke-indeks_array merupakan free variable
    */
    private boolean[] freeVar;

    /**
    * apakah matriks merupakan matriks interpolasi
    */
    private boolean isInterpolationMatrix;

    /**
    * solusi yang sudah siap ditampilkan ke layar
    */
    private String[] solution;

    /**
    * variabel untuk menerima input, baik dari keyboard ataupun dari file
    */
    private Scanner input = new Scanner(System.in);

    /**
    * konstanta output dari keyboard, digunakan untuk mengembalikan output ke layar
    */
    private final PrintStream stdout = System.out;

    /**
    * untuk menyimpan objek file untuk output
    */
    private PrintStream fileout;

    /**
    * konstanta presisi program (jumlah angka penting)
    */
    private final int precision = 100;

    /**
    * jumlah angka penting untuk perhitungan matematika
    */
    private final MathContext mc = new MathContext(precision);

    /**
    * konstanta epsilon
    */
    private final BigDecimal EPS = BigDecimal.valueOf(1e-40);

    // METHODS :
    // (27)  Kelompok primitif - constructor, selector/getter, setter
    // (116) Kelompok I/O - input(matriks, interpolasi), output(matriks, interpolasi, file)
    // (287) Elementary Row Operation - swap, scale, add
    // (313) Kelompok Cek Properti Matriks - isSq, hasSolution, rowValid, rowZero
    // (342) Kelompok Manipulasi Matriks - reduce, transpose, mult, duplicate
    // (401) Kelompok SPL - gauss, gaussJordan, inverse, cramer
    // (480) Kelompok Eliminasi SPL - gaussElim, jordanElim, gaussJordanElim, getSPLsolution, constructSolutionMatrix, constructSolutionString
    // (606) Kelompok Interpolasi - interpolasi, estimaasi nilai Y
    // (643) Kelompok Matriks Bujur Sangkar (NxN) - invers(gaussJordan, Cramer), determinan(gaussJordan, Cramer), Cofactor, Adjoin
    // (1000) Kelompok fungsi pembantu lainnya

    // =========================================== Kelompok Primitif ========================================== //
    // Constructor
    /**
    * Konstruktor
    *
    * @param    r jumlah baris
    * @param    c jumlah kolom
    */
    public Matrix(int r, int c){
        this.reset(r, c);
    }

    /**
    * "Konstruktor"
    *
    * @param    r jumlah baris
    * @param    c jumlah kolom
    */
    private void reset(int r, int c){
        this.rows   = r;
        this.cols   = c;
        this.scalar = BigDecimal.ONE;
        this.tabInt = new BigDecimal[this.rows+5][this.cols+5];
        this.freeVar  = new boolean[this.cols+5];
        this.solution = new String[this.cols+5];
    }

    // Selector / Getter
    /**
    * Untuk mendapat elemen matriks baris r kolom c
    *
    * @param    r jumlah baris
    * @param    c jumlah kolom
    * @return   elemen matriks baris r kolom c
    */
    private BigDecimal getElmt(int r, int c){
        return this.tabInt[r][c];
    }

    /**
    * Konstruktor
    *
    * @return   matriks koefisien dari matriks augmentednya
    */
    private Matrix getCoeffMatrix(){
        Matrix m = this.duplicateMatrix();
        m.cols--;
        return m;
    }

    /**
    * mendapat dimensi inputan sebuah file
    */
    private void getDimension(){
        this.rows = 1;
        this.cols = this.input.nextLine().split(" ").length;
        while(this.input.hasNextLine()){
            this.input.nextLine();
            this.rows += 1;
        }
    }

    /**
    * @param    r baris
    * @return   kolom terakhir dari sebuah matriks augmented
    */
    private Matrix getLastCol(){
        Matrix m = new Matrix(this.rows, 1);
        for(int r = 1; r <= this.rows; r++){
            m.setElmt(r, 1, this.getElmt(r, this.cols));
        }
        return m;
    }

    /**
    * @return   indeks pertama yang elemennya bukan 0
    */
    private int idxNotZero(int r){
        for(int c = 1; c <= this.cols; c++){
            if(this.getElmt(r, c).compareTo(BigDecimal.ZERO) != 0) return c;
        }
        return this.cols+1;
    }

    /**
     * @return digunakan untuk mengisi array of boolean bernama freeVar
     */
    private void getFreeVar(){
        // set semua jadi true dulu
        for(int c = this.cols-1; c >= 1; c--) this.freeVar[c] = true;

        // kalau ada yg bukan freevar baru diset jadi false
        for(int r = this.rows; r >= 1; r--){
            if (this.idxNotZero(r) < this.cols) this.freeVar[r] = false;
        }
    }

    /**
     * 
     * @return digunakan untuk mengembalikan nilai x maksimal dari titik interpolasi
     */
    private BigDecimal maxXInterpolation() {
        BigDecimal max = this.getElmt(1,2);
        for(int r = 1; r <= this.rows; r++) {
            max = max.max(getElmt(r, 2));
        }
        return max;
    }

    /**
     * 
     * @return digunakan untuk mengembalikan nilai x minimum dari titik interpolasi
     */
    private BigDecimal minXInterpolation() {
        BigDecimal min = this.getElmt(1,2);
        for(int r = 1; r <= this.rows; r++) {
            min = min.min(getElmt(r, 2));
        }
        return min;
    }

    // Setter
    /**
     * Untuk mengeset elemen di baris dan kolom tertentu
     * 
     * @param r baris
     * @param c kolom
     * @param x elemen yang ingin diset
     */
    private void setElmt(int r, int c, BigDecimal x){
        this.tabInt[r][c] = x;
    }

    /**
     * Untuk mengset semua elemen pada matriks menjadi nol
     */
    private void setZero(){
        for(int r = 1; r <= this.rows; r++){
            for(int c = 1; c <= this.cols; c++){
                this.setElmt(r, c, BigDecimal.ZERO);
            }
        }
    }

    // ============================================ Kelompok I/O ============================================ //
    // == INPUT == //
    // Matriks
    /**
     * Untuk menerima input elemen
     */
    private void inputElements(){
        for(int r = 1; r <= this.rows; r++) {
            for(int c = 1; c <= this.cols; c++) {
                this.setElmt(r, c, this.input.nextBigDecimal());
            }
        }
    }

    /**
     * 
     * @param isSq mengecek apakah matriks yang dimasukkan adalah matriks bujur sangka
     * Untuk memasukkan matriks 
     */
    public void inputMatrix(boolean isSq){
        if (!this.isInterpolationMatrix && ((this.isSquareMatrix() && isSq) || !isSq) && this.rows != 0) {
            System.out.printf("Tersimpan matriks sebelumnya:\n");
            this.print();
            System.out.printf("Apakah anda ingin input matriks baru?(0/1)\n");
            if (this.input.nextInt() == 0) return;
        }
        isInterpolationMatrix = false;
        System.out.printf("Input dari file(0)/keyboard(1)?\n");
        int useKeyboard = this.input.nextInt();
        
        if (useKeyboard == 0){
            System.out.printf("Masukkan nama file+extension: ");
            String fileName = this.input.next();
            try{
                this.inputMatrixFromFile(fileName, isSq);
            } catch (FileNotFoundException e){
                System.out.printf("File tidak ditemukan! Input akan dilakukan melalui keyboard!\n");
                useKeyboard = 1;
            } catch (NotSquareMatrixException e){
                System.out.printf("Matriks dalam file tidak persegi! Input akan dilakukan melalui keyboard\n");
                useKeyboard = 1;
            }
        }

        if (useKeyboard == 1){
            if (isSq){
                System.out.printf("Masukkan ordo matriks: ");
                this.rows = this.input.nextInt();
                this.cols = this.rows+1;
            } else {
                System.out.printf("Masukkan jumlah baris: "); this.rows = this.input.nextInt();
                System.out.printf("Masukkan jumlah kolom: "); this.cols = this.input.nextInt()+1;    
            }
            this.reset(this.rows, this.cols);
            this.inputElements();
        }
    }

    // File
    /**
     * 
     * @param fileName nama file yang ingin dimasukkan output-nya
     * @param isSq mengecek apakah matriksnya berbentuk bujur sangkar
     * @throws NotSquareMatrixException menandakan bahwa bukan matriks bujur sangkar
     * @throws FileNotFoundException menandakan bahwa belum ada file dengan nama yang dimasukkan
     */
    public void inputMatrixFromFile(String fileName, boolean isSq) throws NotSquareMatrixException, FileNotFoundException{
        File inputFile = new File("../test/" + fileName);
        try{
            input = new Scanner(inputFile);
        } catch (FileNotFoundException e){
            throw e;
        }
        this.getDimension();
        if(isSq && !this.isSquareMatrix()) throw new NotSquareMatrixException("Please input Square Matrix");

        input = new Scanner(inputFile);
        this.reset(this.rows, this.cols);
        this.inputElements();

        input = new Scanner(System.in);
    }

    /**
     * 
     * @param fileName nama file yang dimasukkan output-nya
     * @throws NotInterpolationException menandakan bahwa bukan matriks interpolasi
     * @throws FileNotFoundException menandakan bahwa tidak ditemukan file dengan nama tersebut
     */
    public void inputInterpolationFromFile(String fileName) throws NotInterpolationException, FileNotFoundException{
        File inputFile = new File("../test/" + fileName);
        try{
            input = new Scanner(inputFile);
        } catch (FileNotFoundException e){
            throw e;
        }
        this.getDimension();
        if(this.cols != 2) throw new NotInterpolationException("Please input interpolation points \"<BigDecimal><space><BigDecimal>\"");
        this.cols = this.rows+1;
        
        input = new Scanner(inputFile);
        this.reset(this.rows, this.cols);
        this.inputInterpolationData();

        input = new Scanner(System.in);
    }

    // Interpolasi
    /**
     * Untuk menerima input titik interpolasi x dan y dan membentuk matriks interpolasi dari titik-titik tersebut
     */
    private void inputInterpolationData(){
        for(int r = 1; r <= this.rows; r++){
            BigDecimal x = this.input.nextBigDecimal();
            BigDecimal y = this.input.nextBigDecimal();
            // set koef baris dengan x^0, x^1, x^2, x^3, ..., y (matriks interpolasi)
            for(int c = 1; c <= this.cols; c++){
                if (c != this.cols) this.setElmt(r, c, x.pow(c-1));
                else this.setElmt(r, c, y);
            }
        }
    }

    /**
     * Untuk menerima input untuk operasi interpolasi dengan dua cara pula, yaitu melalui file atau melalui keyboard
     */
    public void inputInterpolation(){
        if (this.isInterpolationMatrix && this.rows != 0){
            System.out.printf("Tersimpan data interpolasi sebelumnya:\n");
            this.printInterpolationData();
            System.out.printf("Apakah anda ingin input titik(-titik) baru?(0/1)\n");
            if (this.input.nextInt() == 0) return;
        }
        isInterpolationMatrix = true;
        System.out.printf("Input dari file(0)/keyboard(1)?\n");
        int useKeyboard = this.input.nextInt();
        
        if (useKeyboard == 0){
            System.out.printf("Masukkan nama file+extension: ");
            String fileName = this.input.next();
            try{
                this.inputInterpolationFromFile(fileName);
            } catch (FileNotFoundException e){
                System.out.printf("File tidak ditemukan! Input akan dilakukan melalui keyboard!\n");
                useKeyboard = 1;
            } catch (NotInterpolationException e){
                System.out.printf("Matriks dalam file bukan berupa titik interpolasi! Input akan dilakukan melalui keyboard\n");
                useKeyboard = 1;
            }
        }

        if (useKeyboard == 1) {
            System.out.print("Berapa banyak titik yang Anda ingin masukkan? ");
            this.rows = this.input.nextInt();
            this.cols = this.rows+1;

            this.reset(this.rows, this.cols);
            this.inputInterpolationData();
        }
    }

    // == OUTPUT == //
    // Matriks
    /**
     * Untuk menampilkan matriks ke layar, bersifat publik
     */
    public void showMatrix(){
        this.print();
    }

    /**
     * Untuk menampilkan matriks ke layar, tetapi bersifat private
     */
    private void print(){
        if (this.rows == 0) System.out.println("Matriks kosong!");
        for(int r = 1; r <= this.rows; r++) {
            System.out.printf("|");
            for(int c = 1; c <= this.cols; c++) {
                System.out.printf(format(this.getElmt(r, c), 2)+" ");
            }
            System.out.println("|");
        }
    }

    // Interpolasi
    /**
     * Untuk menampilkan data interpolasi ke layar
     */
    private void printInterpolationData(){
        for(int r = 1; r <= this.rows; r++){
            System.out.printf("%.4f %.4f\n",this.getElmt(r, 2), this.getElmt(r, this.cols));
        }
    }

    /**
     * Untuk menampilkan solusi dari interpolasi berupa fungsi
     * @param sol matriks solusi
     */
    private void printSolutionInterpolation(Matrix sol) {
        System.out.print("f(x) = ");
        boolean firstNum = true;
        for(int i = 1; i <= this.cols-1; i++) {
            if(sol.getElmt(i, 1).compareTo(BigDecimal.ZERO) != 0) {
                String s = this.prettify(sol.getElmt(i, 1));
                if(firstNum){
                    s = this.formatAsFirstNum(s);
                    firstNum = false;
                }
                if(i != 1){
                    s += "x";
                    if(i != 2) s += "^"+ (i - 1);
                }
                System.out.print(s + " ");
            }
        }
        System.out.println();
        System.out.println();
    }

    // File
    /**
     * @return true jika user ingin memasukkan output ke dalam file
     */
    public boolean outputFile() {
        System.out.print("Apakah hasil ini ingin disimpan ke dalam file? (0/1)\n");
        int save = this.input.nextInt();
        if(save == 1) {
            System.out.println("Masukkan nama file+extension: ");
            String fileName = this.input.next();
            try{
                fileout = new PrintStream("../test/output/" + fileName);
                System.out.println("File berhasil tersimpan dengan nama \"" + fileName +"\"!");
                System.out.println("Silakan cek folder test/output/");
                System.setOut(fileout);
            } catch (Exception e){
                System.out.println(e);  
            }
            return true;
        } else {
            return false;
        }
    }
    
    // String
    /**
     * Untuk menampilkan solusi yang telah dihitung ke layar dalam bentuk string
     */
    private void printSolutionString(){
        for(int i = 1; i <= this.cols-1; i++){
            System.out.println(this.solution[i]);
        }
    }

    /**
     * @param n 1 elemen yang ingin dipercantik
     * @return string yang telah dipercantik
     */
    private String prettify(BigDecimal n){
        String ans = "";
        if(n.compareTo(BigDecimal.ZERO) < 0) ans += "- ";
        else if (n.compareTo(BigDecimal.ZERO) > 0) ans += "+ ";
        else return "0";
        n = n.abs();

        ans += format(n, 2);
        return ans;
    }

    /**
     * Untuk memformat angka pertama yang dikeluarkan ke layar dalam baris tertentu
     * @param S solusi dalam bentuk string
     */
    private String formatAsFirstNum(String s){
        if(s.isEmpty() || s.equals("0")) return "0";
        if(s.substring(0,1).equals("+")) s = s.substring(2);
        else s = s.substring(0,1)+s.substring(2);
        return s;
    }

    // ================================= Elementary Row Operation ================================= //
    // Swap Row
    /**
     * Untuk menukar dua baris
     * @param r1 baris pertama
     * @param r2 baris kedua
     */
    private void swapRow(int r1, int r2){
        this.scalar = this.scalar.negate();
        for(int c = 1; c <= this.cols; c++){
            BigDecimal tmp = this.getElmt(r1, c);
            this.setElmt(r1, c, this.getElmt(r2, c));
            this.setElmt(r2, c, tmp);
        }
    }

    // Scale Row
    /**
     * Untuk mengalikan suatu elemen dengan suatu skalar
     * @param r baris
     * @param k skalar
     */
    private void scaleRow(int r, BigDecimal k){
        if(k.compareTo(BigDecimal.ZERO) != 0)
            this.scalar = this.scalar.divide(k, precision, RoundingMode.CEILING);
        for(int c = r; c <= this.cols; c++){
            this.tabInt[r][c] = this.tabInt[r][c].multiply(k);
        }
    }

    // Add Row
    /**
     * Untuk menambah tiap elemen dari baris r1 dengan suatu kelipatan tiap elemen dari baris r2
     * @param r1 baris
     * @param r2 baris
     * @param k suatu kelipatan
     */
    private void addRow(int r1, int r2, BigDecimal k){
        for(int c = 1; c <= this.cols; c++){
            this.tabInt[r1][c] = this.tabInt[r1][c].add(k.multiply(this.getElmt(r2, c)));
            this.tabInt[r1][c] = this.myRound(this.tabInt[r1][c]);
        }
    }

    // ============================== Kelompok Cek Properti Matriks =================================== //
    /**
     * @return Untuk mengecek apakah suatu matriks berbentuk bujur sangkar
     */
    private boolean isSquareMatrix(){
        return (this.rows+1 == this.cols);
    }

    /**
     * @param r baris
     * @return true jika suatu baris mengandung elemen yang semuanya nol
     */
    private boolean isRowZero(int r) {
        int c = 1;
        while((c < this.cols) && (this.getElmt(r, c).compareTo(BigDecimal.ZERO) == 0)) {
            c += 1;
        }
        return (this.getElmt(r,this.cols).compareTo(BigDecimal.ZERO) == 0);
    }

    /**
     * @param r baris
     * @return false jika seluruh elemen pada baris r memiliki nilai nol kecuali elemen terakhirnya, karena tidak mungkin x1+x2+...+xn = k jika semua x = 0
     */
    private boolean isRowValid(int r){
        for(int c = 1; c < this.cols; c++){
            if (this.getElmt(r, c).compareTo(BigDecimal.ZERO) != 0) return true;
        }
        return (this.getElmt(r, this.cols).compareTo(BigDecimal.ZERO) == 0);
    }

    /**
     * @return true jika matriks tersebut memiliki solusi dengan cara melakukan eliminasi Gauss lalu mengecek apakah seluruh barisnya valid
     */
    private boolean hasSolution(){
        Matrix m = this.duplicateMatrix();
        m.gaussElim();
        for(int r = 1; r <= m.rows; r++){
            if (!m.isRowValid(r)) return false;
        }
        return true;
    }

    // ================================== Kelompok Manipulasi Matriks ===================================== //
    /**
     * 
     * @return menyalin seluruh variabel sebuah matriks ke sebuah matriks baru
     */
    private Matrix duplicateMatrix(){
        Matrix m = new Matrix(this.rows, this.cols);
        m.scalar = this.scalar;
        m.freeVar = this.freeVar;
        m.solution = this.solution;
        m.isInterpolationMatrix = this.isInterpolationMatrix;
        for(int r = 1; r <= this.rows; r++){
            for(int c = 1; c <= this.cols; c++){
                m.setElmt(r, c, this.getElmt(r, c));
            }
        }
        return m;
    }

    /**
     * 
     * @param rx baris
     * @param cx kolom
     * @return mereduksi matriks pada baris dan kolom tertentu dengan cara mengambil elemen-elemen yang tidak berada pada baris dan kolom tersebut
     */
    private Matrix reduce(int rx, int cx){
        Matrix m = this.duplicateMatrix();
        for(int r = 1; r <= this.rows; r++){
            for(int c = 1; c <= this.cols; c++){
                int newr = r, newc = c;
                if (r > rx) newr--;
                if (c > cx) newc--;
                m.setElmt(newr, newc, this.getElmt(r, c));
            }
        }
        m.cols--;
        m.rows--;
        return m;
    }

    /**
     * 
     * @return matriks yang berisi transpose dari sebuah matriks
     */
    private Matrix transpose(){
        Matrix m = this.duplicateMatrix();
        for(int r = 1; r <= m.rows; r++){
            for(int c = 1; c <= m.cols; c++){
                m.setElmt(r, c, this.getElmt(c, r));
            }
        }
        return m;
    }

    /**
     * @return hasil kali matriks
     */
    private Matrix multMatrix(Matrix m1, Matrix m2) {
        Matrix ans = new Matrix(m1.rows, m2.cols);
        ans.isInterpolationMatrix = false;

        if (m1.cols != m2.rows){
            System.out.println("Tidak ada solusi");
        } else {
            for(int r1 = 1; r1 <= m1.rows; r1++ ) {
                for(int c2 = 1; c2 <= m2.cols; c2++ ) {
                    ans.setElmt(r1, c2, BigDecimal.ZERO);
                    for(int r2 = 1; r2 <= m2.rows; r2++) {
                        ans.tabInt[r1][c2] = ans.tabInt[r1][c2].add(m1.getElmt(r1, r2).multiply(m2.getElmt(r2, c2)));
                    }
                    ans.tabInt[r1][c2] = this.myRound(ans.tabInt[r1][c2]);
                }
            }
        }
        return ans;
    }

    // ====================================== 1. BAGIAN SPL ========================================== //
    // Gauss
    /**
     * Untuk menampilkan solusi persamaan linier dengan menggunakan metode eliminasi Gauss
     */
    public void splGauss(){
        this.splGaussUtil();
        if(this.outputFile()){
            this.splGaussUtil();
            System.setOut(stdout);
        }
    }

    /**
     * Untuk melakukan perhitungan metode gauss dengan cara memanggil fungsi gaussElim()
     */
    private void splGaussUtil(){
        System.out.println("Menggunakan metode eliminasi Gauss (EF):");
        Matrix ans = this.duplicateMatrix()
                         .gaussElim();
        ans.print();
        if (!ans.hasSolution()) System.out.println("Tidak ada solusi");
        else {
            ans.getSPLSolution();
            this.freeVar = ans.freeVar;
            this.solution = ans.solution;
        }
        System.out.println();
    }

    // Gauss-Jordan
    /**
     * Untuk menampilkan solusi persamaan linier dengan menggunakan metode eliminasi Gauss-Jordan 
     */
    public void splGaussJordan(){
        this.splGaussJordanUtil();
        if(this.outputFile()){
            this.splGaussJordanUtil();
            System.setOut(stdout);
        }
    }

    /**
     * Untuk melakukan perhitungan metode gauss dengan cara memanggil fungsi gaussJordanElim()
     */
    private void splGaussJordanUtil(){
        System.out.println("Menggunakan metode eliminasi Gauss-Jordan (REF):");
        Matrix ans = this.duplicateMatrix()
                         .gaussJordanElim();
        ans.print();
        if (!ans.hasSolution()) System.out.println("Tidak ada solusi");
        else {
            ans.getSPLSolution();
            this.freeVar = ans.freeVar;
            this.solution = ans.solution;
        }
        System.out.println();
    }

    // Invers
    /**
     * Untuk menampilkan solusi persamaan linier dengan menggunakan metode matriks balikan
     */
    public void splInv(){
        this.splInvUtil();
        if(this.outputFile()){
            this.splInvUtil();
            System.setOut(stdout);
        }
    }

    /**
     * Untuk menghasilkan solusi persamaan linier dengan menggunakan metode matriks balikan (invers) 
     * dengan rumus matriks balikan dikali kolom terakhir dari matriks augmented
     */
    private void splInvUtil() {
        // AX = B, maka X = A^-1 B
        // A: getCoeffMatrix, B: getLastCol
        System.out.println("Menggunakan metode Matriks Balikan (invers):");
        if(this.isSquareMatrix()) {
            if(this.detGaussUtil().compareTo(BigDecimal.ZERO) == 0){
                System.out.println("Matriks ini determinannya 0, tidak bisa ditentukan dengan metode Invers");
                System.out.println("Silakan mencoba metode lain");
            } else {
                this.print();
                Matrix a = this.duplicateMatrix();
                Matrix b = this.getLastCol();
                a = a.invCramUtil();

                System.out.println("Hasil invers:");
                a.print();
                Matrix ans = multMatrix(a,b);
                for(int r = 1; r <= this.rows; r++){
                    String s = formatAsFirstNum(prettify(ans.getElmt(r, 1)));
                    this.solution[r] = String.format("X%d = ", r) + s;
                }
                this.printSolutionString();
            }
        } else {
            System.out.println("Tidak bisa dicari solusinya dengan metode ini karena matriks tidak berbentuk persegi!");
            System.out.println("Silakan coba metode lain.");
        }
        System.out.println();
    }

    // Cramer
    /**
     * Untuk menampilkan solusi persamaan linier dengan menggunakan metode Cramer
     */
    public void splCram(){
        this.splCramUtil();
        if(this.outputFile()){
            this.splCramUtil();
            System.setOut(stdout);
        }
    }

    /**
     * Untuk menghasilkan solusi persamaan linier dengan menggunakan kaidah Cramer
     * Caranya adalah dengan menukar kolom ke-i dengan kolom terakhir dari matriks augmented
     */
    private void splCramUtil(){
        System.out.println("Menggunakan metode Kaidah Cramer (determinan):");
        // Tukar kolom i dengan kolom terakhir
        if(this.isSquareMatrix()) {
            if (this.hasSolution()) {
                BigDecimal denom = this.detGaussUtil();
                if (denom.compareTo(BigDecimal.ZERO) == 0) {
                    System.out.println("Matriks ini determinannya 0, tidak bisa ditentukan dengan metode Crammer");
                    System.out.println("Silakan mencoba metode lain");
                } else {
                    Matrix a = this.getLastCol();
                    for (int c = 1 ; c <= this.cols; c++) {
                        Matrix ans = this.duplicateMatrix();
                        for (int r = 1 ; r <= this.rows ; r++) {
                            ans.setElmt(r, c, a.getElmt(r, 1));
                        }
                        String s = formatAsFirstNum(prettify(ans.detGaussUtil().divide(denom, precision, RoundingMode.CEILING)));
                        this.solution[c] = String.format("X%d = ", c) + s;
                    }
                    this.printSolutionString();
                }
            }
        } else {
            System.out.println("Tidak bisa dicari solusinya dengan metode ini karena matriks tidak berbentuk persegi!");
            System.out.println("Silakan coba metode lain.");
        }
        System.out.println();
    }

    // ========================================= Kelompok Eliminasi SPL ======================================= //
    // Gauss
    /**
     * @return matriks yang telah dieliminasi menggunakan metode Gauss (dari atas ke bawah)
     */
    private Matrix gaussElim(){
        Matrix m = this.duplicateMatrix();
        for (int pivot = 1; pivot <= m.rows; pivot++){
            // cari leading "1" yang paling depan, tukar ke baris yg sedang diproses
            for(int r = pivot+1; r <= m.rows; r++){
                if(m.idxNotZero(r) < m.idxNotZero(pivot)){
                    m.swapRow(r, pivot);
                }
            }

            int c = m.idxNotZero(pivot);
            if(c == m.cols+1) break;
            // kurangi semua baris di bawah pivot dengan k*baris pivot, sehingga
            // angka di bawah pivot jadi 0 semua
            for(int r = pivot + 1; r <= m.rows; r++){
                BigDecimal k = m.getElmt(r, c).divide(m.getElmt(pivot, c), precision, RoundingMode.CEILING);
                if (k.compareTo(BigDecimal.ZERO) != 0) m.addRow(r, pivot, k.negate());
            }

            // ubah elmt ke-pivot menjadi 1, agar menjadi echelon form
            m.scaleRow(pivot, BigDecimal.ONE.divide(m.getElmt(pivot, c), precision, RoundingMode.CEILING));
        }
        m.scalar = m.scalar.multiply(this.scalar);
        return m;
    }

    // Jordan
    /**
     * @return matriks yang telah dieliminasi menggunakan metode Jordan (dari bawah ke atas)
     */
    private Matrix jordanElim(){
        Matrix m = this.duplicateMatrix();
        // sama seperti gaussElim(), tapi dari bawah ke atas
        for (int pivot = m.rows; pivot >= 1; pivot--){
            int c = m.idxNotZero(pivot);
            if(c == m.cols+1) continue;
            for(int r = pivot-1; r >= 1; r--){
                BigDecimal k = m.getElmt(r, c).divide(m.getElmt(pivot, c), precision, RoundingMode.CEILING);
                if (k.compareTo(BigDecimal.ZERO) != 0) m.addRow(r, pivot, k.negate());
            }
        }
        return m;
    }

    // Gauss Jordan
    /**
     * @return matriks yang telah dieliminasi menggunakan campuran fungsi gaussElim() dan jordanElim()
     */
    private Matrix gaussJordanElim(){
        return this.gaussElim()
                   .jordanElim();
    }

    /**
     * Untuk mengubah matriks EF/REF menjadi string yang tertampil di layar
     */
    private void getSPLSolution(){
        // prekondisi: this.hasSolution() == true;
        this.getFreeVar();
        Matrix ans = this.constructSolutionMatrix();
        
        ans.constructSolutionString();
        this.solution = ans.solution;

        System.out.println("Solusi dari matriks SPL:");
        ans.printSolutionString();
    }

    /**
     * 
     * @return matriks berisi solusi
     */
    private Matrix constructSolutionMatrix(){
        int r = 1;
        // cari row terbawah yang tidak 0 semua
        for(int rr = 1; rr <= this.rows; rr++){
            if(this.idxNotZero(rr) < this.cols) r = rr;
        }

        Matrix m = new Matrix(this.cols-1, this.cols);
        m.freeVar = this.freeVar;
        m.setZero();
        for(int cc = this.cols-1; cc >= 1; cc--){
            if(!m.freeVar[cc]){
                m.setElmt(cc, this.cols, this.getElmt(r, this.cols));
                // jumlahkan semua
                for(int c = this.cols-1; c > cc; c--){
                    m.addRow(cc, c, this.getElmt(r, c).negate());
                }
                r--;
            } else {
                m.setElmt(cc, cc, BigDecimal.ONE);
            }
        }
        return m;
    }

    /**
     * Untuk mengubah solusi matriks menjadi string
     */
    private void constructSolutionString(){
        for(int r = 1; r <= this.rows; r++){
            this.solution[r] = String.format("X%d = ", r);
            boolean firstNum = true;
            for(int c = this.cols; c >= 1; c--){
                String s = prettify(this.getElmt(r, c));
                if(s.equals("0")) continue; // kalau 0 langsung skip

                // angka
                String num = s.substring(2);
                String sign = s.substring(0,1);
                // angka 1 tidak perlu ditulis (kecuali angka pertama)
                if(!firstNum && num.equals("1")) s = sign+" ";

                // angka pertama tidak perlu +
                if(firstNum) {
                    if(sign.equals("+")) s = num;
                    else s = sign+num;
                    firstNum = false;
                }

                // huruf
                if(c != this.cols && (this.getElmt(r, c).compareTo(BigDecimal.ZERO) != 0)){
                    s += String.format("%c ", 'a'+(this.cols-c-1));
                }
                else s += " ";

                this.solution[r] += s;
            }

            if(firstNum) this.solution[r] += "0";
        }
    }

    // ================================== 2. BAGIAN INTERPOLASI ==================================== //
    /**
     * Untuk menampilkan hasil interpolasi dari titik-titik yang telah dimasukkan
     */
    public void interpolate(){
        this.interpolateUtil();
        if(this.outputFile()){
            this.interpolateUtil();
            System.setOut(stdout);
        }
    }

    /**
     * Untuk menghasilkan fungsi yang merupakan hasil interpolasi dari titik-titik yang telah dimasukkan
     */
    private void interpolateUtil(){
        Matrix ans = this.gaussJordanElim();
        ans.print();
        // Kasus input beberapa titik sama, jadikan 1 titik saja
        for(int r = 1; r <= ans.rows; r++) {
            if(ans.isRowZero(r)) {
                reduce(r,this.cols-1);
            }
        }

        ans.print();
        if (!ans.hasSolution()) System.out.println("Solusi interpolasi tidak dapat ditemukan");
        else {
            Matrix sol = ans.getLastCol();
            ans.printSolutionInterpolation(sol);
            System.out.printf("nilai y = %.4f", this.estimateYInterpolation(sol));
        }
        System.out.println();
        System.out.println();
    }

    /**
     * 
     * @param sol matriks solusi
     * @return mengembalikan taksiran nilai fungsi (y) dari titik x yang dimasukkan.
     */
    private BigDecimal estimateYInterpolation(Matrix sol) {
        BigDecimal x;
        BigDecimal minX = this.minXInterpolation();
        BigDecimal maxX = this.maxXInterpolation();
        do{
            System.setOut(stdout);
            System.out.print("Masukkan nilai x antara " + minX + " dan " + maxX + " untuk ditaksir nilai y-nya: ");
            x = this.input.nextBigDecimal();
            if((x.compareTo(minX) < 0) || (x.compareTo(maxX) > 0)) {
                System.out.println("Titik tidak di dalam range. Silakan ulangi.");
                System.out.println();
            }
        } while((x.compareTo(minX) < 0) || (x.compareTo(maxX) > 0));

        BigDecimal hasil = BigDecimal.ZERO;
        for(int c = 1; c <= this.cols-1; c++) {
            hasil = hasil.add(sol.getElmt(c, 1).multiply((x.pow(c-1))));
        }
        if(fileout != null) {
            System.setOut(fileout);
            fileout = null;
        }
        System.out.printf("Untuk x = %.4f, ", x);
        return hasil;
    }

    // ================================== 3. BAGIAN MATRIKS NxN =================================== //
    // == INVERS == //
    // Gauss-Jordan
    /**
     * Untuk menghitung inverse dengan metode GaussJordan dan juga menanyakan apakah hasil inverse metode Gauss-Jordan ingin disimpan dan memasukkannya ke dalam file.
     */
    public void invGaussJordan(){
        this.invGaussJordanUt();
        if(this.outputFile()){
            this.invGaussJordanUt();
            System.setOut(stdout);
        }
    }

    /**
     * Untuk mencari inverse dengan metode Gauss-Jordan
     */
    private void invGaussJordanUt(){
        System.out.println("Menggunakan metode eliminasi Gauss-Jordan (REF):");
        if (this.detGaussUtil().compareTo(BigDecimal.ZERO) == 0){
            System.out.println("Tidak bisa dicari matriks invers! Determinannya 0!");
        } else {
            this.duplicateMatrix()
                .invGaussJordanUtil()
                .print();
        }
        System.out.println();
    }

    /**
     * 
     * @return matriks yang sudah dibalik atau diinvers menggunakan metode Gauss Jordan
     */
    private Matrix invGaussJordanUtil(){
        // pakai OBE, ditempelin matriks identitas di sblh kanannya
        Matrix m = this.duplicateMatrix()
                       .getCoeffMatrix();

        // copy m ke anstemp untuk di OBE
        Matrix anstemp = new Matrix(m.rows, 2*m.cols);
        for(int r = 1; r <= m.rows; r++){
            for(int c = 1; c <= m.cols; c++){
                anstemp.setElmt(r, c, m.getElmt(r, c));
            }
        }

        // copy matriks identitas di sblh kanannya
        for(int r = 1; r <= anstemp.rows; r++) {
            for(int c = (anstemp.cols/2) + 1; c <= anstemp.cols; c++) {
                if(r + (anstemp.cols/2) == c) anstemp.setElmt(r, c, BigDecimal.ONE);
                else anstemp.setElmt(r, c, BigDecimal.ZERO);
            }
        }
        anstemp = anstemp.gaussJordanElim();
        
        // copy ke matriks jawaban
        Matrix ans = new Matrix(m.rows, m.cols);
        for(int r = 1; r <= ans.rows; r++) {
            for(int c = 1; c <= ans.cols; c++) {
                BigDecimal a = anstemp.getElmt(r, c + (anstemp.cols/2));
                ans.setElmt(r, c, a);
            }
        }
        return ans;
    }

    // Cramer
    /**
     * prosedur digunakan untuk menghitung inverse dengan metode Cramer dan 
     * menanyakan apakah hasil inverse metode Cramer ingin disimpan dan memasukkannya ke dalam file.
     */
    public void invCram(){
        this.invCramUt();
        if(this.outputFile()){
            this.invCramUt();
            System.setOut(stdout);
        }
    }
    /**
     * prosedur mengecek terlebih dahulu apakah matriks yang dimasukkan memiliki determinan 
     * yang tidak sama dengan nol, karena matriks dengan determinan nol tidak dapat dicari inverse-nya. 
     * Apabila tidak, maka akan dilakukan proses inverse dengan memanfaatkan fungsi invCramUtil.
     */

    private void invCramUt(){
        System.out.println("Menggunakan Metode Crammer");
        if (this.detGaussUtil().compareTo(BigDecimal.ZERO) == 0){
            System.out.println("Tidak bisa dicari matriks invers! Determinannya 0!");
        } else {
            Matrix m = this.duplicateMatrix().invCramUtil();
            m.print();
        }
        System.out.println();
    }   
    /**
     * fungsi mengembalikan sebuah matriks yang sudah dibalik atau di-inverse menggunakan metode kaidah Cramer, 
     * yaitu dengan mencari matriks kofaktor dari matriks yang ditanya dan melakukan transpose agar 
     * mendapatkan matriks adjoin-nya. Kemudian matriks adjoin akan dibagi oleh determinan dari 
     * matriks itu sendiri dan akan mendapatkan matriks balikan atau matriks inverse dari matriks masukan.
     * @return hasil matriks balikan dari matriks yang dimasukkan dengan metode crammer
     */

    private Matrix invCramUtil(){
        // 1/det * adjoin
        Matrix ans = this.duplicateMatrix().getCoeffMatrix();
        BigDecimal x = ans.detCramUtil();

        ans = ans.adjoinUtil();
        for(int r = 1; r <= this.rows; r++) {
            for(int c = 1; c <= this.cols; c++) {
                ans.tabInt[r][c] = ans.tabInt[r][c].divide(x, precision, RoundingMode.CEILING);
            }
        }
        return ans;
    }

    // == DETERMINAN == //
    // Gauss
    /**
     * prosedur digunakan untuk menanyakan apakah hasil determinan metode Gauss 
     * ingin disimpan dan memasukkannya ke dalam file.
     */
    public void detGauss(){
        this.detGaussUt();
        if(this.outputFile()){
            this.detGaussUt();
            System.setOut(stdout);
        }
    }
    /**
     * prosedur digunakan untuk mempercantik hasil dari operasi 
     * determinan metode Gauss menggunakan fungsi prettify.
     */

    private void detGaussUt(){
        System.out.println("Menggunakan metode eliminasi Gauss (EF):");
        System.out.println(formatAsFirstNum(prettify(this.detGaussUtil())));
        System.out.println();
    }
    /**
     * fungsi mengembalikan nilai dari operasi determinan menggunakan metode Gauss. 
     * Jika diagonal utama dari matriks hasil eliminasi Gauss ada yang bernilai 0, maka determinan pastilah bernilai 0.
     * @return nilai determinan yang dicari menggunakan metode Gauss
     */
    private BigDecimal detGaussUtil(){
        Matrix m = this.duplicateMatrix()
                       .gaussElim();
        for(int r = 1; r <= m.rows; r++){
            if (m.getElmt(r, r).compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        }
        return m.scalar;
    }

    // Gauss Jordan
    /**
     * prosedur digunakan untuk menanyakan apakah 
     * hasil determinan metode Gauss-Jordan ingin disimpan dan memasukkannya ke dalam file.
     */
    public void detGaussJordan(){
        this.detGaussJordanUt();
        if(this.outputFile()){
            this.detGaussJordanUt();
            System.setOut(stdout);
        }
    }

    /**
     * prosedur digunakan untuk menampilkan dan mempercantik
     * hasil dari operasi determinan metode Gauss-Jordan menggunakan fungsi prettify.
     */
    private void detGaussJordanUt(){
        System.out.println("Menggunakan metode eliminasi Gauss-Jordan (REF):");
        System.out.println(formatAsFirstNum(prettify(this.detGaussJordanUtil())));
        System.out.println();
    }
    /**
     * fungsi mengembalikan nilai dari operasi determinan menggunakan metode Gauss-Jordan. 
     * Jika diagonal utama dari matriks hasil eliminasi Gauss ada yang bernilai 0, maka determinan pastilah bernilai 0.
     * @return nilai determinan matriks yang dicari menggunakan metode Gauss-Jordan
     */

    private BigDecimal detGaussJordanUtil(){
        Matrix m = this.duplicateMatrix()
                       .gaussJordanElim();
        for(int r = 1; r <= m.rows; r++){
            if (m.getElmt(r, r).compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        }
        return m.scalar;
    }

    // Cramer
    /**
     * prosedur digunakan untuk menanyakan apakah hasil determinan metode Cramer 
     * ingin disimpan dan memasukkannya ke dalam file.
     */
    public void detCram(){
        this.detCramUt();
        if(this.outputFile()){
            this.detCramUt();
            System.setOut(stdout);
        }
    }
    /**
     * prosedur digunakan untuk mempercantik hasil dari operasi determinan 
     * metode Cramer menggunakan fungsi prettify.

     */

    private void detCramUt(){
        System.out.println("Menggunakan Metode Cramer:");
        BigDecimal det = this.duplicateMatrix()
                         .getCoeffMatrix()
                         .detCramUtil();
        System.out.println(formatAsFirstNum(prettify(det)));
        System.out.println();
    }
    /**
     * fungsi mengembalikan sebuah nilai yaitu determinan matriks yang dicari dengan kaidah Cramer, 
     * yaitu dengan menggunakan matriks kofaktor. Fungsi ini bersifat rekursif.
     * @return angka determinan matriks dengan metode Crammer
     */

    private BigDecimal detCramUtil(){
        // Basis
        if (this.rows == 0) return BigDecimal.ONE;
        
        // Rekursi
        BigDecimal ans = BigDecimal.ZERO;
        for(int c = 1; c <= this.cols; c++){
            Matrix tmp = this.duplicateMatrix()
                             .reduce(1, c);
            ans = ans.add(this.getElmt(1, c).multiply(tmp.detCramUtil()).multiply(BigDecimal.valueOf(-1).pow(c+1)));
        }
        return ans;
    }

    // == COFACTOR == //
    /**
     * prosedur digunakan untuk menanyakan apakah hasilnya ingin disimpan dan memasukkannya ke dalam file
     */
    public void cofactor(){
        this.cofactorUt();
        if(this.outputFile()){
            this.cofactorUt();
            System.setOut(stdout);
        }
    }
    /**
     * prosedur menampilkan matriks kofaktor dari matriks yang dimasukkan.
     */

    private void cofactorUt(){
        this.getCoeffMatrix()
            .cofactorUtil()
            .print();
        System.out.println();
    }
    /**
     * fungsi digunakan untuk mengembalikan matriks yang berisi kofaktor dari matriks yang dimasukkan.
     * Pertama-tama , dibuat matriks bernama answ yang nantinya akan menampung hasil dari kofaktor.
     * tetapi pertama kali diisi dengan matriks hasil input. Lalu dibuat lagi sebuah matriks bernama
     * newm di dalam loop yang menampung minor baris dan kolom tertentu dari matriks hasil input. 
     * Kemudian, determinan dari matriks newm  tersebut dikali dengan kofaktornya. 
     * Akhirnya, matriks answ dimasukkan nilainya dengan hasil kali tersebut menggunakan setter
     * @return matriks hasil kofaktor
     */
    private Matrix cofactorUtil(){
        Matrix ans = this.duplicateMatrix();

        Matrix newm;
        for(int r = 1; r <= ans.rows; r++){
            for(int c = 1; c <= ans.cols; c++){
                newm = this.duplicateMatrix();
                newm = newm.reduce(r, c);
                BigDecimal det = newm.detCramUtil().multiply(BigDecimal.valueOf(-1).pow(r+c));
                ans.setElmt(r, c, det);
            }
        }
        return ans;
    }

    // == ADJOIN == //
    /**
     * prosedur digunakan untuk menanyakan apaakah hasilnya ingin disimpan dan dimasukkan ke dalam file.
     */
    public void adjoin(){
        this.adjoinUt();
        if(this.outputFile()){
            this.adjoinUt();
            System.setOut(stdout);
        }
    }
    /**
     * prosedur untuk menampilkan matriks adjoin dari matriks yang dimasukkan.
     */

    private void adjoinUt(){
        this.getCoeffMatrix()
            .adjoinUtil()
            .print();
        System.out.println();
    }
    /**
     * fungsi digunakan untuk mengembalikan matriks yang berisi transpose dari matriks kofaktor.
     * @return matriks adjoin
     */
    private Matrix adjoinUtil(){
        // Cofactor transpose
        return this.duplicateMatrix()
                   .cofactorUtil()
                   .transpose();
    }

    // == Studi Kasus == //
    /**
     * fungsi digunakan untuk mengembalikan matriks Hilbert pada studi kasus dari nilai n(derajat) tertentu/
     * @param n nilai yang akan diinput untuk mengisi matriks Hilbert
     * @return matriks Hilbert yang sudah diisi tiap elemennya menurut n yang diinput
     */
    public Matrix matriksHilbert(int n){
        Matrix m = new Matrix(n,n+1);
        for (int r = 1; r<= m.rows; r++){
            for(int c = 1; c<= m.cols; c++){
                if (c == m.cols) {
                    if (r == 1) {
                        m.setElmt(r, c, BigDecimal.ONE);
                    } else {
                        m.setElmt(r, c, BigDecimal.ZERO);
                    }
                } else {
                    BigDecimal x = BigDecimal.valueOf(r+c-1);
                    m.setElmt(r, c, BigDecimal.ONE.divide(x, precision, RoundingMode.CEILING));  
                }
            }
        }
        return m;
    }
    /**
     * prosedur dibuat untuk interpolasi fungsi pada studi kasus.
     */

    public void interpolasiDerajat(){
        System.out.print("Masukkan derajat polinom: ");
        int n = this.input.nextInt();
        Matrix m = new Matrix(n+1, n+2);
        BigDecimal h = BigDecimal.valueOf(2.0).divide(BigDecimal.valueOf(n), precision, RoundingMode.CEILING);
        BigDecimal x = BigDecimal.ZERO;

        for (int r = 1; r<= m.rows; r++){
            BigDecimal y = fungsi(x);
            for(int c = 1; c <= m.cols; c++){
                if (c != m.cols) m.setElmt(r, c, x.pow(c-1));
                else m.setElmt(r, c, y);
            }
            x = x.add(h);
        }
        m.interpolate();
        
    }
    /**
     * fungsi dibuat untuk mencari nilai fungsi(y) yang ada di studi kasus
     * @param x nilai yang akan dimasukan ke dalam fungsi
     * @return bigDecimal yang merupakan hasil perhitungan oleh x ke dalam fungsi
     */

    private BigDecimal fungsi(BigDecimal x){
        return ((x.multiply(x)).add(x.sqrt(mc))).divide(BigDecimal.valueOf(Math.exp(x.doubleValue())).add(x), precision, RoundingMode.CEILING);
    }
    /**
     * fungsi digunakan untuk membulatkan desimal(BigDecimal) yang mendekati nol (atau bilangan bulat lain). 
     * Pembulatan dilakukan cara, jika selisih BigDecimal dengan integer terdekatnya kurang dari suatu nilai
     * tertentu, maka akan dibulatkan.
     * @param x BigDecimal yang ingin dibulatkan mendekati nol 
     * @return Big decimal yang sudah dibulatkan mendekati nol
     */

    private BigDecimal myRound(BigDecimal x){
        if(x.subtract(BigDecimal.valueOf(x.intValue())).abs().compareTo(EPS) < 0) return BigDecimal.valueOf(x.intValue());
        return x;
    }
    /**
     * fungsi digunakan untuk pembulatan big decimal agar tidak semua angka di belakang koma ditampilkan. 
     * Pembulatan ini dilakukan sebelum big decimal diubah menjadi string.
     * @param x angka yang mau diubah menjadi string
     * @param scale angka penting
     * @return big decimal yang sudah diubah menjadi string
     */

    private String format(BigDecimal x, int scale) {
        NumberFormat formatter = new DecimalFormat("0.0E0");
        formatter.setRoundingMode(RoundingMode.HALF_UP);
        formatter.setMinimumFractionDigits(scale);
        return formatter.format(x);
    }
}