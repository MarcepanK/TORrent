import common.Connection;
import order.*;


public class OrderProcessor {

    private int myId;
    private FileRepository fileRepository;
    private Connection trackerConnection;
    private TransferServiceContainer transferServiceContainer;

    public OrderProcessor(int myId, FileRepository fileRepository, Connection trackerConnection, TransferServiceContainer transferServiceContainer) {
        this.myId = myId;
        this.fileRepository = fileRepository;
        this.trackerConnection = trackerConnection;
        this.transferServiceContainer = transferServiceContainer;
    }

    public void processOrder(Order order) throws Exception {
        if (order instanceof UploadOrder) {
            handleUploadOrder((UploadOrder) order);
        } else if (order instanceof DownloadOrder) {
            handleDownloadOrder((DownloadOrder) order);
        }
    }

    private void handleUploadOrder(UploadOrder order) throws Exception {
        transferServiceContainer.add(FileTransferServiceFactory.getService(myId, order, fileRepository, trackerConnection));
    }

    private void handleDownloadOrder(DownloadOrder order) {
        transferServiceContainer.add(FileTransferServiceFactory.getService(myId, order, trackerConnection));
    }


}
