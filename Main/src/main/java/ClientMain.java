import java.util.Scanner;

public class ClientMain {
    public static void main(String[] args) {
        Scanner userInput = new Scanner(System.in);
        System.out.print("Enter host id: ");
        int id = userInput.nextInt();
        Client client = new Client(id);
        client.launch();
    }
}
