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
        torrentContainer.onClientDisconnected(request.requesterId);
        connectionContainer.onClientDisconnected(request.requesterId);
    }

    private void handleUpdateRequest(UpdateRequest updateRequest) {
        torrentContainer.getTrackedTorrentByFileName(updateRequest.fileName).ifPresent(
                trackedTorrent -> trackedTorrent.getPeerById(updateRequest.requesterId).ifPresent(
                        trackedPeer -> trackedPeer.update(updateRequest.downloaded, updateRequest.uploaded)));
    }

    private void handleFileListRequest(Request request) {
        connectionContainer.getConnectionById(request.requesterId).ifPresent(
                connection -> connection.send(torrentContainer.getAllTrackedTorrentsFileMetadata()));
    }

    private void handlePullRequest(PullRequest pullRequest) {

    }

    private void handlePushRequest(PushRequest pushRequest) {

    }

    private void handleUnknownRequest(Request request) {
        connectionContainer.getConnectionById(request.requesterId).ifPresent(
                connection -> connection.send("Unknown request code"));
    }

    public void handleRequest(Request request) {
        new Thread(() -> {
            logger.info(String.format("Handling request: %s from id: %d",
                    request.requestCode.toString(), request.requesterId));
            switch(request.requestCode) {
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
                    handlePushRequest((PushRequest)request);
                    break;
                case PULL:
                    handlePullRequest((PullRequest)request);
                    break;
                default:
                    handleUnknownRequest(request);
                    break;
            }
        }).start();
    }
}
