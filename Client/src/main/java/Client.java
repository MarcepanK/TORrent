import common.ClientHandshake;
import common.Connection;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;

public class Client {

    private static final Logger logger = Logger.getLogger(Client.class.getName());
    private static final String DEFAULT_PATH = "D:/TORrent_";

    private int id;
    private String directoryPath;
    private Connection trackerConnection;
    private TrackerListener trackerListener;
    private CommandProcessor commandProcessor;
    private ClientConsole console;


    public Client(int id) {
        this.id = id;
        directoryPath = DEFAULT_PATH + id;
        initTrackerConnection();
        trackerListener = new TrackerListener(trackerConnection);
        commandProcessor = new CommandProcessor(id, trackerConnection);
        console = new ClientConsole(commandProcessor);
    }

    private void initTrackerConnection() {
        try {
            logger.info("Initializing connection to tracker");
            Socket sock = new Socket("localhost", Tracker.TRACKER_PORT);
            trackerConnection = new Connection(sock);
            logger.info("Connecting to tracker");
            ClientHandshake handshake = generateHandshake();
            logger.info("Sending handshake");
            if (handshake != null) {
                trackerConnection.send(generateHandshake());
            }
        } catch (IOException e) {
            logger.severe("Unable to connect to tracker.\nQuitting");
            System.exit(1);
        }
    }

    private ClientHandshake generateHandshake() {
        logger.info("Generating handshake");
        File directory = new File(directoryPath);
        if (directory.exists()) {
            return new ClientHandshake(id, directory.listFiles());
        } else {
            try {
                Files.createDirectory(Paths.get(directoryPath));
                return new ClientHandshake(id, directory.listFiles());
            } catch (Exception e) {
                logger.severe("Unable to create directory");
                System.exit(1);
            }
        }
        return null;
    }

    public void launch() {
        Thread t1 = new Thread(console);
        Thread t2 = new Thread(trackerListener);
        t1.start();
        t2.start();
    }
}
