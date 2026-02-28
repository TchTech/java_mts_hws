package hw11.test;

import hw11.CachingDecorator;
import hw11.DataService;
import hw11.SimpleDataService;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CachingDecoratorTest {
    private final DataService service = new CachingDecorator(new SimpleDataService());

    @Test
    void testCacheHit() {
        service.saveData("key", "value");
        assertEquals("value", service.findDataByKey("key").orElse(null));
        assertEquals("value", service.findDataByKey("key").orElse(null));
    }

    @Test
    void testCacheAfterDelete() {
        service.saveData("key", "value");
        service.deleteData("key");
        assertFalse(service.findDataByKey("key").isPresent());
    }
}
