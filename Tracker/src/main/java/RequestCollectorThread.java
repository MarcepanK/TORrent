import common.Connection;
import request.Request;
import request.RequestCode;

import java.util.List;

public class RequestCollectorThread extends Thread {

    private Connection connection;
    private List<Request> requestBuffer;
    private boolean running;

    public RequestCollectorThread(Connection connection, List<Request> requestBuffer) {
        this.connection = connection;
        this.requestBuffer = requestBuffer;
        running = true;
    }

    @Override
    public void run() {
        while(running) {
            if (connection.getSocket().isConnected()) {
                Object recv = connection.receive();
                if (recv != null) {
                    if (recv instanceof Request) {
                        Request request = (Request) recv;
                        requestBuffer.add(request);
                        if (request.requestCode == RequestCode.DISCONNECT) {
                            running = false;
                        }
                    }
                }
            }
        }
    }

    public boolean isRunning() {
        return running;
    }
}
