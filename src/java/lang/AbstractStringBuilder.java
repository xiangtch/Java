/*
 * Copyright (c) 2003, 2016, Oracle and/or its affiliates. All rights reserved.
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

package java.lang;

import sun.misc.FloatingDecimal;
import java.util.Arrays;

/**
 * A mutable sequence of characters.
 * <p>
 * Implements a modifiable string. At any point in time it contains some
 * particular sequence of characters, but the length and content of the
 * sequence can be changed through certain method calls.
 *
 * <p>Unless otherwise noted, passing a {@code null} argument to a constructor
 * or method in this class will cause a {@link NullPointerException} to be
 * thrown.
 *
 * @author      Michael McCloskey
 * @author      Martin Buchholz
 * @author      Ulf Zibis
 * @since       1.5
 */
// 这是一个抽象类，是 StringBuffer 与 StringBuilder 的父类，这个类并没有像 String 一样用 final 修饰
abstract class AbstractStringBuilder implements Appendable, CharSequence {
    /**
     * The value is used for character storage.
     */
    // value 没有用 final 修饰，可变，存储 char
    char[] value;

    /**
     * The count is the number of characters used.
     */
    // count 也没有用 final 修饰，也可变, 字符的长度(对外的字符长度)
    int count;

    /**
     * This no-arg constructor is necessary for serialization of subclasses.
     */
    AbstractStringBuilder() {
    }

    /**
     * Creates an AbstractStringBuilder of the specified capacity.
     */
    // 创建一个指定大小的 char[]
    AbstractStringBuilder(int capacity) {
        value = new char[capacity];
    }

    /**
     * Returns the length (character count).
     *
     * @return  the length of the sequence of characters currently
     *          represented by this object
     */
    // 目前代表的字符的长度(可能不为实际字符串的长度，因为这里有 length 和 capacity 两个概念)
    @Override
    public int length() {
        return count;
    }

    /**
     * Returns the current capacity. The capacity is the amount of storage
     * available for newly inserted characters, beyond which an allocation
     * will occur.
     *
     * @return  the current capacity
     */
    // 返回 AbstractStringBuilder 的容量
    public int capacity() {
        return value.length;
    }

    /**
     * Ensures that the capacity is at least equal to the specified minimum.
     * If the current capacity is less than the argument, then a new internal
     * array is allocated with greater capacity. The new capacity is the
     * larger of:
     * <ul>
     * <li>The {@code minimumCapacity} argument.
     * <li>Twice the old capacity, plus {@code 2}.
     * </ul>
     * If the {@code minimumCapacity} argument is nonpositive, this
     * method takes no action and simply returns.
     * Note that subsequent operations on this object can reduce the
     * actual capacity below that requested here.
     *
     * @param   minimumCapacity   the minimum desired capacity.
     */
    // 确保 value 数组的容量，minimumCapacity 最小容量值
    public void ensureCapacity(int minimumCapacity) {
        if (minimumCapacity > 0)
            ensureCapacityInternal(minimumCapacity);
    }

    /**
     * For positive values of {@code minimumCapacity}, this method
     * behaves like {@code ensureCapacity}, however it is never
     * synchronized.
     * If {@code minimumCapacity} is non positive due to numeric
     * overflow, this method throws {@code OutOfMemoryError}.
     */
    // 确保 value 数组的容量，minimumCapacity 最小容量值
    private void ensureCapacityInternal(int minimumCapacity) {
        // overflow-conscious code
        // minimumCapacity > value.length, 即需要的容量比实际字符串的长度要大，则对 value 进行扩容操作
        if (minimumCapacity - value.length > 0) {
            // 复制数组, 由于扩容操作，所以会导致 value.length(开辟的存储容量) 会比 count(实际字符长度) 大
            value = Arrays.copyOf(value,
                    newCapacity(minimumCapacity));
        }
    }

    /**
     * The maximum size of array to allocate (unless necessary).
     * Some VMs reserve some header words in an array.
     * Attempts to allocate larger arrays may result in
     * OutOfMemoryError: Requested array size exceeds VM limit
     */
    // 最大的数组容量，跟 JVM 的限制有关
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    /**
     * Returns a capacity at least as large as the given minimum capacity.
     * Returns the current capacity increased by the same amount + 2 if
     * that suffices.
     * Will not return a capacity greater than {@code MAX_ARRAY_SIZE}
     * unless the given minimum capacity is greater than that.
     *
     * @param  minCapacity the desired minimum capacity
     * @throws OutOfMemoryError if minCapacity is less than zero or
     *         greater than Integer.MAX_VALUE
     */
    private int newCapacity(int minCapacity) {
        // overflow-conscious code
        // 默认扩容为原来容量 + 1 的两倍
        int newCapacity = (value.length << 1) + 2;
        // 如果扩容后的容量比需要的容量还小，则直接扩容到需要的容量
        if (newCapacity - minCapacity < 0) {
            newCapacity = minCapacity;
        }
        // 如果扩容后的容量大于数组的最大容量，则进行高扩容，否则返回扩容后的容量
        return (newCapacity <= 0 || MAX_ARRAY_SIZE - newCapacity < 0)
            ? hugeCapacity(minCapacity)
            : newCapacity;
    }

    private int hugeCapacity(int minCapacity) {
        // 如果所需的最小容量超过了 Integer 的最大值，则抛出内存溢出异常
        if (Integer.MAX_VALUE - minCapacity < 0) { // overflow
            throw new OutOfMemoryError();
        }
        // 否则如果所需最小容量大于数组最大值，则返回所需的容量，否则返回数组最大值
        // 其实也就 8 位的扩容空间
        return (minCapacity > MAX_ARRAY_SIZE)
            ? minCapacity : MAX_ARRAY_SIZE;
    }

    /**
     * Attempts to reduce storage used for the character sequence.
     * If the buffer is larger than necessary to hold its current sequence of
     * characters, then it may be resized to become more space efficient.
     * Calling this method may, but is not required to, affect the value
     * returned by a subsequent call to the {@link #capacity()} method.
     */
    // 减少存储空间
    public void trimToSize() {
        // 如果字符串的实际长度小于开辟的数组长度(value.length 为开辟的容量，进行了扩容操作后，可能会存在空间冗余)
        if (count < value.length) {
            // 减少数据的开辟空间，即使用字符长度的空间即可
            value = Arrays.copyOf(value, count);
        }
    }

    /**
     * Sets the length of the character sequence.
     * The sequence is changed to a new character sequence
     * whose length is specified by the argument. For every nonnegative
     * index <i>k</i> less than {@code newLength}, the character at
     * index <i>k</i> in the new character sequence is the same as the
     * character at index <i>k</i> in the old sequence if <i>k</i> is less
     * than the length of the old character sequence; otherwise, it is the
     * null character {@code '\u005Cu0000'}.
     *
     * In other words, if the {@code newLength} argument is less than
     * the current length, the length is changed to the specified length.
     * <p>
     * If the {@code newLength} argument is greater than or equal
     * to the current length, sufficient null characters
     * ({@code '\u005Cu0000'}) are appended so that
     * length becomes the {@code newLength} argument.
     * <p>
     * The {@code newLength} argument must be greater than or equal
     * to {@code 0}.
     *
     * @param      newLength   the new length
     * @throws     IndexOutOfBoundsException  if the
     *               {@code newLength} argument is negative.
     */
    // 给字符序列设置长度
    public void setLength(int newLength) {
        if (newLength < 0)
            throw new StringIndexOutOfBoundsException(newLength);
        // 确保容量够用，newLength > value.length 的话会自动进行扩容操作，否则使用原来的容量空间
        ensureCapacityInternal(newLength);

        // 如果实际字符数量小于新设置的长度，多出来的空间用 '\0' 填充
        if (count < newLength) {
            // 多余的空间用 '\0' 填充
            Arrays.fill(value, count, newLength, '\0');
        }

        // 设置 count 为新的 newLength
        count = newLength;
    }

    /**
     * Returns the {@code char} value in this sequence at the specified index.
     * The first {@code char} value is at index {@code 0}, the next at index
     * {@code 1}, and so on, as in array indexing.
     * <p>
     * The index argument must be greater than or equal to
     * {@code 0}, and less than the length of this sequence.
     *
     * <p>If the {@code char} value specified by the index is a
     * <a href="Character.html#unicode">surrogate</a>, the surrogate
     * value is returned.
     *
     * @param      index   the index of the desired {@code char} value.
     * @return     the {@code char} value at the specified index.
     * @throws     IndexOutOfBoundsException  if {@code index} is
     *             negative or greater than or equal to {@code length()}.
     */
    // 返回指定位置的 char
    @Override
    public char charAt(int index) {
        // 边界值判断
        if ((index < 0) || (index >= count))
            throw new StringIndexOutOfBoundsException(index);
        return value[index];
    }

    /**
     * Returns the character (Unicode code point) at the specified
     * index. The index refers to {@code char} values
     * (Unicode code units) and ranges from {@code 0} to
     * {@link #length()}{@code  - 1}.
     *
     * <p> If the {@code char} value specified at the given index
     * is in the high-surrogate range, the following index is less
     * than the length of this sequence, and the
     * {@code char} value at the following index is in the
     * low-surrogate range, then the supplementary code point
     * corresponding to this surrogate pair is returned. Otherwise,
     * the {@code char} value at the given index is returned.
     *
     * @param      index the index to the {@code char} values
     * @return     the code point value of the character at the
     *             {@code index}
     * @exception  IndexOutOfBoundsException  if the {@code index}
     *             argument is negative or not less than the length of this
     *             sequence.
     */
    // 获取指定位置的字符的码元 Unicode code point，从索引位置往后判断(所以先判断高位代理再判断低位代理)
    public int codePointAt(int index) {
        if ((index < 0) || (index >= count)) {
            throw new StringIndexOutOfBoundsException(index);
        }
        // 获取指定索引位置的码元，先判断高位代理，在判断低位代理
        return Character.codePointAtImpl(value, index, count);
    }

    /**
     * Returns the character (Unicode code point) before the specified
     * index. The index refers to {@code char} values
     * (Unicode code units) and ranges from {@code 1} to {@link
     * #length()}.
     *
     * <p> If the {@code char} value at {@code (index - 1)}
     * is in the low-surrogate range, {@code (index - 2)} is not
     * negative, and the {@code char} value at {@code (index -
     * 2)} is in the high-surrogate range, then the
     * supplementary code point value of the surrogate pair is
     * returned. If the {@code char} value at {@code index -
     * 1} is an unpaired low-surrogate or a high-surrogate, the
     * surrogate value is returned.
     *
     * @param     index the index following the code point that should be returned
     * @return    the Unicode code point value before the given index.
     * @exception IndexOutOfBoundsException if the {@code index}
     *            argument is less than 1 or greater than the length
     *            of this sequence.
     */
    // 返回指定索引的前一位字符的码元，从后往前判断(所以先判断低位代理，在判断高位代理)
    public int codePointBefore(int index) {
        int i = index - 1;
        if ((i < 0) || (i >= count)) {
            throw new StringIndexOutOfBoundsException(index);
        }
        // 先判断低位代理，再判断高位代理
        return Character.codePointBeforeImpl(value, index, 0);
    }

    /**
     * Returns the number of Unicode code points in the specified text
     * range of this sequence. The text range begins at the specified
     * {@code beginIndex} and extends to the {@code char} at
     * index {@code endIndex - 1}. Thus the length (in
     * {@code char}s) of the text range is
     * {@code endIndex-beginIndex}. Unpaired surrogates within
     * this sequence count as one code point each.
     *
     * @param beginIndex the index to the first {@code char} of
     * the text range.
     * @param endIndex the index after the last {@code char} of
     * the text range.
     * @return the number of Unicode code points in the specified text
     * range
     * @exception IndexOutOfBoundsException if the
     * {@code beginIndex} is negative, or {@code endIndex}
     * is larger than the length of this sequence, or
     * {@code beginIndex} is larger than {@code endIndex}.
     */
    // 计算码元个数，因为 java 采用的是 utf-16 编码，对于 BMP 之外的字符会使用两位来表示一个字符
    public int codePointCount(int beginIndex, int endIndex) {
        if (beginIndex < 0 || endIndex > count || beginIndex > endIndex) {
            throw new IndexOutOfBoundsException();
        }
        return Character.codePointCountImpl(value, beginIndex, endIndex-beginIndex);
    }

    /**
     * Returns the index within this sequence that is offset from the
     * given {@code index} by {@code codePointOffset} code
     * points. Unpaired surrogates within the text range given by
     * {@code index} and {@code codePointOffset} count as
     * one code point each.
     *
     * @param index the index to be offset
     * @param codePointOffset the offset in code points
     * @return the index within this sequence
     * @exception IndexOutOfBoundsException if {@code index}
     *   is negative or larger then the length of this sequence,
     *   or if {@code codePointOffset} is positive and the subsequence
     *   starting with {@code index} has fewer than
     *   {@code codePointOffset} code points,
     *   or if {@code codePointOffset} is negative and the subsequence
     *   before {@code index} has fewer than the absolute value of
     *   {@code codePointOffset} code points.
     */
    // 根据 codePoint 偏移量来返回序列的索引
    public int offsetByCodePoints(int index, int codePointOffset) {
        if (index < 0 || index > count) {
            throw new IndexOutOfBoundsException();
        }
        // 返回在索引位置的指定 codePoint 偏移量的索引位置
        return Character.offsetByCodePointsImpl(value, 0, count,
                                                index, codePointOffset);
    }

    /**
     * Characters are copied from this sequence into the
     * destination character array {@code dst}. The first character to
     * be copied is at index {@code srcBegin}; the last character to
     * be copied is at index {@code srcEnd-1}. The total number of
     * characters to be copied is {@code srcEnd-srcBegin}. The
     * characters are copied into the subarray of {@code dst} starting
     * at index {@code dstBegin} and ending at index:
     * <pre>{@code
     * dstbegin + (srcEnd-srcBegin) - 1
     * }</pre>
     *
     * @param      srcBegin   start copying at this offset.
     * @param      srcEnd     stop copying at this offset.
     * @param      dst        the array to copy the data into.
     * @param      dstBegin   offset into {@code dst}.
     * @throws     IndexOutOfBoundsException  if any of the following is true:
     *             <ul>
     *             <li>{@code srcBegin} is negative
     *             <li>{@code dstBegin} is negative
     *             <li>the {@code srcBegin} argument is greater than
     *             the {@code srcEnd} argument.
     *             <li>{@code srcEnd} is greater than
     *             {@code this.length()}.
     *             <li>{@code dstBegin+srcEnd-srcBegin} is greater than
     *             {@code dst.length}
     *             </ul>
     */
    // 将序列的 srcBegin 到 srcEnd - 1,即 srcEnd - srcBegin 长度的子序列从 dst 的 dstBegin 位置开始复制到 dst 中
    public void getChars(int srcBegin, int srcEnd, char[] dst, int dstBegin)
    {
        if (srcBegin < 0)
            throw new StringIndexOutOfBoundsException(srcBegin);
        if ((srcEnd < 0) || (srcEnd > count))
            throw new StringIndexOutOfBoundsException(srcEnd);
        if (srcBegin > srcEnd)
            throw new StringIndexOutOfBoundsException("srcBegin > srcEnd");
        System.arraycopy(value, srcBegin, dst, dstBegin, srcEnd - srcBegin);
    }

    /**
     * The character at the specified index is set to {@code ch}. This
     * sequence is altered to represent a new character sequence that is
     * identical to the old character sequence, except that it contains the
     * character {@code ch} at position {@code index}.
     * <p>
     * The index argument must be greater than or equal to
     * {@code 0}, and less than the length of this sequence.
     *
     * @param      index   the index of the character to modify.
     * @param      ch      the new character.
     * @throws     IndexOutOfBoundsException  if {@code index} is
     *             negative or greater than or equal to {@code length()}.
     */
    // 修改指定索引位置的字符
    public void setCharAt(int index, char ch) {
        // 边界值校验
        if ((index < 0) || (index >= count))
            throw new StringIndexOutOfBoundsException(index);
        value[index] = ch;
    }

    /**
     * Appends the string representation of the {@code Object} argument.
     * <p>
     * The overall effect is exactly as if the argument were converted
     * to a string by the method {@link String#valueOf(Object)},
     * and the characters of that string were then
     * {@link #append(String) appended} to this character sequence.
     *
     * @param   obj   an {@code Object}.
     * @return  a reference to this object.
     */
    // 附加 Object 对象，要先转成 String
    public AbstractStringBuilder append(Object obj) {
        return append(String.valueOf(obj));
    }

    /**
     * Appends the specified string to this character sequence.
     * <p>
     * The characters of the {@code String} argument are appended, in
     * order, increasing the length of this sequence by the length of the
     * argument. If {@code str} is {@code null}, then the four
     * characters {@code "null"} are appended.
     * <p>
     * Let <i>n</i> be the length of this character sequence just prior to
     * execution of the {@code append} method. Then the character at
     * index <i>k</i> in the new character sequence is equal to the character
     * at index <i>k</i> in the old character sequence, if <i>k</i> is less
     * than <i>n</i>; otherwise, it is equal to the character at index
     * <i>k-n</i> in the argument {@code str}.
     *
     * @param   str   a string.
     * @return  a reference to this object.
     */
    // 在字符序列后追加 str, 这里 append 方法并没有重新创建对象而是使用原来的 value
    public AbstractStringBuilder append(String str) {
        // 如果 str 为 null，则追加 "null" 字符串
        if (str == null)
            return appendNull();
        // 获取要追加字符的长度
        int len = str.length();
        // 确保容量够用
        ensureCapacityInternal(count + len);
        // 将 str 从 srcBegin 位置 len 长度的字符串 copy 到 value 中，从 value 的 count 位置开始
        str.getChars(0, len, value, count);
        // 更新 count 的值
        count += len;
        return this;
    }

    // Documentation in subclasses because of synchro difference
    // 在字符序列后追加 StringBuffer 对象
    public AbstractStringBuilder append(StringBuffer sb) {
        if (sb == null)
            return appendNull();
        int len = sb.length();
        ensureCapacityInternal(count + len);
        sb.getChars(0, len, value, count);
        count += len;
        return this;
    }

    /**
     * @since 1.8
     */
    // 在字符序列后追加 AbstractStringBuilder 对象
    AbstractStringBuilder append(AbstractStringBuilder asb) {
        if (asb == null)
            return appendNull();
        int len = asb.length();
        ensureCapacityInternal(count + len);
        asb.getChars(0, len, value, count);
        count += len;
        return this;
    }

    // Documentation in subclasses because of synchro difference
    // 在字符序列之后追加 CharSequence 对象
    @Override
    public AbstractStringBuilder append(CharSequence s) {
        // 如果对象为 null, 则 appendNull
        if (s == null)
            return appendNull();
        // 如果是 String 类型，则调用 append(String) 的方法
        if (s instanceof String)
            return this.append((String)s);
        // 如果是 AbstractStringBuilder 对象，则调用 append(AbstractStringBuilder) 方法
        if (s instanceof AbstractStringBuilder)
            return this.append((AbstractStringBuilder)s);

        // 否则在字符序列之后追加指定长度的 CharSequence, 这里追加全部的 CharSequence
        return this.append(s, 0, s.length());
    }

    // 在字符序列后追加 "null" 字符串
    private AbstractStringBuilder appendNull() {
        int c = count;
        // 确保有 c + 4 的容量，不够的话自动扩容，value.length << 1 + 2
        ensureCapacityInternal(c + 4);
        // 往 value 中追加 'n'、'u'、'l'、'l' 字符
        final char[] value = this.value;
        value[c++] = 'n';
        value[c++] = 'u';
        value[c++] = 'l';
        value[c++] = 'l';
        count = c;
        return this;
    }

    /**
     * Appends a subsequence of the specified {@code CharSequence} to this
     * sequence.
     * <p>
     * Characters of the argument {@code s}, starting at
     * index {@code start}, are appended, in order, to the contents of
     * this sequence up to the (exclusive) index {@code end}. The length
     * of this sequence is increased by the value of {@code end - start}.
     * <p>
     * Let <i>n</i> be the length of this character sequence just prior to
     * execution of the {@code append} method. Then the character at
     * index <i>k</i> in this character sequence becomes equal to the
     * character at index <i>k</i> in this sequence, if <i>k</i> is less than
     * <i>n</i>; otherwise, it is equal to the character at index
     * <i>k+start-n</i> in the argument {@code s}.
     * <p>
     * If {@code s} is {@code null}, then this method appends
     * characters as if the s parameter was a sequence containing the four
     * characters {@code "null"}.
     *
     * @param   s the sequence to append.
     * @param   start   the starting index of the subsequence to be appended.
     * @param   end     the end index of the subsequence to be appended.
     * @return  a reference to this object.
     * @throws     IndexOutOfBoundsException if
     *             {@code start} is negative, or
     *             {@code start} is greater than {@code end} or
     *             {@code end} is greater than {@code s.length()}
     */
    // 在字符序列后追加指定长度的 CharSequence
    @Override
    public AbstractStringBuilder append(CharSequence s, int start, int end) {
        // 如果 s 为空，则令 s = "null"
        if (s == null)
            s = "null";
        // 边界条件的检验
        if ((start < 0) || (start > end) || (end > s.length()))
            throw new IndexOutOfBoundsException(
                "start " + start + ", end " + end + ", s.length() "
                + s.length());
        // 计算要追加的字符序列的长度
        int len = end - start;
        // 确保容量够用
        ensureCapacityInternal(count + len);
        // 遍历 s 往字符序列中追加字符
        for (int i = start, j = count; i < end; i++, j++)
            value[j] = s.charAt(i);
        // 更新 count 的值
        count += len;
        return this;
    }

    /**
     * Appends the string representation of the {@code char} array
     * argument to this sequence.
     * <p>
     * The characters of the array argument are appended, in order, to
     * the contents of this sequence. The length of this sequence
     * increases by the length of the argument.
     * <p>
     * The overall effect is exactly as if the argument were converted
     * to a string by the method {@link String#valueOf(char[])},
     * and the characters of that string were then
     * {@link #append(String) appended} to this character sequence.
     *
     * @param   str   the characters to be appended.
     * @return  a reference to this object.
     */
    // 在字符序列之后追加 char[]
    public AbstractStringBuilder append(char[] str) {
        // 获取数组长度
        int len = str.length;
        // 确保容量够用
        ensureCapacityInternal(count + len);
        // 将 str 从 0 位置开始，长度为 len 的子数组从 value 的 count 位置开始复制到 value 中
        System.arraycopy(str, 0, value, count, len);
        // 更新 count 的值
        count += len;
        return this;
    }

    /**
     * Appends the string representation of a subarray of the
     * {@code char} array argument to this sequence.
     * <p>
     * Characters of the {@code char} array {@code str}, starting at
     * index {@code offset}, are appended, in order, to the contents
     * of this sequence. The length of this sequence increases
     * by the value of {@code len}.
     * <p>
     * The overall effect is exactly as if the arguments were converted
     * to a string by the method {@link String#valueOf(char[],int,int)},
     * and the characters of that string were then
     * {@link #append(String) appended} to this character sequence.
     *
     * @param   str      the characters to be appended.
     * @param   offset   the index of the first {@code char} to append.
     * @param   len      the number of {@code char}s to append.
     * @return  a reference to this object.
     * @throws IndexOutOfBoundsException
     *         if {@code offset < 0} or {@code len < 0}
     *         or {@code offset+len > str.length}
     */
    // 在字符序列后追加 str[] 指定位置开始的 len 长度的子数组
    public AbstractStringBuilder append(char str[], int offset, int len) {
        // len > 0 ,确保容量够用
        if (len > 0)                // let arraycopy report AIOOBE for len < 0
            ensureCapacityInternal(count + len);
        // 将 str 的子数组复制到 value 中，从 count 位置开始, 即末尾追加
        System.arraycopy(str, offset, value, count, len);
        // 更新 count 的值
        count += len;
        return this;
    }

    /**
     * Appends the string representation of the {@code boolean}
     * argument to the sequence.
     * <p>
     * The overall effect is exactly as if the argument were converted
     * to a string by the method {@link String#valueOf(boolean)},
     * and the characters of that string were then
     * {@link #append(String) appended} to this character sequence.
     *
     * @param   b   a {@code boolean}.
     * @return  a reference to this object.
     */
    // 在字符序列后追加 boolean 类型的值, 其实是追加 "true" 和 "false"
    public AbstractStringBuilder append(boolean b) {
        if (b) {
            ensureCapacityInternal(count + 4);
            value[count++] = 't';
            value[count++] = 'r';
            value[count++] = 'u';
            value[count++] = 'e';
        } else {
            ensureCapacityInternal(count + 5);
            value[count++] = 'f';
            value[count++] = 'a';
            value[count++] = 'l';
            value[count++] = 's';
            value[count++] = 'e';
        }
        return this;
    }

    /**
     * Appends the string representation of the {@code char}
     * argument to this sequence.
     * <p>
     * The argument is appended to the contents of this sequence.
     * The length of this sequence increases by {@code 1}.
     * <p>
     * The overall effect is exactly as if the argument were converted
     * to a string by the method {@link String#valueOf(char)},
     * and the character in that string were then
     * {@link #append(String) appended} to this character sequence.
     *
     * @param   c   a {@code char}.
     * @return  a reference to this object.
     */
    // 在字符序列后追加 char 类型数据
    @Override
    public AbstractStringBuilder append(char c) {
        // 确保容量够用
        ensureCapacityInternal(count + 1);
        // 追加字符并更新 count 的值
        value[count++] = c;
        return this;
    }

    /**
     * Appends the string representation of the {@code int}
     * argument to this sequence.
     * <p>
     * The overall effect is exactly as if the argument were converted
     * to a string by the method {@link String#valueOf(int)},
     * and the characters of that string were then
     * {@link #append(String) appended} to this character sequence.
     *
     * @param   i   an {@code int}.
     * @return  a reference to this object.
     */
    // 在字符序列后追加 int 类型的数据
    public AbstractStringBuilder append(int i) {
        // 如果是最小值，则直接追加最小值,这里单独判断是因为下面在获取负数的长度的时候是获取对应的正数的长度 + 1
        // 而 Integer 的最小值的绝对值比最大值大 1，所以最小值单独判断
        if (i == Integer.MIN_VALUE) {
            append("-2147483648");
            return this;
        }
        // 获取 i 转成 String 的长度
        int appendedLength = (i < 0) ? Integer.stringSize(-i) + 1
                                     : Integer.stringSize(i);
        // 计算所需要的空间容量大小
        int spaceNeeded = count + appendedLength;
        // 确保空间够用
        ensureCapacityInternal(spaceNeeded);
        // 将 int 类型的 i 倒序插入 value 中，即从索引 spaceNeeded 位置开始
        Integer.getChars(i, spaceNeeded, value);
        // 更新 count 的值
        count = spaceNeeded;
        return this;
    }

    /**
     * Appends the string representation of the {@code long}
     * argument to this sequence.
     * <p>
     * The overall effect is exactly as if the argument were converted
     * to a string by the method {@link String#valueOf(long)},
     * and the characters of that string were then
     * {@link #append(String) appended} to this character sequence.
     *
     * @param   l   a {@code long}.
     * @return  a reference to this object.
     */
    // 在字符序列之后追加 long 类型的数据
    public AbstractStringBuilder append(long l) {
        // 跟 Integer 一样，对于最小值，直接追加对应的 String
        if (l == Long.MIN_VALUE) {
            append("-9223372036854775808");
            return this;
        }
        // 获取追加的 long 转成字符串的长度，负数 + 1
        int appendedLength = (l < 0) ? Long.stringSize(-l) + 1
                                     : Long.stringSize(l);
        // 计算所需要的容量空间
        int spaceNeeded = count + appendedLength;
        // 确保空间够用
        ensureCapacityInternal(spaceNeeded);
        // 将 l 追加进 value 中，倒序插入， spaceNeeded 为倒序的最大索引
        Long.getChars(l, spaceNeeded, value);
        // 更新 count 的值
        count = spaceNeeded;
        return this;
    }

    /**
     * Appends the string representation of the {@code float}
     * argument to this sequence.
     * <p>
     * The overall effect is exactly as if the argument were converted
     * to a string by the method {@link String#valueOf(float)},
     * and the characters of that string were then
     * {@link #append(String) appended} to this character sequence.
     *
     * @param   f   a {@code float}.
     * @return  a reference to this object.
     */
    // 在字符序列之后追加 float 类型的值
    public AbstractStringBuilder append(float f) {
        FloatingDecimal.appendTo(f,this);
        return this;
    }

    /**
     * Appends the string representation of the {@code double}
     * argument to this sequence.
     * <p>
     * The overall effect is exactly as if the argument were converted
     * to a string by the method {@link String#valueOf(double)},
     * and the characters of that string were then
     * {@link #append(String) appended} to this character sequence.
     *
     * @param   d   a {@code double}.
     * @return  a reference to this object.
     */
    // 在字符序列之后追加 double 类型的数据
    public AbstractStringBuilder append(double d) {
        FloatingDecimal.appendTo(d,this);
        return this;
    }

    /**
     * Removes the characters in a substring of this sequence.
     * The substring begins at the specified {@code start} and extends to
     * the character at index {@code end - 1} or to the end of the
     * sequence if no such character exists. If
     * {@code start} is equal to {@code end}, no changes are made.
     *
     * @param      start  The beginning index, inclusive. 包括
     * @param      end    The ending index, exclusive. 不包括
     * @return     This object.
     * @throws     StringIndexOutOfBoundsException  if {@code start}
     *             is negative, greater than {@code length()}, or
     *             greater than {@code end}.
     */
    // 在字符序列中删除一个子字符序列
    public AbstractStringBuilder delete(int start, int end) {
        // 边界条件的前置校验
        if (start < 0)
            throw new StringIndexOutOfBoundsException(start);
        if (end > count)
            end = count;
        if (start > end)
            throw new StringIndexOutOfBoundsException();
        int len = end - start;
        if (len > 0) {
            // 将 value 的 start+len 之后长度为 count - end 的字符从 value 的 start 位置开始复制到 value 中，实现子字符串的删除
            System.arraycopy(value, start+len, value, start, count-end);
            // 更新 count 的值
            count -= len;
        }
        return this;
    }

    /**
     * Appends the string representation of the {@code codePoint}
     * argument to this sequence.
     *
     * <p> The argument is appended to the contents of this sequence.
     * The length of this sequence increases by
     * {@link Character#charCount(int) Character.charCount(codePoint)}.
     *
     * <p> The overall effect is exactly as if the argument were
     * converted to a {@code char} array by the method
     * {@link Character#toChars(int)} and the character in that array
     * were then {@link #append(char[]) appended} to this character
     * sequence.
     *
     * @param   codePoint   a Unicode code point
     * @return  a reference to this object.
     * @exception IllegalArgumentException if the specified
     * {@code codePoint} isn't a valid Unicode code point
     */
    // 在字符序列之后根据 codePoint(Unicode) 追加相应的字符
    public AbstractStringBuilder appendCodePoint(int codePoint) {
        // final 修饰，线程安全
        final int count = this.count;

        // 判断 codePoint 是否为 BMP 编码，BMP 编码一个 char 就可以完成编码
        if (Character.isBmpCodePoint(codePoint)) {
            // 确保容量够用
            ensureCapacityInternal(count + 1);
            // 直接转成 char 存入 value 中
            value[count] = (char) codePoint;
            // 更新 count 的值
            this.count = count + 1;
        } else if (Character.isValidCodePoint(codePoint)) { // 否则校验是否为有效字符编码
            // 如果是字符编码的话应该就是 BMP 之外的编码，需要两个 char 来完成编码
            ensureCapacityInternal(count + 2);
            // 代理存储 codePoint 到 value 中
            Character.toSurrogates(codePoint, value, count);
            // 更新 count 的值
            this.count = count + 2;
        } else { // 否则抛出异常
            throw new IllegalArgumentException();
        }
        return this;
    }

    /**
     * Removes the {@code char} at the specified position in this
     * sequence. This sequence is shortened by one {@code char}.
     *
     * <p>Note: If the character at the given index is a supplementary
     * character, this method does not remove the entire character. If
     * correct handling of supplementary characters is required,
     * determine the number of {@code char}s to remove by calling
     * {@code Character.charCount(thisSequence.codePointAt(index))},
     * where {@code thisSequence} is this sequence.
     *
     * @param       index  Index of {@code char} to remove
     * @return      This object.
     * @throws      StringIndexOutOfBoundsException  if the {@code index}
     *              is negative or greater than or equal to
     *              {@code length()}.
     */
    // 根据索引位置删除字符序列中指定的元素
    public AbstractStringBuilder deleteCharAt(int index) {
        // 对参数进行前置校验
        if ((index < 0) || (index >= count))
            throw new StringIndexOutOfBoundsException(index);
        // 将 value 的 index+1 之后的字符复制到 value 的 index 后面
        System.arraycopy(value, index+1, value, index, count-index-1);
        // 更新 count 的值
        count--;
        return this;
    }

    /**
     * Replaces the characters in a substring of this sequence
     * with characters in the specified {@code String}. The substring
     * begins at the specified {@code start} and extends to the character
     * at index {@code end - 1} or to the end of the
     * sequence if no such character exists. First the
     * characters in the substring are removed and then the specified
     * {@code String} is inserted at {@code start}. (This
     * sequence will be lengthened to accommodate the
     * specified String if necessary.)
     *
     * @param      start    The beginning index, inclusive.
     * @param      end      The ending index, exclusive.
     * @param      str   String that will replace previous contents.
     * @return     This object.
     * @throws     StringIndexOutOfBoundsException  if {@code start}
     *             is negative, greater than {@code length()}, or
     *             greater than {@code end}.
     */
    // 使用 str 替换字符序列中从 start 到 end(不包含) 的子字符序列
    public AbstractStringBuilder replace(int start, int end, String str) {
        // 参数前置校验
        if (start < 0)
            throw new StringIndexOutOfBoundsException(start);
        if (start > count)
            throw new StringIndexOutOfBoundsException("start > length()");
        if (start > end)
            throw new StringIndexOutOfBoundsException("start > end");

        if (end > count)
            end = count;
        // 获取 str 的长度
        int len = str.length();
        // 计算新字符序列的长度
        int newCount = count + len - (end - start);
        // 确保容量够用
        ensureCapacityInternal(newCount);
        // 将 value 中的 end 之后的字符序列复制到 value 的 start+len 之后
        System.arraycopy(value, end, value, start + len, count - end);
        // 将 str 复制到 value 中，从 start 位置开始
        str.getChars(value, start);
        // 更新 count 的值
        count = newCount;
        return this;
    }

    /**
     * Returns a new {@code String} that contains a subsequence of
     * characters currently contained in this character sequence. The
     * substring begins at the specified index and extends to the end of
     * this sequence.
     *
     * @param      start    The beginning index, inclusive.
     * @return     The new string.
     * @throws     StringIndexOutOfBoundsException  if {@code start} is
     *             less than zero, or greater than the length of this object.
     */
    // 截取子字符串，从 start 位置开始
    public String substring(int start) {
        return substring(start, count);
    }

    /**
     * Returns a new character sequence that is a subsequence of this sequence.
     *
     * <p> An invocation of this method of the form
     *
     * <pre>{@code
     * sb.subSequence(begin,&nbsp;end)}</pre>
     *
     * behaves in exactly the same way as the invocation
     *
     * <pre>{@code
     * sb.substring(begin,&nbsp;end)}</pre>
     *
     * This method is provided so that this class can
     * implement the {@link CharSequence} interface.
     *
     * @param      start   the start index, inclusive.
     * @param      end     the end index, exclusive.
     * @return     the specified subsequence.
     *
     * @throws  IndexOutOfBoundsException
     *          if {@code start} or {@code end} are negative,
     *          if {@code end} is greater than {@code length()},
     *          or if {@code start} is greater than {@code end}
     * @spec JSR-51
     */
    @Override
    public CharSequence subSequence(int start, int end) {
        return substring(start, end);
    }

    /**
     * Returns a new {@code String} that contains a subsequence of
     * characters currently contained in this sequence. The
     * substring begins at the specified {@code start} and
     * extends to the character at index {@code end - 1}.
     *
     * @param      start    The beginning index, inclusive.
     * @param      end      The ending index, exclusive.
     * @return     The new string.
     * @throws     StringIndexOutOfBoundsException  if {@code start}
     *             or {@code end} are negative or greater than
     *             {@code length()}, or {@code start} is
     *             greater than {@code end}.
     */
    // 在字符序列的指定位置截取子字符序列
    public String substring(int start, int end) {
        // 边界条件前置校验
        if (start < 0)
            throw new StringIndexOutOfBoundsException(start);
        if (end > count)
            throw new StringIndexOutOfBoundsException(end);
        if (start > end)
            throw new StringIndexOutOfBoundsException(end - start);
        // 创建一个截取后的 String
        return new String(value, start, end - start);
    }

    /**
     * Inserts the string representation of a subarray of the {@code str}
     * array argument into this sequence. The subarray begins at the
     * specified {@code offset} and extends {@code len} {@code char}s.
     * The characters of the subarray are inserted into this sequence at
     * the position indicated by {@code index}. The length of this
     * sequence increases by {@code len} {@code char}s.
     *
     * @param      index    position at which to insert subarray.
     * @param      str       A {@code char} array.
     * @param      offset   the index of the first {@code char} in subarray to
     *             be inserted.
     * @param      len      the number of {@code char}s in the subarray to
     *             be inserted.
     * @return     This object
     * @throws     StringIndexOutOfBoundsException  if {@code index}
     *             is negative or greater than {@code length()}, or
     *             {@code offset} or {@code len} are negative, or
     *             {@code (offset+len)} is greater than
     *             {@code str.length}.
     */
    // 从字符序列的 index 位置开始往字符序列中插入 str 的子字符序列(从 offset 位置开始，len 长度)
    public AbstractStringBuilder insert(int index, char[] str, int offset,
                                        int len)
    {
        // 前置边界条件检验
        if ((index < 0) || (index > length()))
            throw new StringIndexOutOfBoundsException(index);
        if ((offset < 0) || (len < 0) || (offset > str.length - len))
            throw new StringIndexOutOfBoundsException(
                "offset " + offset + ", len " + len + ", str.length "
                + str.length);
        // 确保容量够用
        ensureCapacityInternal(count + len);
        // 将 value 的 index 后面的字符复制到 value  的 index+len 索引后面，复制的长度为 count - index
        System.arraycopy(value, index, value, index + len, count - index);
        // 将 str 从 offset 索引位置开始，将 len 长度的字符串复制到 value 的 index 索引后面
        System.arraycopy(str, offset, value, index, len);
        // 更新 count 的值
        count += len;
        return this;
    }

    /**
     * Inserts the string representation of the {@code Object}
     * argument into this character sequence.
     * <p>
     * The overall effect is exactly as if the second argument were
     * converted to a string by the method {@link String#valueOf(Object)},
     * and the characters of that string were then
     * {@link #insert(int,String) inserted} into this character
     * sequence at the indicated offset.
     * <p>
     * The {@code offset} argument must be greater than or equal to
     * {@code 0}, and less than or equal to the {@linkplain #length() length}
     * of this sequence.
     *
     * @param      offset   the offset.
     * @param      obj      an {@code Object}.
     * @return     a reference to this object.
     * @throws     StringIndexOutOfBoundsException  if the offset is invalid.
     */
    // 将 Object 类型的值 obj 从 offset 位置插入进字符序列中，转成 String 插入
    public AbstractStringBuilder insert(int offset, Object obj) {
        return insert(offset, String.valueOf(obj));
    }

    /**
     * Inserts the string into this character sequence.
     * <p>
     * The characters of the {@code String} argument are inserted, in
     * order, into this sequence at the indicated offset, moving up any
     * characters originally above that position and increasing the length
     * of this sequence by the length of the argument. If
     * {@code str} is {@code null}, then the four characters
     * {@code "null"} are inserted into this sequence.
     * <p>
     * The character at index <i>k</i> in the new character sequence is
     * equal to:
     * <ul>
     * <li>the character at index <i>k</i> in the old character sequence, if
     * <i>k</i> is less than {@code offset}
     * <li>the character at index <i>k</i>{@code -offset} in the
     * argument {@code str}, if <i>k</i> is not less than
     * {@code offset} but is less than {@code offset+str.length()}
     * <li>the character at index <i>k</i>{@code -str.length()} in the
     * old character sequence, if <i>k</i> is not less than
     * {@code offset+str.length()}
     * </ul><p>
     * The {@code offset} argument must be greater than or equal to
     * {@code 0}, and less than or equal to the {@linkplain #length() length}
     * of this sequence.
     *
     * @param      offset   the offset.
     * @param      str      a string.
     * @return     a reference to this object.
     * @throws     StringIndexOutOfBoundsException  if the offset is invalid.
     */
    // 将 String 类型的数据从 offset 位置开始插入字符序列中
    public AbstractStringBuilder insert(int offset, String str) {
        // 边界条件的前置校验
        if ((offset < 0) || (offset > length()))
            throw new StringIndexOutOfBoundsException(offset);
        // 如果 str == null 则插入 "null" 字符串
        if (str == null)
            str = "null";
        // 获取字符串的长度
        int len = str.length();
        // 确保容量够用
        ensureCapacityInternal(count + len);
        // 将 value 的 offset 位置开始，count - offset 长度字符串复制到 value 的 offset+len 索引之后
        System.arraycopy(value, offset, value, offset + len, count - offset);
        // 将 str 复制到 value 中，从 offset 位置开始复制
        str.getChars(value, offset);
        // 更新 count 的值
        count += len;
        return this;
    }

    /**
     * Inserts the string representation of the {@code char} array
     * argument into this sequence.
     * <p>
     * The characters of the array argument are inserted into the
     * contents of this sequence at the position indicated by
     * {@code offset}. The length of this sequence increases by
     * the length of the argument.
     * <p>
     * The overall effect is exactly as if the second argument were
     * converted to a string by the method {@link String#valueOf(char[])},
     * and the characters of that string were then
     * {@link #insert(int,String) inserted} into this character
     * sequence at the indicated offset.
     * <p>
     * The {@code offset} argument must be greater than or equal to
     * {@code 0}, and less than or equal to the {@linkplain #length() length}
     * of this sequence.
     *
     * @param      offset   the offset.
     * @param      str      a character array.
     * @return     a reference to this object.
     * @throws     StringIndexOutOfBoundsException  if the offset is invalid.
     */
    // 将 char[] 类型的 str 从 offset 位置开始插入到字符序列中
    public AbstractStringBuilder insert(int offset, char[] str) {
        // 前置边界条件检验
        if ((offset < 0) || (offset > length()))
            throw new StringIndexOutOfBoundsException(offset);
        // 获取 str 的长度
        int len = str.length;
        // 确保容量够用
        ensureCapacityInternal(count + len);
        // 将 value 的 offset 之后的 count - offset 长度的字符串复制到 value 中，从 offset + len 位置开始
        System.arraycopy(value, offset, value, offset + len, count - offset);
        // 将 str 的 0 开始长度为 len 的字符串复制到 offset 中，索引从 offset 位置开始
        System.arraycopy(str, 0, value, offset, len);
        // 更新 count 的值
        count += len;
        return this;
    }

    /**
     * Inserts the specified {@code CharSequence} into this sequence.
     * <p>
     * The characters of the {@code CharSequence} argument are inserted,
     * in order, into this sequence at the indicated offset, moving up
     * any characters originally above that position and increasing the length
     * of this sequence by the length of the argument s.
     * <p>
     * The result of this method is exactly the same as if it were an
     * invocation of this object's
     * {@link #insert(int,CharSequence,int,int) insert}(dstOffset, s, 0, s.length())
     * method.
     *
     * <p>If {@code s} is {@code null}, then the four characters
     * {@code "null"} are inserted into this sequence.
     *
     * @param      dstOffset   the offset.
     * @param      s the sequence to be inserted
     * @return     a reference to this object.
     * @throws     IndexOutOfBoundsException  if the offset is invalid.
     */
    // 在字符序列的 dstOffset 位置开始插入 CharSequence 对象
    public AbstractStringBuilder insert(int dstOffset, CharSequence s) {
        // 如果 s == null 则插入 "null" 字符串
        if (s == null)
            s = "null";
        // 判断 s 是不是 String 类型，如果是的话直接按照 String 类型的插入方法来插入
        if (s instanceof String)
            return this.insert(dstOffset, (String)s);
        // 否则调用插入 CharSequence 方法来进行 CharSequence 类型数据的插入
        return this.insert(dstOffset, s, 0, s.length());
    }

    /**
     * Inserts a subsequence of the specified {@code CharSequence} into
     * this sequence.
     * <p>
     * The subsequence of the argument {@code s} specified by
     * {@code start} and {@code end} are inserted,
     * in order, into this sequence at the specified destination offset, moving
     * up any characters originally above that position. The length of this
     * sequence is increased by {@code end - start}.
     * <p>
     * The character at index <i>k</i> in this sequence becomes equal to:
     * <ul>
     * <li>the character at index <i>k</i> in this sequence, if
     * <i>k</i> is less than {@code dstOffset}
     * <li>the character at index <i>k</i>{@code +start-dstOffset} in
     * the argument {@code s}, if <i>k</i> is greater than or equal to
     * {@code dstOffset} but is less than {@code dstOffset+end-start}
     * <li>the character at index <i>k</i>{@code -(end-start)} in this
     * sequence, if <i>k</i> is greater than or equal to
     * {@code dstOffset+end-start}
     * </ul><p>
     * The {@code dstOffset} argument must be greater than or equal to
     * {@code 0}, and less than or equal to the {@linkplain #length() length}
     * of this sequence.
     * <p>The start argument must be nonnegative, and not greater than
     * {@code end}.
     * <p>The end argument must be greater than or equal to
     * {@code start}, and less than or equal to the length of s.
     *
     * <p>If {@code s} is {@code null}, then this method inserts
     * characters as if the s parameter was a sequence containing the four
     * characters {@code "null"}.
     *
     * @param      dstOffset   the offset in this sequence.
     * @param      s       the sequence to be inserted.
     * @param      start   the starting index of the subsequence to be inserted.
     * @param      end     the end index of the subsequence to be inserted.
     * @return     a reference to this object.
     * @throws     IndexOutOfBoundsException  if {@code dstOffset}
     *             is negative or greater than {@code this.length()}, or
     *              {@code start} or {@code end} are negative, or
     *              {@code start} is greater than {@code end} or
     *              {@code end} is greater than {@code s.length()}
     */
    // 将 CharSequence 的从 start 到 end 索引的子字符序列插入到字符序列中，从 dstOffset 索引位置开始
     public AbstractStringBuilder insert(int dstOffset, CharSequence s,
                                         int start, int end) {
         // 如果 s == null 插入 "null" 字符串
        if (s == null)
            s = "null";
        // 对边界条件进行前置检验
        if ((dstOffset < 0) || (dstOffset > this.length()))
            throw new IndexOutOfBoundsException("dstOffset "+dstOffset);
        if ((start < 0) || (end < 0) || (start > end) || (end > s.length()))
            throw new IndexOutOfBoundsException(
                "start " + start + ", end " + end + ", s.length() "
                + s.length());
        // 获取要插入的字符序列的长度
        int len = end - start;
        // 确保容量够用
        ensureCapacityInternal(count + len);
        // 将 value 的 dstOffset 索引之后的 count - dstOffset 长度的字符复制到 value 的 dstOffset + len 位置之后
        System.arraycopy(value, dstOffset, value, dstOffset + len,
                         count - dstOffset);
        // 将 CharSequence 的字符一个个添加到 value 中
        for (int i=start; i<end; i++)
            value[dstOffset++] = s.charAt(i);
        // 更新 count 的值
        count += len;
        return this;
    }

    /**
     * Inserts the string representation of the {@code boolean}
     * argument into this sequence.
     * <p>
     * The overall effect is exactly as if the second argument were
     * converted to a string by the method {@link String#valueOf(boolean)},
     * and the characters of that string were then
     * {@link #insert(int,String) inserted} into this character
     * sequence at the indicated offset.
     * <p>
     * The {@code offset} argument must be greater than or equal to
     * {@code 0}, and less than or equal to the {@linkplain #length() length}
     * of this sequence.
     *
     * @param      offset   the offset.
     * @param      b        a {@code boolean}.
     * @return     a reference to this object.
     * @throws     StringIndexOutOfBoundsException  if the offset is invalid.
     */
    // 将 boolean 类型的值从字符序列的 offset 的位置开始插入到字符序列中，转成 String 进行插入
    public AbstractStringBuilder insert(int offset, boolean b) {
        return insert(offset, String.valueOf(b));
    }

    /**
     * Inserts the string representation of the {@code char}
     * argument into this sequence.
     * <p>
     * The overall effect is exactly as if the second argument were
     * converted to a string by the method {@link String#valueOf(char)},
     * and the character in that string were then
     * {@link #insert(int,String) inserted} into this character
     * sequence at the indicated offset.
     * <p>
     * The {@code offset} argument must be greater than or equal to
     * {@code 0}, and less than or equal to the {@linkplain #length() length}
     * of this sequence.
     *
     * @param      offset   the offset.
     * @param      c        a {@code char}.
     * @return     a reference to this object.
     * @throws     IndexOutOfBoundsException  if the offset is invalid.
     */
    // 将 char 类型的数据类型从 offset 位置插入到字符序列中
    public AbstractStringBuilder insert(int offset, char c) {
        // 确保容量够用
        ensureCapacityInternal(count + 1);
        // 将 value 的 offset 位置开始 count - offset 长度的字符串复制到 value 中，从 offset + 1 位置开始
        System.arraycopy(value, offset, value, offset + 1, count - offset);
        // 将 c 插入到 value 中
        value[offset] = c;
        // 更新 count 的值
        count += 1;
        return this;
    }

    /**
     * Inserts the string representation of the second {@code int}
     * argument into this sequence.
     * <p>
     * The overall effect is exactly as if the second argument were
     * converted to a string by the method {@link String#valueOf(int)},
     * and the characters of that string were then
     * {@link #insert(int,String) inserted} into this character
     * sequence at the indicated offset.
     * <p>
     * The {@code offset} argument must be greater than or equal to
     * {@code 0}, and less than or equal to the {@linkplain #length() length}
     * of this sequence.
     *
     * @param      offset   the offset.
     * @param      i        an {@code int}.
     * @return     a reference to this object.
     * @throws     StringIndexOutOfBoundsException  if the offset is invalid.
     */
    // 将 int 类型的 i 从字符序列的 offset 位置开始复制到字符序列中，将 int 转成 String 类型插入
    public AbstractStringBuilder insert(int offset, int i) {
        return insert(offset, String.valueOf(i));
    }

    /**
     * Inserts the string representation of the {@code long}
     * argument into this sequence.
     * <p>
     * The overall effect is exactly as if the second argument were
     * converted to a string by the method {@link String#valueOf(long)},
     * and the characters of that string were then
     * {@link #insert(int,String) inserted} into this character
     * sequence at the indicated offset.
     * <p>
     * The {@code offset} argument must be greater than or equal to
     * {@code 0}, and less than or equal to the {@linkplain #length() length}
     * of this sequence.
     *
     * @param      offset   the offset.
     * @param      l        a {@code long}.
     * @return     a reference to this object.
     * @throws     StringIndexOutOfBoundsException  if the offset is invalid.
     */
    // 将 long 类型的 l 从字符序列的 offset 位置开始复制到字符序列中，将 long 转成 String 类型插入
    public AbstractStringBuilder insert(int offset, long l) {
        return insert(offset, String.valueOf(l));
    }

    /**
     * Inserts the string representation of the {@code float}
     * argument into this sequence.
     * <p>
     * The overall effect is exactly as if the second argument were
     * converted to a string by the method {@link String#valueOf(float)},
     * and the characters of that string were then
     * {@link #insert(int,String) inserted} into this character
     * sequence at the indicated offset.
     * <p>
     * The {@code offset} argument must be greater than or equal to
     * {@code 0}, and less than or equal to the {@linkplain #length() length}
     * of this sequence.
     *
     * @param      offset   the offset.
     * @param      f        a {@code float}.
     * @return     a reference to this object.
     * @throws     StringIndexOutOfBoundsException  if the offset is invalid.
     */
    // 将 float 类型的数据从字符序列的 offset 位置开始复制到字符序列中，将 float 转成 String 类型插入
    public AbstractStringBuilder insert(int offset, float f) {
        return insert(offset, String.valueOf(f));
    }

    /**
     * Inserts the string representation of the {@code double}
     * argument into this sequence.
     * <p>
     * The overall effect is exactly as if the second argument were
     * converted to a string by the method {@link String#valueOf(double)},
     * and the characters of that string were then
     * {@link #insert(int,String) inserted} into this character
     * sequence at the indicated offset.
     * <p>
     * The {@code offset} argument must be greater than or equal to
     * {@code 0}, and less than or equal to the {@linkplain #length() length}
     * of this sequence.
     *
     * @param      offset   the offset.
     * @param      d        a {@code double}.
     * @return     a reference to this object.
     * @throws     StringIndexOutOfBoundsException  if the offset is invalid.
     */
    // 将 double 类型的数据从字符序列的 offset 位置开始复制到字符序列中，将 double 转成 String 类型插入
    public AbstractStringBuilder insert(int offset, double d) {
        return insert(offset, String.valueOf(d));
    }

    /**
     * Returns the index within this string of the first occurrence of the
     * specified substring. The integer returned is the smallest value
     * <i>k</i> such that:
     * <pre>{@code
     * this.toString().startsWith(str, <i>k</i>)
     * }</pre>
     * is {@code true}.
     *
     * @param   str   any string.
     * @return  if the string argument occurs as a substring within this
     *          object, then the index of the first character of the first
     *          such substring is returned; if it does not occur as a
     *          substring, {@code -1} is returned.
     */
    // 返回第一次出现 str 字符串的索引
    public int indexOf(String str) {
        return indexOf(str, 0);
    }

    /**
     * Returns the index within this string of the first occurrence of the
     * specified substring, starting at the specified index.  The integer
     * returned is the smallest value {@code k} for which:
     * <pre>{@code
     *     k >= Math.min(fromIndex, this.length()) &&
     *                   this.toString().startsWith(str, k)
     * }</pre>
     * If no such value of <i>k</i> exists, then -1 is returned.
     *
     * @param   str         the substring for which to search.
     * @param   fromIndex   the index from which to start the search.
     * @return  the index within this string of the first occurrence of the
     *          specified substring, starting at the specified index.
     */
    // 从字符序列的 fromIndex 位置开始的子字符序列中查找 str 第一次出现的位置的索引
    public int indexOf(String str, int fromIndex) {
        return String.indexOf(value, 0, count, str, fromIndex);
    }

    /**
     * Returns the index within this string of the rightmost occurrence
     * of the specified substring.  The rightmost empty string "" is
     * considered to occur at the index value {@code this.length()}.
     * The returned index is the largest value <i>k</i> such that
     * <pre>{@code
     * this.toString().startsWith(str, k)
     * }</pre>
     * is true.
     *
     * @param   str   the substring to search for.
     * @return  if the string argument occurs one or more times as a substring
     *          within this object, then the index of the first character of
     *          the last such substring is returned. If it does not occur as
     *          a substring, {@code -1} is returned.
     */
    // 查询 str 在字符序列中最后一次出现的位置
    public int lastIndexOf(String str) {
        return lastIndexOf(str, count);
    }

    /**
     * Returns the index within this string of the last occurrence of the
     * specified substring. The integer returned is the largest value <i>k</i>
     * such that:
     * <pre>{@code
     *     k <= Math.min(fromIndex, this.length()) &&
     *                   this.toString().startsWith(str, k)
     * }</pre>
     * If no such value of <i>k</i> exists, then -1 is returned.
     *
     * @param   str         the substring to search for.
     * @param   fromIndex   the index to start the search from.
     * @return  the index within this sequence of the last occurrence of the
     *          specified substring.
     */
    // 从字符序列的子字符序列中查找 str 最后一次出现的位置
    public int lastIndexOf(String str, int fromIndex) {
        return String.lastIndexOf(value, 0, count, str, fromIndex);
    }

    /**
     * Causes this character sequence to be replaced by the reverse of
     * the sequence. If there are any surrogate pairs included in the
     * sequence, these are treated as single characters for the
     * reverse operation. Thus, the order of the high-low surrogates
     * is never reversed.
     *
     * Let <i>n</i> be the character length of this character sequence
     * (not the length in {@code char} values) just prior to
     * execution of the {@code reverse} method. Then the
     * character at index <i>k</i> in the new character sequence is
     * equal to the character at index <i>n-k-1</i> in the old
     * character sequence.
     *
     * <p>Note that the reverse operation may result in producing
     * surrogate pairs that were unpaired low-surrogates and
     * high-surrogates before the operation. For example, reversing
     * "\u005CuDC00\u005CuD800" produces "\u005CuD800\u005CuDC00" which is
     * a valid surrogate pair.
     *
     * @return  a reference to this object.
     */
    // 翻转字符
    public AbstractStringBuilder reverse() {
        boolean hasSurrogates = false;
        int n = count - 1;
        // 从中间开始翻转
        for (int j = (n-1) >> 1; j >= 0; j--) {
            int k = n - j;
            char cj = value[j];
            char ck = value[k];
            value[j] = ck;
            value[k] = cj;
            // 判断字符是否为代理字符
            if (Character.isSurrogate(cj) ||
                Character.isSurrogate(ck)) {
                hasSurrogates = true;
            }
        }
        if (hasSurrogates) {
            reverseAllValidSurrogatePairs();
        }
        return this;
    }

    /** Outlined helper method for reverse() */
    // 翻转所有的代理值，代理时，低位代理的索引值比高位代理要大
    private void reverseAllValidSurrogatePairs() {
        // 遍历字符调整代理字符
        for (int i = 0; i < count - 1; i++) {
            char c2 = value[i];
            // 互换翻转后的低位代理与高位代理
            if (Character.isLowSurrogate(c2)) {
                char c1 = value[i + 1];
                if (Character.isHighSurrogate(c1)) {
                    value[i++] = c1;
                    value[i] = c2;
                }
            }
        }
    }

    /**
     * Returns a string representing the data in this sequence.
     * A new {@code String} object is allocated and initialized to
     * contain the character sequence currently represented by this
     * object. This {@code String} is then returned. Subsequent
     * changes to this sequence do not affect the contents of the
     * {@code String}.
     *
     * @return  a string representation of this sequence of characters.
     */
    @Override
    public abstract String toString();

    /**
     * Needed by {@code String} for the contentEquals method.
     */
    final char[] getValue() {
        return value;
    }

}
