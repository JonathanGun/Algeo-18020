import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

public class MatrixSquare extends MatrixSPL{
	public void inputMatrix(Scanner input){
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
                useKeyboard = 1;
            }
        }

        if (useKeyboard == 1){
            System.out.printf("Masukkan ordo matriks: ");
            this.rows = input.nextInt();
            this.cols = this.rows+1;
        }

        this.makeEmpty();
        this.inputElements(input);
    }
    // Invers

    // DETERMINAN
    // Cramer
    public double detCram(MatrixSquare m){
        // Basis
        if (m.rows == 1) return m.TabInt[1][1];

        // Rekursi
        double ans = 0;
        for(int c = 1; c <= m.cols; c++){
            MatrixSquare tmp = new MatrixSquare();
            copyMatrix(m, tmp);
            reduce(tmp,1,c);
            ans += m.TabInt[1][c]*detCram(tmp)*Math.pow(-1,c+1);
        }
        return ans;
    }

    // Gauss - EF
    public double detGauss(MatrixSquare m){
        m.gaussElim();
        for(int r = 1; r <= m.rows; r++){
            if (m.TabInt[r][r] == 0) return 0;
        }
        return m.scalar;
    }

    // Gauss Jordan - REF
    public double detGaussJordan(MatrixSquare m){
        m.gaussJordanElim();
        for(int r = 1; r <= m.rows; r++){
            if (m.TabInt[r][r] == 0) return 0;
        }
        return m.scalar;
    }

    // COFACTOR
    public MatrixSquare cofactor(MatrixSquare m){
        MatrixSquare newm = new MatrixSquare();
        MatrixSquare answ = new MatrixSquare();
        newm.makeEmpty();
        answ.rows = m.rows;
        answ.cols = m.cols;
        answ.makeEmpty();
        for(int r = 1; r <= m.rows; r++){
            for(int c = 1; c <= m.cols; c++){
                copyMatrix(m, newm);
                newm.reduce(newm,r,c);
                double det = newm.detGauss(newm);
                answ.setElmt(r,c,det);


            }
        }

        return answ; // tmp
    }

    // ADJOIN (Cofactor transpose)
    public MatrixSquare adjoin(MatrixSquare m){
        m = cofactor(m);
        m.transpose();
        return m;// tmp
    }

    // Operasi Lain
}