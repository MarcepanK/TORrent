import request.RetryDownloadRequest;

import java.util.ArrayList;
import java.util.Collection;

public class IncompleteServiceHandler {

    public static void handleIncompleteDownloadService(FileDownloadService fileDownloadService) {
        Collection<Integer> missingPiecesIndexes = getMissingPiecesIndexes(fileDownloadService.getPieceBuffer());
        int downloadedBytesCount = getDownloadedByteCount(fileDownloadService.getPieceBuffer());
        if (isSizeCorrect(missingPiecesIndexes.size(), downloadedBytesCount,
                (int)fileDownloadService.getTransferredFileMetadata().size)) {
            RetryDownloadRequest request = RequestFactory.getRequest(fileDownloadService.getMyId(),
                    fileDownloadService.getTransferredFileMetadata(), missingPiecesIndexes, 0);
            FileUtils.storeRetryDownloadRequestInFile(request);
        } else {
            int leftByteCount = getLeftByteCount(missingPiecesIndexes.size(), downloadedBytesCount,
                    (int)fileDownloadService.getTransferredFileMetadata().size);
            RetryDownloadRequest request = RequestFactory.getRequest(fileDownloadService.getMyId(),
                    fileDownloadService.getTransferredFileMetadata(), missingPiecesIndexes, leftByteCount);
            FileUtils.storeRetryDownloadRequestInFile(request);
        }
    }

    private static Collection<Integer> getMissingPiecesIndexes(Collection<Piece> pieces) {
        ArrayList<Integer> missingPieces = new ArrayList<>();
        int idx = 0;
        for (Piece piece : pieces) {
            if (piece.index != idx) {
                missingPieces.add(idx);
                idx++;
            }
        }
        return missingPieces;
    }

    private static boolean isSizeCorrect(int missingPiecesCount, int downloadedBytesCount, int fileSize) {
        return missingPiecesCount * Piece.DEFAULT_PIECE_DATA_LEN + downloadedBytesCount >= fileSize;
    }

    private static int getDownloadedByteCount(Collection<Piece> pieces) {
        int downloadedBytes = 0;
        for (Piece piece : pieces) {
            downloadedBytes += piece.data.length;
        }
        return downloadedBytes;
    }

    private static int getLeftByteCount(int missingPiecesCount, int downloadedBytesCount, int fileSize) {
        return fileSize - missingPiecesCount * Piece.DEFAULT_PIECE_DATA_LEN + downloadedBytesCount;
    }
}
