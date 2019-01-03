package request;

import java.io.Serializable;

public class SimpleRequest implements Serializable {

    public final int requesterId;           //Id of host who sent the request
    public final RequestCode requestCode;   //Code of request

    public SimpleRequest(int requesterId, RequestCode requestCode) {
        this.requesterId = requesterId;
        this.requestCode = requestCode;
    }
}
