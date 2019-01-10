import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TransferServiceContainer {

    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

    private List<FileTransferService> serviceList;

    public TransferServiceContainer() throws Exception {
        serviceList = Collections.synchronizedList(new LinkedList<>());
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            try {
                finalizeIfComplete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 2, TimeUnit.SECONDS);
    }

    public void add(FileTransferService service) {
        serviceList.add(service);
        service.run();
    }

    private void finalizeIfComplete() throws Exception {
        Iterator iter = serviceList.iterator();
        while (iter.hasNext()) {
            FileTransferService service = (FileTransferService) iter.next();
            if (service instanceof FileDownloadService) {
                if (((FileDownloadService) service).isComplete()) {
                    ((FileDownloadService) service).finalizeDownloadService();
                    iter.remove();
                }
            } else if (service instanceof FileUploadService) {
                if (((FileUploadService) service).isComplete()) {
                    iter.remove();
                }
            }
        }
    }
}
