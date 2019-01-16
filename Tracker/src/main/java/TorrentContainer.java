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

    /**
     * <p>Invoked when new client connects to tracker.</p>
     * Checks if file owned by new client is already tracked (other clients may have it)
     * if (true) -> adds new peer(new client) to already tracked torrent
     * if (false) -> creates new tracked torrent and ads new client as it's peer
     *
     * @param clientMetadata newly connected client metadata(InetSocketAddress and id)
     * @param ownedFiles FileMetadata of files owned by newly connected client
     */
    public void onClientConnected(ClientMetadata clientMetadata, FileMetadata[] ownedFiles) {
        for (FileMetadata fileMetadata : ownedFiles) {
            Optional<TrackedTorrent> torrent = getTrackedTorrentByMd5Sum(fileMetadata.md5sum);
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
     * <p>Invoked when client disconnects</p>
     * <p>Iterates through all tracked torrents and checks if client
     * that disconnected was peer of said torrent</p>
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

    /**
     * Searches and returns Optional of tracked torrent by given file name
     * @param fileName name of a file that torrent is tracking
     * @return Optional of TrackedTorrent
     */
    public Optional<TrackedTorrent> getTrackedTorrentByFileName(String fileName) {
        for (TrackedTorrent torrent : trackedTorrents) {
            if (torrent.fileMetadata.name.equals(fileName)) {
                return Optional.of(torrent);
            }
        }
        return Optional.empty();
    }

    public Optional<TrackedTorrent> getTrackedTorrentByMd5Sum(String md5sum) {
        for (TrackedTorrent torrent : trackedTorrents) {
            if(torrent.fileMetadata.md5sum.equals(md5sum)) {
                return Optional.of(torrent);
            }
        }
        return Optional.empty();
    }

    public List<FileMetadata> getAllTrackedTorrentsFileMetadata() {
        return trackedTorrents.stream().map(trackedTorrent -> trackedTorrent.fileMetadata).collect(Collectors.toList());
    }

    /**
     * Returns list of fileMetadata of tracked Torrents that aren't tracked by
     * client with given id
     * Purpose of this function is to call it when client sends request for file list
     * and we don't want to send him files that he already has
     *
     * @param clientId id of client who requests file list
     * @return Collection of fileMetadata objects
     */
    public ArrayList<FileMetadata> provideFileListForCient(int clientId) {
        ArrayList<FileMetadata> fileList = new ArrayList<>();
        for (TrackedTorrent torrent : trackedTorrents) {
            if (!torrent.getPeerById(clientId).isPresent()) {
                fileList.add(torrent.fileMetadata);
            }
        }
        return fileList;
    }

}
