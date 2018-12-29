import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TorrentContainer {

    private static final Logger logger = Logger.getLogger(TorrentContainer.class.getName());

    private List<TrackedTorrent> trackedTorrents;

    public TorrentContainer() {
        trackedTorrents = Collections.synchronizedList(new LinkedList<>());
    }

    public void onClientConnected(ClientMetadata clientMetadata, FileMetadata[] files) {
        for (FileMetadata fileMetadata : files) {
            Optional<TrackedTorrent> torrent = getTrackedTorrentByMd5sum(fileMetadata.md5sum);
            if (torrent.isPresent()) {
                torrent.get().addPeer(new TrackedPeer(clientMetadata));
                logger.info(String.format("Torrent %s found. Peer %d added.", fileMetadata.name, clientMetadata.id));
            } else {
                trackedTorrents.add(new TrackedTorrent(fileMetadata, new TrackedPeer(clientMetadata)));
                logger.info(String.format("Torrent %s not found and has been added", fileMetadata.name));
            }
        }
    }

    public void onClientDisconnected(int clientId) {
        for (TrackedTorrent trackedTorrent : trackedTorrents) {
            Optional<TrackedPeer> peer = trackedTorrent.getPeerById(clientId);
            if (peer.isPresent()) {
                trackedTorrent.removePeer(peer.get());
                logger.info(String.format("peer {} is removed from torrent {}",
                        clientId, trackedTorrent.fileMetadata.name));
                if (!trackedTorrent.hasAnyPeer()) {
                    trackedTorrents.remove(trackedTorrent);
                    logger.info(String.format("torrent %s has no peers and was removed",
                                    trackedTorrent.fileMetadata.name));

                }
            }
        }
    }

    public Optional<TrackedTorrent> getTrackedTorrentByFileName(String fileName) {
        for (TrackedTorrent torrent : trackedTorrents) {
            if (torrent.fileMetadata.name.equals(fileName)) {
                return Optional.of(torrent);
            }
        }
        return Optional.empty();
    }

    public Optional<TrackedTorrent> getTrackedTorrentByMd5sum(String md5sum) {
        for (TrackedTorrent torrent : trackedTorrents) {
            if (torrent.fileMetadata.md5sum.equals(md5sum)) {
                return Optional.of(torrent);
            }
        }
        return Optional.empty();
    }

}
