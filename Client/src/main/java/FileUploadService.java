import common.Connection;
import common.FileMetadata;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Logger;

public class FileUploadService extends FileTransferService {

    private static final Logger logger = Logger.getLogger(FileUploadService.class.getName());

    private int myId;
    private Connection leechConnection;
    private Connection trackerConnection;
    private Piece[] piecesToSend;
    private boolean complete = false;

    public FileUploadService(FileMetadata transferredFileMetadata,
                             int myId, int leechId, Connection trackerConnection, Piece[] piecesToSend) {
        super(transferredFileMetadata);
        this.myId = myId;
        this.trackerConnection = trackerConnection;
        this.piecesToSend = piecesToSend;
        logger.info("Starting uploadService");
        establishConnectionWithLeech(leechId);
        logger.info("Pieces to send: " + piecesToSend.length);
    }

    private void establishConnectionWithLeech(int leechId) {
        try {
            Thread.sleep(3000);
            leechConnection = new Connection(new Socket("localhost", 10000 + leechId));
            logger.info("connection established");
            Thread.sleep(5000);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        logger.info("Sending");
        for (Piece piece : piecesToSend) {
            logger.info("Sending " + piece);
            leechConnection.send(piece);
            trackerConnection.send(RequestFactory.getUpdateRequest(myId, 0L,
                                                                   piece.data.length,  piece.fileMetadata.name));
        }
        logger.info("finalizing");
        leechConnection.send(RequestFactory.getDisconnectRequest());
        complete = true;
    }

    public boolean isComplete() {
        return complete;
    }
}
