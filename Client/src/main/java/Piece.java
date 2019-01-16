import common.FileMetadata;

import java.io.Serializable;

public class Piece implements Serializable {

    public static final int DEFAULT_PIECE_DATA_LEN = 262144;

    public final FileMetadata fileMetadata;
    public final int index;
    public final byte[] data;

    public Piece(FileMetadata fileMetadata, int index, byte[] data) {
        this.fileMetadata = fileMetadata;
        this.index = index;
        this.data = data;
    }

    @Override
    public String toString() {
        return index + " | bytes: " + data.length;
    }
}
