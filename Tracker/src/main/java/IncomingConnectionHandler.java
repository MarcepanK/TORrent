import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

public class IncomingConnectionHandler implements Runnable {

    private static final Logger logger = Logger.getLogger(IncomingConnectionHandler.class.getName());

    private ServerSocket serverSocket;
    private ConnectionContainer connectionContainer;
    private TorrentContainer torrentContainer;

    public IncomingConnectionHandler(ConnectionContainer connectionContainer,
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

    private void handleNewConnection(Socket socket) {
        Connection newConnection = new Connection(socket);
        Object received = newConnection.receive();
        if(received instanceof ClientHandshake) {
            ClientHandshake handshake = (ClientHandshake) received;
            InetSocketAddress sockAddress = (InetSocketAddress)socket.getRemoteSocketAddress();
            ClientMetadata clientMetadata = new ClientMetadata(handshake.id, sockAddress);
            logger.info(String.format("Received handshake from: id: %d | address: %s | port: %d",
                    clientMetadata.id, clientMetadata.address.getAddress(), clientMetadata.id));
            torrentContainer.onClientConnected(clientMetadata, handshake.ownedFiles);
            connectionContainer.onClientConnected(clientMetadata.id, newConnection);
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
