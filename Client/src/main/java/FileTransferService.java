import common.FileMetadata;

public abstract class FileTransferService implements Runnable {

    public final FileMetadata transferredFileMetadata;

    public FileTransferService(FileMetadata transferredFileMetadata) {
        this.transferredFileMetadata = transferredFileMetadata;
    }
}
