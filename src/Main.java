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
        System.out.println("3. Kaidah Cramer");
        int choice;
        do{
            System.out.println("Metode apa yang Anda inginkan?");
            choice = input.nextInt();
            if ((choice < 1) || (choice > 3))
                System.out.println("Input tidak valid. Silakan ulangi!");
        } while ((choice < 1) || (choice > 3));
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
        Matrix m = new Matrix(0, 0);

        int choice = getMenu();
        while (choice != 7){
            if (choice == 0){
                m.showMatrix();

            // Sistem Persamaan Linier
            } else if (choice == 1){
                m.inputMatrix(input, false);
                int splchoice = getSPLMethod();
                if (splchoice == 1){
                    System.out.println("Menggunakan metode eliminasi Gauss (EF):");
                    m.splGauss();
                } else if (splchoice == 2){
                    System.out.println("Menggunakan metode eliminasi Gauss-Jordan (REF):");
                    m.splGaussJordan();
                } else if (splchoice == 3){
                    System.out.println("Menggunakan metode Matriks Balikan (invers):");
                    m.splInv();
                } else if (splchoice == 4){
                    System.out.println("Menggunakan metode Kaidah Cramer (determinan):");
                    m.splCram();
                }

            // Determinan
            } else if (choice == 2){
                m.inputMatrix(input, true);
                int detchoice = getDetMethod();
                if (detchoice == 1){
                    System.out.println("Menggunakan metode eliminasi Gauss (EF):");
                    System.out.println(m.detGauss());
                } else if (detchoice == 2){
                    System.out.println("Menggunakan metode eliminasi Gauss-Jordan (REF):");
                    System.out.println(m.detGaussJordan());
                } else if (detchoice == 3){
                    System.out.println("Menggunakan Metode Cramer:");
                    System.out.println(m.detCram());
                }
            
            // Invers
            } else if (choice == 3){
                m.inputMatrix(input, true);
                int invchoice = getInvMethod();
                if(invchoice == 1) {
                    System.out.println("Menggunakan metode eliminasi Gauss-Jordan (REF):");
                    m.invGaussJordan();
                } else if(invchoice == 2) {
                    System.out.println("Menggunakan Metode Crammer");
                    m.invCram();
                }
            
            // Cofactor
            } else if (choice == 4){
                m.inputMatrix(input, true);
                m.cofactor();
            
            // Adjoin
            } else if (choice == 5){
                m.inputMatrix(input, true);
                m.adjoin();
            
            // Interpolation
            } else if (choice == 6){
                m.inputInterpolation(input);
                m.interpolate();
            }

            System.out.println();
            choice = getMenu();
        }
        System.out.printf("Terima kasih!")
    }
}