import request.SimpleRequest;

import java.util.Collection;

/**
 * This class is responsible for collecting incoming requests
 * from all connected Clients and passing them to request processor
 */
public class IncomingRequestsHandler implements Runnable {

    private ConnectionContainer connectionContainer;
    private RequestProcessor requestProcessor;

    public IncomingRequestsHandler(ConnectionContainer connectionContainer, RequestProcessor requestProcessor) {
        this.connectionContainer = connectionContainer;
        this.requestProcessor = requestProcessor;
    }

    @Override
    public void run() {
        Collection<Connection> connections = connectionContainer.getConnections();
        for (Connection conn : connections) {
            Object recv = conn.receive();
            if (recv instanceof SimpleRequest) {
                requestProcessor.processRequest((SimpleRequest)recv);
            }
        }
    }
}
