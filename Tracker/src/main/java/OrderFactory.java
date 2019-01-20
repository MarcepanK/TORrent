import common.ClientMetadata;
import order.DownloadOrder;
import order.SpecificPiecesUploadOrder;
import order.UploadOrder;
import request.PullRequest;
import request.PushRequest;
import request.RetryDownloadRequest;

import java.util.ArrayList;
import java.util.Optional;

public class OrderFactory {

    /**
     * Invoked when tracker receives PullRequest and has to generate
     * UploadOrders that will be sent to seeds
     * <p>
     * Searches for all {@link TrackedPeer} that own requested file
     * and returns Array of {@link UploadOrder} that will be sent
     * to those peers
     *
     * @param request {@link PullRequest} request that has been received
     * @return {@link ArrayList<UploadOrder>}
     */
    public static UploadOrder[] getUploadOrders(TorrentContainer torrentContainer, PullRequest request) {
        Optional<TrackedTorrent> torrent = torrentContainer.getTrackedTorrentByFileName(request.fileName);
        if (torrent.isPresent()) {
            TrackedPeer[] seeds = torrent.get().getPeersWithCompleteFile();
            UploadOrder[] uploadOrders = new UploadOrder[seeds.length];
            System.out.println("seeds: " + seeds.length);
            for (int i = 0; i < seeds.length; i++) {
                uploadOrders[i] = new UploadOrder(torrent.get().fileMetadata, request.requesterId, i + 1, seeds.length);
            }
            return uploadOrders;
        }
        return null;
    }

    /**
     * Invoked when tracker receives PushRequest and has to generate
     * Upload Order that will be sent to seed
     * <p>
     * Returns {@link UploadOrder} to client that sent request
     *
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
     * Invoked when tracker receives PullRequest and has to genereate downloadOrdere
     * that will be sent to leech
     * <p>
     * Returns {@link DownloadOrder} to client that sent request with Pull {@link request.RequestCode}
     * Searches for all peers that are capable of sending requested file and places their {@link ClientMetadata}
     * in {@link DownloadOrder}
     *
     * @param request {@link PullRequest} that has been received
     * @return {@link DownloadOrder}
     */
    public static DownloadOrder getDownloadOrder(TorrentContainer torrentContainer, PullRequest request) {
        ArrayList<ClientMetadata> seedsMetadata = new ArrayList<>();
        Optional<TrackedTorrent> torrent = torrentContainer.getTrackedTorrentByFileName(request.fileName);
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
     * Invoked when tracker receives RetryDownloadRequest and has to generate download order
     * that will be sent to leech
     *
     * @param torrentContainer
     * @param request
     * @return
     */
    public static DownloadOrder getDownloadOrder(TorrentContainer torrentContainer, RetryDownloadRequest request) {
        Optional<TrackedTorrent> torrent = torrentContainer.getTrackedTorrentByFileName(request.transferredFileMetadata.name);
        if (torrent.isPresent()) {
            TrackedPeer[] peers = torrent.get().getPeersWithCompleteFile();
            ClientMetadata[] seeds = new ClientMetadata[1];
            seeds[0] = peers[0].clientMetadata;
            return new DownloadOrder(torrent.get().fileMetadata, seeds);
        }
        return null;
    }

    /**
     * Invoked when tracker receives PushRequest and has to generate Download Order
     * that will be sent to leech
     * <p>
     * Returns {@link DownloadOrder} with single {@link ClientMetadata} of client that sent {@link PushRequest}
     *
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

    public static SpecificPiecesUploadOrder getSpecificPiecesUploadOrder(TorrentContainer torrentContainer,
                                                                         RetryDownloadRequest request) {
        Optional<TrackedTorrent> torrent = torrentContainer.getTrackedTorrentByFileName(request.transferredFileMetadata.name);
        if (torrent.isPresent()) {
            return new SpecificPiecesUploadOrder(request.transferredFileMetadata, request.requesterId,
                    request.missingPiecesIndexes, request.missingBytesCount, request.biggestPieceIndex);
        }
        return null;
    }
}
