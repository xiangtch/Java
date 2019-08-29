package sun.misc;

public class DoubleConsts
{
  public static final double POSITIVE_INFINITY = Double.POSITIVE_INFINITY;
  public static final double NEGATIVE_INFINITY = Double.NEGATIVE_INFINITY;
  public static final double NaN = NaN.0D;
  public static final double MAX_VALUE = Double.MAX_VALUE;
  public static final double MIN_VALUE = Double.MIN_VALUE;
  // double 类型的最小规约数
  public static final double MIN_NORMAL = 2.2250738585072014E-308D;
  // double 类型有效数位长度
  public static final int SIGNIFICAND_WIDTH = 53;
  public static final int MAX_EXPONENT = 1023;
  // 最小 E 的值
  public static final int MIN_EXPONENT = -1022;
  public static final int MIN_SUB_EXPONENT = -1074;
  // 中间值
  public static final int EXP_BIAS = 1023;
  // 获取 double 类型 S 的掩码
  public static final long SIGN_BIT_MASK = Long.MIN_VALUE; // 0x8000000000000000L
  // 获取 double 类型 E 的掩码
  public static final long EXP_BIT_MASK = 9218868437227405312L; // 7ff0000000000000
  // 获取 double 类型 M 的掩码
  public static final long SIGNIF_BIT_MASK = 4503599627370495L; // fffffffffffff
}


/* Location:              E:\java_source\rt.jar!\sun\misc\DoubleConsts.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */