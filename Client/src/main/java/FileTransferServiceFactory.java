public class FileTransferServiceFactory {

    public static FileTransferService getFileTransferService() {
        return new FileDownloadService();
    }

}
