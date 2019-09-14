import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

public class MatrixSPL extends Matrix{
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

        makeEmpty();
        inputElements(input);
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
}