import common.FileMetadata;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class FileRepository {

    private Map<File, FileMetadata> files;

    public FileRepository(String dirPath) {
        files = new ConcurrentHashMap<>();
        initFiles(dirPath);

    }

    private void initFiles(String dirPath) {
        File directory = new File(dirPath);
        if (!directory.exists()) {
            try {
                Files.createDirectory(Paths.get(dirPath));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        for(File file : directory.listFiles()) {
            files.put(file, FileUtils.getFileMetadata(file));
        }
    }

    public Optional<File> getFileByName(String fileName) {
        for(Map.Entry<File, FileMetadata> entry : files.entrySet()) {
            if (entry.getValue().name.equals(fileName)) {
                return Optional.of(entry.getKey());
            }
        }
        return Optional.empty();
    }

    public Optional<File> getFileByMd5sum(String md5sum) {
        for (Map.Entry<File, FileMetadata> entry : files.entrySet()) {
            if (entry.getValue().md5sum.equals(md5sum)) {
                return Optional.of(entry.getKey());
            }
        }
        return Optional.empty();
    }

    public FileMetadata[] getAllFilesMetadata() {
        return files.values().toArray(new FileMetadata[0]);
    }
}
