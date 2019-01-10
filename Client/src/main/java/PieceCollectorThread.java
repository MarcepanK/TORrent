import common.Connection;
import request.Request;

import java.util.ArrayList;
import java.util.List;

public class PieceCollectorThread extends Thread {

    private List<Piece> pieces;
    private Connection seedConnection;
    private boolean running = true;

    public PieceCollectorThread(List<Piece> pieces, Connection seedConnection) {
        this.pieces = pieces;
        this.seedConnection = seedConnection;
    }

    @Override
    public void run() {
        while(running) {
            if (seedConnection.getSocket().isClosed()) {
                Object received = seedConnection.receive();
                System.out.println("Received" + received);
                if (received instanceof Piece) {
                    System.out.println("Received piece");
                    pieces.add((Piece) received);
                } else if (received instanceof Request) {
                    running = false;
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
