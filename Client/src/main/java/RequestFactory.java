import request.*;

import java.util.Arrays;

public class RequestFactory {

    /**
     * Returns Request depending on what client typed into console
     *
     * @param requestStr arguments that clients enters into console
     * @return Request object dependend on arguments
     */
    public static Request getDisconnectRequest(int clientId, String requestStr) {
        String[] args = requestStr.split("\\s+");
        String requestCodeArg = args[0].toLowerCase();
        switch (requestCodeArg) {
            case "disconnect": return getDisconnectRequest(clientId);
            case "files":      return getFileListRequest(clientId);
            case "pull":       return getPullRequest(clientId, Arrays.copyOfRange(args, 1, args.length));
            case "push":       return getPushRequest(clientId, Arrays.copyOfRange(args, 1, args.length));
            default:           return new Request(clientId, RequestCode.UNKNOWN);
        }
    }

    public static Request getDisconnectRequest() {
        return new Request(0, RequestCode.DISCONNECT);
    }

    public static UpdateRequest getUpdateRequest(int clientId, long downloaded, long uploaded, String fileName) {
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
        if (args.length == 2) {
            if (args[1].matches("[0-9]+")) {
                String fileName = args[0];
                long downloaded = Long.parseLong(args[1]);
                return new PullRequest(clientId, RequestCode.PULL, fileName, downloaded);
            }
        }
        return new PullRequest(clientId, RequestCode.UNKNOWN, null, 0);
    }

    /**
     * Checks if given arguments are correct and returns {@link PushRequest}
     *
     * @param clientId requester id
     * @param args args that user typed into console
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
