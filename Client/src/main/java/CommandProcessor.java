import common.Connection;
import request.*;

import java.util.logging.Logger;

public class CommandProcessor {

    private static final Logger logger = Logger.getLogger(CommandProcessor.class.getName());

    private int clientId;
    private Connection trackerConnection;
    private RequestFactory requestFactory;

    public CommandProcessor(int clientId, Connection trackerConnection) {
        this.clientId = clientId;
        this.trackerConnection = trackerConnection;
        requestFactory = new RequestFactory(clientId);
    }

    private void processRequestCommand(String requestArgs) {
        Request request = requestFactory.getRequest(requestArgs);
        trackerConnection.send(request);
    }

    private void processListCommand(String listCommandArg) {
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
