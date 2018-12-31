import org.junit.Test;

import java.net.InetSocketAddress;

import static org.junit.Assert.*;

public class TorrentContainerTest {

    ClientMetadata clientA = new ClientMetadata(1, new InetSocketAddress(1));
    FileMetadata[] clientAFiles = {
            new FileMetadata("file1", 10, "md1"),
            new FileMetadata("file2", 20, "md2")
    };

    ClientMetadata clientB = new ClientMetadata(2, new InetSocketAddress(2));
    FileMetadata[] clientBFiles = {
            new FileMetadata("file1", 10, "md1"),
            new FileMetadata("file20", 200, "md20")
    };

    @Test
    public void TorrentContainer_Creation() {
        TorrentContainer container = new TorrentContainer();

        assertTrue(container.getAllTrackedTorrentsFileMetadata().isEmpty());
    }

    @Test
    public void TorrentContainer_OnClientConnected_SingleClient() {
        TorrentContainer container = new TorrentContainer();

        container.onClientConnected(clientA, clientAFiles);

        assertTrue(container.getTrackedTorrentByFileName("file1").isPresent());
        assertTrue(container.getTrackedTorrentByMd5sum("md1").isPresent());
        assertArrayEquals(container.getAllTrackedTorrentsFileMetadata().toArray(), clientAFiles);
    }

    @Test
    public void TorrentContainer_OnClientDisconnected_SingleClient() {
        TorrentContainer container = new TorrentContainer();

        container.onClientConnected(clientA, clientAFiles);
        container.onClientDisconnected(clientA.id);

        assertFalse(container.getTrackedTorrentByFileName("file1").isPresent());
        assertFalse(container.getTrackedTorrentByFileName("file2").isPresent());
        assertTrue(container.getAllTrackedTorrentsFileMetadata().isEmpty());
    }

    @Test
    public void TorrentContainer_OnClientConnected_MultipleClients() {
        TorrentContainer container = new TorrentContainer();

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