import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

public class Matrix{
    protected double[][] TabInt;
    protected int rows, cols;
    protected double scalar;

    // Constructor
    public Matrix(){
        this.rows = 0;
        this.cols = 0;
        this.scalar = 1;
    }

    public void makeEmpty(){
        this.TabInt = new double[this.rows+5][this.cols+5];
    }

    // Selector
    protected double getElmt(int r, int c){
        return this.TabInt[r][c];
    }

    // Setter
    protected void setElmt(int r, int c, double x){
        this.TabInt[r][c] = x;
    }

    // I/O
    protected void inputElements(Scanner input){
        for(int r = 1; r <= this.rows; r++) {
            for(int c = 1; c <= this.cols; c++) {
                setElmt(r, c, input.nextDouble());
            }
        }
    }

    public void inputMatrix(Scanner input){
        System.out.printf("Input from file/keyboard? (0/1)");
        int useKeyboard = input.nextInt();
        
        if (useKeyboard == 0){
            System.out.printf("Masukkan nama file+extension: ");
            String fileName = input.next();
            try{
                input = new Scanner(new File("../test/" + fileName));
                this.rows = input.nextInt();
                this.cols = input.nextInt();
            } catch (FileNotFoundException e){
                System.out.printf("File not found, will use keyboard");
                useKeyboard = 1;
            }
        }

        if (useKeyboard == 1){
            System.out.printf("Masukkan jumlah baris: "); this.rows = input.nextInt();
            System.out.printf("Masukkan jumlah kolom: "); this.cols = input.nextInt();
        }

        this.makeEmpty();
        this.inputElements(input);
    }

    public void print(){
        if (this.rows == 0) System.out.println("Matriks kosong!");
        for(int r = 1; r <= this.rows; r++) {
            System.out.printf("|");
            for(int c = 1; c <= this.cols; c++) {
                System.out.printf("%.2f ", getElmt(r, c));
            }
            System.out.println("|");
        }
    }

    // Elementary Row Operation
    // Swap Row
    protected void swapRow(int r1, int r2){
        this.scalar = -this.scalar;
        for(int c = 1; c <= this.cols; c++){
            double tmp = this.TabInt[r1][c];
            this.TabInt[r1][c] = this.TabInt[r2][c];
            this.TabInt[r2][c] = tmp;
        }
    }

    // Scale Row
    protected void scaleRow(int r, double k){
        this.scalar /= k;
        for(int c = r; c <= this.cols; c++){
            this.TabInt[r][c] *= k;
        }
    }

    // Add Row
    protected void addRow(int r1, int r2, double k){
        for(int c = 1; c <= this.cols; c++){
            this.TabInt[r1][c] += k*this.TabInt[r2][c];
        }
    }

    // Operasi Lain
    public boolean isSquareMatrix(){
        return (this.rows+1 == this.cols);
    }

    protected void convertToCoeff(){
        this.cols--;
    }

    public void copyMatrix(Matrix m, Matrix newm){
        newm = new Matrix();
        newm.rows = m.rows;
        newm.cols = m.cols;
        newm.makeEmpty();
        for(int r = 1; r <= m.rows; r++){
            for(int c = 1; c <= m.cols; c++){
                newm.TabInt[r][c] = m.TabInt[r][c];
            }
        }
    }

    protected void reduce(Matrix m, int rx, int cx){
        Matrix newm = new Matrix();
        copyMatrix(m, newm);
        for(int r = 1; r <= m.rows; r++){
            for(int c = 1; c <= m.cols; c++){
                int newr = r, newc = c;
                if (r > rx) newr--;
                if (c > cx) newc--;
                newm.TabInt[newr][newc] = m.TabInt[r][c];
            }
        }
        newm.cols--;
        newm.rows--;
        copyMatrix(newm, m);
    }

    protected void transpose(){
        Matrix newm = new Matrix();
        newm.rows = this.cols;
        newm.cols = this.rows;
        newm.makeEmpty();
        for(int r = 1; r <= newm.rows; r++){
            for(int c = 1; c <= newm.cols; c++){
                newm.TabInt[r][c] = this.TabInt[c][r];
            }
        }
        copyMatrix(newm, this);
    }

    protected boolean isAllZero(int r){
        for(int c = 1; c <= this.cols; c++){
            if (this.TabInt[r][c] != 0) return false;
        }
        return true;
    }
}