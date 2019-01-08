package common;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Connection {

    private Socket socket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;

    public Connection(Socket socket) {
        this.socket = socket;
        setupStreams();
    }

    private void setupStreams() {
        try {
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            inputStream.close();
            outputStream.close();
            if (socket.isConnected()) {
                socket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void send(Object data) {
        try {
            System.out.println("sending: " + data.toString());
            outputStream.writeObject(data);
            outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Object receive() {
        try {
            Object recv = inputStream.readObject();
            System.out.println("Received: " + recv.toString());
            return recv;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Socket getSocket() {
        return socket;
    }
}
