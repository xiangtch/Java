/*
 * Copyright (c) 1997, 2017, Oracle and/or its affiliates. All rights reserved.
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

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import sun.misc.SharedSecrets;

/**
 * Resizable-array implementation of the <tt>List</tt> interface.  Implements
 * all optional list operations, and permits all elements, including
 * <tt>null</tt>.  In addition to implementing the <tt>List</tt> interface,
 * this class provides methods to manipulate the size of the array that is
 * used internally to store the list.  (This class is roughly equivalent to
 * <tt>Vector</tt>, except that it is unsynchronized.)
 *
 * <p>The <tt>size</tt>, <tt>isEmpty</tt>, <tt>get</tt>, <tt>set</tt>,
 * <tt>iterator</tt>, and <tt>listIterator</tt> operations run in constant
 * time.  The <tt>add</tt> operation runs in <i>amortized constant time</i>,
 * that is, adding n elements requires O(n) time.  All of the other operations
 * run in linear time (roughly speaking).  The constant factor is low compared
 * to that for the <tt>LinkedList</tt> implementation.
 *
 * <p>Each <tt>ArrayList</tt> instance has a <i>capacity</i>.  The capacity is
 * the size of the array used to store the elements in the list.  It is always
 * at least as large as the list size.  As elements are added to an ArrayList,
 * its capacity grows automatically.  The details of the growth policy are not
 * specified beyond the fact that adding an element has constant amortized
 * time cost.
 *
 * <p>An application can increase the capacity of an <tt>ArrayList</tt> instance
 * before adding a large number of elements using the <tt>ensureCapacity</tt>
 * operation.  This may reduce the amount of incremental reallocation.
 *
 * <p><strong>Note that this implementation is not synchronized.</strong>
 * If multiple threads access an <tt>ArrayList</tt> instance concurrently,
 * and at least one of the threads modifies the list structurally, it
 * <i>must</i> be synchronized externally.  (A structural modification is
 * any operation that adds or deletes one or more elements, or explicitly
 * resizes the backing array; merely setting the value of an element is not
 * a structural modification.)  This is typically accomplished by
 * synchronizing on some object that naturally encapsulates the list.
 *
 * If no such object exists, the list should be "wrapped" using the
 * {@link Collections#synchronizedList Collections.synchronizedList}
 * method.  This is best done at creation time, to prevent accidental
 * unsynchronized access to the list:<pre>
 *   List list = Collections.synchronizedList(new ArrayList(...));</pre>
 *
 * <p><a name="fail-fast">
 * The iterators returned by this class's {@link #iterator() iterator} and
 * {@link #listIterator(int) listIterator} methods are <em>fail-fast</em>:</a>
 * if the list is structurally modified at any time after the iterator is
 * created, in any way except through the iterator's own
 * {@link ListIterator#remove() remove} or
 * {@link ListIterator#add(Object) add} methods, the iterator will throw a
 * {@link ConcurrentModificationException}.  Thus, in the face of
 * concurrent modification, the iterator fails quickly and cleanly, rather
 * than risking arbitrary, non-deterministic behavior at an undetermined
 * time in the future.
 *
 * <p>Note that the fail-fast behavior of an iterator cannot be guaranteed
 * as it is, generally speaking, impossible to make any hard guarantees in the
 * presence of unsynchronized concurrent modification.  Fail-fast iterators
 * throw {@code ConcurrentModificationException} on a best-effort basis.
 * Therefore, it would be wrong to write a program that depended on this
 * exception for its correctness:  <i>the fail-fast behavior of iterators
 * should be used only to detect bugs.</i>
 *
 * <p>This class is a member of the
 * <a href="{@docRoot}/../technotes/guides/collections/index.html">
 * Java Collections Framework</a>.
 *
 * @author  Josh Bloch
 * @author  Neal Gafter
 * @see     Collection
 * @see     List
 * @see     LinkedList
 * @see     Vector
 * @since   1.2
 */

public class ArrayList<E> extends AbstractList<E>
        implements List<E>, RandomAccess, Cloneable, java.io.Serializable
{
    private static final long serialVersionUID = 8683452581122892189L;

    /**
     * Default initial capacity.
     */
    // 默认初始容量
    private static final int DEFAULT_CAPACITY = 10;

    /**
     * Shared empty array instance used for empty instances.
     */
    // 使用指定的容量创建一个空对象 Object[]
    private static final Object[] EMPTY_ELEMENTDATA = {};

    /**
     * Shared empty array instance used for default sized empty instances. We
     * distinguish this from EMPTY_ELEMENTDATA to know how much to inflate when
     * first element is added.
     */
    // 默认构造器创建的一个空对象
    private static final Object[] DEFAULTCAPACITY_EMPTY_ELEMENTDATA = {};

    /**
     * The array buffer into which the elements of the ArrayList are stored.
     * The capacity of the ArrayList is the length of this array buffer. Any
     * empty ArrayList with elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA
     * will be expanded to DEFAULT_CAPACITY when the first element is added.
     */
    // 当前数据对象存放的地方，当前对象不参与序列化
    // 元素为对象的话，则存放的是对象的引用地址,
    // Capacity 其实就是 elementData[] 的 length，这里用 transient 修饰也是为了不让虚拟机采用的默认序列化策略
    // 导致将 elementData[] 没有元素的位置给序列化成 null，造成空间浪费，因为大多数情况下 ArrayList 的 Capacity 要大于 size 的，
    // 即 elementData[] 经常不会被填充满
    transient Object[] elementData; // non-private to simplify nested class access

    /**
     * The size of the ArrayList (the number of elements it contains).
     *
     * @serial
     */
    // ArrayList 的大小，包含元素的个数
    private int size;

    /**
     * Constructs an empty list with the specified initial capacity.
     *
     * @param  initialCapacity  the initial capacity of the list
     * @throws IllegalArgumentException if the specified initial capacity
     *         is negative
     */
    // 根据指定容量初始化 ArrayList
    public ArrayList(int initialCapacity) {
        // 如果 initialCapacity > 0
        if (initialCapacity > 0) {
            // 创建指定大小的 Object[] 数组存放对象
            this.elementData = new Object[initialCapacity];
        } else if (initialCapacity == 0) {
            // 如果指定 initialCapacity 为 0，则使用 EMPTY_ELEMENTDATA 进行初始化 ArrayList
            this.elementData = EMPTY_ELEMENTDATA;
        } else {
            throw new IllegalArgumentException("Illegal Capacity: "+
                                               initialCapacity);
        }
    }

    /**
     * Constructs an empty list with an initial capacity of ten.
     */
    // 使用默认构造器创建一个初始容量为 10 的空的 list
    public ArrayList() {
        this.elementData = DEFAULTCAPACITY_EMPTY_ELEMENTDATA;
    }

    /**
     * Constructs a list containing the elements of the specified
     * collection, in the order they are returned by the collection's
     * iterator.
     *
     * @param c the collection whose elements are to be placed into this list
     * @throws NullPointerException if the specified collection is null
     */
    // 根据指定的集合 Collection 创建 ArrayList
    public ArrayList(Collection<? extends E> c) {
        // 将 Collection 转换成 Object[]，赋值给 elementData
        elementData = c.toArray();
        // 如果元素长度不为 0
        if ((size = elementData.length) != 0) {
            // c.toArray might (incorrectly) not return Object[] (see 6260652)
            // 如果元素的类型不是 Object 类型，这是由于继承造成的，父类实例的具体类型，实际上是取决于在 new 时，我们所使用的子类的类型
            // 比如 ArrayList 的 toArray 方法
            if (elementData.getClass() != Object[].class)
                // 将数组转成 Object[] 类型
                elementData = Arrays.copyOf(elementData, size, Object[].class);
        } else {
            // replace with empty array.
            // 元素长度为 0，则将 EMPTY_ELEMENTDATA 赋值给 elementData，即调用指定的容量大小创建一个空 Object[]
            this.elementData = EMPTY_ELEMENTDATA;
        }
    }

    /**
     * Trims the capacity of this <tt>ArrayList</tt> instance to be the
     * list's current size.  An application can use this operation to minimize
     * the storage of an <tt>ArrayList</tt> instance.
     */
    // 将 ArrayList 集合容量的大小设置为实际元素的个数
    public void trimToSize() {
        // 修改次数 + 1
        modCount++;
        // size < elementData.length
        if (size < elementData.length) {
            // 非空的话复制 elementData 数组中长度为 size 的元素到新的数组中
            elementData = (size == 0)
              ? EMPTY_ELEMENTDATA
              : Arrays.copyOf(elementData, size);
        }
    }

    /**
     * Increases the capacity of this <tt>ArrayList</tt> instance, if
     * necessary, to ensure that it can hold at least the number of elements
     * specified by the minimum capacity argument.
     *
     * @param   minCapacity   the desired minimum capacity
     */
    // 对 ArrayList 进行扩容操作
    public void ensureCapacity(int minCapacity) {
        // 获取当前 ArrayList 的最小容量
        int minExpand = (elementData != DEFAULTCAPACITY_EMPTY_ELEMENTDATA)
            // any size if not default element table
            ? 0
            // larger than default for default empty table. It's already
            // supposed to be at default size.
            : DEFAULT_CAPACITY;

        // 如果需要的最小容量大于当前 ArrayList 的最小容量，则进行扩容操作
        if (minCapacity > minExpand) {
            ensureExplicitCapacity(minCapacity);
        }
    }

    // 计算容量
    private static int calculateCapacity(Object[] elementData, int minCapacity) {
        // 空的时候，返回 DEFAULT_CAPACITY 与 minCapacity 的最大值
        if (elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA) {
            return Math.max(DEFAULT_CAPACITY, minCapacity);
        }
        return minCapacity;
    }

    // 确保 Capacity 够用，并将 modCount + 1
    private void ensureCapacityInternal(int minCapacity) {
        ensureExplicitCapacity(calculateCapacity(elementData, minCapacity));
    }

    // 扩展到指定的容量
    private void ensureExplicitCapacity(int minCapacity) {
        // ArrayList 修改次数 + 1
        modCount++;

        // overflow-conscious code
        // minCapacity > elementData.length 的时候，将 ArrayList 扩容到 minCapacity 容量
        if (minCapacity - elementData.length > 0)
            grow(minCapacity);
    }

    /**
     * The maximum size of array to allocate.
     * Some VMs reserve some header words in an array.
     * Attempts to allocate larger arrays may result in
     * OutOfMemoryError: Requested array size exceeds VM limit
     */
    // 这里定义 MAX_ARRAY_SIZE 为 Integer.MAX_VALUE - 8，是因为一些 JVM 会
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    /**
     * Increases the capacity to ensure that it can hold at least the
     * number of elements specified by the minimum capacity argument.
     *
     * @param minCapacity the desired minimum capacity
     */
    // 将 ArrayList 的容量扩容到满足元素个数的最小容量， 即 minCapacity
    private void grow(int minCapacity) {
        // overflow-conscious code
        // 获取旧容量大小
        int oldCapacity = elementData.length;
        // 第一次扩容将就容量增加原来的一半得到新容量
        int newCapacity = oldCapacity + (oldCapacity >> 1);
        // 扩容后的新容量小于 minCapacity 时，直接扩展容量到 minCapacity
        if (newCapacity - minCapacity < 0)
            newCapacity = minCapacity;
        // 如果 newCapacity 大小大于 MAX_ARRAY_SIZE 即 Integer.MAX_VALUE - 8
        if (newCapacity - MAX_ARRAY_SIZE > 0)
            newCapacity = hugeCapacity(minCapacity);
        // minCapacity is usually close to size, so this is a win:
        // 将 elementData 中长度为 newCapacity 的元素 copy 到新数组中，copy 的时候取的是 elementData.length 与 newCapacity 的
        // 最小值
        elementData = Arrays.copyOf(elementData, newCapacity);
    }

    // 扩容到最大的容量，即 Integer.MAX_VALUE。
    private static int hugeCapacity(int minCapacity) {
        if (minCapacity < 0) // overflow
            throw new OutOfMemoryError();
        return (minCapacity > MAX_ARRAY_SIZE) ?
            Integer.MAX_VALUE :
            MAX_ARRAY_SIZE;
    }

    /**
     * Returns the number of elements in this list.
     *
     * @return the number of elements in this list
     */
    // 返回 List 中的元素个数大小
    public int size() {
        return size;
    }

    /**
     * Returns <tt>true</tt> if this list contains no elements.
     *
     * @return <tt>true</tt> if this list contains no elements
     */
    // 判断集合是否为空，即集合中的元素个数是否为 0
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns <tt>true</tt> if this list contains the specified element.
     * More formally, returns <tt>true</tt> if and only if this list contains
     * at least one element <tt>e</tt> such that
     * <tt>(o==null&nbsp;?&nbsp;e==null&nbsp;:&nbsp;o.equals(e))</tt>.
     *
     * @param o element whose presence in this list is to be tested
     * @return <tt>true</tt> if this list contains the specified element
     */
    // 判断集合中是否含有某个元素
    public boolean contains(Object o) {
        return indexOf(o) >= 0;
    }

    /**
     * Returns the index of the first occurrence of the specified element
     * in this list, or -1 if this list does not contain the element.
     * More formally, returns the lowest index <tt>i</tt> such that
     * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>,
     * or -1 if there is no such index.
     */
    // 返回元素在 List 集合中第一次出现的位置
    public int indexOf(Object o) {
        if (o == null) { // null 进行单独遍历判断
            for (int i = 0; i < size; i++)
                if (elementData[i]==null)
                    return i;
        } else { // 非 null 的值进行遍历采用 equals() 方法进行判断
            for (int i = 0; i < size; i++)
                if (o.equals(elementData[i]))
                    return i;
        }
        // 没有则返回 -1
        return -1;
    }

    /**
     * Returns the index of the last occurrence of the specified element
     * in this list, or -1 if this list does not contain the element.
     * More formally, returns the highest index <tt>i</tt> such that
     * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>,
     * or -1 if there is no such index.
     */
    // 返回元素在 List 集合中最后一次出现的位置
    public int lastIndexOf(Object o) {
        if (o == null) { // null 进行遍历采用 == 进行判断
            for (int i = size-1; i >= 0; i--)
                if (elementData[i]==null)
                    return i;
        } else { // 非 null 值进行遍历采用 equals() 进行判断
            for (int i = size-1; i >= 0; i--)
                if (o.equals(elementData[i]))
                    return i;
        }
        // 没有的话返回 -1
        return -1;
    }

    /**
     * Returns a shallow copy of this <tt>ArrayList</tt> instance.  (The
     * elements themselves are not copied.)
     *
     * @return a clone of this <tt>ArrayList</tt> instance
     */
    // shallow copy，浅克隆，只 clone 对象的引用，不会 clone 对象元素的值
    public Object clone() {
        try {
            ArrayList<?> v = (ArrayList<?>) super.clone();
            // 将 elementData[] 数组中的元素复制到新的集合 v 中
            v.elementData = Arrays.copyOf(elementData, size);
            // 设置 v 的 modCount 为 0
            v.modCount = 0;
            return v;
        } catch (CloneNotSupportedException e) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError(e);
        }
    }

    /**
     * Returns an array containing all of the elements in this list
     * in proper sequence (from first to last element).
     *
     * <p>The returned array will be "safe" in that no references to it are
     * maintained by this list.  (In other words, this method must allocate
     * a new array).  The caller is thus free to modify the returned array.
     *
     * <p>This method acts as bridge between array-based and collection-based
     * APIs.
     *
     * @return an array containing all of the elements in this list in
     *         proper sequence
     */
    // 将 list 集合对象转换成 Object[] 数组
    public Object[] toArray() {
        return Arrays.copyOf(elementData, size);
    }

    /**
     * Returns an array containing all of the elements in this list in proper
     * sequence (from first to last element); the runtime type of the returned
     * array is that of the specified array.  If the list fits in the
     * specified array, it is returned therein.  Otherwise, a new array is
     * allocated with the runtime type of the specified array and the size of
     * this list.
     *
     * <p>If the list fits in the specified array with room to spare
     * (i.e., the array has more elements than the list), the element in
     * the array immediately following the end of the collection is set to
     * <tt>null</tt>.  (This is useful in determining the length of the
     * list <i>only</i> if the caller knows that the list does not contain
     * any null elements.)
     *
     * @param a the array into which the elements of the list are to
     *          be stored, if it is big enough; otherwise, a new array of the
     *          same runtime type is allocated for this purpose.
     * @return an array containing the elements of the list
     * @throws ArrayStoreException if the runtime type of the specified array
     *         is not a supertype of the runtime type of every element in
     *         this list
     * @throws NullPointerException if the specified array is null
     */
    @SuppressWarnings("unchecked")
    // 将 list 对象转换成指定类型的数组，并存储到指定的数组 a 中
    public <T> T[] toArray(T[] a) {
        // 如果 a.length < size，则根据 size 的大小返回指定类型的数组
        if (a.length < size)
            // Make a new array of a's runtime type, but my contents:
            return (T[]) Arrays.copyOf(elementData, size, a.getClass());
        // 将 elementData 中的元素复制到数组 a 中
        System.arraycopy(elementData, 0, a, 0, size);
        // 当 a.length 超过 size 的大小的时候，会将 a[size] 位置的元素设置为 null，在数组 a 中不存在 null 的时候，可以用来判断
        // list 的大小和元素以及 a 的大小和超过 list 大小之后的元素
        if (a.length > size)
            a[size] = null;
        return a;
    }

    // Positional Access Operations

    @SuppressWarnings("unchecked")
    // 返回指定索引位置的元素
    E elementData(int index) {
        return (E) elementData[index];
    }

    /**
     * Returns the element at the specified position in this list.
     *
     * @param  index index of the element to return
     * @return the element at the specified position in this list
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    public E get(int index) {
        // 检查索引边界值
        rangeCheck(index);

        // 返回指定索引位置的元素
        return elementData(index);
    }

    /**
     * Replaces the element at the specified position in this list with
     * the specified element.
     *
     * @param index index of the element to replace
     * @param element element to be stored at the specified position
     * @return the element previously at the specified position
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    // 修改指定位置的元素，返回修改之前的元素的值
    public E set(int index, E element) {
        // 对索引进行边界检查
        rangeCheck(index);

        // 获取修改之前的索引位置的值
        E oldValue = elementData(index);
        // 将新的元素赋值到索引的位置
        elementData[index] = element;
        // 返回修改之前的元素的值
        return oldValue;
    }

    /**
     * Appends the specified element to the end of this list.
     *
     * @param e element to be appended to this list
     * @return <tt>true</tt> (as specified by {@link Collection#add})
     */
    // 添加元素
    public boolean add(E e) {
        // 确保 Capacity 够用，并且将 modCount + 1，这里 minCapacity 为 size + 1
        ensureCapacityInternal(size + 1);  // Increments modCount!!
        // 将元素 e 放到 list 的末尾，并将 size 进行 + 1 操作
        elementData[size++] = e;
        // 返回 true
        return true;
    }

    /**
     * Inserts the specified element at the specified position in this
     * list. Shifts the element currently at that position (if any) and
     * any subsequent elements to the right (adds one to their indices).
     *
     * @param index index at which the specified element is to be inserted
     * @param element element to be inserted
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    // 指定索引位置添加元素
    public void add(int index, E element) {
        // 对 add 操作进行索引边界检查
        rangeCheckForAdd(index);

        // 确保内部容量够用，并将 modCount + 1
        ensureCapacityInternal(size + 1);  // Increments modCount!!
        // 将 elementData[] 中 index 位置开始的元素复制到 index + 1 的位置后面，
        // 即将 elementData[] 的 index 开始位置后面的元素全部后移一位
        System.arraycopy(elementData, index, elementData, index + 1,
                         size - index);
        // 将指定元素赋值到 index 索引位置上
        elementData[index] = element;
        // size 进行 + 1 操作
        size++;
    }

    /**
     * Removes the element at the specified position in this list.
     * Shifts any subsequent elements to the left (subtracts one from their
     * indices).
     *
     * @param index the index of the element to be removed
     * @return the element that was removed from the list
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    // 删除索引位置的元素，并且返回被删除的元素
    public E remove(int index) {
        // 检查索引的边界条件
        rangeCheck(index);

        // modCount++
        modCount++;
        // 获取删除之前的索引位置的元素
        E oldValue = elementData(index);

        // 计算要移动的元素的个数，即长度
        int numMoved = size - index - 1;
        if (numMoved > 0)
            // 将 elementData[] 中的 index + 1 开始，后面的元素全部前移一位
            System.arraycopy(elementData, index+1, elementData, index,
                             numMoved);
        // 让数组的最后一位为 null，等待 GC 对其进行清除操作，并且将 size - 1
        elementData[--size] = null; // clear to let GC do its work

        // 返回删除之前的元素的值
        return oldValue;
    }

    /**
     * Removes the first occurrence of the specified element from this list,
     * if it is present.  If the list does not contain the element, it is
     * unchanged.  More formally, removes the element with the lowest index
     * <tt>i</tt> such that
     * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>
     * (if such an element exists).  Returns <tt>true</tt> if this list
     * contained the specified element (or equivalently, if this list
     * changed as a result of the call).
     *
     * @param o element to be removed from this list, if present
     * @return <tt>true</tt> if this list contained the specified element
     */
    // 删除元素，只删除找到的第一个元素
    public boolean remove(Object o) {
        if (o == null) { // 对 null，采用遍历加 == 进行查找删除，
            for (int index = 0; index < size; index++)
                if (elementData[index] == null) {
                // 快速移除 index 索引位置的值
                    fastRemove(index);
                    return true;
                }
        } else { // 对于非 null 的值，采用遍历加 equals() 方法进行查找并删除
            for (int index = 0; index < size; index++)
                if (o.equals(elementData[index])) {
                // 采用 fastRemove 方法进行删除，即不进行索引边界的检查，也不用返回被删除元素的值
                    fastRemove(index);
                    return true;
                }
        }
        return false;
    }

    /*
     * Private remove method that skips bounds checking and does not
     * return the value removed.
     */
    // 快速删除，跳过索引边界检查并且不返回被删除的元素
    private void fastRemove(int index) {
        // modCount++
        modCount++;
        // 计算要移动的元素的长度
        int numMoved = size - index - 1;
        // numMoved > 0
        if (numMoved > 0)
            // 将 index + 1 后面的 numMoved 个元素全部前移一位
            System.arraycopy(elementData, index+1, elementData, index,
                             numMoved);
        // 将 size - 1 并将 elementData[size - 1] 索引位置置为 null，等待 GC 回收
        elementData[--size] = null; // clear to let GC do its work
    }

    /**
     * Removes all of the elements from this list.  The list will
     * be empty after this call returns.
     */
    // 清除 list 集合中所有元素的值
    public void clear() {
        // modCount++
        modCount++;

        // clear to let GC do its work
        // 遍历将 elementData 中的所有元素置位 null，等待 GC 进行垃圾回收
        for (int i = 0; i < size; i++)
            elementData[i] = null;

        // 将 size 重置为 0
        size = 0;
    }

    /**
     * Appends all of the elements in the specified collection to the end of
     * this list, in the order that they are returned by the
     * specified collection's Iterator.  The behavior of this operation is
     * undefined if the specified collection is modified while the operation
     * is in progress.  (This implies that the behavior of this call is
     * undefined if the specified collection is this list, and this
     * list is nonempty.)
     *
     * @param c collection containing elements to be added to this list
     * @return <tt>true</tt> if this list changed as a result of the call
     * @throws NullPointerException if the specified collection is null
     */
    // 将集合 c 中的所有元素加入到 list 中
    public boolean addAll(Collection<? extends E> c) {
        // 将集合转成 Object[] 数组
        Object[] a = c.toArray();
        // 获取数组的长度
        int numNew = a.length;
        // 确保 Capacity 能满足需求，并将 modCount + 1
        ensureCapacityInternal(size + numNew);  // Increments modCount
        // 将数组 a[] 的元素从 elementData[] 的 size 位置复制到 elementData[] 数组中，复制长度为 numNew
        System.arraycopy(a, 0, elementData, size, numNew);
        // 更新 size 的值
        size += numNew;
        // 返回添加的状态
        return numNew != 0;
    }

    /**
     * Inserts all of the elements in the specified collection into this
     * list, starting at the specified position.  Shifts the element
     * currently at that position (if any) and any subsequent elements to
     * the right (increases their indices).  The new elements will appear
     * in the list in the order that they are returned by the
     * specified collection's iterator.
     *
     * @param index index at which to insert the first element from the
     *              specified collection
     * @param c collection containing elements to be added to this list
     * @return <tt>true</tt> if this list changed as a result of the call
     * @throws IndexOutOfBoundsException {@inheritDoc}
     * @throws NullPointerException if the specified collection is null
     */
    // 将集合 c 中的元素从指定 index 索引位置添加到 list 中
    public boolean addAll(int index, Collection<? extends E> c) {
        // 对添加操作的 index 进行边界检查，即 index > size || index < 0 均为非法的
        rangeCheckForAdd(index);

        // 将集合 c 转换成数组
        Object[] a = c.toArray();
        // 获取数组 a[] 的长度
        int numNew = a.length;
        // 确保 Capacity 够用并将 modCount++，不够的话将 Capacity 先增加原来的一半，
        // 如果还不够用则直接使用 size + numNew 当做新的 Capacity
        ensureCapacityInternal(size + numNew);  // Increments modCount

        // 计算要移动的元素的个数
        int numMoved = size - index;
        // 将 elementData[] 中从 index 开始 numMoved 长度的元素向后移 numNew 位
        // 即将 elementData[] 中的 index 开始到后面的 numMoved 个元素从 elementData[] 的 index+numNew 位置开始依次复制过去
        if (numMoved > 0)
            System.arraycopy(elementData, index, elementData, index + numNew,
                             numMoved);

        // 然后将数组 a[] 中的元素从 index 位置开始复制到 elementData[] 对应的索引位置中
        System.arraycopy(a, 0, elementData, index, numNew);
        // 更新 size 的值
        size += numNew;
        // 返回操作是否成功，如果 numNew == 0，则返回 false
        return numNew != 0;
    }

    /**
     * Removes from this list all of the elements whose index is between
     * {@code fromIndex}, inclusive, and {@code toIndex}, exclusive.
     * Shifts any succeeding elements to the left (reduces their index).
     * This call shortens the list by {@code (toIndex - fromIndex)} elements.
     * (If {@code toIndex==fromIndex}, this operation has no effect.)
     *
     * @throws IndexOutOfBoundsException if {@code fromIndex} or
     *         {@code toIndex} is out of range
     *         ({@code fromIndex < 0 ||
     *          fromIndex >= size() ||
     *          toIndex > size() ||
     *          toIndex < fromIndex})
     */
    // 移除指定索引范围内的元素
    protected void removeRange(int fromIndex, int toIndex) {
        //将 modCount 进行 ++ 操作
        modCount++;
        // 计算要移动的元素个数
        int numMoved = size - toIndex;
        // 将 elementData[] 中 toIndex 后面的 numMoved 个元素从 fromIndex 位置开始填充到 elementData[] 中
        System.arraycopy(elementData, toIndex, elementData, fromIndex,
                         numMoved);

        // clear to let GC do its work
        // 计算新 size 的值
        int newSize = size - (toIndex-fromIndex);
        // 将 elemengData[] 中超过 newSize 位置索引的值置为 null，等待 GC 进行回收
        for (int i = newSize; i < size; i++) {
            elementData[i] = null;
        }
        // 更新 size 的值
        size = newSize;
    }

    /**
     * Checks if the given index is in range.  If not, throws an appropriate
     * runtime exception.  This method does *not* check if the index is
     * negative: It is always used immediately prior to an array access,
     * which throws an ArrayIndexOutOfBoundsException if index is negative.
     */
    // 检查索引边界值
    private void rangeCheck(int index) {
        if (index >= size)
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }

    /**
     * A version of rangeCheck used by add and addAll.
     */
    // 对 add 操作进行索引边界检查
    private void rangeCheckForAdd(int index) {
        if (index > size || index < 0)
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }

    /**
     * Constructs an IndexOutOfBoundsException detail message.
     * Of the many possible refactorings of the error handling code,
     * this "outlining" performs best with both server and client VMs.
     */
    // outOfBoundsMsg，定义索引越界消息
    private String outOfBoundsMsg(int index) {
        return "Index: "+index+", Size: "+size;
    }

    /**
     * Removes from this list all of its elements that are contained in the
     * specified collection.
     *
     * @param c collection containing elements to be removed from this list
     * @return {@code true} if this list changed as a result of the call
     * @throws ClassCastException if the class of an element of this list
     *         is incompatible with the specified collection
     * (<a href="Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException if this list contains a null element and the
     *         specified collection does not permit null elements
     * (<a href="Collection.html#optional-restrictions">optional</a>),
     *         or if the specified collection is null
     * @see Collection#contains(Object)
     */
    // 从 ArrayList 集合中移除集合 c 包含的元素
    public boolean removeAll(Collection<?> c) {
        // 集合 c 不能为 null
        Objects.requireNonNull(c);
        // 批量从集合 ArrayList 中移除集合 c 中存在的元素，complement 设置为 false，保留的是集合 c 的补集
        return batchRemove(c, false);
    }

    /**
     * Retains only the elements in this list that are contained in the
     * specified collection.  In other words, removes from this list all
     * of its elements that are not contained in the specified collection.
     *
     * @param c collection containing elements to be retained in this list
     * @return {@code true} if this list changed as a result of the call
     * @throws ClassCastException if the class of an element of this list
     *         is incompatible with the specified collection
     * (<a href="Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException if this list contains a null element and the
     *         specified collection does not permit null elements
     * (<a href="Collection.html#optional-restrictions">optional</a>),
     *         or if the specified collection is null
     * @see Collection#contains(Object)
     */
    // 从集合 ArrayList 中保留集合 c 中存在的元素
    public boolean retainAll(Collection<?> c) {
        Objects.requireNonNull(c);
        // 批量从 ArrayList 中移除集合 c 中不存在的元素，complement 设置为 true，保留的是集合 c 的子集
        return batchRemove(c, true);
    }

    // 批量移除集合 c 中存在的元素，
    // complement 为 true 时，则找出的是集合 c 的子集，complement 为 false 时，则找出的是集合 c 中的元素的补集
    private boolean batchRemove(Collection<?> c, boolean complement) {
        // 复制 elementData[] 中的元素
        final Object[] elementData = this.elementData;
        int r = 0, w = 0;
        boolean modified = false;
        try {
            for (; r < size; r++) // 遍历数组 elementData[]
                // 找出 elementData[] 中存在于集合 c 的元素或者不存在与集合 c 的元素
                // complement 为 true 时，则找出的是集合 c 的子集，complement 为 false 时，则找出的是集合 c 中的元素的补集
                if (c.contains(elementData[r]) == complement)
                    elementData[w++] = elementData[r];
        } finally {
            // Preserve behavioral compatibility with AbstractCollection,
            // even if c.contains() throws.
            // 如果 contains 抛出异常导致 r != size
            if (r != size) {
                // 将 elementData[] 中从索引 r 位置之后的 size - r 个元素复制到 elementData[] 的 w 索引位置之后
                System.arraycopy(elementData, r,
                                 elementData, w,
                                 size - r);
                // 更新 w 的值，即加上剩余没判断的值
                w += size - r;
            }
            // 如果 w != size，即正常遍历完了之后
            if (w != size) {
                // clear to let GC do its work
                // 将 w 索引之后的值全部置为 null，等待 GC 进行回收操作
                for (int i = w; i < size; i++)
                    elementData[i] = null;
                // 更新 modCount 的值，加上 size - w 次操作
                modCount += size - w;
                // 更新 size 的值
                size = w;
                // 然后将 modified 设置为 true
                modified = true;
            }
        }
        // 返回 modified 的值
        return modified;
    }

    /**
     * Save the state of the <tt>ArrayList</tt> instance to a stream (that
     * is, serialize it).
     *
     * @serialData The length of the array backing the <tt>ArrayList</tt>
     *             instance is emitted (int), followed by all of its elements
     *             (each an <tt>Object</tt>) in the proper order.
     */
    // 将 ArrayList 进行序列化
    private void writeObject(java.io.ObjectOutputStream s)
        throws java.io.IOException{
        // Write out element count, and any hidden stuff
        int expectedModCount = modCount;
        // 采用默认的序列化策略将没有用 transient 修饰的数据进行序列化
        s.defaultWriteObject();

        // Write out size as capacity for behavioural compatibility with clone()
        // 将数组大小进行序列化
        s.writeInt(size);

        // Write out all elements in the proper order.
        // 将数组中的元素进行序列化
        for (int i=0; i<size; i++) {
            s.writeObject(elementData[i]);
        }

        // 在序列化的过程中如果 ArrayList 被修改过，即 modCount 的值发生了改变，则抛出 ConcurrentModificationException()
        if (modCount != expectedModCount) {
            throw new ConcurrentModificationException();
        }
    }

    /**
     * Reconstitute the <tt>ArrayList</tt> instance from a stream (that is,
     * deserialize it).
     */
    // 将 ArrayList 进行反序列化 (Reconstitute / deserialize)
    private void readObject(java.io.ObjectInputStream s)
        throws java.io.IOException, ClassNotFoundException {
        // 创建一个空对象 Object[], 即 EMPTY_ELEMENTDATA
        elementData = EMPTY_ELEMENTDATA;

        // Read in size, and any hidden stuff
        // 采用默认的反序列化方法，即将没有用 transient 关键字修饰的数据进行反序列化
        s.defaultReadObject();

        // Read in capacity
        // 其实获取的是 size，不过 size 可以通过 defaultReadObject() 获得，所以这里可以忽略
        s.readInt(); // ignored

        if (size > 0) {
            // be like clone(), allocate array based upon size not capacity
            // 计算 capacity 的大小
            int capacity = calculateCapacity(elementData, size);
            // Java 安全检查
            SharedSecrets.getJavaOISAccess().checkArray(s, Object[].class, capacity);
            // 确保 capacity 能够满足 size 大小，进行 elementData 扩容操作
            ensureCapacityInternal(size);

            Object[] a = elementData;
            // Read in all elements in the proper order.
            // 将 ArrayList 中的元素反序列化到数组 a[] 中
            for (int i=0; i<size; i++) {
                a[i] = s.readObject();
            }
        }
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
    // 在指定索引位置创建一个 ListIterator 迭代对象
    public ListIterator<E> listIterator(int index) {
        // 对索引位置进行边界条件检查
        if (index < 0 || index > size)
            throw new IndexOutOfBoundsException("Index: "+index);
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
    // 使用默认构造器创建一个 ListIterator 对象，即索引位置为 0
    public ListIterator<E> listIterator() {
        return new ListItr(0);
    }

    /**
     * Returns an iterator over the elements in this list in proper sequence.
     *
     * <p>The returned iterator is <a href="#fail-fast"><i>fail-fast</i></a>.
     *
     * @return an iterator over the elements in this list in proper sequence
     */
    // 默认构造器创建一个 Iterator 对象
    public Iterator<E> iterator() {
        return new Itr();
    }

    /**
     * An optimized version of AbstractList.Itr
     */
    // 优化版的 Itr
    private class Itr implements Iterator<E> {
        int cursor;       // index of next element to return
        int lastRet = -1; // index of last element returned; -1 if no such
        int expectedModCount = modCount; // 集合修改次数

        Itr() {}

        // cursor != size 表名 cursor 还没到末尾，所以 hasNext() 是存在的
        public boolean hasNext() {
            return cursor != size;
        }

        @SuppressWarnings("unchecked")
        // 返回 cursor 后面的元素
        public E next() {
            // 检查 ArrayList 的结构是否被修改
            checkForComodification();
            // 获取游标的值
            int i = cursor;
            // cursor >= size，则抛出 NoSuchElementException()
            if (i >= size)
                throw new NoSuchElementException();
            // 获取 elementData[]
            Object[] elementData = ArrayList.this.elementData;
            // 如果 cursor >= elementData.length，则抛出 ConcurrentModificationException()
            if (i >= elementData.length)
                throw new ConcurrentModificationException();
            // 将游标后移一位
            cursor = i + 1;
            // 返回游标对应索引位置的元素
            return (E) elementData[lastRet = i];
        }

        // Iterator 移除游标前面的一个元素
        public void remove() {
            // 如果 lastRet < 0 ，则说明 cursor 位于首位，无法移除前面的元素
            if (lastRet < 0)
                throw new IllegalStateException();
            // 检查 ArrayList 的结构是否被修改
            checkForComodification();

            try {
                // 移除游标前面的索引位置的元素
                ArrayList.this.remove(lastRet);
                // 游标前移
                cursor = lastRet;
                // lastRet 设置为 -1, 即不能从后向前遍历
                lastRet = -1;
                // 更新 expectedModCount 的值
                expectedModCount = modCount;
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }

        @Override
        @SuppressWarnings("unchecked")
        // 遍历集合中游标后面的元素
        public void forEachRemaining(Consumer<? super E> consumer) {
            // 对 consumer 进行非 null 判断
            Objects.requireNonNull(consumer);
            // 获取 ArrayList 的 size 大小
            final int size = ArrayList.this.size;
            // 获取游标 cursor 的值
            int i = cursor;
            // 如果 cursor >= size ，直接返回
            if (i >= size) {
                return;
            }
            // 获取 ArrayList 的 elementData[]
            final Object[] elementData = ArrayList.this.elementData;
            // 如果 cursor >= elementData.length，则抛出 ConcurrentModificationException()
            if (i >= elementData.length) {
                throw new ConcurrentModificationException();
            }
            // 当 i != size && modCount == expectedModCount，即 cursor 没到末尾并且 ArrayList 的结构没被改变时
            while (i != size && modCount == expectedModCount) {
                // 消费者接收 elementData[] 中的剩余的元素
                consumer.accept((E) elementData[i++]);
            }
            // update once at end of iteration to reduce heap write traffic
            // 在迭代结束时更新 cursor 与 lastRet 的值，减少堆的写阻塞
            cursor = i;
            lastRet = i - 1;
            // 检查 ArrayList 的结构是否改变，即 modCount 是否和 expectedModCount 的值是否相等
            checkForComodification();
        }

        // 检查 ArrayList 的结构有没有被修改
        final void checkForComodification() {
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
        }
    }

    /**
     * An optimized version of AbstractList.ListItr
     */
    // 最优化版本的 ListItr
    private class ListItr extends Itr implements ListIterator<E> {
        // 根据指定索引位置创建一个 ListIterator 对象，并将 index 赋值给 cursor
        ListItr(int index) {
            super();
            cursor = index;
        }

        // 游标的前面是否有元素，根据 cursor 是否为 0 来判断
        public boolean hasPrevious() {
            return cursor != 0;
        }

        // 获取游标后面元素的索引位置的值，其实就是 cursor 的值
        public int nextIndex() {
            return cursor;
        }

        // 获取游标前面元素的索引位置的值，即 cursor - 1
        public int previousIndex() {
            return cursor - 1;
        }

        @SuppressWarnings("unchecked")
        // 获取游标前面的元素
        public E previous() {
            // 检查 ArrayList 的结构是否被改变
            checkForComodification();
            // 获取游标前移一位的值，即 cursor - 1
            int i = cursor - 1;
            // 如果 i < 0，则抛出 NoSuchElementException() 异常
            if (i < 0)
                throw new NoSuchElementException();
            // 获取 elementData[] 的值
            Object[] elementData = ArrayList.this.elementData;
            // 如果 i >= elementData.length 的值，则抛出 ConcurrentModificationException() 的值
            if (i >= elementData.length)
                throw new ConcurrentModificationException();
            // 更新 cursor 的值，即将 cursor 前移一位
            cursor = i;
            // 获取 cursor 前面的元素，并返回
            return (E) elementData[lastRet = i];
        }

        // 修改游标前面元素的值
        public void set(E e) {
            // 如果 lastRet < 0，则抛出 IllegalStateException() 异常
            if (lastRet < 0)
                throw new IllegalStateException();
            // 检查 ArrayList 的数据结构是否改变，即 expectedModCount 的值是否和 modCount 的值相等
            checkForComodification();

            try {
                // 修改游标前面元素的值
                ArrayList.this.set(lastRet, e);
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }

        // 在游标位置对应的索引位置添加元素，即在游标的后面添加元素
        public void add(E e) {
            // 检查 ArrayList 的数据结构有没有被修改，即 modCount 的值是否和 expectedModCount 相等
            checkForComodification();

            try {
                // 获取游标的值
                int i = cursor;
                // 在游标的值对应的索引上添加元素 e
                ArrayList.this.add(i, e);
                // 游标后移一位
                cursor = i + 1;
                // lastRet 置为首位
                lastRet = -1;
                // 更新 expectedModCount 的值
                expectedModCount = modCount;
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }
    }

    /**
     * Returns a view of the portion of this list between the specified
     * {@code fromIndex}, inclusive, and {@code toIndex}, exclusive.  (If
     * {@code fromIndex} and {@code toIndex} are equal, the returned list is
     * empty.)  The returned list is backed by this list, so non-structural
     * changes in the returned list are reflected in this list, and vice-versa.
     * The returned list supports all of the optional list operations.
     *
     * <p>This method eliminates the need for explicit range operations (of
     * the sort that commonly exist for arrays).  Any operation that expects
     * a list can be used as a range operation by passing a subList view
     * instead of a whole list.  For example, the following idiom
     * removes a range of elements from a list:
     * <pre>
     *      list.subList(from, to).clear();
     * </pre>
     * Similar idioms may be constructed for {@link #indexOf(Object)} and
     * {@link #lastIndexOf(Object)}, and all of the algorithms in the
     * {@link Collections} class can be applied to a subList.
     *
     * <p>The semantics of the list returned by this method become undefined if
     * the backing list (i.e., this list) is <i>structurally modified</i> in
     * any way other than via the returned list.  (Structural modifications are
     * those that change the size of this list, or otherwise perturb it in such
     * a fashion that iterations in progress may yield incorrect results.)
     *
     * @throws IndexOutOfBoundsException {@inheritDoc}
     * @throws IllegalArgumentException {@inheritDoc}
     */
    // 获取 ArrayList 指定索引范围内的子集，即 subList
    public List<E> subList(int fromIndex, int toIndex) {
        subListRangeCheck(fromIndex, toIndex, size);
        // 根据指定构造器创建 SubList 对象
        return new SubList(this, 0, fromIndex, toIndex);
    }

    // 对截取子类的参数进行边界检查
    static void subListRangeCheck(int fromIndex, int toIndex, int size) {
        // fromIndex 不能小于 0
        if (fromIndex < 0)
            throw new IndexOutOfBoundsException("fromIndex = " + fromIndex);
        // toIndex 不能大于 size
        if (toIndex > size)
            throw new IndexOutOfBoundsException("toIndex = " + toIndex);
        // fromIndex 不能大于 toIndex
        if (fromIndex > toIndex)
            throw new IllegalArgumentException("fromIndex(" + fromIndex +
                                               ") > toIndex(" + toIndex + ")");
    }

    private class SubList extends AbstractList<E> implements RandomAccess {
        // 父类 AbstractList 类
        private final AbstractList<E> parent;
        // 父类开始偏移的位置
        private final int parentOffset;
        // 偏移后的位置
        private final int offset;
        // SubList 集合的大小，即元素个数
        int size;

        SubList(AbstractList<E> parent,
                int offset, int fromIndex, int toIndex) {
            // 父类
            this.parent = parent;
            // 父类开始偏移的位置
            this.parentOffset = fromIndex;
            // 偏移后的位置
            this.offset = offset + fromIndex;
            // 子类集合长度
            this.size = toIndex - fromIndex;
            // 同步父类的 modCount
            this.modCount = ArrayList.this.modCount;
        }

        // 修改指定索引位置的元素的值
        public E set(int index, E e) {
            // 检查索引的值
            rangeCheck(index);
            // 检查 ArrayList 的数据结构是否被修改，即 modCount 的值是否和 expectedModCount 的值相等
            checkForComodification();
            // 获取修改之前的索引位置对应的元素，从 elementData[] 中获取
            E oldValue = ArrayList.this.elementData(offset + index);
            // 修改指定索引位置的元素
            ArrayList.this.elementData[offset + index] = e;
            // 返回修改之前的元素
            return oldValue;
        }

        // 获取指定索引位置的元素
        public E get(int index) {
            // 对 index 进行边界条件检查
            rangeCheck(index);
            // 对 ArrayList 的数据结构进行检查，即比较 expectedModCount 与 modCount 的值是否相同
            checkForComodification();
            // 从 elementData[] 中获取指定索引对应的元素
            return ArrayList.this.elementData(offset + index);
        }

        // 获取子集合的元素大小
        public int size() {
            // 对 ArrayList 的数据结构进行检查，即比较 expectedModCount 与 modCount 的值是否相同
            checkForComodification();
            // 返回 subList 子集的元素个数，即 size
            return this.size;
        }

        // 在指定索引位置添加元素
        public void add(int index, E e) {
            // 对 index 进行边界条件的检查
            rangeCheckForAdd(index);
            // 对 ArrayList 的数据结构进行检查，即比较 expectedModCount 与 modCount 的值是否相同
            checkForComodification();
            // 在父类中根据偏移量在指定位置添加元素
            parent.add(parentOffset + index, e);
            // 同步父类的 modCount
            this.modCount = parent.modCount;
            // 子类集合的 size++
            this.size++;
        }

        // 移除指定索引位置的元素
        public E remove(int index) {
            // 对 index 进行边界条件的检查
            rangeCheck(index);
            // 对 ArrayList 的数据结构进行检查，即比较 expectedModCount 与 modCount 的值是否相同
            checkForComodification();
            // 根据偏移量从父集合中移除对应位置索引的元素
            E result = parent.remove(parentOffset + index);
            // 同步父集合的 modCount
            this.modCount = parent.modCount;
            // 更新子类集合的 size
            this.size--;
            // 返回被移除的元素
            return result;
        }

        // 移除指定索引范围内的元素
        protected void removeRange(int fromIndex, int toIndex) {
            // 对 ArrayList 的数据结构进行边界条件的检查
            checkForComodification();
            // 根据偏移量移除父集合中指定索引范围内的元素
            parent.removeRange(parentOffset + fromIndex,
                               parentOffset + toIndex);
            // 同步父集合的 modCount 的值
            this.modCount = parent.modCount;
            // 更新子集合的 size 的值
            this.size -= toIndex - fromIndex;
        }

        // 将集合 c 中的元素全部加入到子集合中，从末尾位置开始添加
        public boolean addAll(Collection<? extends E> c) {
            return addAll(this.size, c);
        }

        // 将集合 c 中的元素全部加入到子集合中，从指定索引位置开始添加
        public boolean addAll(int index, Collection<? extends E> c) {
            // 对新增元素的 index 进行边界条件的检查
            rangeCheckForAdd(index);
            // 获取集合 c 的元素个数
            int cSize = c.size();
            // cSize == 0，则直接返回 false
            if (cSize==0)
                return false;

            // 对 ArrayList 的数据结构进行检查
            checkForComodification();
            // 根据偏移量在父集合 ArrayList 中指定索引位置添加集合 c 的元素
            parent.addAll(parentOffset + index, c);
            // 同步父集合的 modCount
            this.modCount = parent.modCount;
            // 更新 SubList 的 size
            this.size += cSize;
            // 返回 true
            return true;
        }

        // 获取迭代 ArrayList 的迭代器，即 ListIterator 对象
        public Iterator<E> iterator() {
            return listIterator();
        }

        // 根据指定索引位置获取 SubList 的 ListIterator 迭代器
        public ListIterator<E> listIterator(final int index) {
            // 对 ArrayList 的数据结构进行检查，即判断父集合 ArrayList 的 modCount 与子集合 SubList 的 modCount 是否相等
            checkForComodification();
            // 对新增元素的时候的 index 进行边界检查，新增的时候 index 可以等于 size
            rangeCheckForAdd(index);
            // 获取偏移量
            final int offset = this.offset;

            // 默认构造器创建一个 ListIterator 迭代器
            return new ListIterator<E>() {
                // 获取游标的位置
                int cursor = index;
                // 重置 lastRet 的初始值
                int lastRet = -1;
                // 同步 ArrayList 的 modCount
                int expectedModCount = ArrayList.this.modCount;

                // 判断游标的后面是否有元素，游标不在 SubList 的末尾位置即表示后面有元素
                public boolean hasNext() {
                    return cursor != SubList.this.size;
                }

                @SuppressWarnings("unchecked")
                // 获取游标后面的元素，即游标索引位置对应的元素
                public E next() {
                    // 对 ArrayList 的数据结构进行检查
                    checkForComodification();
                    // 获取游标的值
                    int i = cursor;
                    // cursor > SubList.this.size，则抛出 NoSuchElementEcxception()
                    if (i >= SubList.this.size)
                        throw new NoSuchElementException();
                    // 获取 ArrayList 的 elementData[] 中的元素
                    Object[] elementData = ArrayList.this.elementData;
                    // 这里还是对游标的合理性进行检验，不合理则会抛出 ConcurrentModficationException()
                    if (offset + i >= elementData.length)
                        throw new ConcurrentModificationException();
                    // 将游标后移一位
                    cursor = i + 1;
                    // 根据偏移量从父集合 ArrayList 中获取游标后面的元素，并且更新 lastRet 的值
                    return (E) elementData[offset + (lastRet = i)];
                }

                // 判断游标前面是否有元素，即 cursor != 0
                public boolean hasPrevious() {
                    return cursor != 0;
                }

                @SuppressWarnings("unchecked")
                // 获取游标前面的元素
                public E previous() {
                    // 对 ArrayList 的数据结构进行检查，
                    // 即判断 ArrayList 的 modCount 与 ListIterator 的 expectedModCount 是否相等
                    checkForComodification();
                    // 将游标前移一位
                    int i = cursor - 1;
                    // i < 0，抛出 NoSuchElementException()
                    if (i < 0)
                        throw new NoSuchElementException();
                    // 获取 ArrayList 父集合的 elementData[]
                    Object[] elementData = ArrayList.this.elementData;
                    // 判断 SubList 偏移后的索引位置是否有效，否则抛出 ConcurrentModificationException()
                    if (offset + i >= elementData.length)
                        throw new ConcurrentModificationException();
                    // 更新游标的值
                    cursor = i;
                    // 返回游标前面的元素，并更新 lastRet 的值
                    return (E) elementData[offset + (lastRet = i)];
                }

                @SuppressWarnings("unchecked")
                // 遍历保留集合索引后面的元素
                public void forEachRemaining(Consumer<? super E> consumer) {
                    // 对 consumen 进行非 null 判断
                    Objects.requireNonNull(consumer);
                    // 获取 SubList.this.size 的值
                    final int size = SubList.this.size;
                    // 获取游标的值
                    int i = cursor;
                    // i >= size，不合理直接 return
                    if (i >= size) {
                        return;
                    }
                    // 获取 ArrayList 的 elementData[] 的元素
                    final Object[] elementData = ArrayList.this.elementData;
                    // SubList 偏移后的 cursor 对应 ArrayList 的索引位置的值进行边界判断
                    // 如果 offset + i >= elementData.length，则抛出 ConcurrentModificationException()
                    if (offset + i >= elementData.length) {
                        throw new ConcurrentModificationException();
                    }
                    // cursor 没到末尾并且 ArrayList 的数据结构也没有被改变，则获取 elementData[] 中 cursor 后面的元素
                    while (i != size && modCount == expectedModCount) {
                        consumer.accept((E) elementData[offset + (i++)]);
                    }
                    // update once at end of iteration to reduce heap write traffic
                    // 更新 cursor 以及 lastRet 的值，减少 heap 写入阻塞
                    lastRet = cursor = i;
                    // 最后检查一下 ArrayList 的数据结构修改次数是否和预期一致，即 expectedModCount == modCount
                    checkForComodification();
                }

                // 获取游标后面一个元素的索引值，即 cursor 对应的值
                public int nextIndex() {
                    return cursor;
                }

                // 获取游标前面一个元素的索引值，即 cursor - 1 对应的值
                public int previousIndex() {
                    return cursor - 1;
                }

                // 移除游标前面的元素
                public void remove() {
                    // lastRet < 0，则抛出 IllegalStateException()
                    if (lastRet < 0)
                        throw new IllegalStateException();
                    // 对 ArrayList 的数据结构进行检查
                    checkForComodification();

                    try {
                        // 调用 SubList 的 remove 方法，移除游标前面的元素
                        SubList.this.remove(lastRet);
                        // 更新 cursor 的值，即将 cursor 向前移动一位
                        cursor = lastRet;
                        // 重置 lastRet 的值为 -1
                        lastRet = -1;
                        // 同步 ArrayList 的 modCount 值到 expectedModCount
                        expectedModCount = ArrayList.this.modCount;
                    } catch (IndexOutOfBoundsException ex) {
                        throw new ConcurrentModificationException();
                    }
                }

                // 更新右边前面的元素
                public void set(E e) {
                    // lastRet < 0，则抛出 IllegalStateException()
                    if (lastRet < 0)
                        throw new IllegalStateException();
                    // 对 ArrayList 的数据结构进行检查
                    checkForComodification();

                    try {
                        // 调用 ArrayList 的 set 方法更新指定索引位置的元素
                        ArrayList.this.set(offset + lastRet, e);
                    } catch (IndexOutOfBoundsException ex) {
                        throw new ConcurrentModificationException();
                    }
                }

                // 在游标对应的索引位置新增元素
                public void add(E e) {
                    // 对 ArrayList 进行数据结构检查
                    checkForComodification();

                    try {
                        // 获取游标的值
                        int i = cursor;
                        // 在游标指定位置新增元素，索引后的元素全部后移一位
                        SubList.this.add(i, e);
                        // cursor 后移一位
                        cursor = i + 1;
                        // lastRet 重置为 -1
                        lastRet = -1;
                        // 同步 ArrayList 的 modCount 的值
                        expectedModCount = ArrayList.this.modCount;
                    } catch (IndexOutOfBoundsException ex) {
                        throw new ConcurrentModificationException();
                    }
                }

                // 对 ArrayList 的数据结构进行检查，即判断 ArrayLsit 的 modCount 与 expectedModCount 是否相同
                final void checkForComodification() {
                    if (expectedModCount != ArrayList.this.modCount)
                        throw new ConcurrentModificationException();
                }
            };
        }

        // 根据指定索引范围获取子集合 subList
        public List<E> subList(int fromIndex, int toIndex) {
            // 对获取子集合的参数进行边界检查
            subListRangeCheck(fromIndex, toIndex, size);
            // 调用指定构造器创建 SubList 对象
            return new SubList(this, offset, fromIndex, toIndex);
        }

        // 对索引的值进行边界条件的检查
        private void rangeCheck(int index) {
            if (index < 0 || index >= this.size)
                throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
        }

        // 对新增元素的 index 进行边界条件的检查，index 可以等于 size
        private void rangeCheckForAdd(int index) {
            if (index < 0 || index > this.size)
                throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
        }

        // 返回 outOfBoundsMsg 信息
        private String outOfBoundsMsg(int index) {
            return "Index: "+index+", Size: "+this.size;
        }

        // 对 ArrayList 的数据结构进行检查，即判断 ArrayList 的 modCount 与 SubList 的 modCount 是否相同
        private void checkForComodification() {
            if (ArrayList.this.modCount != this.modCount)
                throw new ConcurrentModificationException();
        }

        // 创建一个并行执行对象 Spliterator
        public Spliterator<E> spliterator() {
            // 对 ArrayList 的数据结构进行检查
            checkForComodification();
            // 根据指定构造器创建一个 ArrayListSpliterator 对象
            return new ArrayListSpliterator<E>(ArrayList.this, offset,
                                               offset + this.size, this.modCount);
        }
    }

    // 实现 forEach 遍历
    @Override
    public void forEach(Consumer<? super E> action) {
        // action 不能为 null
        Objects.requireNonNull(action);
        // 获取 modCount 的值
        final int expectedModCount = modCount;
        @SuppressWarnings("unchecked")
        // 获取 elementData[] 中的元素
        final E[] elementData = (E[]) this.elementData;
        // 获取 size 的值
        final int size = this.size;
        // 遍历获取 elementData[] 中的元素并进行 accept 操作
        for (int i=0; modCount == expectedModCount && i < size; i++) {
            action.accept(elementData[i]);
        }
        // 确认 ArrayList 的数据结构是否被修改
        if (modCount != expectedModCount) {
            throw new ConcurrentModificationException();
        }
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
    @Override
    public Spliterator<E> spliterator() {
        return new ArrayListSpliterator<>(this, 0, -1, 0);
    }

    /** Index-based split-by-two, lazily initialized Spliterator */
    static final class ArrayListSpliterator<E> implements Spliterator<E> {

        /*
         * If ArrayLists were immutable, or structurally immutable (no
         * adds, removes, etc), we could implement their spliterators
         * with Arrays.spliterator. Instead we detect as much
         * interference during traversal as practical without
         * sacrificing much performance. We rely primarily on
         * modCounts. These are not guaranteed to detect concurrency
         * violations, and are sometimes overly conservative about
         * within-thread interference, but detect enough problems to
         * be worthwhile in practice. To carry this out, we (1) lazily
         * initialize fence and expectedModCount until the latest
         * point that we need to commit to the state we are checking
         * against; thus improving precision.  (This doesn't apply to
         * SubLists, that create spliterators with current non-lazy
         * values).  (2) We perform only a single
         * ConcurrentModificationException check at the end of forEach
         * (the most performance-sensitive method). When using forEach
         * (as opposed to iterators), we can normally only detect
         * interference after actions, not before. Further
         * CME-triggering checks apply to all other possible
         * violations of assumptions for example null or too-small
         * elementData array given its size(), that could only have
         * occurred due to interference.  This allows the inner loop
         * of forEach to run without any further checks, and
         * simplifies lambda-resolution. While this does entail a
         * number of checks, note that in the common case of
         * list.stream().forEach(a), no checks or other computation
         * occur anywhere other than inside forEach itself.  The other
         * less-often-used methods cannot take advantage of most of
         * these streamlinings.
         */

        // ArrayList 数据源的对象引用
        private final ArrayList<E> list;
        // 起始位置索引，会被 advance() 和 split() 修改
        private int index; // current index, modified on advance/split
        // 结束位置，-1 表示到最后一个元素
        private int fence; // -1 untils ued; then one past last index
        // 在 fence 初始化的时候初始化
        private int expectedModCount; // initialized when fence set

        /** Create new spliterator covering the given  range */
        // 创建一个可分割的迭代器，即 ArrayListSpliterator
        ArrayListSpliterator(ArrayList<E> list, int origin, int fence,
                             int expectedModCount) {
            this.list = list; // OK if null unless traversed
            this.index = origin;
            this.fence = fence;
            this.expectedModCount = expectedModCount;
        }

        // 获取 fence 的值，第一次应使用 ArrayList 的 size 来初始化 fence
        private int getFence() { // initialize fence to size on first use
            int hi; // (a specialized variant appears in method forEach)
            ArrayList<E> lst;
            // 如果 fence < 0，则表示是最后一个元素
            if ((hi = fence) < 0) {
                // 如果 list == null，则 hi = fence = 0
                if ((lst = list) == null)
                    hi = fence = 0;
                else {
                    // 否则，同步 ArrayList 的 modCount 值，并更新 hi = fence = lst.size，即最后一个元素
                    expectedModCount = lst.modCount;
                    hi = fence = lst.size;
                }
            }
            // 返回 hi，其实就是 fence
            return hi;
        }

        // 对 ArrayList 进行分割
        public ArrayListSpliterator<E> trySplit() {
            int hi = getFence(), lo = index, mid = (lo + hi) >>> 1;
            // 将 ArrayList 进行分割，将原来的迭代器分割为两个，并返回索引靠前的那个
            return (lo >= mid) ? null : // divide range in half unless too small
                new ArrayListSpliterator<E>(list, lo, index = mid,
                                            expectedModCount);
        }

        // 使用 action 消费下一个元素
        public boolean tryAdvance(Consumer<? super E> action) {
            // action 为 null，则抛出 NullPointerException()
            if (action == null)
                throw new NullPointerException();
            int hi = getFence(), i = index;
            if (i < hi) {
                // 索引后移一位
                index = i + 1;
                // 获取之前索引所在位置的元素
                @SuppressWarnings("unchecked") E e = (E)list.elementData[i];
                // 消费获取的元素
                action.accept(e);
                // 检查 ArrayList 的数据结构是否被修改
                if (list.modCount != expectedModCount)
                    throw new ConcurrentModificationException();
                return true;
            }
            return false;
        }

        // 批量消费所有未迭代的元素
        public void forEachRemaining(Consumer<? super E> action) {
            int i, hi, mc; // hoist accesses and checks from loop
            ArrayList<E> lst; Object[] a;
            // action 不能为 null
            if (action == null)
                throw new NullPointerException();
            // list 与 list 的 elementData[] 都不能为 null
            if ((lst = list) != null && (a = lst.elementData) != null) {
                // fence < 0，表示到了最后一个元素
                if ((hi = fence) < 0) {
                    // 同步 modCount 以及 size 的值
                    mc = lst.modCount;
                    hi = lst.size;
                }
                else
                    // 否则更新 mc 为 expectedModCount 的值
                    mc = expectedModCount;
                // 如果 index 的值是合理的
                if ((i = index) >= 0 && (index = hi) <= a.length) {
                    // 遍历消耗索引后面未被迭代的元素
                    for (; i < hi; ++i) {
                        @SuppressWarnings("unchecked") E e = (E) a[i];
                        action.accept(e);
                    }
                    // 判断 ArrayList 的数据结构是否被修改，否则抛出 ConcurrentModificationException()
                    if (lst.modCount == mc)
                        return;
                }
            }
            throw new ConcurrentModificationException();
        }

        // 估计当前 Spliterator 实例中将要迭代的元素的数量
        public long estimateSize() {
            return (long) (getFence() - index);
        }

        // 获取迭代策略
        public int characteristics() {
            return Spliterator.ORDERED | Spliterator.SIZED | Spliterator.SUBSIZED;
        }
    }

    // 根据策略移除元素
    @Override
    public boolean removeIf(Predicate<? super E> filter) {
        // filter 不能为 null
        Objects.requireNonNull(filter);
        // figure out which elements are to be removed
        // any exception thrown from the filter predicate at this stage
        // will leave the collection unmodified
        // 移除元素个数
        int removeCount = 0;
        // 位图法记录元素位置，后面记录被删除的元素
        final BitSet removeSet = new BitSet(size);
        final int expectedModCount = modCount;
        final int size = this.size;
        // 遍历获取 elementData[] 中的元素
        for (int i=0; modCount == expectedModCount && i < size; i++) {
            @SuppressWarnings("unchecked")
            final E element = (E) elementData[i];
            // 元素满足 filter 规则
            if (filter.test(element)) {
                // 在 removeSet 中记录元素的位置
                removeSet.set(i);
                // 移除元素数目 + 1，即 removeCount++
                removeCount++;
            }
        }
        // 若 ArrayList 的数据结构被修改，则抛出 ConcurrentModificationException()
        if (modCount != expectedModCount) {
            throw new ConcurrentModificationException();
        }

        // shift surviving elements left over the spaces left by removed elements
        final boolean anyToRemove = removeCount > 0;
        //
        if (anyToRemove) {
            final int newSize = size - removeCount;
            // 根据 removeSet 整理出保存的元素
            for (int i=0, j=0; (i < size) && (j < newSize); i++, j++) {
                i = removeSet.nextClearBit(i);
                elementData[j] = elementData[i];
            }
            // 将 newSize 之后的元素置位 null，等待 GC 进行回收操作
            for (int k=newSize; k < size; k++) {
                elementData[k] = null;  // Let gc do its work
            }
            // 更新 size 的值
            this.size = newSize;
            // 检查 ArrayList 的数据结构是否被改变
            if (modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
            // modCount++
            modCount++;
        }
        // 返回被移除的元素的个数
        return anyToRemove;
    }

    // 替换所有符合的元素
    @Override
    @SuppressWarnings("unchecked")
    public void replaceAll(UnaryOperator<E> operator) {
        // operator 不能为 null
        Objects.requireNonNull(operator);
        final int expectedModCount = modCount;
        final int size = this.size;
        for (int i=0; modCount == expectedModCount && i < size; i++) {
            elementData[i] = operator.apply((E) elementData[i]);
        }
        if (modCount != expectedModCount) {
            throw new ConcurrentModificationException();
        }
        modCount++;
    }

    // 对 elementData[] 中的元素按照 c 的规则进行排序
    @Override
    @SuppressWarnings("unchecked")
    public void sort(Comparator<? super E> c) {
        // 获取 modCount 的值
        final int expectedModCount = modCount;
        // 对 elementData[] 中的元素按照 c 的规则进行排序
        Arrays.sort((E[]) elementData, 0, size, c);
        // 对 ArrayList 的数据结构进行检查
        if (modCount != expectedModCount) {
            throw new ConcurrentModificationException();
        }
        // modCount++
        modCount++;
    }
}
