package hw11.test;

import hw11.DataService;
import hw11.SimpleDataService;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SimpleDataServiceTest {
    private final DataService service = new SimpleDataService();

    @Test
    void testSaveAndFindData() {
        service.saveData("key", "value");
        assertEquals("value", service.findDataByKey("key").orElse(null));
    }

    @Test
    void testDeleteData() {
        service.saveData("key", "value");
        assertTrue(service.deleteData("key"));
        assertFalse(service.deleteData("key"));
    }

    @Test
    void testFindNonexistentKey() {
        assertFalse(service.findDataByKey("nonexistent").isPresent());
    }
}
