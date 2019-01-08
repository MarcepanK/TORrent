import common.FileMetadata;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class FileDownloadService extends FileTransferService {

    private List<Piece> filePieces;

    public FileDownloadService(FileMetadata orderedFileMetadata) {
        super(orderedFileMetadata);
        this.filePieces = Collections.synchronizedList(new LinkedList<>());
    }


    @Override
    public void run() {

    }
}
