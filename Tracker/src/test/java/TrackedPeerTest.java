import common.ClientMetadata;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class TrackedPeerTest {

    @Mock
    private ClientMetadata clientMetadata;

    private final int initDownloaded = 10;
    private final int initUploaded = 5;
    private final int initLeft = 100;

    @Test
    public void TrackedPeerTest_CreationWithoutInitValues() {
        TrackedPeer peer = new TrackedPeer(clientMetadata);

        assertEquals(peer.getDownloaded(), 0);
        assertEquals(peer.getUploaded(), 0);
        assertEquals(peer.getDownloaded(), 0);
    }

    @Test
    public void TrackedPeerTest_CreationWithInitValues() {
        TrackedPeer peer = new TrackedPeer(clientMetadata, initDownloaded, initUploaded, initLeft);

        assertEquals(peer.getDownloaded(), initDownloaded);
        assertEquals(peer.getUploaded(), initUploaded);
        assertEquals(peer.getLeft(), initLeft);
    }

    @Test
    public void TrackedPeerTest_Update() {
        TrackedPeer peer = new TrackedPeer(clientMetadata, initDownloaded, initUploaded, initLeft);
        int downloaded = 1000;
        int uploaded = 20;

        peer.update(downloaded, uploaded);

        assertEquals(peer.getDownloaded(), initDownloaded + downloaded);
        assertEquals(peer.getUploaded(), initUploaded + uploaded);
        assertEquals(peer.getLeft(), initLeft - downloaded);
    }
}