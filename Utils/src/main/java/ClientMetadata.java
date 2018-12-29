import java.io.Serializable;
import java.net.InetAddress;
import java.net.InetSocketAddress;

public class ClientMetadata implements Serializable {

    public final int id;
    public final InetAddress address;

    public ClientMetadata(int id, InetAddress address) {
        this.id = id;
        this.address = address;
    }

    public ClientMetadata(ClientMetadata clientMetadata) {
        this.id = clientMetadata.id;
        this.address = clientMetadata.address;
    }
}
