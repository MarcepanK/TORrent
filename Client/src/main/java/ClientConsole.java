import java.util.Scanner;

public class ClientConsole implements Runnable {

    private CommandProcessor commandProcessor;

    public ClientConsole(CommandProcessor commandProcessor) {
        this.commandProcessor = commandProcessor;
    }

    @Override
    public void run() {
        while (true) {
            Scanner userInput = new Scanner(System.in);
            commandProcessor.processCommand(userInput.nextLine());
        }
    }
}
