package collections;

import java.util.*;

/**
 * Небольшая демонстрация для задания с Map.
 */
public class MapDemo {
    public static void main(String[] args) {
        Map<Integer, Student> byId = new HashMap<>();
        byId.put(1, new Student(1, "Alice", 4.8));
        byId.put(3, new Student(3, "Bob", 3.6));
        byId.put(2, new Student(2, "Carol", 4.2));
        byId.put(5, new Student(5, "Dave", 2.9));

        System.out.println("В диапазоне [3.5, 5.0]: " +
                MapUtils.findStudentsByGradeRange(byId, 3.5, 5.0));

        TreeMap<Integer, Student> desc = new TreeMap<>(Comparator.reverseOrder());
        desc.putAll(byId);
        System.out.println("ТОП-3 по id: " + MapUtils.getTopNStudents(desc, 3));
    }
}
