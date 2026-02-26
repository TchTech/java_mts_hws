package hw6;

import java.util.Objects;

public class ArrayUtils {
    public static <T> int findFirst(T[] array, T element) {
        if (array == null || array.length == 0) return -1;
        for (int i = 0; i < array.length; i++) {
            if (Objects.equals(array[i], element)) return i;
        }
        return -1;
    }
    public static void main(String[] args) {
        // пример использования
        final String[] names = {"Alice", "Bob", "Charlie"};
        final int index = ArrayUtils.findFirst(names, "Bob"); // Ожидаем: 1 (тк нумерация в массиве начинается с нуля)
    }
}
