public class ResponseHandler {

    private Connection trackerConnection;
    private ResponseProcessor responseProcessor;

    public ResponseHandler(Connection trackerConnection, ResponseProcessor responseProcessor) {
        this.trackerConnection = trackerConnection;
        this.responseProcessor = responseProcessor;
    }
}
