import common.ClientHandshake;
import common.Connection;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Logger;

public class Client {

    private static final Logger logger = Logger.getLogger(Client.class.getName());

    public static final String DEFAULT_PATH_PREFIX = "/home/marcin/Documents/TORrent_";

    private int id;
    private FileRepository fileRepository;
    private TransferServiceContainer transferServiceContainer;
    private Connection trackerConnection;
    private OrderProcessor orderProcessor;
    private TrackerListener trackerListener;
    private CommandProcessor commandProcessor;
    private ClientConsole console;


    public Client(int id) {
        this.id = id;
        fileRepository = new FileRepository(DEFAULT_PATH_PREFIX + id);
        transferServiceContainer = new TransferServiceContainer();
        initTrackerConnection();
        orderProcessor = new OrderProcessor(id, fileRepository, trackerConnection, transferServiceContainer);
        trackerListener = new TrackerListener(trackerConnection, orderProcessor);
        commandProcessor = new CommandProcessor(id, trackerConnection, fileRepository, transferServiceContainer);
        console = new ClientConsole(commandProcessor);
    }

    private void initTrackerConnection() {
        try {
            logger.info("Initializing connection to tracker");
            Socket sock = new Socket("localhost", Tracker.TRACKER_PORT);
            trackerConnection = new Connection(sock);
            ClientHandshake handshake = new ClientHandshake(id, fileRepository.getAllFilesMetadata());
            trackerConnection.send(handshake);
            logger.info("Handshake sent");
        } catch (IOException e) {
            logger.severe("Unable to connect to tracker.\nQuitting");
            System.exit(1);
        }
    }

    public void launch() {
        Thread t1 = new Thread(console);
        Thread t2 = new Thread(trackerListener);
        t1.start();
        t2.start();
    }

    /**
     * This function should be only used in {@link ClientMain}
     * <p>
     * Connection to tracker is required in order to
     * make shutdown hook work properly
     *
     * @return connection to tracker
     */
    public Connection getTrackerConnection() {
        return trackerConnection;
    }
}
