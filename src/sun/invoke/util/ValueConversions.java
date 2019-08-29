/*     */ package sun.invoke.util;
/*     */ 
/*     */ import java.lang.invoke.MethodHandle;
/*     */ import java.lang.invoke.MethodHandles;
/*     */ import java.lang.invoke.MethodHandles.Lookup;
/*     */ import java.lang.invoke.MethodType;
/*     */ import java.util.EnumMap;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ValueConversions
/*     */ {
/*     */   private static final Class<?> THIS_CLASS;
/*     */   private static final Lookup IMPL_LOOKUP;
/*     */   private static final WrapperCache[] UNBOX_CONVERSIONS;
/*     */   private static final Integer ZERO_INT;
/*     */   private static final Integer ONE_INT;
/*     */   private static final WrapperCache[] BOX_CONVERSIONS;
/*     */   private static final WrapperCache[] CONSTANT_FUNCTIONS;
/*     */   private static final MethodHandle CAST_REFERENCE;
/*     */   private static final MethodHandle IGNORE;
/*     */   private static final MethodHandle EMPTY;
/*     */   
/*     */   private static class WrapperCache
/*     */   {
/*  43 */     private final EnumMap<Wrapper, MethodHandle> map = new EnumMap(Wrapper.class);
/*     */     
/*     */     public MethodHandle get(Wrapper paramWrapper) {
/*  46 */       return (MethodHandle)this.map.get(paramWrapper);
/*     */     }
/*     */     
/*     */     public synchronized MethodHandle put(Wrapper paramWrapper, MethodHandle paramMethodHandle) {
/*  50 */       MethodHandle localMethodHandle = (MethodHandle)this.map.putIfAbsent(paramWrapper, paramMethodHandle);
/*  51 */       if (localMethodHandle != null) return localMethodHandle;
/*  52 */       return paramMethodHandle;
/*     */     }
/*     */   }
/*     */   
/*     */   private static WrapperCache[] newWrapperCaches(int paramInt) {
/*  57 */     WrapperCache[] arrayOfWrapperCache = new WrapperCache[paramInt];
/*  58 */     for (int i = 0; i < paramInt; i++)
/*  59 */       arrayOfWrapperCache[i] = new WrapperCache(null);
/*  60 */     return arrayOfWrapperCache;
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
/*  71 */   static int unboxInteger(Integer paramInteger) { return paramInteger.intValue(); }
/*     */   
/*     */   static int unboxInteger(Object paramObject, boolean paramBoolean) {
/*  74 */     if ((paramObject instanceof Integer))
/*  75 */       return ((Integer)paramObject).intValue();
/*  76 */     return primitiveConversion(Wrapper.INT, paramObject, paramBoolean).intValue();
/*     */   }
/*     */   
/*     */ 
/*  80 */   static byte unboxByte(Byte paramByte) { return paramByte.byteValue(); }
/*     */   
/*     */   static byte unboxByte(Object paramObject, boolean paramBoolean) {
/*  83 */     if ((paramObject instanceof Byte))
/*  84 */       return ((Byte)paramObject).byteValue();
/*  85 */     return primitiveConversion(Wrapper.BYTE, paramObject, paramBoolean).byteValue();
/*     */   }
/*     */   
/*     */ 
/*  89 */   static short unboxShort(Short paramShort) { return paramShort.shortValue(); }
/*     */   
/*     */   static short unboxShort(Object paramObject, boolean paramBoolean) {
/*  92 */     if ((paramObject instanceof Short))
/*  93 */       return ((Short)paramObject).shortValue();
/*  94 */     return primitiveConversion(Wrapper.SHORT, paramObject, paramBoolean).shortValue();
/*     */   }
/*     */   
/*     */ 
/*  98 */   static boolean unboxBoolean(Boolean paramBoolean) { return paramBoolean.booleanValue(); }
/*     */   
/*     */   static boolean unboxBoolean(Object paramObject, boolean paramBoolean) {
/* 101 */     if ((paramObject instanceof Boolean))
/* 102 */       return ((Boolean)paramObject).booleanValue();
/* 103 */     return (primitiveConversion(Wrapper.BOOLEAN, paramObject, paramBoolean).intValue() & 0x1) != 0;
/*     */   }
/*     */   
/*     */ 
/* 107 */   static char unboxCharacter(Character paramCharacter) { return paramCharacter.charValue(); }
/*     */   
/*     */   static char unboxCharacter(Object paramObject, boolean paramBoolean) {
/* 110 */     if ((paramObject instanceof Character))
/* 111 */       return ((Character)paramObject).charValue();
/* 112 */     return (char)primitiveConversion(Wrapper.CHAR, paramObject, paramBoolean).intValue();
/*     */   }
/*     */   
/*     */ 
/* 116 */   static long unboxLong(Long paramLong) { return paramLong.longValue(); }
/*     */   
/*     */   static long unboxLong(Object paramObject, boolean paramBoolean) {
/* 119 */     if ((paramObject instanceof Long))
/* 120 */       return ((Long)paramObject).longValue();
/* 121 */     return primitiveConversion(Wrapper.LONG, paramObject, paramBoolean).longValue();
/*     */   }
/*     */   
/*     */ 
/* 125 */   static float unboxFloat(Float paramFloat) { return paramFloat.floatValue(); }
/*     */   
/*     */   static float unboxFloat(Object paramObject, boolean paramBoolean) {
/* 128 */     if ((paramObject instanceof Float))
/* 129 */       return ((Float)paramObject).floatValue();
/* 130 */     return primitiveConversion(Wrapper.FLOAT, paramObject, paramBoolean).floatValue();
/*     */   }
/*     */   
/*     */ 
/* 134 */   static double unboxDouble(Double paramDouble) { return paramDouble.doubleValue(); }
/*     */   
/*     */   static double unboxDouble(Object paramObject, boolean paramBoolean) {
/* 137 */     if ((paramObject instanceof Double))
/* 138 */       return ((Double)paramObject).doubleValue();
/* 139 */     return primitiveConversion(Wrapper.DOUBLE, paramObject, paramBoolean).doubleValue();
/*     */   }
/*     */   
/*     */   private static MethodType unboxType(Wrapper paramWrapper, int paramInt) {
/* 143 */     if (paramInt == 0)
/* 144 */       return MethodType.methodType(paramWrapper.primitiveType(), paramWrapper.wrapperType());
/* 145 */     return MethodType.methodType(paramWrapper.primitiveType(), Object.class, new Class[] { Boolean.TYPE });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static MethodHandle unbox(Wrapper paramWrapper, int paramInt)
/*     */   {
/* 155 */     WrapperCache localWrapperCache = UNBOX_CONVERSIONS[paramInt];
/* 156 */     MethodHandle localMethodHandle = localWrapperCache.get(paramWrapper);
/* 157 */     if (localMethodHandle != null) {
/* 158 */       return localMethodHandle;
/*     */     }
/*     */     
/* 161 */     switch (paramWrapper) {
/*     */     case OBJECT: 
/*     */     case VOID: 
/* 164 */       throw new IllegalArgumentException("unbox " + paramWrapper);
/*     */     }
/*     */     
/* 167 */     String str = "unbox" + paramWrapper.wrapperSimpleName();
/* 168 */     MethodType localMethodType = unboxType(paramWrapper, paramInt);
/*     */     try {
/* 170 */       localMethodHandle = IMPL_LOOKUP.findStatic(THIS_CLASS, str, localMethodType);
/*     */     } catch (ReflectiveOperationException localReflectiveOperationException) {
/* 172 */       localMethodHandle = null;
/*     */     }
/* 174 */     if (localMethodHandle != null) {
/* 175 */       if (paramInt > 0) {
/* 176 */         boolean bool = paramInt != 2;
/* 177 */         localMethodHandle = MethodHandles.insertArguments(localMethodHandle, 1, new Object[] { Boolean.valueOf(bool) });
/*     */       }
/* 179 */       if (paramInt == 1) {
/* 180 */         localMethodHandle = localMethodHandle.asType(unboxType(paramWrapper, 0));
/*     */       }
/* 182 */       return localWrapperCache.put(paramWrapper, localMethodHandle);
/*     */     }
/* 184 */     throw new IllegalArgumentException("cannot find unbox adapter for " + paramWrapper + (paramInt == 3 ? " (cast)" : paramInt <= 1 ? " (exact)" : ""));
/*     */   }
/*     */   
/*     */ 
/*     */   public static MethodHandle unboxExact(Wrapper paramWrapper)
/*     */   {
/* 190 */     return unbox(paramWrapper, 0);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static MethodHandle unboxExact(Wrapper paramWrapper, boolean paramBoolean)
/*     */   {
/* 198 */     return unbox(paramWrapper, paramBoolean ? 0 : 1);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static MethodHandle unboxWiden(Wrapper paramWrapper)
/*     */   {
/* 207 */     return unbox(paramWrapper, 2);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static MethodHandle unboxCast(Wrapper paramWrapper)
/*     */   {
/* 215 */     return unbox(paramWrapper, 3);
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
/*     */   public static Number primitiveConversion(Wrapper paramWrapper, Object paramObject, boolean paramBoolean)
/*     */   {
/* 230 */     if (paramObject == null) {
/* 231 */       if (!paramBoolean) return null;
/* 232 */       return ZERO_INT; }
/*     */     Object localObject;
/* 234 */     if ((paramObject instanceof Number)) {
/* 235 */       localObject = (Number)paramObject;
/* 236 */     } else if ((paramObject instanceof Boolean)) {
/* 237 */       localObject = ((Boolean)paramObject).booleanValue() ? ONE_INT : ZERO_INT;
/* 238 */     } else if ((paramObject instanceof Character)) {
/* 239 */       localObject = Integer.valueOf(((Character)paramObject).charValue());
/*     */     }
/*     */     else {
/* 242 */       localObject = (Number)paramObject;
/*     */     }
/* 244 */     Wrapper localWrapper = Wrapper.findWrapperType(paramObject.getClass());
/* 245 */     if ((localWrapper == null) || ((!paramBoolean) && (!paramWrapper.isConvertibleFrom(localWrapper))))
/*     */     {
/* 247 */       return (Number)paramWrapper.wrapperType().cast(paramObject); }
/* 248 */     return (Number)localObject;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static int widenSubword(Object paramObject)
/*     */   {
/* 257 */     if ((paramObject instanceof Integer))
/* 258 */       return ((Integer)paramObject).intValue();
/* 259 */     if ((paramObject instanceof Boolean))
/* 260 */       return fromBoolean(((Boolean)paramObject).booleanValue());
/* 261 */     if ((paramObject instanceof Character))
/* 262 */       return ((Character)paramObject).charValue();
/* 263 */     if ((paramObject instanceof Short))
/* 264 */       return ((Short)paramObject).shortValue();
/* 265 */     if ((paramObject instanceof Byte)) {
/* 266 */       return ((Byte)paramObject).byteValue();
/*     */     }
/*     */     
/* 269 */     return ((Integer)paramObject).intValue();
/*     */   }
/*     */   
/*     */ 
/*     */   static Integer boxInteger(int paramInt)
/*     */   {
/* 275 */     return Integer.valueOf(paramInt);
/*     */   }
/*     */   
/*     */   static Byte boxByte(byte paramByte) {
/* 279 */     return Byte.valueOf(paramByte);
/*     */   }
/*     */   
/*     */   static Short boxShort(short paramShort) {
/* 283 */     return Short.valueOf(paramShort);
/*     */   }
/*     */   
/*     */   static Boolean boxBoolean(boolean paramBoolean) {
/* 287 */     return Boolean.valueOf(paramBoolean);
/*     */   }
/*     */   
/*     */   static Character boxCharacter(char paramChar) {
/* 291 */     return Character.valueOf(paramChar);
/*     */   }
/*     */   
/*     */   static Long boxLong(long paramLong) {
/* 295 */     return Long.valueOf(paramLong);
/*     */   }
/*     */   
/*     */   static Float boxFloat(float paramFloat) {
/* 299 */     return Float.valueOf(paramFloat);
/*     */   }
/*     */   
/*     */   static Double boxDouble(double paramDouble) {
/* 303 */     return Double.valueOf(paramDouble);
/*     */   }
/*     */   
/*     */   private static MethodType boxType(Wrapper paramWrapper)
/*     */   {
/* 308 */     Class localClass = paramWrapper.wrapperType();
/* 309 */     return MethodType.methodType(localClass, paramWrapper.primitiveType());
/*     */   }
/*     */   
/*     */ 
/*     */   public static MethodHandle boxExact(Wrapper paramWrapper)
/*     */   {
/* 315 */     WrapperCache localWrapperCache = BOX_CONVERSIONS[0];
/* 316 */     MethodHandle localMethodHandle = localWrapperCache.get(paramWrapper);
/* 317 */     if (localMethodHandle != null) {
/* 318 */       return localMethodHandle;
/*     */     }
/*     */     
/* 321 */     String str = "box" + paramWrapper.wrapperSimpleName();
/* 322 */     MethodType localMethodType = boxType(paramWrapper);
/*     */     try {
/* 324 */       localMethodHandle = IMPL_LOOKUP.findStatic(THIS_CLASS, str, localMethodType);
/*     */     } catch (ReflectiveOperationException localReflectiveOperationException) {
/* 326 */       localMethodHandle = null;
/*     */     }
/* 328 */     if (localMethodHandle != null) {
/* 329 */       return localWrapperCache.put(paramWrapper, localMethodHandle);
/*     */     }
/* 331 */     throw new IllegalArgumentException("cannot find box adapter for " + paramWrapper);
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
/*     */   static Object zeroObject()
/*     */   {
/* 344 */     return null;
/*     */   }
/*     */   
/*     */   static int zeroInteger() {
/* 348 */     return 0;
/*     */   }
/*     */   
/*     */   static long zeroLong() {
/* 352 */     return 0L;
/*     */   }
/*     */   
/*     */   static float zeroFloat() {
/* 356 */     return 0.0F;
/*     */   }
/*     */   
/*     */   static double zeroDouble() {
/* 360 */     return 0.0D;
/*     */   }
/*     */   
/*     */ 
/*     */   public static MethodHandle zeroConstantFunction(Wrapper paramWrapper)
/*     */   {
/* 366 */     WrapperCache localWrapperCache = CONSTANT_FUNCTIONS[0];
/* 367 */     MethodHandle localMethodHandle = localWrapperCache.get(paramWrapper);
/* 368 */     if (localMethodHandle != null) {
/* 369 */       return localMethodHandle;
/*     */     }
/*     */     
/* 372 */     MethodType localMethodType = MethodType.methodType(paramWrapper.primitiveType());
/* 373 */     switch (paramWrapper) {
/*     */     case VOID: 
/* 375 */       localMethodHandle = EMPTY;
/* 376 */       break;
/*     */     case OBJECT: case INT: case LONG: 
/*     */     case FLOAT: case DOUBLE: 
/*     */       try {
/* 380 */         localMethodHandle = IMPL_LOOKUP.findStatic(THIS_CLASS, "zero" + paramWrapper.wrapperSimpleName(), localMethodType);
/*     */       } catch (ReflectiveOperationException localReflectiveOperationException) {
/* 382 */         localMethodHandle = null;
/*     */       }
/*     */     }
/*     */     
/* 386 */     if (localMethodHandle != null) {
/* 387 */       return localWrapperCache.put(paramWrapper, localMethodHandle);
/*     */     }
/*     */     
/*     */ 
/* 391 */     if ((paramWrapper.isSubwordOrInt()) && (paramWrapper != Wrapper.INT)) {
/* 392 */       localMethodHandle = MethodHandles.explicitCastArguments(zeroConstantFunction(Wrapper.INT), localMethodType);
/* 393 */       return localWrapperCache.put(paramWrapper, localMethodHandle);
/*     */     }
/* 395 */     throw new IllegalArgumentException("cannot find zero constant for " + paramWrapper);
/*     */   }
/*     */   
/*     */   static
/*     */   {
/*  35 */     THIS_CLASS = ValueConversions.class;
/*  36 */     IMPL_LOOKUP = MethodHandles.lookup();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 148 */     UNBOX_CONVERSIONS = newWrapperCaches(4);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 218 */     ZERO_INT = Integer.valueOf(0);ONE_INT = Integer.valueOf(1);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 312 */     BOX_CONVERSIONS = newWrapperCaches(1);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 363 */     CONSTANT_FUNCTIONS = newWrapperCaches(2);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     try
/*     */     {
/* 401 */       MethodType localMethodType1 = MethodType.genericMethodType(1);
/* 402 */       MethodType localMethodType2 = localMethodType1.changeReturnType(Void.TYPE);
/* 403 */       CAST_REFERENCE = IMPL_LOOKUP.findVirtual(Class.class, "cast", localMethodType1);
/* 404 */       IGNORE = IMPL_LOOKUP.findStatic(THIS_CLASS, "ignore", localMethodType2);
/* 405 */       EMPTY = IMPL_LOOKUP.findStatic(THIS_CLASS, "empty", localMethodType2.dropParameterTypes(0, 1));
/*     */     } catch (NoSuchMethodException|IllegalAccessException localNoSuchMethodException) {
/* 407 */       throw newInternalError("uncaught exception", localNoSuchMethodException);
/*     */     }
/*     */   }
/*     */   
/*     */   public static MethodHandle ignore() {
/* 412 */     return IGNORE;
/*     */   }
/*     */   
/*     */   public static MethodHandle cast()
/*     */   {
/* 417 */     return CAST_REFERENCE;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static float doubleToFloat(double paramDouble)
/*     */   {
/* 428 */     return (float)paramDouble;
/*     */   }
/*     */   
/* 431 */   static long doubleToLong(double paramDouble) { return paramDouble; }
/*     */   
/*     */   static int doubleToInt(double paramDouble) {
/* 434 */     return (int)paramDouble;
/*     */   }
/*     */   
/* 437 */   static short doubleToShort(double paramDouble) { return (short)(int)paramDouble; }
/*     */   
/*     */   static char doubleToChar(double paramDouble) {
/* 440 */     return (char)(int)paramDouble;
/*     */   }
/*     */   
/* 443 */   static byte doubleToByte(double paramDouble) { return (byte)(int)paramDouble; }
/*     */   
/*     */   static boolean doubleToBoolean(double paramDouble) {
/* 446 */     return toBoolean((byte)(int)paramDouble);
/*     */   }
/*     */   
/*     */   static double floatToDouble(float paramFloat)
/*     */   {
/* 451 */     return paramFloat;
/*     */   }
/*     */   
/*     */   static long floatToLong(float paramFloat) {
/* 455 */     return paramFloat;
/*     */   }
/*     */   
/* 458 */   static int floatToInt(float paramFloat) { return (int)paramFloat; }
/*     */   
/*     */   static short floatToShort(float paramFloat) {
/* 461 */     return (short)(int)paramFloat;
/*     */   }
/*     */   
/* 464 */   static char floatToChar(float paramFloat) { return (char)(int)paramFloat; }
/*     */   
/*     */   static byte floatToByte(float paramFloat) {
/* 467 */     return (byte)(int)paramFloat;
/*     */   }
/*     */   
/* 470 */   static boolean floatToBoolean(float paramFloat) { return toBoolean((byte)(int)paramFloat); }
/*     */   
/*     */ 
/*     */   static double longToDouble(long paramLong)
/*     */   {
/* 475 */     return paramLong;
/*     */   }
/*     */   
/* 478 */   static float longToFloat(long paramLong) { return (float)paramLong; }
/*     */   
/*     */   static int longToInt(long paramLong)
/*     */   {
/* 482 */     return (int)paramLong;
/*     */   }
/*     */   
/* 485 */   static short longToShort(long paramLong) { return (short)(int)paramLong; }
/*     */   
/*     */   static char longToChar(long paramLong) {
/* 488 */     return (char)(int)paramLong;
/*     */   }
/*     */   
/* 491 */   static byte longToByte(long paramLong) { return (byte)(int)paramLong; }
/*     */   
/*     */   static boolean longToBoolean(long paramLong) {
/* 494 */     return toBoolean((byte)(int)paramLong);
/*     */   }
/*     */   
/*     */   static double intToDouble(int paramInt)
/*     */   {
/* 499 */     return paramInt;
/*     */   }
/*     */   
/* 502 */   static float intToFloat(int paramInt) { return paramInt; }
/*     */   
/*     */   static long intToLong(int paramInt) {
/* 505 */     return paramInt;
/*     */   }
/*     */   
/*     */   static short intToShort(int paramInt) {
/* 509 */     return (short)paramInt;
/*     */   }
/*     */   
/* 512 */   static char intToChar(int paramInt) { return (char)paramInt; }
/*     */   
/*     */   static byte intToByte(int paramInt) {
/* 515 */     return (byte)paramInt;
/*     */   }
/*     */   
/* 518 */   static boolean intToBoolean(int paramInt) { return toBoolean((byte)paramInt); }
/*     */   
/*     */ 
/*     */   static double shortToDouble(short paramShort)
/*     */   {
/* 523 */     return paramShort;
/*     */   }
/*     */   
/* 526 */   static float shortToFloat(short paramShort) { return paramShort; }
/*     */   
/*     */   static long shortToLong(short paramShort) {
/* 529 */     return paramShort;
/*     */   }
/*     */   
/* 532 */   static int shortToInt(short paramShort) { return paramShort; }
/*     */   
/*     */   static char shortToChar(short paramShort)
/*     */   {
/* 536 */     return (char)paramShort;
/*     */   }
/*     */   
/* 539 */   static byte shortToByte(short paramShort) { return (byte)paramShort; }
/*     */   
/*     */   static boolean shortToBoolean(short paramShort) {
/* 542 */     return toBoolean((byte)paramShort);
/*     */   }
/*     */   
/*     */   static double charToDouble(char paramChar)
/*     */   {
/* 547 */     return paramChar;
/*     */   }
/*     */   
/* 550 */   static float charToFloat(char paramChar) { return paramChar; }
/*     */   
/*     */   static long charToLong(char paramChar) {
/* 553 */     return paramChar;
/*     */   }
/*     */   
/* 556 */   static int charToInt(char paramChar) { return paramChar; }
/*     */   
/*     */   static short charToShort(char paramChar)
/*     */   {
/* 560 */     return (short)paramChar;
/*     */   }
/*     */   
/* 563 */   static byte charToByte(char paramChar) { return (byte)paramChar; }
/*     */   
/*     */   static boolean charToBoolean(char paramChar) {
/* 566 */     return toBoolean((byte)paramChar);
/*     */   }
/*     */   
/*     */   static double byteToDouble(byte paramByte)
/*     */   {
/* 571 */     return paramByte;
/*     */   }
/*     */   
/* 574 */   static float byteToFloat(byte paramByte) { return paramByte; }
/*     */   
/*     */   static long byteToLong(byte paramByte) {
/* 577 */     return paramByte;
/*     */   }
/*     */   
/* 580 */   static int byteToInt(byte paramByte) { return paramByte; }
/*     */   
/*     */   static short byteToShort(byte paramByte) {
/* 583 */     return (short)paramByte;
/*     */   }
/*     */   
/* 586 */   static char byteToChar(byte paramByte) { return (char)paramByte; }
/*     */   
/*     */   static boolean byteToBoolean(byte paramByte)
/*     */   {
/* 590 */     return toBoolean(paramByte);
/*     */   }
/*     */   
/*     */   static double booleanToDouble(boolean paramBoolean)
/*     */   {
/* 595 */     return fromBoolean(paramBoolean);
/*     */   }
/*     */   
/* 598 */   static float booleanToFloat(boolean paramBoolean) { return fromBoolean(paramBoolean); }
/*     */   
/*     */   static long booleanToLong(boolean paramBoolean) {
/* 601 */     return fromBoolean(paramBoolean);
/*     */   }
/*     */   
/* 604 */   static int booleanToInt(boolean paramBoolean) { return fromBoolean(paramBoolean); }
/*     */   
/*     */   static short booleanToShort(boolean paramBoolean) {
/* 607 */     return (short)fromBoolean(paramBoolean);
/*     */   }
/*     */   
/* 610 */   static char booleanToChar(boolean paramBoolean) { return (char)fromBoolean(paramBoolean); }
/*     */   
/*     */   static byte booleanToByte(boolean paramBoolean) {
/* 613 */     return fromBoolean(paramBoolean);
/*     */   }
/*     */   
/*     */ 
/*     */   static boolean toBoolean(byte paramByte)
/*     */   {
/* 619 */     return (paramByte & 0x1) != 0;
/*     */   }
/*     */   
/*     */   static byte fromBoolean(boolean paramBoolean) {
/* 623 */     return paramBoolean ? 1 : 0;
/*     */   }
/*     */   
/* 626 */   private static final WrapperCache[] CONVERT_PRIMITIVE_FUNCTIONS = newWrapperCaches(Wrapper.values().length);
/*     */   
/*     */   public static MethodHandle convertPrimitive(Wrapper paramWrapper1, Wrapper paramWrapper2) {
/* 629 */     WrapperCache localWrapperCache = CONVERT_PRIMITIVE_FUNCTIONS[paramWrapper1.ordinal()];
/* 630 */     MethodHandle localMethodHandle = localWrapperCache.get(paramWrapper2);
/* 631 */     if (localMethodHandle != null) {
/* 632 */       return localMethodHandle;
/*     */     }
/*     */     
/* 635 */     Class localClass1 = paramWrapper1.primitiveType();
/* 636 */     Class localClass2 = paramWrapper2.primitiveType();
/* 637 */     MethodType localMethodType = MethodType.methodType(localClass2, localClass1);
/* 638 */     if (paramWrapper1 == paramWrapper2) {
/* 639 */       localMethodHandle = MethodHandles.identity(localClass1);
/*     */     } else {
/* 641 */       assert ((localClass1.isPrimitive()) && (localClass2.isPrimitive()));
/*     */       try {
/* 643 */         localMethodHandle = IMPL_LOOKUP.findStatic(THIS_CLASS, localClass1.getSimpleName() + "To" + capitalize(localClass2.getSimpleName()), localMethodType);
/*     */       } catch (ReflectiveOperationException localReflectiveOperationException) {
/* 645 */         localMethodHandle = null;
/*     */       }
/*     */     }
/* 648 */     if (localMethodHandle != null) {
/* 649 */       assert (localMethodHandle.type() == localMethodType) : localMethodHandle;
/* 650 */       return localWrapperCache.put(paramWrapper2, localMethodHandle);
/*     */     }
/*     */     
/*     */ 
/* 654 */     throw new IllegalArgumentException("cannot find primitive conversion function for " + localClass1.getSimpleName() + " -> " + localClass2.getSimpleName());
/*     */   }
/*     */   
/*     */   public static MethodHandle convertPrimitive(Class<?> paramClass1, Class<?> paramClass2) {
/* 658 */     return convertPrimitive(Wrapper.forPrimitiveType(paramClass1), Wrapper.forPrimitiveType(paramClass2));
/*     */   }
/*     */   
/*     */   private static String capitalize(String paramString) {
/* 662 */     return Character.toUpperCase(paramString.charAt(0)) + paramString.substring(1);
/*     */   }
/*     */   
/*     */   private static InternalError newInternalError(String paramString, Throwable paramThrowable)
/*     */   {
/* 667 */     return new InternalError(paramString, paramThrowable);
/*     */   }
/*     */   
/* 670 */   private static InternalError newInternalError(Throwable paramThrowable) { return new InternalError(paramThrowable); }
/*     */   
/*     */   static void ignore(Object paramObject) {}
/*     */   
/*     */   static void empty() {}
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\invoke\util\ValueConversions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */