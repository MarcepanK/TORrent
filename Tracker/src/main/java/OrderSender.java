import common.ClientMetadata;
import common.FileMetadata;
import order.DownloadOrder;
import order.UploadOrder;

import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * <p>This class is responsible for validating and sending Orders to Clients</p>
 * <p>Used by {@link RequestProcessor}</p>
 */
public class OrderSender {

    private static final Logger logger = Logger.getLogger(OrderSender.class.getName());

    private ConnectionContainer connectionContainer;

    public OrderSender(ConnectionContainer connectionContainer) {
        this.connectionContainer = connectionContainer;
    }

    public void sendOutOrders(DownloadOrder downloadOrder, ArrayList<UploadOrder> uploadOrders) {
        if (ordersValid(uploadOrders) && orderValid(downloadOrder)) {
           connectionContainer.getConnectionById(uploadOrders.get(0).leech.id).get().send(downloadOrder);
           for (int seedIdx = 0; seedIdx < downloadOrder.seeds.length; seedIdx++) {
               connectionContainer.getConnectionById(downloadOrder.seeds[seedIdx].id).get().send(uploadOrders.get(seedIdx));
           }
        }
    }

    public void sendOutOrders(DownloadOrder downloadOrder, UploadOrder uploadOrder) {
        if (orderValid(downloadOrder) && orderValid(uploadOrder)) {
            connectionContainer.getConnectionById(uploadOrder.leech.id).get().send(downloadOrder);
            connectionContainer.getConnectionById(downloadOrder.seeds[0].id).get().send(uploadOrder);
        }
    }

    private boolean ordersValid(ArrayList<UploadOrder> orders) {
        int orderCount = orders.size();
        ClientMetadata leech = orders.get(0).leech;
        FileMetadata orderedFileMetadata = orders.get(0).orderedFileMetadata;
        for (int orderNo = 0; orderNo < orderCount; orderCount++) {
            UploadOrder currentOrder = orders.get(orderNo);
            if (currentOrder.filePartToSend != orderNo + 1 ||
                    currentOrder.totalParts != orderCount ||
                    currentOrder.leech != leech ||
                    currentOrder.orderedFileMetadata != orderedFileMetadata) {
                logger.warning("UploadOrder not valid");
                return false;
            }
        }
        return connectionContainer.getConnectionById(leech.id).isPresent();
    }

    private boolean orderValid(UploadOrder order) {
        ClientMetadata leech = order.leech;
        if (order.totalParts != 0 || order.filePartToSend != 1) {
            logger.warning("Upload order not valid");
            return false;
        }
        return connectionContainer.getConnectionById(leech.id).isPresent();
    }

    private boolean orderValid(DownloadOrder order) {
        for (ClientMetadata seed : order.seeds) {
            if (!connectionContainer.getConnectionById(seed.id).isPresent()) {
                logger.warning("Download order not valid");
                return false;
            }
        }
        return true;
    }
}
