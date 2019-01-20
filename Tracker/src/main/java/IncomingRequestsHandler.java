import common.Connection;
import request.Request;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * This class is responsible for collecting incoming requests
 * from all connected Clients and passing them to request processor
 */
public class IncomingRequestsHandler implements Runnable {

    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

    private RequestProcessor requestProcessor;
    private List<Request> requestBuffer;
    private Set<RequestCollectorThread> requestCollectorThreads;

    public IncomingRequestsHandler(RequestProcessor requestProcessor) {
        this.requestProcessor = requestProcessor;
        requestBuffer = Collections.synchronizedList(new LinkedList<>());
        requestCollectorThreads = Collections.synchronizedSet(new HashSet<>());
        scheduledExecutorService.scheduleAtFixedRate(this::cleanupInactiveThreads, 0, 3, TimeUnit.SECONDS);
    }

    /**
     * Invoked when client connects to tracker
     * Creates new thread responsible for receiving requests from client
     *
     * @param connection connection to newly connected client
     */
    public void addNewRequestCollectorThread(Connection connection) {
        RequestCollectorThread thread = new RequestCollectorThread(connection, requestBuffer);
        thread.start();
        requestCollectorThreads.add(thread);
    }

    /**
     * Invoked every 3 seconds
     * <p>
     * Searches for inactive threads responsible for receiving
     * requests from clients and removes them
     */
    private void cleanupInactiveThreads() {
        requestCollectorThreads.removeIf(thread -> !thread.isRunning());
    }

    @Override
    public void run() {
        while (true) {
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
