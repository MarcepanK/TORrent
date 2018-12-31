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
            inputStream = new ObjectInputStream(socket.getInputStream());
            outputStream = new ObjectOutputStream(socket.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            inputStream.close();
            outputStream.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void send(Object data) {
        try {
            outputStream.writeObject(data);
            outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Object receive() {
        try {
            return inputStream.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
