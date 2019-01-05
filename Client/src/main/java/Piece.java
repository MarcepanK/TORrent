import java.io.Serializable;

public class Piece implements Serializable {

    public final int index;
    public final int totalPiecesCount;
    public final String fileMd5sum;
    public final byte[] data;

    public Piece(int index, int totalPiecesCount, String fileMd5sum, byte[] data) {
        this.index = index;
        this.totalPiecesCount = totalPiecesCount;
        this.fileMd5sum = fileMd5sum;
        this.data = data;
    }
}
