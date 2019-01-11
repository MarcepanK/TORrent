import common.Connection;
import request.Request;

import java.util.List;

public class PieceCollectorThread extends Thread {

    private List<Piece> pieces;
    private Connection seedConnection;
    private boolean running = true;

    public PieceCollectorThread(List<Piece> pieces, Connection seedConnection) {
        this.pieces = pieces;
        this.seedConnection = seedConnection;
        System.out.println("Piece collector thread created");
    }

    @Override
    public void run() {
        seedConnection.send("start");
        while(running) {
            Object received = seedConnection.receive();
            if (received instanceof Piece) {
                pieces.add((Piece) received);
            } else if (received instanceof Request) {
                running = false;
                break;
            }
        }
    }

    public boolean isRunning() {
        return running;
    }
}
