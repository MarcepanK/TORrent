import java.util.Collection;
import java.util.List;

public class FileTransferServiceContainer {

    private Collection<FileTransferService> fileTransferServices;


    public Collection<FileTransferService> getAllServices() {
        return fileTransferServices;
    }

}
