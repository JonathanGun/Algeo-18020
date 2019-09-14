import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

public class Main {
    private static int getMenu(){
        // tampilkan menu 1..7
        // MENU
        // 1. Sistem Persamaaan Linie r
        // 2. Determinan
        // 3. Matriks balikan
        // 4. Matriks kofaktor
        // 5. Adjoin
        // 6. Interpolasi Polinom
        // 7. Keluar
        // return inputan user
        return 2; // sementara
    }

    private static int getSPLMethod(){
        // tampilkan menu metode 1..4
        // 1. Metode elim inasi Gauss
        // 2. Metode eliminasi Gauss -Jordan
        // 3. Metode matriks balikan
        // 4. Kaidah Cramer
        // return inputan user
        return 2; // sementara
    }

    private static int getDetMethod(){
        // tampilkan menu metode 1..4
        // 1. Metode elim inasi Gauss
        // 2. Metode eliminasi Gauss -Jordan
        // 3. Metode matriks balikan
        // 4. Kaidah Cramer
        // return inputan user
        return 1; // sementara
    }

    public static void main(String[] args){
        Matrix m = new Matrix();
        MatrixSPL mspl = new MatrixSPL();
        MatrixSquare msq = new MatrixSquare();
        
        int choice = getMenu();
        while (choice != 7){
            // Sistem Persamaan Linier
            if (choice == 1){
                mspl.inputMatrix();
                mspl.print();
                int splchoice = getSPLMethod();
                if (splchoice == 1){
                    System.out.println("Menggunakan metode eliminasi gauss:");
                    mspl.gaussElim();
                } else if (splchoice == 2){
                    System.out.println("Menggunakan metode eliminasi gauss-jordan:");
                    mspl.gaussJordanElim();
                } else if (splchoice == 3){

                } else if (splchoice == 4){

                }
                mspl.print();

            } else if (choice == 2){
                msq.inputMatrix();
                msq.print();
                msq.convertToCoeff();
                int detchoice = getDetMethod();
                if (detchoice == 1){
                    System.out.println("Menggunakan metode cramer:");
                    System.out.println(msq.detCram(msq));
                } else if (detchoice == 2){
                    System.out.println("Menggunakan metode eliminasi gauss-jordan (REF):");
                    System.out.println(msq.detREF(msq));
                } else if (detchoice == 3){

                } else if (detchoice == 4){

                }
                
            } else if (choice == 3){
                
            } else if (choice == 4){
                
            } else if (choice == 5){
                
            } else if (choice == 6){
                
            }

            choice = getMenu();
            choice = 7; // sementara
        }
    }
}