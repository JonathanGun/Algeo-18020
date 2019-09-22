import java.util.Scanner;

public class Main {
    static Scanner input = new Scanner(System.in);
    static Matrix m = new Matrix(0, 0);

    private static void menu(){
        System.out.println("Menu");
        System.out.println("0. Lihat matriks tersimpan");
        System.out.println("1. Sistem Persamaan Linier");
        System.out.println("2. Determinan");
        System.out.println("3. Matriks balikan");
        System.out.println("4. Matriks kofaktor");
        System.out.println("5. Adjoin");
        System.out.println("6. Interpolasi Polinom");
        System.out.println("7. Keluar");
        System.out.println("Apa yang ingin Anda lakukan?");

        int choice = input.nextInt();
        switch(choice){
            case 0: 
                m.showMatrix();
                break;
            case 1:
                m.inputMatrix(input, false);
                spl();
                break;
            case 2:
                m.inputMatrix(input, true);
                det();
                break;
            case 3:
                m.inputMatrix(input, true);
                inv();
                break;
            case 4:
                m.inputMatrix(input, true);
                m.cofactor();
                break;
            case 5:
                m.inputMatrix(input, true);
                m.adjoin();
                break;
            case 6:
                m.inputInterpolation(input);
                m.interpolate(input);
                break;
            case 7:
                break;
            default:
                System.out.println("Input tidak valid. Silakan ulangi!");
                System.out.println("Apa yang ingin Anda lakukan?");
                choice = input.nextInt();
        }
        if(choice == 7) System.out.println("Terima kasih!");
        else menu();
    }

    private static void spl(){
        System.out.println("Menu Metode SPL");
        System.out.println("1. Metode eliminasi Gauss");
        System.out.println("2. Metode eliminasi Gauss-Jordan");
        System.out.println("3. Metode matriks balikan");
        System.out.println("4. Kaidah Cramer");
        System.out.println("Metode apa yang Anda inginkan?");
        int choice = input.nextInt();
        switch(choice){
            case 1:
                System.out.println("Menggunakan metode eliminasi Gauss (EF):");
                m.splGauss();
                break;
            case 2:
                System.out.println("Menggunakan metode eliminasi Gauss-Jordan (REF):");
                m.splGaussJordan();
                break;
            case 3:
                System.out.println("Menggunakan metode Matriks Balikan (invers):");
                m.splInv();
                break;
            case 4:
                System.out.println("Menggunakan metode Kaidah Cramer (determinan):");
                m.splCram();
                break;
            default:
                System.out.println("Input tidak valid. Silakan ulangi!");
                System.out.println("Metode apa yang Anda inginkan?");
                choice = input.nextInt();
        }
    }

    private static void det(){
        System.out.println("Menu Metode Determinan");
        System.out.println("1. Metode eliminasi Gauss");
        System.out.println("2. Metode eliminasi Gauss-Jordan");
        System.out.println("3. Kaidah Cramer");
        System.out.println("Metode apa yang Anda inginkan?");
        int choice = input.nextInt();
        switch(choice){
            case 1:
                System.out.println("Menggunakan metode eliminasi Gauss (EF):");
                m.detGauss();
                break;
            case 2:
                System.out.println("Menggunakan metode eliminasi Gauss-Jordan (REF):");
                m.detGaussJordan();
                break;
            case 3:
                System.out.println("Menggunakan Metode Cramer:");
                m.detCram();
                break;
            default:
                System.out.println("Input tidak valid. Silakan ulangi!");
                System.out.println("Metode apa yang Anda inginkan?");
                choice = input.nextInt();
        }
    }

    private static void inv(){
        System.out.println("Menu Metode Matriks Balikan");
        System.out.println("1. Metode eliminasi Gauss-Jordan (REF)");
        System.out.println("2. Kaidah Cramer");
        System.out.println("Metode apa yang Anda inginkan?");
        int choice = input.nextInt();
        switch(choice){
            case 1:
                System.out.println("Menggunakan metode eliminasi Gauss-Jordan (REF):");
                m.invGaussJordan();
                break;
            case 2:
                System.out.println("Menggunakan Metode Crammer");
                m.invCram();
                break;
            default:
                System.out.println("Input tidak valid. Silakan ulangi!");
                System.out.println("Metode apa yang Anda inginkan?");
                choice = input.nextInt();
        }
    }

    public static void main(String[] args){
        menu();
    }
}