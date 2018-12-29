import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class TrackedTorrent {

    private static final Logger logger = Logger.getLogger(TrackedTorrent.class.getName());

    public final FileMetadata fileMetadata;
    private List<TrackedPeer> peers;

    public TrackedTorrent(FileMetadata fileMetadata, TrackedPeer trackedPeer) {
        this.fileMetadata = fileMetadata;
        peers = Collections.synchronizedList(new LinkedList<>());
        peers.add(trackedPeer);
    }

    public void addPeer(TrackedPeer trackedPeer) {
        if (isUniquePeer(trackedPeer)) {
            peers.add(trackedPeer);
            logger.info(String.format("peer id: %d added to torrent %s", trackedPeer.id, fileMetadata.name));
        }
    }

    public void removePeer(TrackedPeer trackedPeerToRemove) {
        peers.remove(trackedPeerToRemove);
        logger.info(String.format("peer id: %d removed from torrent %s", trackedPeerToRemove.id, fileMetadata.name));
    }

    private boolean isUniquePeer(TrackedPeer peer) {
        for (TrackedPeer trackedPeer : peers) {
            if (trackedPeer.id == peer.id) {
                return false;
            }
        }
        return true;
    }

    public boolean hasAnyPeer() {
        return peers.isEmpty();
    }

    public Optional<TrackedPeer> getPeerById(int id) {
        for (TrackedPeer trackedPeer : peers) {
            if (trackedPeer.id == id) {
                return Optional.of(trackedPeer);
            }
        }
        return Optional.empty();
    }
}
