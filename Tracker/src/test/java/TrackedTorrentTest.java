import org.junit.Test;

import java.net.InetSocketAddress;

import static org.junit.Assert.*;

public class TrackedTorrentTest {

    private final ClientMetadata clientA = new ClientMetadata(1, new InetSocketAddress("localhost", 1));
    private final ClientMetadata clientB = new ClientMetadata(2, new InetSocketAddress("localhost", 2));
    private final FileMetadata fileMetadata = new FileMetadata("A", 10, "MdA");

    @Test
    public void TrackedTorrent_Creation() {
        TrackedPeer peer = new TrackedPeer(clientA);
        TrackedTorrent torrent = new TrackedTorrent(fileMetadata, peer);

        assertTrue(torrent.hasAnyPeer());
        assertEquals(torrent.fileMetadata, fileMetadata);
        assertEquals(torrent.getPeerById(peer.id).get(), peer);
    }

    @Test
    public void TrackedTorrent_AddPeer() {
        TrackedPeer peerA = new TrackedPeer(clientA);
        TrackedPeer peerB = new TrackedPeer(clientB);

        TrackedTorrent torrent = new TrackedTorrent(fileMetadata, peerA);
        torrent.addPeer(peerB);

        assertTrue(torrent.hasAnyPeer());
        assertEquals(torrent.fileMetadata, fileMetadata);
        assertTrue(torrent.getPeerById(1).isPresent());
        assertEquals(torrent.getPeerById(1).get(), peerA);
        assertTrue(torrent.getPeerById(2).isPresent());
        assertEquals(torrent.getPeerById(2).get(), peerB);
    }

    @Test
    public void TrackedTorrent_RemovePeer() {
        TrackedPeer peerA = new TrackedPeer(clientA);
        TrackedPeer peerB = new TrackedPeer(clientB);
        TrackedTorrent torrent = new TrackedTorrent(fileMetadata, peerA);

        torrent.removePeer(peerA);
        assertFalse(torrent.hasAnyPeer());
        assertFalse(torrent.getPeerById(peerA.id).isPresent());

        torrent.addPeer(peerB);
        assertTrue(torrent.hasAnyPeer());
        assertTrue(torrent.getPeerById(peerB.id).isPresent());
    }

}