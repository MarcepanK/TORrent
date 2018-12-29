import java.util.logging.Level;
import java.util.logging.Logger;

public class TestClass {

    private static final Logger logger = Logger.getLogger(TestClass.class.getName());

    private int cos;

    public TestClass(int a) {
        cos = a;
        logger.info("created\n___________");
        logger.warning("warning\n___________");
        logger.config("config\n__________");
        logger.fine("fine");
        logger.severe("severe\n____________");
    }
}
