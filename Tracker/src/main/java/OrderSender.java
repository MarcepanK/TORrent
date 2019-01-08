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

    public void sendOutOrders(DownloadOrder downloadOrder, UploadOrder[] uploadOrders) {
        if (ordersValid(uploadOrders) && orderValid(downloadOrder)) {
           connectionContainer.getConnectionById(uploadOrders[0].leechId).get().send(downloadOrder);
           for (int seedIdx = 0; seedIdx < downloadOrder.seeds.length; seedIdx++) {

               connectionContainer.getConnectionById(downloadOrder.seeds[seedIdx].id).get().send(uploadOrders[seedIdx]);
           }
        }
    }

    public void sendOutOrders(DownloadOrder downloadOrder, UploadOrder uploadOrder) {
        if (orderValid(downloadOrder) && orderValid(uploadOrder)) {
            connectionContainer.getConnectionById(uploadOrder.leechId).get().send(downloadOrder);
            connectionContainer.getConnectionById(downloadOrder.seeds[0].id).get().send(uploadOrder);
        }
    }

    private boolean ordersValid(UploadOrder[] orders) {
        int orderCount = orders.length;
        int leechId = orders[0].leechId;
        FileMetadata orderedFileMetadata = orders[0].orderedFileMetadata;
        for (int orderNo = 0; orderNo < orderCount; orderNo++) {
            UploadOrder currentOrder = orders[orderNo];
            if (currentOrder.filePartToSend != orderNo + 1) {
                logger.warning("wring file part to send");
                return false;
            } else if (currentOrder.totalParts != orderCount) {
                logger.warning(String.format("wrong total parts | total parts: %d  | order count: %d", currentOrder.totalParts, orderCount));
                return false;
            } else if (currentOrder.leechId != leechId) {
                logger.warning("wrong leech");
                return false;
            } else if (currentOrder.orderedFileMetadata != orderedFileMetadata) {
                logger.warning("wrong file metadata");
                return false;
            }
//            if (currentOrder.filePartToSend != orderNo + 1 ||
//                    currentOrder.totalParts != orderCount ||
//                    currentOrder.leech != leech ||
//                    currentOrder.orderedFileMetadata != orderedFileMetadata) {
//                logger.warning("UploadOrder not valid");
//                return false;
//            }
        }
        return connectionContainer.getConnectionById(leechId).isPresent();
    }

    private boolean orderValid(UploadOrder order) {
        if (order.totalParts != 1 || order.filePartToSend != 1) {
            logger.warning("Upload order not valid | ");
            return false;
        }
        return connectionContainer.getConnectionById(order.leechId).isPresent();
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
