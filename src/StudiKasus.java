import java.util.Scanner;

public class StudiKasus {
	static Scanner input = new Scanner(System.in);
    static Matrix m = new Matrix(0, 0);

   
    private static void menu(){
    	System.out.println("$ Pilihan Menu Studi Kasus $");
        System.out.println("0. Lihat matriks tersimpan");
        System.out.println("1. Kasus Ax = b");
        System.out.println("2. Kasus Matriks Augmented");
        System.out.println("3. Kasus SPL");
        System.out.println("4. Determinan, Kofaktor, danInvers Matriks Bujur Sangkar");
        System.out.println("5. Mencari Arus dan Tegangan Rangkaian Listrik");
        System.out.println("6. Prediksi Jumlah Penduduk Jawa Barat");
        System.out.println("7. Prediksi fungsi f(x) transenden");
        System.out.println("8. Keluar");
        System.out.println("Apa yang ingin Anda lakukan?");

        int choice = input.nextInt();
        switch(choice){
            case 0: 
                m.showMatrix();
                break;
            case 1:
            	
                break;
            case 2:
            	// fungsi_di_sini
                break;
            case 3:
            	// fungsi_di_sini
                break;
            case 4:
            	// fungsi_di_sini
                break;
            case 5:
            	// fungsi_di_sini
                break;
            case 6:
            	// fungsi_di_sini
                break;
            case 7:
            	// fungsi_di_sini
                break;
            case 8:
            	break;
            default:
                System.out.println("Input tidak valid. Silakan ulangi!");
                System.out.println("Apa yang ingin Anda lakukan?");
                choice = input.nextInt();
        }
        if(choice == 8) System.out.println("Terima kasih!");
        else menu();
    }

    public static void main(String[] args){
        menu();
    }

 
}