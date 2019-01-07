import common.ClientMetadata;
import common.FileMetadata;
import order.DownloadOrder;
import order.UploadOrder;
import request.PullRequest;
import request.PushRequest;

import java.util.ArrayList;
import java.util.Optional;

/**
 * <p>This class is responsible for creating Orders depending on given data</p>
 * <p>used by {@link RequestProcessor}</p>
 */
public class OrderFactory {

    private TorrentContainer torrentContainer;

    public OrderFactory(TorrentContainer torrentContainer) {
        this.torrentContainer = torrentContainer;
    }

    /**
     * Searches for all {@link TrackedPeer} that have requested file
     * and returns Array of {@link UploadOrder} that will be sent
     * to those peers
     *
     * @param request {@link PullRequest} request that has been received
     * @return {@link ArrayList<UploadOrder>}
     */
    public ArrayList<UploadOrder> getUploadOrders(PullRequest request) {
        ArrayList<UploadOrder> uploadOrders = new ArrayList<>();
        Optional<TrackedTorrent> torrent = torrentContainer.getTrackedTorrentByFileName(request.fileName);
        if (torrent.isPresent()) {
            TrackedPeer[] seeds = torrent.get().getPeersWithCompleteFile();
            for(int i=0; i<seeds.length; i++) {
                uploadOrders.add(new UploadOrder(torrent.get().fileMetadata, seeds[i].clientMetadata, i+1, seeds.length));
            }
        }
        uploadOrders.trimToSize();
        return uploadOrders;
    }

    /**
     * Returns {@link UploadOrder} to client that sent request
     *
     * @param request {@link PushRequest} that has been received
     * @return {@link UploadOrder}
     */
    public UploadOrder getUploadOrder(PushRequest request) {
        Optional<TrackedTorrent> torrent = torrentContainer.getTrackedTorrentByFileName(request.fileName);
        if (torrent.isPresent()) {
            Optional<TrackedPeer> peer = torrent.get().getPeerById(request.requesterId);
            if (peer.isPresent()) {
                return new UploadOrder(torrent.get().fileMetadata, peer.get().clientMetadata, 1, 1);
            }
        }
        return null;
    }

    /**
     * Returns {@link DownloadOrder} to client that sent request with Pull {@link request.RequestCode}
     * Searches for all peers that are capable of sending requested file and places their {@link ClientMetadata}
     * in {@link DownloadOrder}
     * @param pullRequest {@link PullRequest} that has been received
     * @return {@link DownloadOrder}
     */
    public DownloadOrder getDownloadOrder(PullRequest pullRequest) {
        ArrayList<ClientMetadata> seedsMetadata = new ArrayList<>();
        Optional<TrackedTorrent> torrent = torrentContainer.getTrackedTorrentByFileName(pullRequest.fileName);
        if (torrent.isPresent()) {
            TrackedPeer[] peers = torrent.get().getAllPeers();
            for (TrackedPeer peer : peers) {
                if (peer.getLeft() == 0) {
                    seedsMetadata.add(peer.clientMetadata);
                }
            }
        }
        return new DownloadOrder(torrent.get().fileMetadata,(ClientMetadata[])seedsMetadata.toArray());
    }

    /**
     * Returns {@link DownloadOrder} with single {@link ClientMetadata} of client that sent {@link PushRequest}
     *
     * @param pushRequest {@link PushRequest} that has been received
     * @return {@link DownloadOrder}
     */
    public DownloadOrder getDownloadOrder(PushRequest pushRequest) {
        Optional<TrackedTorrent> torrent = torrentContainer.getTrackedTorrentByFileName(pushRequest.fileName);
        if (torrent.isPresent()) {
            Optional<TrackedPeer> peer = torrent.get().getPeerById(pushRequest.requesterId);
            if (peer.isPresent()) {
                ClientMetadata[] seeds = new ClientMetadata[1];
                seeds[0] = peer.get().clientMetadata;
                return new DownloadOrder(torrent.get().fileMetadata, seeds);
            }
        }
        return null;
    }

}
