import common.Connection;
import common.FileMetadata;

public abstract class FileTransferService implements Runnable {

    public final FileMetadata transferredFileMetadata;

    protected Connection trackerConnection;

    public FileTransferService(FileMetadata transferredFileMetadata, Connection trackerConnection) {
        this.transferredFileMetadata = transferredFileMetadata;
        this.trackerConnection = trackerConnection;
    }

    protected abstract void finalize();
}
