import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class Tracker {

    private static final Logger logger = Logger.getLogger(Tracker.class.getName());

    public static final int TRACKER_PORT = 10000;

    private TorrentContainer torrentContainer;
    private ConnectionContainer connectionContainer;
    private IncomingConnectionsHandler incomingConnectionsHandler;
    private RequestExecutor requestExecutor;
    private IncomingRequestsHandler incomingRequestsHandler;
    private ExecutorService executorService;

    public Tracker() {
        torrentContainer = new TorrentContainer();
        connectionContainer = new ConnectionContainer();
        incomingConnectionsHandler = new IncomingConnectionsHandler(connectionContainer, torrentContainer);
        requestExecutor = new RequestExecutor(connectionContainer, torrentContainer);
        incomingRequestsHandler = new IncomingRequestsHandler(connectionContainer.getConnections(), requestExecutor);
        executorService = Executors.newFixedThreadPool(2);
    }

    public void launch() {
        Thread t1 = new Thread(incomingConnectionsHandler);
        Thread t2 = new Thread(incomingRequestsHandler);
        while(true) {
            t1.run();
            logger.info("t1 running");
            t2.run();
            logger.info("t2 running");
        }
    }
}
