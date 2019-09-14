import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

public class MatrixSquare extends MatrixSPL{
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
                this.cols = this.rows+1;
            } catch (FileNotFoundException e){
                System.out.printf("File not found, will use keyboard");
            }
        }

        if (useKeyboard == 1){
            System.out.printf("Masukkan ordo matriks: "); this.rows = input.nextInt(); this.cols = this.rows+1;
        }

        makeEmpty();
        inputElements(input);
    }
    // Invers

    // DETERMINAN
    // Cramer
    private MatrixSquare reduce(MatrixSquare m, int rx, int cx){
        MatrixSquare newm = copyMatrix(m);
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
        return newm;
    }
    public double detCram(MatrixSquare m){
        // Basis
        if (m.rows == 1) return m.TabInt[1][1];

        // Rekursi
        double ans = 0;
        for(int c = 1; c <= m.cols; c++){
            ans += m.TabInt[1][c]*detCram(reduce(m, 1, c))*Math.pow(-1,c+1);
        }
        return ans;
    }

    // REF
    public double detREF(MatrixSquare m){
        m.gaussJordanElim();
        return m.scalar;
    }
    // Cofactor

    // Adjoin

    // Operasi Lain
    public MatrixSquare copyMatrix(MatrixSquare m){
        MatrixSquare newm = new MatrixSquare();
        newm.rows = m.rows;
        newm.cols = m.cols;
        newm.makeEmpty();
        for(int r = 1; r <= m.rows; r++){
            for(int c = 1; c <= m.cols; c++){
                newm.TabInt[r][c] = m.TabInt[r][c];
            }
        }
        return newm;
    }
}