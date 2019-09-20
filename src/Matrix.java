import java.util.Scanner;
import java.util.Vector;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

public class Matrix{
    private double[][] tabInt;
    private int rows, cols;
    private double scalar;
    private double[] solution;
    private boolean isInterpolationMatrix;
    private String[] solutionString;

    // Constructor
    public Matrix(int r, int c){
        this.rows   = r;
        this.cols   = c;
        this.scalar = 1;
        this.tabInt = new double[this.rows+5][this.cols+5];
        this.solution       = new double[this.cols+5];
        this.solutionString = new String[this.cols+5];
    }

    // Selector
    private double getElmt(int r, int c){
        return this.tabInt[r][c];
    }

    // Setter
    private void setElmt(int r, int c, double x){
        this.tabInt[r][c] = x;
    }

    // I/O
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

    private void printInterpolationData(){
        for(int r = 1; r <= this.rows; r++){
            System.out.printf("%.5f %.5f\n",this.getElmt(r, 2), this.getElmt(r, this.cols));
        }
    }

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
                }
                else{
                    System.out.println("File sudah ada!");
                }
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void inputElements(Scanner input){
        for(int r = 1; r <= this.rows; r++) {
            for(int c = 1; c <= this.cols; c++) {
                this.setElmt(r, c, input.nextDouble());
            }
        }
    }

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

    private void getDimension(Scanner input){
        this.rows = 1;
        this.cols = input.nextLine().split(" ").length;
        while(input.hasNextLine()){
            input.nextLine();
            this.rows += 1;
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
                input = new Scanner(inputFile);
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

        this.tabInt = new double[this.rows+5][this.cols+5];
        this.solution = new double[this.rows+5];
        this.inputElements(input);
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
        this.tabInt = new double[this.rows+5][this.cols+5];
        this.solution = new double[this.rows+5];
        this.inputInterpolationData(input);
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
        Matrix m = new Matrix(this.rows, this.cols);
        m.scalar = this.scalar;
        m.solution = this.solution;
        m.solutionString = this.solutionString;
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

    private boolean isValidRow(int r){
        for(int c = 1; c < this.cols; c++){
            if (this.getElmt(r, c) != 0) return true;
        }
        return (this.getElmt(r, this.cols) == 0);
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

    private boolean hasSolution(){
        Matrix m = this.duplicateMatrix();
        m.gaussElim();
        for(int r = 1; r <= m.rows; r++){
            if (!m.isValidRow(r)) return false;
        }
        return true;
    }

    // ====================================== 1. BAGIAN SPL ========================================== //

    public void splGauss(){
        Matrix ans = this.duplicateMatrix()
                         .gaussElim();
        ans.print();
        if (!ans.hasSolution()) System.out.println("Tidak ada solusi");
        else {
            ans.getSolution();
            this.solution = ans.solution;
            ans.printSolution();
            this.solutionString = ans.solutionString;
            ans.printSolutionString();
        }
    }

    public void splGaussJordan(){
        Matrix ans = this.duplicateMatrix()
                         .gaussJordanElim();
        ans.print();
        if (!ans.hasSolution()) System.out.println("Tidak ada solusi");
        else {
            ans.getSolution();
            this.solution = ans.solution;
            ans.printSolution();
            this.solutionString = ans.solutionString;
            ans.printSolutionString();
        }
    }

    // Invers
    public void splInv() {
        // AX = B, maka X = A^-1 B
        // A: getCoeffMatrix, B: getLastCol
        if(this.isSquareMatrix()) {
            if(this.detGauss() == 0){
                System.out.println("Tidak bisa dicari matriks invers! Determinannya 0!");
            } else {
                this.print();
                Matrix a = this.duplicateMatrix();
                Matrix b = this.getLastCol();
                a = a.invCramUtil();
                Matrix ans = multMatrix(a,b);
                for(int r = 1; r <= this.rows; r++){
                    this.solutionString[r] = String.format("X%d = %.4f", r, ans.getElmt(r, 1));
                }
                this.printSolutionString();
            }
        }
        else{
            System.out.println("Tidak bisa dicari solusinya dengan metode ini karena tidak bisa didapat inversnya!");
            System.out.println("Silakan coba metode lain.");
        }
    }


    public void splCram(){
        // yang ditukar kolom i dengan kolom terakhir (manfaatkan getlastcol)
        if(this.isSquareMatrix()) {
            if (this.hasSolution()) {
                double denom = this.detGauss();
                if (denom == 0) {
                    System.out.println("Matriks ini determinan 0, tidak bisa ditentukan dengan metode Crammer");
                    System.out.println("Silakan mencoba metode lain");
                } else {
                    Matrix a = this.getLastCol();
                    for (int c = 1 ; c <= this.cols; c++) {
                        Matrix ans = this.duplicateMatrix();
                        for (int r = 1 ; r <= this.rows ; r++) {
                            ans.setElmt(r, c, a.getElmt(r, 1));
                        }
                        this.solutionString[c] = String.format("X%d = %.4f", c, ans.detGauss() / denom);
                    }
                    this.printSolutionString();
                }
            }
        } else{
            System.out.println("Tidak bisa dicari solusinya dengan metode ini karena matriks tidak berbentuk persegi!");
            System.out.println("Silakan coba metode lain.");
        }

    }
    

    // GAUSS-JORDAN
    // Gauss
    private Matrix gaussElim(){
        Matrix m = this.duplicateMatrix();
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
            m.scaleRow(pivot, 1/m.getElmt(pivot, pivot));
        }
        m.scalar *= this.scalar;
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
        return this.gaussElim()
                   .jordanElim();
    }

    // Operasi lain
    private void getSolution(){
        for(int r = this.rows; r >= 1; r--){
            this.solution[r] = this.getElmt(r, this.cols)/this.getElmt(r, r);
        }
        for(int c = this.rows+1; c <= this.cols-1; c++){
            this.solution[c] = 0.0/0.0;
        }
    }

    private void printSolution(){
        System.out.println("Solusi dari matriks SPL:");
        Vector<Integer> freeVar = new Vector<>();
        for(int c = this.cols-1; c > this.rows; c--){
            freeVar.add(c);
            this.solutionString[c] = String.format("X%d = %c\n", c, 'a'+freeVar.size()-1);
        }
        for(int r = this.rows; r >= 1; r--){
            String tmp = String.format("X%d = ", r);
            if(Double.isNaN(this.solution[r])){
                freeVar.add(r);
                tmp += String.format("%c ", 'a'+freeVar.size()-1);
            } else {
                for(int c = this.cols-1; c > r; c--){
                    if(this.getElmt(r, c) != 0){
                        this.solution[r] -= this.getElmt(r, c)*this.solution[c];
                    }
                }
                tmp += String.format("%.4f ", this.solution[r]);
                for(int i = 0; i <= freeVar.size()-1; i++){
                    double x = this.getElmt(r, freeVar.get(i));
                    if(x < 0) tmp += String.format("+ %.2f%c ", -x, 'a'+i);
                    if(x > 0) tmp += String.format("- %.2f%c ", x, 'a'+i);
                }
            }
            this.solutionString[r] = tmp;
        }
    }

    private void printSolutionString(){
        for(int i = 1; i <= this.cols-1; i++){
            System.out.println(this.solutionString[i]);
        }
    }

    // ================================== 2. BAGIAN INTERPOLASI ==================================== //
    public void interpolate(Scanner input){
        Matrix ans = this.gaussJordanElim();
        ans.getSolution();
        this.solution = ans.solution;
        if (this.canInterpolate()) System.out.println("Solusi interpolasi tidak dapat ditemukan");
        else {
            this.printSolutionInterpolation();
            System.out.printf("Nilai y = %.4f", this.valueFunction(input));
        }
    }

    private boolean canInterpolate(){
        for(int i = 1; i <= this.cols-1; i++){
            if(Double.isInfinite(this.solution[i])) return false;
        }
        return true;
    }

    private double nilaiMaksimal() {
        double maks = this.getElmt(1,2);

        for(int r=1; r<=this.rows; r++) {
            if(maks < getElmt(r, 2)) {
                maks = getElmt(r, 2);
            }
        }
        
        return maks;
    }

    private double nilaiMinimum() {
        double min = this.getElmt(1, 2);

        for(int r=1; r<=this.rows; r++) {
            if(min > getElmt(r, 2)) {
                min = getElmt(r, 2);
            }
        }

        return min;
    }

    private void printSolutionInterpolation() {
        System.out.print("f(x) = ");
        for(int i = 1; i <= this.cols; i++) {
            if(this.solution[i] == 0) {
                i++;
            } else if(i == 1) {
                System.out.printf("%.4f ", this.solution[i]);
            } else {
                if(this.solution[i] > 0) System.out.printf("+ %.4f", this.solution[i]);
                else System.out.printf("- %.4f", -this.solution[i]);

                if(i == 2) System.out.print("x");
                else System.out.printf("x^%d", i - 1);
            }
        }
        System.out.println();
    }

    public double valueFunction(Scanner input) {
        double x;
        do{
            System.out.print("Masukkan nilai x di dalam range yang ingin ditaksir: ");
            x = input.nextDouble();
            if((x<this.nilaiMinimum()) || (x>this.nilaiMaksimal())) {
                System.out.println("Titik tidak di dalam range. Silakan ulangi.");
            }
        }while((x<this.nilaiMinimum()) || (x>this.nilaiMaksimal()));

        double hasil = 0;
        for(int c=1; c<=this.cols; c++) {
            hasil+=this.solution[c]*(Math.pow(x, c-1));
        }

        return hasil;
    }

    // ================================== 3. BAGIAN MATRIKS NxN =================================== //

    // INVERS
    // Gauss-Jordan
    public void invGaussJordan(){
        if (this.detGauss() == 0){
            System.out.println("Tidak bisa dicari matriks invers! Determinannya 0!");
        } else {
            this.duplicateMatrix()
                .invGaussJordanUtil()
                .print();
        }
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
        if (this.detGauss() == 0){
            System.out.println("Tidak bisa dicari matriks invers! Determinannya 0!");
        } else {
            Matrix m = this.duplicateMatrix().invCramUtil();
            if (m.getElmt(1,1) == Double.NaN) System.out.println("Tidak bisa dicari matriks invers! Determinannya 0!");
            else m.print();
        }
    }

    private Matrix invCramUtil(){
        // 1/det * adjoin
        Matrix ans = this.duplicateMatrix().getCoeffMatrix();
        double x = ans.detCramUtil();
        if (x == 0){
            ans.setElmt(1, 1, Double.NaN);
            return ans;
        }

        ans = ans.getAdjoin();
        for(int r = 1; r <= this.rows; r++) {
            for(int c = 1; c <= this.cols; c++) {
                ans.tabInt[r][c] /= x;
            }
        }
        return ans;
    }

    // DETERMINAN
    // Cramer
    public double detCram(){
        return this.duplicateMatrix()
                   .getCoeffMatrix()
                   .detCramUtil();
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

    // Gauss - EF
    public double detGauss(){
        Matrix m = this.duplicateMatrix()
                       .gaussElim();
        for(int r = 1; r <= m.rows; r++){
            if (m.getElmt(r, r) == 0) return 0;
        }
        return m.scalar;
    }

    // Gauss Jordan - REF
    public double detGaussJordan(){
        Matrix m = this.duplicateMatrix()
                       .gaussJordanElim();
        for(int r = 1; r <= m.rows; r++){
            if (m.getElmt(r, r) == 0) return 0;
        }
        return m.scalar;
    }

    // Cofactor
    public void cofactor(){
        this.getCoeffMatrix()
            .getCofactor()
            .print();
    }

    private Matrix getCofactor(){
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

    // Adjoin (Cofactor transpose)
    public void adjoin(){
        this.getCoeffMatrix()
            .getAdjoin()
            .print();
    }

    public Matrix getAdjoin(){
        return this.duplicateMatrix().getCofactor().transpose();
    }
}