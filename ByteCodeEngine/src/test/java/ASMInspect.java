public class ASMInspect {
    public static void main(String[] args) {
        Object[][] array = new Object[0][1];
        System.out.println(array);

        Object[] array2 = new Object[0];
        System.out.println(array2);

        boolean test = Double.valueOf("421") <= Integer.valueOf("129");

        if (Boolean.parseBoolean("true")) {
            System.out.println("test");
        }

        System.out.println(123 & Long.parseLong("12"));
        System.out.println(!Boolean.valueOf("true") && Boolean.valueOf("false"));
    }
}
