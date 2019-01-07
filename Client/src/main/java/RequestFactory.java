import request.*;

import java.util.Arrays;

public class RequestFactory {

    private int clientId;

    public RequestFactory(int clientId) {
        this.clientId = clientId;
    }

    /**
     * Returns Request depending on what client typed into console
     *
     * @param requestStr arguments that clients enters into console
     * @return Request object dependend on arguments
     */
    public Request getRequest(String requestStr) {
        String[] args = requestStr.split("\\s+");
        String requestCodeArg = args[0].toLowerCase();
        switch (requestCodeArg) {
            case "disconnect": return getDisconnectRequest();
            case "files":      return getFileListRequest();
            case "pull":       return getPullRequest(Arrays.copyOfRange(args, 1, args.length));
            case "push":       return getPushRequest(Arrays.copyOfRange(args, 1, args.length));
            default:           return new Request(clientId, RequestCode.UNKNOWN);
        }
    }

    /**
     * @return {@link Request} with Disconnect {@link RequestCode}
     */
    private Request getDisconnectRequest() {
        return new Request(clientId, RequestCode.DISCONNECT);
    }

    /**
     * @return {@link Request} with FILE_LIST {@link RequestCode}
     */
    private Request getFileListRequest() {
        return new Request(clientId, RequestCode.FILE_LIST);
    }

    /**
     * This function checks if given arguments are correct and returns {@link PullRequest}
     *
     * @param args
     * @return {@link PullRequest}
     */
    private PullRequest getPullRequest(String[] args) {
        if (args.length == 2) {
            if (args[1].matches("[0-9]+")) {
                String fileName = args[0];
                long downloaded = Long.parseLong(args[1]);
                return new PullRequest(clientId, RequestCode.PULL, fileName, downloaded);
            }
        }
        return new PullRequest(clientId, RequestCode.UNKNOWN, null, 0);
    }

    private PushRequest getPushRequest(String[] args) {
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
