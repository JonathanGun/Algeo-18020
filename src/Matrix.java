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
    private void inputFromKeyboard(Scanner input){
        for(int r = 1; r <= this.rows; r++) {
            for(int c = 1; c <= this.cols; c++) {
                setElmt(r, c, input.nextDouble());
            }
        }
    }

    private void inputFromFile(Scanner fileinput){
        for(int r = 1; r <= this.rows; r++) {
            for(int c = 1; c <= this.cols; c++) {
                setElmt(r, c, fileinput.nextDouble());
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
                Scanner fileInput = new Scanner(new File("../test/" + fileName));
                this.rows = fileInput.nextInt();
                this.cols = fileInput.nextInt();
                makeEmpty(this.rows, this.cols);
                inputFromFile(fileInput);
            } catch (FileNotFoundException e){
                System.out.printf("File not found, will use keyboard");
                useKeyboard = 1;
            }
        }

        if (useKeyboard == 1){
            System.out.printf("Masukkan jumlah baris: ");
            this.rows = input.nextInt();
            System.out.printf("Masukkan jumlah kolom: ");
            this.cols = input.nextInt(); 
            makeEmpty(this.rows, this.cols);
            inputFromKeyboard(input);
        }
    }

    public void print(){
        for(int r = 1; r <= this.rows; r++) {
            for(int c = 1; c <= this.cols; c++) {
                System.out.printf("%f ", getElmt(r, c));
            }
            System.out.println("");
        }
    }
}