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

    @Test
    public void RequestFactory_Request_Invalid() {
        String requestStr = "tradalksd";
        Request request = RequestFactory.getRequest(requesterId, requestStr);

        assertEquals(request.requesterId, requesterId);
        assertEquals(request.requestCode, RequestCode.UNKNOWN);
    }

    @Test
    public void RequestFactory_Update_Invalid() {
        String requestStr = "Update tralala 10 1l2kjjf3";
        UpdateRequest request = (UpdateRequest) RequestFactory.getRequest(requesterId, requestStr);

        assertEquals(request.requesterId, requesterId);
        assertEquals(request.requestCode, RequestCode.UNKNOWN);
        assertEquals(request.downloaded, 0);
        assertEquals(request.uploaded, 0);
        assertNull(request.fileName);
    }

    @Test
    public void RequestFactory_Pull_Invalid() {
        String requestStr = "pull";
        PullRequest request = (PullRequest) RequestFactory.getRequest(requesterId, requestStr);

        assertEquals(request.requesterId, requesterId);
        assertEquals(request.requestCode, RequestCode.UNKNOWN);
        assertNull(request.fileName);
    }

    @Test
    public void RequestFactory_Push_Invalid() {
        String requestStr = "push file 123123l";
        PushRequest request = (PushRequest) RequestFactory.getRequest(requesterId, requestStr);

        assertEquals(request.requesterId, requesterId);
        assertEquals(request.requestCode, RequestCode.UNKNOWN);
        assertEquals(request.destinationHostId, 0);
        assertNull(request.fileName);
    }
}