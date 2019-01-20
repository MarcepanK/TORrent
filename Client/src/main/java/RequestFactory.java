import common.FileMetadata;
import request.*;

import java.util.Arrays;
import java.util.Collection;

public class RequestFactory {

    /**
     * Returns Request depending on what client typed into console
     *
     * @param requestStr arguments that client types into console
     * @return Request object depending on arguments
     */
    public static Request getRequest(int clientId, String requestStr) {
        String[] args = requestStr.split("\\s+");
        String requestCodeArg = args[0].toLowerCase();
        switch (requestCodeArg) {
            case "disconnect":
                return getDisconnectRequest(clientId);
            case "files":
                return getFileListRequest(clientId);
            case "pull":
                return getPullRequest(clientId, Arrays.copyOfRange(args, 1, args.length));
            case "push":
                return getPushRequest(clientId, Arrays.copyOfRange(args, 1, args.length));
            default:
                return new Request(clientId, RequestCode.UNKNOWN);
        }
    }

    public static RetryDownloadRequest getRequest(int clientId, FileMetadata transferredFileMetadata,
                                                  Collection<Integer> missingPiecesIndexes, int missingBytesCount,
                                                  int biggestPieceIndex) {
        return new RetryDownloadRequest(clientId, RequestCode.RETRY, transferredFileMetadata,
                missingPiecesIndexes, missingBytesCount, biggestPieceIndex);
    }

    public static Request getRequest() {
        return new Request(0, RequestCode.DISCONNECT);
    }

    public static UpdateRequest getRequest(int clientId, long downloaded, long uploaded, String fileName) {
        return new UpdateRequest(clientId, RequestCode.UPDATE, downloaded, uploaded, fileName);
    }

    private static Request getDisconnectRequest(int clientId) {
        return new Request(clientId, RequestCode.DISCONNECT);
    }

    private static Request getFileListRequest(int clientId) {
        return new Request(clientId, RequestCode.FILE_LIST);
    }

    /**
     * Checks if given arguments are correct and returns {@link PullRequest}
     *
     * @param args args that user typed into console
     * @return {@link PullRequest}
     */
    private static PullRequest getPullRequest(int clientId, String[] args) {
        if (args.length == 1) {
            return new PullRequest(clientId, RequestCode.PULL, args[0]);
        }
        return new PullRequest(clientId, RequestCode.UNKNOWN, args[0]);
    }

    /**
     * Checks if given arguments are correct and returns {@link PushRequest}
     *
     * @param clientId requester id
     * @param args     args that user typed into console
     * @return {@link PushRequest}
     */
    private static PushRequest getPushRequest(int clientId, String[] args) {
        if (args.length == 2) {
            if (args[0].matches("[0-9]+")) {
                int destinationHostId = Integer.parseInt(args[0]);
                String fileName = args[1];
                return new PushRequest(clientId, RequestCode.PUSH, destinationHostId, fileName);
            }
        }
        return new PushRequest(clientId, RequestCode.UNKNOWN, 0, null);
    }
}
