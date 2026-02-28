package collections;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Наивная реализация списка на основе массива Object[].
 * При нехватке места увеличивает ёмкость в 1.5 раза.
 *
 * @param <A> тип элемента
 */
public class CustomArrayList<A> implements CustomList<A> {

    private static final int DEFAULT_CAPACITY = 10;
    private static final double GROWTH = 1.5;

    private Object[] data;
    private int size;

    /**
     * Пустой список с ёмкостью по умолчанию.
     */
    public CustomArrayList() {
        this.data = new Object[DEFAULT_CAPACITY];
    }

    /**
     * Пустой список с заданной стартовой ёмкостью.
     *
     * @param initialCapacity начальная ёмкость (>= 0)
     * @throws IllegalArgumentException если initialCapacity < 0
     */
    public CustomArrayList(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("capacity < 0");
        }
        this.data = new Object[Math.max(1, initialCapacity)];
    }

    @Override
    public void add(A val) {
        if (val == null) {
            throw new NullPointerException("null запрещён");
        }
        ensure(size + 1);
        data[size++] = val;
    }

    @SuppressWarnings("unchecked")
    @Override
    public A get(int index) {
        check(index);
        return (A) data[index];
    }

    @SuppressWarnings("unchecked")
    @Override
    public A remove(int index) {
        check(index);
        A old = (A) data[index];
        int moved = size - index - 1;
        if (moved > 0) {
            System.arraycopy(data, index + 1, data, index, moved);
        }
        data[--size] = null;
        return old;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public Iterator<A> iterator() {
        return new Itr();
    }

    private void ensure(int min) {
        if (min <= data.length) return;
        int newCap = (int) Math.ceil(data.length * GROWTH);
        if (newCap < min) newCap = min;
        Object[] next = new Object[newCap];
        System.arraycopy(data, 0, next, 0, size);
        data = next;
    }

    private void check(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("index=" + index + ", size=" + size);
        }
    }

    private final class Itr implements Iterator<A> {
        int i;
        @Override public boolean hasNext() { return i < size; }
        @SuppressWarnings("unchecked")
        @Override public A next() {
            if (!hasNext()) throw new NoSuchElementException();
            return (A) data[i++];
        }
    }
}
