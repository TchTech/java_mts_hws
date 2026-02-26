package hw11.test;

import hw11.DataService;
import hw11.LoggingDecorator;
import hw11.SimpleDataService;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LoggingDecoratorTest {
    private final DataService service = new LoggingDecorator(new SimpleDataService());

    @Test
    void testLoggingOnSave() {
        service.saveData("key", "value");
    }

    @Test
    void testLoggingOnFind() {
        service.findDataByKey("key");
    }

    @Test
    void testLoggingOnDelete() {
        service.deleteData("key");
    }
}
