import common.Connection;
import common.FileMetadata;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Logger;

public class FileUploadService extends FileTransferService {

    private static final Logger logger = Logger.getLogger(FileUploadService.class.getName());

    private int myId;
    private Connection leechConnection;
    private Piece[] piecesToSend;
    private boolean complete = false;

    public FileUploadService(FileMetadata transferredFileMetadata, Connection trackerConnection,
                             int myId, int leechId, Piece[] piecesToSend) {
        super(transferredFileMetadata, trackerConnection);
        this.myId = myId;
        this.piecesToSend = piecesToSend;
        logger.info(String.format("Started upload service. Sending file: %s to: %d", transferredFileMetadata.name, leechId));
        establishConnectionWithLeech(leechId);
    }

    private void establishConnectionWithLeech(int leechId) {
        try {
            Thread.sleep(2000);
            leechConnection = new Connection(new Socket("localhost", 10000 + leechId));
            logger.info(String.format("connection to leech with id: %d established", leechId));
            Thread.sleep(5000);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * sends Request with Disconnect code
     * to leech and closes connection to it
     */
    @Override
    protected void finalize() {
        logger.info("finalizing upload service");
        leechConnection.send(RequestFactory.getRequest());
        leechConnection.close();
        complete = true;
    }

    @Override
    public void run() {
        logger.info("Waiting for permission to send files");
        Object received = leechConnection.receive();
        if (received.equals("start")) {
            for (Piece piece : piecesToSend) {
                logger.info("Sending " + piece);
                leechConnection.send(piece);
                trackerConnection.send(RequestFactory.getRequest(myId, 0L,
                        piece.data.length, piece.fileMetadata.name));
            }
            finalize();
        }
    }

    public boolean isComplete() {
        return complete;
    }
}
