package request;

public class PullRequest extends Request {

    public final String fileName;

    public PullRequest(int requesterId, RequestCode requestCode, String fileName) {
        super(requesterId, requestCode);
        this.fileName = fileName;
    }
}
