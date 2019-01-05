import common.ClientMetadata;

import java.util.logging.Logger;

public class TrackedPeer {

    private static final Logger logger = Logger.getLogger(TrackedPeer.class.getName());

    public final ClientMetadata clientMetadata;
    private long downloaded;
    private long uploaded;
    private long left;

    public TrackedPeer(ClientMetadata clientMetadata, long downloaded, long uploaded, long left) {
        this.clientMetadata = clientMetadata;
        this.downloaded = downloaded;
        this.uploaded = uploaded;
        this.left = left;
    }

    public TrackedPeer(ClientMetadata clientMetadata) {
        this.clientMetadata = clientMetadata;
        downloaded = 0;
        uploaded = 0;
        left = 0;
    }

    public void update(long downloaded, long uploaded) {
        this.downloaded += downloaded;
        left -= downloaded;
        this.uploaded += uploaded;
        if (left < 0) {
            logger.warning(String.format("left < 0. Inspect on id: %d", clientMetadata.id));
        }
    }

    public long getDownloaded() {
        return downloaded;
    }

    public long getUploaded() {
        return uploaded;
    }

    public long getLeft() {
        return left;
    }
}
