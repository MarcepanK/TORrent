import common.Connection;
import request.Request;

import java.util.List;

public class RequestCollectorThread extends Thread {

    private Connection connection;
    private List<Request> requestBuffer;

    public RequestCollectorThread(Connection connection, List<Request> requestBuffer) {
        this.connection = connection;
        this.requestBuffer = requestBuffer;
    }

    @Override
    public void run() {
        boolean running = true;
        while(running) {
            if (connection.getSocket().isConnected()) {
                Object recv = connection.receive();
                if (recv != null) {
                    if (recv instanceof Request) {
                        requestBuffer.add((Request) recv);
                    }
                }
            } else {
                running = false;
            }
        }
    }
}
