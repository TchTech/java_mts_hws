import java.util.Objects;

public class ArrayUtils {
    public static <T> int findFirst(T[] array, T element) {
        if (array == null || array.length == 0) return -1;
        for (int i = 0; i < array.length; i++) {
            if (Objects.equals(array[i], element)) return i;
        }
        return -1;
    }
}
