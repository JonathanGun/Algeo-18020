import java.util.Scanner;

public class Main {
    private static Scanner input = new Scanner(System.in);

    public static void main(String[] args){
        System.out.printf("Masukkan jumlah baris: ");
        int r = input.nextInt();
        System.out.printf("Masukkan jumlah kolom: ");
        int c = input.nextInt();
        Matrix m = new Matrix(r, c);
        m.input();
        m.print();
    }
}