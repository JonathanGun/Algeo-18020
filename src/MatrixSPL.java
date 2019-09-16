import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

public class MatrixSPL extends Matrix{
	public double[] Solution;

    // GAUSS-JORDAN
    // Gauss
    public void gaussElim(){
        // Kasus normal (ada 1 solusi) : ukuran matriks NxN
        for (int pivot = 1; pivot <= this.rows; pivot++){
        	// cek ElmtDiag ke-pivot, jika 0 tukar dengan yg tidak 0
        	if (this.TabInt[pivot][pivot] == 0){
        		for(int r = pivot+1; r <= this.rows; r++){
        			if (this.TabInt[r][pivot] != 0){
        				this.swapRow(r, pivot);
        				break;
        			}
        		}
        	}

        	// cek lagi, jika masih 0 maka memang 0 nya di bawahnya
        	if (this.TabInt[pivot][pivot] == 0) continue;

        	// kurangi semua baris di bawah pivot dengan k*baris pivot, sehingga
        	// angka di bawah pivot jadi 0 semua
            for(int r = pivot + 1; r <= this.rows; r++){
                double k = this.TabInt[r][pivot]/this.TabInt[pivot][pivot];
                if (k != 0) this.addRow(r, pivot, -k);
            }

            // ubah elmt ke-pivot menjadi 1, agar menjadi echelon form
            scaleRow(pivot, 1/this.TabInt[pivot][pivot]);
        }
    }

    // Jordan
    private void jordanElim(){
        for (int pivot = this.rows; pivot >= 1; pivot--){
        	if (this.TabInt[pivot][pivot] == 0) continue;
            for(int r = pivot-1; r >= 1; r--){
                double k = this.TabInt[r][pivot]/this.TabInt[pivot][pivot];
                if (k != 0) this.addRow(r, pivot, -k);
            }
        }
    }

    // Gauss Jordan
    public void gaussJordanElim(){
        gaussElim();
        jordanElim();
    }


    // Interpolasi
    public void inputInterpolation(Scanner input){
        this.rows = input.nextInt();
        this.cols = this.rows+1;
        this.makeEmpty();
        for(int r = 1; r <= this.rows; r++){
            double x, y;
            x = input.nextDouble();
            y = input.nextDouble();
            for(int c = 1; c <= this.cols; c++){
                if (c != this.cols) this.setElmt(r, c, Math.pow(x, c-1));
                else this.setElmt(r, c, y);
            }
        }
    }

    // Invers
    public Matrix inversSPL (Matrix m) {
        Matrix newx = new Matrix();
        MatrixSquare newm = new MatrixSquare();
        Matrix answ = new Matrix();
        newx.makeEmpty();
        answ.makeEmpty();
        newm.makeEmpty();
        copyMatrix(m, newm);
        newm.convertToCoeff();
        newx.rows = m.rows;
        newx.cols = 1;
        for(int r = 1; r <= m.rows; r++){
            int c = 1;
            double b = m.getElmt(r,m.cols);
            newx.setElmt(r,c,b);
        }
        newm = newm.invCram(newm);
        answ.rows = m.rows;
        answ.cols = 1;
        answ = kaliMatrix(newm.convertToMatrix(), newx);
        return answ;
    }

    // Operasi lain
    public void getSolution(){
    	for(int r = 1; r <= this.rows; r++){
    		if (this.isAllZero(r)){
    			this.Solution = new double[0];
    			return;
    		}
    	}
    	this.Solution = new double[this.rows+1];
    	for(int r = 1; r <= this.rows; r++){
    		this.Solution[r] = this.TabInt[r][this.cols];
    	}
    }

    public void printSolution(){
        System.out.println("Solusi dari matriks SPL:");
        for(int r = 1; r <= this.rows; r++){
            System.out.printf("X%d = %f\n", r, this.Solution[r]);
        }
    }
}