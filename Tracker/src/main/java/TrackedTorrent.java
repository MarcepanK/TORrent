import common.FileMetadata;

import java.util.*;
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
            logger.info(String.format("peer id: %d added to torrent %s", trackedPeer.clientMetadata.id, fileMetadata.name));
        } else {
            logger.warning(String.format("Peer with id: %d is already tracking torrent: %s",
                    trackedPeer.clientMetadata.id, fileMetadata.name));
        }
    }

    public void removePeer(TrackedPeer trackedPeerToRemove) {
        peers.remove(trackedPeerToRemove);
        logger.info(String.format("peer id: %d removed from torrent %s",
                trackedPeerToRemove.clientMetadata.id, fileMetadata.name));
    }

    public void removePeer(int peerId) {
        Optional<TrackedPeer> peer = getPeerById(peerId);
        if (peer.isPresent()) {
            removePeer(peer.get());
        } else {
            logger.warning(String.format("peer with id: %d not found", peerId));
        }
    }

    private boolean isUniquePeer(TrackedPeer peer) {
        for (TrackedPeer trackedPeer : peers) {
            if (trackedPeer.clientMetadata.id == peer.clientMetadata.id) {
                return false;
            }
        }
        return true;
    }

    public boolean isPeer(int clientId) {
        for (TrackedPeer peer : peers) {
            if (peer.clientMetadata.id == clientId) {
                return true;
            }
        }
        return false;
    }

    public boolean hasAnyPeer() {
        return peers.size() != 0;
    }

    public Optional<TrackedPeer> getPeerById(int id) {
        for (TrackedPeer trackedPeer : peers) {
            if (trackedPeer.clientMetadata.id == id) {
                return Optional.of(trackedPeer);
            }
        }
        return Optional.empty();
    }

    public TrackedPeer[] getAllPeers() {
        return peers.toArray(new TrackedPeer[peers.size()]);
    }

    public TrackedPeer[] getPeersWithCompleteFile() {
        ArrayList<TrackedPeer> seeds = new ArrayList<>();
        TrackedPeer[] allPeers = getAllPeers();
        for (TrackedPeer peer : allPeers) {
            if (peer.getLeft() == 0) {
                seeds.add(peer);
            }
        }
        return (TrackedPeer[]) seeds.toArray();
    }
}
