import request.RequestFactory;
import request.*;

import java.util.logging.Logger;

public class CommandProcessor {

    private static final Logger logger = Logger.getLogger(CommandProcessor.class.getName());

    private int clientId;
    private Connection trackerConnection;
    private FileTransferServiceContainer fileTransferServiceContainer;

    public CommandProcessor(Connection trackerConnection, FileTransferServiceContainer fileTransferServiceContainer) {
        this.trackerConnection = trackerConnection;
        this.fileTransferServiceContainer = fileTransferServiceContainer;
    }

    private void processRequestCommand(String command) {
        SimpleRequest request = RequestFactory.getRequest(clientId, command);
        trackerConnection.send(request);
    }

    private void processListCommand(String listCommandArg) {
        if (listCommandArg.equals("transfers")) {
            for (FileTransferService service : fileTransferServiceContainer.getAllServices()) {
                System.out.println(service.toString());
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
            }
        }).start();
    }
}
