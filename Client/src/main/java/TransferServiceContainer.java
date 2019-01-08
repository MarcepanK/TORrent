import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class TransferServiceContainer {

    private List<FileTransferService> serviceList;

    public TransferServiceContainer() {
        serviceList = Collections.synchronizedList(new LinkedList<>());
    }

    public void add(FileTransferService service) {
        serviceList.add(service);
        service.run();
    }

}
