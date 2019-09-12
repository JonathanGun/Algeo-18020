import java.util.Scanner;

public class Matrix{
    private double[][] TabInt;
    private int row, col;

    // Constructor
    public Matrix(int r, int c){
        this.TabInt = new double[r+1][c+1];
        this.row = r;
        this.col = c;
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
    public void input(){
        Scanner input = new Scanner(System.in);
        for(int i = 1; i <= this.row; i++) {
            for(int j = 1; j <= this.col; j++) {
                setElmt(i, j, input.nextDouble());
            }
        }
    }

    public void print(){
        for(int i = 1; i <= this.row; i++) {
            for(int j = 1; j <= this.col; j++) {
                System.out.printf("%f ", getElmt(i, j));
            }
            System.out.println("");
        }
    }
}