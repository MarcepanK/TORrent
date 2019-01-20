import common.FileMetadata;
import request.RetryDownloadRequest;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class FileUtils {

    /**
     * Invoked when client receives {@link order.UploadOrder}
     * Returns pieces that will be sent to other client
     * <p>
     * If there are multiple peers that are able to send
     * file, then every one of them receives {@link order.UploadOrder}
     * with part number, and number of all parts.
     * i.e. if there's 5 peers able to send a file
     * and client gets order with  params: partNo=2 totalParts=5
     * he needs to send (1,[2],3,4,5) <- marked part of file
     * which has to be split to pieces
     *
     * @param file         file which was ordered by leech
     * @param fileMetadata metadata of ordered file
     * @param partNo       part of file that will be split to Pieces
     * @param totalParts   total number of parts that will be sent to leech
     * @return Pieces which will be sent to leech
     * @throws Exception file might be null
     */
    public static Piece[] getOrderedPieces(File file, FileMetadata fileMetadata, int partNo, int totalParts) {
        long partLengthInBytes = fileMetadata.size / totalParts;
        long piecesInPart = partLengthInBytes / Piece.DEFAULT_PIECE_DATA_LEN;
        int pieceIdx = (int) ((partNo - 1) * piecesInPart);
        int byteArrStartingIdx = (int) (partLengthInBytes * (partNo - 1));
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
     * @param file               file that will be split to pieces
     * @param fileMetadata       metadata of file
     * @param byteArrStartingIdx index at which bytes will be read from file
     * @param pieceIdx           index of first piece that will be sent to leech
     * @return Pieces which will be sent to leech
     */
    public static Piece[] getRemainingPieces(File file, FileMetadata fileMetadata, int byteArrStartingIdx, int pieceIdx) {
        int byteArrEndingIdx = (int) fileMetadata.size;
        byte[] fileContent;
        try {
            fileContent = getBytes(file, byteArrStartingIdx, byteArrEndingIdx);
            return splitToPieces(fileMetadata, fileContent, pieceIdx,
                    (fileContent.length / Piece.DEFAULT_PIECE_DATA_LEN) + 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Collection<Piece> getSpecificPieces(File file, FileMetadata fileMetadata, Collection<Integer> missingPiecesIndexes,
                                                      int trailingBytes, int lastOwnedPieceIdx) {
        List<Piece> missingPieces = new ArrayList<>();
        for (int missingPieceIdx : missingPiecesIndexes) {
            missingPieces.add(getSpecificPiece(file, fileMetadata, missingPieceIdx));
        }
        if (trailingBytes > 0) {
            Piece[] trailingPieces = getRemainingPieces(file, fileMetadata,
                    lastOwnedPieceIdx * Piece.DEFAULT_PIECE_DATA_LEN, lastOwnedPieceIdx);
            if (trailingPieces != null) {
                missingPieces.addAll(Arrays.asList(trailingPieces));
            }
        }
        for (Piece piece : missingPieces) {
            System.out.println("Missing piece: " + piece.index + " | " + piece.data.length);
        }
        System.out.println("Trailing bytes: " + trailingBytes);
        return missingPieces;
    }

    public static Piece getSpecificPiece(File file, FileMetadata fileMetadata, int missingPieceIdx) {
        int startingIdx = missingPieceIdx * Piece.DEFAULT_PIECE_DATA_LEN;
        int endingIdx = (missingPieceIdx + 1) * Piece.DEFAULT_PIECE_DATA_LEN;
        byte[] bytes = getBytes(file, startingIdx, endingIdx);
        return new Piece(fileMetadata, missingPieceIdx, bytes);
    }

    /**
     * Returns bytes
     *
     * @param file                   file that will be split to pieces
     * @param fileContentStartingIdx index from which we will start reading bytes from file
     * @param fileContentEndingIdx   index at which we will stop reading bytes from file
     * @return array of bytes representing part of file
     */
    private static byte[] getBytes(File file, int fileContentStartingIdx, int fileContentEndingIdx) {
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
            byte[] fileContent = new byte[fileContentEndingIdx - fileContentStartingIdx];
            System.out.println(String.format("File size: %d\nStarting idx: %d Ending idx: %d\nNew array size: %d", file.length(),
                    fileContentStartingIdx, fileContentEndingIdx, fileContent.length));
            randomAccessFile.seek(fileContentStartingIdx);
            randomAccessFile.read(fileContent, 0, fileContent.length);
            return fileContent;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Returns array of pieces generated from already cut out byte array from file
     * converts array of bytes to array of pieces
     *
     * @param fileMetadata metadata of ordered file
     * @param bytes        file content that will be converted into pieces
     * @param pieceIdx     index of first piece
     * @param piecesInPart total number of pieces in one part
     * @return Pieces that will be sent to leech
     */
    private static Piece[] splitToPieces(FileMetadata fileMetadata, byte[] bytes, int pieceIdx, long piecesInPart) {
        Piece[] pieces = new Piece[(int) piecesInPart];
        int i;
        for (i = 0; i < piecesInPart - 1; i++) {
            int from = i * Piece.DEFAULT_PIECE_DATA_LEN;
            int to = i * Piece.DEFAULT_PIECE_DATA_LEN + Piece.DEFAULT_PIECE_DATA_LEN;
            pieces[i] = new Piece(fileMetadata, pieceIdx, Arrays.copyOfRange(bytes, from, to));
            pieceIdx++;
        }
        pieces[i] = new Piece(fileMetadata, pieceIdx, Arrays.copyOfRange(bytes, i * Piece.DEFAULT_PIECE_DATA_LEN, bytes.length));
        return pieces;
    }

    public static void assembleFileFromPieces(Collection<Piece> pieces, String path) {
        try {
            createFile(path);
            FileOutputStream fos = new FileOutputStream(new File(path));
            for (Piece piece : pieces) {
                fos.write(piece.data);
            }
        } catch (IOException e) {
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

    public static void storeOwnedPiecesInFile(List<Piece> pieces, int clientId) {
        try {
            String path = Client.DEFAULT_PATH_PREFIX + clientId + "/" + pieces.get(0).fileMetadata.name +
                    ".pieces.ser";
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path));
            for (Piece piece : pieces) {
                oos.writeObject(piece);
                oos.flush();
            }
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void storeRetryDownloadRequestInFile(RetryDownloadRequest request) {
        try {
            String path = Client.DEFAULT_PATH_PREFIX + request.requesterId + "/" +
                    request.transferredFileMetadata.name + ".ser";
            createFile(path);
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path));
            oos.writeObject(request);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Collection<Piece> getPiecesFromBrokenFile(int clientId, String fileName) throws Exception {
        ArrayList<Piece> pieces = new ArrayList<>();
        File dir = new File(Client.DEFAULT_PATH_PREFIX + clientId);
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.getName().equals(fileName + ".pieces.ser")) {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
                while(ois.available() > 0) {
                    Object rcv = ois.readObject();
                    if (rcv instanceof Piece) {
                        System.out.println("Piece from file: " + ((Piece) rcv).index + " | " + ((Piece) rcv).index) ;
                        pieces.add((Piece) rcv);
                    }
                }
            }
        }
        return pieces;
    }

    public static void removeBrokenFiles(int clientId, String fileName) throws Exception {
        File dir = new File(Client.DEFAULT_PATH_PREFIX + clientId);
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.getName().endsWith(".ser") && file.getName().startsWith(fileName)) {
                file.delete();
            }
        }
    }
}
