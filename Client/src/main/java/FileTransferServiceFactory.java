import common.Connection;
import common.FileMetadata;
import order.DownloadOrder;
import order.UploadOrder;

import java.io.File;
import java.util.Optional;

public class FileTransferServiceFactory {

    public static FileUploadService getService(int myId, UploadOrder order, FileRepository fileRepository,
                                               Connection trackerConnection) throws Exception {
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
}
