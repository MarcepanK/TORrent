import common.Connection;

import java.util.Scanner;

public class ClientMain {
    public static void main(String[] args) {
        Scanner userInput = new Scanner(System.in);
        System.out.print("Enter host id: ");
        int id = userInput.nextInt();
        try {
            Client client = new Client(id);
            //Shutdown hook not working as intended 
            //Runtime.getRuntime().addShutdownHook(new ShutdownHook(id, client.getTrackerConnection()));
            client.launch();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Not working as intended
    //DO NOT USE
    private static class ShutdownHook extends Thread {

        int clientId;
        Connection trackerConnection;

        private ShutdownHook(int clientId, Connection trackerConnection) {
                this.clientId = clientId;
                this.trackerConnection = trackerConnection;
        }

        public void run() {
            if (trackerConnection.getSocket().isConnected()) {
                trackerConnection.send(RequestFactory.getRequest(clientId, "disconnect"));
            } else {
                System.out.println("connection to tracker is closed");
            }
        }
    }
}
