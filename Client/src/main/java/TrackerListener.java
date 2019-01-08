import common.Connection;
import common.FileMetadata;

public class TrackerListener implements Runnable {

    private Connection trackerConnection;

    public TrackerListener(Connection trackerConnection) {
        this.trackerConnection = trackerConnection;
    }

    @Override
    public void run() {
        while(true) {
            Object received = trackerConnection.receive();
            if (received instanceof FileMetadata) {
                FileMetadata metadata = (FileMetadata) received;
                System.out.println(metadata);
            } else {
                System.out.println(received);
            }
        }
    }

}
