package order;

import common.FileMetadata;

import java.util.Collection;

public class SpecificPiecesUploadOrder extends Order {

    public final int leechId;
    public Collection<Integer> piecesToSend;
    public final int trailingBytesToSend;
    public final int biggestOwnedPieceIndex;

    public SpecificPiecesUploadOrder(FileMetadata orderedFileMetadata, int leechId, Collection<Integer> piecesToSend,
                                     int trailingBytesToSend, int biggestOwnedPieceIndex) {
        super(orderedFileMetadata);
        this.leechId = leechId;
        this.piecesToSend = piecesToSend;
        this.trailingBytesToSend = trailingBytesToSend;
        this.biggestOwnedPieceIndex = biggestOwnedPieceIndex;
    }
}
