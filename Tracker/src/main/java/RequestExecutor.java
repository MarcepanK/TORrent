import request.*;

import java.util.logging.Logger;

public class RequestExecutor {

    private static final Logger logger = Logger.getLogger(RequestExecutor.class.getName());

    private ConnectionContainer connectionContainer;
    private TorrentContainer torrentContainer;

    public RequestExecutor(ConnectionContainer connectionContainer, TorrentContainer torrentContainer) {
        this.connectionContainer = connectionContainer;
        this.torrentContainer = torrentContainer;
    }

    private void handleDisconnectRequest(Request request) {
        logger.info(String.format("Handling %s request from %d", request.requestCode.toString(), request.requesterId));
        torrentContainer.onClientDisconnected(request.requesterId);
        connectionContainer.onClientDisconnected(request.requesterId);
    }

    private void handleUpdateRequest(UpdateRequest updateRequest) {
        logger.info(String.format("Handling %s request | from: %d | downloaded: %d uploaded %d",
                updateRequest.requestCode.toString(), updateRequest.requesterId, updateRequest.downloaded, updateRequest.uploaded));
        torrentContainer.getTrackedTorrentByFileName(updateRequest.fileName).ifPresent(
                trackedTorrent -> trackedTorrent.getPeerById(updateRequest.requesterId).ifPresent(
                        trackedPeer -> trackedPeer.update(updateRequest.downloaded, updateRequest.uploaded)));
    }

    private void handleFileListRequest(Request request) {
        logger.info(String.format("Handling %s request | from %d", request.requestCode.toString(), request.requesterId));
        connectionContainer.getConnectionById(request.requesterId).ifPresent(
                connection -> connection.send(torrentContainer.getAllTrackedTorrentsFileMetadata()));
    }

    private void handlePullRequest(PullRequest pullRequest) {
        logger.info(String.format("Handling %s request | from %d | file: %s already has: %d",
                pullRequest.requestCode.toString(), pullRequest.requesterId, pullRequest.fileName, pullRequest.downloaded));
    }

    private void handlePushRequest(PushRequest pushRequest) {
        logger.info(String.format("Handling %s request | from %d to %d | file: %s",
                pushRequest.requestCode.toString(), pushRequest.requesterId, pushRequest.destinationHostId, pushRequest.fileName));
    }

    private void handleUnknownRequest(Request request) {
        logger.warning(String.format("Unknown request from: %d", request.requesterId));
        connectionContainer.getConnectionById(request.requesterId).ifPresent(
                connection -> connection.send("Unknown request code"));
    }

    public void handleRequest(Request request) {
        new Thread(() -> {
            switch (request.requestCode) {
                case DISCONNECT:
                    handleDisconnectRequest(request);
                    break;
                case UPDATE:
                    handleUpdateRequest((UpdateRequest) request);
                    break;
                case FILE_LIST:
                    handleFileListRequest(request);
                    break;
                case PUSH:
                    handlePushRequest((PushRequest) request);
                    break;
                case PULL:
                    handlePullRequest((PullRequest) request);
                    break;
                default:
                    handleUnknownRequest(request);
                    break;
            }
        }).start();
    }
}
