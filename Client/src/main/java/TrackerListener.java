import common.Connection;
import common.SerializedFileList;
import order.Order;

import java.util.logging.Logger;

public class TrackerListener implements Runnable {

    private static final Logger logger = Logger.getLogger(TrackerListener.class.getName());

    private Connection trackerConnection;
    private OrderProcessor orderProcessor;

    public TrackerListener(Connection trackerConnection, OrderProcessor orderProcessor) {
        this.trackerConnection = trackerConnection;
        this.orderProcessor = orderProcessor;
    }

    @Override
    public void run() {
        while (true) {
            Object received = trackerConnection.receive();
            logger.info(String.format("Received data from tracker: %s", received.toString()));
            if (received instanceof SerializedFileList) {
                SerializedFileList fileList = (SerializedFileList) received;
                fileList.print();
            } else if (received instanceof Order) {
                try {
                    orderProcessor.processOrder((Order) received);
                } catch (Exception e) {
                    logger.warning("failed to process order");
                }
            } else {
                System.out.println(received);
            }
        }
    }
}
