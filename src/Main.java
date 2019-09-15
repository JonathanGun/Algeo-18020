import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

public class Main {
    static Scanner input = new Scanner(System.in);
    private static int getMenu(){
        System.out.println("Menu");
        // MENU
        // 0. Lihat matriks tersimpan
        // 1. Sistem Persamaaan Linier
        // 2. Determinan
        // 3. Matriks balikan
        // 4. Matriks kofaktor
        // 5. Adjoin
        // 6. Interpolasi Polinom
        // 7. Keluar
        // return inputan user

        int choice;
        do{
            choice = input.nextInt();
            if ((choice < 0) || (choice > 7))
                System.out.println("input tidak valid. silakan ulangi");
        } while ((choice < 0) || (choice > 7));
        return choice;
    }

    private static int getSPLMethod(){
        System.out.println("Menu Metode SPL");
        // tampilkan menu metode 1..4
        // 1. Metode elim inasi Gauss
        // 2. Metode eliminasi Gauss -Jordan
        // 3. Metode matriks balikan
        // 4. Kaidah Cramer
        // return inputan user
        int choice;
        do{
            choice = input.nextInt();
            if ((choice < 1) || (choice > 4))
                System.out.println("input tidak valid. silakan ulangi");
        } while ((choice < 1) || (choice > 4));
        return choice;
    }

    private static int getDetMethod(){
        System.out.println("Menu Metode Determinan");
        // tampilkan menu metode 1..4
        // 1. Metode elim inasi Gauss
        // 2. Metode eliminasi Gauss -Jordan
        // 3. Metode matriks balikan
        // 4. Kaidah Cramer
        // return inputan user
        int choice;
        do{
            choice = input.nextInt();
            if ((choice < 1) || (choice > 4))
                System.out.println("input tidak valid. silakan ulangi");
        } while ((choice < 1) || (choice > 4));
        return choice;
    }

    public static void main(String[] args){
        Matrix m = new Matrix();
        MatrixSPL mspl = new MatrixSPL();
        MatrixSquare msq = new MatrixSquare();

        int choice = getMenu();
        while (choice != 7){
            if (choice == 0){
                m.print();
            // Sistem Persamaan Linier
            } else if (choice == 1){
                mspl.inputMatrix(input);
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
                msq.inputMatrix(input);
                msq.convertToCoeff();
                int detchoice = getDetMethod();
                if (detchoice == 1){
                    System.out.println("Menggunakan metode eliminasi gauss (EF):");
                    System.out.println(msq.detGauss(msq));
                } else if (detchoice == 2){
                    System.out.println("Menggunakan metode eliminasi gauss-jordan (REF):");
                    System.out.println(msq.detGaussJordan(msq));
                } else if (detchoice == 3){

                } else if (detchoice == 4){
                    System.out.println("Menggunakan metode cramer:");
                    System.out.println(msq.detCram(msq));
                }
                msq.print();
                
            } else if (choice == 3){
                
            } else if (choice == 4){
                
            } else if (choice == 5){
                
            } else if (choice == 6){
                
            }

            choice = getMenu();
        }
    }
}