import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class ConnectionContainer {

    private static final Logger logger = Logger.getLogger(ConnectionContainer.class.getName());

    /**
     * key - client id
     * value - connection to the client
     */
    private Map<Integer, Connection> connections;

    public ConnectionContainer() {
        connections = new ConcurrentHashMap<>();
    }

    public void onClientConnected(int clientId, Connection connection) {
        connections.put(clientId, connection);
        logger.info(String.format("New common.Connection has been established with id: %d", clientId));
    }

    /**
     * Invoked when Client sends Disconnect request
     * Closes connection with client and removes it from connections list
     *
     * @param clientId id of a client that wants to disconnect from tracker
     */
    public void onClientDisconnected(int clientId) {
        Connection conn = connections.get(clientId);
        conn.close();
        connections.remove(clientId, conn);
        logger.info(String.format("common.Connection with id: %d has been closed", clientId));
    }

    public Optional<Connection> getConnectionById(int clientId) {
        return Optional.ofNullable(connections.get(clientId));
    }

    public Collection<Connection> getConnections() {
        return connections.values();
    }
}
