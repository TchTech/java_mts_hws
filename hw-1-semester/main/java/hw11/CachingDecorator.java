package hw11;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CachingDecorator implements DataService {
    private DataService service;
    private Map<String, Optional<String>> cache = new HashMap<>();

    public CachingDecorator(DataService service) {
        this.service = service;
    }

    public Optional<String> findDataByKey(String key) {
        if (cache.containsKey(key)) {
            return cache.get(key);
        }
        Optional<String> result = service.findDataByKey(key);
        cache.put(key, result);
        return result;
    }

    public void saveData(String key, String data) {
        service.saveData(key, data);
        cache.put(key, Optional.ofNullable(data));
    }

    public boolean deleteData(String key) {
        boolean res = service.deleteData(key);
        cache.remove(key);
        return res;
    }
}