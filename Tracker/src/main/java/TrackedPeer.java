public class TrackedPeer extends ClientMetadata {

    private long downloaded;
    private long uploaded;
    private long left;

    public TrackedPeer(ClientMetadata clientMetadata, long downloaded, long uploaded, long left) {
        super(clientMetadata);
        this.downloaded = downloaded;
        this.uploaded = uploaded;
        this.left = left;
    }

    public TrackedPeer(ClientMetadata clientMetadata) {
        super(clientMetadata);
        downloaded = 0;
        uploaded = 0;
        left = 0;
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
