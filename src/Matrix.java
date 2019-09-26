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
    private void setElmt(int r, int c, BigDecimal x){
        this.tabInt[r][c] = x;
    }

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
    private void inputElements(){
        for(int r = 1; r <= this.rows; r++) {
            for(int c = 1; c <= this.cols; c++) {
                this.setElmt(r, c, this.input.nextBigDecimal());
            }
        }
    }

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
    public void showMatrix(){
        this.print();
    }

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
    private void printInterpolationData(){
        for(int r = 1; r <= this.rows; r++){
            System.out.printf("%.4f %.4f\n",this.getElmt(r, 2), this.getElmt(r, this.cols));
        }
    }

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
    private void printSolutionString(){
        for(int i = 1; i <= this.cols-1; i++){
            System.out.println(this.solution[i]);
        }
    }

    private String prettify(BigDecimal n){
        String ans = "";
        if(n.compareTo(BigDecimal.ZERO) < 0) ans += "- ";
        else if (n.compareTo(BigDecimal.ZERO) > 0) ans += "+ ";
        else return "0";
        n = n.abs();

        ans += format(n, 2);
        return ans;
    }

    private String formatAsFirstNum(String s){
        if(s.isEmpty() || s.equals("0")) return "0";
        if(s.substring(0,1).equals("+")) s = s.substring(2);
        else s = s.substring(0,1)+s.substring(2);
        return s;
    }

    // ================================= Elementary Row Operation ================================= //
    // Swap Row
    private void swapRow(int r1, int r2){
        this.scalar = this.scalar.negate();
        for(int c = 1; c <= this.cols; c++){
            BigDecimal tmp = this.getElmt(r1, c);
            this.setElmt(r1, c, this.getElmt(r2, c));
            this.setElmt(r2, c, tmp);
        }
    }

    // Scale Row
    private void scaleRow(int r, BigDecimal k){
        if(k.compareTo(BigDecimal.ZERO) != 0)
            this.scalar = this.scalar.divide(k, precision, RoundingMode.CEILING);
        for(int c = r; c <= this.cols; c++){
            this.tabInt[r][c] = this.tabInt[r][c].multiply(k);
        }
    }

    // Add Row
    private void addRow(int r1, int r2, BigDecimal k){
        for(int c = 1; c <= this.cols; c++){
            this.tabInt[r1][c] = this.tabInt[r1][c].add(k.multiply(this.getElmt(r2, c)));
            this.tabInt[r1][c] = this.myRound(this.tabInt[r1][c]);
        }
    }

    // ============================== Kelompok Cek Properti Matriks =================================== //
    private boolean isSquareMatrix(){
        return (this.rows+1 == this.cols);
    }

    private boolean isRowZero(int r) {
        int c = 1;
        while((c < this.cols) && (this.getElmt(r, c).compareTo(BigDecimal.ZERO) == 0)) {
            c += 1;
        }
        return (this.getElmt(r,this.cols).compareTo(BigDecimal.ZERO) == 0);
    }

    private boolean isRowValid(int r){
        for(int c = 1; c < this.cols; c++){
            if (this.getElmt(r, c).compareTo(BigDecimal.ZERO) != 0) return true;
        }
        return (this.getElmt(r, this.cols).compareTo(BigDecimal.ZERO) == 0);
    }

    private boolean hasSolution(){
        Matrix m = this.duplicateMatrix();
        m.gaussElim();
        for(int r = 1; r <= m.rows; r++){
            if (!m.isRowValid(r)) return false;
        }
        return true;
    }

    // ================================== Kelompok Manipulasi Matriks ===================================== //
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

    private Matrix transpose(){
        Matrix m = this.duplicateMatrix();
        for(int r = 1; r <= m.rows; r++){
            for(int c = 1; c <= m.cols; c++){
                m.setElmt(r, c, this.getElmt(c, r));
            }
        }
        return m;
    }

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
    public void splGauss(){
        this.splGaussUtil();
        if(this.outputFile()){
            this.splGaussUtil();
            System.setOut(stdout);
        }
    }

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
    public void splGaussJordan(){
        this.splGaussJordanUtil();
        if(this.outputFile()){
            this.splGaussJordanUtil();
            System.setOut(stdout);
        }
    }

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
    public void splInv(){
        this.splInvUtil();
        if(this.outputFile()){
            this.splInvUtil();
            System.setOut(stdout);
        }
    }

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
    public void splCram(){
        this.splCramUtil();
        if(this.outputFile()){
            this.splCramUtil();
            System.setOut(stdout);
        }
    }

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
    private Matrix gaussJordanElim(){
        return this.gaussElim()
                   .jordanElim();
    }

    private void getSPLSolution(){
        // prekondisi: this.hasSolution() == true;
        this.getFreeVar();
        Matrix ans = this.constructSolutionMatrix();
        
        ans.constructSolutionString();
        this.solution = ans.solution;

        System.out.println("Solusi dari matriks SPL:");
        ans.printSolutionString();
    }

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

                    // tidak perlu tulis angka 1 krn ada hurufnya (meskipun angka pertama)
                    if(c != this.cols && (this.getElmt(r, c).compareTo(BigDecimal.ZERO) != 0)){
                        if(sign.equals("+")) s = "";
                        else s = sign;
                    }
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
    public void interpolate(){
        this.interpolateUtil();
        if(this.outputFile()){
            this.interpolateUtil();
            System.setOut(stdout);
        }
    }

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
    public void invGaussJordan(){
        this.invGaussJordanUt();
        if(this.outputFile()){
            this.invGaussJordanUt();
            System.setOut(stdout);
        }
    }

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
    public void invCram(){
        this.invCramUt();
        if(this.outputFile()){
            this.invCramUt();
            System.setOut(stdout);
        }
    }

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
    public void detGauss(){
        this.detGaussUt();
        if(this.outputFile()){
            this.detGaussUt();
            System.setOut(stdout);
        }
    }

    private void detGaussUt(){
        System.out.println("Menggunakan metode eliminasi Gauss (EF):");
        System.out.println(formatAsFirstNum(prettify(this.detGaussUtil())));
        System.out.println();
    }

    private BigDecimal detGaussUtil(){
        Matrix m = this.duplicateMatrix()
                       .gaussElim();
        for(int r = 1; r <= m.rows; r++){
            if (m.getElmt(r, r).compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        }
        return m.scalar;
    }

    // Gauss Jordan
    public void detGaussJordan(){
        this.detGaussJordanUt();
        if(this.outputFile()){
            this.detGaussJordanUt();
            System.setOut(stdout);
        }
    }

    private void detGaussJordanUt(){
        System.out.println("Menggunakan metode eliminasi Gauss-Jordan (REF):");
        System.out.println(formatAsFirstNum(prettify(this.detGaussJordanUtil())));
        System.out.println();
    }

    private BigDecimal detGaussJordanUtil(){
        Matrix m = this.duplicateMatrix()
                       .gaussJordanElim();
        for(int r = 1; r <= m.rows; r++){
            if (m.getElmt(r, r).compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        }
        return m.scalar;
    }

    // Cramer
    public void detCram(){
        this.detCramUt();
        if(this.outputFile()){
            this.detCramUt();
            System.setOut(stdout);
        }
    }

    private void detCramUt(){
        System.out.println("Menggunakan Metode Cramer:");
        BigDecimal det = this.duplicateMatrix()
                         .getCoeffMatrix()
                         .detCramUtil();
        System.out.println(formatAsFirstNum(prettify(det)));
        System.out.println();
    }

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
    public void cofactor(){
        this.cofactorUt();
        if(this.outputFile()){
            this.cofactorUt();
            System.setOut(stdout);
        }
    }

    private void cofactorUt(){
        this.getCoeffMatrix()
            .cofactorUtil()
            .print();
        System.out.println();
    }

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
    public void adjoin(){
        this.adjoinUt();
        if(this.outputFile()){
            this.adjoinUt();
            System.setOut(stdout);
        }
    }

    private void adjoinUt(){
        this.getCoeffMatrix()
            .adjoinUtil()
            .print();
        System.out.println();
    }

    private Matrix adjoinUtil(){
        // Cofactor transpose
        return this.duplicateMatrix()
                   .cofactorUtil()
                   .transpose();
    }

    // == Studi Kasus == //
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

    private BigDecimal fungsi(BigDecimal x){
        return ((x.multiply(x)).add(x.sqrt(mc))).divide(BigDecimal.valueOf(Math.exp(x.doubleValue())).add(x), precision, RoundingMode.CEILING);
    }

    private BigDecimal myRound(BigDecimal x){
        if(x.subtract(BigDecimal.valueOf(x.intValue())).abs().compareTo(EPS) < 0) return BigDecimal.valueOf(x.intValue());
        return x;
    }

    private String format(BigDecimal x, int scale) {
        NumberFormat formatter = new DecimalFormat("0.0E0");
        formatter.setRoundingMode(RoundingMode.HALF_UP);
        formatter.setMinimumFractionDigits(scale);
        return formatter.format(x);
    }
}