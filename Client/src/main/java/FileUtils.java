import common.FileMetadata;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class FileUtils {

    /**
     * Invoked when client receives {@link order.UploadOrder}
     * Returns pieces that will be sent to other client
     *
     * If there are multiple peers that are able to send
     * file, then every one of them receives {@link order.UploadOrder}
     * with part number, and number of all parts.
     * i.e. if there's 5 peers able to send a file
     * and client gets order with  params: partNo=2 totalParts=5
     * he needs to send (1,[2],3,4,5) <- marked part of file
     * which has to be split to pieces
     *
     * @param file file which was ordered by leech
     * @param fileMetadata metadata of ordered file
     * @param partNo part of file that will be split to Pieces
     * @param totalParts total number of parts that will be sent to leech
     * @return Pieces which will be sent to leech
     * @throws Exception file might be null
     */
    public static Piece[] getOrderedPieces(File file, FileMetadata fileMetadata, int partNo, int totalParts) throws Exception {
        long partLengthInBytes = fileMetadata.size / totalParts;
        long piecesInPart = partLengthInBytes / Piece.DEFAULT_PIECE_DATA_LEN;
        int pieceIdx = (int) ((partNo - 1) * piecesInPart);
        int byteArrStartingIdx = (int) (partLengthInBytes * (partNo-1));
        if (partNo == 1 && totalParts == 1) {
            return getRemainingPieces(file, fileMetadata, byteArrStartingIdx, pieceIdx);
        } else {
            int byteArrEndingIdx = (int) (byteArrStartingIdx + partLengthInBytes);
            byte[] fileContent = getBytes(file, byteArrStartingIdx, byteArrEndingIdx);
            return splitToPieces(fileMetadata, fileContent, pieceIdx, piecesInPart);
        }
    }

    /**
     * Invoked when last part of file is required or there's only 1 seed
     * Returns pieces that will be sent to other client
     *
     * @param file file that will be split to pieces
     * @param fileMetadata metadata of file
     * @param byteArrStartingIdx index at which bytes will be read from file
     * @param pieceIdx index of first piece that will be sent to leech
     * @return Pieces which will be sent to leech
     */
    public static Piece[] getRemainingPieces(File file, FileMetadata fileMetadata, int byteArrStartingIdx, int pieceIdx) {
        int byteArrEndingIdx= (int) fileMetadata.size;
        byte[] fileContent;
        try {
            fileContent = getBytes(file, byteArrStartingIdx, byteArrEndingIdx);
            return splitToPieces(fileMetadata, fileContent, pieceIdx, (fileContent.length/Piece.DEFAULT_PIECE_DATA_LEN )+1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Returns bytes
     *
     * @param file file that will be split to pieces
     * @param fileContentStartingIdx index from which we will start reading bytes from file
     * @param fileContentEndingIdx index at which we will stop reading bytes from file
     * @return array of bytes representing part of file
     */
    private static byte[] getBytes(File file, int fileContentStartingIdx, int fileContentEndingIdx) throws Exception {
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
        byte[] fileContent = new byte[fileContentEndingIdx - fileContentStartingIdx];
        System.out.println(String.format("File size: %d\nStarting idx: %d Ending idx: %d\nNew array size: %d", file.length(),
                fileContentStartingIdx, fileContentEndingIdx, fileContent.length));
        randomAccessFile.seek(fileContentStartingIdx);
        randomAccessFile.read(fileContent, 0, fileContent.length);
        return fileContent;
    }

    /**
     * Returns array of pieces generated from already cut out byte array from file
     * converts array of bytes to array of pieces
     *
     * @param fileMetadata metadata of ordered file
     * @param bytes file content that will be converted into pieces
     * @param pieceIdx index of first piece
     * @param piecesInPart total number of pieces in one part
     * @return Pieces that will be sent to leech
     */
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
        createFile(path);
        FileOutputStream fos = new FileOutputStream(new File(path));
        for (Piece piece : pieces){
            fos.write(piece.data);
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
