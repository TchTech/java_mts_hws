package hw11;
import java.util.Optional;

public class LoggingDecorator implements DataService {
    private DataService service;

    public LoggingDecorator(DataService service) {
        this.service = service;
    }

    public Optional<String> findDataByKey(String key) {
        System.out.println("findDataByKey: " + key);
        return service.findDataByKey(key);
    }

    public void saveData(String key, String data) {
        System.out.println("saveData: " + key);
        service.saveData(key, data);
    }

    public boolean deleteData(String key) {
        System.out.println("deleteData: " + key);
        return service.deleteData(key);
    }
}
