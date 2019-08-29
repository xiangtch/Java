/*
 * Copyright (c) 1994, 2013, Oracle and/or its affiliates. All rights reserved.
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
 * The Boolean class wraps a value of the primitive type
 * {@code boolean} in an object. An object of type
 * {@code Boolean} contains a single field whose type is
 * {@code boolean}.
 * <p>
 * In addition, this class provides many methods for
 * converting a {@code boolean} to a {@code String} and a
 * {@code String} to a {@code boolean}, as well as other
 * constants and methods useful when dealing with a
 * {@code boolean}.
 *
 * @author  Arthur van Hoff
 * @since   JDK1.0
 */
// final 类，不可被继承与修改
public final class Boolean implements java.io.Serializable,
                                      Comparable<Boolean>
{
    /**
     * The {@code Boolean} object corresponding to the primitive
     * value {@code true}.
     */
    // 对应 boolean 值为 ture 时的值
    public static final Boolean TRUE = new Boolean(true);

    /**
     * The {@code Boolean} object corresponding to the primitive
     * value {@code false}.
     */
    // 对应 boolean 值为 false 时的值
    public static final Boolean FALSE = new Boolean(false);

    /**
     * The Class object representing the primitive type boolean.
     *
     * @since   JDK1.1
     */
    // 根据基础类型名称获取封装类 Boolean
    @SuppressWarnings("unchecked")
    public static final Class<Boolean> TYPE = (Class<Boolean>) Class.getPrimitiveClass("boolean");

    /**
     * The value of the Boolean.
     *
     * @serial
     */
    // Boolean 对象的值
    private final boolean value;

    /** use serialVersionUID from JDK 1.0.2 for interoperability */
    // 序列化 ID
    private static final long serialVersionUID = -3665804199014368530L;

    /**
     * Allocates a {@code Boolean} object representing the
     * {@code value} argument.
     *
     * <p><b>Note: It is rarely appropriate to use this constructor.
     * Unless a <i>new</i> instance is required, the static factory
     * {@link #valueOf(boolean)} is generally a better choice. It is
     * likely to yield significantly better space and time performance.</b>
     *
     * @param   value   the value of the {@code Boolean}.
     */
    // 根据 boolean 类型创建 Boolean 类(boolean 的自动装箱)
    public Boolean(boolean value) {
        this.value = value;
    }

    /**
     * Allocates a {@code Boolean} object representing the value
     * {@code true} if the string argument is not {@code null}
     * and is equal, ignoring case, to the string {@code "true"}.
     * Otherwise, allocate a {@code Boolean} object representing the
     * value {@code false}. Examples:<p>
     * {@code new Boolean("True")} produces a {@code Boolean} object
     * that represents {@code true}.<br>
     * {@code new Boolean("yes")} produces a {@code Boolean} object
     * that represents {@code false}.
     *
     * @param   s   the string to be converted to a {@code Boolean}.
     */
    // 根据 String 类型的 s 创建 Boolean 对象
    public Boolean(String s) {
        this(parseBoolean(s));
    }

    /**
     * Parses the string argument as a boolean.  The {@code boolean}
     * returned represents the value {@code true} if the string argument
     * is not {@code null} and is equal, ignoring case, to the string
     * {@code "true"}. <p>
     * Example: {@code Boolean.parseBoolean("True")} returns {@code true}.<br>
     * Example: {@code Boolean.parseBoolean("yes")} returns {@code false}.
     *
     * @param      s   the {@code String} containing the boolean
     *                 representation to be parsed
     * @return     the boolean represented by the string argument
     * @since 1.5
     */
    // 将 String 对象解析成为 boolean 值，忽略大小写
    public static boolean parseBoolean(String s) {
        return ((s != null) && s.equalsIgnoreCase("true"));
    }

    /**
     * Returns the value of this {@code Boolean} object as a boolean
     * primitive.
     *
     * @return  the primitive {@code boolean} value of this object.
     */
    // 获取 Boolean 对象对应基础类型 boolean 的 value，自动拆箱时编译器调用该方法
    public boolean booleanValue() {
        return value;
    }

    /**
     * Returns a {@code Boolean} instance representing the specified
     * {@code boolean} value.  If the specified {@code boolean} value
     * is {@code true}, this method returns {@code Boolean.TRUE};
     * if it is {@code false}, this method returns {@code Boolean.FALSE}.
     * If a new {@code Boolean} instance is not required, this method
     * should generally be used in preference to the constructor
     * {@link #Boolean(boolean)}, as this method is likely to yield
     * significantly better space and time performance.
     *
     * @param  b a boolean value.
     * @return a {@code Boolean} instance representing {@code b}.
     * @since  1.4
     */
    // 根据 boolean 的值返回对应的 Boolean 对象，自动装箱时编译器默认调用这个方法
    public static Boolean valueOf(boolean b) {
        return (b ? TRUE : FALSE);
    }

    /**
     * Returns a {@code Boolean} with a value represented by the
     * specified string.  The {@code Boolean} returned represents a
     * true value if the string argument is not {@code null}
     * and is equal, ignoring case, to the string {@code "true"}.
     *
     * @param   s   a string.
     * @return  the {@code Boolean} value represented by the string.
     */
    // 根据 String 对象的值返回对应的 Boolean 对象，只有 s 为 "true"(忽略大小写) 时为 TRUE
    public static Boolean valueOf(String s) {
        return parseBoolean(s) ? TRUE : FALSE;
    }

    /**
     * Returns a {@code String} object representing the specified
     * boolean.  If the specified boolean is {@code true}, then
     * the string {@code "true"} will be returned, otherwise the
     * string {@code "false"} will be returned.
     *
     * @param b the boolean to be converted
     * @return the string representation of the specified {@code boolean}
     * @since 1.4
     */
    // 根据 boolean 的值返回转成 String 对应的值
    public static String toString(boolean b) {
        return b ? "true" : "false";
    }

    /**
     * Returns a {@code String} object representing this Boolean's
     * value.  If this object represents the value {@code true},
     * a string equal to {@code "true"} is returned. Otherwise, a
     * string equal to {@code "false"} is returned.
     *
     * @return  a string representation of this object.
     */
    // 重写 Object 的 toString 方法，返回 Boolean 对象的 toString 之后的值，即 value 对应的字符串
    public String toString() {
        return value ? "true" : "false";
    }

    /**
     * Returns a hash code for this {@code Boolean} object.
     *
     * @return  the integer {@code 1231} if this object represents
     * {@code true}; returns the integer {@code 1237} if this
     * object represents {@code false}.
     */
    // 重写 Object 的 hashCode 方法
    @Override
    public int hashCode() {
        return Boolean.hashCode(value);
    }

    /**
     * Returns a hash code for a {@code boolean} value; compatible with
     * {@code Boolean.hashCode()}.
     *
     * @param value the value to hash
     * @return a hash code value for a {@code boolean} value.
     * @since 1.8
     */
    // 根据 boolean 的值返回对应的 hashCode, true - 1231, false - 1237
    public static int hashCode(boolean value) {
        return value ? 1231 : 1237;
    }

   /**
     * Returns {@code true} if and only if the argument is not
     * {@code null} and is a {@code Boolean} object that
     * represents the same {@code boolean} value as this object.
     *
     * @param   obj   the object to compare with.
     * @return  {@code true} if the Boolean objects represent the
     *          same value; {@code false} otherwise.
     */
   // 重写 Object 的 equals 方法
    public boolean equals(Object obj) {
        // 先判断类型
        if (obj instanceof Boolean) {
            // 然后获取 Boolean 类型对应的 boolean 值是否相等
            return value == ((Boolean)obj).booleanValue();
        }
        return false;
    }

    /**
     * Returns {@code true} if and only if the system property
     * named by the argument exists and is equal to the string
     * {@code "true"}. (Beginning with version 1.0.2 of the
     * Java<small><sup>TM</sup></small> platform, the test of
     * this string is case insensitive.) A system property is accessible
     * through {@code getProperty}, a method defined by the
     * {@code System} class.
     * <p>
     * If there is no property with the specified name, or if the specified
     * name is empty or null, then {@code false} is returned.
     *
     * @param   name   the system property name.
     * @return  the {@code boolean} value of the system property.
     * @throws  SecurityException for the same reasons as
     *          {@link System#getProperty(String) System.getProperty}
     * @see     java.lang.System#getProperty(java.lang.String)
     * @see     java.lang.System#getProperty(java.lang.String, java.lang.String)
     */
    // 根据系统属性的值返回对应的 boolean 值
    public static boolean getBoolean(String name) {
        boolean result = false;
        try {
            // parseBoolean 方法忽略大小写("true")
            result = parseBoolean(System.getProperty(name));
        } catch (IllegalArgumentException | NullPointerException e) {
        }
        return result;
    }

    /**
     * Compares this {@code Boolean} instance with another.
     *
     * @param   b the {@code Boolean} instance to be compared
     * @return  zero if this object represents the same boolean value as the
     *          argument; a positive value if this object represents true
     *          and the argument represents false; and a negative value if
     *          this object represents false and the argument represents true
     * @throws  NullPointerException if the argument is {@code null}
     * @see     Comparable
     * @since  1.5
     */
    // 比较两个 Boolean 对象, 不相等的话大小取决于 this.value 的值
    public int compareTo(Boolean b) {
        return compare(this.value, b.value);
    }

    /**
     * Compares two {@code boolean} values.
     * The value returned is identical to what would be returned by:
     * <pre>
     *    Boolean.valueOf(x).compareTo(Boolean.valueOf(y))
     * </pre>
     *
     * @param  x the first {@code boolean} to compare
     * @param  y the second {@code boolean} to compare
     * @return the value {@code 0} if {@code x == y};
     *         a value less than {@code 0} if {@code !x && y}; and
     *         a value greater than {@code 0} if {@code x && !y}
     * @since 1.7
     */
    // 将两个 boolean 值进行比较，相等返回 0，否则根据第一个 boolean 值决定返回 1 还是 -1
    public static int compare(boolean x, boolean y) {
        return (x == y) ? 0 : (x ? 1 : -1);
    }

    /**
     * Returns the result of applying the logical AND operator to the
     * specified {@code boolean} operands.
     *
     * @param a the first operand
     * @param b the second operand
     * @return the logical AND of {@code a} and {@code b}
     * @see java.util.function.BinaryOperator
     * @since 1.8
     */
    // 返回两个 boolean 值的逻辑与, 只有 a 与 b 全部为 true 时才返回 true
    public static boolean logicalAnd(boolean a, boolean b) {
        return a && b;
    }

    /**
     * Returns the result of applying the logical OR operator to the
     * specified {@code boolean} operands.
     *
     * @param a the first operand
     * @param b the second operand
     * @return the logical OR of {@code a} and {@code b}
     * @see java.util.function.BinaryOperator
     * @since 1.8
     */
    // 返回两个 boolean 值的逻辑或, a 与 b 中只要有一个为 true 时，就返回 true, 而且会短路判断
    // 即 a 为 true 时，不会再对 b 进行判断，直接返回 true
    public static boolean logicalOr(boolean a, boolean b) {
        return a || b;
    }

    /**
     * Returns the result of applying the logical XOR operator to the
     * specified {@code boolean} operands.
     *
     * @param a the first operand
     * @param b the second operand
     * @return  the logical XOR of {@code a} and {@code b}
     * @see java.util.function.BinaryOperator
     * @since 1.8
     */
    // 返回两个 boolean 值的异或，只有 a 与 b 不一样时才返回 true
    public static boolean logicalXor(boolean a, boolean b) {
        return a ^ b;
    }
}
