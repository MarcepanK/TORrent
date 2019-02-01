import common.ClientMetadata;
import common.Connection;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.net.InetSocketAddress;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doNothing;

@RunWith(MockitoJUnitRunner.class)
public class ConnectionContainerTest {

    @Mock private Connection conA = Mockito.mock(Connection.class);
    @Mock private Connection conB = Mockito.mock(Connection.class);

    private ClientMetadata clientA = new ClientMetadata(1, new InetSocketAddress(1));
    private ClientMetadata clientB = new ClientMetadata(2, new InetSocketAddress(2));
    private ConnectionContainer container;

    @Before
    public void setup() throws IOException {
        container = new ConnectionContainer();
    }

    @Test
    public void ConnectionContainer_Creation() {
        assertFalse(container.getConnectionById(clientA.id).isPresent());
        assertFalse(container.getConnectionById(clientB.id).isPresent());
        assertTrue(container.getConnections().isEmpty());
    }

    @Test
    public void ConnectionContainer_OnClientConnected_SingleClient() {
        container.onClientConnected(clientA.id, conA);

        assertFalse(container.getConnections().isEmpty());
        assertTrue(container.getConnectionById(clientA.id).isPresent());
        assertFalse(container.getConnections().isEmpty());
    }

    @Test
    public void ConnectionContainer_OnClientDisconnect_SingleClient() {
        container.onClientConnected(clientA.id, conA);
        container.onClientDisconnected(clientA.id);

        assertFalse(container.getConnectionById(clientA.id).isPresent());
        assertTrue(container.getConnections().isEmpty());
    }

    @Test
    public void ConnectionContainer_OnClientConnected_MultipleClients() {
        doNothing().when(conA).close();
        doNothing().when(conB).close();

        container.onClientConnected(clientA.id, conA);
        container.onClientConnected(clientB.id, conB);

        container.onClientDisconnected(clientA.id);
        assertFalse(container.getConnectionById(clientA.id).isPresent());
        assertTrue(container.getConnectionById(clientB.id).isPresent());
        assertFalse(container.getConnections().isEmpty());

        container.onClientDisconnected(clientB.id);
        assertFalse(container.getConnectionById(clientB.id).isPresent());
        assertTrue(container.getConnections().isEmpty());
    }
}