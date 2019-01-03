import org.junit.Before;
import org.junit.Test;

import java.net.InetSocketAddress;

import static org.junit.Assert.*;

public class TrackedTorrentTest {

    private final FileMetadata fileMetadata = new FileMetadata("A", 10, "MdA");
    private TrackedPeer peerA = new TrackedPeer(new ClientMetadata(1, new InetSocketAddress(1)));
    private TrackedPeer peerB = new TrackedPeer(new ClientMetadata(2, new InetSocketAddress(2)));

    private TrackedTorrent torrent;

    @Before
    public void setup() {
        torrent = new TrackedTorrent(fileMetadata, peerA);
    }

    @Test
    public void TrackedTorrent_Creation() {
        assertTrue(torrent.hasAnyPeer());
        assertEquals(torrent.fileMetadata, fileMetadata);
        assertTrue(torrent.getPeerById(peerA.id).isPresent());
    }

    @Test
    public void TrackedTorrent_RemovePeer_singlePeer() {
        torrent.removePeer(peerA.id);

        assertFalse(torrent.getPeerById(peerA.id).isPresent());
        assertFalse(torrent.hasAnyPeer());
    }

    @Test
    public void TrackedTorrent_AddPeer_multiplePeers() {
        torrent.addPeer(peerB);

        assertTrue(torrent.hasAnyPeer());
        assertTrue(torrent.getPeerById(peerA.id).isPresent());
        assertTrue(torrent.getPeerById(peerB.id).isPresent());
    }

    @Test
    public void TrackedTorrent_RemovePeer_multiplePeers() {
        torrent.addPeer(peerB);
        torrent.removePeer(peerA);

        assertFalse(torrent.getPeerById(peerA.id).isPresent());
        assertTrue(torrent.getPeerById(peerB.id).isPresent());
        assertTrue(torrent.hasAnyPeer());
    }

    @Test
    public void TrackedTorrent_RemovePeer_allPeers() {
        torrent.addPeer(peerB);
        torrent.removePeer(peerA);
        torrent.removePeer(peerB);

        assertFalse(torrent.getPeerById(peerA.id).isPresent());
        assertFalse(torrent.getPeerById(peerB.id).isPresent());
        assertFalse(torrent.hasAnyPeer());
    }

}