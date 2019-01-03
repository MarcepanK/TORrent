import java.io.Serializable;
import java.net.InetSocketAddress;

public class ClientMetadata implements Serializable {

    public final int id;
    public final InetSocketAddress address;

    public ClientMetadata(int id, InetSocketAddress address) {
        this.id = id;
        this.address = address;
    }

    public ClientMetadata(ClientMetadata clientMetadata) {
        this.id = clientMetadata.id;
        this.address = clientMetadata.address;
    }
}
