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
    }

    public void onClientDisconnected(int clientId) {
        Connection conn = connections.get(clientId);
        conn.close();
        connections.remove(clientId, conn);
    }
}
