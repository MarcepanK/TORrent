import common.FileMetadata;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class FileUtils {

    public static Piece[] getOrderedFilePieces(File file, FileMetadata fileMetadata, int partNo, int totalParts) {
        try {
            RandomAccessFile RAFFile = new RandomAccessFile(file, "r");
            long fileLen = fileMetadata.size;
            long partLen = fileLen/totalParts;
            byte[] bytes = new byte[(int)partLen];
            RAFFile.read(bytes,(int)partLen * partNo, (int)partLen);
            return splitToPieces(fileMetadata, bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Piece[] splitToPieces(FileMetadata fileMetadata, byte[] data) {
        Piece[] pieces = new Piece[data.length / (int)Piece.DEFAULT_PIECE_DATA_LEN];
        int pieceStartingIdx = 0;
        int pieceIdx = 0;
        while (pieceStartingIdx + (int)Piece.DEFAULT_PIECE_DATA_LEN < data.length) {
            if (pieceStartingIdx + (int)Piece.DEFAULT_PIECE_DATA_LEN > data.length) {
                byte[] newData = Arrays.copyOfRange(data, pieceStartingIdx, data.length);
                pieces[pieceIdx] = new Piece(fileMetadata, pieceIdx, pieces.length, newData);
            } else {
                pieces[pieceIdx] = new Piece(fileMetadata, pieceIdx, pieces.length,
                        Arrays.copyOfRange(data, pieceStartingIdx, pieceStartingIdx + (int) Piece.DEFAULT_PIECE_DATA_LEN));
            }
            pieceStartingIdx += Piece.DEFAULT_PIECE_DATA_LEN;
            pieceIdx++;
        }
        return pieces;
    }

    public static void assemblyFileFromPieces(Piece[] pieces, String path) throws IOException {
        byte[] fileContent;
        int allPiecesLen = 0;
        int idx = 0;
        for (Piece piece : pieces) {
            if (piece != null) {
                System.out.println(idx++);
                allPiecesLen += piece.data.length;
            }
        }
        fileContent = new byte[allPiecesLen];
        idx = 0;
        for(Piece piece : pieces) {
            if (piece != null) {
                for (byte b : piece.data) {
                    fileContent[idx++] = b;
                }
            } else {
                break;
            }
        }
        try {
            for (int i=0; i < allPiecesLen; i++) {
                System.out.println(fileContent[i]);
            }
            createFile(path);
            FileOutputStream fos = new FileOutputStream(path);
            fos.write(fileContent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void createFile(String path) throws IOException {
        File f = new File(path);
        if (!f.exists()) {
            Files.createFile(Paths.get(path));
        }
    }

    public static FileMetadata getFileMetadata(File file) {
        return new FileMetadata(file);
    }
}
