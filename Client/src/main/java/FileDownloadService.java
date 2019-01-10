import common.Connection;
import common.FileMetadata;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class FileDownloadService extends FileTransferService {

    private static final Logger logger = Logger.getLogger(FileDownloadService.class.getName());
    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

    private List<Piece> filePieces;
    private Set<PieceCollectorThread> pieceCollectorThreadSet;
    private ServerSocket serverSocket;

    public FileDownloadService(int myId, FileMetadata orderedFileMetadata) {
        super(orderedFileMetadata);
        this.filePieces = Collections.synchronizedList(new LinkedList<>());
        this.pieceCollectorThreadSet = Collections.synchronizedSet(new HashSet<>());
        scheduledExecutorService.scheduleAtFixedRate(this::cleanupInactiveThreads, 0,3, TimeUnit.SECONDS);
        createServerSock(myId);
    }

    private void createServerSock(int myId) {
        try {
            serverSocket = new ServerSocket(10000 + myId, 10);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addNewPieceCollectorThread(Socket socket) {
        Connection connection = new Connection(socket);
        PieceCollectorThread thread = new PieceCollectorThread(filePieces, connection);
        pieceCollectorThreadSet.add(thread);
        thread.run();
    }

    private void cleanupInactiveThreads() {
        pieceCollectorThreadSet.removeIf(thread -> !thread.isRunning());
    }

    @Override
    public void run() {
        while (true) {
            try {
                addNewPieceCollectorThread(serverSocket.accept());
            } catch (IOException e) {
                logger.severe("Big oopsie");
                e.printStackTrace();
            }
        }
    }
}
