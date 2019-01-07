import common.ClientMetadata;
import common.Connection;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

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
        container.onClientConnected(clientA, conA);
        when(container.getConnectionById(clientA.id)).thenReturn(Optional.of(conA));
        assertFalse(container.getConnections().isEmpty());
        assertTrue(container.getConnectionById(clientA.id).isPresent());
        assertFalse(container.getConnections().isEmpty());
    }

    @Test
    public void ConnectionContainer_OnClientDisconnect_SingleClient() {
        container.onClientConnected(clientA, conA);
        container.onClientDisconnected(clientA.id);

        assertFalse(container.getConnectionById(clientA.id).isPresent());
        assertTrue(container.getConnections().isEmpty());
    }

    @Test
    public void ConnectionContainer_OnClientConnected_MultipleClients() {
        doNothing().when(conA).close();
        doNothing().when(conB).close();

        container.onClientConnected(clientA, conA);
        container.onClientConnected(clientB, conB);

        container.onClientDisconnected(clientA.id);
        assertFalse(container.getConnectionById(clientA.id).isPresent());
        assertTrue(container.getConnectionById(clientB.id).isPresent());
        assertFalse(container.getConnections().isEmpty());

        container.onClientDisconnected(clientB.id);
        assertFalse(container.getConnectionById(clientB.id).isPresent());
        assertTrue(container.getConnections().isEmpty());
    }
}