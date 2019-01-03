package request;

public class PushRequest extends SimpleRequest {

    public final int destinationHostId; //Host id to whom requester wants to push a file
    public final String fileName;       //name of a file that is goind to be sent

    public PushRequest(int requesterId, RequestCode requestCode,
                       int destinationHostId, String fileName) {
        super(requesterId, requestCode);
        this.destinationHostId = destinationHostId;
        this.fileName = fileName;
    }
}
