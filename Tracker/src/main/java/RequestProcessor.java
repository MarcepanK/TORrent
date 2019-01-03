import request.*;

import java.util.logging.Logger;

public class RequestProcessor {

    private static final Logger logger = Logger.getLogger(RequestProcessor.class.getName());

    private ConnectionContainer connectionContainer;
    private TorrentContainer torrentContainer;

    public RequestProcessor(ConnectionContainer connectionContainer, TorrentContainer torrentContainer) {
        this.connectionContainer = connectionContainer;
        this.torrentContainer = torrentContainer;
    }

    private void processDisconnectRequest(SimpleRequest request) {
        logger.info(String.format("Handling %s simpleRequest from %d",
                request.requestCode.toString(), request.requesterId));

        torrentContainer.onClientDisconnected(request.requesterId);
        connectionContainer.onClientDisconnected(request.requesterId);
    }

    private void processUpdateRequest(UpdateRequest request) {
        logger.info(String.format("Handling %s request | from: %d | downloaded: %d uploaded %d",
                request.requestCode.toString(), request.requesterId, request.downloaded, request.uploaded));

        torrentContainer.getTrackedTorrentByFileName(request.fileName).ifPresent(
                trackedTorrent -> trackedTorrent.getPeerById(request.requesterId).ifPresent(
                        trackedPeer -> trackedPeer.update(request.downloaded, request.uploaded)));
    }

    private void processFileListRequest(SimpleRequest rquest) {
        logger.info(String.format("Handling %s simpleRequest | from %d",
                rquest.requestCode.toString(), rquest.requesterId));

        connectionContainer.getConnectionById(rquest.requesterId).ifPresent(
                connection -> connection.send(torrentContainer.getAllTrackedTorrentsFileMetadata()));
    }

    private void processPullRequest(PullRequest request) {
        logger.info(String.format("Handling %s request | from %d | file: %s already has: %d",
                request.requestCode.toString(), request.requesterId, request.fileName, request.downloaded));
    }

    private void processPushRequest(PushRequest request) {
        logger.info(String.format("Handling %s request | from %d to %d | file: %s",
                request.requestCode.toString(), request.requesterId, request.destinationHostId, request.fileName));
    }

    private void handleUnknownRequest(SimpleRequest request) {
        logger.warning(String.format("Unknown simpleRequest from: %d", request.requesterId));

        connectionContainer.getConnectionById(request.requesterId).ifPresent(
                connection -> connection.send("Unknown simpleRequest code"));
    }

    public void processRequest(SimpleRequest request) {
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
