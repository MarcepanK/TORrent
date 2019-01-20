package request;

import common.FileMetadata;

import java.util.Collection;

/**
 * This class is responsible for storing data about incomplete DownloadTransferService
 *
 * can be sent to tracker to try to renew service
 * can be stored in file in case client exits program
 *
 * DEFAULT_PIECES_SIZE -> amount of bytes stored in one Piece
 *
 * Scenario 1
 * Collection missingPiecesIndexes can be empty
 * This might happen if all indexes are correct
 * but client received less bytes than requested file has
 *
 * Scenario 2
 * Param missingBytesCount might be 0
 * This might happen if size of all pieces that
 * have been received + (missingPiecesCount * DEFAULT_PIECE_SIZE) == fileSize
 *
 * Scenario 3
 * If param missingBytesCount is not 0 and missingPiecesIndexes is not empty
 * This might happen if some pieces are missing and
 * receivedBytes + (missingPiecesCount * DEFAULT_PIECE_SIZE) != file_size
 */
public class RetryDownloadRequest extends Request {

    public final FileMetadata transferredFileMetadata;
    public final Collection<Integer> missingPiecesIndexes;
    public final int missingBytesCount;

    public RetryDownloadRequest(int requesterId, RequestCode requestCode, FileMetadata transferredFileMetadata,
                                Collection<Integer> missingPiecesIndexes, int missingBytesCount) {
        super(requesterId, requestCode);
        this.transferredFileMetadata = transferredFileMetadata;
        this.missingPiecesIndexes = missingPiecesIndexes;
        this.missingBytesCount = missingBytesCount;
    }
}
