package collections;

import java.util.*;

/**
 * Утилиты для работы с картами id -> Student.
 */
public final class MapUtils {

    private MapUtils() {}

    /**
     * Возвращает список студентов с оценкой в диапазоне [min, max].
     * Порядок — по возрастанию id.
     *
     * @param map источник
     * @param minGrade нижняя граница (включительно)
     * @param maxGrade верхняя граница (включительно)
     * @return список студентов
     */
    public static List<Student> findStudentsByGradeRange(Map<Integer, Student> map,
                                                         double minGrade,
                                                         double maxGrade) {
        if (map == null) throw new NullPointerException("map");
        if (minGrade > maxGrade) {
            double t = minGrade; minGrade = maxGrade; maxGrade = t;
        }
        List<Student> res = new ArrayList<>();
        for (Student s : map.values()) {
            double g = s.getGrade();
            if (g >= minGrade && g <= maxGrade) {
                res.add(s);
            }
        }
        res.sort(Comparator.comparingInt(Student::getId));
        return res;
    }

    /**
     * Возвращает первых N студентов из TreeMap с ключами в порядке убывания.
     *
     * @param map дерево с компаратором по ключу по убыванию
     * @param n сколько вернуть
     * @return до N студентов в порядке убывания id
     */
    public static List<Student> getTopNStudents(TreeMap<Integer, Student> map, int n) {
        if (map == null) throw new NullPointerException("map");
        if (n <= 0) return List.of();
        List<Student> res = new ArrayList<>(Math.min(n, map.size()));
        int i = 0;
        for (Map.Entry<Integer, Student> e : map.entrySet()) {
            if (i++ >= n) break;
            res.add(e.getValue());
        }
        return res;
    }
}
