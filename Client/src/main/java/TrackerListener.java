import common.Connection;
import common.FileMetadata;
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
        while(true) {
            Object received = trackerConnection.receive();
            logger.info(String.format("Received data from tracker: %s", received.toString()));
            if (received instanceof FileMetadata) {
                FileMetadata metadata = (FileMetadata) received;
                System.out.println(metadata);
            } else if (received instanceof Order) {
                try {
                    orderProcessor.processOrder((Order)received);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println(received);
            }
        }
    }
}
