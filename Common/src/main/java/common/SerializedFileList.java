package common;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class SerializedFileList implements Serializable {
    //key - metadata of file, int[] - id's of owners that have the file
    private Map<FileMetadata, int[]> files;

    public SerializedFileList() {
        files = new HashMap<>();
    }

    public void addEntry(FileMetadata fileMetadata, int[] owners) {
        files.putIfAbsent(fileMetadata, owners);
    }

    public void print() {
        if (!files.isEmpty()) {
            for (Map.Entry<FileMetadata, int[]> entry : files.entrySet()) {
                System.out.print(entry.getKey() + "| owners: ");
                for (int ownerId : entry.getValue()) {
                    System.out.print(ownerId + " ");
                }
                System.out.println();
            }
        } else {
            System.out.println("no files");
        }
    }
}
