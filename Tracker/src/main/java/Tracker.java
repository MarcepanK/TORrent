import java.util.logging.Logger;

public class Tracker {

    private static final Logger logger = Logger.getLogger(Tracker.class.getName());
    public static final int TRACKER_PORT = 10000;

    private TorrentContainer torrentContainer;
    private ConnectionContainer connectionContainer;
    private IncomingConnectionHandler incomingConnectionHandler;

    public Tracker() {
        torrentContainer = new TorrentContainer();
        connectionContainer = new ConnectionContainer();
        incomingConnectionHandler = new IncomingConnectionHandler(connectionContainer, torrentContainer);
    }
}
