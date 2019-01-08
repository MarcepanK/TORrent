import common.FileMetadata;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class FileUtilsTest {

    private static String testFilePath = "D:/TORrent_1/asd.txt.txt";
    private static File myFile;
    private static FileMetadata fileMetadata;

    private void writeToFile() throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(myFile));
        for (int i=0; i<10000; i++) {
            if (i%10 == 0) {
                bw.write(String.format("%d \n", i));
            } else {
                bw.write(String.format("%d", i));
            }
        }
    }

    @Before
    public void setup() throws IOException {
        File directory = new File(Client.DEFAULT_PATH_PREFIX + 1);
        myFile = new File(testFilePath);
        writeToFile();
//
//        if (directory.exists()) {
//            System.out.println("Directory exists");
//            for (File file : directory.listFiles()) {
//                if (file.getName().equals("asd.txt")) {
//                    System.out.println("File found");
//                    myFile = file;
//                    fileMetadata = FileUtils.getFileMetadata(myFile);
//                }
//            }
//        }
    }

    @Test
    public void splitToPiecesTest() throws IOException {
        if (myFile.exists()) {
            byte[] fileContent = Files.readAllBytes(Paths.get(testFilePath));
            Piece[] pieces = FileUtils.splitToPieces(fileMetadata, fileContent);
//            for (Piece piece : pieces) {
//                System.out.println(piece.index + " " + piece.data.length);
//            }

            FileUtils.assemblyFileFromPieces(pieces, "D:/TORrent_1/asdTest.txt");
        } else {
            System.out.println("File doesnt exist");
        }
    }


}