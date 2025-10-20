package collections;

import org.junit.jupiter.api.Test;

/**
 * Печатает таблицу замеров.
 */
public class CollectionPerformanceTesterTest {
    @Test
    void runAndPrint() {
        long[][] r = CollectionPerformanceTester.measure(10_000);
        System.out.print(CollectionPerformanceTester.rToTable(r));
    }
}
