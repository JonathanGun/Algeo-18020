import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

public class Matrix{
    private double[][] TabInt;
    private int rows, cols;
    private double scalar;
    private double[] Solution;

    // Constructor
    public Matrix(int r, int c){
        this.rows = r;
        this.cols = c;
        this.scalar = 1;
        this.TabInt = new double[this.rows+5][this.cols+5];
        this.Solution = new double[this.rows+5];
    }

    // Selector
    private double getElmt(int r, int c){
        return this.TabInt[r][c];
    }

    // Setter
    private void setElmt(int r, int c, double x){
        this.TabInt[r][c] = x;
    }

    // I/O
    private void inputElements(Scanner input){
        for(int r = 1; r <= this.rows; r++) {
            for(int c = 1; c <= this.cols; c++) {
                this.setElmt(r, c, input.nextDouble());
            }
        }
    }

    private void getDimension(Scanner input){
        this.rows = 1;
        this.cols = input.nextLine().split(" ").length;
        while(input.hasNextLine()){
            input.nextLine();
            this.rows += 1;
        }
    }

    public void inputMatrix(Scanner input, boolean isSq){
        if (this.rows != 0) {
            System.out.printf("Matriks sebelumnya:\n");
            this.print();
            System.out.printf("Apakah ingin input ulang?(0/1)\n");
            if (input.nextInt() == 0) return;
        }
        System.out.printf("Input from file/keyboard?(0/1)\n");
        int useKeyboard = input.nextInt();
        
        if (useKeyboard == 0){
            System.out.printf("Masukkan nama file+extension: ");
            String fileName = input.next();
            try{
                File inputFile = new File("../test/" + fileName);
                this.getDimension(new Scanner(inputFile));
                input = new Scanner(inputFile);
            } catch (FileNotFoundException e){
                System.out.printf("File not found, will use keyboard");
                useKeyboard = 1;
            }
        }

        if (useKeyboard == 1){
            System.out.printf("Masukkan jumlah baris: "); this.rows = input.nextInt();
            System.out.printf("Masukkan jumlah kolom: "); this.cols = input.nextInt();
        }

        this.TabInt = new double[this.rows+5][this.cols+5];
        this.Solution = new double[this.rows+5];
        this.inputElements(input);
    }

    public void inputInterpolation(Scanner input){
        this.rows = input.nextInt();
        this.cols = this.rows+1;
        this.TabInt = new double[this.rows+5][this.cols+5];
        this.Solution = new double[this.rows+5];
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

    public void print(){
        if (this.rows == 0) System.out.println("Matriks kosong!");
        for(int r = 1; r <= this.rows; r++) {
            System.out.printf("|");
            for(int c = 1; c <= this.cols; c++) {
                System.out.printf("%.2f ", this.getElmt(r, c));
            }
            System.out.println("|");
        }
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
            this.TabInt[r][c] *= k;
        }
    }

    // Add Row
    private void addRow(int r1, int r2, double k){
        for(int c = 1; c <= this.cols; c++){
            this.TabInt[r1][c] += k*this.getElmt(r2, c);
        }
    }

    // =========================================== Operasi Lain ====================================== //
    public boolean isSquareMatrix(){
        return (this.rows+1 == this.cols);
    }

    private Matrix getCoeffMatrix(){
        Matrix m = this.duplicateMatrix();
        m.cols--;
        return m;
    }

    private Matrix getLastCol(){
        Matrix m = new Matrix(this.rows, 1);
        for(int r = 1; r <= this.rows; r++){
            m.setElmt(r, 1, this.getElmt(r, this.cols));
        }
        return m;
    }

    private Matrix duplicateMatrix(){
        Matrix newm = new Matrix(this.rows, this.cols);
        for(int r = 1; r <= this.rows; r++){
            for(int c = 1; c <= this.cols; c++){
                newm.setElmt(r, c, this.getElmt(r, c));
            }
        }
        return newm;
    }

    private Matrix reduce(int rx, int cx){
        Matrix newm = this.duplicateMatrix();
        for(int r = 1; r <= this.rows; r++){
            for(int c = 1; c <= this.cols; c++){
                int newr = r, newc = c;
                if (r > rx) newr--;
                if (c > cx) newc--;
                newm.setElmt(newr, newc, this.getElmt(r, c));
            }
        }
        newm.cols--;
        newm.rows--;
        return newm;
    }

    private Matrix transpose(){
        Matrix newm = new Matrix(this.cols, this.rows);
        for(int r = 1; r <= newm.rows; r++){
            for(int c = 1; c <= newm.cols; c++){
                newm.setElmt(r, c, this.getElmt(c, r));
            }
        }
        return newm;
    }

    private boolean isAllZero(int r){
        for(int c = 1; c <= this.cols; c++){
            if (this.getElmt(r, c) != 0) return false;
        }
        return true;
    }

    private Matrix multMatrix(Matrix m1, Matrix m2) {
        Matrix ans = new Matrix(m1.rows, m2.cols);

        if (m1.cols != m2.rows){
            System.out.println("Tidak ada solusi");
        } else {
            for(int r1 = 1; r1 <= m1.rows; r1++ ) {
                for(int c2 = 1; c2 <= m2.cols; c2++ ) {
                    ans.setElmt(r1, c2, 0);
                    for(int r2 = 1; r2 <= m2.rows; r2++) {
                        ans.TabInt[r1][c2] += m1.getElmt(r1, r2) * m2.getElmt(r2, c2);
                    }
                }
            }
        }
        return ans;
    }

    // ====================================== 1. BAGIAN SPL ========================================== //

    public void splGauss(){
        Matrix ans = this.duplicateMatrix().gaussElim();
        ans.getSolution();
        this.Solution = ans.Solution;
        this.printSolution();
    }

    public void splGaussJordan(){
        Matrix ans = this.duplicateMatrix().gaussJordanElim();
        ans.getSolution();
        this.Solution = ans.Solution;
        this.printSolution();
    }

    // Invers
    public void splInv() {
        // AX = B, maka X = A^-1 B
        // A: getCoeffMatrix, B: getLastCol
        Matrix a = this.getCoeffMatrix();
        Matrix b = this.getLastCol();
        a = a.invCram();
        Matrix x  = multMatrix(a, b);
        for(int i = 1; i <= this.rows; i++){
            this.Solution[i] = x.getElmt(i, 1);
        }
        this.printSolution();
    }

    public void splCram(){
        if (!this.isSquareMatrix()) System.out.println("Matriks tidak persegi! Tidak dapat dicari determinannya!");
        // yang ditukar kolom i dengan kolom terakhir (manfaatkan getlastcol)
    }

    // GAUSS-JORDAN
    // Gauss
    private Matrix gaussElim(){
        Matrix m = this.duplicateMatrix();
        // Kasus normal (ada 1 solusi) : ukuran matriks NxN
        for (int pivot = 1; pivot <= m.rows; pivot++){
            // cek ElmtDiag ke-pivot, jika 0 tukar dengan yg tidak 0
            if (m.getElmt(pivot, pivot) == 0){
                for(int r = pivot+1; r <= m.rows; r++){
                    if (m.getElmt(r, pivot) != 0){
                        m.swapRow(r, pivot);
                        break;
                    }
                }
            }

            // cek lagi, jika masih 0 maka memang 0 semua di bawahnya
            if (m.getElmt(pivot, pivot) == 0) continue;

            // kurangi semua baris di bawah pivot dengan k*baris pivot, sehingga
            // angka di bawah pivot jadi 0 semua
            for(int r = pivot + 1; r <= m.rows; r++){
                double k = m.getElmt(r, pivot)/m.getElmt(pivot, pivot);
                if (k != 0) m.addRow(r, pivot, -k);
            }

            // ubah elmt ke-pivot menjadi 1, agar menjadi echelon form
            scaleRow(pivot, 1/m.getElmt(pivot, pivot));
        }
        return m;
    }

    // Jordan
    private Matrix jordanElim(){
        Matrix m = this.duplicateMatrix();
        // sama seperti gaussElim(), tapi dari kanan bawah ke kiri atas,
        // lalu kurangi semua baris di atas pivot agar mjd 0
        for (int pivot = m.rows; pivot >= 1; pivot--){
            if (m.getElmt(pivot, pivot) == 0) continue;
            for(int r = pivot-1; r >= 1; r--){
                double k = m.getElmt(r, pivot)/m.getElmt(pivot, pivot);
                if (k != 0) m.addRow(r, pivot, -k);
            }
        }
        return m;
    }

    // Gauss Jordan
    private Matrix gaussJordanElim(){
        return this.gaussElim().jordanElim();
    }

    // Operasi lain
    private void getSolution(){
        for(int r = 1; r <= this.rows; r++){
            if (this.isAllZero(r)){
                this.Solution = new double[0];
                return;
            }
        }

        for(int r = this.rows; r >= 1; r--){
            double sum = this.getElmt(r, this.cols);
            for(int c = this.cols-1; c >= r; c--){
                sum -= this.getElmt(r, c)*this.Solution[c];
            }
            this.Solution[r] = sum/this.getElmt(r,r);
        }
    }

    private void printSolution(){
        System.out.println("Solusi dari matriks SPL:");
        for(int r = 1; r <= this.rows; r++){
            System.out.printf("X%d = %f\n", r, this.Solution[r]);
        }
    }

    // ================================== 2. BAGIAN INTERPOLASI ==================================== //
    public void interpolate(){
        this.gaussJordanElim();
        this.getSolution();
        this.printSolution();
    }

    // ================================== 3. BAGIAN MATRIKS NxN =================================== //

    // INVERS
    // Gauss-Jordan
    public Matrix invGaussJordan(){
        // pakai OBE, ditempelin matriks identitas di sblh kanannya
        Matrix m = this.duplicateMatrix().getCoeffMatrix();

        // copy m ke answtemp untuk di OBE
        Matrix answtemp = new Matrix(m.rows, 2*m.cols);
        for(int r = 1; r <= m.rows; r++){
            for(int c = 1; c <= m.cols; c++){
                answtemp.setElmt(r, c, m.getElmt(r, c));
            }
        }

        // copy matriks identitas di sblh kanannya
        for(int r = 1; r <= answtemp.rows; r++) {
            for(int c = (answtemp.cols/2) + 1; c <= answtemp.cols; c++) {
                if(r + (answtemp.cols/2) == c) answtemp.setElmt(r, c, 1);
                else answtemp.setElmt(r, c, 0);
            }
        }
        answtemp = answtemp.gaussJordanElim();
        
        // copy ke matriks jawaban
        Matrix answ = new Matrix(m.rows, m.cols);
        for(int r = 1; r <= answ.rows; r++) {
            for(int c = 1; c <= answ.cols; c++) {
                double a = answtemp.getElmt(r, c + (answtemp.cols/2));
                answ.setElmt(r, c, a);
            }
        }
        return answ;
    }

    // Cramer
    public Matrix invCram(){
        // 1/det * adjoin
        Matrix answ = this.duplicateMatrix().getCoeffMatrix();
        double x = 1/answ.detGauss();
        answ = answ.adjoin();
        for(int r = 1; r <= this.rows; r++) {
            for(int c = 1; c <= this.cols; c++) {
                answ.TabInt[r][c] *= x;
            }
        }
        return answ;
    }

    // DETERMINAN
    // Cramer
    public double detCram(){
        return this.duplicateMatrix().getCoeffMatrix().detCramUtil();
    }

    private double detCramUtil(){
        // Basis
        if (this.rows == 0) return 1;

        // Rekursi
        double ans = 0;
        for(int c = 1; c <= this.cols; c++){
            Matrix tmp = this.duplicateMatrix().reduce(1, c);
            ans += this.getElmt(1, c) * tmp.detCramUtil() * Math.pow(-1,c+1);
        }
        return ans;
    }

    // Gauss - EF
    public double detGauss(){
        Matrix m = this.duplicateMatrix().getCoeffMatrix().gaussElim();
        for(int r = 1; r <= m.rows; r++){
            if (m.getElmt(r, r) == 0) return 0;
        }
        return m.scalar;
    }

    // Gauss Jordan - REF
    public double detGaussJordan(){
        Matrix m = this.duplicateMatrix().getCoeffMatrix().gaussJordanElim();
        for(int r = 1; r <= m.rows; r++){
            if (m.getElmt(r, r) == 0) return 0;
        }
        return m.scalar;
    }

    // COFACTOR
    public Matrix cofactor(){
        Matrix answ = this.duplicateMatrix();

        Matrix newm;
        for(int r = 1; r <= answ.rows; r++){
            for(int c = 1; c <= answ.cols; c++){
                newm = answ.duplicateMatrix();

                answ.print();
                System.out.println("Sebelum direduce:");
                newm.print();

                newm = newm.reduce(r, c);

                System.out.printf("Setelah direduce %d %d:", r, c);
                newm.print();

                double det = newm.detGauss() * Math.pow(-1, r+c);
                answ.setElmt(r, c, det);
            }
        }
        return answ;
    }

    // ADJOIN (Cofactor transpose)
    public Matrix adjoin(){
        return this.duplicateMatrix().cofactor().transpose();
    }
}