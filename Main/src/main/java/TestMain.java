public class TestMain {
    public static void main(String[] args) {
        System.out.print("processing ");
         for (int i=0; i<100; i++) {
             if (i<10) {
                 System.out.print(i+"%");
                 System.out.print("\b\b");
             } else if (i>10) {
                 System.out.print(i+"%");
                 System.out.print("\b\b\b");
             }
             try{
                 Thread.sleep(500);
             } catch (Exception e) {
                 e.printStackTrace();
             }
         }
    }
}
