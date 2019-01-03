import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class TorrentContainer {

    private static final Logger logger = Logger.getLogger(TorrentContainer.class.getName());

    private List<TrackedTorrent> trackedTorrents;

    public TorrentContainer() {
        trackedTorrents = Collections.synchronizedList(new LinkedList<>());
    }

    /**
     * Invoked when new client connects to tracker.
     * Checks if file owned by new client is already tracked (other clients may have it)
     * if (true) -> adds new peer(new client) to already tracked torrent
     * if (false) -> creates new tracked torrent and ads new client as it's peer
     *
     * @param clientMetadata newly connected client metadata(InetSocketAddress and id)
     * @param files FileMetadata of files owned by newly connected client
     */
    public void onClientConnected(ClientMetadata clientMetadata, FileMetadata[] files) {
        for (FileMetadata fileMetadata : files) {
            Optional<TrackedTorrent> torrent = getTrackedTorrentByFileName(fileMetadata.name);
            if (torrent.isPresent()) {
                torrent.get().addPeer(new TrackedPeer(clientMetadata));
                logger.info(String.format("Torrent %s found. Peer %d added.", fileMetadata.name, clientMetadata.id));
            } else {
                trackedTorrents.add(new TrackedTorrent(fileMetadata, new TrackedPeer(clientMetadata)));
                logger.info(String.format("Torrent %s not found and has been added", fileMetadata.name));
            }
        }
    }

    /**
     * Invoked when client disconnects
     * Iterates through all tracked torrents and checks if client
     * that disconnected was peer of said torrent
     * if (true) -> removes peer and checks if torrent has any peer (if not removes torrent)
     *
     * @param clientId id of a client that disconnected from tracker
     */
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

    public List<FileMetadata> getAllTrackedTorrentsFileMetadata() {
        return trackedTorrents.stream().map(trackedTorrent -> trackedTorrent.fileMetadata).collect(Collectors.toList());
    }

}
