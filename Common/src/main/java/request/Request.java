package request;

import java.io.Serializable;

public class Request implements Serializable {

    public final int requesterId;           //Id of host who sent the request
    public final RequestCode requestCode;   //Code of request

    public Request(int requesterId, RequestCode requestCode) {
        this.requesterId = requesterId;
        this.requestCode = requestCode;
    }
}
