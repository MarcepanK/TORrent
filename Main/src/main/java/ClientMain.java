import common.ClientHandshake;
import common.FileMetadata;
import request.Request;
import request.RequestCode;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientMain {
    public static void main(String[] args) {


        Socket sock;
        FileMetadata[] clientBFiles = {new FileMetadata("file1", 10, "md1"),
                new FileMetadata("file20", 200, "md20")};

        {
            try {
                sock = new Socket("localhost", Tracker.TRACKER_PORT);
                ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());
                ObjectInputStream ois = new ObjectInputStream(sock.getInputStream());


                oos.writeObject(new ClientHandshake(1, clientBFiles));
                oos.writeObject(new Request(1, RequestCode.FILE_LIST));


                while(true) {
                    Object recv = ois.readObject();
                    recv.toString();
                }

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
