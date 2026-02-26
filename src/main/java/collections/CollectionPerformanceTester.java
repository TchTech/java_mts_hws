package collections;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Простой замер времени базовых операций для ArrayList и LinkedList.
 * Результат печатается в консоль таблицей.
 */
public final class CollectionPerformanceTester {

    private CollectionPerformanceTester() {}

    public static void main(String[] args) {
        var r = measure(10_000);
        System.out.println(rToTable(r));
    }

    /**
     * Замер для n элементов.
     * @param n количество
     * @return массив из 6 строк по 2 значения (ArrayList, LinkedList)
     */
    public static long[][] measure(int n) {
        long[][] out = new long[6][2];

        // Добавление в конец
        out[0][0] = timeMs(() -> {
            List<Integer> a = new ArrayList<>();
            for (int i = 0; i < n; i++) a.add(i);
        });
        out[0][1] = timeMs(() -> {
            List<Integer> a = new LinkedList<>();
            for (int i = 0; i < n; i++) a.add(i);
        });

        // Добавление в начало
        out[1][0] = timeMs(() -> {
            List<Integer> a = new ArrayList<>();
            for (int i = 0; i < n; i++) a.add(0, i);
        });
        out[1][1] = timeMs(() -> {
            LinkedList<Integer> a = new LinkedList<>();
            for (int i = 0; i < n; i++) a.addFirst(i);
        });

        // Вставка в середину
        out[2][0] = timeMs(() -> {
            List<Integer> a = new ArrayList<>();
            for (int i = 0; i < n; i++) a.add(i);
            for (int i = 0; i < n; i++) a.add(a.size() / 2, -1);
        });
        out[2][1] = timeMs(() -> {
            LinkedList<Integer> a = new LinkedList<>();
            for (int i = 0; i < n; i++) a.add(i);
            for (int i = 0; i < n; i++) a.add(a.size() / 2, -1);
        });

        // Доступ по индексу
        out[3][0] = timeMs(() -> {
            List<Integer> a = filledArrayList(n);
            long s = 0;
            for (int i = 0; i < n; i++) s += a.get(i);
            blackHole(s);
        });
        out[3][1] = timeMs(() -> {
            List<Integer> a = filledLinkedList(n);
            long s = 0;
            for (int i = 0; i < n; i++) s += a.get(i);
            blackHole(s);
        });

        // Удаление из начала
        out[4][0] = timeMs(() -> {
            List<Integer> a = filledArrayList(n);
            for (int i = 0; i < n; i++) a.remove(0);
        });
        out[4][1] = timeMs(() -> {
            LinkedList<Integer> a = filledLinkedList(n);
            for (int i = 0; i < n; i++) a.removeFirst();
        });

        // Удаление с конца
        out[5][0] = timeMs(() -> {
            List<Integer> a = filledArrayList(n);
            for (int i = 0; i < n; i++) a.remove(a.size() - 1);
        });
        out[5][1] = timeMs(() -> {
            LinkedList<Integer> a = filledLinkedList(n);
            for (int i = 0; i < n; i++) a.removeLast();
        });

        return out;
    }

    public static String rToTable(long[][] r) {
        String[] ops = {
                "Добавление в конец",
                "Добавление в начало",
                "Вставка в середину",
                "Доступ по индексу",
                "Удаление из начала",
                "Удаление из конца"
        };
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-22s | %-14s | %-14s%n", "Операция", "ArrayList, ms", "LinkedList, ms"));
        sb.append("----------------------+-" + "-".repeat(14) + "-+-" + "-".repeat(14) + "\n");
        for (int i = 0; i < ops.length; i++) {
            sb.append(String.format("%-22s | %-14d | %-14d%n", ops[i], r[i][0], r[i][1]));
        }
        return sb.toString();
    }

    private static List<Integer> filledArrayList(int n) {
        List<Integer> a = new ArrayList<>(n);
        for (int i = 0; i < n; i++) a.add(i);
        return a;
    }

    private static LinkedList<Integer> filledLinkedList(int n) {
        LinkedList<Integer> a = new LinkedList<>();
        for (int i = 0; i < n; i++) a.add(i);
        return a;
    }

    private static void blackHole(long v) {
        if (v == Long.MIN_VALUE) System.out.print("");
    }

    private static long timeMs(Runnable r) {
        long t0 = System.nanoTime();
        r.run();
        long t1 = System.nanoTime();
        return (t1 - t0) / 1_000_000;
    }
}
