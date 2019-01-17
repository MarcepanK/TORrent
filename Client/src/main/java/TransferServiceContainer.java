import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class TransferServiceContainer {

    private static final Logger logger = Logger.getLogger(TransferServiceContainer.class.getName());
    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

    private List<FileTransferService> serviceList;

    public TransferServiceContainer() {
        serviceList = Collections.synchronizedList(new LinkedList<>());
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            try {
                finalizeIfComplete();
            } catch (Exception e) {
                logger.severe(String.format("Failed to finalize service"));
            }
        }, 0, 2, TimeUnit.SECONDS);
    }

    public void add(FileTransferService service) {
        serviceList.add(service);
        service.run();
    }

    /**
     * Iterates over all running services and checks their state
     * if complete service is found finalize function is called
     * and service is removed from container
     */
    private void finalizeIfComplete() {
        Iterator iter = serviceList.iterator();
        while (iter.hasNext()) {
            FileTransferService service = (FileTransferService) iter.next();
            if (service instanceof FileDownloadService) {
                FileDownloadService downloadService = (FileDownloadService) service;
                if (downloadService.isComplete()) {
                    downloadService.closeServerSock();
                    iter.remove();
		    logger.info("removing download service");
                }
            } else if (service instanceof FileUploadService) {
                FileUploadService uploadService = (FileUploadService) service;
                if (uploadService.isComplete()) {
                    iter.remove();
		    logger.info("removing upload service");
                }
            }
        }
    }

    public Collection<FileTransferService> getActiveServices() {
        return serviceList;
    }
}
