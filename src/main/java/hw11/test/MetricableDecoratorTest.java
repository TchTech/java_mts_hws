package hw11.test;
import hw11.DataService;
import hw11.MetricableDecorator;
import hw11.SimpleDataService;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MetricableDecoratorTest {
    private final DataService service = new MetricableDecorator(new SimpleDataService());

    @Test
    void testMetricOnSave() {
        service.saveData("key", "value");
    }

    @Test
    void testMetricOnFind() {
        service.findDataByKey("key");
    }

    @Test
    void testMetricOnDelete() {
        service.deleteData("key");
    }
}
