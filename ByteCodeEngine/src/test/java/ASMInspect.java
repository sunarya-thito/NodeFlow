public class ASMInspect {
    public static void main(String[] args) {
        Object[][] array = new Object[0][1];
        System.out.println(array);

        Object[] array2 = new Object[0];
        System.out.println(array2);

        boolean test = Double.valueOf("421") <= Integer.valueOf("129");

        if (test) {
            System.out.println("test");
        }
    }
}
