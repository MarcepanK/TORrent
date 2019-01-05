import common.ClientMetadata;
import order.DownloadOrder;
import order.UploadOrder;
import request.*;

import java.util.ArrayList;
import java.util.logging.Logger;

public class RequestProcessor {

    private static final Logger logger = Logger.getLogger(RequestProcessor.class.getName());

    private ConnectionContainer connectionContainer;
    private TorrentContainer torrentContainer;
    private OrderFactory orderFactory;

    public RequestProcessor(ConnectionContainer connectionContainer, TorrentContainer torrentContainer) {
        this.connectionContainer = connectionContainer;
        this.torrentContainer = torrentContainer;
        orderFactory = new OrderFactory(torrentContainer);
    }

    /**
     * Removes client from every tracked torrent and connection container
     * @param requesterMetadata {@link ClientMetadata} of client that sent request
     * @param request {@link Request} that has been received
     */
    private void processDisconnectRequest(ClientMetadata requesterMetadata, Request request) {
        logger.info(String.format("Handling %s simpleRequest from %d",
                request.requestCode.toString(), request.requesterId));
        torrentContainer.onClientDisconnected(request.requesterId);
        connectionContainer.onClientDisconnected(request.requesterId);
    }

    /**
     * Updates state of {@link TrackedPeer} downloaded, uploaded and left fields
     * @param requesterMetadata {@link ClientMetadata} of client that sent request
     * @param request {@link UpdateRequest} that has been received
     */
    private void processUpdateRequest(ClientMetadata requesterMetadata, UpdateRequest request) {
        logger.info(String.format("Handling %s request | from: %d | downloaded: %d uploaded %d",
                request.requestCode.toString(), request.requesterId, request.downloaded, request.uploaded));
        torrentContainer.getTrackedTorrentByFileName(request.fileName).ifPresent(
                trackedTorrent -> trackedTorrent.getPeerById(request.requesterId).ifPresent(
                        trackedPeer -> trackedPeer.update(request.downloaded, request.uploaded)));
    }

    /**
     * Sends array of {@link common.FileMetadata} of all tracked torrents to requester
     * @param requesterMetadata {@link ClientMetadata} of client that sent request
     * @param request {@link Request} that has been received
     */
    private void processFileListRequest(ClientMetadata requesterMetadata, Request request) {
        logger.info(String.format("Handling %s simpleRequest | from %d",
                request.requestCode.toString(), request.requesterId));
        connectionContainer.getConnectionById(request.requesterId).ifPresent(
                connection -> connection.send(torrentContainer.getAllTrackedTorrentsFileMetadata()));
    }

    /**
     * Sends {@link UploadOrder} to clients that own requested file and {@link DownloadOrder}
     * to client that wants to download a file
     * @param requesterMetadata {@link ClientMetadata} of client that sent request
     * @param request {@link PullRequest} that has been received
     */
    private void processPullRequest(ClientMetadata requesterMetadata, PullRequest request) {
        logger.info(String.format("Handling %s request | from %d | file: %s already has: %d",
                request.requestCode.toString(), request.requesterId, request.fileName, request.downloaded));
        ArrayList<UploadOrder> uploadOrders = orderFactory.getUploadOrders(request);
        DownloadOrder downloadOrder = orderFactory.getDownloadOrder(request);
        connectionContainer.getConnectionById(uploadOrders.get(0).leech.id).ifPresent(conn->conn.send(downloadOrder));
        for (int i=0; i < downloadOrder.seeds.length; i++) {
            final int finalI = i;
            connectionContainer.getConnectionById(downloadOrder.seeds[finalI].id).ifPresent(conn -> conn.send(uploadOrders.get(finalI)));
        }
    }

    /**
     * Sends {@link UploadOrder} to client that sent {@link PushRequest} and {@link DownloadOrder}
     * to client that is goind to receive the file
     * @param requesterMetadata {@link ClientMetadata} of client that sent request
     * @param request {@link PushRequest} that has been received
     */
    private void processPushRequest(ClientMetadata requesterMetadata, PushRequest request) {
        logger.info(String.format("Handling %s request | from %d to %d | file: %s",
                request.requestCode.toString(), request.requesterId, request.destinationHostId, request.fileName));

    }

    /**
     * Sends message to requester about bad request args
     * @param requesterMetadata {@link ClientMetadata} of client that sent request
     * @param request {@link Request} that has been received
     */
    private void handleUnknownRequest(ClientMetadata requesterMetadata, Request request) {
        logger.warning(String.format("Unknown simpleRequest from: %d", request.requesterId));
        connectionContainer.getConnectionById(request.requesterId).ifPresent(
                connection -> connection.send("Bad Request"));
    }

    /**
     * Checks type of received {@link Request} and invokes valid function to process it.
     * @param requesterMetadata {@link ClientMetadata} of Client that sent request
     * @param request {@link Request} that has been received
     */
    public void processRequest(ClientMetadata requesterMetadata, Request request) {
        new Thread(() -> {
            switch (request.requestCode) {
                case DISCONNECT:
                    processDisconnectRequest(requesterMetadata, request);
                    break;
                case UPDATE:
                    processUpdateRequest(requesterMetadata, (UpdateRequest) request);
                    break;
                case FILE_LIST:
                    processFileListRequest(requesterMetadata, request);
                    break;
                case PUSH:
                    processPushRequest(requesterMetadata, (PushRequest) request);
                    break;
                case PULL:
                    processPullRequest(requesterMetadata, (PullRequest) request);
                    break;
                default:
                    handleUnknownRequest(requesterMetadata, request);
                    break;
            }
        }).start();
    }
}
