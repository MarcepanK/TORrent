import common.ClientMetadata;
import common.Connection;
import common.FileMetadata;
import order.DownloadOrder;
import order.UploadOrder;
import request.*;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Optional;
import java.util.logging.Logger;

public class RequestProcessor {

    private static final Logger logger = Logger.getLogger(RequestProcessor.class.getName());

    private ConnectionContainer connectionContainer;
    private TorrentContainer torrentContainer;
    private OrderDispatcher orderDispatcher;

    public RequestProcessor(ConnectionContainer connectionContainer, TorrentContainer torrentContainer) {
        this.connectionContainer = connectionContainer;
        this.torrentContainer = torrentContainer;
        orderDispatcher = new OrderDispatcher(connectionContainer);
    }

    /**
     * Removes client from every tracked torrent and connection container
     *
     * @param request {@link Request} that has been received
     */
    private void processDisconnectRequest(Request request) {
        logger.info(String.format("Handling %s simpleRequest from %d",
                request.requestCode.toString(), request.requesterId));
        torrentContainer.onClientDisconnected(request.requesterId);
        connectionContainer.onClientDisconnected(request.requesterId);
    }

    /**
     * Updates state of {@link TrackedPeer} downloaded, uploaded and left fields
     *
     * @param request {@link UpdateRequest} that has been received
     */
    private void processUpdateRequest(UpdateRequest request) {
        logger.info(String.format("Handling %s request | from: %d | downloaded: %d uploaded %d",
                request.requestCode.toString(), request.requesterId, request.downloaded, request.uploaded));
        torrentContainer.getTrackedTorrentByFileName(request.fileName).ifPresent(
                trackedTorrent -> trackedTorrent.getPeerById(request.requesterId).ifPresent(
                        trackedPeer -> trackedPeer.update(request.downloaded, request.uploaded)));
    }

    /**
     * Sends array of {@link common.FileMetadata} of all tracked torrents to requester
     *
     * @param request {@link Request} that has been received
     */
    private void processFileListRequest(Request request) {
        logger.info(String.format("Handling %s request from client with id: %d",
                request.requestCode.toString(), request.requesterId));
        ArrayList<FileMetadata> fileList = torrentContainer.provideFileListForClient(request.requesterId);
        connectionContainer.getConnectionById(request.requesterId).ifPresent(connection -> connection.send(fileList));
    }

    /**
     * Sends {@link UploadOrder} to clients that own requested file and {@link DownloadOrder}
     * to client that wants to download a file
     *
     * @param request {@link PullRequest} that has been received
     */
    private void processPullRequest(PullRequest request) {
        logger.info(String.format("Handling %s request | from %d | file: %s",
                request.requestCode.toString(), request.requesterId, request.fileName));
        orderDispatcher.dispatchOrders(OrderFactory.getDownloadOrder(torrentContainer, request),
                                  OrderFactory.getUploadOrders(torrentContainer, request));
        torrentContainer.getTrackedTorrentByFileName(request.fileName).ifPresent(torrent->{
            Optional<Connection> newPeerConnection = connectionContainer.getConnectionById(request.requesterId);
            if (newPeerConnection.isPresent()) {
                InetSocketAddress sockAddress =(InetSocketAddress) newPeerConnection.get().getSocket().getRemoteSocketAddress();
                ClientMetadata newPeerMetadata = new ClientMetadata(request.requesterId, sockAddress);
                torrent.addPeer(new TrackedPeer(newPeerMetadata, 0L, 0L, 0L));
            }
        });
    }

    /**
     * Sends {@link UploadOrder} to client that sent {@link PushRequest} and {@link DownloadOrder}
     * to client that is goind to receive the file
     *
     * @param request {@link PushRequest} that has been received
     */
    private void processPushRequest(PushRequest request) {
        logger.info(String.format("Handling %s request | from %d to %d | file: %s",
                request.requestCode.toString(), request.requesterId, request.destinationHostId, request.fileName));
        orderDispatcher.dispatchOrders(OrderFactory.getDownloadOrder(torrentContainer, request),
                                  OrderFactory.getUploadOrder(torrentContainer, request));
    }

    /**
     * Sends message to requester about bad request args
     *
     * @param request {@link Request} that has been received
     */
    private void handleUnknownRequest(Request request) {
        logger.warning(String.format("Unknown simpleRequest from: %d", request.requesterId));
        connectionContainer.getConnectionById(request.requesterId).ifPresent(
                connection -> connection.send("Bad Request"));
    }

    /**
     * Checks type of received {@link Request} and invokes valid function to process it.
     *
     * @param request {@link Request} that has been received
     */
    public void processRequest(Request request) {
        new Thread(() -> {
            switch (request.requestCode) {
                case DISCONNECT:
                    processDisconnectRequest(request);
                    break;
                case UPDATE:
                    processUpdateRequest((UpdateRequest) request);
                    break;
                case FILE_LIST:
                    processFileListRequest(request);
                    break;
                case PUSH:
                    processPushRequest((PushRequest) request);
                    break;
                case PULL:
                    processPullRequest((PullRequest) request);
                    break;
                default:
                    handleUnknownRequest(request);
                    break;
            }
        }).start();
    }
}
