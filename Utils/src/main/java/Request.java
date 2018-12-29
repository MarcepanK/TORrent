import java.io.Serializable;

public class Request implements Serializable {

    public final RequestCode requestCode;
    public final int requesterId;

    public Request(RequestCode requestCode, int requesterId) {
        this.requestCode = requestCode;
        this.requesterId = requesterId;
    }
}
