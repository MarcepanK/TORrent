package request;

import java.io.Serializable;

public class Request implements Serializable {

    public final int requesterId;
    public final RequestCode requestCode;


    public Request (int requesterId, RequestCode requestCode) {
        this.requesterId = requesterId;
        this.requestCode = requestCode;
    }
}
