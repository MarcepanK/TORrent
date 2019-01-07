import common.FileMetadata;

import java.io.File;
import java.util.ArrayList;

public class FileUtils {


    public static ArrayList<Piece> splitFileToPieces(File file) {
        return new ArrayList<Piece>();
    }

    public static FileMetadata getFileMetadata(File file) {
        return new FileMetadata(file);
    }
}
