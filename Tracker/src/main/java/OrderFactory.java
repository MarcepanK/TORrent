import common.ClientMetadata;
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

    /**
     * Searches for all {@link TrackedPeer} that have requested file
     * and returns Array of {@link UploadOrder} that will be sent
     * to those peers
     * @param request {@link PullRequest} request that has been received
     * @return {@link ArrayList<UploadOrder>}
     */
    public static UploadOrder[] getUploadOrders(TorrentContainer torrentContainer, PullRequest request) {
        Optional<TrackedTorrent> torrent = torrentContainer.getTrackedTorrentByFileName(request.fileName);
        if (torrent.isPresent()) {
            TrackedPeer[] seeds = torrent.get().getPeersWithCompleteFile();
            UploadOrder[] uploadOrders = new UploadOrder[seeds.length];
            System.out.println("seeds: " + seeds.length);
            for(int i=0; i<seeds.length; i++) {
                System.out.println("added upload order for id: %d" + seeds[i].clientMetadata.id);
                uploadOrders[i] = new UploadOrder(torrent.get().fileMetadata, request.requesterId, i+1, seeds.length);
            }
            System.out.println("Upload orders len: " + uploadOrders.length);
            return uploadOrders;
        }
        return null;
    }

    /**
     * Returns {@link UploadOrder} to client that sent request
     * @param request {@link PushRequest} that has been received
     * @return {@link UploadOrder}
     */
    public static UploadOrder getUploadOrder(TorrentContainer torrentContainer, PushRequest request) {
        Optional<TrackedTorrent> torrent = torrentContainer.getTrackedTorrentByFileName(request.fileName);
        if (torrent.isPresent()) {
            Optional<TrackedPeer> peer = torrent.get().getPeerById(request.requesterId);
            if (peer.isPresent()) {
                return new UploadOrder(torrent.get().fileMetadata, request.destinationHostId, 1, 1);
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
    public static DownloadOrder getDownloadOrder(TorrentContainer torrentContainer, PullRequest pullRequest) {
        ArrayList<ClientMetadata> seedsMetadata = new ArrayList<>();
        Optional<TrackedTorrent> torrent = torrentContainer.getTrackedTorrentByFileName(pullRequest.fileName);
        if (torrent.isPresent()) {
            TrackedPeer[] peers = torrent.get().getPeersWithCompleteFile();
            for (TrackedPeer peer : peers) {
                seedsMetadata.add(peer.clientMetadata);
            }
            return new DownloadOrder(torrent.get().fileMetadata, seedsMetadata.toArray(new ClientMetadata[0]));
        }
        return null;
    }

    /**
     * Returns {@link DownloadOrder} with single {@link ClientMetadata} of client that sent {@link PushRequest}
     * @param pushRequest {@link PushRequest} that has been received
     * @return {@link DownloadOrder}
     */
    public static DownloadOrder getDownloadOrder(TorrentContainer torrentContainer, PushRequest pushRequest) {
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
