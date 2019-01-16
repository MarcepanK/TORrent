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

    private int myId;
    private List<Piece> filePieces;
    private Set<PieceCollectorThread> pieceCollectorThreadSet;
    private ServerSocket serverSocket;
    private boolean complete = false;

    public FileDownloadService(Connection trackerConnection, int myId, FileMetadata orderedFileMetadata) {
        super(orderedFileMetadata, trackerConnection);
        this.myId = myId;
        this.filePieces = Collections.synchronizedList(new LinkedList<>());
        this.pieceCollectorThreadSet = Collections.synchronizedSet(new HashSet<>());
        createServerSock();
        scheduledExecutorService.scheduleAtFixedRate(this::cleanupInactiveThreads, 5,3, TimeUnit.SECONDS);
        logger.info("Starting download service");
    }

    private void createServerSock() {
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
        thread.start();
        logger.info("new download thread started");
    }

    private void cleanupInactiveThreads() {
        pieceCollectorThreadSet.removeIf(thread -> !thread.isRunning());
        if (pieceCollectorThreadSet.isEmpty()) {
            try {
                finalizeDownloadService();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void finalizeDownloadService() throws Exception {
        if (!complete) {
            logger.info("Finalizing service");
            filePieces.sort(Comparator.comparingInt(o -> o.index));
            FileUtils.assembleFileFromPieces(filePieces.toArray(new Piece[0]), Client.DEFAULT_PATH_PREFIX + myId + "/" + filePieces.get(0).fileMetadata.name);
            complete = true;
        }
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

    public boolean isComplete() {
        return complete;
    }
}
