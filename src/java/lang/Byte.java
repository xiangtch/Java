/*
 * Copyright (c) 1996, 2013, Oracle and/or its affiliates. All rights reserved.
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
 *
 * The {@code Byte} class wraps a value of primitive type {@code byte}
 * in an object.  An object of type {@code Byte} contains a single
 * field whose type is {@code byte}.
 *
 * <p>In addition, this class provides several methods for converting
 * a {@code byte} to a {@code String} and a {@code String} to a {@code
 * byte}, as well as other constants and methods useful when dealing
 * with a {@code byte}.
 *
 * @author  Nakul Saraiya
 * @author  Joseph D. Darcy
 * @see     java.lang.Number
 * @since   JDK1.1
 */
// final 类
public final class Byte extends Number implements Comparable<Byte> {

    /**
     * A constant holding the minimum value a {@code byte} can
     * have, -2<sup>7</sup>.
     */
    // byte 的最小值，byte 1 个字节 8 位
    public static final byte   MIN_VALUE = -128;

    /**
     * A constant holding the maximum value a {@code byte} can
     * have, 2<sup>7</sup>-1.
     */
    // byte 的最大值
    public static final byte   MAX_VALUE = 127;

    /**
     * The {@code Class} instance representing the primitive type
     * {@code byte}.
     */
    // 根据基础类型名称获取 Byte 类，实现自动装箱
    @SuppressWarnings("unchecked")
    public static final Class<Byte>     TYPE = (Class<Byte>) Class.getPrimitiveClass("byte");

    /**
     * Returns a new {@code String} object representing the
     * specified {@code byte}. The radix is assumed to be 10.
     *
     * @param b the {@code byte} to be converted
     * @return the string representation of the specified {@code byte}
     * @see java.lang.Integer#toString(int)
     */
    // 将 byte 转成 String, 先将 byte 转成 int 类型，再调用 Integer 的 toString 方法
    public static String toString(byte b) {
        return Integer.toString((int)b, 10);
    }

    // 定义单例模式的 ByteCache 类
    private static class ByteCache {
        private ByteCache(){}

        // 初始化一个长度为 256 的 Byte 数组
        static final Byte cache[] = new Byte[-(-128) + 127 + 1];

        // 给 cache[] 赋值 -128 到 127
        static {
            for(int i = 0; i < cache.length; i++)
                cache[i] = new Byte((byte)(i - 128));
        }
    }

    /**
     * Returns a {@code Byte} instance representing the specified
     * {@code byte} value.
     * If a new {@code Byte} instance is not required, this method
     * should generally be used in preference to the constructor
     * {@link #Byte(byte)}, as this method is likely to yield
     * significantly better space and time performance since
     * all byte values are cached.
     *
     * @param  b a byte value.
     * @return a {@code Byte} instance representing {@code b}.
     * @since  1.5
     */
    // 根据 byte 的值返回一个 Byte 实例，自动装箱时编译器默认调用这个方法
    public static Byte valueOf(byte b) {
        // 从 cache[] 中获取 byte 对应的 Byte 实例
        final int offset = 128;
        return ByteCache.cache[(int)b + offset];
    }

    /**
     * Parses the string argument as a signed {@code byte} in the
     * radix specified by the second argument. The characters in the
     * string must all be digits, of the specified radix (as
     * determined by whether {@link java.lang.Character#digit(char,
     * int)} returns a nonnegative value) except that the first
     * character may be an ASCII minus sign {@code '-'}
     * ({@code '\u005Cu002D'}) to indicate a negative value or an
     * ASCII plus sign {@code '+'} ({@code '\u005Cu002B'}) to
     * indicate a positive value.  The resulting {@code byte} value is
     * returned.
     *
     * <p>An exception of type {@code NumberFormatException} is
     * thrown if any of the following situations occurs:
     * <ul>
     * <li> The first argument is {@code null} or is a string of
     * length zero.
     *
     * <li> The radix is either smaller than {@link
     * java.lang.Character#MIN_RADIX} or larger than {@link
     * java.lang.Character#MAX_RADIX}.
     *
     * <li> Any character of the string is not a digit of the
     * specified radix, except that the first character may be a minus
     * sign {@code '-'} ({@code '\u005Cu002D'}) or plus sign
     * {@code '+'} ({@code '\u005Cu002B'}) provided that the
     * string is longer than length 1.
     *
     * <li> The value represented by the string is not a value of type
     * {@code byte}.
     * </ul>
     *
     * @param s         the {@code String} containing the
     *                  {@code byte}
     *                  representation to be parsed
     * @param radix     the radix to be used while parsing {@code s}
     * @return          the {@code byte} value represented by the string
     *                   argument in the specified radix
     * @throws          NumberFormatException If the string does
     *                  not contain a parsable {@code byte}.
     */
    // 将指定 radix 进制的 String 转换成 byte
    public static byte parseByte(String s, int radix)
        throws NumberFormatException {
        // 将指定 radix 进制的 String 转换成 int
        int i = Integer.parseInt(s, radix);
        if (i < MIN_VALUE || i > MAX_VALUE)
            throw new NumberFormatException(
                "Value out of range. Value:\"" + s + "\" Radix:" + radix);
        // 将 int 转成 byte
        return (byte)i;
    }

    /**
     * Parses the string argument as a signed decimal {@code
     * byte}. The characters in the string must all be decimal digits,
     * except that the first character may be an ASCII minus sign
     * {@code '-'} ({@code '\u005Cu002D'}) to indicate a negative
     * value or an ASCII plus sign {@code '+'}
     * ({@code '\u005Cu002B'}) to indicate a positive value. The
     * resulting {@code byte} value is returned, exactly as if the
     * argument and the radix 10 were given as arguments to the {@link
     * #parseByte(java.lang.String, int)} method.
     *
     * @param s         a {@code String} containing the
     *                  {@code byte} representation to be parsed
     * @return          the {@code byte} value represented by the
     *                  argument in decimal
     * @throws          NumberFormatException if the string does not
     *                  contain a parsable {@code byte}.
     */
    // 将 String 解析成 byte, 默认 String 的格式是 10 进制的数字字符串
    public static byte parseByte(String s) throws NumberFormatException {
        return parseByte(s, 10);
    }

    /**
     * Returns a {@code Byte} object holding the value
     * extracted from the specified {@code String} when parsed
     * with the radix given by the second argument. The first argument
     * is interpreted as representing a signed {@code byte} in
     * the radix specified by the second argument, exactly as if the
     * argument were given to the {@link #parseByte(java.lang.String,
     * int)} method. The result is a {@code Byte} object that
     * represents the {@code byte} value specified by the string.
     *
     * <p> In other words, this method returns a {@code Byte} object
     * equal to the value of:
     *
     * <blockquote>
     * {@code new Byte(Byte.parseByte(s, radix))}
     * </blockquote>
     *
     * @param s         the string to be parsed
     * @param radix     the radix to be used in interpreting {@code s}
     * @return          a {@code Byte} object holding the value
     *                  represented by the string argument in the
     *                  specified radix.
     * @throws          NumberFormatException If the {@code String} does
     *                  not contain a parsable {@code byte}.
     */
    // 获取指定 radix 进制的 String 类型对应的 Byte 的值
    public static Byte valueOf(String s, int radix)
        throws NumberFormatException {
        // 先将指定 radix 进制的 String 转成对应的 byte, 然后根据 byte 获取对应的 Byte 实例
        return valueOf(parseByte(s, radix));
    }

    /**
     * Returns a {@code Byte} object holding the value
     * given by the specified {@code String}. The argument is
     * interpreted as representing a signed decimal {@code byte},
     * exactly as if the argument were given to the {@link
     * #parseByte(java.lang.String)} method. The result is a
     * {@code Byte} object that represents the {@code byte}
     * value specified by the string.
     *
     * <p> In other words, this method returns a {@code Byte} object
     * equal to the value of:
     *
     * <blockquote>
     * {@code new Byte(Byte.parseByte(s))}
     * </blockquote>
     *
     * @param s         the string to be parsed
     * @return          a {@code Byte} object holding the value
     *                  represented by the string argument
     * @throws          NumberFormatException If the {@code String} does
     *                  not contain a parsable {@code byte}.
     */
    // 将 String 类型的数据转换成 Byte 类型，默认 String 对应的是 10 进制的数字字符串
    public static Byte valueOf(String s) throws NumberFormatException {
        return valueOf(s, 10);
    }

    /**
     * Decodes a {@code String} into a {@code Byte}.
     * Accepts decimal, hexadecimal, and octal numbers given by
     * the following grammar:
     *
     * <blockquote>
     * <dl>
     * <dt><i>DecodableString:</i>
     * <dd><i>Sign<sub>opt</sub> DecimalNumeral</i>
     * <dd><i>Sign<sub>opt</sub></i> {@code 0x} <i>HexDigits</i>
     * <dd><i>Sign<sub>opt</sub></i> {@code 0X} <i>HexDigits</i>
     * <dd><i>Sign<sub>opt</sub></i> {@code #} <i>HexDigits</i>
     * <dd><i>Sign<sub>opt</sub></i> {@code 0} <i>OctalDigits</i>
     *
     * <dt><i>Sign:</i>
     * <dd>{@code -}
     * <dd>{@code +}
     * </dl>
     * </blockquote>
     *
     * <i>DecimalNumeral</i>, <i>HexDigits</i>, and <i>OctalDigits</i>
     * are as defined in section 3.10.1 of
     * <cite>The Java&trade; Language Specification</cite>,
     * except that underscores are not accepted between digits.
     *
     * <p>The sequence of characters following an optional
     * sign and/or radix specifier ("{@code 0x}", "{@code 0X}",
     * "{@code #}", or leading zero) is parsed as by the {@code
     * Byte.parseByte} method with the indicated radix (10, 16, or 8).
     * This sequence of characters must represent a positive value or
     * a {@link NumberFormatException} will be thrown.  The result is
     * negated if first character of the specified {@code String} is
     * the minus sign.  No whitespace characters are permitted in the
     * {@code String}.
     *
     * @param     nm the {@code String} to decode.
     * @return   a {@code Byte} object holding the {@code byte}
     *          value represented by {@code nm}
     * @throws  NumberFormatException  if the {@code String} does not
     *            contain a parsable {@code byte}.
     * @see java.lang.Byte#parseByte(java.lang.String, int)
     */
    // 将一个 String 对象解码成一个 Byte 对象
    public static Byte decode(String nm) throws NumberFormatException {
        // 调用 Integer 的 decode 方法解码
        int i = Integer.decode(nm);
        if (i < MIN_VALUE || i > MAX_VALUE)
            throw new NumberFormatException(
                    "Value " + i + " out of range from input " + nm);
        // 将 int 类型转成 byte 类型
        return valueOf((byte)i);
    }

    /**
     * The value of the {@code Byte}.
     *
     * @serial
     */
    private final byte value;

    /**
     * Constructs a newly allocated {@code Byte} object that
     * represents the specified {@code byte} value.
     *
     * @param value     the value to be represented by the
     *                  {@code Byte}.
     */
    // 根据 byte 的值创建 Byte 对象
    public Byte(byte value) {
        this.value = value;
    }

    /**
     * Constructs a newly allocated {@code Byte} object that
     * represents the {@code byte} value indicated by the
     * {@code String} parameter. The string is converted to a
     * {@code byte} value in exactly the manner used by the
     * {@code parseByte} method for radix 10.
     *
     * @param s         the {@code String} to be converted to a
     *                  {@code Byte}
     * @throws           NumberFormatException If the {@code String}
     *                  does not contain a parsable {@code byte}.
     * @see        java.lang.Byte#parseByte(java.lang.String, int)
     */
    // 将 String 转成对应的 Byte 对象，默认 String 是 10 进制的数字字符串
    public Byte(String s) throws NumberFormatException {
        this.value = parseByte(s, 10);
    }

    /**
     * Returns the value of this {@code Byte} as a
     * {@code byte}.
     */
    // 返回 Byte 对象的 byte 值，自动拆箱时编译器自动调用该方法
    public byte byteValue() {
        return value;
    }

    /**
     * Returns the value of this {@code Byte} as a {@code short} after
     * a widening primitive conversion.
     * @jls 5.1.2 Widening Primitive Conversions
     */
    // 返回 Byte 对象对应的 short 值，将 byte 值强转 short 值
    public short shortValue() {
        return (short)value;
    }

    /**
     * Returns the value of this {@code Byte} as an {@code int} after
     * a widening primitive conversion.
     * @jls 5.1.2 Widening Primitive Conversions
     */
    // 返回 Byte 对象对应的 int 值，将 byte 值强转 int 值
    public int intValue() {
        return (int)value;
    }

    /**
     * Returns the value of this {@code Byte} as a {@code long} after
     * a widening primitive conversion.
     * @jls 5.1.2 Widening Primitive Conversions
     */
    // 返回 Byte 对象对应的 long 值，将 byte 值强转 long 值
    public long longValue() {
        return (long)value;
    }

    /**
     * Returns the value of this {@code Byte} as a {@code float} after
     * a widening primitive conversion.
     * @jls 5.1.2 Widening Primitive Conversions
     */
    // 返回 Byte 对象对应的 float 值，将 byte 值强转 float 值
    public float floatValue() {
        return (float)value;
    }

    /**
     * Returns the value of this {@code Byte} as a {@code double}
     * after a widening primitive conversion.
     * @jls 5.1.2 Widening Primitive Conversions
     */
    // 返回 Byte 对象对应的 double 值，将 byte 值强转 double 值
    public double doubleValue() {
        return (double)value;
    }

    /**
     * Returns a {@code String} object representing this
     * {@code Byte}'s value.  The value is converted to signed
     * decimal representation and returned as a string, exactly as if
     * the {@code byte} value were given as an argument to the
     * {@link java.lang.Byte#toString(byte)} method.
     *
     * @return  a string representation of the value of this object in
     *          base&nbsp;10.
     */
    // 重写 Object 的 toString 方法，调用 Integer 的 toString 方法
    public String toString() {
        return Integer.toString((int)value);
    }

    /**
     * Returns a hash code for this {@code Byte}; equal to the result
     * of invoking {@code intValue()}.
     *
     * @return a hash code value for this {@code Byte}
     */
    // 重写 Object 的 hashCode 方法
    @Override
    public int hashCode() {
        return Byte.hashCode(value);
    }

    /**
     * Returns a hash code for a {@code byte} value; compatible with
     * {@code Byte.hashCode()}.
     *
     * @param value the value to hash
     * @return a hash code value for a {@code byte} value.
     * @since 1.8
     */
    // 获取 byte 值对应的 hashCode, 直接返回 byte 转成 int 的值
    public static int hashCode(byte value) {
        return (int)value;
    }

    /**
     * Compares this object to the specified object.  The result is
     * {@code true} if and only if the argument is not
     * {@code null} and is a {@code Byte} object that
     * contains the same {@code byte} value as this object.
     *
     * @param obj       the object to compare with
     * @return          {@code true} if the objects are the same;
     *                  {@code false} otherwise.
     */
    // 重写 Object 的 equals 方法
    public boolean equals(Object obj) {
        // 先判断类型是否一致，再判断 Byte 拆箱之后对应的 byte 的值是否相等
        if (obj instanceof Byte) {
            return value == ((Byte)obj).byteValue();
        }
        return false;
    }

    /**
     * Compares two {@code Byte} objects numerically.
     *
     * @param   anotherByte   the {@code Byte} to be compared.
     * @return  the value {@code 0} if this {@code Byte} is
     *          equal to the argument {@code Byte}; a value less than
     *          {@code 0} if this {@code Byte} is numerically less
     *          than the argument {@code Byte}; and a value greater than
     *           {@code 0} if this {@code Byte} is numerically
     *           greater than the argument {@code Byte} (signed
     *           comparison).
     * @since   1.2
     */
    // 实现 Comparable 接口的 compareTo 方法, 比较 Byte 对象的 value 值的大小
    public int compareTo(Byte anotherByte) {
        return compare(this.value, anotherByte.value);
    }

    /**
     * Compares two {@code byte} values numerically.
     * The value returned is identical to what would be returned by:
     * <pre>
     *    Byte.valueOf(x).compareTo(Byte.valueOf(y))
     * </pre>
     *
     * @param  x the first {@code byte} to compare
     * @param  y the second {@code byte} to compare
     * @return the value {@code 0} if {@code x == y};
     *         a value less than {@code 0} if {@code x < y}; and
     *         a value greater than {@code 0} if {@code x > y}
     * @since 1.7
     */
    // 比较两个 byte 值的大小 x - y,即前者减后者
    public static int compare(byte x, byte y) {
        return x - y;
    }

    /**
     * Converts the argument to an {@code int} by an unsigned
     * conversion.  In an unsigned conversion to an {@code int}, the
     * high-order 24 bits of the {@code int} are zero and the
     * low-order 8 bits are equal to the bits of the {@code byte} argument.
     *
     * Consequently, zero and positive {@code byte} values are mapped
     * to a numerically equal {@code int} value and negative {@code
     * byte} values are mapped to an {@code int} value equal to the
     * input plus 2<sup>8</sup>.
     *
     * @param  x the value to convert to an unsigned {@code int}
     * @return the argument converted to {@code int} by an unsigned
     *         conversion
     * @since 1.8
     */
    // 将 byte 类型的值转成无符号的 int 类型的值，直接将 byte 放在 int 的后 8 位，高位补 0
    public static int toUnsignedInt(byte x) {
        return ((int) x) & 0xff;
    }

    /**
     * Converts the argument to a {@code long} by an unsigned
     * conversion.  In an unsigned conversion to a {@code long}, the
     * high-order 56 bits of the {@code long} are zero and the
     * low-order 8 bits are equal to the bits of the {@code byte} argument.
     *
     * Consequently, zero and positive {@code byte} values are mapped
     * to a numerically equal {@code long} value and negative {@code
     * byte} values are mapped to a {@code long} value equal to the
     * input plus 2<sup>8</sup>.
     *
     * @param  x the value to convert to an unsigned {@code long}
     * @return the argument converted to {@code long} by an unsigned
     *         conversion
     * @since 1.8
     */
    // 将 byte 类型的值转成无符号 long 类型的值，直接将 byte 放在 long 的后 8 位，高位补 0
    public static long toUnsignedLong(byte x) {
        return ((long) x) & 0xffL;
    }


    /**
     * The number of bits used to represent a {@code byte} value in two's
     * complement binary form.
     *
     * @since 1.5
     */
    // byte 的位数大小
    public static final int SIZE = 8;

    /**
     * The number of bytes used to represent a {@code byte} value in two's
     * complement binary form.
     *
     * @since 1.8
     */
    public static final int BYTES = SIZE / Byte.SIZE;

    /** use serialVersionUID from JDK 1.1. for interoperability */
    private static final long serialVersionUID = -7183698231559129828L;
}
