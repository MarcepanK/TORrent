import common.ClientMetadata;
import common.FileMetadata;
import order.DownloadOrder;
import order.SpecificPiecesUploadOrder;
import order.UploadOrder;

import java.util.logging.Logger;

/**
 * <p>This class is responsible for validating and sending Orders to Clients</p>
 * <p>Used by {@link RequestProcessor}</p>
 */
public class OrderDispatcher {

    private static final Logger logger = Logger.getLogger(OrderDispatcher.class.getName());

    private ConnectionContainer connectionContainer;

    public OrderDispatcher(ConnectionContainer connectionContainer) {
        this.connectionContainer = connectionContainer;
    }

    /**
     * Validates orders and sends them to clients
     *
     * @param downloadOrder order that will be sent to leech
     * @param uploadOrders  orders that will be sent to seeds
     */
    public void dispatchOrders(DownloadOrder downloadOrder, UploadOrder[] uploadOrders) {
        if (ordersValid(uploadOrders) && orderValid(downloadOrder)) {
            connectionContainer.getConnectionById(uploadOrders[0].leechId).get().send(downloadOrder);
            for (int seedIdx = 0; seedIdx < downloadOrder.seeds.length; seedIdx++) {

                connectionContainer.getConnectionById(downloadOrder.seeds[seedIdx].id).get().send(uploadOrders[seedIdx]);
            }
        }
    }

    /**
     * Validates orders and sends them to clients
     *
     * @param downloadOrder order that will be sent to leech
     * @param uploadOrder   order that will be sent to seed
     */
    public void dispatchOrders(DownloadOrder downloadOrder, UploadOrder uploadOrder) {
        if (orderValid(downloadOrder) && orderValid(uploadOrder)) {
            connectionContainer.getConnectionById(uploadOrder.leechId).get().send(downloadOrder);
            connectionContainer.getConnectionById(downloadOrder.seeds[0].id).get().send(uploadOrder);
        }
    }

    public void dispatchOrders(DownloadOrder downloadOrder, SpecificPiecesUploadOrder uploadOrder) {
        if (orderValid(downloadOrder) && orderValid(uploadOrder)) {
            connectionContainer.getConnectionById(uploadOrder.leechId).get().send(downloadOrder);
            connectionContainer.getConnectionById(downloadOrder.seeds[0].id).get().send(uploadOrder);
        }
    }

    /**
     * Checks if orders are valid:
     * - file parts are correct
     * - number of file parts is equal to number of seeds
     * - all upload orders have the same leech
     * - all orders have the same {@link FileMetadata}
     * - leech is connected to tracker
     *
     * @param orders orders that will be sent to seeds
     * @return true if all orders meet requirements
     */
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
                logger.warning(String.format("wrong total parts | total parts: %d  | order count: %d",
                        currentOrder.totalParts, orderCount));
                return false;
            } else if (currentOrder.leechId != leechId) {
                logger.warning("wrong leech");
                return false;
            } else if (currentOrder.orderedFileMetadata != orderedFileMetadata) {
                logger.warning("wrong file metadata");
                return false;
            }
        }
        return connectionContainer.getConnectionById(leechId).isPresent();
    }

    /**
     * Checks if order is valid:
     * - number of parts is 1
     * - leech is connected to tracker
     *
     * @param order order that will be sent to seed
     * @return true if order meets all requirements
     */
    private boolean orderValid(UploadOrder order) {
        if (order.totalParts != 1 || order.filePartToSend != 1) {
            logger.warning("Upload order not valid | ");
            return false;
        }
        return connectionContainer.getConnectionById(order.leechId).isPresent();
    }

    /**
     * Checks if order is valid:
     * - all seeds are connected to tracker
     *
     * @param order order that will be sent to leech
     * @return true if order meets all requirements
     */
    private boolean orderValid(DownloadOrder order) {
        for (ClientMetadata seed : order.seeds) {
            if (!connectionContainer.getConnectionById(seed.id).isPresent()) {
                logger.warning("Download order not valid");
                return false;
            }
        }
        return true;
    }

    //TODO
    private boolean orderValid(SpecificPiecesUploadOrder order) {
        if (!connectionContainer.getConnectionById(order.leechId).isPresent()) {
            logger.warning("SpecificPiecesUploadOrder not valid");
            return false;
        }
        return true;
    }
}
