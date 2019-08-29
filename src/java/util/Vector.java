/*
 * Copyright (c) 1994, 2017, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package java.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

/**
 * The {@code Vector} class implements a growable array of
 * objects. Like an array, it contains components that can be
 * accessed using an integer index. However, the size of a
 * {@code Vector} can grow or shrink as needed to accommodate
 * adding and removing items after the {@code Vector} has been created.
 *
 * <p>Each vector tries to optimize storage management by maintaining a
 * {@code capacity} and a {@code capacityIncrement}. The
 * {@code capacity} is always at least as large as the vector
 * size; it is usually larger because as components are added to the
 * vector, the vector's storage increases in chunks the size of
 * {@code capacityIncrement}. An application can increase the
 * capacity of a vector before inserting a large number of
 * components; this reduces the amount of incremental reallocation.
 *
 * <p><a name="fail-fast">
 * The iterators returned by this class's {@link #iterator() iterator} and
 * {@link #listIterator(int) listIterator} methods are <em>fail-fast</em></a>:
 * if the vector is structurally modified at any time after the iterator is
 * created, in any way except through the iterator's own
 * {@link ListIterator#remove() remove} or
 * {@link ListIterator#add(Object) add} methods, the iterator will throw a
 * {@link ConcurrentModificationException}.  Thus, in the face of
 * concurrent modification, the iterator fails quickly and cleanly, rather
 * than risking arbitrary, non-deterministic behavior at an undetermined
 * time in the future.  The {@link Enumeration Enumerations} returned by
 * the {@link #elements() elements} method are <em>not</em> fail-fast.
 *
 * <p>Note that the fail-fast behavior of an iterator cannot be guaranteed
 * as it is, generally speaking, impossible to make any hard guarantees in the
 * presence of unsynchronized concurrent modification.  Fail-fast iterators
 * throw {@code ConcurrentModificationException} on a best-effort basis.
 * Therefore, it would be wrong to write a program that depended on this
 * exception for its correctness:  <i>the fail-fast behavior of iterators
 * should be used only to detect bugs.</i>
 *
 * <p>As of the Java 2 platform v1.2, this class was retrofitted to
 * implement the {@link List} interface, making it a member of the
 * <a href="{@docRoot}/../technotes/guides/collections/index.html">
 * Java Collections Framework</a>.  Unlike the new collection
 * implementations, {@code Vector} is synchronized.  If a thread-safe
 * implementation is not needed, it is recommended to use {@link
 * ArrayList} in place of {@code Vector}.
 *
 * @author  Lee Boynton
 * @author  Jonathan Payne
 * @see Collection
 * @see LinkedList
 * @since   JDK1.0
 */
public class Vector<E>
    extends AbstractList<E>
    implements List<E>, RandomAccess, Cloneable, java.io.Serializable
{
    /**
     * The array buffer into which the components of the vector are
     * stored. The capacity of the vector is the length of this array buffer,
     * and is at least large enough to contain all the vector's elements.
     *
     * <p>Any array elements following the last element in the Vector are null.
     *
     * @serial
     */
    // 存放元素的数组 elementData[]
    protected Object[] elementData;

    /**
     * The number of valid components in this {@code Vector} object.
     * Components {@code elementData[0]} through
     * {@code elementData[elementCount-1]} are the actual items.
     *
     * @serial
     */
    // 已经放入数组中的元素的个数
    protected int elementCount;

    /**
     * The amount by which the capacity of the vector is automatically
     * incremented when its size becomes greater than its capacity.  If
     * the capacity increment is less than or equal to zero, the capacity
     * of the vector is doubled each time it needs to grow.
     *
     * @serial
     */
    // vector 的 capacity 每次增长的数量，为 0 时，增长一倍
    protected int capacityIncrement;

    /** use serialVersionUID from JDK 1.0.2 for interoperability */
    // 序列化 ID
    private static final long serialVersionUID = -2767605614048989439L;

    /**
     * Constructs an empty vector with the specified initial capacity and
     * capacity increment.
     *
     * @param   initialCapacity     the initial capacity of the vector
     * @param   capacityIncrement   the amount by which the capacity is
     *                              increased when the vector overflows
     * @throws IllegalArgumentException if the specified initial capacity
     *         is negative
     */
    // 根据 initialCapacity 和 capacityIncrement 创建 Vector 对象
    // 即根据指定参数构造器创建 Vector 对象
    public Vector(int initialCapacity, int capacityIncrement) {
        super();
        // initialCapacity < 0，则抛出 IllegalArgumentException()
        if (initialCapacity < 0)
            throw new IllegalArgumentException("Illegal Capacity: "+
                                               initialCapacity);
        // 创建一个指定 initialCapacity 长度的 Object[] 数组，初始化 elementData[]
        this.elementData = new Object[initialCapacity];
        // 初始化 capacityIncrement 的值
        this.capacityIncrement = capacityIncrement;
    }

    /**
     * Constructs an empty vector with the specified initial capacity and
     * with its capacity increment equal to zero.
     *
     * @param   initialCapacity   the initial capacity of the vector
     * @throws IllegalArgumentException if the specified initial capacity
     *         is negative
     */
    // 根据指定参数构造器创建 Vector 对象，即根据 initialCapacity 创建 Vector 对象
    public Vector(int initialCapacity) {
        // 默认 capacityIncrement 为 0，即每次进行扩容的时候会增长一倍的 Vector 的容量
        this(initialCapacity, 0);
    }

    /**
     * Constructs an empty vector so that its internal data array
     * has size {@code 10} and its standard capacity increment is
     * zero.
     */
    // 使用默认的构造器创建 Vector 对象，默认 initialCapacity 的初始值为 10
    public Vector() {
        this(10);
    }

    /**
     * Constructs a vector containing the elements of the specified
     * collection, in the order they are returned by the collection's
     * iterator.
     *
     * @param c the collection whose elements are to be placed into this
     *       vector
     * @throws NullPointerException if the specified collection is null
     * @since   1.2
     */
    // 根据指定的集合创建 Vector 对象
    public Vector(Collection<? extends E> c) {
        // 将集合 c 转成 Object[] 数组赋值给 elementData[]，
        // 集合的 toArray() 默认是在 AbstractCollection 中使用 iterator 实现的
        elementData = c.toArray();
        // 获取 elementData[] 中元素的个数，这里是因为上面的 toArray 会进行缩容操作
        elementCount = elementData.length;
        // c.toArray might (incorrectly) not return Object[] (see 6260652)
        // 如果 elementData[] 的数据类型不为 Object[] 类型，这个是由于继承，然后向上转型导致的，创建出来的父类类型其实还是子类类型
        if (elementData.getClass() != Object[].class)
            // 将 elementData[] 中的元素转成 Object 类型
            elementData = Arrays.copyOf(elementData, elementCount, Object[].class);
    }

    /**
     * Copies the components of this vector into the specified array.
     * The item at index {@code k} in this vector is copied into
     * component {@code k} of {@code anArray}.
     *
     * @param  anArray the array into which the components get copied
     * @throws NullPointerException if the given array is null
     * @throws IndexOutOfBoundsException if the specified array is not
     *         large enough to hold all the components of this vector
     * @throws ArrayStoreException if a component of this vector is not of
     *         a runtime type that can be stored in the specified array
     * @see #toArray(Object[])
     */
    // 同步方法，将 elementData[] 中的元素全部 copy 到 anArray 数组中
    public synchronized void copyInto(Object[] anArray) {
        System.arraycopy(elementData, 0, anArray, 0, elementCount);
    }

    /**
     * Trims the capacity of this vector to be the vector's current
     * size. If the capacity of this vector is larger than its current
     * size, then the capacity is changed to equal the size by replacing
     * its internal data array, kept in the field {@code elementData},
     * with a smaller one. An application can use this operation to
     * minimize the storage of a vector.
     */
    // 同步方法，将 Vector 的 capacity 设置为 elementData[] 中实际元素个数
    // 即将 elementData[] 中多余的长度截取不要
    public synchronized void trimToSize() {
        // modCount++
        modCount++;
        // 获取 trim 之前的 Vector 的 capacity，即 elementData[] 的 length
        int oldCapacity = elementData.length;
        // elementCount 即 vector 中实际元素的个数，小于 oldCapacity
        if (elementCount < oldCapacity) {
            // 将 elementData[] 中指定长度的元素提取出来
            elementData = Arrays.copyOf(elementData, elementCount);
        }
    }

    /**
     * Increases the capacity of this vector, if necessary, to ensure
     * that it can hold at least the number of components specified by
     * the minimum capacity argument.
     *
     * <p>If the current capacity of this vector is less than
     * {@code minCapacity}, then its capacity is increased by replacing its
     * internal data array, kept in the field {@code elementData}, with a
     * larger one.  The size of the new data array will be the old size plus
     * {@code capacityIncrement}, unless the value of
     * {@code capacityIncrement} is less than or equal to zero, in which case
     * the new capacity will be twice the old capacity; but if this new size
     * is still smaller than {@code minCapacity}, then the new capacity will
     * be {@code minCapacity}.
     *
     * @param minCapacity the desired minimum capacity
     */
    // 同步方法，对 vector 进行扩容操作，minCapacity 需要的最小容量
    public synchronized void ensureCapacity(int minCapacity) {
        if (minCapacity > 0) {
            // 扩容前 modCount++
            modCount++;
            ensureCapacityHelper(minCapacity);
        }
    }

    /**
     * This implements the unsynchronized semantics of ensureCapacity.
     * Synchronized methods in this class can internally call this
     * method for ensuring capacity without incurring the cost of an
     * extra synchronization.
     *
     * @see #ensureCapacity(int)
     */
    // 扩容
    private void ensureCapacityHelper(int minCapacity) {
        // overflow-conscious code
        // 需要的最小容量 minCapacity > elementData[].length，即需要的最小容量大于已有的容量时
        if (minCapacity - elementData.length > 0)
            // 进行扩容操作
            grow(minCapacity);
    }

    /**
     * The maximum size of array to allocate.
     * Some VMs reserve some header words in an array.
     * Attempts to allocate larger arrays may result in
     * OutOfMemoryError: Requested array size exceeds VM limit
     */
    // 数组默认分配的最大值，Integer.MAX_VALUE - 8，因为一些 JVM 会保留数组的头信息，所以一般会预留 8 个字节
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    // 根据 minCapacity 大小进行扩容操作
    private void grow(int minCapacity) {
        // overflow-conscious code
        // 获取 elementData[].length，即 oldCapacity
        int oldCapacity = elementData.length;
        // 计算新容量，如果 capacityIncrement > 0，则扩容指定增长的容量，否则将 oldCapacity 增加一倍
        int newCapacity = oldCapacity + ((capacityIncrement > 0) ?
                                         capacityIncrement : oldCapacity);
        // 如果扩容后的 newCapacity < minCapacity，那么直接将 minCapacity 作为新的容量
        if (newCapacity - minCapacity < 0)
            newCapacity = minCapacity;
        // 如果 newCapacity > MAX_ARRAY_SIZE，则启用更大的空间容量
        if (newCapacity - MAX_ARRAY_SIZE > 0)
            newCapacity = hugeCapacity(minCapacity);
        // 将 elementData[] 中的元素复制到指定长度的数组中
        elementData = Arrays.copyOf(elementData, newCapacity);
    }

    // 启用更大的空间容量
    private static int hugeCapacity(int minCapacity) {
        // 对 minCapacity 进行合法检验，如果 minCapacity < 0，则表示值已经溢出，抛出 OutOfMemoryError()
        if (minCapacity < 0) // overflow
            throw new OutOfMemoryError();
        // 否则根据 minCapacity 返回对应的大小的容量
        return (minCapacity > MAX_ARRAY_SIZE) ?
            Integer.MAX_VALUE :
            MAX_ARRAY_SIZE;
    }

    /**
     * Sets the size of this vector. If the new size is greater than the
     * current size, new {@code null} items are added to the end of
     * the vector. If the new size is less than the current size, all
     * components at index {@code newSize} and greater are discarded.
     *
     * @param  newSize   the new size of this vector
     * @throws ArrayIndexOutOfBoundsException if the new size is negative
     */
    // 同步方法，设置 vector 的 size 大小
    public synchronized void setSize(int newSize) {
        // modCount++
        modCount++;
        // 如果 newSize > elementCount
        if (newSize > elementCount) {
            // 则对 vector 进行扩容操作，确保 vector 的 capacity 能满足 newSize 的大小
            ensureCapacityHelper(newSize);
        } else {
            // 否则，将 newSize 后面的所有元素设置为 null，等待 gc 的处理
            for (int i = newSize ; i < elementCount ; i++) {
                // 将 newSize 后面的元素全部设置为 null
                elementData[i] = null;
            }
        }
        // 更新 elementCount 为 newSize
        elementCount = newSize;
    }

    /**
     * Returns the current capacity of this vector.
     *
     * @return  the current capacity (the length of its internal
     *          data array, kept in the field {@code elementData}
     *          of this vector)
     */
    // 同步方法，获取 vector 的 capacity，即 elementData[] 的 length
    public synchronized int capacity() {
        return elementData.length;
    }

    /**
     * Returns the number of components in this vector.
     *
     * @return  the number of components in this vector
     */
    // synchronized 方法，获取 vector 的元素个数，即 elementCount 的值
    public synchronized int size() {
        return elementCount;
    }

    /**
     * Tests if this vector has no components.
     *
     * @return  {@code true} if and only if this vector has
     *          no components, that is, its size is zero;
     *          {@code false} otherwise.
     */
    // synchronized 方法，判断 vector 是否为空，即 elementCount 是否为 0
    public synchronized boolean isEmpty() {
        return elementCount == 0;
    }

    /**
     * Returns an enumeration of the components of this vector. The
     * returned {@code Enumeration} object will generate all items in
     * this vector. The first item generated is the item at index {@code 0},
     * then the item at index {@code 1}, and so on.
     *
     * @return  an enumeration of the components of this vector
     * @see     Iterator
     */
    // 返回 vector 中全部元素对应的 Enumeration，枚举迭代器，枚举迭代 Vector
    public Enumeration<E> elements() {
        // 匿名内部类实现
        return new Enumeration<E>() {
            // 索引值
            int count = 0;

            // count < elementCount，表明索引和面还有元素
            public boolean hasMoreElements() {
                return count < elementCount;
            }

            // 返回索引后面的元素，同时将索引后移一位
            public E nextElement() {
                // synchronized Vector
                synchronized (Vector.this) {
                    if (count < elementCount) {
                        return elementData(count++);
                    }
                }
                // 否则抛出 NoSuchElementException()
                throw new NoSuchElementException("Vector Enumeration");
            }
        };
    }

    /**
     * Returns {@code true} if this vector contains the specified element.
     * More formally, returns {@code true} if and only if this vector
     * contains at least one element {@code e} such that
     * <tt>(o==null&nbsp;?&nbsp;e==null&nbsp;:&nbsp;o.equals(e))</tt>.
     *
     * @param o element whose presence in this vector is to be tested
     * @return {@code true} if this vector contains the specified element
     */
    // 判断 vector 中是否还有元素 o，即从开始位置遍历查找，比较查找元素第一次出现位置的索引的值与 0 的大小
    public boolean contains(Object o) {
        return indexOf(o, 0) >= 0;
    }

    /**
     * Returns the index of the first occurrence of the specified element
     * in this vector, or -1 if this vector does not contain the element.
     * More formally, returns the lowest index {@code i} such that
     * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>,
     * or -1 if there is no such index.
     *
     * @param o element to search for
     * @return the index of the first occurrence of the specified element in
     *         this vector, or -1 if this vector does not contain the element
     */
    // 返回指定元素在 vector 第一次出现的位置
    public int indexOf(Object o) {
        return indexOf(o, 0);
    }

    /**
     * Returns the index of the first occurrence of the specified element in
     * this vector, searching forwards from {@code index}, or returns -1 if
     * the element is not found.
     * More formally, returns the lowest index {@code i} such that
     * <tt>(i&nbsp;&gt;=&nbsp;index&nbsp;&amp;&amp;&nbsp;(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i))))</tt>,
     * or -1 if there is no such index.
     *
     * @param o element to search for
     * @param index index to start searching from
     * @return the index of the first occurrence of the element in
     *         this vector at position {@code index} or later in the vector;
     *         {@code -1} if the element is not found.
     * @throws IndexOutOfBoundsException if the specified index is negative
     * @see     Object#equals(Object)
     */
    // synchronized 方法，从指定 index 位置开始查找 Object o 出现的位置，并返回 o 第一次出现的位置
    public synchronized int indexOf(Object o, int index) {
        if (o == null) { // 如果 o == null，则遍历 vector 的 index 后面的元素进行 == 匹配
            for (int i = index ; i < elementCount ; i++)
                // 找到后返回索引的值
                if (elementData[i]==null)
                    return i;
        } else { // 如果 o 不为 null，则遍历 vector 的 index 后面的元素进行 equals() 比较
            for (int i = index ; i < elementCount ; i++)
                // 找到后返回索引的值
                if (o.equals(elementData[i]))
                    return i;
        }
        // 没找到则返回 -1
        return -1;
    }

    /**
     * Returns the index of the last occurrence of the specified element
     * in this vector, or -1 if this vector does not contain the element.
     * More formally, returns the highest index {@code i} such that
     * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>,
     * or -1 if there is no such index.
     *
     * @param o element to search for
     * @return the index of the last occurrence of the specified element in
     *         this vector, or -1 if this vector does not contain the element
     */
    // sycchronized 方法，获取元素最后一次出现在 vector 的位置
    public synchronized int lastIndexOf(Object o) {
        return lastIndexOf(o, elementCount-1);
    }

    /**
     * Returns the index of the last occurrence of the specified element in
     * this vector, searching backwards from {@code index}, or returns -1 if
     * the element is not found.
     * More formally, returns the highest index {@code i} such that
     * <tt>(i&nbsp;&lt;=&nbsp;index&nbsp;&amp;&amp;&nbsp;(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i))))</tt>,
     * or -1 if there is no such index.
     *
     * @param o element to search for
     * @param index index to start searching backwards from
     * @return the index of the last occurrence of the element at position
     *         less than or equal to {@code index} in this vector;
     *         -1 if the element is not found.
     * @throws IndexOutOfBoundsException if the specified index is greater
     *         than or equal to the current size of this vector
     */
    // synchronized 方法，从指定 index 位置向前查找元素第一次出现的位置
    public synchronized int lastIndexOf(Object o, int index) {
        // 对 index 的值进行一个边界校验，否则抛出 IndexOutOfBoundsException()
        if (index >= elementCount)
            throw new IndexOutOfBoundsException(index + " >= "+ elementCount);

        // 如果元素为 null，则从 index 位置开始向前遍历使用 == 进行比较
        if (o == null) {
            for (int i = index; i >= 0; i--)
                // 找到后返回索引的值
                if (elementData[i]==null)
                    return i;
        } else {
            // 如果元素不为 null，则从 index 位置开始向前遍历使用 equals() 进行比较
            for (int i = index; i >= 0; i--)
                // 找到后返回索引的值
                if (o.equals(elementData[i]))
                    return i;
        }
        // 没找到的话返回 -1
        return -1;
    }

    /**
     * Returns the component at the specified index.
     *
     * <p>This method is identical in functionality to the {@link #get(int)}
     * method (which is part of the {@link List} interface).
     *
     * @param      index   an index into this vector
     * @return     the component at the specified index
     * @throws ArrayIndexOutOfBoundsException if the index is out of range
     *         ({@code index < 0 || index >= size()})
     */
    // synchronized 方法，获取指定索引位置的元素的值
    public synchronized E elementAt(int index) {
        // 对 index 的值进行边界条件的检查，会抛出 ArrayIndexOutOfBoundsException()
        if (index >= elementCount) {
            throw new ArrayIndexOutOfBoundsException(index + " >= " + elementCount);
        }

        // 返回指定索引位置的元素
        return elementData(index);
    }

    /**
     * Returns the first component (the item at index {@code 0}) of
     * this vector.
     *
     * @return     the first component of this vector
     * @throws NoSuchElementException if this vector has no components
     */
    // synchronzied 方法，返回第一个元素
    public synchronized E firstElement() {
        // elementCount == 0，则抛出 NoSuchElementException()
        if (elementCount == 0) {
            throw new NoSuchElementException();
        }
        // 返回第一个元素，即索引为 0 的元素
        return elementData(0);
    }

    /**
     * Returns the last component of the vector.
     *
     * @return  the last component of the vector, i.e., the component at index
     *          <code>size()&nbsp;-&nbsp;1</code>.
     * @throws NoSuchElementException if this vector is empty
     */
    // synchronized 方法，返回最后一个元素
    public synchronized E lastElement() {
        // elementCount == 0，抛出 NoSuchElementException()
        if (elementCount == 0) {
            throw new NoSuchElementException();
        }
        // 返回 vector 的最后一个元素，即索引值为 elementCount - 1 的元素
        return elementData(elementCount - 1);
    }

    /**
     * Sets the component at the specified {@code index} of this
     * vector to be the specified object. The previous component at that
     * position is discarded.
     *
     * <p>The index must be a value greater than or equal to {@code 0}
     * and less than the current size of the vector.
     *
     * <p>This method is identical in functionality to the
     * {@link #set(int, Object) set(int, E)}
     * method (which is part of the {@link List} interface). Note that the
     * {@code set} method reverses the order of the parameters, to more closely
     * match array usage.  Note also that the {@code set} method returns the
     * old value that was stored at the specified position.
     *
     * @param      obj     what the component is to be set to
     * @param      index   the specified index
     * @throws ArrayIndexOutOfBoundsException if the index is out of range
     *         ({@code index < 0 || index >= size()})
     */
    // synchronized 方法，更新指定索引位置的元素
    public synchronized void setElementAt(E obj, int index) {
        // 对 index 进行边界校验，会抛出 ArrayIndexOutOfBoundsException()
        if (index >= elementCount) {
            throw new ArrayIndexOutOfBoundsException(index + " >= " +
                                                     elementCount);
        }
        // 修改指定索引位置的元素
        elementData[index] = obj;
    }

    /**
     * Deletes the component at the specified index. Each component in
     * this vector with an index greater or equal to the specified
     * {@code index} is shifted downward to have an index one
     * smaller than the value it had previously. The size of this vector
     * is decreased by {@code 1}.
     *
     * <p>The index must be a value greater than or equal to {@code 0}
     * and less than the current size of the vector.
     *
     * <p>This method is identical in functionality to the {@link #remove(int)}
     * method (which is part of the {@link List} interface).  Note that the
     * {@code remove} method returns the old value that was stored at the
     * specified position.
     *
     * @param      index   the index of the object to remove
     * @throws ArrayIndexOutOfBoundsException if the index is out of range
     *         ({@code index < 0 || index >= size()})
     */
    // synchronized 方法，移除指定索引位置的元素
    public synchronized void removeElementAt(int index) {
        // modCount++
        modCount++;
        // 对 index 进行边界条件的检验，可能会抛出 ArrayIndexOutOfBoundsException()
        if (index >= elementCount) {
            throw new ArrayIndexOutOfBoundsException(index + " >= " +
                                                     elementCount);
        }
        else if (index < 0) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        // 计算要向前移动的元素个数
        int j = elementCount - index - 1;
        if (j > 0) {
            // 将 index + 1 位置开始的元素向前移动一位
            System.arraycopy(elementData, index + 1, elementData, index, j);
        }
        // elementCount--
        elementCount--;
        // 并将 elementData[] 中 elementCount 索引位置的元素置为 null，等待 gc 进行回收操作
        elementData[elementCount] = null; /* to let gc do its work */
    }

    /**
     * Inserts the specified object as a component in this vector at the
     * specified {@code index}. Each component in this vector with
     * an index greater or equal to the specified {@code index} is
     * shifted upward to have an index one greater than the value it had
     * previously.
     *
     * <p>The index must be a value greater than or equal to {@code 0}
     * and less than or equal to the current size of the vector. (If the
     * index is equal to the current size of the vector, the new element
     * is appended to the Vector.)
     *
     * <p>This method is identical in functionality to the
     * {@link #add(int, Object) add(int, E)}
     * method (which is part of the {@link List} interface).  Note that the
     * {@code add} method reverses the order of the parameters, to more closely
     * match array usage.
     *
     * @param      obj     the component to insert
     * @param      index   where to insert the new component
     * @throws ArrayIndexOutOfBoundsException if the index is out of range
     *         ({@code index < 0 || index > size()})
     */
    // synchronized 方法，在指定索引位置插入元素
    public synchronized void insertElementAt(E obj, int index) {
        // modCount++
        modCount++;
        // 对 index 进行边界条件检查，可能会抛出 ArrayIndexOutOfBoundsException()
        if (index > elementCount) {
            throw new ArrayIndexOutOfBoundsException(index
                                                     + " > " + elementCount);
        }
        // 确保容量够用，不够的话进行扩容操作
        ensureCapacityHelper(elementCount + 1);
        // 将 elementData[] 的 index 位置开始长度为 elementCount - index 的元素全部向后移一位
        System.arraycopy(elementData, index, elementData, index + 1, elementCount - index);
        // 更新 index 位置的值
        elementData[index] = obj;
        // elementCount++
        elementCount++;
    }

    /**
     * Adds the specified component to the end of this vector,
     * increasing its size by one. The capacity of this vector is
     * increased if its size becomes greater than its capacity.
     *
     * <p>This method is identical in functionality to the
     * {@link #add(Object) add(E)}
     * method (which is part of the {@link List} interface).
     *
     * @param   obj   the component to be added
     */
    // synchronized 方法，添加元素
    public synchronized void addElement(E obj) {
        // modCount++
        modCount++;
        // 确保容量够用
        ensureCapacityHelper(elementCount + 1);
        // 在 vector 的末尾添加元素
        elementData[elementCount++] = obj;
    }

    /**
     * Removes the first (lowest-indexed) occurrence of the argument
     * from this vector. If the object is found in this vector, each
     * component in the vector with an index greater or equal to the
     * object's index is shifted downward to have an index one smaller
     * than the value it had previously.
     *
     * <p>This method is identical in functionality to the
     * {@link #remove(Object)} method (which is part of the
     * {@link List} interface).
     *
     * @param   obj   the component to be removed
     * @return  {@code true} if the argument was a component of this
     *          vector; {@code false} otherwise.
     */
    // synchronized 方法，移除第一次找到的元素
    public synchronized boolean removeElement(Object obj) {
        // modCount++
        modCount++;
        // 获取元素第一次出现的位置
        int i = indexOf(obj);
        // 找到元素后
        if (i >= 0) {
            // 移除指定索引位置的元素，并返回 true
            removeElementAt(i);
            return true;
        }
        // 否则返回 false
        return false;
    }

    /**
     * Removes all components from this vector and sets its size to zero.
     *
     * <p>This method is identical in functionality to the {@link #clear}
     * method (which is part of the {@link List} interface).
     */
    // synchronized 方法，移除全部元素
    public synchronized void removeAllElements() {
        // modCount++
        modCount++;
        // Let gc do its work
        // 将所有元素设置为 null，等待 gc 进行内存回收操作
        for (int i = 0; i < elementCount; i++)
            elementData[i] = null;

        // 更新 elementCount 的值为 0
        elementCount = 0;
    }

    /**
     * Returns a clone of this vector. The copy will contain a
     * reference to a clone of the internal data array, not a reference
     * to the original internal data array of this {@code Vector} object.
     *
     * @return  a clone of this vector
     */
    // synchronized 方法，重写 Object 的 clone 方法，浅克隆
    public synchronized Object clone() {
        try {
            @SuppressWarnings("unchecked")
                    // 调用 Object 的 clone 方法 clone 出一个 Vector 对象
                Vector<E> v = (Vector<E>) super.clone();
            // 将 elementData[] 中的元素赋值到 Vector 中，对于对象而言，vector 中存放的是对象的引用，所以这里是浅克隆
            v.elementData = Arrays.copyOf(elementData, elementCount);
            // 设置 vector 的 modCount 为 0
            v.modCount = 0;
            // 返回 clone 的 vector 对象
            return v;
        } catch (CloneNotSupportedException e) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError(e);
        }
    }

    /**
     * Returns an array containing all of the elements in this Vector
     * in the correct order.
     *
     * @since 1.2
     */
    // 重写 toArray() 方法，将 vector 转换成 Object[]
    public synchronized Object[] toArray() {
        // 即将 elementData[] 中的全部元素复制到新的数组中
        return Arrays.copyOf(elementData, elementCount);
    }

    /**
     * Returns an array containing all of the elements in this Vector in the
     * correct order; the runtime type of the returned array is that of the
     * specified array.  If the Vector fits in the specified array, it is
     * returned therein.  Otherwise, a new array is allocated with the runtime
     * type of the specified array and the size of this Vector.
     *
     * <p>If the Vector fits in the specified array with room to spare
     * (i.e., the array has more elements than the Vector),
     * the element in the array immediately following the end of the
     * Vector is set to null.  (This is useful in determining the length
     * of the Vector <em>only</em> if the caller knows that the Vector
     * does not contain any null elements.)
     *
     * @param a the array into which the elements of the Vector are to
     *          be stored, if it is big enough; otherwise, a new array of the
     *          same runtime type is allocated for this purpose.
     * @return an array containing the elements of the Vector
     * @throws ArrayStoreException if the runtime type of a is not a supertype
     * of the runtime type of every element in this Vector
     * @throws NullPointerException if the given array is null
     * @since 1.2
     */
    @SuppressWarnings("unchecked")
    // synchronized 方法，将 vector 转换成指定类型的数组
    public synchronized <T> T[] toArray(T[] a) {
        // 对 a[] 数组的长度进行判断
        if (a.length < elementCount)
            // 如果 a.length < elementCount，则将 elementData[] 中的元素全部转换成 a[] 类型，创建一个新数组并返回
            return (T[]) Arrays.copyOf(elementData, elementCount, a.getClass());

        // 否则将 elementData[] 中的元素全部 copy 到 a[] 中
        System.arraycopy(elementData, 0, a, 0, elementCount);

        // 如果 a.length > elementCount，则将 a[elementCount] 设置为 null
        if (a.length > elementCount)
            // 如果 elementData[] 中没有 null 值的话，这样处理可以保留 vector 的基本信息
            a[elementCount] = null;

        // 返回转换后的数组 a[]
        return a;
    }

    // Positional Access Operations

    @SuppressWarnings("unchecked")
    // 根据指定索引获取元素
    E elementData(int index) {
        return (E) elementData[index];
    }

    /**
     * Returns the element at the specified position in this Vector.
     *
     * @param index index of the element to return
     * @return object at the specified index
     * @throws ArrayIndexOutOfBoundsException if the index is out of range
     *            ({@code index < 0 || index >= size()})
     * @since 1.2
     */
    // synchronized 方法，根据指定索引获取元素
    public synchronized E get(int index) {
        // 对 index 进行边界条件的检查，可能会抛出 ArrayIndexOutOfBoundsException()
        if (index >= elementCount)
            throw new ArrayIndexOutOfBoundsException(index);

        // 返回指定索引位置的元素
        return elementData(index);
    }

    /**
     * Replaces the element at the specified position in this Vector with the
     * specified element.
     *
     * @param index index of the element to replace
     * @param element element to be stored at the specified position
     * @return the element previously at the specified position
     * @throws ArrayIndexOutOfBoundsException if the index is out of range
     *         ({@code index < 0 || index >= size()})
     * @since 1.2
     */
    // synchronized 方法，更新指定索引位置的元素, 并返回修改前的元素
    public synchronized E set(int index, E element) {
        // 对 index 进行边界条件的判断，可能会抛出 ArrayIndexOutOfBoundsException()
        if (index >= elementCount)
            throw new ArrayIndexOutOfBoundsException(index);

        // 获取修改前元素的值
        E oldValue = elementData(index);
        // 更新指定索引位置的值
        elementData[index] = element;
        // 返回修改之前的元素
        return oldValue;
    }

    /**
     * Appends the specified element to the end of this Vector.
     *
     * @param e element to be appended to this Vector
     * @return {@code true} (as specified by {@link Collection#add})
     * @since 1.2
     */
    // synchronized 方法，在 vector 末尾添加元素
    public synchronized boolean add(E e) {
        // modCount++，因为涉及到 vector 中元素个数的修改
        modCount++;
        // 确保 capacity 满足能够新增元素
        ensureCapacityHelper(elementCount + 1);
        // 在 vector 的末尾添加元素
        elementData[elementCount++] = e;
        // 返回 true
        return true;
    }

    /**
     * Removes the first occurrence of the specified element in this Vector
     * If the Vector does not contain the element, it is unchanged.  More
     * formally, removes the element with the lowest index i such that
     * {@code (o==null ? get(i)==null : o.equals(get(i)))} (if such
     * an element exists).
     *
     * @param o element to be removed from this Vector, if present
     * @return true if the Vector contained the specified element
     * @since 1.2
     */
    // 移除元素，仅移除第一次出现的元素，里面的 removeElement() 方法是同步的，所以这个方法也是同步的
    public boolean remove(Object o) {
        return removeElement(o);
    }

    /**
     * Inserts the specified element at the specified position in this Vector.
     * Shifts the element currently at that position (if any) and any
     * subsequent elements to the right (adds one to their indices).
     *
     * @param index index at which the specified element is to be inserted
     * @param element element to be inserted
     * @throws ArrayIndexOutOfBoundsException if the index is out of range
     *         ({@code index < 0 || index > size()})
     * @since 1.2
     */
    // 在指定索引位置添加元素，里面的 insertElementAt() 方法是 synchronized 的
    public void add(int index, E element) {
        insertElementAt(element, index);
    }

    /**
     * Removes the element at the specified position in this Vector.
     * Shifts any subsequent elements to the left (subtracts one from their
     * indices).  Returns the element that was removed from the Vector.
     *
     * @throws ArrayIndexOutOfBoundsException if the index is out of range
     *         ({@code index < 0 || index >= size()})
     * @param index the index of the element to be removed
     * @return element that was removed
     * @since 1.2
     */
    // synchronized 方法，根据指定索引位置移除元素，返回被删除的元素
    public synchronized E remove(int index) {
        // modCount++
        modCount++;
        // 对 index 进行边界条件检查，可能会抛出 ArrayIndexOutOfBoundsException()
        if (index >= elementCount)
            throw new ArrayIndexOutOfBoundsException(index);
        // 获取指定索引位置的元素
        E oldValue = elementData(index);

        // 计算要移动的元素的个数
        int numMoved = elementCount - index - 1;
        if (numMoved > 0)
            // 将 index 后面的元素全部向前移动一位
            System.arraycopy(elementData, index+1, elementData, index,
                             numMoved);
        // 将 elementData[] 的最后一位元素置为 null，等待 GC 的自动回收
        elementData[--elementCount] = null; // Let gc do its work

        // 返回被删除的元素的值
        return oldValue;
    }

    /**
     * Removes all of the elements from this Vector.  The Vector will
     * be empty after this call returns (unless it throws an exception).
     *
     * @since 1.2
     */
    // 移除全部元素，removeAllElements() 方法是 synchronized
    public void clear() {
        removeAllElements();
    }

    // Bulk Operations

    /**
     * Returns true if this Vector contains all of the elements in the
     * specified Collection.
     *
     * @param   c a collection whose elements will be tested for containment
     *          in this Vector
     * @return true if this Vector contains all of the elements in the
     *         specified collection
     * @throws NullPointerException if the specified collection is null
     */
    // synchronized 方法，判断 vector 中是否含有集合 c 中的全部元素
    public synchronized boolean containsAll(Collection<?> c) {
        // 先遍历集合 c，在遍历集合 vector ，判断集合 c 中的元素是否全部在 vector 中存在
        return super.containsAll(c);
    }

    /**
     * Appends all of the elements in the specified Collection to the end of
     * this Vector, in the order that they are returned by the specified
     * Collection's Iterator.  The behavior of this operation is undefined if
     * the specified Collection is modified while the operation is in progress.
     * (This implies that the behavior of this call is undefined if the
     * specified Collection is this Vector, and this Vector is nonempty.)
     *
     * @param c elements to be inserted into this Vector
     * @return {@code true} if this Vector changed as a result of the call
     * @throws NullPointerException if the specified collection is null
     * @since 1.2
     */
    // synchronized 方法，将集合 c 中的元素全部加入到集合 vector 中
    public synchronized boolean addAll(Collection<? extends E> c) {
        // modCount++
        modCount++;
        // 将集合 c 转成 Object[]
        Object[] a = c.toArray();
        // 获取要增加的元素的个数
        int numNew = a.length;
        // 判断 capacity 是否需要进行扩容操作
        ensureCapacityHelper(elementCount + numNew);
        // 将数组 a[] 中的元素全部添加到 elementData[] 中，从 elementCount 位置开始
        System.arraycopy(a, 0, elementData, elementCount, numNew);
        // 更新 elementCount 的值
        elementCount += numNew;
        // 如果 numNew != 0，则返回 true，否则返回 false
        return numNew != 0;
    }

    /**
     * Removes from this Vector all of its elements that are contained in the
     * specified Collection.
     *
     * @param c a collection of elements to be removed from the Vector
     * @return true if this Vector changed as a result of the call
     * @throws ClassCastException if the types of one or more elements
     *         in this vector are incompatible with the specified
     *         collection
     * (<a href="Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException if this vector contains one or more null
     *         elements and the specified collection does not support null
     *         elements
     * (<a href="Collection.html#optional-restrictions">optional</a>),
     *         or if the specified collection is null
     * @since 1.2
     */
    // synchronized 方法，从 vector 中移除集合 c 中含有的元素
    public synchronized boolean removeAll(Collection<?> c) {
        return super.removeAll(c);
    }

    /**
     * Retains only the elements in this Vector that are contained in the
     * specified Collection.  In other words, removes from this Vector all
     * of its elements that are not contained in the specified Collection.
     *
     * @param c a collection of elements to be retained in this Vector
     *          (all other elements are removed)
     * @return true if this Vector changed as a result of the call
     * @throws ClassCastException if the types of one or more elements
     *         in this vector are incompatible with the specified
     *         collection
     * (<a href="Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException if this vector contains one or more null
     *         elements and the specified collection does not support null
     *         elements
     *         (<a href="Collection.html#optional-restrictions">optional</a>),
     *         or if the specified collection is null
     * @since 1.2
     */
    // synchronized 方法，从 vector 中保留集合 c 中存在的元素
    public synchronized boolean retainAll(Collection<?> c) {
        return super.retainAll(c);
    }

    /**
     * Inserts all of the elements in the specified Collection into this
     * Vector at the specified position.  Shifts the element currently at
     * that position (if any) and any subsequent elements to the right
     * (increases their indices).  The new elements will appear in the Vector
     * in the order that they are returned by the specified Collection's
     * iterator.
     *
     * @param index index at which to insert the first element from the
     *              specified collection
     * @param c elements to be inserted into this Vector
     * @return {@code true} if this Vector changed as a result of the call
     * @throws ArrayIndexOutOfBoundsException if the index is out of range
     *         ({@code index < 0 || index > size()})
     * @throws NullPointerException if the specified collection is null
     * @since 1.2
     */
    // synchronized 方法，从指定 index 位置开始添加集合 c 的全部元素到 vector
    public synchronized boolean addAll(int index, Collection<? extends E> c) {
        // modCount++
        modCount++;
        // 对 index 进行边界条件检查，可能会抛出 ArrayIndexOutOfBoundsException()
        if (index < 0 || index > elementCount)
            throw new ArrayIndexOutOfBoundsException(index);

        // 将集合 c 转成 Object[] 数组
        Object[] a = c.toArray();
        // 计算新增元素的个数
        int numNew = a.length;
        // 计算并判断 capacity 是否需要进行扩容操作
        ensureCapacityHelper(elementCount + numNew);

        // 计算要移动的元素的个数
        int numMoved = elementCount - index;
        if (numMoved > 0)
            // 将 index 开始后面的所有元素全部后移 numNew 位
            System.arraycopy(elementData, index, elementData, index + numNew,
                             numMoved);

        // 将数组 a[] 的全部元素从 elementData[] 的 index 位置开始填充进 vector 中
        System.arraycopy(a, 0, elementData, index, numNew);
        // 更新 elementCount 的值
        elementCount += numNew;
        // 返回是否添加成功
        return numNew != 0;
    }

    /**
     * Compares the specified Object with this Vector for equality.  Returns
     * true if and only if the specified Object is also a List, both Lists
     * have the same size, and all corresponding pairs of elements in the two
     * Lists are <em>equal</em>.  (Two elements {@code e1} and
     * {@code e2} are <em>equal</em> if {@code (e1==null ? e2==null :
     * e1.equals(e2))}.)  In other words, two Lists are defined to be
     * equal if they contain the same elements in the same order.
     *
     * @param o the Object to be compared for equality with this Vector
     * @return true if the specified Object is equal to this Vector
     */
    // synchronized 方法，
    // 先判断内存地址，在判断 List 的子类，在判断集合内元素是否满足元素类型的 equals()，最后判断两个集合是否同时遍历完
    public synchronized boolean equals(Object o) {
        return super.equals(o);
    }

    /**
     * Returns the hash code value for this Vector.
     */
    // synchronized 方法，31的次方乘以每个元素的 hashCode，第一个元素对应的次方更高
    public synchronized int hashCode() {
        return super.hashCode();
    }

    /**
     * Returns a string representation of this Vector, containing
     * the String representation of each element.
     */
    // synchronized 方法，[String.valueOf(e1), String.valueOf(e2), ...]
    public synchronized String toString() {
        return super.toString();
    }

    /**
     * Returns a view of the portion of this List between fromIndex,
     * inclusive, and toIndex, exclusive.  (If fromIndex and toIndex are
     * equal, the returned List is empty.)  The returned List is backed by this
     * List, so changes in the returned List are reflected in this List, and
     * vice-versa.  The returned List supports all of the optional List
     * operations supported by this List.
     *
     * <p>This method eliminates the need for explicit range operations (of
     * the sort that commonly exist for arrays).  Any operation that expects
     * a List can be used as a range operation by operating on a subList view
     * instead of a whole List.  For example, the following idiom
     * removes a range of elements from a List:
     * <pre>
     *      list.subList(from, to).clear();
     * </pre>
     * Similar idioms may be constructed for indexOf and lastIndexOf,
     * and all of the algorithms in the Collections class can be applied to
     * a subList.
     *
     * <p>The semantics of the List returned by this method become undefined if
     * the backing list (i.e., this List) is <i>structurally modified</i> in
     * any way other than via the returned List.  (Structural modifications are
     * those that change the size of the List, or otherwise perturb it in such
     * a fashion that iterations in progress may yield incorrect results.)
     *
     * @param fromIndex low endpoint (inclusive) of the subList
     * @param toIndex high endpoint (exclusive) of the subList
     * @return a view of the specified range within this List
     * @throws IndexOutOfBoundsException if an endpoint index value is out of range
     *         {@code (fromIndex < 0 || toIndex > size)}
     * @throws IllegalArgumentException if the endpoint indices are out of order
     *         {@code (fromIndex > toIndex)}
     */
    // synchronized 方法，从指定索引范围内获取 vector 的子集
    public synchronized List<E> subList(int fromIndex, int toIndex) {
        // 确保线程安全的获取指定索引范围的子集
        return Collections.synchronizedList(super.subList(fromIndex, toIndex),
                                            this);
    }

    /**
     * Removes from this list all of the elements whose index is between
     * {@code fromIndex}, inclusive, and {@code toIndex}, exclusive.
     * Shifts any succeeding elements to the left (reduces their index).
     * This call shortens the list by {@code (toIndex - fromIndex)} elements.
     * (If {@code toIndex==fromIndex}, this operation has no effect.)
     */
    // synchronized 方法，移除指定索引范围内的元素
    protected synchronized void removeRange(int fromIndex, int toIndex) {
        // modCount++
        modCount++;
        // 计算要移动的元素个数
        int numMoved = elementCount - toIndex;
        // 将 toIndex 开始到后面所有的元素向前移动，从 fronIndex 位置开始填充
        System.arraycopy(elementData, toIndex, elementData, fromIndex,
                         numMoved);

        // Let gc do its work
        // 计算移除元素之后的新 elmentCount 应该达到的值
        int newElementCount = elementCount - (toIndex-fromIndex);
        while (elementCount != newElementCount)
            // 将超过 newElementCount 部分的元素置为 null，同时更新 elementCount 的值，等待 GC 进行内存回收
            elementData[--elementCount] = null;
    }

    /**
     * Loads a {@code Vector} instance from a stream
     * (that is, deserializes it).
     * This method performs checks to ensure the consistency
     * of the fields.
     *
     * @param in the stream
     * @throws java.io.IOException if an I/O error occurs
     * @throws ClassNotFoundException if the stream contains data
     *         of a non-existing class
     */
    // 将 vector 进行反序列化操作
    private void readObject(ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        ObjectInputStream.GetField gfields = in.readFields();
        int count = gfields.get("elementCount", 0);
        Object[] data = (Object[])gfields.get("elementData", null);
        if (count < 0 || data == null || count > data.length) {
            throw new StreamCorruptedException("Inconsistent vector internals");
        }
        elementCount = count;
        elementData = data.clone();
    }

    /**
     * Save the state of the {@code Vector} instance to a stream (that
     * is, serialize it).
     * This method performs synchronization to ensure the consistency
     * of the serialized data.
     */
    // 对 vector 进行序列化操作
    private void writeObject(java.io.ObjectOutputStream s)
            throws java.io.IOException {
        final java.io.ObjectOutputStream.PutField fields = s.putFields();
        final Object[] data;
        // 同步序列化 capacityIncrement, elementCount 和 elementData[]
        synchronized (this) {
            fields.put("capacityIncrement", capacityIncrement);
            fields.put("elementCount", elementCount);
            data = elementData.clone();
        }
        fields.put("elementData", data);
        s.writeFields();
    }

    /**
     * Returns a list iterator over the elements in this list (in proper
     * sequence), starting at the specified position in the list.
     * The specified index indicates the first element that would be
     * returned by an initial call to {@link ListIterator#next next}.
     * An initial call to {@link ListIterator#previous previous} would
     * return the element with the specified index minus one.
     *
     * <p>The returned list iterator is <a href="#fail-fast"><i>fail-fast</i></a>.
     *
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    // synchronized 方法，根据指定索引获取 ListIterator 迭代器
    public synchronized ListIterator<E> listIterator(int index) {
        // 对 index 的值进行边界条件校验，可能会抛出 IndexOutOfBoundsException()
        if (index < 0 || index > elementCount)
            throw new IndexOutOfBoundsException("Index: "+index);
        // 根据指定索引创建 ListItr 对象
        return new ListItr(index);
    }

    /**
     * Returns a list iterator over the elements in this list (in proper
     * sequence).
     *
     * <p>The returned list iterator is <a href="#fail-fast"><i>fail-fast</i></a>.
     *
     * @see #listIterator(int)
     */
    // synchronized 方法，创建一个默认的 ListIterator 迭代器，cursor 的默认值为 0
    public synchronized ListIterator<E> listIterator() {
        return new ListItr(0);
    }

    /**
     * Returns an iterator over the elements in this list in proper sequence.
     *
     * <p>The returned iterator is <a href="#fail-fast"><i>fail-fast</i></a>.
     *
     * @return an iterator over the elements in this list in proper sequence
     */
    // synchronized 方法，创建一个 Iterator 迭代器
    public synchronized Iterator<E> iterator() {
        return new Itr();
    }

    /**
     * An optimized version of AbstractList.Itr
     */
    // vector 中的 Itr 对象实现方式
    private class Itr implements Iterator<E> {
        // cursor 游标，下一个元素的索引值
        int cursor;       // index of next element to return
        // lastRet，上一个元素索引值，-1 表示没有
        int lastRet = -1; // index of last element returned; -1 if no such
        // expectedModCount，预期的修改次数
        int expectedModCount = modCount;

        // 判断游标后面是否还有元素，即比较 cursor 与 elementCount 的值就可以了
        public boolean hasNext() {
            // Racy but within spec, since modifications are checked
            // within or after synchronization in next/previous
            return cursor != elementCount;
        }

        // 获取 cursor 值表示索引对应的元素，即获取 cursor 后面的元素
        public E next() {
            // synchronzied Vector
            synchronized (Vector.this) {
                // 对 vector 的数据结构进行检查
                checkForComodification();
                // 获取 cursor 的值
                int i = cursor;
                // 如果 i >= elementCount，则抛出 NoSuchElementException()
                if (i >= elementCount)
                    throw new NoSuchElementException();
                // 将 cursor 向后移一位
                cursor = i + 1;
                // 获取 cursor 移位之前的索引对应的元素，并更新 lastRet 的值
                return elementData(lastRet = i);
            }
        }

        // 移除 cursor 前面的元素
        public void remove() {
            // 判断 lastRet 的值，可能会抛出 IllegalStateException()
            if (lastRet == -1)
                throw new IllegalStateException();
            // synchronized Vector
            synchronized (Vector.this) {
                // 对 vector 的数据结构进行检查
                checkForComodification();
                // 移除 lastRet 索引对应的元素
                Vector.this.remove(lastRet);
                // 更新 expectedModCount 的值
                expectedModCount = modCount;
            }
            // 更新 cursor 以及 lastRet 的值
            cursor = lastRet;
            lastRet = -1;
        }

        // 保存没有被迭代完的元素
        @Override
        public void forEachRemaining(Consumer<? super E> action) {
            // action 不能为 null
            Objects.requireNonNull(action);
            // synchronized Vector
            synchronized (Vector.this) {
                // 获取 elementCount 的值
                final int size = elementCount;
                // 获取 cursor 的值
                int i = cursor;
                // 对 cursor 进行边界值检查
                if (i >= size) {
                    return;
                }
        @SuppressWarnings("unchecked")
                // 获取 Vector 的 elementData[] 元素
                final E[] elementData = (E[]) Vector.this.elementData;
                // 对 cursor 进行边界值检查，可能会抛出 ConcurrentModificationException()
                if (i >= elementData.length) {
                    throw new ConcurrentModificationException();
                }
                // 如果没有迭代完，即 cursor != size，则 consumer 消费掉 cursor 后面的元素
                while (i != size && modCount == expectedModCount) {
                    action.accept(elementData[i++]);
                }
                // update once at end of iteration to reduce heap write traffic
                // 更新 cursor 以及 lastRet 的值，减少堆写入流量
                cursor = i;
                lastRet = i - 1;
                // 检查 vector 的数据结构
                checkForComodification();
            }
        }

        // 对 vector 的数据结构进行检查，即判断 modCount 和 expectedModCount 的值是否相等
        final void checkForComodification() {
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
        }
    }

    /**
     * An optimized version of AbstractList.ListItr
     */
    final class ListItr extends Itr implements ListIterator<E> {
        // 根据指定索引创建 ListItr 对象
        ListItr(int index) {
            // 调用父类构造器进行初始化
            super();
            // 初始化 cursor 的值
            cursor = index;
        }

        // 判断游标前面是否存在元素，即 cursor != 0 即可
        public boolean hasPrevious() {
            return cursor != 0;
        }

        // 获取游标后面一个元素的索引值，即 cursor 的值
        public int nextIndex() {
            return cursor;
        }

        // 获取游标前面一个元素的索引值，即 cursor - 1
        public int previousIndex() {
            return cursor - 1;
        }

        // 获取游标前面的元素
        public E previous() {
            // synchronized vector
            synchronized (Vector.this) {
                // 对 vector 的数据结构进行检查
                checkForComodification();
                // 获取游标前面一个元素的索引值
                int i = cursor - 1;
                // 对 i 进行合理性判断，可能抛出 NoSuchElementException()
                if (i < 0)
                    throw new NoSuchElementException();
                // 更新 cursor 的值
                cursor = i;
                // 返回 cursor 更新前前面的元素，并更新 lastRet 的值
                return elementData(lastRet = i);
            }
        }

        // 更新 cursor 前面的元素
        public void set(E e) {
            // lastRet == -1，会抛出 IllegalStateExceptions()
            if (lastRet == -1)
                throw new IllegalStateException();
            // synchronized vector
            synchronized (Vector.this) {
                // 对 vector 的数据结构进行检查
                checkForComodification();
                // 更新 cursor 前面的元素
                Vector.this.set(lastRet, e);
            }
        }

        // 在 cursor 后面新增一个元素
        public void add(E e) {
            // 获取 cursor 的值
            int i = cursor;
            // synchronized vector
            synchronized (Vector.this) {
                // 对 vector 的边界值进行检查
                checkForComodification();
                // 在 cursor 对应的索引值位置增加元素
                Vector.this.add(i, e);
                // 更新 expectedModCount 的值
                expectedModCount = modCount;
            }
            // 更新 cursor 和 lastRet 的值
            cursor = i + 1;
            lastRet = -1;
        }
    }

    // synchronized 方法，实现 forEach 遍历操作 vector 中的元素
    @Override
    public synchronized void forEach(Consumer<? super E> action) {
        // action 不能为 null
        Objects.requireNonNull(action);
        // 获取 modCount 的值
        final int expectedModCount = modCount;
        @SuppressWarnings("unchecked")
        // 获取 elementData[] 中的元素
        final E[] elementData = (E[]) this.elementData;
        // 获取 elementCount 的值
        final int elementCount = this.elementCount;
        // 遍历对 elementData[] 中的元素进行 accept 操作
        for (int i=0; modCount == expectedModCount && i < elementCount; i++) {
            action.accept(elementData[i]);
        }
        // 最后判断 vector 的数据结构是否被修改
        if (modCount != expectedModCount) {
            throw new ConcurrentModificationException();
        }
    }

    // synchronized 方法，根据策略移除元素
    @Override
    @SuppressWarnings("unchecked")
    public synchronized boolean removeIf(Predicate<? super E> filter) {
        // filter 不能为 null
        Objects.requireNonNull(filter);
        // figure out which elements are to be removed
        // any exception thrown from the filter predicate at this stage
        // will leave the collection unmodified
        // 初始化 removeCount
        int removeCount = 0;
        // 获取 elementCount 的值
        final int size = elementCount;
        // 创建一个 size 大小的位图
        final BitSet removeSet = new BitSet(size);
        // 获取 modCount 的值
        final int expectedModCount = modCount;
        // 遍历 elementData[]
        for (int i=0; modCount == expectedModCount && i < size; i++) {
            @SuppressWarnings("unchecked")
            final E element = (E) elementData[i];
            // 对于满足 filter 策略的在 removeSet 的对应位置进行标识
            if (filter.test(element)) {
                removeSet.set(i);
                // 计算 removeCount
                removeCount++;
            }
        }
        // 判断 vector 的数据结构是否发生改变，可能抛出 ConcurrentModificationException()
        if (modCount != expectedModCount) {
            throw new ConcurrentModificationException();
        }

        // shift surviving elements left over the spaces left by removed elements
        // removeCount > 0 才进行移除操作
        final boolean anyToRemove = removeCount > 0;
        if (anyToRemove) {
            // 计算移除后 vector 中元素的个数
            final int newSize = size - removeCount;
            // 遍历获取未被移除的元素
            for (int i=0, j=0; (i < size) && (j < newSize); i++, j++) {
                // 获取标识为 false 的索引
                i = removeSet.nextClearBit(i);
                // 将不被移除的元素移到前面
                elementData[j] = elementData[i];
            }
            // 对于 newSize 后面的元素，全部置为 null，等待 GC 进行内存回收操作
            for (int k=newSize; k < size; k++) {
                elementData[k] = null;  // Let gc do its work
            }
            // 更新 elementCount 的值
            elementCount = newSize;
            // 再次判断 vector 的数据结构是否发生了改变
            if (modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
            // modCount++
            modCount++;
        }

        // 返回 removeIf 的操作结果
        return anyToRemove;
    }

    // synchronized 方法，对所有元素进行 apply 操作
    @Override
    @SuppressWarnings("unchecked")
    public synchronized void replaceAll(UnaryOperator<E> operator) {
        // operator 不能为 null
        Objects.requireNonNull(operator);
        // 获取 modCount 的值
        final int expectedModCount = modCount;
        // 获取 elementCount 的值
        final int size = elementCount;
        // 遍历对 elementData[] 中的元素进行 apply 操作
        for (int i=0; modCount == expectedModCount && i < size; i++) {
            elementData[i] = operator.apply((E) elementData[i]);
        }
        // 对 vector 的数据结构进行检查
        if (modCount != expectedModCount) {
            throw new ConcurrentModificationException();
        }
        // modCount++
        modCount++;
    }

    // synchronized 方法，对 vector 进行排序操作
    @SuppressWarnings("unchecked")
    @Override
    public synchronized void sort(Comparator<? super E> c) {
        // 获取 modCount 的值
        final int expectedModCount = modCount;
        // 对 elementData[] 中的元素按照 c 的排序方式进行排序
        Arrays.sort((E[]) elementData, 0, elementCount, c);
        // 检查 vector 的数据结构是否发生变化
        if (modCount != expectedModCount) {
            throw new ConcurrentModificationException();
        }
        // modCount++
        modCount++;
    }

    /**
     * Creates a <em><a href="Spliterator.html#binding">late-binding</a></em>
     * and <em>fail-fast</em> {@link Spliterator} over the elements in this
     * list.
     *
     * <p>The {@code Spliterator} reports {@link Spliterator#SIZED},
     * {@link Spliterator#SUBSIZED}, and {@link Spliterator#ORDERED}.
     * Overriding implementations should document the reporting of additional
     * characteristic values.
     *
     * @return a {@code Spliterator} over the elements in this list
     * @since 1.8
     */
    // 创建一个分割迭代器对象
    @Override
    public Spliterator<E> spliterator() {
        return new VectorSpliterator<>(this, null, 0, -1, 0);
    }

    /** Similar to ArrayList Spliterator */
    static final class VectorSpliterator<E> implements Spliterator<E> {
        // 要分割的对象 Vector
        private final Vector<E> list;
        // 用来存放 elementData[] 元素
        private Object[] array;
        // 当前索引值，可以被 trySplit 和 tryAdvance 方法改变
        private int index; // current index, modified on advance/split
        // 结束位置，-1 表示到了最后一个元素
        private int fence; // -1 until used; then one past last index
        // expectedModCount
        private int expectedModCount; // initialized when fence set

        /** Create new spliterator covering the given  range */
        // 创建一个 VectorSpliterator 分割迭代器
        VectorSpliterator(Vector<E> list, Object[] array, int origin, int fence,
                          int expectedModCount) {
            this.list = list;
            this.array = array;
            this.index = origin;
            this.fence = fence;
            this.expectedModCount = expectedModCount;
        }

        // 获取结束位置
        private int getFence() { // initialize on first use
            int hi;
            // fence < 0，表示到了最后一个元素
            if ((hi = fence) < 0) {
                // synchronized list
                synchronized(list) {
                    // 同步获取 elementData[]，modCount，elementCount
                    array = list.elementData;
                    expectedModCount = list.modCount;
                    hi = fence = list.elementCount;
                }
            }
            // 返回 fence 的值
            return hi;
        }

        // 创建 Spliterator 分割迭代器
        public Spliterator<E> trySplit() {
            // 对 list 进行分割，并返回索引靠前的那个迭代器
            int hi = getFence(), lo = index, mid = (lo + hi) >>> 1;
            return (lo >= mid) ? null :
                new VectorSpliterator<E>(list, array, lo, index = mid,
                                         expectedModCount);
        }

        // 使用 action 进行消费元素
        @SuppressWarnings("unchecked")
        public boolean tryAdvance(Consumer<? super E> action) {
            int i;
            // action 不能为 null
            if (action == null)
                throw new NullPointerException();
            if (getFence() > (i = index)) {
                // 将索引后移一位，并消耗之前索引位置的元素
                index = i + 1;
                action.accept((E)array[i]);
                // 检查 vector 的数据结构，可能会抛出 ConcurrentModificationException()
                if (list.modCount != expectedModCount)
                    throw new ConcurrentModificationException();
                return true;
            }
            return false;
        }

        // 保留迭代器未遍历的元素
        @SuppressWarnings("unchecked")
        public void forEachRemaining(Consumer<? super E> action) {
            int i, hi; // hoist accesses and checks from loop
            Vector<E> lst; Object[] a;
            // action 不能为 null
            if (action == null)
                throw new NullPointerException();
            // list 不能为 null，否则会抛出 ConcurrentModificationException()
            if ((lst = list) != null) {
                // fence < 0，表示到了最后一个元素
                if ((hi = fence) < 0) {
                    // synchronized list，直接同步 modCount, elementData[], elementCount
                    synchronized(lst) {
                        expectedModCount = lst.modCount;
                        a = array = lst.elementData;
                        hi = fence = lst.elementCount;
                    }
                }
                else
                    a = array;
                // 遍历消费迭代器后面的元素，即没有被迭代器迭代的元素
                if (a != null && (i = index) >= 0 && (index = hi) <= a.length) {
                    while (i < hi)
                        action.accept((E) a[i++]);
                    // 对 vector 的数据结构进行检查
                    if (lst.modCount == expectedModCount)
                        return;
                }
            }
            throw new ConcurrentModificationException();
        }

        // 获取估算的当前 Spliterator 要迭代的元素的个数
        public long estimateSize() {
            return (long) (getFence() - index);
        }

        // 获取迭代策略
        public int characteristics() {
            return Spliterator.ORDERED | Spliterator.SIZED | Spliterator.SUBSIZED;
        }
    }
}
