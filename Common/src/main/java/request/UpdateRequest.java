package request;

public class UpdateRequest extends SimpleRequest {

    public final long downloaded;   //
    public final long uploaded;
    public final String fileName;

    public UpdateRequest(int requesterId, RequestCode requestCode,
                         long downloaded, long uploaded, String fileName) {
        super(requesterId, requestCode);
        this.downloaded = downloaded;
        this.uploaded = uploaded;
        this.fileName = fileName;
    }
}
