import java.io.IOException;
import java.net.Socket;
import java.util.logging.Logger;

public class Client {

    private static final Logger logger = Logger.getLogger(Client.class.getName());
    private static String dirPath = "D:\\TORrent_";

    private int id;
    private Connection trackerConnection;
    private FileTransferServiceContainer fileTransferServiceContainer;
    private CommandProcessor commandProcessor;
    private ResponseProcessor responseProcessor;
    private ResponseHandler responseHandler;
    private ClientConsole console;


    public Client(int id) {
        this.id = id;
        initTrackerConnection();
        fileTransferServiceContainer = new FileTransferServiceContainer();
        commandProcessor = new CommandProcessor(trackerConnection, fileTransferServiceContainer);
        responseProcessor = new ResponseProcessor(fileTransferServiceContainer);
        responseHandler = new ResponseHandler(trackerConnection, responseProcessor);
        console = new ClientConsole(commandProcessor);
    }

    private void initTrackerConnection() {
        try {
            Socket sock = new Socket("localhost", Tracker.TRACKER_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
