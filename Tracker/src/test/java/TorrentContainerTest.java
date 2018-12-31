import org.junit.Before;
import org.junit.Test;

import java.net.InetSocketAddress;

import static org.junit.Assert.*;

public class TorrentContainerTest {

    private ClientMetadata clientA = new ClientMetadata(1, new InetSocketAddress(1));
    private FileMetadata[] clientAFiles = { new FileMetadata("file1", 10, "md1"),
                                    new FileMetadata("file2", 20, "md2")};

    private ClientMetadata clientB = new ClientMetadata(2, new InetSocketAddress(2));
    private FileMetadata[] clientBFiles = { new FileMetadata("file1", 10, "md1"),
                                    new FileMetadata("file20", 200, "md20")};

    private TorrentContainer container;

    @Before
    public void setup() {
        container = new TorrentContainer();
    }

    @Test
    public void TorrentContainer_Creation() {
        assertTrue(container.getAllTrackedTorrentsFileMetadata().isEmpty());
    }

    @Test
    public void TorrentContainer_OnClientConnected_SingleClient() {
        container.onClientConnected(clientA, clientAFiles);
        assertArrayEquals(container.getAllTrackedTorrentsFileMetadata().toArray(), clientAFiles);

        TrackedTorrent torrent1 = container.getTrackedTorrentByFileName(clientAFiles[0].name).get();
        TrackedTorrent torrent2 = container.getTrackedTorrentByFileName(clientAFiles[1].name).get();

        assertTrue(torrent1.getPeerById(clientA.id).isPresent());
        assertTrue(torrent2.getPeerById(clientA.id).isPresent());
    }

    @Test
    public void TorrentContainer_OnClientDisconnected_SingleClient() {
        container.onClientConnected(clientA, clientAFiles);
        container.onClientDisconnected(clientA.id);

        assertFalse(container.getTrackedTorrentByFileName(clientAFiles[0].name).isPresent());
        assertFalse(container.getTrackedTorrentByFileName(clientAFiles[1].name).isPresent());
        assertTrue(container.getAllTrackedTorrentsFileMetadata().isEmpty());
    }

    @Test
    public void TorrentContainer_OnClientConnected_MultipleClients() {
        container.onClientConnected(clientA, clientAFiles);
        container.onClientConnected(clientB, clientBFiles);

        for (FileMetadata fileMetadata : clientAFiles) {
            assertTrue(container.getTrackedTorrentByFileName(fileMetadata.name).isPresent());
        }

        for (FileMetadata fileMetadata : clientBFiles) {
            assertTrue(container.getTrackedTorrentByFileName(fileMetadata.name).isPresent());
        }

        assertTrue(container.getTrackedTorrentByFileName("file1").get().getPeerById(clientA.id).isPresent());
        assertTrue(container.getTrackedTorrentByFileName("file1").get().getPeerById(clientB.id).isPresent());
    }


}