package order;

import common.FileMetadata;

public class UploadOrder extends Order {

    public final int leechId;
    public final int filePartToSend;
    public final int totalParts;

    public UploadOrder(FileMetadata orderedFile, int leechId, int filePartToSend, int totalParts) {
        super(orderedFile);
        this.leechId = leechId;
        this.filePartToSend = filePartToSend;
        this.totalParts = totalParts;
    }
}
