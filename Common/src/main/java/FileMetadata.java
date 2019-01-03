import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.Serializable;
import java.nio.file.Files;
import java.security.MessageDigest;

public class FileMetadata implements Serializable {

    public final String name;
    public final long size;
    public final String md5sum;

    public FileMetadata(File file) {
        name = file.getName();
        size = file.length();
        md5sum = generateMD5sum(file);
    }

    public FileMetadata(String name, long size, String md5sum) {
        this.name = name;
        this.size = size;
        this.md5sum = md5sum;
    }

    public static String generateMD5sum(File file) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] arr = md.digest(Files.readAllBytes(file.toPath()));
            return DatatypeConverter.printHexBinary(arr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
