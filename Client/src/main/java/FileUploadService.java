import common.Connection;
import common.FileMetadata;

import java.io.IOException;
import java.net.Socket;

public class FileUploadService extends FileTransferService {

    private int myId;
    private Connection leechConnection;
    private Connection trackerConnection;
    private Piece[] piecesToSend;

    public FileUploadService(FileMetadata transferredFileMetadata,
                             int myId, int leechId, Connection trackerConnection, Piece[] piecesToSend) {
        super(transferredFileMetadata);
        this.myId = myId;
        this.trackerConnection = trackerConnection;
        this.piecesToSend = piecesToSend;
        establishConnectionWithLeech(leechId);
    }

    private void establishConnectionWithLeech(int leechId) {
        try {
            leechConnection = new Connection(new Socket("localhost", 10000 + leechId));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        for (Piece piece : piecesToSend) {
            leechConnection.send(piece);
            trackerConnection.send(RequestFactory.getUpdateRequest(myId, 0L,
                                                                   piece.data.length,  piece.fileMetadata.name));
        }
    }
}
