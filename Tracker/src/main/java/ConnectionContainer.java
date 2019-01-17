import common.Connection;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * This class is responsible for storing and manipulating
 * Connections to all connected clients
 */
public class ConnectionContainer {

    private static final Logger logger = Logger.getLogger(ConnectionContainer.class.getName());

    //<clientId, Connection>
    private Map<Integer, Connection> connections;

    public ConnectionContainer() {
        connections = new ConcurrentHashMap<>();
    }

    /**
     * Invoked when new client connects to tracker
     * Adds new entry to map
     *
     * @param clientId id of newly connected client
     * @param connection {@link Connection} to newly connected client
     */
    public void onClientConnected(int clientId, Connection connection) {
        connections.put(clientId, connection);
        logger.info(String.format("common.Connection has been established with id: %d\nCurrently stored connections: %d", clientId, connections.size()));
    }

    /**
     * Invoked when Client sends {@link request.Request} with Disconnect {@link request.RequestCode}
     * Closes connection with client and removes it from connections list
     *
     * @param clientId id of a client that wants to disconnect from tracker
     */
    public void onClientDisconnected(int clientId) {
        if (connections.containsKey(clientId)) {
            connections.get(clientId).close();
            connections.remove(clientId);
            logger.info(String.format("Connection to client with id: %d has beend closed and removed", clientId));
        }
    }

    public Optional<Connection> getConnectionById(int clientId) {
        return Optional.ofNullable(connections.get(clientId));
    }

    public Collection<Connection> getConnections() {
        return connections.values();
    }
}
