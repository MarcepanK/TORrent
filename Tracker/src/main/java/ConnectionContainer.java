import common.Connection;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;

public class ConnectionContainer {

    private static final Logger logger = Logger.getLogger(ConnectionContainer.class.getName());

    private ConcurrentMap<Integer, Connection> connections;

    public ConnectionContainer() {
        connections = new ConcurrentHashMap<>();
    }

    public void onClientConnected(int clientId, Connection connection) {
        connections.put(clientId, connection);
        logger.info(String.format("New common.Connection has been established with id: %d", clientId));
    }

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
