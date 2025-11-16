package hw11;

import java.util.Optional;

public class ValidationDecorator implements DataService {
    private DataService service;

    public ValidationDecorator(DataService service) {
        this.service = service;
    }

    public Optional<String> findDataByKey(String key) {
        if (key == null || key.isEmpty())
            throw new IllegalArgumentException();
        return service.findDataByKey(key);
    }

    public void saveData(String key, String data) {
        if (key == null || key.isEmpty() || data == null)
            throw new IllegalArgumentException();
        service.saveData(key, data);
    }

    public boolean deleteData(String key) {
        if (key == null || key.isEmpty())
            throw new IllegalArgumentException();
        return service.deleteData(key);
    }
}
