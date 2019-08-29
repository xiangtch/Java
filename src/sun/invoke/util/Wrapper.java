/*     */ package sun.invoke.util;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.lang.reflect.Array;
/*     */ import java.util.Arrays;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public enum Wrapper
/*     */ {
/*     */   private final Class<?> wrapperType;
/*     */   private final Class<?> primitiveType;
/*     */   private final char basicTypeChar;
/*     */   private final Object zero;
/*     */   private final Object emptyArray;
/*     */   private final int format;
/*     */   private final String wrapperSimpleName;
/*     */   private final String primitiveSimpleName;
/*     */   private static final Wrapper[] FROM_PRIM;
/*     */   private static final Wrapper[] FROM_WRAP;
/*     */   private static final Wrapper[] FROM_CHAR;
/*     */   
/*     */   private Wrapper(Class<?> paramClass1, Class<?> paramClass2, char paramChar, Object paramObject1, Object paramObject2, int paramInt)
/*     */   {
/*  54 */     this.wrapperType = paramClass1;
/*  55 */     this.primitiveType = paramClass2;
/*  56 */     this.basicTypeChar = paramChar;
/*  57 */     this.zero = paramObject1;
/*  58 */     this.emptyArray = paramObject2;
/*  59 */     this.format = paramInt;
/*  60 */     this.wrapperSimpleName = paramClass1.getSimpleName();
/*  61 */     this.primitiveSimpleName = paramClass2.getSimpleName();
/*     */   }
/*     */   
/*     */   public String detailString()
/*     */   {
/*  66 */     return 
/*  67 */       this.wrapperSimpleName + Arrays.asList(new Object[] { this.wrapperType, this.primitiveType, 
/*  68 */       Character.valueOf(this.basicTypeChar), this.zero, "0x" + 
/*  69 */       Integer.toHexString(this.format) });
/*     */   }
/*     */   
/*     */   private static abstract class Format {
/*     */     static final int SLOT_SHIFT = 0;
/*     */     static final int SIZE_SHIFT = 2;
/*     */     static final int KIND_SHIFT = 12;
/*     */     static final int SIGNED = -4096;
/*     */     static final int UNSIGNED = 0;
/*     */     static final int FLOATING = 4096;
/*     */     static final int SLOT_MASK = 3;
/*     */     
/*     */     static int format(int paramInt1, int paramInt2, int paramInt3) {
/*  82 */       assert (paramInt1 >> 12 << 12 == paramInt1);
/*  83 */       assert ((paramInt2 & paramInt2 - 1) == 0);
/*  84 */       assert (paramInt1 == 61440 ? paramInt2 > 0 : paramInt1 == 0 ? paramInt2 > 0 : (paramInt1 == 4096) && ((paramInt2 == 32) || (paramInt2 == 64)));
/*     */       
/*     */ 
/*     */ 
/*  88 */       assert (paramInt3 == 2 ? paramInt2 != 64 : (paramInt3 == 1) && (paramInt2 <= 32));
/*     */       
/*     */ 
/*  91 */       return paramInt1 | paramInt2 << 2 | paramInt3 << 0; }
/*     */     
/*     */     static final int SIZE_MASK = 1023;
/*     */     static final int INT = -3967;
/*     */     static final int SHORT = -4031;
/*     */     static final int BOOLEAN = 5;
/*     */     static final int CHAR = 65;
/*     */     static final int FLOAT = 4225;
/*     */     static final int VOID = 0;
/*     */     static final int NUM_MASK = -4;
/* 101 */     static int signed(int paramInt) { return format(61440, paramInt, paramInt > 32 ? 2 : 1); }
/* 102 */     static int unsigned(int paramInt) { return format(0, paramInt, paramInt > 32 ? 2 : 1); }
/* 103 */     static int floating(int paramInt) { return format(4096, paramInt, paramInt > 32 ? 2 : 1); }
/* 104 */     static int other(int paramInt) { return paramInt << 0; }
/*     */   }
/*     */   
/*     */ 
/*     */   public int bitWidth()
/*     */   {
/* 110 */     return this.format >> 2 & 0x3FF; }
/*     */   
/* 112 */   public int stackSlots() { return this.format >> 0 & 0x3; }
/*     */   
/* 114 */   public boolean isSingleWord() { return (this.format & 0x1) != 0; }
/*     */   
/* 116 */   public boolean isDoubleWord() { return (this.format & 0x2) != 0; }
/*     */   
/* 118 */   public boolean isNumeric() { return (this.format & 0xFFFFFFFC) != 0; }
/*     */   
/* 120 */   public boolean isIntegral() { return (isNumeric()) && (this.format < 4225); }
/*     */   
/* 122 */   public boolean isSubwordOrInt() { return (isIntegral()) && (isSingleWord()); }
/*     */   
/* 124 */   public boolean isSigned() { return this.format < 0; }
/*     */   
/* 126 */   public boolean isUnsigned() { return (this.format >= 5) && (this.format < 4225); }
/*     */   
/* 128 */   public boolean isFloating() { return this.format >= 4225; }
/*     */   
/* 130 */   public boolean isOther() { return (this.format & 0xFFFFFFFC) == 0; }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isConvertibleFrom(Wrapper paramWrapper)
/*     */   {
/* 143 */     if (this == paramWrapper) return true;
/* 144 */     if (compareTo(paramWrapper) < 0)
/*     */     {
/* 146 */       return false;
/*     */     }
/*     */     
/*     */ 
/* 150 */     int i = (this.format & paramWrapper.format & 0xF000) != 0 ? 1 : 0;
/* 151 */     if (i == 0) {
/* 152 */       if (isOther()) { return true;
/*     */       }
/* 154 */       if (paramWrapper.format == 65) { return true;
/*     */       }
/* 156 */       return false;
/*     */     }
/*     */     
/* 159 */     assert ((isFloating()) || (isSigned()));
/* 160 */     assert ((paramWrapper.isFloating()) || (paramWrapper.isSigned()));
/* 161 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   private static boolean checkConvertibleFrom()
/*     */   {
/* 167 */     for (Wrapper localWrapper1 : ) {
/* 168 */       assert (localWrapper1.isConvertibleFrom(localWrapper1));
/* 169 */       assert (VOID.isConvertibleFrom(localWrapper1));
/* 170 */       if (localWrapper1 != VOID) {
/* 171 */         assert (OBJECT.isConvertibleFrom(localWrapper1));
/* 172 */         assert (!localWrapper1.isConvertibleFrom(VOID));
/*     */       }
/*     */       
/* 175 */       if (localWrapper1 != CHAR) {
/* 176 */         assert (!CHAR.isConvertibleFrom(localWrapper1));
/* 177 */         if ((!localWrapper1.isConvertibleFrom(INT)) && 
/* 178 */           (!$assertionsDisabled) && (localWrapper1.isConvertibleFrom(CHAR))) throw new AssertionError();
/*     */       }
/* 180 */       if (localWrapper1 != BOOLEAN) {
/* 181 */         assert (!BOOLEAN.isConvertibleFrom(localWrapper1));
/* 182 */         if ((localWrapper1 != VOID) && (localWrapper1 != OBJECT) && 
/* 183 */           (!$assertionsDisabled) && (localWrapper1.isConvertibleFrom(BOOLEAN))) throw new AssertionError();
/*     */       }
/*     */       Wrapper localWrapper2;
/* 186 */       if (localWrapper1.isSigned()) {
/* 187 */         for (localWrapper2 : values()) {
/* 188 */           if (localWrapper1 != localWrapper2) {
/* 189 */             if (localWrapper2.isFloating()) {
/* 190 */               if ((!$assertionsDisabled) && (localWrapper1.isConvertibleFrom(localWrapper2))) throw new AssertionError();
/* 191 */             } else if (localWrapper2.isSigned()) {
/* 192 */               if (localWrapper1.compareTo(localWrapper2) < 0) {
/* 193 */                 if ((!$assertionsDisabled) && (localWrapper1.isConvertibleFrom(localWrapper2))) throw new AssertionError();
/*     */               } else
/* 195 */                 assert (localWrapper1.isConvertibleFrom(localWrapper2));
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/* 200 */       if (localWrapper1.isFloating()) {
/* 201 */         for (localWrapper2 : values()) {
/* 202 */           if (localWrapper1 != localWrapper2) {
/* 203 */             if (localWrapper2.isSigned()) {
/* 204 */               if ((!$assertionsDisabled) && (!localWrapper1.isConvertibleFrom(localWrapper2))) throw new AssertionError();
/* 205 */             } else if (localWrapper2.isFloating())
/* 206 */               if (localWrapper1.compareTo(localWrapper2) < 0) {
/* 207 */                 if ((!$assertionsDisabled) && (localWrapper1.isConvertibleFrom(localWrapper2))) throw new AssertionError();
/*     */               } else
/* 209 */                 assert (localWrapper1.isConvertibleFrom(localWrapper2));
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 214 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Object zero()
/*     */   {
/* 225 */     return this.zero;
/*     */   }
/*     */   
/*     */ 
/*     */   public <T> T zero(Class<T> paramClass)
/*     */   {
/* 231 */     return (T)convert(this.zero, paramClass);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Wrapper forPrimitiveType(Class<?> paramClass)
/*     */   {
/* 239 */     Wrapper localWrapper = findPrimitiveType(paramClass);
/* 240 */     if (localWrapper != null) return localWrapper;
/* 241 */     if (paramClass.isPrimitive())
/* 242 */       throw new InternalError();
/* 243 */     throw newIllegalArgumentException("not primitive: " + paramClass);
/*     */   }
/*     */   
/*     */   static Wrapper findPrimitiveType(Class<?> paramClass) {
/* 247 */     Wrapper localWrapper = FROM_PRIM[hashPrim(paramClass)];
/* 248 */     if ((localWrapper != null) && (localWrapper.primitiveType == paramClass)) {
/* 249 */       return localWrapper;
/*     */     }
/* 251 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Wrapper forWrapperType(Class<?> paramClass)
/*     */   {
/* 261 */     Wrapper localWrapper1 = findWrapperType(paramClass);
/* 262 */     if (localWrapper1 != null) return localWrapper1;
/* 263 */     for (Wrapper localWrapper2 : values())
/* 264 */       if (localWrapper2.wrapperType == paramClass)
/* 265 */         throw new InternalError();
/* 266 */     throw newIllegalArgumentException("not wrapper: " + paramClass);
/*     */   }
/*     */   
/*     */   static Wrapper findWrapperType(Class<?> paramClass) {
/* 270 */     Wrapper localWrapper = FROM_WRAP[hashWrap(paramClass)];
/* 271 */     if ((localWrapper != null) && (localWrapper.wrapperType == paramClass)) {
/* 272 */       return localWrapper;
/*     */     }
/* 274 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Wrapper forBasicType(char paramChar)
/*     */   {
/* 282 */     Wrapper localWrapper1 = FROM_CHAR[hashChar(paramChar)];
/* 283 */     if ((localWrapper1 != null) && (localWrapper1.basicTypeChar == paramChar)) {
/* 284 */       return localWrapper1;
/*     */     }
/* 286 */     for (Wrapper localWrapper2 : values())
/* 287 */       if (localWrapper1.basicTypeChar == paramChar)
/* 288 */         throw new InternalError();
/* 289 */     throw newIllegalArgumentException("not basic type char: " + paramChar);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static Wrapper forBasicType(Class<?> paramClass)
/*     */   {
/* 296 */     if (paramClass.isPrimitive())
/* 297 */       return forPrimitiveType(paramClass);
/* 298 */     return OBJECT;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static int hashPrim(Class<?> paramClass)
/*     */   {
/* 311 */     String str = paramClass.getName();
/* 312 */     if (str.length() < 3) return 0;
/* 313 */     return (str.charAt(0) + str.charAt(2)) % 16;
/*     */   }
/*     */   
/* 316 */   private static int hashWrap(Class<?> paramClass) { String str = paramClass.getName();
/* 317 */     assert (10 == "java.lang.".length());
/* 318 */     if (str.length() < 13) return 0;
/* 319 */     return ('\003' * str.charAt(11) + str.charAt(12)) % 16;
/*     */   }
/*     */   
/* 322 */   private static int hashChar(char paramChar) { return (paramChar + (paramChar >> '\001')) % 16;
/*     */   }
/*     */   
/*     */   static
/*     */   {
/*  30 */     BOOLEAN = new Wrapper("BOOLEAN", 0, Boolean.class, Boolean.TYPE, 'Z', Boolean.valueOf(false), new boolean[0], Format.unsigned(1));
/*     */     
/*  32 */     BYTE = new Wrapper("BYTE", 1, Byte.class, Byte.TYPE, 'B', Byte.valueOf((byte)0), new byte[0], Format.signed(8));
/*  33 */     SHORT = new Wrapper("SHORT", 2, Short.class, Short.TYPE, 'S', Short.valueOf((short)0), new short[0], Format.signed(16));
/*  34 */     CHAR = new Wrapper("CHAR", 3, Character.class, Character.TYPE, 'C', Character.valueOf('\000'), new char[0], Format.unsigned(16));
/*  35 */     INT = new Wrapper("INT", 4, Integer.class, Integer.TYPE, 'I', Integer.valueOf(0), new int[0], Format.signed(32));
/*  36 */     LONG = new Wrapper("LONG", 5, Long.class, Long.TYPE, 'J', Long.valueOf(0L), new long[0], Format.signed(64));
/*  37 */     FLOAT = new Wrapper("FLOAT", 6, Float.class, Float.TYPE, 'F', Float.valueOf(0.0F), new float[0], Format.floating(32));
/*  38 */     DOUBLE = new Wrapper("DOUBLE", 7, Double.class, Double.TYPE, 'D', Double.valueOf(0.0D), new double[0], Format.floating(64));
/*  39 */     OBJECT = new Wrapper("OBJECT", 8, Object.class, Object.class, 'L', null, new Object[0], Format.other(1));
/*     */     
/*  41 */     VOID = new Wrapper("VOID", 9, Void.class, Void.TYPE, 'V', null, null, Format.other(0));$VALUES = new Wrapper[] { BOOLEAN, BYTE, SHORT, CHAR, INT, LONG, FLOAT, DOUBLE, OBJECT, VOID };
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 164 */     assert (checkConvertibleFrom());
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 307 */     FROM_PRIM = new Wrapper[16];
/* 308 */     FROM_WRAP = new Wrapper[16];
/* 309 */     FROM_CHAR = new Wrapper[16];
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 325 */     for (Wrapper localWrapper : values()) {
/* 326 */       int k = hashPrim(localWrapper.primitiveType);
/* 327 */       int m = hashWrap(localWrapper.wrapperType);
/* 328 */       int n = hashChar(localWrapper.basicTypeChar);
/* 329 */       assert (FROM_PRIM[k] == null);
/* 330 */       assert (FROM_WRAP[m] == null);
/* 331 */       assert (FROM_CHAR[n] == null);
/* 332 */       FROM_PRIM[k] = localWrapper;
/* 333 */       FROM_WRAP[m] = localWrapper;
/* 334 */       FROM_CHAR[n] = localWrapper;
/*     */     }
/*     */   }
/*     */   
/*     */   public Class<?> primitiveType()
/*     */   {
/* 340 */     return this.primitiveType;
/*     */   }
/*     */   
/* 343 */   public Class<?> wrapperType() { return this.wrapperType; }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public <T> Class<T> wrapperType(Class<T> paramClass)
/*     */   {
/* 353 */     if (paramClass == this.wrapperType)
/* 354 */       return paramClass;
/* 355 */     if ((paramClass == this.primitiveType) || (this.wrapperType == Object.class) || 
/*     */     
/* 357 */       (paramClass.isInterface())) {
/* 358 */       return forceType(this.wrapperType, paramClass);
/*     */     }
/* 360 */     throw newClassCastException(paramClass, this.primitiveType);
/*     */   }
/*     */   
/*     */   private static ClassCastException newClassCastException(Class<?> paramClass1, Class<?> paramClass2) {
/* 364 */     return new ClassCastException(paramClass1 + " is not compatible with " + paramClass2);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static <T> Class<T> asWrapperType(Class<T> paramClass)
/*     */   {
/* 371 */     if (paramClass.isPrimitive()) {
/* 372 */       return forPrimitiveType(paramClass).wrapperType(paramClass);
/*     */     }
/* 374 */     return paramClass;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static <T> Class<T> asPrimitiveType(Class<T> paramClass)
/*     */   {
/* 381 */     Wrapper localWrapper = findWrapperType(paramClass);
/* 382 */     if (localWrapper != null) {
/* 383 */       return forceType(localWrapper.primitiveType(), paramClass);
/*     */     }
/* 385 */     return paramClass;
/*     */   }
/*     */   
/*     */   public static boolean isWrapperType(Class<?> paramClass)
/*     */   {
/* 390 */     return findWrapperType(paramClass) != null;
/*     */   }
/*     */   
/*     */   public static boolean isPrimitiveType(Class<?> paramClass)
/*     */   {
/* 395 */     return paramClass.isPrimitive();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static char basicTypeChar(Class<?> paramClass)
/*     */   {
/* 402 */     if (!paramClass.isPrimitive()) {
/* 403 */       return 'L';
/*     */     }
/* 405 */     return forPrimitiveType(paramClass).basicTypeChar();
/*     */   }
/*     */   
/*     */ 
/*     */   public char basicTypeChar()
/*     */   {
/* 411 */     return this.basicTypeChar;
/*     */   }
/*     */   
/*     */   public String wrapperSimpleName() {
/* 415 */     return this.wrapperSimpleName;
/*     */   }
/*     */   
/*     */   public String primitiveSimpleName() {
/* 419 */     return this.primitiveSimpleName;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public <T> T cast(Object paramObject, Class<T> paramClass)
/*     */   {
/* 439 */     return (T)convert(paramObject, paramClass, true);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public <T> T convert(Object paramObject, Class<T> paramClass)
/*     */   {
/* 448 */     return (T)convert(paramObject, paramClass, false);
/*     */   }
/*     */   
/*     */   private <T> T convert(Object paramObject, Class<T> paramClass, boolean paramBoolean) {
/* 452 */     if (this == OBJECT)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/* 457 */       assert (!paramClass.isPrimitive());
/* 458 */       if (!paramClass.isInterface()) {
/* 459 */         paramClass.cast(paramObject);
/*     */       }
/* 461 */       localObject1 = paramObject;
/* 462 */       return (T)localObject1;
/*     */     }
/* 464 */     Object localObject1 = wrapperType(paramClass);
/* 465 */     if (((Class)localObject1).isInstance(paramObject)) {
/* 466 */       return (T)((Class)localObject1).cast(paramObject);
/*     */     }
/* 468 */     if (!paramBoolean) {
/* 469 */       localObject2 = paramObject.getClass();
/* 470 */       Wrapper localWrapper = findWrapperType((Class)localObject2);
/* 471 */       if ((localWrapper == null) || (!isConvertibleFrom(localWrapper))) {
/* 472 */         throw newClassCastException((Class)localObject1, (Class)localObject2);
/*     */       }
/* 474 */     } else if (paramObject == null)
/*     */     {
/* 476 */       localObject2 = this.zero;
/* 477 */       return (T)localObject2;
/*     */     }
/*     */     
/* 480 */     Object localObject2 = wrap(paramObject);
/* 481 */     if (!$assertionsDisabled) if ((localObject2 == null ? Void.class : localObject2.getClass()) != localObject1) throw new AssertionError();
/* 482 */     return (T)localObject2;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static <T> Class<T> forceType(Class<?> paramClass, Class<T> paramClass1)
/*     */   {
/* 494 */     int i = (paramClass == paramClass1) || ((paramClass.isPrimitive()) && (forPrimitiveType(paramClass) == findWrapperType(paramClass1))) || ((paramClass1.isPrimitive()) && (forPrimitiveType(paramClass1) == findWrapperType(paramClass))) || ((paramClass == Object.class) && (!paramClass1.isPrimitive())) ? 1 : 0;
/* 495 */     if (i == 0)
/* 496 */       System.out.println(paramClass + " <= " + paramClass1);
/* 497 */     assert ((paramClass == paramClass1) || 
/* 498 */       ((paramClass.isPrimitive()) && (forPrimitiveType(paramClass) == findWrapperType(paramClass1))) || 
/* 499 */       ((paramClass1.isPrimitive()) && (forPrimitiveType(paramClass1) == findWrapperType(paramClass))) || ((paramClass == Object.class) && 
/* 500 */       (!paramClass1.isPrimitive())));
/*     */     
/* 502 */     Class<?> localClass = paramClass;
/* 503 */     return localClass;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Object wrap(Object paramObject)
/*     */   {
/* 516 */     switch (this.basicTypeChar) {
/* 517 */     case 'L':  return paramObject;
/* 518 */     case 'V':  return null;
/*     */     }
/* 520 */     Number localNumber = numberValue(paramObject);
/* 521 */     switch (this.basicTypeChar) {
/* 522 */     case 'I':  return Integer.valueOf(localNumber.intValue());
/* 523 */     case 'J':  return Long.valueOf(localNumber.longValue());
/* 524 */     case 'F':  return Float.valueOf(localNumber.floatValue());
/* 525 */     case 'D':  return Double.valueOf(localNumber.doubleValue());
/* 526 */     case 'S':  return Short.valueOf((short)localNumber.intValue());
/* 527 */     case 'B':  return Byte.valueOf((byte)localNumber.intValue());
/* 528 */     case 'C':  return Character.valueOf((char)localNumber.intValue());
/* 529 */     case 'Z':  return Boolean.valueOf(boolValue(localNumber.byteValue()));
/*     */     }
/* 531 */     throw new InternalError("bad wrapper");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Object wrap(int paramInt)
/*     */   {
/* 541 */     if (this.basicTypeChar == 'L') return Integer.valueOf(paramInt);
/* 542 */     switch (this.basicTypeChar) {
/* 543 */     case 'L':  throw newIllegalArgumentException("cannot wrap to object type");
/* 544 */     case 'V':  return null;
/* 545 */     case 'I':  return Integer.valueOf(paramInt);
/* 546 */     case 'J':  return Long.valueOf(paramInt);
/* 547 */     case 'F':  return Float.valueOf(paramInt);
/* 548 */     case 'D':  return Double.valueOf(paramInt);
/* 549 */     case 'S':  return Short.valueOf((short)paramInt);
/* 550 */     case 'B':  return Byte.valueOf((byte)paramInt);
/* 551 */     case 'C':  return Character.valueOf((char)paramInt);
/* 552 */     case 'Z':  return Boolean.valueOf(boolValue((byte)paramInt));
/*     */     }
/* 554 */     throw new InternalError("bad wrapper");
/*     */   }
/*     */   
/*     */   private static Number numberValue(Object paramObject) {
/* 558 */     if ((paramObject instanceof Number)) return (Number)paramObject;
/* 559 */     if ((paramObject instanceof Character)) return Integer.valueOf(((Character)paramObject).charValue());
/* 560 */     if ((paramObject instanceof Boolean)) { return Integer.valueOf(((Boolean)paramObject).booleanValue() ? 1 : 0);
/*     */     }
/* 562 */     return (Number)paramObject;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static boolean boolValue(byte paramByte)
/*     */   {
/* 569 */     paramByte = (byte)(paramByte & 0x1);
/* 570 */     return paramByte != 0;
/*     */   }
/*     */   
/*     */   private static RuntimeException newIllegalArgumentException(String paramString, Object paramObject) {
/* 574 */     return newIllegalArgumentException(paramString + paramObject);
/*     */   }
/*     */   
/* 577 */   private static RuntimeException newIllegalArgumentException(String paramString) { return new IllegalArgumentException(paramString); }
/*     */   
/*     */ 
/*     */   public Object makeArray(int paramInt)
/*     */   {
/* 582 */     return Array.newInstance(this.primitiveType, paramInt);
/*     */   }
/*     */   
/* 585 */   public Class<?> arrayType() { return this.emptyArray.getClass(); }
/*     */   
/*     */   public void copyArrayUnboxing(Object[] paramArrayOfObject, int paramInt1, Object paramObject, int paramInt2, int paramInt3) {
/* 588 */     if (paramObject.getClass() != arrayType())
/* 589 */       arrayType().cast(paramObject);
/* 590 */     for (int i = 0; i < paramInt3; i++) {
/* 591 */       Object localObject = paramArrayOfObject[(i + paramInt1)];
/* 592 */       localObject = convert(localObject, this.primitiveType);
/* 593 */       Array.set(paramObject, i + paramInt2, localObject);
/*     */     }
/*     */   }
/*     */   
/* 597 */   public void copyArrayBoxing(Object paramObject, int paramInt1, Object[] paramArrayOfObject, int paramInt2, int paramInt3) { if (paramObject.getClass() != arrayType())
/* 598 */       arrayType().cast(paramObject);
/* 599 */     for (int i = 0; i < paramInt3; i++) {
/* 600 */       Object localObject = Array.get(paramObject, i + paramInt1);
/*     */       
/* 602 */       assert (localObject.getClass() == this.wrapperType);
/* 603 */       paramArrayOfObject[(i + paramInt2)] = localObject;
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\invoke\util\Wrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */