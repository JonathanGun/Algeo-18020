import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

public class Main {
    private static int getMenu(){
        // tampilkan menu 1..7
        // return inputan user
        return 1; // sementara
    }

    private static int getSPLMethod(){
        // tampilkan menu metode 1..4
        // return inputan user
        return 1; // sementara
    }
    public static void main(String[] args){
        Matrix m = new Matrix();
        m.inputMatrix();
        m.print();

        int choice = getMenu();
        while (choice != 7){
            // Sistem Persamaan Linier
            if (choice == 1){
                int splchoice = getSPLMethod();
                if (splchoice == 1){
                    System.out.printf("Menggunakan metode eliminasi gauss: \n");
                    m.gaussElim();
                } else if (splchoice == 2){
                    System.out.printf("Menggunakan metode eliminasi gauss-jordan: \n");
                    m.gaussJordanElim();
                } else if (splchoice == 3){

                } else if (splchoice == 4){

                }
                m.print();

            } else if (choice == 2){

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