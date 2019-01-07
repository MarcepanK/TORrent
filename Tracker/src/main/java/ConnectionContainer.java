import common.ClientMetadata;
import common.Connection;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class ConnectionContainer {

    private static final Logger logger = Logger.getLogger(ConnectionContainer.class.getName());

    private Map<ClientMetadata, Connection> connections;

    public ConnectionContainer() {
        connections = new ConcurrentHashMap<>();
    }

    /**
     * <p>Invoked when new client connects to tracker</p>
     * <p>Adds {@link ClientMetadata} and {@link Connection} to newly connected</p>
     *
     * @param clientMetadata {@link ClientMetadata} of newly connected client
     * @param connection {@Connection} to newly connected client
     */
    public void onClientConnected(ClientMetadata clientMetadata, Connection connection) {
        connections.put(clientMetadata, connection);
        logger.info(String.format("common.Connection has been established with id: %d", clientMetadata.id));
    }

    /**
     * <p>Invoked when Client sends {@link request.Request} with Disconnect {@link request.RequestCode}</p>
     * <p>Closes connection with client and removes it from connections list</p>
     *
     * @param clientId id of a client that wants to disconnect from tracker
     */
    public void onClientDisconnected(int clientId) {
        connections.get(clientId).close();
        connections.remove(clientId);
        logger.info(String.format("Connection to client with id: %d has been closed and removed", clientId));
        if (connections.containsKey(clientId)) {
            connections.get(clientId).close();
            connections.remove(clientId);
            logger.info(String.format("Connection to client with id: %d has beend closed and removed", clientId));
        }
    }

    public Optional<Connection> getConnectionById(int clientId) {
        return Optional.ofNullable(connections.get(clientId));
    }

    public Optional<ClientMetadata> getClientMetadataByConnection(Connection connection) {
        for (Map.Entry<ClientMetadata, Connection> entry : connections.entrySet()) {
            if (entry.getValue() == connection) {
                return Optional.ofNullable(entry.getKey());
            }
        }
        return Optional.empty();
    }

    public Optional<ClientMetadata> getClientMetadataById(int clientId) {
        for (Map.Entry<ClientMetadata, Connection> entry : connections.entrySet()) {
            if(entry.getKey().id == clientId) {
                return Optional.of(entry.getKey());
            }
        }
        return Optional.empty();
    }

    public Collection<Connection> getConnections() {
        return connections.values();
    }
}
