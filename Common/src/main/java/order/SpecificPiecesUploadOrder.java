package order;

import common.FileMetadata;

import java.util.Collection;

public class SpecificPiecesUploadOrder extends Order {

    public final int leechId;
    public Collection<Integer> piecesToSend;
    public final int trailingBytesToSend;

    public SpecificPiecesUploadOrder(FileMetadata orderedFileMetadata, int leechId,
                                     Collection<Integer> piecesToSend, int trailingBytesToSend) {
        super(orderedFileMetadata);
        this.leechId = leechId;
        this.piecesToSend = piecesToSend;
        this.trailingBytesToSend = trailingBytesToSend;
    }
}
