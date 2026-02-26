package hw11.test;
import hw11.DataService;
import hw11.SimpleDataService;
import hw11.ValidationDecorator;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ValidationDecoratorTest {
    private final DataService service = new ValidationDecorator(new SimpleDataService());

    @Test
    void testValidSave() {
        assertDoesNotThrow(() -> service.saveData("key", "value"));
    }

    @Test
    void testInvalidSave() {
        assertThrows(IllegalArgumentException.class, () -> service.saveData("", "value"));
        assertThrows(IllegalArgumentException.class, () -> service.saveData(null, "value"));
        assertThrows(IllegalArgumentException.class, () -> service.saveData("key", null));
    }

    @Test
    void testValidFind() {
        assertDoesNotThrow(() -> service.findDataByKey("key"));
    }

    @Test
    void testInvalidFind() {
        assertThrows(IllegalArgumentException.class, () -> service.findDataByKey(""));
        assertThrows(IllegalArgumentException.class, () -> service.findDataByKey(null));
    }

    @Test
    void testValidDelete() {
        assertDoesNotThrow(() -> service.deleteData("key"));
    }

    @Test
    void testInvalidDelete() {
        assertThrows(IllegalArgumentException.class, () -> service.deleteData(""));
        assertThrows(IllegalArgumentException.class, () -> service.deleteData(null));
    }
}
