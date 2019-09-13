import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

public class Matrix{
    private double[][] TabInt;
    private int rows, cols;

    // Constructor
    public Matrix(){
        
    }

    public void makeEmpty(int r, int c){
        this.TabInt = new double[r+1][c+1];
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
                setElmt(r, c, input.nextDouble());
            }
        }
    }

    public void inputMatrix(){
        Scanner input = new Scanner(System.in);

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
            }
        }

        if (useKeyboard == 1){
            System.out.printf("Masukkan jumlah baris: "); this.rows = input.nextInt();
            System.out.printf("Masukkan jumlah kolom: "); this.cols = input.nextInt();
        }

        makeEmpty(this.rows, this.cols);
        inputElements(input);
    }

    public void print(){
        for(int r = 1; r <= this.rows; r++) {
            for(int c = 1; c <= this.cols; c++) {
                System.out.printf("%.2f ", getElmt(r, c));
            }
            System.out.println("");
        }
    }

    // Elementary Row Operation
    // Swap Row
    private void swapRow(int r1, int r2){
        for(int c = 1; c <= this.cols; c++){
            double tmp = this.TabInt[r1][c];
            this.TabInt[r1][c] = this.TabInt[r2][c];
            this.TabInt[r2][c] = tmp;
        }
    }

    // Scale Row
    private void scaleRow(int r, double k){
        for (int c = r; c <= this.cols; c++){
            this.TabInt[r][c] *= k;    
        }
    }

    // GAUSS-JORDAN
    // Gauss
    public void gaussElim(){
        // int r = 2;
        // Cek ada solusi/tidak/banyak
        // for(int c = 1; this.rows+c <= this.cols; c++){
        //     while(r <= this.rows && this.TabInt[1][c] == 0){
        //         swapRow(1, r);
        //         r++;
        //     }
        // }
        // if (r == this.rows){
        //     System.out.printf("Solusi tidak ada");
        // }
        
        // Kasus normal (ada 1 solusi) : ukuran matriks NxN
        for (int pivot = 1; pivot <= this.rows; pivot++){
            for(int r = pivot+1; r <= this.rows; r++){
                double k = this.TabInt[r][pivot]/this.TabInt[pivot][pivot];
                for (int c = pivot; c <= this.cols; c++){
                    this.TabInt[r][c] -= k*this.TabInt[pivot][c];
                }
            }
        }

        for (int r = 1; r <= this.rows; r++){
            double k = this.TabInt[r][r];
            scaleRow(r, 1/k);
        }
    }

    // Jordan
    private void jordanElim(){
        for (int pivot = this.rows; pivot >= 1; pivot--){
            for(int r = pivot-1; r >= 1; r--){
                double k = this.TabInt[r][pivot]/this.TabInt[pivot][pivot];
                for (int c = this.cols; c > r; c--){
                    this.TabInt[r][c] -= k*this.TabInt[pivot][c];
                }
            }
        }
    }

    // Gauss Jordan
    public void gaussJordanElim(){
        gaussElim();
        jordanElim();
    }

    // Invers

    // Determinan

    // Cofactor

    // Adjoin

    // Interpolasi Polinom

}