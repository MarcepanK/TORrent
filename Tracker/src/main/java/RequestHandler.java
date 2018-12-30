import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public class RequestHandler {

    Logger logger = Logger.getLogger(RequestHandler.class.getName());

    private ConnectionContainer connectionContainer;
    private TorrentContainer torrentContainer;
    private List<Request> requests;

    public RequestHandler(ConnectionContainer connectionContainer, TorrentContainer torrentContainer) {
        this.connectionContainer = connectionContainer;
        this.torrentContainer = torrentContainer;
        requests = Collections.synchronizedList(new LinkedList<>());
    }

    private void handleFileListRequest(Request request) {

    }

    private void handlePushRequest(Request request) {

    }

    private void handlePullRequest(Request request) {

    }

    private void handleRequest(Request request) {
        logger.info(String.format("Handling request: %s from id: %d",
                request.requestCode.toString(), request.requesterId));
        switch(request.requestCode) {
            case FILE_LIST: handleFileListRequest(request);
            case PUSH: handlePushRequest(request);
            case PULL: handlePullRequest(request);
            default:
        }
    }

}
