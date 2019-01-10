import common.Connection;
import order.*;


public class OrderProcessor {

    private int myId;
    private FileRepository fileRepository;
    Connection trackerConnection;
    private TransferServiceContainer transferServiceContainer;

    public OrderProcessor(int myId) {

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
        transferServiceContainer.add(FileTransferServiceFactory.getService(myId, order, fileRepository, trackerConnection));
    }


}
