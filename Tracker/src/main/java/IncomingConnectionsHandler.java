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

    public IncomingConnectionsHandler(ConnectionContainer connectionContainer,
                                      TorrentContainer torrentContainer) {
        try {
            serverSocket = new ServerSocket(Tracker.TRACKER_PORT);
        } catch (Exception e) {
            logger.severe("Unable to create Sever Socket");
            e.printStackTrace();
        }
        this.connectionContainer = connectionContainer;
        this.torrentContainer = torrentContainer;
    }

    /**
     * Invoked when client connects to serverSocket.
     * Awaits for client to send a Handshake
     * @see ClientHandshake
     *
     * @param socket
     */
    private void handleNewConnection(Socket socket) {
        Connection newConnection = new Connection(socket);
        Object received = newConnection.receive();
        if(received instanceof ClientHandshake) {
            ClientHandshake handshake = (ClientHandshake) received;
            InetSocketAddress sockAddress = (InetSocketAddress)socket.getRemoteSocketAddress();
            ClientMetadata clientMetadata = new ClientMetadata(handshake.id, sockAddress);
            torrentContainer.onClientConnected(clientMetadata, handshake.ownedFiles);
            connectionContainer.onClientConnected(clientMetadata, newConnection);
            logger.info(String.format("Received handshake from: id: %d | address: %s | port: %d",
                    clientMetadata.id, clientMetadata.address.getAddress(), clientMetadata.id));
        }
    }

    @Override
    public void run() {
        try {
            handleNewConnection(serverSocket.accept());
        } catch (Exception e) {
            logger.warning("Failed while running serverSocket");
            e.printStackTrace();
        }
    }
}
