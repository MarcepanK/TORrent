package common;

import java.io.File;
import java.io.Serializable;

public class ClientHandshake implements Serializable {

    public final int id;
    public final FileMetadata[] ownedFilesMetadata;

    public ClientHandshake(int id, File[] files) {
        this.id = id;
        ownedFilesMetadata = new FileMetadata[files.length];
        for(int i=0; i<files.length; i++) {
            ownedFilesMetadata[i] = new FileMetadata(files[i]);
        }
    }

    public ClientHandshake(int id, FileMetadata[] ownedFilesMetadata) {
        this.id = id;
        this.ownedFilesMetadata = ownedFilesMetadata;
    }
}
