package dz.nexatech.reporter.client.common;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
public class StaticList<E> extends AbstractList<E> implements RandomAccess {

    private final E[] a;

    public StaticList(E[] array) {
        a = Objects.requireNonNull(array);
    }

    @Override
    public int size() {
        return a.length;
    }

    @Override
    public Object[] toArray() {
        return Arrays.copyOf(a, a.length, Object[].class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        int size = size();
        if (a.length < size)
            return Arrays.copyOf(this.a, size,
                    (Class<? extends T[]>) a.getClass());
        System.arraycopy(this.a, 0, a, 0, size);
        if (a.length > size)
            a[size] = null;
        return a;
    }

    @Override
    public E get(int index) {
        return a[index];
    }

    @Override
    public E set(int index, E element) {
        E oldValue = a[index];
        a[index] = element;
        return oldValue;
    }

    @Override
    public int indexOf(Object o) {
        E[] a = this.a;
        if (o == null) {
            for (int i = 0; i < a.length; i++)
                if (a[i] == null)
                    return i;
        } else {
            for (int i = 0; i < a.length; i++)
                if (o.equals(a[i]))
                    return i;
        }
        return -1;
    }

    @Override
    public boolean contains(Object o) {
        return indexOf(o) >= 0;
    }

    @Override
    public Spliterator<E> spliterator() {
        return Spliterators.spliterator(a, Spliterator.ORDERED);
    }

    @Override
    public void forEach(Consumer<? super E> action) {
        Objects.requireNonNull(action);
        for (E e : a) {
            action.accept(e);
        }
    }

    @Override
    public void replaceAll(UnaryOperator<E> operator) {
        Objects.requireNonNull(operator);
        E[] a = this.a;
        for (int i = 0; i < a.length; i++) {
            a[i] = operator.apply(a[i]);
        }
    }

    @Override
    public void sort(Comparator<? super E> c) {
        Arrays.sort(a, c);
    }

    @Override
    public Iterator<E> iterator() {
        return new ArrayIterator<>(a);
    }

    public static class ArrayIterator<E> implements Iterator<E> {
        private int cursor;
        private final E[] a;

        ArrayIterator(E[] a) {
            this.a = a;
        }

        @Override
        public boolean hasNext() {
            return cursor < a.length;
        }

        @Override
        public E next() {
            int i = cursor;
            if (i >= a.length) {
                throw new NoSuchElementException();
            }
            cursor = i + 1;
            return a[i];
        }
    }

    public static <E> Builder<E> newBuilder(Class<E> type, int capacity) {
        return new Builder<>(type, capacity, false);
    }

    public static <E> Builder<E> newReversedBuilder(Class<E> type, int capacity) {
        return new Builder<>(type, capacity, true);
    }

    @SuppressWarnings("unchecked")
    public static class Builder<E> {
        private final E[] array;
        private int index;

        private int size = 0;

        private final boolean reversed;

        private final Class<E> type;

        private Builder(Class<E> type, int capacity, boolean reversed) {
            this.array = (E[]) Array.newInstance(type, capacity);
            this.type = type;
            this.index = reversed ? capacity - 1 : 0;
            this.reversed = reversed;
        }

        public Builder<E> add(E value) {
            array[index] = value;
            if (reversed)
                index--;
            else
                index++;
            size++;
            return this;
        }

        public List<E> build() {
            E[] result = (E[]) Array.newInstance(type, size);
            if (reversed) {
                System.arraycopy(array, array.length - size, result, 0, size);
            } else {
                System.arraycopy(array, 0, result, 0, size);
            }
            return new StaticList<>(result);
        }
    }
}

