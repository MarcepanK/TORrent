package order;

import common.ClientMetadata;
import common.FileMetadata;

public class UploadOrder extends Order {

    public final ClientMetadata leech;
    public final int filePartToSend;
    public final int totalParts;

    public UploadOrder(FileMetadata orderedFile, ClientMetadata leech, int filePartToSend, int totalParts) {
        super(orderedFile);
        this.leech = leech;
        this.filePartToSend = filePartToSend;
        this.totalParts = totalParts;
    }
}
