/*
 * Copyright (c) 1997, 2012, Oracle and/or its affiliates. All rights reserved.
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

/**
 * This class provides a skeletal implementation of the {@link List}
 * interface to minimize the effort required to implement this interface
 * backed by a "random access" data store (such as an array).  For sequential
 * access data (such as a linked list), {@link AbstractSequentialList} should
 * be used in preference to this class.
 *
 * <p>To implement an unmodifiable list, the programmer needs only to extend
 * this class and provide implementations for the {@link #get(int)} and
 * {@link List#size() size()} methods.
 *
 * <p>To implement a modifiable list, the programmer must additionally
 * override the {@link #set(int, Object) set(int, E)} method (which otherwise
 * throws an {@code UnsupportedOperationException}).  If the list is
 * variable-size the programmer must additionally override the
 * {@link #add(int, Object) add(int, E)} and {@link #remove(int)} methods.
 *
 * <p>The programmer should generally provide a void (no argument) and collection
 * constructor, as per the recommendation in the {@link Collection} interface
 * specification.
 *
 * <p>Unlike the other abstract collection implementations, the programmer does
 * <i>not</i> have to provide an iterator implementation; the iterator and
 * list iterator are implemented by this class, on top of the "random access"
 * methods:
 * {@link #get(int)},
 * {@link #set(int, Object) set(int, E)},
 * {@link #add(int, Object) add(int, E)} and
 * {@link #remove(int)}.
 *
 * <p>The documentation for each non-abstract method in this class describes its
 * implementation in detail.  Each of these methods may be overridden if the
 * collection being implemented admits a more efficient implementation.
 *
 * <p>This class is a member of the
 * <a href="{@docRoot}/../technotes/guides/collections/index.html">
 * Java Collections Framework</a>.
 *
 * @author  Josh Bloch
 * @author  Neal Gafter
 * @since 1.2
 */
// AbstactList 抽象类，实现 AbstractCollection
public abstract class AbstractList<E> extends AbstractCollection<E> implements List<E> {
    /**
     * Sole constructor.  (For invocation by subclass constructors, typically
     * implicit.)
     */
    // 唯一构造器，不能被实例化
    protected AbstractList() {
    }

    /**
     * Appends the specified element to the end of this list (optional
     * operation).
     *
     * <p>Lists that support this operation may place limitations on what
     * elements may be added to this list.  In particular, some
     * lists will refuse to add null elements, and others will impose
     * restrictions on the type of elements that may be added.  List
     * classes should clearly specify in their documentation any restrictions
     * on what elements may be added.
     *
     * <p>This implementation calls {@code add(size(), e)}.
     *
     * <p>Note that this implementation throws an
     * {@code UnsupportedOperationException} unless
     * {@link #add(int, Object) add(int, E)} is overridden.
     *
     * @param e element to be appended to this list
     * @return {@code true} (as specified by {@link Collection#add})
     * @throws UnsupportedOperationException if the {@code add} operation
     *         is not supported by this list
     * @throws ClassCastException if the class of the specified element
     *         prevents it from being added to this list
     * @throws NullPointerException if the specified element is null and this
     *         list does not permit null elements
     * @throws IllegalArgumentException if some property of this element
     *         prevents it from being added to this list
     */
    // 往 List 中添加元素，并返回是否添加成功
    public boolean add(E e) {
        // 在集合的末尾添加元素
        add(size(), e);
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    // 根据索引位置获取 List 中的元素
    abstract public E get(int index);

    /**
     * {@inheritDoc}
     *
     * <p>This implementation always throws an
     * {@code UnsupportedOperationException}.
     *
     * @throws UnsupportedOperationException {@inheritDoc}
     * @throws ClassCastException            {@inheritDoc}
     * @throws NullPointerException          {@inheritDoc}
     * @throws IllegalArgumentException      {@inheritDoc}
     * @throws IndexOutOfBoundsException     {@inheritDoc}
     */
    // 在指定索引位置插入元素
    public E set(int index, E element) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     *
     * <p>This implementation always throws an
     * {@code UnsupportedOperationException}.
     *
     * @throws UnsupportedOperationException {@inheritDoc}
     * @throws ClassCastException            {@inheritDoc}
     * @throws NullPointerException          {@inheritDoc}
     * @throws IllegalArgumentException      {@inheritDoc}
     * @throws IndexOutOfBoundsException     {@inheritDoc}
     */
    // 往集合中指定位置添加元素
    public void add(int index, E element) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     *
     * <p>This implementation always throws an
     * {@code UnsupportedOperationException}.
     *
     * @throws UnsupportedOperationException {@inheritDoc}
     * @throws IndexOutOfBoundsException     {@inheritDoc}
     */
    // 删除指定位置的元素
    public E remove(int index) {
        throw new UnsupportedOperationException();
    }


    // Search Operations

    /**
     * {@inheritDoc}
     *
     * <p>This implementation first gets a list iterator (with
     * {@code listIterator()}).  Then, it iterates over the list until the
     * specified element is found or the end of the list is reached.
     *
     * @throws ClassCastException   {@inheritDoc}
     * @throws NullPointerException {@inheritDoc}
     */
    // 获取元素第一次出现的位置
    public int indexOf(Object o) {
        // 获取 ListIterator 对象，ListIterator 可以向前向后遍历，而 Iterator 只能向后遍历
        ListIterator<E> it = listIterator();
        if (o==null) {
            while (it.hasNext())
                if (it.next()==null)
                    // 因为游标的开始位置跟索引开始的位置的编号相同，都是从 0 开始，使用 it.previousIndex() 返回索引的位置
                    return it.previousIndex();
        } else {
            while (it.hasNext())
                if (o.equals(it.next()))
                    return it.previousIndex();
        }
        return -1;
    }

    /**
     * {@inheritDoc}
     *
     * <p>This implementation first gets a list iterator that points to the end
     * of the list (with {@code listIterator(size())}).  Then, it iterates
     * backwards over the list until the specified element is found, or the
     * beginning of the list is reached.
     *
     * @throws ClassCastException   {@inheritDoc}
     * @throws NullPointerException {@inheritDoc}
     */
    // 获取元素最后一次出现的位置
    public int lastIndexOf(Object o) {
        // 获取 ListIterator 迭代器，将游标置于末尾位置
        ListIterator<E> it = listIterator(size());
        if (o==null) {
            while (it.hasPrevious())
                if (it.previous()==null)
                    return it.nextIndex();
        } else {
            while (it.hasPrevious())
                if (o.equals(it.previous()))
                    return it.nextIndex();
        }
        return -1;
    }


    // Bulk Operations

    /**
     * Removes all of the elements from this list (optional operation).
     * The list will be empty after this call returns.
     *
     * <p>This implementation calls {@code removeRange(0, size())}.
     *
     * <p>Note that this implementation throws an
     * {@code UnsupportedOperationException} unless {@code remove(int
     * index)} or {@code removeRange(int fromIndex, int toIndex)} is
     * overridden.
     *
     * @throws UnsupportedOperationException if the {@code clear} operation
     *         is not supported by this list
     */
    public void clear() {
        removeRange(0, size());
    }

    /**
     * {@inheritDoc}
     *
     * <p>This implementation gets an iterator over the specified collection
     * and iterates over it, inserting the elements obtained from the
     * iterator into this list at the appropriate position, one at a time,
     * using {@code add(int, E)}.
     * Many implementations will override this method for efficiency.
     *
     * <p>Note that this implementation throws an
     * {@code UnsupportedOperationException} unless
     * {@link #add(int, Object) add(int, E)} is overridden.
     *
     * @throws UnsupportedOperationException {@inheritDoc}
     * @throws ClassCastException            {@inheritDoc}
     * @throws NullPointerException          {@inheritDoc}
     * @throws IllegalArgumentException      {@inheritDoc}
     * @throws IndexOutOfBoundsException     {@inheritDoc}
     */
    // 将集合 c 的全部元素从索引位置开始加入到集合中，返回是否成功
    public boolean addAll(int index, Collection<? extends E> c) {
        // 检查新增元素时索引位置的边界条件
        rangeCheckForAdd(index);
        boolean modified = false;
        // forEach 遍历集合 c，新增元素到原集合中
        for (E e : c) {
            add(index++, e);
            modified = true;
        }
        return modified;
    }


    // Iterators

    /**
     * Returns an iterator over the elements in this list in proper sequence.
     *
     * <p>This implementation returns a straightforward implementation of the
     * iterator interface, relying on the backing list's {@code size()},
     * {@code get(int)}, and {@code remove(int)} methods.
     *
     * <p>Note that the iterator returned by this method will throw an
     * {@link UnsupportedOperationException} in response to its
     * {@code remove} method unless the list's {@code remove(int)} method is
     * overridden.
     *
     * <p>This implementation can be made to throw runtime exceptions in the
     * face of concurrent modification, as described in the specification
     * for the (protected) {@link #modCount} field.
     *
     * @return an iterator over the elements in this list in proper sequence
     */
    public Iterator<E> iterator() {
        return new Itr();
    }

    /**
     * {@inheritDoc}
     *
     * <p>This implementation returns {@code listIterator(0)}.
     *
     * @see #listIterator(int)
     */
    // 获取遍历集合的迭代器
    public ListIterator<E> listIterator() {
        return listIterator(0);
    }

    /**
     * {@inheritDoc}
     *
     * <p>This implementation returns a straightforward implementation of the
     * {@code ListIterator} interface that extends the implementation of the
     * {@code Iterator} interface returned by the {@code iterator()} method.
     * The {@code ListIterator} implementation relies on the backing list's
     * {@code get(int)}, {@code set(int, E)}, {@code add(int, E)}
     * and {@code remove(int)} methods.
     *
     * <p>Note that the list iterator returned by this implementation will
     * throw an {@link UnsupportedOperationException} in response to its
     * {@code remove}, {@code set} and {@code add} methods unless the
     * list's {@code remove(int)}, {@code set(int, E)}, and
     * {@code add(int, E)} methods are overridden.
     *
     * <p>This implementation can be made to throw runtime exceptions in the
     * face of concurrent modification, as described in the specification for
     * the (protected) {@link #modCount} field.
     *
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    // 将迭代器游标置于指定位置
    public ListIterator<E> listIterator(final int index) {
        // 边界条件检查
        rangeCheckForAdd(index);

        // 返回 ListIterator 迭代器，游标置于指定位置
        return new ListItr(index);
    }

    // 实现 Iterator 迭代器，cursor 和 lastRet 这两个成员相当于表示集合位置的两个指针，
    // Iterator 的时候两个指针的位置在遍历的单个元素的前后，而 ListIterator 的两个指针位置分别在 List 的头和尾
    private class Itr implements Iterator<E> {
        /**
         * Index of element to be returned by subsequent call to next.
         */
        // 游标位置，初始位置为 0
        int cursor = 0;

        /**
         * Index of element returned by most recent call to next or
         * previous.  Reset to -1 if this element is deleted by a call
         * to remove.
         */
        // 元素索引位置，用于元素被删除的时候
        int lastRet = -1;

        /**
         * The modCount value that the iterator believes that the backing
         * List should have.  If this expectation is violated, the iterator
         * has detected concurrent modification.
         */
        // 支持 List 的迭代器需要包含 modeCount 值，用于检测其他迭代器有没有对对象的结构进行改变
        int expectedModCount = modCount;

        // 判断游标后面是否还有元素
        public boolean hasNext() {
            return cursor != size();
        }

        // 获取游标后面的元素
        public E next() {
            // 结构检查
            checkForComodification();
            try {
                int i = cursor;
                // 获取游标编号对应位置的元素，即游标后面的元素
                E next = get(i);
                // 将当前游标的值保存到 lastRet 中
                lastRet = i;
                // 游标后移一位
                cursor = i + 1;
                // 返回之前游标右面的元素
                return next;
            } catch (IndexOutOfBoundsException e) {
                // 数组越界后进行结构检查，结构被改变则抛出 ConcurrentModificationException() 异常
                checkForComodification();
                // 结构未被改变则抛出 NoSuchElementException() 异常
                throw new NoSuchElementException();
            }
        }

        // 移除游标的前一个元素
        public void remove() {
            // 游标位于开始的位置的时候将不能进行 remove() 操作
            if (lastRet < 0)
                throw new IllegalStateException();
            // 集合结构检查
            checkForComodification();

            try {
                // 删除游标前面位置的元素
                AbstractList.this.remove(lastRet);
                // 如果 lastRet < cursor，将 cursor 减 1
                if (lastRet < cursor)
                    cursor--;
                // 执行一次 remove() 后，将 lastRet 置位 -1，不允许对同一个 Iterator remove() 多次
                lastRet = -1;
                // 将 list 的 modeCount 赋值给 expectedModCount
                expectedModCount = modCount;
            } catch (IndexOutOfBoundsException e) {
                // 发生越界异常时抛出 ConcurrentModificationException()，这个主要是针对多线程环境并发执行的时候可能出现的问题
                throw new ConcurrentModificationException();
            }
        }

        // 修改次数一致性检查
        final void checkForComodification() {
            // 如果 List 集合的结构被修改，则抛出 ConcurrentModificationException() 异常
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
        }
    }

    // ListItr 继承 Itr 来实现 ListIterator 接口
    private class ListItr extends Itr implements ListIterator<E> {
        // 通过构造器获取游标位置
        ListItr(int index) {
            cursor = index;
        }

        // 判断游标前面是否有元素
        public boolean hasPrevious() {
            return cursor != 0;
        }

        // 获取游标前面的元素
        public E previous() {
            // 进行 List 的结构检查
            checkForComodification();
            try {
                // 将游标的位置 - 1
                int i = cursor - 1;
                // 获取游标位置所对应的元素
                E previous = get(i);
                // 将之前右面前面的位置赋值给 lastRet 和 cursor
                lastRet = cursor = i;
                // 返回之前右边前面的元素
                return previous;
            } catch (IndexOutOfBoundsException e) {
                // 如果出现越界异常，检查 List 集合的结构，即 modCount 和 expectedModCount 是否相等
                // 如果不等则抛出 ConcurrentModificationException() 异常，主要是多线程环境
                checkForComodification();
                // 否则抛出 NoSuchElementException() 异常
                throw new NoSuchElementException();
            }
        }

        // 返回游标下一个元素的位置
        public int nextIndex() {
            return cursor;
        }

        // 获取游标前一个元素的位置
        public int previousIndex() {
            return cursor-1;
        }

        // 更新游标前面位置的元素
        public void set(E e) {
            // 判断游标前面位置是否有元素
            if (lastRet < 0)
                throw new IllegalStateException();
            // 对 List 进行结构检查
            checkForComodification();

            try {
                // 在指定索引位置插入元素
                AbstractList.this.set(lastRet, e);
                // 更新修改次数
                expectedModCount = modCount;
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }

        // 往 List 中添加元素
        public void add(E e) {
            // 对 List 进行结构检查
            checkForComodification();

            try {
                // 获取游标的值
                int i = cursor;
                // 在游标的位置添加元素
                AbstractList.this.add(i, e);
                // 将游标的上一个位置值设置为 -1，即记录 List 的头位置
                lastRet = -1;
                // 将游标的值 +1，即记录 List 的尾位置
                cursor = i + 1;
                // 更新 expectedModCount 的值
                expectedModCount = modCount;
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * <p>This implementation returns a list that subclasses
     * {@code AbstractList}.  The subclass stores, in private fields, the
     * offset of the subList within the backing list, the size of the subList
     * (which can change over its lifetime), and the expected
     * {@code modCount} value of the backing list.  There are two variants
     * of the subclass, one of which implements {@code RandomAccess}.
     * If this list implements {@code RandomAccess} the returned list will
     * be an instance of the subclass that implements {@code RandomAccess}.
     *
     * <p>The subclass's {@code set(int, E)}, {@code get(int)},
     * {@code add(int, E)}, {@code remove(int)}, {@code addAll(int,
     * Collection)} and {@code removeRange(int, int)} methods all
     * delegate to the corresponding methods on the backing abstract list,
     * after bounds-checking the index and adjusting for the offset.  The
     * {@code addAll(Collection c)} method merely returns {@code addAll(size,
     * c)}.
     *
     * <p>The {@code listIterator(int)} method returns a "wrapper object"
     * over a list iterator on the backing list, which is created with the
     * corresponding method on the backing list.  The {@code iterator} method
     * merely returns {@code listIterator()}, and the {@code size} method
     * merely returns the subclass's {@code size} field.
     *
     * <p>All methods first check to see if the actual {@code modCount} of
     * the backing list is equal to its expected value, and throw a
     * {@code ConcurrentModificationException} if it is not.
     *
     * @throws IndexOutOfBoundsException if an endpoint index value is out of range
     *         {@code (fromIndex < 0 || toIndex > size)}
     * @throws IllegalArgumentException if the endpoint indices are out of order
     *         {@code (fromIndex > toIndex)}
     */
    // 获取 List 的子集
    public List<E> subList(int fromIndex, int toIndex) {
        // 判断子集是否实现了 RandomAccess 接口，拥有随机访问存储元素的能力
        return (this instanceof RandomAccess ?
                new RandomAccessSubList<>(this, fromIndex, toIndex) :
                new SubList<>(this, fromIndex, toIndex));
    }

    // Comparison and hashing

    /**
     * Compares the specified object with this list for equality.  Returns
     * {@code true} if and only if the specified object is also a list, both
     * lists have the same size, and all corresponding pairs of elements in
     * the two lists are <i>equal</i>.  (Two elements {@code e1} and
     * {@code e2} are <i>equal</i> if {@code (e1==null ? e2==null :
     * e1.equals(e2))}.)  In other words, two lists are defined to be
     * equal if they contain the same elements in the same order.<p>
     *
     * This implementation first checks if the specified object is this
     * list. If so, it returns {@code true}; if not, it checks if the
     * specified object is a list. If not, it returns {@code false}; if so,
     * it iterates over both lists, comparing corresponding pairs of elements.
     * If any comparison returns {@code false}, this method returns
     * {@code false}.  If either iterator runs out of elements before the
     * other it returns {@code false} (as the lists are of unequal length);
     * otherwise it returns {@code true} when the iterations complete.
     *
     * @param o the object to be compared for equality with this list
     * @return {@code true} if the specified object is equal to this list
     */
    // 重写 Object 的 equals 方法
    public boolean equals(Object o) {
        // 先判断物理地址是否相等
        if (o == this)
            return true;
        // 不是 List 集合的子集，返回 false
        // 即再判断类型是否一致
        if (!(o instanceof List))
            return false;

        // 获取遍历集合的迭代器 ListIterator
        ListIterator<E> e1 = listIterator();
        // 获取遍历集合 o 的迭代器 ListIterator
        ListIterator<?> e2 = ((List<?>) o).listIterator();
        while (e1.hasNext() && e2.hasNext()) {
            E o1 = e1.next();
            Object o2 = e2.next();
            // 比较集合中的元素是否相等，注意 null 的判断
            if (!(o1==null ? o2==null : o1.equals(o2)))
                return false;
        }
        // 两个集合同时遍历完成，即集合长度相等
        return !(e1.hasNext() || e2.hasNext());
    }

    /**
     * Returns the hash code value for this list.
     *
     * <p>This implementation uses exactly the code that is used to define the
     * list hash function in the documentation for the {@link List#hashCode}
     * method.
     *
     * @return the hash code value for this list
     */
    // 重写 Object 的 hashCode 方法
    public int hashCode() {
        int hashCode = 1;
        // 31 递归乘以集合中每个元素的 hashCode
        for (E e : this)
            hashCode = 31*hashCode + (e==null ? 0 : e.hashCode());
        return hashCode;
    }

    /**
     * Removes from this list all of the elements whose index is between
     * {@code fromIndex}, inclusive, and {@code toIndex}, exclusive.
     * Shifts any succeeding elements to the left (reduces their index).
     * This call shortens the list by {@code (toIndex - fromIndex)} elements.
     * (If {@code toIndex==fromIndex}, this operation has no effect.)
     *
     * <p>This method is called by the {@code clear} operation on this list
     * and its subLists.  Overriding this method to take advantage of
     * the internals of the list implementation can <i>substantially</i>
     * improve the performance of the {@code clear} operation on this list
     * and its subLists.
     *
     * <p>This implementation gets a list iterator positioned before
     * {@code fromIndex}, and repeatedly calls {@code ListIterator.next}
     * followed by {@code ListIterator.remove} until the entire range has
     * been removed.  <b>Note: if {@code ListIterator.remove} requires linear
     * time, this implementation requires quadratic time.</b>
     *
     * @param fromIndex index of first element to be removed
     * @param toIndex index after last element to be removed
     */
    // 移除集合中指定范围内的元素
    protected void removeRange(int fromIndex, int toIndex) {
        // 将游标置于指定位置并获取迭代器
        ListIterator<E> it = listIterator(fromIndex);
        // 迭代删除指定范围内的元素
        for (int i=0, n=toIndex-fromIndex; i<n; i++) {
            it.next();
            it.remove();
        }
    }

    /**
     * The number of times this list has been <i>structurally modified</i>.
     * Structural modifications are those that change the size of the
     * list, or otherwise perturb it in such a fashion that iterations in
     * progress may yield incorrect results.
     *
     * <p>This field is used by the iterator and list iterator implementation
     * returned by the {@code iterator} and {@code listIterator} methods.
     * If the value of this field changes unexpectedly, the iterator (or list
     * iterator) will throw a {@code ConcurrentModificationException} in
     * response to the {@code next}, {@code remove}, {@code previous},
     * {@code set} or {@code add} operations.  This provides
     * <i>fail-fast</i> behavior, rather than non-deterministic behavior in
     * the face of concurrent modification during iteration.
     *
     * <p><b>Use of this field by subclasses is optional.</b> If a subclass
     * wishes to provide fail-fast iterators (and list iterators), then it
     * merely has to increment this field in its {@code add(int, E)} and
     * {@code remove(int)} methods (and any other methods that it overrides
     * that result in structural modifications to the list).  A single call to
     * {@code add(int, E)} or {@code remove(int)} must add no more than
     * one to this field, or the iterators (and list iterators) will throw
     * bogus {@code ConcurrentModificationExceptions}.  If an implementation
     * does not wish to provide fail-fast iterators, this field may be
     * ignored.
     */
    // 记录 List 被修改的次数，用 transient 修饰，不会被序列化，即不会被持久化存储
    // 这个值可以用来进行 fail-fast 操作
    protected transient int modCount = 0;

    // 检查新增元素时索引位置的边界条件
    private void rangeCheckForAdd(int index) {
        // 边界条件检查
        if (index < 0 || index > size())
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }

    private String outOfBoundsMsg(int index) {
        return "Index: "+index+", Size: "+size();
    }
}

// 获取 List 的子集，主要属性有 AbstractList, offset，size
class SubList<E> extends AbstractList<E> {
    private final AbstractList<E> l; // AbstractList 对象
    private final int offset; // 子集在 AbstactList 上的开始位置
    private int size; // 子集的大小

    // 通过 AbstractList，fromIndex, toIndex 来构造 SubList 对象
    SubList(AbstractList<E> list, int fromIndex, int toIndex) {
        // 边界条件检查
        if (fromIndex < 0)
            throw new IndexOutOfBoundsException("fromIndex = " + fromIndex);
        if (toIndex > list.size())
            throw new IndexOutOfBoundsException("toIndex = " + toIndex);
        if (fromIndex > toIndex)
            throw new IllegalArgumentException("fromIndex(" + fromIndex +
                    ") > toIndex(" + toIndex + ")");
        // 初始化成员 AbstractList
        l = list;
        // 初始化 offset
        offset = fromIndex;
        // 初始化集合大小
        size = toIndex - fromIndex;
        // 初始化 modCount
        this.modCount = l.modCount;
    }

    // 更新子集指定位置的元素
    public E set(int index, E element) {
        // 检查索引位置边界条件
        rangeCheck(index);
        // 检查 List 集合的结构
        checkForComodification();
        // 更新指定位置的元素，在原 AbstractList 上进行更新
        return l.set(index+offset, element);
    }

    // 获取子集指定索引位置的元素
    public E get(int index) {
        // 对索引进行边界检查
        rangeCheck(index);
        // 检查 List 集合的结构，即修改次数是否一致
        checkForComodification();
        // 获取指定索引位置的元素，在原 AbstractList 进行获取
        return l.get(index+offset);
    }

    // 获取子集的大小
    public int size() {
        // 检查 List 集合的结构，是否有被修改过，主要是多线程环境
        checkForComodification();
        // 返回子集的大小
        return size;
    }

    // 在子集指定位置新增元素
    public void add(int index, E element) {
        // 对索引进行边界条件检查
        rangeCheckForAdd(index);
        // 对 List 集合结构进行检查，实现 fail-fast 机制
        checkForComodification();
        // 在原 AbstractList 上指定位置新增元素
        l.add(index+offset, element);
        // 更新 modCount 的值
        this.modCount = l.modCount;
        // 子集容量 + 1
        size++;
    }

    // 移除指定位置的元素
    public E remove(int index) {
        // 对索引进行边界条件的检查
        rangeCheck(index);
        // 对 List 集合的结构进行检查，fail-fast 机制
        checkForComodification();
        // 在原 AbstractList 上删除指定位置的元素
        E result = l.remove(index+offset);
        // 更新 modCount 的值
        this.modCount = l.modCount;
        // 子集大小 - 1
        size--;
        // 返回删除的元素
        return result;
    }

    // 删除子集中指定范围内的元素
    protected void removeRange(int fromIndex, int toIndex) {
        // 对 List 集合的结构进行检查
        checkForComodification();
        // 在原 AbstractList 上删除指定范围内的元素
        l.removeRange(fromIndex+offset, toIndex+offset);
        // 更新 modCount 的值
        this.modCount = l.modCount;
        // 更新子集的 size 大小
        size -= (toIndex-fromIndex);
    }

    // 将集合 c 中的所有元素加入集合中
    public boolean addAll(Collection<? extends E> c) {
        return addAll(size, c);
    }

    // 从指定位置将集合 c 中的元素加入到集合中
    public boolean addAll(int index, Collection<? extends E> c) {
        // 检查新增的时候索引的边界条件
        rangeCheckForAdd(index);
        // 获取集合 c 的大小
        int cSize = c.size();
        if (cSize==0)
            return false;

        checkForComodification();
        // 在原 AbstractList 集合上指定索引位置加入集合 c 的全部元素
        l.addAll(offset+index, c);
        // 更新 modCount 的值
        this.modCount = l.modCount;
        // 更新子集的 size 大小
        size += cSize;
        return true;
    }

    // 返回 ListIterator 迭代器
    public Iterator<E> iterator() {
        return listIterator();
    }

    // 将游标置于指定位置
    public ListIterator<E> listIterator(final int index) {
        // 检查 List 集合结构是否一致
        checkForComodification();
        // 检查添加元素时的索引的边界
        rangeCheckForAdd(index);

        return new ListIterator<E>() {
            // 调用 AbstractList 的 listIterator 获取子集的 ListIterator 对象
            private final ListIterator<E> i = l.listIterator(index+offset);

            // 判断游标的右边是否有元素存在，即子集游标后边元素位置的索引小于子集元素个数即可
            public boolean hasNext() {
                return nextIndex() < size;
            }

            // 获取游标后面的元素
            public E next() {
                // 如果游标后面有元素
                if (hasNext())
                    // 返回游标后面的元素并且游标后移一位
                    return i.next();
                else
                    throw new NoSuchElementException();
            }

            // 判断游标前面是否有元素存在，即子集游标前面元素位置的索引大于 0
            public boolean hasPrevious() {
                return previousIndex() >= 0;
            }

            // 获取游标前面的元素
            public E previous() {
                // 如果游标前面有元素
                if (hasPrevious())
                    //则获取游标前面的元素，并且将 cursor 及 lastRet 同时设为 cursor - 1
                    return i.previous();
                else
                    // 否则抛出 NoSuchElementException() 异常
                    throw new NoSuchElementException();
            }

            // 获取子集的游标后面的元素
            public int nextIndex() {
                // AbstractList 集合的游标的右边位置减去 offset 就是子集的右边右面元素的位置
                return i.nextIndex() - offset;
            }

            // 获取子集游标前面元素的位置索引
            public int previousIndex() {
                return i.previousIndex() - offset;
            }

            // 删除子集迭代器最后一次操作的元素，即游标前面的那个元素
            public void remove() {
                // 删除迭代器最后一次操作的元素
                i.remove();
                // 更新 modCount 的值
                SubList.this.modCount = l.modCount;
                // size 大小 - 1
                size--;
            }

            // 更新迭代器最后一次操作的元素
            public void set(E e) {
                i.set(e);
            }

            // 在游标前面插入元素
            public void add(E e) {
                // 在游标前面插入元素
                i.add(e);
                // 更新 modCount 的值
                SubList.this.modCount = l.modCount;
                // size 大小 + 1
                size++;
            }
        };
    }

    // 获取集合指定范围的子集
    public List<E> subList(int fromIndex, int toIndex) {
        return new SubList<>(this, fromIndex, toIndex);
    }

    // 检查索引位置边界条件
    private void rangeCheck(int index) {
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }

    // 检查新增时候索引位置的边界条件
    private void rangeCheckForAdd(int index) {
        if (index < 0 || index > size)
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }

    // 返回 outOfBound 的信息
    private String outOfBoundsMsg(int index) {
        return "Index: "+index+", Size: "+size;
    }

    // 检查 List 集合的结构，即判断 modCount 是否一致，fail-fast 机制
    private void checkForComodification() {
        if (this.modCount != l.modCount)
            throw new ConcurrentModificationException();
    }
}

// 实现 RandomAccess 接口，标识该类拥有随机访问存储元素的能力
class RandomAccessSubList<E> extends SubList<E> implements RandomAccess {
    RandomAccessSubList(AbstractList<E> list, int fromIndex, int toIndex) {
        super(list, fromIndex, toIndex);
    }

    public List<E> subList(int fromIndex, int toIndex) {
        return new RandomAccessSubList<>(this, fromIndex, toIndex);
    }
}
