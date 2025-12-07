package collections;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Минимальные проверки функционала CustomArrayList.
 */
public class CustomArrayListTest {

    @Test
    void addGetSize() {
        CustomList<String> list = new CustomArrayList<>();
        list.add("x");
        list.add("y");
        assertEquals(2, list.size());
        assertEquals("x", list.get(0));
        assertEquals("y", list.get(1));
    }

    @Test
    void nullRejected() {
        CustomList<Integer> list = new CustomArrayList<>();
        assertThrows(NullPointerException.class, () -> list.add(null));
        assertTrue(list.isEmpty());
    }

    @Test
    void removeShifts() {
        CustomList<Integer> list = new CustomArrayList<>();
        list.add(10); list.add(20); list.add(30);
        assertEquals(20, list.remove(1));
        assertEquals(2, list.size());
        assertEquals(30, list.get(1));
    }

    @Test
    void bounds() {
        CustomList<Integer> list = new CustomArrayList<>();
        list.add(1);
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(-1));
    }

    @Test
    void iteratorOrder() {
        CustomList<Integer> list = new CustomArrayList<>();
        for (int i = 0; i < 5; i++) list.add(i);
        List<Integer> copy = new ArrayList<>();
        for (int v : list) copy.add(v);
        assertEquals(List.of(0,1,2,3,4), copy);
    }
}
