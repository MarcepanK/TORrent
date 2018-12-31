package request;

public class PushRequest extends Request {

    public final int destinationHostId;
    public final String fileName;

    public PushRequest(int requesterId, RequestCode requestCode,
                       int destinationHostId, String fileName) {
        super(requesterId, requestCode);
        this.destinationHostId = destinationHostId;
        this.fileName = fileName;
    }
}
