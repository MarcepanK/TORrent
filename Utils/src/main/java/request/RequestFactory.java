package request;

import java.util.Arrays;

public class RequestFactory {

    public static Request getRequest(int requesterId, String requestArgs) {
        String[] args = requestArgs.split("\\s+");
        String requestCodeArg = args[0].toLowerCase();
        switch(requestCodeArg) {
            case "disconnect": return getDisconnectRequest(requesterId);
            case "update":     return getUpdateRequest(requesterId, Arrays.copyOfRange(args, 1, args.length));
            case "files":      return getFileListRequest(requesterId);
            case "pull":       return getPullRequest(requesterId, Arrays.copyOfRange(args, 1, args.length));
            case "push":       return getPushRequest(requesterId, Arrays.copyOfRange(args, 1, args.length));
            default:           return new Request(requesterId, RequestCode.UNKNOWN);
        }
    }

    private static Request getDisconnectRequest(int requesterId) {
        return new Request(requesterId, RequestCode.DISCONNECT);
    }

    private static UpdateRequest getUpdateRequest(int requesterId, String[] args) {
        if (args.length == 3) {
            if (args[0].matches("[0-9]+") && args[1].matches("[0-9]+")) {
                long downloaded = Long.parseLong(args[0]);
                long uploaded = Long.parseLong(args[1]);
                String fileName = args[2];
                return new UpdateRequest(requesterId, RequestCode.UPDATE, downloaded, uploaded, fileName);
            }
        }
        return new UpdateRequest(requesterId, RequestCode.UNKNOWN, 0,0, null);
    }

    private static Request getFileListRequest(int requesterId) {
        return new Request(requesterId, RequestCode.FILE_LIST);
    }

    private static PullRequest getPullRequest(int requesterId, String[] args) {
       if (args.length == 2) {
           if (args[1].matches("[0-9]+")) {
               String fileName = args[0];
               long downloaded = Long.parseLong(args[1]);
               return new PullRequest(requesterId, RequestCode.PULL, fileName, downloaded);
           }
       }
       return new PullRequest(requesterId, RequestCode.UNKNOWN, null, 0);
    }

    private static PushRequest getPushRequest(int requesterId, String[] args) {
        if (args.length == 2) {
            if(args[0].matches("[0-9]+")) {
                int destinationHostId = Integer.parseInt(args[0]);
                String fileName = args[1];
                return new PushRequest(requesterId, RequestCode.PUSH, destinationHostId, fileName);
            }
        }
        return new PushRequest(requesterId, RequestCode.UNKNOWN, 0, null);
    }
}
