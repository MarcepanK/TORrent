import common.Connection;
import order.UploadOrder;

import java.io.File;
import java.util.Optional;

public class FileTransferServiceFactory {

    public static FileUploadService getService(int myId, UploadOrder order, FileRepository fileRepository, Connection trackerConnection) {
        Optional<File> orderedFile = fileRepository.getFileByMd5sum(order.orderedFileMetadata.md5sum);
        if (orderedFile.isPresent()) {
            Piece[] filePieces = FileUtils.getOrderedFilePieces(orderedFile.get(), order.orderedFileMetadata,
                    order.filePartToSend, order.totalParts);
            return new FileUploadService(order.orderedFileMetadata, myId, order.leechId, trackerConnection, filePieces);
        } else {
            return null;
        }
    }
}
