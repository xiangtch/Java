/*
 * Copyright (c) 2003, 2013, Oracle and/or its affiliates. All rights reserved.
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


/**
 * A mutable sequence of characters.  This class provides an API compatible
 * with {@code StringBuffer}, but with no guarantee of synchronization.
 * This class is designed for use as a drop-in replacement for
 * {@code StringBuffer} in places where the string buffer was being
 * used by a single thread (as is generally the case).   Where possible,
 * it is recommended that this class be used in preference to
 * {@code StringBuffer} as it will be faster under most implementations.
 *
 * <p>The principal operations on a {@code StringBuilder} are the
 * {@code append} and {@code insert} methods, which are
 * overloaded so as to accept data of any type. Each effectively
 * converts a given datum to a string and then appends or inserts the
 * characters of that string to the string builder. The
 * {@code append} method always adds these characters at the end
 * of the builder; the {@code insert} method adds the characters at
 * a specified point.
 * <p>
 * For example, if {@code z} refers to a string builder object
 * whose current contents are "{@code start}", then
 * the method call {@code z.append("le")} would cause the string
 * builder to contain "{@code startle}", whereas
 * {@code z.insert(4, "le")} would alter the string builder to
 * contain "{@code starlet}".
 * <p>
 * In general, if sb refers to an instance of a {@code StringBuilder},
 * then {@code sb.append(x)} has the same effect as
 * {@code sb.insert(sb.length(), x)}.
 * <p>
 * Every string builder has a capacity. As long as the length of the
 * character sequence contained in the string builder does not exceed
 * the capacity, it is not necessary to allocate a new internal
 * buffer. If the internal buffer overflows, it is automatically made larger.
 *
 * <p>Instances of {@code StringBuilder} are not safe for
 * use by multiple threads. If such synchronization is required then it is
 * recommended that {@link java.lang.StringBuffer} be used.
 *
 * <p>Unless otherwise noted, passing a {@code null} argument to a constructor
 * or method in this class will cause a {@link NullPointerException} to be
 * thrown.
 *
 * @author      Michael McCloskey
 * @see         java.lang.StringBuffer
 * @see         java.lang.String
 * @since       1.5
 */
// final 类，线程不安全
public final class StringBuilder
    extends AbstractStringBuilder
    implements java.io.Serializable, CharSequence
{

    /** use serialVersionUID for interoperability */
    static final long serialVersionUID = 4383685877147921099L;

    /**
     * Constructs a string builder with no characters in it and an
     * initial capacity of 16 characters.
     */
    // 创建一个默认容量为 16 的 StringBuilder 对象
    public StringBuilder() {
        super(16);
    }

    /**
     * Constructs a string builder with no characters in it and an
     * initial capacity specified by the {@code capacity} argument.
     *
     * @param      capacity  the initial capacity.
     * @throws     NegativeArraySizeException  if the {@code capacity}
     *               argument is less than {@code 0}.
     */
    // 创建一个指定容量的 StringBuilder 对象
    public StringBuilder(int capacity) {
        super(capacity);
    }

    /**
     * Constructs a string builder initialized to the contents of the
     * specified string. The initial capacity of the string builder is
     * {@code 16} plus the length of the string argument.
     *
     * @param   str   the initial contents of the buffer.
     */
    // 根据 str 创建一个 StringBuilder 对象，容量的大小为 str.length() + 16
    public StringBuilder(String str) {
        // 调用父类初始化容量大小
        super(str.length() + 16);
        // 将 str 追加在字符序列之后
        append(str);
    }

    /**
     * Constructs a string builder that contains the same characters
     * as the specified {@code CharSequence}. The initial capacity of
     * the string builder is {@code 16} plus the length of the
     * {@code CharSequence} argument.
     *
     * @param      seq   the sequence to copy.
     */
    // 根据 CharSequence 对象创建一个 StringBuilder 对象
    public StringBuilder(CharSequence seq) {
        // 初始化 StringBuilder 对象的容量的大小
        this(seq.length() + 16);
        // 在字符序列之后追加 CharSequence 对象
        append(seq);
    }

    // 在字符序列之后追加 Object 对象，将 Object 对象转换成 String 对象进行追加
    @Override
    public StringBuilder append(Object obj) {
        return append(String.valueOf(obj));
    }

    // 在字符序列之后追加 str
    @Override
    public StringBuilder append(String str) {
        // 调用父类方法在字符序列之后追加 str
        super.append(str);
        return this;
    }

    /**
     * Appends the specified {@code StringBuffer} to this sequence.
     * <p>
     * The characters of the {@code StringBuffer} argument are appended,
     * in order, to this sequence, increasing the
     * length of this sequence by the length of the argument.
     * If {@code sb} is {@code null}, then the four characters
     * {@code "null"} are appended to this sequence.
     * <p>
     * Let <i>n</i> be the length of this character sequence just prior to
     * execution of the {@code append} method. Then the character at index
     * <i>k</i> in the new character sequence is equal to the character at
     * index <i>k</i> in the old character sequence, if <i>k</i> is less than
     * <i>n</i>; otherwise, it is equal to the character at index <i>k-n</i>
     * in the argument {@code sb}.
     *
     * @param   sb   the {@code StringBuffer} to append.
     * @return  a reference to this object.
     */
    // 在字符序列之后追加 StringBuffer 对象
    public StringBuilder append(StringBuffer sb) {
        super.append(sb);
        return this;
    }

    // 在字符序列之后追加 CharSequence 对象，CharSequence 对象含有 length() 和 CharAt() 方法可以完成字符序列的追加
    @Override
    public StringBuilder append(CharSequence s) {
        super.append(s);
        return this;
    }

    /**
     * @throws     IndexOutOfBoundsException {@inheritDoc}
     */
    // 在字符序列之后追加 CharSequence 对象的子序列，从 start 到 end 的闭开区间
    @Override
    public StringBuilder append(CharSequence s, int start, int end) {
        super.append(s, start, end);
        return this;
    }

    // 在字符序列之后追加 char[]
    @Override
    public StringBuilder append(char[] str) {
        super.append(str);
        return this;
    }

    /**
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    // 在字符序列之后追加 char[] 的指定位置开始指定长度的子数组
    @Override
    public StringBuilder append(char[] str, int offset, int len) {
        super.append(str, offset, len);
        return this;
    }

    // 在字符序列之后追加 boolean 类型的对象
    @Override
    public StringBuilder append(boolean b) {
        super.append(b);
        return this;
    }

    // 在字符序列之后追加 char 类型的对象
    @Override
    public StringBuilder append(char c) {
        super.append(c);
        return this;
    }

    // 在字符序列之后追加 int 类型的对象
    @Override
    public StringBuilder append(int i) {
        super.append(i);
        return this;
    }

    // 在字符序列之后追加 long 类型的对象
    @Override
    public StringBuilder append(long lng) {
        super.append(lng);
        return this;
    }

    // 在字符序列之后追加 float 类型的对象
    @Override
    public StringBuilder append(float f) {
        super.append(f);
        return this;
    }

    // 在字符序列之后追加 double 类型的对象
    @Override
    public StringBuilder append(double d) {
        super.append(d);
        return this;
    }

    /**
     * @since 1.5
     */
    // 在字符序列之后根据 unicode 码追加对应的字符
    @Override
    public StringBuilder appendCodePoint(int codePoint) {
        super.appendCodePoint(codePoint);
        return this;
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     */
    // 删除字符序列指定位置的子字符序列，取闭开区间
    @Override
    public StringBuilder delete(int start, int end) {
        super.delete(start, end);
        return this;
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     */
    // 删除字符序列指定位置对应的字符
    @Override
    public StringBuilder deleteCharAt(int index) {
        super.deleteCharAt(index);
        return this;
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     */
    // 把字符序列中 start 到 end 位置的子字符序列替换成 String 类型的 str
    @Override
    public StringBuilder replace(int start, int end, String str) {
        super.replace(start, end, str);
        return this;
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     */
    // 在字符序列的指定位置插入 char[] 的子数组(从 char[] 的 offset 位置开始 len 长度的子数组)
    @Override
    public StringBuilder insert(int index, char[] str, int offset,
                                int len)
    {
        super.insert(index, str, offset, len);
        return this;
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     */
    // 在字符序列指定位置插入 Object 对象，将 Object 对象转成 String 对象插入
    @Override
    public StringBuilder insert(int offset, Object obj) {
            super.insert(offset, obj);
            return this;
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     */
    // 在字符序列指定位置插入 String 对象
    @Override
    public StringBuilder insert(int offset, String str) {
        super.insert(offset, str);
        return this;
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     */
    // 在字符序列指定位置插入 char[] 对象
    @Override
    public StringBuilder insert(int offset, char[] str) {
        super.insert(offset, str);
        return this;
    }

    /**
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    // 在字符序列指定位置插入 CharSequence 对象
    @Override
    public StringBuilder insert(int dstOffset, CharSequence s) {
            super.insert(dstOffset, s);
            return this;
    }

    /**
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    // 在字符序列指定位置插入 CharSequence 对象的子字符序列(去闭开区间)
    @Override
    public StringBuilder insert(int dstOffset, CharSequence s,
                                int start, int end)
    {
        super.insert(dstOffset, s, start, end);
        return this;
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     */
    // 在字符序列的指定位置插入 boolean 类型的对象
    @Override
    public StringBuilder insert(int offset, boolean b) {
        super.insert(offset, b);
        return this;
    }

    /**
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    // 在字符序列的指定位置插入 char 类型的对象
    @Override
    public StringBuilder insert(int offset, char c) {
        super.insert(offset, c);
        return this;
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     */
    // 在字符序列的指定位置插入 int 类型的对象
    @Override
    public StringBuilder insert(int offset, int i) {
        super.insert(offset, i);
        return this;
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     */
    // 在字符序列的指定位置插入 long 类型的对象
    @Override
    public StringBuilder insert(int offset, long l) {
        super.insert(offset, l);
        return this;
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     */
    // 在字符序列的指定位置插入 float 类型的对象
    @Override
    public StringBuilder insert(int offset, float f) {
        super.insert(offset, f);
        return this;
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     */
    // 在字符序列的指定位置插入 double 类型的对象
    @Override
    public StringBuilder insert(int offset, double d) {
        super.insert(offset, d);
        return this;
    }

    // 获取 String 类型的 str 在字符序列中第一次出现的位置
    @Override
    public int indexOf(String str) {
        return super.indexOf(str);
    }

    // 获取 String 类型的 str 在字符序列的指定位置之后第一次出现的位置
    @Override
    public int indexOf(String str, int fromIndex) {
        return super.indexOf(str, fromIndex);
    }

    // 获取 String 类型的 str 在字符序列中最后一次出现的位置
    @Override
    public int lastIndexOf(String str) {
        return super.lastIndexOf(str);
    }

    // 获取 String 类型的 str 在字符序列的指定位置之前最后一次出现的位置
    @Override
    public int lastIndexOf(String str, int fromIndex) {
        return super.lastIndexOf(str, fromIndex);
    }

    // 对字符序列进行翻转操作
    @Override
    public StringBuilder reverse() {
        super.reverse();
        return this;
    }

    // 将 StringBuilder 对象转成 String 对象(copy 的方式)
    @Override
    public String toString() {
        // Create a copy, don't share the array
        return new String(value, 0, count);
    }

    /**
     * Save the state of the {@code StringBuilder} instance to a stream
     * (that is, serialize it).
     *
     * @serialData the number of characters currently stored in the string
     *             builder ({@code int}), followed by the characters in the
     *             string builder ({@code char[]}).   The length of the
     *             {@code char} array may be greater than the number of
     *             characters currently stored in the string builder, in which
     *             case extra characters are ignored.
     */
    // 将 StringBuilder 序列化，将 StringBuilder 转成流
    private void writeObject(java.io.ObjectOutputStream s)
        throws java.io.IOException {
        s.defaultWriteObject();
        s.writeInt(count);
        s.writeObject(value);
    }

    /**
     * readObject is called to restore the state of the StringBuffer from
     * a stream.
     */
    // 将 StringBuilder 反序列化，从流转成 StringBuilder 对象
    private void readObject(java.io.ObjectInputStream s)
        throws java.io.IOException, ClassNotFoundException {
        s.defaultReadObject();
        count = s.readInt();
        value = (char[]) s.readObject();
    }

}
