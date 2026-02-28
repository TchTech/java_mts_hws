package hw11;

public class Main {
    public static void main(String[] args) {
        final var service = new ValidationDecorator(
                new MetricableDecorator(
                        new LoggingDecorator(
                                new CachingDecorator(
                                        new SimpleDataService()
                                )
                        )
                )
        );

        service.saveData("key", "data");
        final var data = service.findDataByKey("key");
        service.deleteData("key");
        final var noData = service.findDataByKey("key");
    }
}
