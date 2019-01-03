package request;

public class PullRequest extends SimpleRequest {

    public final String fileName;   //file a requester wants to download
    public final long downloaded;

    public PullRequest(int requesterId, RequestCode requestCode, String fileName, long downloaded) {
        super(requesterId, requestCode);
        this.fileName = fileName;
        this.downloaded = downloaded;
    }
}
