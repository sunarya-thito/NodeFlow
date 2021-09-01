import java.util.*;

public class Idk {
    public static void main(String[] args) {
        int[] nilai = new int[20];
        for (int i = 0; i < nilai.length; i++) nilai[i] = new Random().nextInt(200);
        nilai[0] = 100;
        nilai[12] = 100;
        nilai[19] = 100;
        System.out.println(Arrays.toString(nilai));
        int counter = 0;
        int index = 0;
        while (index < nilai.length) if (nilai[index++] == 100 && counter++ != -1) System.out.println("Angka 100 ada di index " + (index - 1));
        System.out.println("Total ada "+counter+" angka 100");
    }
}
