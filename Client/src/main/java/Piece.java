import java.io.Serializable;

public class Piece implements Serializable {

    public final int index;
    public final int totalPiecesCount;
    public final byte[] data;

    public Piece(int index, int totalPiecesCount, byte[] data) {
        this.index = index;
        this.totalPiecesCount = totalPiecesCount;
        this.data = data;
    }
}
