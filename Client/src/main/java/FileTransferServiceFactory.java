import common.Connection;
import common.FileMetadata;
import order.DownloadOrder;
import order.SpecificPiecesUploadOrder;
import order.UploadOrder;

import java.io.File;
import java.util.Optional;

public class FileTransferServiceFactory {

    public static FileUploadService getService(int myId, UploadOrder order, FileRepository fileRepository,
                                               Connection trackerConnection) {
        Optional<File> orderedFile = fileRepository.getFileByMd5sum(order.orderedFileMetadata.md5sum);
        if (orderedFile.isPresent()) {
            Optional<FileMetadata> fileMetadata = fileRepository.getFileMetadata(orderedFile.get());
            Piece[] piecesToUpload = FileUtils.getOrderedPieces(orderedFile.get(), fileMetadata.get(), order.filePartToSend, order.totalParts);
            return new FileUploadService(order.orderedFileMetadata, trackerConnection, myId, order.leechId, piecesToUpload);
        } else {
            return null;
        }
    }

    public static FileDownloadService getService(int myId, DownloadOrder order, Connection trackerConnection) {
        return new FileDownloadService(trackerConnection, myId, order.orderedFileMetadata);
    }

    public static FileUploadService getService(int myId, SpecificPiecesUploadOrder order,
                                                       FileRepository fileRepository, Connection trackerConection) {
        Optional<File> orderedFile = fileRepository.getFileByMd5sum(order.orderedFileMetadata.md5sum);
        if (orderedFile.isPresent()) {
            Piece[] piecesToUpload = FileUtils.getSpecificPieces(orderedFile.get(), order.orderedFileMetadata,
                    order.piecesToSend, order.trailingBytesToSend, order.biggestOwnedPieceIndex).toArray(new Piece[0]);
            return new FileUploadService(order.orderedFileMetadata, trackerConection, myId, order.leechId, piecesToUpload);
        } else {
            return null;
        }
    }
}
