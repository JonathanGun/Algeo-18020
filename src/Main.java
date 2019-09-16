import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

public class Main {
    static Scanner input = new Scanner(System.in);
    private static int getMenu(){
        System.out.println("Menu");
        System.out.println("0. Lihat matriks tersimpan");
        System.out.println("1. Sistem Persamaan Linier");
        System.out.println("2. Determinan");
        System.out.println("3. Matriks balikan");
        System.out.println("4. Matriks kofaktor");
        System.out.println("5. Adjoin");
        System.out.println("6. Interpolasi Polinom");
        System.out.println("7. Keluar");

        int choice;
        do{
            System.out.println("Apa yang ingin Anda lakukan?");
            choice = input.nextInt();
            if ((choice < 0) || (choice > 7))
                System.out.println("Input tidak valid. Silakan ulangi!");
        } while ((choice < 0) || (choice > 7));
        return choice;
    }

    private static int getSPLMethod(){
        System.out.println("Menu Metode SPL");
        System.out.println("1. Metode eliminasi Gauss");
        System.out.println("2. Metode eliminasi Gauss-Jordan");
        System.out.println("3. Metode matriks balikan");
        System.out.println("4. Kaidah Cramer");
        int choice;
        do{
            System.out.println("Metode apa yang Anda inginkan?");
            choice = input.nextInt();
            if ((choice < 1) || (choice > 4))
                System.out.println("Input tidak valid. Silakan ulangi!");
        } while ((choice < 1) || (choice > 4));
        return choice;
    }

    private static int getDetMethod(){
        System.out.println("Menu Metode Determinan");
        System.out.println("1. Metode eliminasi Gauss");
        System.out.println("2. Metode eliminasi Gauss-Jordan");
        System.out.println("3. Metode matriks balikan");
        System.out.println("4. Kaidah Cramer");
        int choice;
        do{
            System.out.println("Metode apa yang Anda inginkan?");
            choice = input.nextInt();
            if ((choice < 1) || (choice > 4))
                System.out.println("Input tidak valid. Silakan ulangi!");
        } while ((choice < 1) || (choice > 4));
        return choice;
    }

    private static int getInvMethod(){
        System.out.println("Menu Metode Matriks Balikan");
        System.out.println("1. Metode eliminasi Gauss-Jordan (REF)");
        System.out.println("2. Kaidah Cramer");
        int choice;
        do{
            System.out.println("Metode apa yang Anda inginkan?");
            choice = input.nextInt();
            if((choice<1) || (choice>2))
                System.out.println("Input tidak valid. Silakan ulangi!");
        }while((choice<1) || (choice>2));
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
                    System.out.println("Menggunakan metode eliminasi Gauss (EF):");
                    mspl.gaussElim();
                } else if (splchoice == 2){
                    System.out.println("Menggunakan metode eliminasi Gauss-Jordan (REF):");
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
                    System.out.println("Menggunakan metode eliminasi Gauss (EF):");
                    System.out.println(msq.detGauss(msq));
                } else if (detchoice == 2){
                    System.out.println("Menggunakan metode eliminasi Gauss-Jordan (REF):");
                    System.out.println(msq.detGaussJordan(msq));
                } else if (detchoice == 3){

                } else if (detchoice == 4){
                    System.out.println("Menggunakan Metode Cramer:");
                    System.out.println(msq.detCram(msq));
                }
                msq.print();
                
            } else if (choice == 3){
                msq.inputMatrix(input);
                msq.convertToCoeff();
                int invchoice = getInvMethod();
                if(invchoice == 1) {
                    System.out.println("Menggunakan metode eliminasi Gauss-Jordan (REF):");
                    System.out.println(msq.invGaussJordan(msq));
                } else if(invchoice == 2) {
                    System.out.println("Menggunakan Metode Crammer");
                    System.out.println(msq.invCram((msq)));
                }
                msq.print();
                
            } else if (choice == 4){
                msq.inputMatrix(input);
                msq.convertToCoeff();
                System.out.println(msq.cofactor(msq));
                
            } else if (choice == 5){
                msq.inputMatrix(input);
                msq.convertToCoeff();
                System.out.println(msq.adjoin(msq));
                
            } else if (choice == 6){
                //interpolasi
            }

            choice = getMenu();
        }
    }
}