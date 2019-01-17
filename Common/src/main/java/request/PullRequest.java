package request;

public class PullRequest extends Request {

    public final String fileName;   //file a requester wants to download

    public PullRequest(int requesterId, RequestCode requestCode, String fileName) {
        super(requesterId, requestCode);
        this.fileName = fileName;
    }
}
