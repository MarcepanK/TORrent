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
        scheduledExecutorService.scheduleAtFixedRate(this::cleanupInactiveThreads, 5, 3, TimeUnit.SECONDS);
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
                finalize();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * sorts elements of PieceBuffer containing {@link Piece}
     * If there are no pieces missing assembles the files
     * If there are some pieces missing, passes this service to {@link IncompleteServiceHandler}
     * to handle it
     */
    @Override
    protected void finalize() {
        if (!complete) {
            logger.info("Finalizing service");
            pieceBuffer.sort(Comparator.comparingInt(o -> o.index));
            if (hasWholeFile()) {
                logger.info("assembling file");
                FileUtils.assembleFileFromPieces(pieceBuffer, Client.DEFAULT_PATH_PREFIX
                        + myId + "/" + pieceBuffer.get(0).fileMetadata.name);
            } else {
                try {
                    Collection<Piece> pieces = FileUtils.getPiecesFromBrokenFile(myId, transferredFileMetadata.name);
                    if (!pieces.isEmpty()) {
                        pieceBuffer.addAll(pieces);
                        FileUtils.assembleFileFromPieces(pieceBuffer, Client.DEFAULT_PATH_PREFIX +
                                myId + "/" + pieceBuffer.get(0).fileMetadata.name);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                logger.warning("Service not complete.");
                IncompleteServiceHandler.handleIncompleteDownloadService(this);
            }
            complete = true;
        }
    }


    /**
     * Returns True if downloaded bytes size is equal to requested file sile
     *
     * @return true if file is complete
     */
    private boolean hasWholeFile() {
        int allPiecesLen = 0;
        for (Piece piece : pieceBuffer) {
            allPiecesLen += piece.data.length;
        }
        if (allPiecesLen == transferredFileMetadata.size) {
            logger.info("whole file has beend downloaded");
            return true;
        } else {
            logger.warning("something went wrong when downloading file");
            return false;
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

    public FileMetadata getTransferredFileMetadata() {
        return transferredFileMetadata;
    }

    public int getMyId() {
        return myId;
    }

    public List<Piece> getPieceBuffer() {
        return pieceBuffer;
    }
}
