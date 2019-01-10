import common.Connection;

import java.util.ArrayList;
import java.util.List;

public class PieceCollectorThread extends Thread {

    private List<Piece> pieces;
    private Connection seedConnection;
    private boolean running;

    public PieceCollectorThread(List<Piece> pieces, Connection seedConnection) {
        this.pieces = pieces;
        this.seedConnection = seedConnection;
    }

    @Override
    public void run() {
        while(running) {
            if (seedConnection.getSocket().isClosed()) {
                Object received = seedConnection.receive();
                if (received instanceof Piece) {
                    pieces.add((Piece) received);
                }
            } else {
                running = false;
            }
        }
    }

    public boolean isRunning() {
        return running;
    }
}
