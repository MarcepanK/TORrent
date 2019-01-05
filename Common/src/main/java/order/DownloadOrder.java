package order;

import common.ClientMetadata;
import common.FileMetadata;

public class DownloadOrder extends Order {

    public final ClientMetadata[] seeds;

    public DownloadOrder(FileMetadata orderedFileMetadata, ClientMetadata[] seeds) {
        super(orderedFileMetadata);
        this.seeds = seeds;
    }
}
