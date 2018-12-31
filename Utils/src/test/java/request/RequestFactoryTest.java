package request;

import org.junit.Test;

import static org.junit.Assert.*;

public class RequestFactoryTest {

    private int requesterId = 1;

    @Test
    public void RequestFactory_Disconnect_Correct() {
        String requestStr = "disconnect";
        Request request = RequestFactory.getRequest(requesterId, requestStr);

        assertEquals(request.requesterId, requesterId);
        assertEquals(request.requestCode, RequestCode.DISCONNECT);
    }

    @Test
    public void RequestFactory_Update_Correct() {
        String requestStr = "Update 100 1000 halko";
        UpdateRequest request = (UpdateRequest)RequestFactory.getRequest(requesterId, requestStr);

        assertEquals(request.requesterId, requesterId);
        assertEquals(request.requestCode, RequestCode.UPDATE);
        assertEquals(request.downloaded, 100L);
        assertEquals(request.uploaded, 1000L);
        assertEquals(request.fileName, "halko");
    }

    @Test
    public void RequestFactory_FileList_Correct() {
        String requestStr = "files";
        Request request = RequestFactory.getRequest(requesterId, requestStr);

        assertEquals(request.requesterId, requesterId);
        assertEquals(request.requestCode, RequestCode.FILE_LIST);
    }

    @Test
    public void RequestFactory_Pull_Correct() {
        String requestStr = "pull file";
        PullRequest request = (PullRequest) RequestFactory.getRequest(requesterId, requestStr);

        assertEquals(request.requesterId, requesterId);
        assertEquals(request.requestCode, RequestCode.PULL);
        assertEquals(request.fileName, "file");
    }

    @Test
    public void RequestFactory_Push_Correct() {
        String requestStr = "push 20 file";
        PushRequest request = (PushRequest) RequestFactory.getRequest(requesterId, requestStr);

        assertEquals(request.requesterId, requesterId);
        assertEquals(request.requestCode, RequestCode.PUSH);
        assertEquals(request.destinationHostId, 20);
        assertEquals(request.fileName, "file");
    }
}