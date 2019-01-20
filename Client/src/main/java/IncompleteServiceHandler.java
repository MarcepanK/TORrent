import request.Request;
import request.RetryDownloadRequest;

import java.util.ArrayList;
import java.util.Collection;

public class IncompleteServiceHandler {

    public static void handleIncompleteDownloadService(FileDownloadService fileDownloadService) {
        FileUtils.storeOwnedPiecesInFile(fileDownloadService.getPieceBuffer(), fileDownloadService.getMyId());
        Collection<Integer> missingPiecesIndexes = getMissingPiecesIndexes(fileDownloadService.getPieceBuffer());
        int biggestPieceIndex = getBiggestPieceIndex(fileDownloadService.getPieceBuffer());
        int fileSize = (int)fileDownloadService.getTransferredFileMetadata().size;
        int trailingBytesCount = getTrailingBytesCount(missingPiecesIndexes.size(), biggestPieceIndex, fileSize);
        RetryDownloadRequest request = RequestFactory.getRequest(fileDownloadService.getMyId(),
                fileDownloadService.getTransferredFileMetadata(), missingPiecesIndexes,
                trailingBytesCount, biggestPieceIndex);
        FileUtils.storeOwnedPiecesInFile(fileDownloadService.getPieceBuffer(), fileDownloadService.getMyId());
        FileUtils.storeRetryDownloadRequestInFile(request);

//        if (isSizeCorrect(missingPiecesIndexes.size(), downloadedBytesCount,
//                (int) fileDownloadService.getTransferredFileMetadata().size)) {
//            RetryDownloadRequest request = RequestFactory.getRequest(fileDownloadService.getMyId(),
//                    fileDownloadService.transferredFileMetadata, missingPiecesIndexes, getTrailingBytesCount(), )
//            RetryDownloadRequest request = RequestFactory.getRequest(fileDownloadService.getMyId(),
//                    fileDownloadService.getTransferredFileMetadata(), missingPiecesIndexes, biggestPieceIndex);
//            FileUtils.storeRetryDownloadRequestInFile(request);
//        } else {
//            int trailingBytes = getTrailingBytesCount(missingPiecesIndexes.size(), downloadedBytesCount,
//                    (int) fileDownloadService.getTransferredFileMetadata().size, biggestPieceIndex);
//            RetryDownloadRequest request = RequestFactory.getRequest(fileDownloadService.getMyId(),
//                    fileDownloadService.getTransferredFileMetadata(), missingPiecesIndexes, trailingBytes, biggestPieceIndex);
//            FileUtils.storeRetryDownloadRequestInFile(request);
//        }
    }

    private static int getBiggestPieceIndex(Collection<Piece> pieces) {
        int max_index = 0;
        for (Piece piece : pieces) {
            if (piece.index > max_index) {
                max_index = piece.index;
            }
        }
        return max_index;
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

    private static int getTrailingBytesCount(int missingPiecesCount, int downloadedBytesCount, int fileSize) {
        return fileSize - missingPiecesCount * Piece.DEFAULT_PIECE_DATA_LEN + downloadedBytesCount;
    }
}
