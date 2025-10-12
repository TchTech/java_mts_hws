import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CollectionUtils {

    public static <T> List<T> mergeLists(List<? extends T> list1,
                                         List<? extends T> list2) {
        List<T> out = new ArrayList<>();
        if (list1 != null) out.addAll(list1);
        if (list2 != null) out.addAll(list2);
        return out;
    }

    public static <T> void addAll(List<? super T> destination,
                                  List<? extends T> source) {
        if (destination == null || source == null) return;
        destination.addAll(source);
    }
}
