import common.Connection;
import request.Request;

import java.util.Collection;

public class IncomingRequestsHandler implements Runnable {

    private Collection<Connection> connections;
    private RequestExecutor requestExecutor;

    public IncomingRequestsHandler(Collection<Connection> connections, RequestExecutor requestExecutor) {
        this.connections = connections;
        this.requestExecutor = requestExecutor;
    }

    @Override
    public void run() {
        for (Connection conn : connections) {
            Object recv = conn.receive();
            if (recv instanceof Request) {
                requestExecutor.handleRequest((Request)recv);
            }
        }
    }
}
