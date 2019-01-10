import common.ClientHandshake;
import common.ClientMetadata;
import common.Connection;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

public class IncomingConnectionsHandler implements Runnable {

    private static final Logger logger = Logger.getLogger(IncomingConnectionsHandler.class.getName());

    private ServerSocket serverSocket;
    private ConnectionContainer connectionContainer;
    private TorrentContainer torrentContainer;
    private IncomingRequestsHandler incomingRequestsHandler;

    public IncomingConnectionsHandler(ConnectionContainer connectionContainer, TorrentContainer torrentContainer,
                                      IncomingRequestsHandler incomingRequestsHandler) {
        this.connectionContainer = connectionContainer;
        this.torrentContainer = torrentContainer;
        this.incomingRequestsHandler = incomingRequestsHandler;
        createServerSocket();
    }

    private void createServerSocket() {
        try {
            serverSocket = new ServerSocket(Tracker.TRACKER_PORT);
        }catch (Exception e) {
            logger.severe("Unable to create Tracker\n Quitiing");
            System.exit(1);
        }
    }

    /**
     * Invoked when client connects to serverSocket.
     * Awaits for client to send a {@link ClientHandshake}
     * @param socket
     */
    private void handleNewConnection(Socket socket) {
        new Thread(() -> {
            logger.info("Handling new connection");
            Connection newConnection = new Connection(socket);
            Object received = newConnection.receive();
            if(received instanceof ClientHandshake) {
                ClientHandshake handshake = (ClientHandshake) received;
                InetSocketAddress sockAddress = (InetSocketAddress)socket.getRemoteSocketAddress();
                ClientMetadata clientMetadata = new ClientMetadata(handshake.id, sockAddress);
                torrentContainer.onClientConnected(clientMetadata, handshake.ownedFilesMetadata);
                connectionContainer.onClientConnected(clientMetadata.id, newConnection);
                logger.info(String.format("Received handshake from: id: %d | address: %s | port: %d",
                        clientMetadata.id, clientMetadata.address.getAddress(), clientMetadata.address.getPort()));
                connectionContainer.getConnectionById(clientMetadata.id).ifPresent(connection-> connection.send("Hello"));
                incomingRequestsHandler.addNewRequestCollectorThread(newConnection);
            }
        }).start();
    }

    @Override
    public void run() {
        try {
            while(true) {
                handleNewConnection(serverSocket.accept());
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            logger.warning("Failed while running serverSocket");
            e.printStackTrace();
        }
    }
}
