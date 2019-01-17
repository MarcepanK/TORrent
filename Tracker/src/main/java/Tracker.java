public class Tracker {

    public static final int TRACKER_PORT = 10000;

    private TorrentContainer torrentContainer;
    private ConnectionContainer connectionContainer;
    private RequestProcessor requestProcessor;
    private IncomingRequestsHandler incomingRequestsHandler;
    private IncomingConnectionsHandler incomingConnectionsHandler;

    public Tracker() {
        torrentContainer = new TorrentContainer();
        connectionContainer = new ConnectionContainer();
        requestProcessor = new RequestProcessor(connectionContainer, torrentContainer);
        incomingRequestsHandler = new IncomingRequestsHandler(requestProcessor);
        incomingConnectionsHandler = new IncomingConnectionsHandler(connectionContainer, torrentContainer,
                incomingRequestsHandler);
    }

    public void launch() {
        Thread t1 = new Thread(incomingConnectionsHandler);
        Thread t2 = new Thread(incomingRequestsHandler);
        t1.start();
        t2.start();
    }
}
