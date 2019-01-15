import common.FileMetadata;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;


/**
 * This class is responsible for storing all files and their metadata from given path
 */
public class FileRepository {

    private static final Logger logger = Logger.getLogger(FileRepository.class.getName());

    private String directoryPath;
    private Map<File, FileMetadata> files;

    public FileRepository(String directoryPath) {
        files = new ConcurrentHashMap<>();
        this.directoryPath = directoryPath;
        initFiles();
    }

    /**
     * Loads all files and their metadata into map
     */
    private void initFiles() {
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            try {
                Files.createDirectory(Paths.get(directoryPath));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        for (File file : directory.listFiles()) {
            files.put(file, FileUtils.getFileMetadata(file));
        }
    }

    /**
     * Invoked when download service is complete or by user entering command 'update'
     * Checks if all files are already stored in map, and if not, generates their metadata
     * and adds them to map
     */
    public void update() {
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            logger.warning(String.format("Directory with path: %s doesnt exist", directoryPath));
        } else {
            File[] dirFiles = directory.listFiles();
            for (File file : dirFiles) {
                if (!files.containsKey(file)) {
                    files.put(file, FileUtils.getFileMetadata(file));
                    logger.info("new file data stored");
                }
            }
        }
    }


    public Optional<File> getFileByName(String fileName) {
        for (Map.Entry<File, FileMetadata> entry : files.entrySet()) {
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

    public Optional<FileMetadata> getFileMetadata(File file) {
        return Optional.of(files.get(file));
    }

    public FileMetadata[] getAllFilesMetadata() {
        return files.values().toArray(new FileMetadata[0]);
    }
}
