package me.catst0day.capi.Utils;

import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.util.*;
import java.util.function.Predicate;

public class CAPIArrayList<E> extends AbstractList<E>
        implements List<E>, RandomAccess, Cloneable, java.io.Serializable {

    @Serial
    private static final long serialVersionUID = 8683452581122892189L;
    private static final int DEFAULT_CAPACITY = 10;
    private static final Object[] EMPTY_ELEMENTDATA = {};
    private static final Object[] DEFAULTCAPACITY_EMPTY_ELEMENTDATA = {};

    transient Object[] elementData;
    private int size;

    public CAPIArrayList(int initialCapacity) {
        if (initialCapacity > 0) {
            this.elementData = new Object[initialCapacity];
        } else if (initialCapacity == 0) {
            this.elementData = EMPTY_ELEMENTDATA;
        } else {
            throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
        }
    }

    public CAPIArrayList() {
        this.elementData = DEFAULTCAPACITY_EMPTY_ELEMENTDATA;
    }

    public CAPIArrayList(Collection<? extends E> c) {
        Object[] a = c.toArray();
        if ((size = a.length) != 0) {
            if (c.getClass() == CAPIArrayList.class) {
                elementData = a;
            } else {
                elementData = Arrays.copyOf(a, size, Object[].class);
            }
        } else {
            elementData = EMPTY_ELEMENTDATA;
        }
    }

    public void reverse() {
        int left = 0;
        int right = size - 1;
        while (left < right) {
            Object temp = elementData[left];
            elementData[left] = elementData[right];
            elementData[right] = temp;
            left++;
            right--;
        }
        modCount++;
    }

    public E getRandom() {
        if (size == 0) return null;
        return elementData(ThreadLocalRandom.current().nextInt(size));
    }

    public void removeIfIndices(Predicate<Integer> filter) {
        Objects.requireNonNull(filter);
        int writeIndex = 0;
        for (int readIndex = 0; readIndex < size; readIndex++) {
            if (!filter.test(readIndex)) {
                elementData[writeIndex++] = elementData[readIndex];
            }
        }
        for (int i = writeIndex; i < size; i++) {
            elementData[i] = null;
        }
        if (size != writeIndex) {
            size = writeIndex;
            modCount++;
        }
    }

    public void shuffle() {
        Random rnd = ThreadLocalRandom.current();
        for (int i = size - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);
            Object temp = elementData[index];
            elementData[index] = elementData[i];
            elementData[i] = temp;
        }
        modCount++;
    }

    public void trimToSize() {
        modCount++;
        if (size < elementData.length) {
            elementData = (size == 0)
                    ? EMPTY_ELEMENTDATA
                    : Arrays.copyOf(elementData, size);
        }
    }

    public void ensureCapacity(int minCapacity) {
        if (minCapacity > elementData.length
                && !(elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA
                && minCapacity <= DEFAULT_CAPACITY)) {
            modCount++;
            grow(minCapacity);
        }
    }

    private Object[] grow(int minCapacity) {
        int oldCapacity = elementData.length;
        if (oldCapacity > 0 || elementData != DEFAULTCAPACITY_EMPTY_ELEMENTDATA) {
            int newCapacity = oldCapacity + (oldCapacity >> 1);
            if (newCapacity - minCapacity < 0) newCapacity = minCapacity;
            return elementData = Arrays.copyOf(elementData, newCapacity);
        } else {
            return elementData = new Object[Math.max(DEFAULT_CAPACITY, minCapacity)];
        }
    }

    private Object[] grow() {
        return grow(size + 1);
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean contains(Object o) {
        return indexOf(o) >= 0;
    }

    public int indexOf(Object o) {
        Object[] es = elementData;
        if (o == null) {
            for (int i = 0; i < size; i++) if (es[i] == null) return i;
        } else {
            for (int i = 0; i < size; i++) if (o.equals(es[i])) return i;
        }
        return -1;
    }

    public int lastIndexOf(Object o) {
        Object[] es = elementData;
        if (o == null) {
            for (int i = size - 1; i >= 0; i--) if (es[i] == null) return i;
        } else {
            for (int i = size - 1; i >= 0; i--) if (o.equals(es[i])) return i;
        }
        return -1;
    }

    public Object clone() {
        try {
            CAPIArrayList<?> v = (CAPIArrayList<?>) super.clone();
            v.elementData = Arrays.copyOf(elementData, size);
            v.modCount = 0;
            return v;
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }

    public Object @NotNull [] toArray() {
        return Arrays.copyOf(elementData, size);
    }

    @SuppressWarnings("unchecked")
    public <T> T @NotNull [] toArray(T[] a) {
        if (a.length < size)
            return (T[]) Arrays.copyOf(elementData, size, a.getClass());
        System.arraycopy(elementData, 0, a, 0, size);
        if (a.length > size) a[size] = null;
        return a;
    }

    @SuppressWarnings("unchecked")
    E elementData(int index) {
        return (E) elementData[index];
    }

    public E get(int index) {
        Objects.checkIndex(index, size);
        return elementData(index);
    }

    public E set(int index, E element) {
        Objects.checkIndex(index, size);
        E oldValue = elementData(index);
        elementData[index] = element;
        return oldValue;
    }

    public boolean add(E e) {
        modCount++;
        if (size == elementData.length)
            elementData = grow();
        elementData[size] = e;
        size++;
        return true;
    }

    public void add(int index, E element) {
        if (index > size || index < 0)
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        modCount++;
        if (size == elementData.length)
            elementData = grow();
        System.arraycopy(elementData, index, elementData, index + 1, size - index);
        elementData[index] = element;
        size++;
    }

    public E remove(int index) {
        Objects.checkIndex(index, size);
        modCount++;
        E oldValue = elementData(index);
        int numMoved = size - index - 1;
        if (numMoved > 0)
            System.arraycopy(elementData, index + 1, elementData, index, numMoved);
        elementData[--size] = null;
        return oldValue;
    }

    public boolean remove(Object o) {
        int index = indexOf(o);
        if (index >= 0) {
            remove(index);
            return true;
        }
        return false;
    }

    public void clear() {
        modCount++;
        for (int i = 0; i < size; i++)
            elementData[i] = null;
        size = 0;
    }
}

final class ThreadLocalRandom {
    private static final Random INSTANCE = new Random();
    public static Random current() {
        return INSTANCE;
    }
}
