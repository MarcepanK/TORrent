package order;

import common.FileMetadata;

import java.io.Serializable;

public abstract class Order implements Serializable {

    public final FileMetadata orderedFileMetadata;

    public Order(FileMetadata orderedFileMetadata) {
        this.orderedFileMetadata = orderedFileMetadata;
    }

}
