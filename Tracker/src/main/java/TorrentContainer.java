import common.ClientMetadata;
import common.FileMetadata;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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
        Iterator<TrackedTorrent> torrentIterator = trackedTorrents.iterator();
        while(torrentIterator.hasNext()) {
            TrackedTorrent torrent = torrentIterator.next();
            Optional<TrackedPeer> peer = torrent.getPeerById(clientId);
            if (peer.isPresent()) {
                torrent.removePeer(peer.get());
                logger.info(String.format("peer %d is removed from torrent %s",
                        clientId, torrent.fileMetadata.name));
                if (!torrent.hasAnyPeer()) {
                    torrentIterator.remove();
                    logger.info(String.format("torrent %s has no peers and was removed",
                            torrent.fileMetadata.name));
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

    public List<FileMetadata> getAllTrackedTorrentsFileMetadata() {
        return trackedTorrents.stream().map(trackedTorrent -> trackedTorrent.fileMetadata).collect(Collectors.toList());
    }

}
