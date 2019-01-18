import common.Connection;
import common.FileMetadata;
import request.*;

import java.util.Collection;
import java.util.logging.Logger;

public class CommandProcessor {

    private static final Logger logger = Logger.getLogger(CommandProcessor.class.getName());

    private int clientId;
    private Connection trackerConnection;
    private FileRepository fileRepository;
    private TransferServiceContainer transferServiceContainer;

    public CommandProcessor(int clientId, Connection trackerConnection, FileRepository fileRepository, TransferServiceContainer transferServiceContainer) {
        this.clientId = clientId;
        this.trackerConnection = trackerConnection;
        this.fileRepository = fileRepository;
        this.transferServiceContainer = transferServiceContainer;
    }

    /**
     * Generates {@link Request} on given args and sends it to tracker
     *
     * @param requestArgs arguments that user typed into console
     */
    private void processRequestCommand(String requestArgs) {
        Request request = RequestFactory.getRequest(clientId, requestArgs);
        if (request.requestCode != RequestCode.UNKNOWN) {
            trackerConnection.send(request);
            logger.info(String.format("Request with code %s has been sent to tracker", request.requestCode.toString()));
            if (request.requestCode == RequestCode.DISCONNECT) {
                System.exit(1);
            }
        } else {
            System.out.println("bad command");
        }
    }

    /**
     * prints data depending on arguments
     *
     * @param listCommandArg
     */
    private void processListCommand(String listCommandArg) {
        if (listCommandArg.equals("files")) {
            fileRepository.update();
            FileMetadata[] ownedFiles = fileRepository.getAllFilesMetadata();
            for (FileMetadata metadata : ownedFiles) {
                System.out.println(metadata);
            }
        } else if (listCommandArg.equals("transfers")) {
            Collection<FileTransferService> activeServices = transferServiceContainer.getActiveServices();
            for (FileTransferService transferService : activeServices) {
                System.out.println(transferService);
            }
        }
    }

    //FIXME
    //implement checking with regex instead of startsWith
    public void processCommand(String command) {
        new Thread(() -> {
            if (command.toLowerCase().startsWith("request")) {
                processRequestCommand(command.substring("request".length()+1));
            } else if (command.toLowerCase().startsWith("list")) {
                processListCommand(command.substring("list".length()+1));
            } else {
                System.out.println("bad command");
            }
        }).start();
    }
}
