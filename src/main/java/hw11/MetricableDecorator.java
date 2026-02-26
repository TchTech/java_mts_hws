package hw11;

import java.util.Optional;

public class MetricableDecorator implements DataService {
    private DataService service;

    public MetricableDecorator(DataService service) {
        this.service = service;
    }

    public Optional<String> findDataByKey(String key) {
        long start = System.nanoTime();
        Optional<String> res = service.findDataByKey(key);
        long duration = System.nanoTime() - start;
        MetricService.sendMetric(duration);
        return res;
    }

    public void saveData(String key, String data) {
        long start = System.nanoTime();
        service.saveData(key, data);
        long duration = System.nanoTime() - start;
        MetricService.sendMetric(duration);
    }

    public boolean deleteData(String key) {
        long start = System.nanoTime();
        boolean res = service.deleteData(key);
        long duration = System.nanoTime() - start;
        MetricService.sendMetric(duration);
        return res;
    }

    static class MetricService {
        public static void sendMetric(long duration) {
            System.out.println("duration: " + duration);
        }
    }
}