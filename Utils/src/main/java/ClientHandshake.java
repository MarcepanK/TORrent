import java.io.File;
import java.io.Serializable;

public class ClientHandshake implements Serializable {

    public final int id;
    public final FileMetadata[] ownedFiles;

    public ClientHandshake(int id, File[] files) {
        this.id = id;
        ownedFiles = new FileMetadata[files.length];
        for(int i=0; i<files.length; i++) {
            ownedFiles[i] = new FileMetadata(files[i]);
        }
    }
}