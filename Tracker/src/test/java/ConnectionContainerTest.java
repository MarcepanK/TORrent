import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class ConnectionContainerTest {

    @Mock
    Connection conA;
    @Mock
    Connection conB;

    private int conAId = 1;
    private int conBId = 2;

    private ConnectionContainer container;

    @Before
    public void setup() {
        container = new ConnectionContainer();
    }

    @Test
    public void ConnectionContainer_Creation() {
        assertTrue(container.getConnections().isEmpty());
    }

    @Test
    public void ConnectionContainer_OnClientConnected_SingleeClient() {
        container.onClientConnected(conAId, conA);

        assertTrue(container.getConnectionById(conAId).isPresent());
        assertFalse(container.getConnections().isEmpty());
    }

    @Test
    public void ConnectionContainer_OnClientDisconnect_SingleClient() {
        container.onClientConnected(conAId, conA);
        container.onClientDisconnected(conAId);

        assertFalse(container.getConnectionById(conAId).isPresent());
        assertTrue(container.getConnections().isEmpty());
    }

    @Test
    public void ConnectionContainer_OnClientConnected_MultipleClients() {
        container.onClientConnected(conAId, conA);
        container.onClientConnected(conBId, conB);

        container.onClientDisconnected(conAId);
        assertFalse(container.getConnectionById(conAId).isPresent());
        assertTrue(container.getConnectionById(conBId).isPresent());
        assertFalse(container.getConnections().isEmpty());

        container.onClientDisconnected(conBId);
        assertFalse(container.getConnectionById(conBId).isPresent());
        assertTrue(container.getConnections().isEmpty());
    }
}