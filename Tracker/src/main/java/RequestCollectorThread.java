import common.Connection;
import request.Request;
import request.RequestCode;

import java.util.List;

/**
 * This class is responsible for receiving requests
 * from single client and storing it in request buffer
 */
public class RequestCollectorThread extends Thread {

    private Connection connection;
    private List<Request> requestBuffer;
    private boolean running;

    /**
     * Constructor
     *
     * @param connection    connection to client that this thread will listen to
     * @param requestBuffer Collection into which requests received from client will be inserted
     */
    public RequestCollectorThread(Connection connection, List<Request> requestBuffer) {
        this.connection = connection;
        this.requestBuffer = requestBuffer;
        running = true;
    }

    @Override
    public void run() {
        while (running) {
            if (connection.getSocket().isConnected()) {
                Object recv = connection.receive();
                if (recv instanceof Request) {
                    Request request = (Request) recv;
                    requestBuffer.add(request);
                    if (request.requestCode == RequestCode.DISCONNECT) {
                        running = false;
                    }
                }
            } else {
                running = false;
            }
        }
    }

    public boolean isRunning() {
        return running;
    }
}
