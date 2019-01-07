import common.Connection;

public abstract class FileTransferService {
    Connection connection;

    public FileTransferService(Connection connection) {
        this.connection = connection;
    }
}
