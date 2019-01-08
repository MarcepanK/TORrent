import common.Connection;
import request.Request;

import java.util.*;

/**
 * This class is responsible for collecting incoming requests
 * from all connected Clients and passing them to request processor
 */
public class IncomingRequestsHandler implements Runnable {

    private RequestProcessor requestProcessor;
    private List<Request> requestBuffer;
    private Set<Thread> requestCollectorThreads;

    public IncomingRequestsHandler(RequestProcessor requestProcessor) {
        this.requestProcessor = requestProcessor;
        requestBuffer = Collections.synchronizedList(new LinkedList<>());
        requestCollectorThreads = new HashSet<>();
    }

    public void addNewRequestCollectorThread(Connection connection) {
        Thread thread = new RequestCollectorThread(connection, requestBuffer);
        thread.start();
        requestCollectorThreads.add(thread);
    }

    @Override
    public void run() {
        while(true) {
            if (!requestBuffer.isEmpty()) {
                Request request = requestBuffer.get(0);
                requestBuffer.remove(0);
                requestProcessor.processRequest(request);
            } else {
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
