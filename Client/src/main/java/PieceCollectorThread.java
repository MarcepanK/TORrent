import common.Connection;
import request.Request;

import java.util.Collection;

public class PieceCollectorThread extends Thread {

    private Collection<Piece> pieces;
    private Connection seedConnection;
    private boolean running = true;

    public PieceCollectorThread(Collection<Piece> pieces, Connection seedConnection) {
        this.pieces = pieces;
        this.seedConnection = seedConnection;
    }

    @Override
    public void run() {
        seedConnection.send("start");
        while (running) {
            Object received = seedConnection.receive();
            if (received instanceof Piece) {
                System.out.println("received: " + received);
                pieces.add((Piece) received);
            } else if (received instanceof Request) {
                System.out.println("received: " + received);
                seedConnection.close();
                running = false;
                break;
            }
        }
    }

    public boolean isRunning() {
        return running;
    }
}
