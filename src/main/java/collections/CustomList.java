package collections;

import java.util.Iterator;

/**
 * Интерфейс динамического массива.
 *
 * @param <A> тип элемента
 */
public interface CustomList<A> extends Iterable<A>{
    /**
     * Добавление непустого объекта в конец массива.
     *
     * @param val элемент к добавлению
     * @throws NullPointerException если элемент равен null
     */
    void add(A val);

    /**
     * Возврат элемента по индексу.
     *
     * @param index позиция элемента, начиная с нуля
     * @return элемент по индексу
     * @throws IndexOutOfBoundsException если индекс вне диапазона [0, size)
     */
    A get(int index);

    /**
     * Удаление элемента по индексу с сдвигом хвоста влево.
     *
     * @param index позиция элемента, начиная с нуля
     * @return удалённый элемент
     * @throws IndexOutOfBoundsException если индекс вне диапазона [0, size)
     */
    A remove(int index);

    /**
     * @return текущее число элементов
     */
    int size();

    /**
     * @return true, если размер равен нулю
     */
    boolean isEmpty();

    /**
     * Итератор по элементам в порядке индексов.
     *
     * @return итератор
     */
    @Override
    Iterator<A> iterator();
}
