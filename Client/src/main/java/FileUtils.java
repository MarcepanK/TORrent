import common.FileMetadata;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class FileUtils {

    public static Piece[] getOrderedPieces(File file, FileMetadata fileMetadata, int partNo, int totalParts) throws Exception {
        long partLengthInBytes = fileMetadata.size / totalParts;
        long piecesInPart = partLengthInBytes / Piece.DEFAULT_PIECE_DATA_LEN;
        int pieceIdx = (int) ((partNo - 1) * piecesInPart);
        int byteArrStartingIdx = (int) (partLengthInBytes * (partNo-1));
        if (partNo == 1 && totalParts == 1) {
            return getRemainingPieces(file, fileMetadata, byteArrStartingIdx, pieceIdx);
        }
        if (partNo == totalParts) {
            return getRemainingPieces(file, fileMetadata, byteArrStartingIdx, pieceIdx);
        }
        int byteArrEndingIdx = (int) (byteArrStartingIdx + partLengthInBytes);
        byte[] fileContent = getBytes(file, byteArrStartingIdx, byteArrEndingIdx);
        Piece[] pieces = splitToPieces(fileMetadata, fileContent, pieceIdx, piecesInPart);
        return pieces;
    }

    public static Piece[] getRemainingPieces(File file, FileMetadata fileMetadata, int byteArrStartingIdx, int pieceIdx) throws Exception {
        int byteArrEndingIdx= (int) fileMetadata.size;
        byte[] fileContent = getBytes(file, byteArrStartingIdx, byteArrEndingIdx);
        return splitToPieces(fileMetadata, fileContent, pieceIdx, (fileContent.length/Piece.DEFAULT_PIECE_DATA_LEN )+1);
    }

    //FIXME
    private static byte[] getBytes(File file, int fileContentStartingIdx, int fileContentEndingIdx) throws Exception {
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
        byte[] fileContent = new byte[fileContentEndingIdx - fileContentStartingIdx];
        System.out.println(String.format("File size: %d\nStarting idx: %d Ending idx: %d\nNew array size: %d", file.length(), fileContentStartingIdx, fileContentEndingIdx, fileContent.length));
        randomAccessFile.seek(fileContentStartingIdx);
        randomAccessFile.read(fileContent, 0, fileContent.length);
        return fileContent;
    }

    private static Piece[] splitToPieces(FileMetadata fileMetadata, byte[] bytes, int pieceIdx, long piecesInPart) {
        Piece[] pieces = new Piece[(int) piecesInPart];
        int i;
        for (i=0; i<piecesInPart-1; i++) {
            int from = i*Piece.DEFAULT_PIECE_DATA_LEN;
            int to = i * Piece.DEFAULT_PIECE_DATA_LEN + Piece.DEFAULT_PIECE_DATA_LEN;
            pieces[i] = new Piece(fileMetadata, pieceIdx, Arrays.copyOfRange(bytes, from, to));
            pieceIdx++;
        }
        pieces[i] = new Piece(fileMetadata, pieceIdx, Arrays.copyOfRange(bytes, i*Piece.DEFAULT_PIECE_DATA_LEN, bytes.length));
        return pieces;
    }

    public static void assembleFileFromPieces(Piece[] pieces, String path) throws Exception {
        byte[] fileContent;
        long allPiecesLen = 0;
        for (Piece piece : pieces) {
            if (piece != null) {
                allPiecesLen += piece.data.length;
            }
        }
        fileContent = new byte[(int)allPiecesLen];
        int idx = 0;
        for (Piece piece : pieces) {
            if (piece != null) {
                for (byte b : piece.data) {
                    if (b == 0) {
                        break;
                    }
                    fileContent[idx++] = b;
                }
            } else {
                break;
            }
        }
        byteArrToFile(fileContent, path);
    }

    public static void byteArrToFile(byte[] fileContent, String filePath) throws Exception {
        createFile(filePath);
        FileOutputStream fos = new FileOutputStream(filePath);
        fos.write(fileContent);
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
