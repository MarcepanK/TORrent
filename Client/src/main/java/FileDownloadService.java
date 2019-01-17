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
    private List<Piece> pieceBuffer;
    private Set<PieceCollectorThread> pieceCollectorThreadSet;
    private ServerSocket serverSocket;
    private boolean complete = false;

    public FileDownloadService(Connection trackerConnection, int myId, FileMetadata orderedFileMetadata) {
        super(orderedFileMetadata, trackerConnection);
        this.myId = myId;
        this.pieceBuffer = Collections.synchronizedList(new LinkedList<>());
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

    /**
     * Creates {@link Connection} to other client
     * and collects pieces to PieceBuffer
     *
     * @param socket returned by ServerSocket method accept()
     */
    private void addNewPieceCollectorThread(Socket socket) {
        Connection connection = new Connection(socket);
        PieceCollectorThread thread = new PieceCollectorThread(pieceBuffer, connection);
        pieceCollectorThreadSet.add(thread);
        thread.start();
        logger.info("new download thread started");
    }


    /**
     * Removes inactive threads responsible for collecting pieces
     * and finalizes service if no threads are active
     */
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

    /**
     * sorts elements of PieceBuffer containing {@link Piece}
     * and assembles them into file
     */
    public void finalizeDownloadService() {
        if (!complete) {
            logger.info("Finalizing service");
            pieceBuffer.sort(Comparator.comparingInt(o -> o.index));
            try {
                FileUtils.assembleFileFromPieces(pieceBuffer.toArray(new Piece[0]), Client.DEFAULT_PATH_PREFIX + myId + "/" + pieceBuffer.get(0).fileMetadata.name);
            } catch (Exception e) {
                e.printStackTrace();
            }
            complete = true;
        }
    }

    public void closeServerSock() {
        try {
            serverSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (!serverSocket.isClosed()) {
            try {
                addNewPieceCollectorThread(serverSocket.accept());
            } catch (IOException e) {
                logger.warning("Server socket closed");
            }
        }
    }

    public boolean isComplete() {
        return complete;
    }
}
