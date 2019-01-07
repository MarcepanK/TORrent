import common.FileMetadata;

import java.io.Serializable;

public class Piece implements Serializable {

    public final FileMetadata fileMetadata;
    public final int index;
    public final int totalPiecesCount;
    public final byte[] data;

    public Piece(FileMetadata fileMetadata, int index, int totalPiecesCount, String fileMd5sum, byte[] data) {
        this.fileMetadata = fileMetadata;
        this.index = index;
        this.totalPiecesCount = totalPiecesCount;
        this.data = data;
    }
}
