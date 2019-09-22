import java.util.Scanner
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

public class Matrix{
    // Variables
    private double[][] tabInt;
    private int rows, cols;
    private double scalar;
    private boolean[] freeVar;
    private boolean isInterpolationMatrix;
    private String[] solution;
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

    // =========================================== Kelompok Primitif ========================================== //
    // Constructor
    public Matrix(int r, int c){
        this.reset(r, c);
    }

    private void reset(int r, int c){
        this.rows   = r;
        this.cols   = c;
        this.scalar = 1;
        this.tabInt = new double[this.rows+5][this.cols+5];
        this.freeVar       = new boolean[this.cols+5];
        this.solution = new String[this.cols+5];
    }

    // Selector / Getter
    private double getElmt(int r, int c){
        return this.tabInt[r][c];
    }

    private Matrix getCoeffMatrix(){
        Matrix m = this.duplicateMatrix();
        m.cols--;
        return m;
    }

    private void getDimension(Scanner input){
        this.rows = 1;
        this.cols = input.nextLine().split(" ").length;
        while(input.hasNextLine()){
            input.nextLine();
            this.rows += 1;
        }
    }

    private Matrix getLastCol(){
        Matrix m = new Matrix(this.rows, 1);
        for(int r = 1; r <= this.rows; r++){
            m.setElmt(r, 1, this.getElmt(r, this.cols));
        }
        return m;
    }

    private int idxNotZero(int r){
        for(int c = 1; c <= this.cols; c++){
            if(this.getElmt(r, c) != 0) return c;
        }
        return this.cols+1;
    }

    private void getFreeVar(){
        // set semua jadi true dulu
        for(int c = this.cols-1; c >= 1; c--) this.freeVar[c] = true;

        // kalau ada yg bukan freevar baru diset jadi false
        for(int r = this.rows; r >= 1; r--){
            if (this.idxNotZero(r) < this.cols) this.freeVar[r] = false;
        }
    }

    private double maxXInterpolation() {
        double max = this.getElmt(1,2);
        for(int r = 1; r <= this.rows; r++) {
            max = Math.max(max, getElmt(r, 2));
        }
        return max;
    }

    private double minXInterpolation() {
        double min = this.getElmt(1,2);
        for(int r = 1; r <= this.rows; r++) {
            min = Math.min(min, getElmt(r, 2));
        }
        return min;
    }

    // Setter
    private void setElmt(int r, int c, double x){
        this.tabInt[r][c] = x;
    }

    private void setZero(){
        for(int r = 1; r <= this.rows; r++){
            for(int c = 1; c <= this.cols; c++){
                this.setElmt(r, c, 0);
            }
        }
    }

    // ============================================ Kelompok I/O ============================================ //
    // == INPUT == //
    // Matriks
    private void inputElements(Scanner input){
        for(int r = 1; r <= this.rows; r++) {
            for(int c = 1; c <= this.cols; c++) {
                this.setElmt(r, c, input.nextDouble());
            }
        }
    }

    public void inputMatrix(Scanner input, boolean isSq){
        if (!this.isInterpolationMatrix && ((this.isSquareMatrix() && isSq) || !isSq) && this.rows != 0) {
            System.out.printf("Tersimpan matriks sebelumnya:\n");
            this.print();
            System.out.printf("Apakah anda ingin input matriks baru?(0/1)\n");
            if (input.nextInt() == 0) return;
        }
        isInterpolationMatrix = false;
        System.out.printf("Input dari file(0)/keyboard(1)?\n");
        int useKeyboard = input.nextInt();
        
        if (useKeyboard == 0){
            System.out.printf("Masukkan nama file+extension: ");
            String fileName = input.next();
            try{
                File inputFile = new File("../test/" + fileName);
                this.getDimension(new Scanner(inputFile));
                if(isSq && !this.isSquareMatrix()){
                    System.out.printf("Matriks dalam file tidak persegi! Input akan dilakukan melalui keyboard\n");
                    useKeyboard = 1;
                } else {
                    input = new Scanner(inputFile);
                }
            } catch (FileNotFoundException e){
                System.out.printf("File tidak ditemukan! Input akan dilakukan melalui keyboard!\n");
                useKeyboard = 1;
            }
        }

        if (useKeyboard == 1){
            if (isSq){
                System.out.printf("Masukkan ordo matriks: ");
                this.rows = input.nextInt();
                this.cols = this.rows+1;
            } else {
                System.out.printf("Masukkan jumlah baris: "); this.rows = input.nextInt();
                System.out.printf("Masukkan jumlah kolom: "); this.cols = input.nextInt()+1;    
            }
        }

        this.reset(this.rows, this.cols);
        this.inputElements(input);
    }

    // Interpolasi
    private void inputInterpolationData(Scanner input){
        for(int r = 1; r <= this.rows; r++){
            double x = input.nextDouble();
            double y = input.nextDouble();
            // set koef baris dengan x^0, x^1, x^2, x^3, ..., y (matriks interpolasi)
            for(int c = 1; c <= this.cols; c++){
                if (c != this.cols) this.setElmt(r, c, Math.pow(x, c-1));
                else this.setElmt(r, c, y);
            }
        }
    }

    public void inputInterpolation(Scanner input){
        if (this.isInterpolationMatrix && this.rows != 0){
            System.out.printf("Tersimpan data interpolasi sebelumnya:\n");
            this.printInterpolationData();
            System.out.printf("Apakah anda ingin input titik(-titik) baru?(0/1)\n");
            if (input.nextInt() == 0) return;
        }
        isInterpolationMatrix = true;
        System.out.printf("Input dari file(0)/keyboard(1)?\n");
        int useKeyboard = input.nextInt();
        
        if (useKeyboard == 0){
            System.out.printf("Masukkan nama file+extension: ");
            String fileName = input.next();
            try{
                File inputFile = new File("../test/" + fileName);
                this.getDimension(new Scanner(inputFile));
                input = new Scanner(inputFile);
            } catch (FileNotFoundException e){
                System.out.printf("File tidak ditemukan! Input akan dilakukan melalui keyboard!\n");
                useKeyboard = 1;
            }
        }

        if (useKeyboard == 1) {
            System.out.print("Berapa banyak titik yang Anda ingin masukkan? ");
            this.rows = input.nextInt();
        }
        this.cols = this.rows+1;

        this.reset(this.rows, this.cols);
        this.inputInterpolationData(input);
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
                System.out.printf("%.2f ", this.getElmt(r, c));
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
        for(int i = 1; i <= this.cols; i++) {
            if(sol.getElmt(i, 1) != 0) {
                String s = this.prettify(sol.getElmt(i, 1));
                if(firstNum){
                    s = this.formatAsFirstNum(s);
                    firstNum = false;
                }
                s += "x";
                if(i != 2) s += "^"+ (i - 1);
                System.out.print(s + " ");
            }
        }
        System.out.println();
        System.out.println();
    }

    // File
    public void outputFile(Scanner input) {
        System.out.print("Apakah hasil ini ingin disimpan ke dalam file? (0/1)\n");
        int save = input.nextInt();
        if(save == 1) {
            System.out.println("Masukkan nama file+extension: ");
            String fileName = input.next();
            try{
                File file = new File ("../test/output/" + fileName);

                if(!file.exists()) {
                    file.createNewFile();
                } else{
                    System.out.println("File dengan nama " + fileName + " sudah ada!");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println();
    }

    private void printSolutionString(){
        for(int i = 1; i <= this.cols-1; i++){
            System.out.println(this.solution[i]);
        }
    }

    // Format String
    private String prettify(double n){
        String ans = "";
        if(n < 0) ans += "- ";
        else if (n > 0) ans += "+ ";
        else return ans;
        n = Math.abs(n);

        if(n == Math.round(n)) ans += Math.round(n);
        else ans += String.format("%.4f", n);
        return ans;
    }

    private String formatAsFirstNum(String s){
        if(s.substring(0,1).equals("+")) s = s.substring(2);
        else s = s.substring(0,1)+s.substring(2);
        if(s.isEmpty()) s = "0";
        return s;
    }

    // ================================= Elementary Row Operation ================================= //
    // Swap Row
    private void swapRow(int r1, int r2){
        this.scalar *= -1;
        for(int c = 1; c <= this.cols; c++){
            double tmp = this.getElmt(r1, c);
            this.setElmt(r1, c, this.getElmt(r2, c));
            this.setElmt(r2, c, tmp);
        }
    }

    // Scale Row
    private void scaleRow(int r, double k){
        this.scalar /= k;
        for(int c = r; c <= this.cols; c++){
            this.tabInt[r][c] *= k;
        }
    }

    // Add Row
    private void addRow(int r1, int r2, double k){
        for(int c = 1; c <= this.cols; c++){
            this.tabInt[r1][c] += k*this.getElmt(r2, c);
        }
    }

    // ============================== Kelompok Cek Properti Matriks =================================== //
    private boolean isSquareMatrix(){
        return (this.rows+1 == this.cols);
    }

    private boolean isRowZero(int r) {
        int c = 1;
        while((c<this.cols) && (this.getElmt(r, c) == 0)) {
            c += 1;
        }
        return (this.getElmt(r,this.cols) == 0);
    }

    private boolean isRowValid(int r){
        for(int c = 1; c < this.cols; c++){
            if (this.getElmt(r, c) != 0) return true;
        }
        return (this.getElmt(r, this.cols) == 0);
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
                    ans.setElmt(r1, c2, 0);
                    for(int r2 = 1; r2 <= m2.rows; r2++) {
                        ans.tabInt[r1][c2] += m1.getElmt(r1, r2) * m2.getElmt(r2, c2);
                    }
                }
            }
        }
        return ans;
    }

    // ====================================== 1. BAGIAN SPL ========================================== //
    public void splGauss(){
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

    public void splGaussJordan(){
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

    public void splInv() {
        // AX = B, maka X = A^-1 B
        // A: getCoeffMatrix, B: getLastCol
        if(this.isSquareMatrix()) {
            if(this.detGaussUtil() == 0){
                System.out.println("Matriks ini determinannya 0, tidak bisa ditentukan dengan metode Invers");
                System.out.println("Silakan mencoba metode lain");
            } else {
                this.print();
                Matrix a = this.duplicateMatrix();
                Matrix b = this.getLastCol();
                a = a.invCramUtil();
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


    public void splCram(){
        // Tukar kolom i dengan kolom terakhir
        if(this.isSquareMatrix()) {
            if (this.hasSolution()) {
                double denom = this.detGaussUtil();
                if (denom == 0) {
                    System.out.println("Matriks ini determinannya 0, tidak bisa ditentukan dengan metode Crammer");
                    System.out.println("Silakan mencoba metode lain");
                } else {
                    Matrix a = this.getLastCol();
                    for (int c = 1 ; c <= this.cols; c++) {
                        Matrix ans = this.duplicateMatrix();
                        for (int r = 1 ; r <= this.rows ; r++) {
                            ans.setElmt(r, c, a.getElmt(r, 1));
                        }
                        String s = formatAsFirstNum(prettify(ans.detGaussUtil() / denom));
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
                double k = m.getElmt(r, c)/m.getElmt(pivot, c);
                if (k != 0) m.addRow(r, pivot, -k);
            }

            // ubah elmt ke-pivot menjadi 1, agar menjadi echelon form
            m.scaleRow(pivot, 1 / m.getElmt(pivot, c));
        }
        m.scalar *= this.scalar;
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
                double k = m.getElmt(r, c) / m.getElmt(pivot, c);
                if (k != 0) m.addRow(r, pivot, -k);
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
                    m.addRow(cc, c, -this.getElmt(r, c));
                }
                r--;
            } else {
                m.setElmt(cc, cc, 1);
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
                if(s.isEmpty()) continue; // kalau 0 langsung skip

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
                    if(c != this.cols && this.getElmt(r, c) != 0){
                        if(sign.equals("+")) s = "";
                        else s = sign;
                    }
                    firstNum = false;
                }

                // huruf
                if(c != this.cols && this.getElmt(r, c) != 0){
                    s += String.format("%c ", 'a'+(this.cols-c-1));
                }
                else s += " ";

                this.solution[r] += s;
            }

            if(firstNum) this.solution[r] += "0";
        }
    }

    // ================================== 2. BAGIAN INTERPOLASI ==================================== //
    public void interpolate(Scanner input){
        Matrix ans = this.gaussJordanElim();
        // Kasus input beberapa titik sama, jadikan 1 titik saja
        for(int r = 1; r <= ans.rows; r++) {
            if(ans.isRowZero(r)) {
                reduce(r,this.cols-1);
            }
        }

        if (!ans.hasSolution()) System.out.println("Solusi interpolasi tidak dapat ditemukan");
        else {
            Matrix sol = ans.getLastCol();
            ans.printSolutionInterpolation(sol);
            System.out.printf("nilai y = %.4f", this.estimateYInterpolation(input, sol));
        }
        System.out.println();
        System.out.println();
    }

    private double estimateYInterpolation(Scanner input, Matrix sol) {
        double x;
        double minX = this.minXInterpolation();
        double maxX = this.maxXInterpolation();
        do{
            System.out.print("Masukkan nilai x antara " + minX + " dan " + maxX + " untuk ditaksir nilai y-nya: ");
            x = input.nextDouble();
            if((x < minX) || (x > maxX)) {
                System.out.println("Titik tidak di dalam range. Silakan ulangi.");
                System.out.println();
            }
        } while((x < minX) || (x > maxX));

        double hasil = 0;
        for(int c = 1; c <= this.cols; c++) {
            hasil += sol.getElmt(c, 1) * (Math.pow(x, c-1));
        }
        System.out.printf("Untuk x = %.4f, ", x);
        return hasil;
    }

    // ================================== 3. BAGIAN MATRIKS NxN =================================== //
    // == INVERS == //
    // Gauss-Jordan
    public void invGaussJordan(){
        if (this.detGaussUtil() == 0){
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
                if(r + (anstemp.cols/2) == c) anstemp.setElmt(r, c, 1);
                else anstemp.setElmt(r, c, 0);
            }
        }
        anstemp = anstemp.gaussJordanElim();
        
        // copy ke matriks jawaban
        Matrix ans = new Matrix(m.rows, m.cols);
        for(int r = 1; r <= ans.rows; r++) {
            for(int c = 1; c <= ans.cols; c++) {
                double a = anstemp.getElmt(r, c + (anstemp.cols/2));
                ans.setElmt(r, c, a);
            }
        }
        return ans;
    }

    // Cramer
    public void invCram(){
        if (this.detGaussUtil() == 0){
            System.out.println("Tidak bisa dicari matriks invers! Determinannya 0!");
        } else {
            Matrix m = this.duplicateMatrix().invCramUtil();
            if (m.getElmt(1,1) == Double.NaN) System.out.println("Tidak bisa dicari matriks invers! Determinannya 0!");
            else m.print();
        }
        System.out.println();
    }

    private Matrix invCramUtil(){
        // 1/det * adjoin
        Matrix ans = this.duplicateMatrix().getCoeffMatrix();
        double x = ans.detCramUtil();
        if (x == 0){
            ans.setElmt(1, 1, Double.NaN);
            return ans;
        }

        ans = ans.adjoinUtil();
        for(int r = 1; r <= this.rows; r++) {
            for(int c = 1; c <= this.cols; c++) {
                ans.tabInt[r][c] /= x;
            }
        }
        return ans;
    }

    // == DETERMINAN == //
    // Gauss
    public void detGauss(){
        System.out.println(formatAsFirstNum(prettify(this.detGaussUtil())));
        System.out.println();
    }

    private double detGaussUtil(){
        Matrix m = this.duplicateMatrix()
                       .gaussElim();
        for(int r = 1; r <= m.rows; r++){
            if (m.getElmt(r, r) == 0) return 0;
        }
        return m.scalar;
    }

    // Gauss Jordan 
    public void detGaussJordan(){
        System.out.println(formatAsFirstNum(prettify(this.detGaussJordanUtil())));
        System.out.println();
    }

    private double detGaussJordanUtil(){
        Matrix m = this.duplicateMatrix()
                       .gaussJordanElim();
        for(int r = 1; r <= m.rows; r++){
            if (m.getElmt(r, r) == 0) return 0;
        }
        return m.scalar;
    }

    // Cramer
    public void detCram(){
        double det = this.duplicateMatrix()
                         .getCoeffMatrix()
                         .detCramUtil();
        System.out.println(formatAsFirstNum(prettify(det)));
        System.out.println();
    }

    private double detCramUtil(){
        // Basis
        if (this.rows == 0) return 1;
        
        // Rekursi
        double ans = 0;
        for(int c = 1; c <= this.cols; c++){
            Matrix tmp = this.duplicateMatrix()
                             .reduce(1, c);
            ans += this.getElmt(1, c) * tmp.detCramUtil() * Math.pow(-1,c+1);
        }
        return ans;
    }

    // == COFACTOR == //
    public void cofactor(){
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
                double det = newm.detCramUtil() * Math.pow(-1, r+c);
                ans.setElmt(r, c, det);
            }
        }
        return ans;
    }

    // == ADJOIN == //
    public void adjoin(){
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
}